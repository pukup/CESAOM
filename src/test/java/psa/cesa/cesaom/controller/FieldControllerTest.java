package psa.cesa.cesaom.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.ComLine;
import psa.cesa.cesaom.model.Heliostat;
import psa.cesa.cesaom.model.XmlLinesReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
class FieldControllerTest {

    Map<Integer, ComLine> comLines;
    FieldController[] fieldControllers;

    @BeforeEach
    public void setup() {
        try {
            comLines = new HashMap<>();
            comLines = XmlLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml"));
            fieldControllers = new FieldController[comLines.size()];
            for (ComLine comLine : comLines.values()) {
                fieldControllers[comLine.getId()-1] = mock(FieldController.class);
                for (Heliostat heliostat : comLine.getHeliostats().values()) {
                    when(fieldControllers[comLine.getId()-1].command(heliostat.getId(), "a")).thenReturn("Command accepted");
                }
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
    public void sendCommandTest() {
        for (ComLine comLine : comLines.values()) {
            for (Heliostat heliostat : comLine.getHeliostats().values()) {
                assertEquals("Command accepted", fieldControllers[comLine.getId()-1].command(heliostat.getId(), "a"));
            }
        }
    }
}
