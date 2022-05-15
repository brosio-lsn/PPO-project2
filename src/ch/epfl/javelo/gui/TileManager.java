package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.util.Pair;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Manages the tiles of the JaVelo map.
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise Aigueperse (341890)
 */

public final class TileManager {

    public record TileId(int zoomLevel, int xIndex, int yIndex) {
        private static final int TILES_AT_ZOOM_0 = 4;

        /**
         * constructor of the id of a tile
         *
         * @param zoomLevel zoom level on the map - scaling factor of the coordinates of the tile
         * @param xIndex    x coordinates of the tile.
         * @param yIndex    y coordinate of the tile.
         */
        public TileId {
            Preconditions.checkArgument(isValid(zoomLevel, xIndex, yIndex));
        }

        public long hashCodeBis() {
            return (long) (zoomLevel * 1e10 + xIndex * 1e5 + yIndex);
        }

        /**
         * determines whether a tile is valid.
         *
         * @param zoomLevel zoom level on the map - scaling factor of the coordinates of the tile
         * @param xIndex    x coordinate of the tile
         * @param yIndex    y coordinate of the tile
         * @return true if the xIndex and the yIndex are within the bounds delimited by the zoom level and
         * the number of tiles
         */
        public static boolean isValid(int zoomLevel, int xIndex, int yIndex) {
            double nbOfTiles = Math.pow(TILES_AT_ZOOM_0, zoomLevel);
            return (xIndex >= 0
                    && yIndex >= 0
                    && xIndex < Math.sqrt(nbOfTiles)
                    && yIndex < Math.sqrt(nbOfTiles));
        }
    }

    LinkedHashMap<Long, Image> cache;
    private final Path pathToRepertory;
    private String serverName;
    private String apiKey;
    private int serverId;
    private final List<String> servers;
    private int currentServer;
    private final static int CAPACITY_OF_CACHE = 100;
    private final static float LOAD_FACTOR = 0.75f;
    private final static boolean ELDEST_ACCES = true;
    private final static String HTTPS = "https://";

    /**
     * Constructor of the TileManager
     *
     * @param pathToRepertory path in which the program will write and read images.
     * @param serverName      server to get the tiles and the corresponding images from.
     */
    public TileManager(Path pathToRepertory, String serverName, String apiKey) {
        this.pathToRepertory = pathToRepertory;
        this.serverName = serverName;
        this.apiKey = apiKey;
        currentServer = 0;
        servers = new ArrayList<>(List.of(serverName));
        cache = new LinkedHashMap<>(CAPACITY_OF_CACHE, LOAD_FACTOR, ELDEST_ACCES);
    }

    /**
     * returns the corresponding image of a tile of a given id.
     *
     * @param id id of the tile to get the image from
     * @return the corresponding image of a tle of a given id.
     * @throws IOException if the paths leading to the system files are invalid.
     */
    public Image imageForTileAt(TileId id) throws IOException {
        long hash =  id.hashCodeBis() + serverName.hashCode();
        StringBuilder url = new StringBuilder();
        String fileOfTile = url.append(serverName)
                .append("/")
                .append(id.zoomLevel)
                .append("/")
                .append(id.xIndex)
                .toString();
        String imagePath = url
                .append("/")
                .append(id.yIndex)
                .append(".png")
                .toString();
        Path pathToFiles = Path.of(fileOfTile);
        if (cache.containsKey(hash)) {
            return cache.get(hash);
        } else if (Files.exists(Path.of(imagePath), LinkOption.NOFOLLOW_LINKS)) {
            return imageFromDisk(id, imagePath, hash);
        } else {
            return imageFromServer(id, pathToFiles, imagePath, hash);
        }
    }

    /**
     * Loads an image, which represents a Tile, from hard drive memory, then puts
     * it into the cache.
     *
     * @param id        id of the tile to load.
     * @param imagePath local path to such an image.
     * @return the image representing the tile of given id from the local hard drive.
     */
    private Image imageFromDisk(TileId id, String imagePath, long hashCode) throws IOException {
        Image fileImage;
        try (FileInputStream i = new FileInputStream(imagePath)) {
            fileImage = new Image(i);
            cache.put(hashCode, fileImage);
        }
        return fileImage;
    }

    /**
     * creates the directory to store an image which represents a Tile, downloads it
     * from a given server address and returns it.
     *
     * @param id          id of the tile to download
     * @param pathToFiles local path to create the directory in.
     * @param pathToImage path to get the image from in the server
     * @return the image downloaded from the server
     * @throws IOException if there is an error in any of the paths used.
     */
    private Image imageFromServer(TileId id, Path pathToFiles, String pathToImage, long hashCode) throws IOException {
        Files.createDirectories(pathToFiles);
        URL u = new URL(HTTPS + pathToImage + apiKey);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try (InputStream i = c.getInputStream();
             OutputStream writer = new FileOutputStream(pathToImage)) {
            i.transferTo(writer);
        }
        Iterator<Long> ite = cache.keySet().iterator();
        if (ite.hasNext() && cache.size() == CAPACITY_OF_CACHE) cache.remove(ite.next());
        return imageFromDisk(id, pathToImage, hashCode);
    }

    public void setNewServer(String serverName, String apiKey) {
        this.serverName = serverName;
        this.apiKey = apiKey;
        Platform.requestNextPulse();
    }
}
