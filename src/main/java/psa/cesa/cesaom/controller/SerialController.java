package psa.cesa.cesaom.controller;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Manages the JSerialComm API
 * <p>
 * Its methods identify serial ports, so as send and receive bytes through them.
 */
public class SerialController {

    /**
     *
     */
    private static SerialPort[] ports;
    private com.fazecast.jSerialComm.SerialPort port;
    private String portName;

    /**
     * @param portName USB computer address
     */
    public SerialController(String portName) {
        this.port = SerialPort.getCommPort(portName);
        assignDefaultValues();
    }

    /**
     * @param portName    USB computer address
     * @param baudRate
     * @param numDataBits
     * @param parity
     * @param numStopBits
     */
    public SerialController(String portName, int baudRate, int numDataBits, boolean parity, int numStopBits) {
        this.portName = portName;
        this.port = SerialPort.getCommPort(portName);
        this.port.setBaudRate(baudRate);
        this.port.setNumDataBits(numDataBits);
        this.port.setParity((parity) ? SerialPort.EVEN_PARITY : SerialPort.NO_PARITY);
        this.port.setNumStopBits(numStopBits);
    }

    /**
     * Returns the computer serial ports
     *
     * @return ports
     */
    public static SerialPort[] getPorts() {
        ports = SerialPort.getCommPorts();
        return ports;
    }

    /**
     * It tries to open the serial port
     */
    public void open() {
        if (port.openPort()) {
            //dialog or something
        } else {
            //Exception
        }
    }

    /**
     * It sends bytes through the port
     *
     * @param pollerFrame
     */
    public void send(byte[] pollerFrame) {
        port.writeBytes(pollerFrame, pollerFrame.length);
    }

    /**
     * It receive bytes through the port
     */
    public byte[] receive() {
        byte[] polledFrame = new byte[port.bytesAvailable()];
        port.readBytes(polledFrame, port.bytesAvailable());
        return polledFrame;
    }

    /**
     * It assigns specific values to the port attributes
     * <p>
     * baudRate=19200
     * numDataBits=8
     * parity=NO_PARITY
     * numStopBits=2
     */
    private void assignDefaultValues() {
        port.setBaudRate(19200);
        port.setNumDataBits(8);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(2);
    }

    public SerialPort getPort(){
        return this.port;
    }
}
