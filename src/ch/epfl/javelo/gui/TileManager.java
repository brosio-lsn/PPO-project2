package ch.epfl.javelo.gui;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;

public final class TileManager {

    public final record TileId(int zoomLevel, int xIndex, int yIndex) {
        public boolean isValid(int zoomLevel, int xIndex, int yIndex) {
            double nbOfTiles = Math.scalb(4, zoomLevel);
            return (xIndex +1 <= Math.sqrt(nbOfTiles) && yIndex +1 <= Math.sqrt(nbOfTiles));
        }
    }
    LinkedHashMap<Image, TileId> cache;
    private final Path pathToRepertory;
    private final String serverName;
    private final StringBuilder url;

    public TileManager(Path pathToRepertory, String serverName) {
        this.pathToRepertory = pathToRepertory;
        this.serverName = serverName;
        url = new StringBuilder();
        cache = new LinkedHashMap<>(100, 0.75f, false);
    }
    public Image imageForTileAt(TileId id) throws IOException {

        url.append("https://tile.openstreetmap.org/")
                .append(id.zoomLevel)
                .append("/")
                .append(id.xIndex)
                .append("/")
                .append(id.yIndex)
                .append(".png");
        URL u = new URL(url.toString());
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try(InputStream i = c.getInputStream()){

        }
        //TODO ?????????????? ne peut pas utiliser removeEldestEntry ?????
        cache.removeEldestEntry;

    }
}
