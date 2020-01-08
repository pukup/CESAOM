package psa.cesa.cesaom.model;

import psa.cesa.cesaom.controller.SerialController;
import psa.cesa.cesaom.model.dao.Heliostat;
import psa.cesa.cesaom.model.dao.Row;

import java.nio.ByteBuffer;
import java.util.Map;

/**
 * It polls and commands <code>Heliostat</code> objects within a <code>Row<code/>
 */
public class FieldController {
    /**
     * @param POLL_ARRAY Contents the bytes to send a poll request on any heliostat.
     * The address byte must be added by <method>poll</method> method.
     * @param rows contents a map filled with all the <code>Row</code> objects of the xml file
     * @param row
     * @param heliostat
     * @param serialController contains the methods to control the jSerialComm api
     */
    private static final byte[] POLL_ARRAY = {(byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x08, (byte) 0x45, (byte) 0xC9};
    private Map<Integer, Row> rows;
    private Row row;
    private Heliostat heliostat;
    private SerialController serialController;

    public FieldController(Map<Integer, Row> rows) {
        this.rows = rows;
    }

    public Map<Integer, Row> getRows() {
        return rows;
    }

    public void setRows(Map<Integer, Row> rows) {
        this.rows = rows;
    }

    /**
     * It targets a <code>Row</code> and an <code>Heliostat</code> to send and receive the poll bytes from it
     *
     * @param rowId            represents the number or position of the row
     * @param heliostatAddress represents the modbus slave address
     */
    public void poll(int rowId, int heliostatAddress) throws Exception {
        row = rows.get(rowId);
        heliostat = row.getHeliostats().get(heliostatAddress);
        serialController = new SerialController(row.getPortDir());
        sendPollerArray();
        Thread.sleep(100);        ///////////////////////////////// check SerialController timeouts values
        receivePolledArray();
        serialController.close();
    }

    /**
     * Adds the <code>Heliostat</code> address and the poller bytes to a buffer and sends it
     */
    private void sendPollerArray() {
        serialController.open();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostat.getAddress());
        byteBuffer.put(POLL_ARRAY);
        serialController.send(byteBuffer.array());
    }

    /**
     * Checks the received bytes and uses <method>setHelioState</method> to set the <code>Heliostat</code> attributes
     */
    private void receivePolledArray() {
        if (serialController.getPort().bytesAvailable() > 0) {
            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
            setHelioState(receivedBuffer);
            row.getHeliostats().put(heliostat.getAddress(), heliostat);
        } else {
            System.out.println("todo loss coms");
            heliostat.setEvent(0x10);
        }
    }

    /**
     * Sets the <code>Heliostat</code> attributes
     *
     * @param receivedBuffer
     */
    private void setHelioState(ByteBuffer receivedBuffer) {
        for (int i = 0; i < receivedBuffer.array().length; i++) {
            Byte b = receivedBuffer.get(i);
            switch (i) {
                case 4:
                    heliostat.setState(b);
                    break;
                case 6:
                    heliostat.setEvent(b);
                    break;
                case 8:
                    heliostat.setDiagnosysAZ(b);
                    break;
                case 10:
                    heliostat.setDiagnosysEL(b);
                    break;
                case 12:
                    heliostat.setPositionAZ(b);
                    break;
                case 14:
                    heliostat.setPositionEL(b);
                    break;
                case 16:
                    heliostat.setSetPointAZ(b);
                    break;
                case 18:
                    heliostat.setSetPointEL(b);
                    break;
            }
        }
    }

    /**
     * It targets a <code>Row</code> and an <code>Heliostat</code> to send commands
     *
     * @param rowId
     * @param heliostatAddress
     * @param command
     */
    public void sendCommand(int rowId, int heliostatAddress, String command) throws InterruptedException {
        row = rows.get(rowId);
        heliostat = row.getHeliostats().get(heliostatAddress);
        serialController = new SerialController(row.getPortDir());
        serialController.open();
        sendCommandArray(command);
        Thread.sleep(100);
        if (serialController.getPort().bytesAvailable() > 0) {
        } else {
            System.out.println("todo loss coms");
            heliostat.setEvent(0x10);
        }
        serialController.close();
    }

    /**
     * Adds the <code>Heliostat</code> address and the command bytes to a buffer and sends it
     *
     * @param command
     */
    private void sendCommandArray(String command) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        byteBuffer.put((byte) heliostat.getAddress());
        byteBuffer.put(new byte[]{16, 0, 0, 0, 1, 2, 0});
        byteBuffer.put(selectCommand(command));
        serialController.send(byteBuffer.array());
    }

    /**
     * It switches between commands with no parameter needed
     *
     * @param command
     * @return byte array with the function and
     */
    private byte[] selectCommand(String command) {
        byte[] bytes = null;
        switch (command) {
            case "a":
                bytes = new byte[]{(byte) 97, (byte) 103, (byte) 184};
                break;
            case "b":
                bytes = new byte[]{(byte) 0x102, (byte) 0x38, (byte) 0x122};
                break;
            case "d":
                bytes = new byte[]{(byte) 0x100, (byte) 0x167, (byte) 0x187};
                break;
            case "e":
                bytes = new byte[]{(byte) 0x101, (byte) 0x102, (byte) 0x123};
                break;
            case "i":
                bytes = new byte[]{(byte) 0x105, (byte) 0x102, (byte) 0x126};
                break;
            case "l":
                bytes = new byte[]{(byte) 0x108, (byte) 0x166, (byte) 0x125};
                break;
            case "n":
                bytes = new byte[]{(byte) 0x110, (byte) 0x39, (byte) 0x188};
                break;
            case "q":
                bytes = new byte[]{(byte) 0x113, (byte) 0x102, (byte) 0x116};
                break;
            case "s":
                bytes = new byte[]{(byte) 0x115, (byte) 0x231, (byte) 0x181};
                break;
        }
        return bytes;
    }

    public void sendFocus(int rowId, int heliostatAddress, int n) {
    }

    public void sendFocus(int rowId, int heliostatAddress, long x, long y, long z) {
    }

    public void sendPosition(int rowId, int heliostatAddress, int az, int el) {
    }

}