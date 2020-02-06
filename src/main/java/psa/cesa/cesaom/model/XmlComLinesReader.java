package psa.cesa.cesaom.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.dao.ComLine;
import psa.cesa.cesaom.model.dao.Heliostat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A <code>XmlComLinesReader</code> contains functionality to parse xml nodes into communication lines represented as <code>ComLine</code> objects
 */
public class XmlComLinesReader {

    /**
     * @param OS Keeps the operation system of the computer.
     */
    private static String OS = null;

    /**
     * It parses a xml file into a <code>HashMap</code> which contains the field's <code>Comline</code> objects.
     *
     * @param path file containing the field's comLines and its RTU.
     * @return <code>ComLine</code> objects from the xml file.
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Map<Integer, ComLine> getXmlRows(InputStream path) throws ParserConfigurationException, IOException, SAXException {
        Document document = getDocument(path);
        Element xmlRoot = document.getDocumentElement();
        NodeList xmlRows = xmlRoot.getElementsByTagName("comLine");
        Map<Integer, ComLine> comLines = new HashMap<>();
        for (int i = 0; i < xmlRows.getLength(); i++) {
            Element xmlComLines = (Element) xmlRows.item(i);
            int comLineId = Integer.valueOf(xmlComLines.getAttribute("id"));
            String portDir = getPortDir(xmlComLines);
            ComLine comLine = new ComLine(comLineId, portDir, getXmlHeliostats(xmlComLines));
            comLines.put(comLineId, comLine);
        }
        return comLines;
    }

    /**
     * Uses a <code>DocumentBuilderFactory</code> to parse from an xml to a <code>Document</code> object.
     *
     * @param path xml file.
     * @return parsed xml file.
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private static Document getDocument(InputStream path) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(path);
    }

    /**
     * It uses an xml node which contains the comLine's <code>Heliostat</code> objects.
     *
     * @param xmlComLine xml node.
     * @return <code>Heliostat</code> objects inside the xml comLine node.
     */
    private static Map<Integer, Heliostat> getXmlHeliostats(Element xmlComLine) {
        NodeList xmlAddresses = xmlComLine.getElementsByTagName("heliostat");
        Map<Integer, Heliostat> heliostats = new HashMap<>();
        for (int i = 0; i < xmlAddresses.getLength(); i++) {
            Element xmlAddress = (Element) xmlAddresses.item(i);
            int heliostatAddress = Integer.valueOf(xmlAddress.getAttribute("id"));
            Heliostat heliostat = new Heliostat(heliostatAddress);
            heliostats.put(heliostatAddress, heliostat);
        }
        return heliostats;
    }

    private static String getPortDir(Element xmlComLines) {
        if (isLinux()) {
            return xmlComLines.getAttribute("linuxDir");
        } else {
            return xmlComLines.getAttribute("winDir");
        }
    }

    private static boolean isLinux() {
        return getOsName().startsWith("Linux");
    }

    private static String getOsName() {
        if (OS == null) {
            OS = System.getProperty("os.name");
        }
        return OS;
    }

}