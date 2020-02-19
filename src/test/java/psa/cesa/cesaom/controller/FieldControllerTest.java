package psa.cesa.cesaom.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.ComLine;
import psa.cesa.cesaom.model.Heliostat;
import psa.cesa.cesaom.model.XmlLinesReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class FieldControllerTest {

    @Mock
    Map<Integer, ComLine> comLines = new HashMap<>();

    @InjectMocks
    FieldController fieldController;

    @BeforeEach
    public void setup() {
        try {
            comLines = XmlLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml"));
            fieldController = new FieldController(comLines);
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void pollOne() {
        fieldController.pollOne(1, 1);
        Assertions.assertEquals(fieldController.getComLines().get(1).getHeliostats().get(1), new Heliostat(1));
    }

    @Test
    public void sendCommandTest() {
        try {
            fieldController.command(1, 1, "a");
            Assertions.assertEquals(fieldController.getComLines().get(1).getHeliostats().get(1), new Heliostat(1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
