package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import javafx.scene.shape.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Iterator;

/**
 * represents a route generator in GPX format
 *
 * @author Louis ROCHE (345620)
 * @author Ambroise AIGUEPERSE (341890)
 */

public final class GpxGenerator {

    private static final int INITIAL_POSITION_ON_ROUTE = 0;

    /**
     * private constructor
     */
    private GpxGenerator() {
    }

    /**
     * returns the GPX document corresponding to the given Route and its elevationProfile
     *
     * @param route   the given Route
     * @param profile the profile of the Route
     * @return the GPX document corresponding to the given Route and its elevationProfile
     */
    public static Document createGpx(Route route, ElevationProfile profile) {
        //given part
        Document doc = newDocument();

        Element root = doc
                .createElementNS("http://www.topografix.com/GPX/1/1",
                        "gpx");
        doc.appendChild(root);

        root.setAttributeNS(
                "http://www.w3.org/2001/XMLSchema-instance",
                "xsi:schemaLocation",
                "http://www.topografix.com/GPX/1/1 "
                        + "http://www.topografix.com/GPX/1/1/gpx.xsd");
        root.setAttribute("version", "1.1");
        root.setAttribute("creator", "JaVelo");

        Element metadata = doc.createElement("metadata");
        root.appendChild(metadata);

        Element name = doc.createElement("name");
        metadata.appendChild(name);
        name.setTextContent("Route JaVelo");

        //personnal implementations part

        //create rte element (represents the route)
        Element rte = doc.createElement("rte");
        root.appendChild(rte);

        //initiate different objects for the following while loop
        Iterator<PointCh> pointChIterator = route.points().iterator();
        Iterator<Edge> edgeIterator = route.edges().iterator();
        double positionOnRtoue = INITIAL_POSITION_ON_ROUTE;
        double altitude;
        Element rtept;

        //append each time rtept Element (represents a point on the route)
        while (pointChIterator.hasNext()) {
            rtept = doc.createElement("rtept");
            rte.appendChild(rtept);

            //latitude and longitude attributes
            PointCh point = pointChIterator.next();
            double lat = Math.toDegrees(point.lat());
            double lon = Math.toDegrees(point.lon());
            //todo demander si nommer ca en constantes
            rtept.setAttribute("lat", String.valueOf(lat));
            rtept.setAttribute("lon", String.valueOf(lon));

            //altitude of rtept element
            altitude = profile.elevationAt(positionOnRtoue);
            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.valueOf(altitude));

            //update position on route
            if (edgeIterator.hasNext()) positionOnRtoue += edgeIterator.next().length();
        }

        return doc;
    }

    /**
     * writes the GPX document corresponding to the given Route and its profile, in the given file
     *
     * @param fileName name of the file in which the GPX document will be written
     * @param route    the given Route
     * @param profile  the elevation profile of the given Route
     * @throws IOException
     */
    public static void writeGpx(String fileName, Route route, ElevationProfile profile) throws IOException {
        Document doc = createGpx(route, profile);
        try (Writer writer = Files.newBufferedWriter(java.nio.file.Path.of(fileName))) {
            try {
                Transformer transformer = TransformerFactory
                        .newDefaultInstance()
                        .newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource(doc),
                        new StreamResult(writer));
            } catch (TransformerException e) {
                throw new Error(e); // Should never happen
            }
        }
    }

    /**
     * creates a new, very simple, GPX Document
     *
     * @return a new basic GPX Document
     */
    private static Document newDocument() {
        try {
            return DocumentBuilderFactory
                    .newDefaultInstance()
                    .newDocumentBuilder()
                    .newDocument();
        } catch (ParserConfigurationException e) {
            throw new Error(e); // Should never happen
        }
    }

}
