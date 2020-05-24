package psa.cesa.cesaom.controller;

import com.fazecast.jSerialComm.SerialPort;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class SerialControllerTest {

    @Mock
    SerialPort port;

    @InjectMocks
    SerialController serialController = new SerialController("test");

    @Test
    public void constructorTest() {
        SerialPort port = serialController.getPort();
        assertEquals(this.port, port);
    }

    @Test
    public void testOpen() {
        when(serialController.open()).thenReturn(true);
        assertTrue(serialController.open());
    }
}
