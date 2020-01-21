package psa.cesa.cesaom.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.dao.Heliostat;
import psa.cesa.cesaom.model.dao.Row;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A <code>RowsReader</code> contains functionality to parse xml nodes into <code>Row</code> objects
 */
public class RowsReader {

    /**
     * @param path xml file
     * @return HashMap containing <code>Row</code> objects inside the xml file
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static Map<Integer, Row> getXmlRows(InputStream path) throws ParserConfigurationException, IOException, SAXException {
        Document document = getDocument(path);
        Element xmlRoot = document.getDocumentElement();
        NodeList xmlRows = xmlRoot.getElementsByTagName("row");
        Map<Integer, Row> rows = new HashMap<>();
        for (int i = 0; i < xmlRows.getLength(); i++) {
            Element xmlRow = (Element) xmlRows.item(i);
            int rowId = Integer.valueOf(xmlRow.getAttribute("id"));
            String portDir = xmlRow.getAttribute("dir");
            Row row = new Row(rowId, portDir, getXmlHeliostats(xmlRow));
            rows.put(rowId, row);
        }
        return rows;
    }

    /**
     * @param xmlRow xml node
     * @return HashMap containing <code>Heliostat</code> objects inside the xml row node
     */
    private static Map<Integer, Heliostat> getXmlHeliostats(Element xmlRow) {
        NodeList xmlAddresses = xmlRow.getElementsByTagName("heliostat");
        Map<Integer, Heliostat> heliostats = new HashMap<>();
        for (int i = 0; i < xmlAddresses.getLength(); i++) {
            Element xmlAddress = (Element) xmlAddresses.item(i);
            int heliostatAddress = Integer.valueOf(xmlAddress.getAttribute("id"));
            Heliostat heliostat = new Heliostat(heliostatAddress);
            heliostats.put(heliostatAddress, heliostat);
        }
        return heliostats;
    }

    /**
     * @param path xml file
     * @return A document builder with the parsed xml file
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws IOException
     */
    private static Document getDocument(InputStream path) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(path);
    }
}