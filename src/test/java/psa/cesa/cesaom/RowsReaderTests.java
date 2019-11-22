package psa.cesa.cesaom;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.Row;
import psa.cesa.cesaom.model.RowsReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class RowsReaderTests {

    @Test
    void readPortsTest() {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("test.xml");
        try {
            List<Row> rows = RowsReader.readPorts(inputStream);
            assertEquals("1", rows.get(0).getId());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}