package ch.epfl.javelo.gui;

import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
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
import java.io.FileWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public final class GpxGenerator {
    private GpxGenerator(){}

    public static Document createGpx(Route route, ElevationProfile profile){
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
        //partie perso
        Element rte =doc.createElement("rte");
        root.appendChild(rte);
        Iterator<PointCh> pointChIterator = route.points().iterator();
        Iterator<Edge> edgeIterator = route.edges().iterator();
        double positionOnRtoue=0;
        while(pointChIterator.hasNext()) {
            Element rtept = doc.createElement("rtept");
            rte.appendChild(rtept);
            PointCh point=pointChIterator.next();
            double lat = point.lat();
            double lon = point.lon();
            //todo par default faut mettre les parentheses aux attributs je crois
            rtept.setAttribute("lat", " \" " + lat + " \" ");
            rtept.setAttribute("lon", " \" " + lon + " \" ");

            double altitude = profile.elevationAt(positionOnRtoue);
            Element ele = doc.createElement("ele");
            rtept.appendChild(ele);
            ele.setTextContent(String.valueOf(altitude));
            if(edgeIterator.hasNext())positionOnRtoue+=edgeIterator.next().length();
        }

        return doc;
    }

    //todo ask si les profiles c bien des elevationProfile
    public static void writeGpx(String fileName, Route route, ElevationProfile profile){
        Document doc= createGpx(route, profile);
        //todo utf8
        try(Writer w = new FileWriter(fileName, StandardCharsets.UTF_8)){
            try {
                Transformer transformer = TransformerFactory
                        .newDefaultInstance()
                        .newTransformer();
                transformer.setOutputProperty(OutputKeys.INDENT, "yes");
                transformer.transform(new DOMSource(doc),
                        new StreamResult(w));
            }catch (TransformerException e){
                throw new Error(e);
            }
        }
    }

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

    //todo ask if static
    private static Document createBaseDocument(){
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
        return doc;
    }
}
