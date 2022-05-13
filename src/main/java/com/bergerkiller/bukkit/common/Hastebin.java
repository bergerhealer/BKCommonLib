package com.bergerkiller.bukkit.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import com.bergerkiller.bukkit.common.config.BasicConfiguration;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.io.ByteArrayIOStream;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

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
    private Session _uploadSession;

    /**
     * Creates a new Hastebin instance with no server configured.
     * Please call {@link #setServer(String)} before using it to upload.
     * Downloading from (other) servers can be done without setting a server.
     * 
     * @param plugin
     */
    public Hastebin(JavaPlugin plugin) {
        this(plugin, null);
    }

    /**
     * Creates a new Hastebin instance
     * 
     * @param plugin that will be responsible for the requests
     * @param serverURL with which is communicated when uploading (for example, https://hastebin.com)
     */
    public Hastebin(JavaPlugin plugin, String serverURL) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin can not be null");
        }
        this._executor = Executors.newFixedThreadPool(2);
        this._plugin = plugin;
        this._uploadSession = new Session(plugin, serverURL);
    }

    /**
     * Sets the URL of the Hastebin server with which is communicated
     * 
     * @param serverURL to set to
     */
    public void setServer(String serverURL) {
        this._uploadSession = new Session(this._plugin, serverURL);
    }

    /**
     * Uploads data to the server and returns a completable future that will be completed
     * when the operation either succeeds or fails. Any registered callbacks will be executed
     * on the main thread, in a task scheduled with the plugin as owner.
     * 
     * @param content to upload
     * @return completable future completed when the upload succeeds or fails
     */
    public CompletableFuture<UploadResult> upload(String content) {
        final CompletableFuture<UploadResult> result = new CompletableFuture<UploadResult>();
        final Session session = this._uploadSession;
        this._executor.execute(() -> {
            try {
                byte[] contents_bytes = null;
                ByteArrayOutputStream contents_bytes_compressed = null;
                while (true) {
                    HttpURLConnection con = session.createRequest("POST", "/documents");
                    con.setDoOutput(true);
                    con.setRequestProperty("Accept", "application/json, */*; q=0.01");

                    // Upload the data
                    if (session.getCapabilities().requestContentEncoding) {
                        // Compress with gzip and store into contents_bytes_compressed
                        // We do this so we can compute the content length of the compressed data
                        if (contents_bytes_compressed == null) {
                            contents_bytes_compressed = new ByteArrayOutputStream();
                            try (Writer writer = new OutputStreamWriter(new GZIPOutputStream(contents_bytes_compressed), "UTF-8")) {
                                writer.write(content);
                            }
                        }

                        // Upload data compressed
                        con.setRequestProperty("Content-Encoding", "gzip");
                        con.setFixedLengthStreamingMode(contents_bytes_compressed.size());
                        try (OutputStream output1 = con.getOutputStream()) {
                            contents_bytes_compressed.writeTo(output1);
                        }
                    } else {
                        // Upload data uncompressed
                        if (contents_bytes == null) {
                            contents_bytes = content.getBytes(Charset.forName("UTF-8"));
                        }
                        con.setFixedLengthStreamingMode(contents_bytes.length);
                        try (OutputStream output2 = con.getOutputStream()) {
                            output2.write(contents_bytes);
                        }
                    }

                    // Check redirects
                    if (session.handleRedirect(con)) {
                        continue;
                    }

                    // Read response
                    HastebinUploadResponse response = decodeGSON(con, HastebinUploadResponse.class);
                    if (response.key == null) {
                        throw new InvalidServerResponseException("No key");
                    }
                    complete(result, new UploadResult(true, session.createURL("/" + response.key).toString(), null));
                    break;
                }
            } catch (IOException ex1) {
                complete(result, new UploadResult(false, null, "I/O Exception occurred: " + ex1.getMessage()));
            } catch (InvalidServerURLException ex2) {
                complete(result, new UploadResult(false, null, "Invalid Server URL: " + session.getServer()));
            } catch (InvalidServerResponseException ex3) {
                complete(result, new UploadResult(false, null, ex3.getMessage()));
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.WARNING, "Unhandled error uploading to hastebin", t);
                complete(result, new UploadResult(false, null, "Error occurred: " + t.getMessage()));
            }
        });
        return result;
    }

    /**
     * Downloads data from the server and returns a completable future that will be completed
     * when the operation either succeeds or fails. Any registered callbacks will be executed
     * on the main thread, in a task scheduled with the plugin as owner.
     * No server has to be configured for this method to work.<br>
     * <br>
     * Instead of the full url, it is also acceptable to omit the http(s):// portion.
     * 
     * @param url to download
     * @return completable future completed when the download succeeds or fails
     */
    public CompletableFuture<DownloadResult> download(String url) {
        final CompletableFuture<DownloadResult> result = new CompletableFuture<DownloadResult>();
        final Session session = new Session(this._plugin, url);
        this._executor.execute(() -> {
            try {
                // Find the /raw/somekey URL
                final String raw_path = session.findRawPath();
                while (true) {
                    HttpURLConnection con = session.createRequest("GET", raw_path);
                    con.setRequestProperty("Accept-Encoding", "gzip");
                    if (session.handleRedirect(con)) {
                        continue;
                    }

                    // Is allowed to be more or less than the actual content
                    // This initializes the initial capacity of the receive buffer
                    int expectedContentSize = con.getContentLength();
                    if (expectedContentSize <= 0) {
                        expectedContentSize = 1024;
                    }

                    // Read from InputStream and write to a ByteArrayIOStream buffer
                    ByteArrayIOStream contentBuffer = new ByteArrayIOStream(expectedContentSize);
                    if ("gzip".equals(con.getContentEncoding())) {
                        // Download compressed content and decode
                        try (GZIPInputStream input1 = new GZIPInputStream(con.getInputStream())) {
                            contentBuffer.readFrom(input1);
                        }
                    } else {
                        // Download uncompressed content
                        try (InputStream input2 = con.getInputStream()) {
                            contentBuffer.readFrom(input2);
                        }
                    }

                    // Return the results
                    complete(result, DownloadResult.content(url, contentBuffer));
                    break;
                }
            } catch (IOException ex1) {
                complete(result, DownloadResult.error(url, "I/O Exception occurred: " + ex1.getMessage()));
            } catch (InvalidServerURLException ex2) {
                complete(result, DownloadResult.error(url, "Invalid URL: " + session.getServer()));
            } catch (InvalidServerResponseException ex3) {
                complete(result, DownloadResult.error(url, ex3.getMessage()));
            } catch (Throwable t) {
                Logging.LOGGER_NETWORK.log(Level.WARNING, "Unhandled error downloading from hastebin", t);
                complete(result, DownloadResult.error(url, "Error occurred: " + t.getMessage()));
            }
        });
        return result;
    }

    private final <T> void complete(CompletableFuture<T> future, T result) {
        if (!this._plugin.isEnabled()) {
            Logging.LOGGER_NETWORK.warning("Hastebin operation for " + this._plugin.getName() + " completed after plugin was disabled");
            return;
        }
        int id = Bukkit.getScheduler().scheduleSyncDelayedTask(this._plugin, () -> future.complete(result));
        if (id == -1) {
            Logging.LOGGER_NETWORK.warning("Hastebin operation for " + this._plugin.getName() + " completed but running the callbacks failed");
        }
    }

    /**
     * The result of a download operation. If the download operation failed,
     * {@link #success()} returns false, with an error reason available
     * at {@link #error()}.
     */
    public static class DownloadResult {
        private final boolean _success;
        private final String _url;
        private final ByteArrayIOStream _content;
        private final String _error;

        private DownloadResult(boolean success, String url, ByteArrayIOStream content, String error) {
            this._success = success;
            this._url = url;
            this._content = content;
            this._error = error;
        }

        /**
         * Creates a download result for when the content could not be downloaded
         * 
         * @param url The url downloaded
         * @param error The error that occurred
         * @return DownloadResult
         */
        public static DownloadResult error(String url, String error) {
            return new DownloadResult(false, url, null, error);
        }

        /**
         * Creates a download result for when the content was downloaded and a content
         * stream is available
         * 
         * @param url The url downloaded
         * @param content Stream of content data
         * @return DownloadResult
         */
        public static DownloadResult content(String url, ByteArrayIOStream content) {
            return new DownloadResult(true, url, content, null);
        }

        /**
         * Whether the download was successful
         * 
         * @return True if successful
         */
        public boolean success() {
            return this._success;
        }

        /**
         * The url that this download result is for
         * 
         * @return url
         */
        public String url() {
            return this._url;
        }

        /**
         * If {@link #success()} returns true, the content returned by the hastebin servers
         * 
         * @return content
         */
        public String content() {
            try {
                return this._content.toString("UTF-8");
            } catch (UnsupportedEncodingException e) {
                return "";
            }
        }

        /**
         * If {@link #success()} returns true, the content returned by the hastebin servers
         * as an input stream of raw bytes. No String decoding is performed.
         * 
         * @return content input stream
         */
        public InputStream contentInputStream() {
            return this._content.toInputStream();
        }

        /**
         * If {@link #success()} returns true, the content returned by the hastebin servers
         * as a YAML Basic Configuration.
         * 
         * @return Basic Configuration
         * @throws IOException Thrown if decoding the downloaded data fails
         */
        public BasicConfiguration contentYAML() throws IOException {
            BasicConfiguration config = new BasicConfiguration();
            config.loadFromStream(this.contentInputStream());
            return config;
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

    /**
     * GSON Structure for the response of hastebin's /documents call (upload)
     */
    private static class HastebinUploadResponse {
        public String key;
    }

    /**
     * GSON structure for the response of hastebin's /capabilities call.
     * This is a special call added to identify additional capabilities.
     * If the call is not supported, default capabilities are used instead.
     */
    private static class HastebinCapabilitiesResponse {
        @SerializedName("request-content-encoding")
        public boolean requestContentEncoding = false;
    }

    private static class Session {
        private final JavaPlugin _plugin;
        private final String _server;
        private URL _serverURL;
        private String _userAgentString;
        private HastebinCapabilitiesResponse _capabilities;
        private int _numRedirects;

        public Session(JavaPlugin plugin, String serverURL) {
            this._plugin = plugin;
            this._server = serverURL;
            this._serverURL = null;
            this._userAgentString = null;
            this._capabilities = null;

            // Parse the root URL, attempt prefixing with http:// if required
            if (this._server != null) {
                try {
                    this._serverURL = new URL(this._server);
                } catch (MalformedURLException e1) {
                    try {
                        this._serverURL = new URL("http://" + _server);
                    } catch (MalformedURLException e2) {
                    }
                }
            }
        }

        // Gets the server configuration that initialized this session
        public String getServer() {
            return this._server;
        }

        // Retrieves the server's capabilities
        public HastebinCapabilitiesResponse getCapabilities() throws InvalidServerURLException, InvalidServerResponseException, IOException {
            synchronized (this) {
                if (this._capabilities != null) {
                    return this._capabilities;
                }
            }
            while (true) {
                HttpURLConnection connection = this.createRequest("GET", "/capabilities");
                connection.setRequestProperty("Accept", "application/json, */*; q=0.01");
                if (this.handleRedirect(connection)) {
                    continue;
                }

                // Vanilla hastebin servers do not support the /capabilities call
                // They return text/html content instead of text/javascript
                HastebinCapabilitiesResponse response;
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK ||
                    !("application/json".equals(connection.getContentType())))
                {
                    response = new HastebinCapabilitiesResponse();
                }
                else
                {
                    response = decodeGSON(connection, HastebinCapabilitiesResponse.class);
                }

                synchronized (this) {
                    if (this._capabilities == null) {
                        this._capabilities = response;
                    }
                    return this._capabilities;
                }
            }
        }

        // Creates a new HttpURLConnection for a new request
        public HttpURLConnection createRequest(String method, String path) throws InvalidServerURLException, IOException {
            URLConnection url_connection = this.createURL(path).openConnection();
            if (!(url_connection instanceof HttpURLConnection)) {
                throw new InvalidServerURLException();
            }
            HttpURLConnection connection = (HttpURLConnection) url_connection;
            connection.setRequestMethod(method);
            connection.setInstanceFollowRedirects(false);
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(20000);
            connection.setRequestProperty("X-Requested-With", "HttpURLConnection");
            connection.setRequestProperty("Accept-Encoding", "gzip");

            synchronized (this) {
                if (this._userAgentString == null) {
                    this._userAgentString = "BKCommonLib/" + CommonPlugin.getInstance().getVersion() +
                            " " + this._plugin.getName() + "/" + this._plugin.getDescription().getVersion() +
                            " Java/" + System.getProperty("java.version");
                }
                connection.setRequestProperty("User-Agent", this._userAgentString);
            }

            return connection;
        }

        // For switching from http to https or server moving
        public synchronized boolean handleRedirect(HttpURLConnection connection) throws IOException, InvalidServerResponseException {
            int status = connection.getResponseCode();
            if (status != HttpURLConnection.HTTP_MOVED_PERM && status != HttpURLConnection.HTTP_MOVED_TEMP) {
                _numRedirects = 0;
                return false;
            } else if (++_numRedirects > 10) {
                throw new InvalidServerResponseException("Maximum number of HTTP redirects reached");
            }

            String redirect_url_str = connection.getHeaderField("Location");
            if (redirect_url_str == null) {
                throw new InvalidServerResponseException("HTTP Redirect without Location header");
            }
            try {
                URL url = new URL(redirect_url_str);
                this._serverURL = new URL(url.getProtocol(), url.getHost(), url.getPort(), "");
                return true;
            } catch (MalformedURLException e) {
                throw new InvalidServerResponseException("HTTP Redirect Location is malformed (" + redirect_url_str + ")");
            }
        }

        // Attempts to decode the server URL and turn it into the raw URL path where contents can be found
        // If the URL does not look like a hastebin url, an InvalidServerURLException is thrown
        // Accepted URLs (with any .extensions):
        // - /sawezoqeco
        // - /documents/sawezoqeco
        // - /raw/sawezoqeco
        // - /download/sawezoqeco
        public synchronized String findRawPath() throws InvalidServerURLException {
            if (this._serverURL == null) {
                throw new InvalidServerURLException();
            }

            // Get path, remove any file extensions at the end of it
            String path = this._serverURL.getPath();
            if (path == null || path.isEmpty() || path.charAt(0) != '/') {
                throw new InvalidServerURLException();
            }
            int last_slash_idx = path.lastIndexOf('/');
            if (last_slash_idx == -1) {
                throw new InvalidServerURLException();
            }
            int ext_idx;
            while ((ext_idx = path.lastIndexOf('.')) != -1 && ext_idx > last_slash_idx) {
                path = path.substring(0, ext_idx);
            }

            // If it starts with /documents/ or /raw/, trim
            if (path.startsWith("/documents/")) {
                path = path.substring(10);
            } else if (path.startsWith("/download/")) {
                path = path.substring(9);
            } else if (path.startsWith("/raw/")) {
                path = path.substring(4);
            }

            // At this point it should be just /key, so only one slash
            if (path.indexOf('/', 1) != -1) {
                throw new InvalidServerURLException();
            }

            // Create the /raw/ url
            return "/raw" + path;
        }

        // Creates a URL for the currently configured server
        public synchronized URL createURL(String path) throws InvalidServerURLException {
            if (this._serverURL == null) {
                throw new InvalidServerURLException();
            }
            try {
                return new URL(this._serverURL.getProtocol(), this._serverURL.getHost(), this._serverURL.getPort(), path);
            } catch (MalformedURLException e) {
                throw new InvalidServerURLException();
            }
        }
    }

    private static <T> T decodeGSON(HttpURLConnection connection, Class<T> type) throws InvalidServerResponseException, IOException {
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new InvalidServerResponseException("Non-OK Status Code " + connection.getResponseCode());
        }

        // Read response. If compressed using gzip, decompress it.
        try {
            T decoded;
            if ("gzip".equals(connection.getHeaderField("Content-Encoding"))) {
                try (Reader reader = new InputStreamReader(new GZIPInputStream(connection.getInputStream()))) {
                    decoded = new Gson().fromJson(reader, type);
                }
            } else {
                try (Reader reader = new InputStreamReader(connection.getInputStream())) {
                    decoded = new Gson().fromJson(reader, type);
                }
            }
            if (decoded == null) {
                throw new InvalidServerResponseException("Empty response received (EOF)");
            }
            return decoded;
        } catch (com.google.gson.JsonIOException ex) {
            throw new IOException("Failed to read JSON: " + ex.getMessage());
        } catch (com.google.gson.JsonSyntaxException ex) {
            throw new InvalidServerResponseException("Response is not valid JSON");
        }
    }

    private static class InvalidServerURLException extends Exception {
        private static final long serialVersionUID = -3102529517837518121L;
    }

    private static class InvalidServerResponseException extends Exception {
        private static final long serialVersionUID = 1542358791115174559L;

        public InvalidServerResponseException(String what) {
            super("Invalid response received from server: " + what);
        }
    }
}
