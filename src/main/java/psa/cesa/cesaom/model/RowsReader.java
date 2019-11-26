package psa.cesa.cesaom.model;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.dao.Row;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * A <code>RowsReader</code> contains functionality to parse xml nodes into <code>Row</code> objects
 */
public class RowsReader {

    /**
     * @param path xml file
     * @return a list containing <code>Row</code> objects inside the xml file
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public static List<Row> readPorts(InputStream path) throws ParserConfigurationException, IOException, SAXException {
        Document document = getDocument(path);
        Element xmlRoot = document.getDocumentElement();
        NodeList xmlRows = xmlRoot.getElementsByTagName("row");
        List<Row> rows = new ArrayList<Row>();
        for (int i = 0; i < xmlRows.getLength(); i++) {
            Element xmlRow = (Element) xmlRows.item(i);
            Row row = new Row();
            row.setId(xmlRow.getAttribute("id"));
            row.setAddresses(getXmlAddressesID(xmlRow));
            rows.add(row);
        }
        return rows;
    }

    /**
     * @param xmlRow xml node
     * @return a list containing Integers inside the xml row node
     */
    private static List<Integer> getXmlAddressesID(Element xmlRow) {
        NodeList xmlAddresses = xmlRow.getElementsByTagName("address");
        List<Integer> addresses = new ArrayList<>();
        for (int j = 0; j < xmlAddresses.getLength(); j++) {
            Element xmlAddress = (Element) xmlAddresses.item(j);
            addresses.add(Integer.valueOf(xmlAddress.getAttribute("id")));
        }
        return addresses;
    }

    /**
     * @param path xml file
     * @return Document the parsed xml file
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
