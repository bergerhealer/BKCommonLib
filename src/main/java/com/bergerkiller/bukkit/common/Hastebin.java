package com.bergerkiller.bukkit.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.google.gson.Gson;

/**
 * Hastebin paste client that connects with hastebin's servers.
 * A free public hastebin server can be found at <a href="https://hastebin.com">https://hastebin.com</a>.
 * For self-hosting the server code can be found on <a href="https://github.com/seejohnrun/haste-server">github</a>.
 * <br><br>
 * To prevent abuse, the JavaPlugin that will be sending the requests must be passed along in the constructor.
 * Its description will be used as part of the user agent string.
 * The callbacks (completable future) will be run in a task scheduled for this plugin.
 */
public class Hastebin {
    private final Executor _executor;
    private final JavaPlugin _plugin;
    private final String _server;
    private final String _userAgentString;

    /**
     * Creates a new Hastebin instance
     * 
     * @param plugin that will be responsible for the requests
     * @param server with which is communicated (for example, https://hastebin.com)
     */
    public Hastebin(JavaPlugin plugin, String server) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin can not be null");
        }
        this._executor = Executors.newFixedThreadPool(2);
        this._plugin = plugin;
        this._server = server;
        this._userAgentString = "BKCommonLib/" + CommonPlugin.getInstance().getVersion() +
                " " + plugin.getName() + "/" + plugin.getDescription().getVersion() +
                " Java/" + System.getProperty("java.version");
    }

    /**
     * Uploads data to the server and returns a completable future that will be completed
     * when the operation either succeeds or fails. Any registered callbacks will be executed
     * on the main thread, in a task scheduled with the plugin as owner.
     * 
     * @param contents to upload
     * @return completable future completed when the upload succeeds or fails
     */
    public CompletableFuture<UploadResult> upload(String contents) {
        final CompletableFuture<UploadResult> result = new CompletableFuture<UploadResult>();
        this._executor.execute(new Runnable() {
            @Override
            public void run() {
                // Parse the root URL from the configuration, attempt prefixing with http:// if required
                URL root_url;
                try {
                    root_url = new URL(_server);
                } catch (MalformedURLException e1) {
                    try {
                        root_url = new URL("http://" +_server);
                    } catch (MalformedURLException e2) {
                        complete(result, new UploadResult(false, null, "The configured server '" + _server + "' has an incorrect syntax"));
                        return;
                    }
                }

                // Use the root URL to generate a new URL with the /documents path
                URL document_url;
                try {
                    document_url = new URL(root_url.getProtocol(), root_url.getHost(), root_url.getPort(), "/documents");
                } catch (MalformedURLException e) {
                    complete(result, new UploadResult(false, null, "Failed to create document url: " + e.getMessage()));
                    return;
                }

                int redirect_limit = 100;
                while (true) {
                    // Make the POST request
                    try {
                        // Prepare the connection
                        HttpURLConnection con = (HttpURLConnection) document_url.openConnection();
                        con.setRequestMethod("POST");
                        con.setRequestProperty("User-Agent", _userAgentString);
                        con.setRequestProperty("X-Requested-With", "HttpURLConnection");
                        con.setRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
                        con.setInstanceFollowRedirects(false);
                        con.setDoInput(true);
                        con.setDoOutput(true);
                        con.setConnectTimeout(10000);
                        con.setReadTimeout(5000);

                        // Write the content data to the connection
                        try (OutputStream output = con.getOutputStream()) {
                            output.write(contents.getBytes(Charset.forName("UTF-8")));
                        }

                        // Check status
                        int status = con.getResponseCode();
                        if (status == HttpURLConnection.HTTP_MOVED_PERM || status == HttpURLConnection.HTTP_MOVED_TEMP) {
                            String redirect_url_str = con.getHeaderField("Location");
                            if (redirect_url_str == null) {
                                throw new RuntimeException("Status " + status + " response has no Location response header");
                            }
                            try {
                                document_url = new URL(redirect_url_str);
                                root_url = new URL(document_url.getProtocol(), document_url.getHost(), document_url.getPort(), "");
                                continue; // retry
                            } catch (MalformedURLException e) {
                                throw new RuntimeException("Status " + status + " response Location header is invalid: " + redirect_url_str);
                            }
                        } else if (status != HttpURLConnection.HTTP_OK) {
                            complete(result, new UploadResult(false, null, "Server returned non-OK status code: " + status));
                            return;
                        }

                        // Read response
                        try (Reader reader = new InputStreamReader(con.getInputStream())) {
                            HastebinUploadResponse response = new Gson().fromJson(reader, HastebinUploadResponse.class);
                            if (response == null || response.key == null) {
                                complete(result, new UploadResult(false, null, "Server did not respond with a key"));
                            } else {
                                URL result_url = new URL(root_url.getProtocol(), root_url.getHost(), root_url.getPort(), "/" + response.key);
                                complete(result, new UploadResult(true, result_url.toString(), null));
                            }
                        }
                    } catch (IOException ex) {
                        complete(result, new UploadResult(false, null, "I/O Exception occurred: " + ex.getMessage()));
                    } catch (Throwable t) {
                        t.printStackTrace();
                        complete(result, new UploadResult(false, null, "Error occurred: " + t.getMessage()));
                    }

                    if (--redirect_limit == 0) {
                        complete(result, new UploadResult(false, null, "Maximum number of redirects reached"));
                        return;
                    }
                }
            }
        });
        return result;
    }

    private final <T> void complete(CompletableFuture<T> future, T result) {
        if (!this._plugin.isEnabled()) {
            Logging.LOGGER_NETWORK.warning("Hastebin operation for " + this._plugin.getName() + " completed after plugin was disabled");
            return;
        }
        int id = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, new Runnable() {
            @Override
            public void run() {
                future.complete(result);
            }
        });
        if (id == -1) {
            Logging.LOGGER_NETWORK.warning("Hastebin operation for " + this._plugin.getName() + " completed but running the callbacks failed");
        }
    }

    /**
     * GSON Structure for the response of hastebin's /documents call (upload)
     */
    private static class HastebinUploadResponse {
        public String key;
    }

    /**
     * The result of an upload operation. If the upload operation failed,
     * {@link #success()} returns false, with an error reason available
     * at {@link #error()}.
     */
    public static class UploadResult {
        private final boolean _success;
        private final String _url;
        private final String _error;

        private UploadResult(boolean success, String url, String error) {
            this._success = success;
            this._url = url;
            this._error = error;
        }

        /**
         * Whether the upload was successful
         * 
         * @return True if successful
         */
        public boolean success() {
            return this._success;
        }

        /**
         * If {@link #success()} returns true, the url generated by the hastebin servers
         * 
         * @return url
         */
        public String url() {
            return this._url;
        }

        /**
         * If {@link #success()} returns false, the error message
         * 
         * @return error message
         */
        public String error() {
            return this._error;
        }
    }
}
