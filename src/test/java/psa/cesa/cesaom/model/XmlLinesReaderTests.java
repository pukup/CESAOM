package psa.cesa.cesaom.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class XmlLinesReaderTests {

    Map<Integer, ComLine> comLines = new HashMap<>();

    @BeforeEach
    public void setup() {
        try {
            comLines = XmlLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml"));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    void getXmlRowsTest() {
        assertEquals(1, comLines.get(1).getId());
        assertEquals("/dev/ttyUSB0", comLines.get(1).getPortDir());
    }

    @Test
    void getXmlHeliostatsTest() {
        Heliostat heliostat = comLines.get(1).getHeliostats().get(1);
        assertEquals(1, heliostat.getId());
    }
}