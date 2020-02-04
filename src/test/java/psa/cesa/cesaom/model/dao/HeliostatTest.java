package psa.cesa.cesaom.model.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.FieldController;
import psa.cesa.cesaom.model.ComLinesReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class HeliostatTest {

    FieldController fieldController;

    @BeforeEach
    public void setup() {
        try {
            fieldController = new FieldController(ComLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml")));
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
            Heliostat heliostat = fieldController.getComLines().get(1).getHeliostats().get(1);
            assertEquals(1, heliostat.getAddress());
            assertEquals("Abatimiento normal", heliostat.state0ToString());
            System.out.println(heliostat.state1ToString());
            assertEquals("Operación OK", heliostat.eventOperationToString());
            assertEquals("Seguridad OK", heliostat.eventSecurityToString());
            System.out.println(heliostat.eventComToString());
            assertEquals("Reloj OK", heliostat.eventCLToString());
            assertEquals("Movimiento OK", heliostat.diagnosisAz0ToString());
            assertEquals("Oscilación Fallo servo", heliostat.diagnosisAz1ToString());
            assertEquals("Posición OK", heliostat.diagnosisAz2ToString());
            assertEquals("Aviso OK", heliostat.diagnosisAz3ToString());
            assertEquals("Movimiento OK", heliostat.diagnosisEl0ToString());
            assertEquals("Oscilación Fallo servo", heliostat.diagnosisEl1ToString());
            assertEquals("Posición OK", heliostat.diagnosisEl2ToString());
            assertEquals("Aviso Cero encontrado", heliostat.diagnosisEl3ToString());
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