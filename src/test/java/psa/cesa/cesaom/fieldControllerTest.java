package psa.cesa.cesaom;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.controller.SerialController;
import psa.cesa.cesaom.model.FieldController;
import psa.cesa.cesaom.model.RowsReader;
import psa.cesa.cesaom.model.dao.Heliostat;
import psa.cesa.cesaom.model.dao.Row;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class fieldControllerTest {

    @Mock
    Map<Integer, Row> rows = new HashMap<>();

    @InjectMocks
    FieldController fieldController;

    @BeforeEach
    public void setup() {
        try {
            rows = RowsReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("test.xml"));
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
    void pollTest() {
        fieldController.poll(1, 1);
        Assertions.assertEquals(rows.get(1).getHeliostats().get(1).getPositionAZ(), 0);
    }
}
