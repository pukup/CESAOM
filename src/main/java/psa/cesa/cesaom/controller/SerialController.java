package psa.cesa.cesaom.controller;

import com.fazecast.jSerialComm.SerialPort;

public class SerialController {

    private com.fazecast.jSerialComm.SerialPort port;
    private String id;

    public SerialController(String id) {
        this.id = id;
        assignDefaultValues();
    }

    public SerialController(String id, int baudRate, int numDataBits, boolean parity, int numStopBits) {
        this.id = id;
        this.port.setBaudRate(baudRate);
        this.port.setNumDataBits(numDataBits);
        this.port.setParity((parity) ? SerialPort.EVEN_PARITY : SerialPort.NO_PARITY);
        this.port.setNumStopBits(numStopBits);
    }

    public void open() {
    }

    public void send() {
    }

    public void recibe() {
    }

    private void assignDefaultValues() {
        port.setBaudRate(19200);
        port.setNumDataBits(8);
        port.setParity(SerialPort.NO_PARITY);
        port.setNumStopBits(2);
    }
}
