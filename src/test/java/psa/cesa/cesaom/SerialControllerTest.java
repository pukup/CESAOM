package psa.cesa.cesaom;

import org.junit.Before;
import org.junit.jupiter.api.Test;
import psa.cesa.cesaom.controller.SerialController;

public class SerialControllerTest {

    SerialController serialController;

    @Before
    void setup() {
        serialController = new SerialController("ttyUSB0");
    }

    @Test
    void testOpen() {
        serialController.open();

    }

    @Test
    void testSend() {

    }

    @Test
    void testRecibe() {

    }
}
