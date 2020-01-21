package psa.cesa.cesaom.model.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.FieldController;
import psa.cesa.cesaom.model.RowsReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

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
            //change prints for asserts
            System.out.println("address" + heliostat.getAddress());
            System.out.println(heliostat.state0ToString());
            System.out.println(heliostat.state1ToString());
            System.out.println(heliostat.eventOperationToString());
            System.out.println(heliostat.eventSecurityToString());
            System.out.println(heliostat.eventComToString());
            System.out.println(heliostat.eventCLToString());
            System.out.println(heliostat.diagnosysAz0ToString());
            System.out.println(heliostat.diagnosysAz1ToString());
            System.out.println(heliostat.diagnosysAz2ToString());
            System.out.println(heliostat.diagnosysAz3ToString());
            System.out.println(heliostat.diagnosysEl0ToString());
            System.out.println(heliostat.diagnosysEl1ToString());
            System.out.println(heliostat.diagnosysEl2ToString());
            System.out.println(heliostat.diagnosysEl3ToString());
            System.out.println(heliostat.getPositionAZ());
            System.out.println(heliostat.getPositionEL());
            System.out.println(heliostat.getSetPointAZ());
            System.out.println(heliostat.getSetPointEL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void askHourTest(){
        try {
            fieldController.getHour(1, 1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}