package psa.cesa.cesaom.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class XmlLinesReaderTests {

    @Test
    void getXmlRowsTest() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.xml");
        try {
            Map<Integer, ComLine> rows = XmlLinesReader.getXmlRows(inputStream);
            assertEquals(1, rows.get(1).getId());
            assertEquals("/dev/ttyUSB0", rows.get(1).getPortDir());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getXmlGeliostatsTest() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.xml");
        try {
            Map<Integer, ComLine> rows = XmlLinesReader.getXmlRows(inputStream);
            Heliostat heliostat = rows.get(1).getHeliostats().get(1);
            assertEquals(1, heliostat.getId());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}