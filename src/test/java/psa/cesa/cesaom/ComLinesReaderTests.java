package psa.cesa.cesaom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.ComLinesReader;
import psa.cesa.cesaom.model.dao.ComLine;
import psa.cesa.cesaom.model.dao.Heliostat;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class ComLinesReaderTests {

    @Test
    void getXmlRowsTest() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.xml");
        try {
            Map<Integer, ComLine> rows = ComLinesReader.getXmlRows(inputStream);
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
            Map<Integer, ComLine> rows = ComLinesReader.getXmlRows(inputStream);
            Heliostat heliostat = rows.get(1).getHeliostats().get(1);
            assertEquals(1, heliostat.getAddress());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}