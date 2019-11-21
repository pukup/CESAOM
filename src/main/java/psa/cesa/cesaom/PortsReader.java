package psa.cesa.cesaom;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

public class PortsReader {

    public static NodeList readPorts(InputStream path) throws ParserConfigurationException, IOException, SAXException {
        Element port = null;
        Document document = getDocument(path);
        Element xmlRoot = document.getDocumentElement();
        NodeList ports = xmlRoot.getElementsByTagName("port");
        return ports;
    }

    private static Document getDocument(InputStream path) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(path);
    }
}
