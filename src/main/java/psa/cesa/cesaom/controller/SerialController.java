package psa.cesa.cesaom.controller;

import com.fazecast.jSerialComm.SerialPort;

/**
 * Manages the JSerialComm API.
 * <p>
 * Its methods identify serial ports, so as send and receive bytes through them.
 */
public class SerialController {

    /**
     * @param port Allocates a <code>SerialPort</code> object corresponding to the cpu direction.
     */
    private SerialPort port;

    /**
     * @param portDir serial port computer address.
     */
    public SerialController(String portDir) {
        this.port = SerialPort.getCommPort(portDir);
        assignDefaultValues();
    }

    /**
     * @param baudRate    Signal's changes per second.
     * @param numDataBits
     * @param parity
     * @param numStopBits
     */
    public SerialController(String portDir, int baudRate, int numDataBits, boolean parity, int numStopBits) {
        this.port = SerialPort.getCommPort(portDir);
        this.port.setBaudRate(baudRate);
        this.port.setNumDataBits(numDataBits);
        this.port.setParity((parity) ? SerialPort.EVEN_PARITY : SerialPort.NO_PARITY);
        this.port.setNumStopBits(numStopBits);
    }

    /**
     * It tries to open the OS serial port.
     */
    public boolean open() {
        if (port.openPort()) {
            return true;
        } else {
            //            throw new RuntimeException("Couldn't open port " + port.toString());
            return false;
        }
    }

    /**
     * It sends bytes through the port.
     *
     * @param pollerFrame
     */
    public void send(byte[] pollerFrame) {
        port.writeBytes(pollerFrame, pollerFrame.length);
    }

    /**
     * It receive bytes through the port.
     */
    public byte[] receive() {
        byte[] polledFrame = new byte[port.bytesAvailable()];
        port.readBytes(polledFrame, port.bytesAvailable());
        return polledFrame;
    }

    /**
     * @return if the port has been closed truly.
     */
    public boolean close() {
        if (port.closePort()) {
            return true;
        } else {
            //            throw new RuntimeException("Couldn't close port " + port.toString());
            return false;
        }
    }

    /**
     * @return the API serial port.
     */
    public SerialPort getPort() {
        return this.port;
    }

    /**
     * Returns the computer serial ports.
     *
     * @return the ports from OS.
     */
    public static SerialPort[] getPorts() {
        return SerialPort.getCommPorts();
    }

    /**
     * It assigns specific values to the port attributes.
     * <p>
     * baudRate=19200.
     * numDataBits=8.
     * parity=NO_PARITY.
     * numStopBits=2.
     */
    private void assignDefaultValues() {
        port.setBaudRate(19200);
        port.setNumDataBits(8);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(2);
        port.setComPortTimeouts(SerialPort.TIMEOUT_READ_BLOCKING, 500, 500);
    }
}