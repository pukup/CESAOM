package psa.cesa.cesaom;

import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import psa.cesa.cesaom.controller.SerialController;

@SpringBootTest
public class SerialControllerTest {

    @Mock
    SerialPort port;

    @InjectMocks
    SerialController serialController = new SerialController("test");


    @BeforeEach
    public void setup() {
        Mockito.when(port.openPort()).thenReturn(true);
    }

    @Test
    public void constructorTest() {
        SerialPort port = serialController.getPort();
        Assertions.assertEquals(this.port, port);
    }

    @Test
    public void testGetPorts() {
        for (SerialPort port : SerialController.getPorts()
        ) {
            System.out.println(port.getDescriptivePortName());
        }
    }

    @Test
    public void testOpen() {
        serialController.open();
    }

    @Test
    public void testSend() {

    }

    @Test
    public void testRecibe() {
    }
}
