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
    FieldController[] fieldControllers;

    @BeforeEach
    public void setup() {
        try {
            comLines = XmlLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml"));
            int i = 0;
            for (ComLine comLine : comLines.values()) {
                fieldControllers[i] = new FieldController(comLine);
                i++;
            }
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
        fieldControllers[0].pollOne(1);
        Assertions.assertEquals(fieldControllers[1].getComLine().getHeliostats().get(1), new Heliostat(1));
    }

    @Test
    public void sendCommandTest() {
        fieldControllers[0].command(1, "a");
        Assertions.assertEquals(fieldControllers[1].getComLine().getHeliostats().get(1), new Heliostat(1));
    }
}
