package ch.epfl.javelo.gui;

import ch.epfl.javelo.Preconditions;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.Image;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages the tiles of the JaVelo map.
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise Aigueperse (341890)
 */

public final class TileManager {

    private final Map<Long, Image> cache;
    private final Path pathToRepertory;
    private final ObjectProperty<String> serverName;
    private final static int CAPACITY_OF_CACHE = 100;
    private final static float LOAD_FACTOR = 0.75f;
    private final static boolean ELDEST_ACCES = true;
    private final static String HTTPS = "https://";
    private String apiKey;

    /**
     * Constructor of the TileManager
     *
     * @param pathToRepertory path in which the program will write and read images.
     * @param serverName      server to get the tiles and the corresponding images from.
     *                        this constructor is to be used when one does not need
     *                        an APIKey to download the tiles from a certain server.
     */
    public TileManager(Path pathToRepertory, String serverName) {
        this(pathToRepertory, serverName, "");
    }

    /**
     * Constructor of the TileManager with the API Key.
     *
     * @param pathToRepertory path in which the program will write and read images.
     * @param serverName      server to get the tiles and the corresponding images from.
     * @param apiKey          apiKey which enables the program to download the tiles from the given server.
     */
    public TileManager(Path pathToRepertory, String serverName, String apiKey) {
        this.pathToRepertory = pathToRepertory;
        this.serverName = new SimpleObjectProperty<>(serverName);
        this.apiKey = apiKey;
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

        Long tileAndServer = id.tileIdIndex()*100+ serverName.get().hashCode()%100;
        System.out.println(tileAndServer);
        String fileOfTile = pathToRepertory + String.format("/%s/%d/%d",serverName.get() , id.zoomLevel, id.xIndex);
        String imagePath = String.format("/%d/%d/%d.png", id.zoomLevel, id.xIndex, id.yIndex);
        Path pathToFiles = Path.of(fileOfTile);
        if (cache.containsKey(tileAndServer)) {
            return cache.get(tileAndServer);
        } else if (Files.exists(Path.of(pathToRepertory + "/" + serverName.get() + imagePath))) {
            return imageFromDisk(tileAndServer, imagePath);
        } else {
            return imageFromServer(tileAndServer, pathToFiles, imagePath);
        }
    }

    /**
     * Loads an image, which represents a Tile, from hard drive memory, then puts
     * it into the cache.
     *
     * @param id        id of the tile to load.
     * @param imagePath local path to such an image.
     * @return the image representing the tile of given id from the local hard drive.
     * @throws IOException if the path in the fileInputStream is incorrect.
     */

    private Image imageFromDisk(Long id, String imagePath) throws IOException {
        Image fileImage;
        try (FileInputStream inputStream = new FileInputStream(pathToRepertory + "/" + serverName.get() + imagePath)) {
            fileImage = new Image(inputStream);
            cache.put(id, fileImage);
            Iterator<Long> ite = cache.keySet().iterator();
            if (ite.hasNext() && cache.size() == CAPACITY_OF_CACHE) {
                cache.remove(ite.next());
            }
        }
        return fileImage;
    }

    /**
     * creates the directory to store an image which represents a Tile, downloads it
     * from a given server address and returns it.
     *
     * @param id          id of the tile to download
     * @param pathToFiles local path to create the directory in.
     * @param imagePath   URL to get the image from in the server
     * @return the image downloaded from the server
     * @throws IOException if there is an error in any of the paths used.
     */
    private Image imageFromServer(Long id, Path pathToFiles, String imagePath) throws IOException {
        Files.createDirectories(pathToFiles);
        URL u = new URL(HTTPS + serverName.get() + imagePath + apiKey);
        URLConnection c = u.openConnection();
        c.setRequestProperty("User-Agent", "JaVelo");
        try (InputStream i = c.getInputStream();
             OutputStream writer = new FileOutputStream(pathToRepertory + "/" + serverName.get() + "/" + imagePath)) {
            i.transferTo(writer);
        }
        return imageFromDisk(id, imagePath);
    }

    /**
     * sets the new server to download the tiles from
     *
     * @param serverName name of the server to download the tiles from.
     * @param apiKey     Key which enables the program to download the tiles from the server
     */
    public void setNewServer(String serverName, String apiKey) {
        this.serverName.set(serverName);
        this.apiKey = apiKey;
    }

    public ReadOnlyProperty<String> serverProperty() {
        return serverName;
    }


    public record TileId(int zoomLevel, int xIndex, int yIndex) {
        private static final int TILES_ON_SIDE_ZOOM_0 = 2;
        private static final double SEPARATING_FACTOR_XY = 1e6;
        private static final double SEPARATING_FACTOR_ZX = 1e12;

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

        public long tileIdIndex() {
            return (long) (zoomLevel * SEPARATING_FACTOR_ZX + xIndex * SEPARATING_FACTOR_XY + yIndex);
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
            double sideOfSquare = Math.pow(TILES_ON_SIDE_ZOOM_0, zoomLevel);
            return (xIndex >= 0
                    && yIndex >= 0
                    && xIndex < sideOfSquare
                    && yIndex < sideOfSquare);
        }
    }
}
