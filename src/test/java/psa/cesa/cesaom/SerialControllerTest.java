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

    byte[] POLLER_ARRAY = {(byte) 0x01, (byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x08, (byte) 0x45, (byte) 0xC9};

    @Mock
    SerialPort port;

    @InjectMocks
    SerialController serialController = new SerialController("test");

    @BeforeEach
    public void setup() {
        Mockito.when(serialController.open()).thenReturn(true);
    }

    @Test
    public void constructorTest() {
        SerialPort port = serialController.getPort();
        Assertions.assertEquals(this.port, port);
    }


    @Test
    public void testOpen() {
        Assertions.assertTrue(serialController.open());
    }

    @Test
    public void testGetPorts() {
        for (SerialPort port : SerialController.getPorts()) {
            System.out.println(port);
        }
    }

}
