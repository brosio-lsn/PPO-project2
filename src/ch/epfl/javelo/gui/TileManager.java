package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
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
        public TileId{
            Preconditions.checkArgument(isValid(zoomLevel, xIndex, yIndex));
        }
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
    private final static int CAPACITY_OF_CACHE = 100;
    private final static float LOAD_FACTOR = 0.75f;

    public TileManager(Path pathToRepertory, String serverName) {
        this.pathToRepertory = pathToRepertory;
        this.serverName = serverName;
        url = new StringBuilder();
        cache = new LinkedHashMap<>(CAPACITY_OF_CACHE, LOAD_FACTOR, true);
    }

    public Image imageForTileAt(TileId id) throws IOException {
        String fileOfTile = url
                .append("/")
                .append(id.zoomLevel)
                .append("/")
                .append(id.xIndex)
                .toString();
        String imagePath = pathToRepertory + url
                .append("/")
                .append(id.yIndex)
                .append(".png")
                .toString();
        String pathOfTileFile = pathToRepertory.toString() + fileOfTile;
        Path pathOfTile = Path.of(pathOfTileFile);

        if (cache.containsKey(id)) {
            return cache.get(id);
        } else if (Files.exists(Path.of(imagePath), LinkOption.NOFOLLOW_LINKS)) {
            Image fileImage = new Image(imagePath);
            cache.put(id, fileImage);
            return fileImage;
        } else {

            Files.createDirectories(pathOfTile);
            URL u = new URL("https://" + serverName + imagePath);
            URLConnection c = u.openConnection();
            c.setRequestProperty("User-Agent", "JaVelo");
            System.out.println(pathToRepertory + imagePath);
            try (InputStream i = c.getInputStream();
                 OutputStream writer = new FileOutputStream(pathToRepertory + imagePath)) {
                i.transferTo(writer);
            }
            Image fileImage = new Image(pathToRepertory.resolve("%d".formatted(id.zoomLevel)
                    ).resolve("%d".formatted(id.xIndex))
                    .resolve("%d.png".formatted(id.yIndex))
                    .toString());
            cache.put(id, fileImage);
            Iterator<TileId> ite = cache.keySet().iterator();
            if (ite.hasNext()) cache.remove(ite.next());
            return fileImage;
        }
    }
}
