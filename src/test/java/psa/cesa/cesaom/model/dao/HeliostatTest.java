package psa.cesa.cesaom.model.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.FieldController;
import psa.cesa.cesaom.model.RowsReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HeliostatTest {

    FieldController fieldController;

    @BeforeEach
    public void setup() {
        try {
            fieldController = new FieldController(RowsReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml")));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void ToString() {
        try {
            fieldController.poll(1, 1);
            Heliostat heliostat = fieldController.getRows().get(1).getHeliostats().get(1);
            assertEquals(1, heliostat.getAddress());
            assertEquals("Abatimiento normal", heliostat.state0ToString());
            System.out.println(heliostat.state1ToString());
            assertEquals("Operación OK", heliostat.eventOperationToString());
            assertEquals("Seguridad OK", heliostat.eventSecurityToString());
            System.out.println(heliostat.eventComToString());
            assertEquals("Reloj OK", heliostat.eventCLToString());
            assertEquals("Movimiento OK", heliostat.diagnosysAz0ToString());
            assertEquals("Oscilación Fallo servo", heliostat.diagnosysAz1ToString());
            assertEquals("Posición OK", heliostat.diagnosysAz2ToString());
            assertEquals("Aviso OK", heliostat.diagnosysAz3ToString());
            assertEquals("Movimiento OK", heliostat.diagnosysEl0ToString());
            assertEquals("Oscilación Fallo servo", heliostat.diagnosysEl1ToString());
            assertEquals("Posición OK", heliostat.diagnosysEl2ToString());
            assertEquals("Aviso Cero encontrado", heliostat.diagnosysEl3ToString());
            System.out.println(heliostat.getPositionAZ());
            System.out.println(heliostat.getPositionEL());
            System.out.println(heliostat.getSetPointAZ());
            System.out.println(heliostat.getSetPointEL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void askHourTest() {
        try {
            fieldController.getHour(1, 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}