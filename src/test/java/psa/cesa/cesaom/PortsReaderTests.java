package psa.cesa.cesaom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class PortsReaderTests {

    @Test
    void readPortsTest() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.xml");
        Element port;
        String portId = null;
        try {
            NodeList ports = PortsReader.readPorts(inputStream);
            for (int i = 0; i < ports.getLength(); i++) {
                port = (Element) ports.item(i);
                portId = port.getAttribute("id");
                assertTrue(portId.equals(String.valueOf(i + 1)));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}