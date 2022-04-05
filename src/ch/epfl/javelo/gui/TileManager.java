package ch.epfl.javelo.gui;

import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;

public final class TileManager {

    public final record TileId(int zoomLevel, int xIndex, int yIndex) {
        public boolean isValid(int zoomLevel, int xIndex, int yIndex) {
            double nbOfTiles = Math.scalb(4, zoomLevel);
            return (xIndex >= 0
                    && yIndex >= 0
                    && xIndex + 1 <= Math.sqrt(nbOfTiles)
                    && yIndex + 1 <= Math.sqrt(nbOfTiles));
        }
    }

    LinkedHashMap<TileId, Image> cache;
    private final Path pathToRepertory;
    private final String serverName;
    private final StringBuilder url;

    public TileManager(Path pathToRepertory, String serverName) {
        this.pathToRepertory = pathToRepertory;
        this.serverName = serverName;
        url = new StringBuilder();
        cache = new LinkedHashMap<>(100, 0.75f, true);
    }

    public Image imageForTileAt(TileId id) throws IOException {
        String fileOfTile = url
                .append(id.zoomLevel)
                .append("/")
                .append(id.xIndex)
                .append("/")
                .append(id.yIndex)
                .append(".png").
                toString();
        Path pathOfTile = Path.of(pathToRepertory.toString() + fileOfTile);
        if (cache.containsKey(id)) {
            return cache.get(id);
        } else if (Files.exists(pathOfTile, LinkOption.NOFOLLOW_LINKS)) {
            Image fileImage = new Image(pathOfTile.toString());
            cache.put(id, fileImage);
            return fileImage;
        } else {
            Files.createDirectories(pathOfTile);
            URL u = new URL(serverName + fileOfTile);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            try (InputStream i = c.getInputStream()) {
                OutputStream writer = new FileOutputStream(pathOfTile.toString());
                i.transferTo(writer);
            }
            Image fileImage = new Image(pathOfTile.toString());
            cache.put(id, fileImage);
            Iterator<TileId> ite = cache.keySet().iterator();
            if (ite.hasNext()) cache.remove(ite.next());
            return fileImage;
        }
        }
}
