package psa.cesa.cesaom;

import com.fazecast.jSerialComm.SerialPort;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;
import psa.cesa.cesaom.controller.SerialController;

import javax.validation.constraints.AssertTrue;

import static org.junit.Assert.assertEquals;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SerialPort.class, SerialController.class})

public class SerialControllerTest {

    SerialController serialController;
    @Mock
    SerialPort serialPort;

    @Before
    public void setup() {
        this.serialPort = PowerMockito.mock(SerialPort.class);
        PowerMockito.mockStatic(SerialPort.class);
        PowerMockito.when(SerialPort.getCommPort(Matchers.anyString())).thenReturn(serialPort);
    }

    @Test
    public void constructorTest() {
        this.serialPort = Mockito.mock(SerialPort.class);
        PowerMockito.mockStatic(SerialPort.class);
        PowerMockito.when(SerialPort.getCommPort(Matchers.anyString())).thenReturn(serialPort);
        serialController = new SerialController("o");
        SerialPort port = Whitebox.getInternalState(serialController, "port");
        assertEquals(this.serialPort, port);
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
    }

    @Test
    public void testSend() {
    }

    @Test
    public void testRecibe() {
    }
}
