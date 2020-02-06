package psa.cesa.cesaom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.XmlComLinesReader;
import psa.cesa.cesaom.controller.FieldController;
import psa.cesa.cesaom.model.dao.ComLine;
import psa.cesa.cesaom.model.dao.Heliostat;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class FieldControllerTest {

    @Mock
    Map<Integer, ComLine> rows = new HashMap<>();

    @InjectMocks
    FieldController fieldController;

    @BeforeEach
    public void setup() {
        try {
            rows = XmlComLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml"));
            fieldController = new FieldController(rows);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void printReceivedBuffer() {
        try {
            Heliostat heliostat = fieldController.poll(1, 1);
            System.out.println(fieldController.printReceivedBuffer());
            Assertions.assertEquals("Comunicaciones OK", heliostat.eventComToString());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void sendCommandTest() {
        try {
            Assertions.assertTrue(fieldController.command(1, 1, "a"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
