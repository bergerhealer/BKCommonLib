package com.bergerkiller.bukkit.common.map.archive;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Keeps a locally downloaded archive synchronized by checking the resource pack SHA1 against a hash.
 * In general, makes sure an archive is downloaded and available.
 * When the hash string is kept empty, it is only re-downloaded when the path/url is changed.
 */
public class MapResourcePackDownloadedArchive implements MapResourcePackArchive {
    private final URL resourcePackURL;
    private final String resourcePackHash;
    private MapResourcePackArchive archive = null;

    public MapResourcePackDownloadedArchive(URL resourcePackURL, String resourcePackHash) {
        this.resourcePackURL = resourcePackURL;
        this.resourcePackHash = resourcePackHash;
    }

    @Override
    public void load(boolean lazy) {
        // TODO Auto-generated method stub
    }

    @Override
    public InputStream openFileStream(String path) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

}
