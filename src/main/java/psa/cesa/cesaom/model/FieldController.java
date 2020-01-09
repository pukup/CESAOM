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
    private static final byte[] POLL_ARRAY = {3, 0, 16, 0, 8, 69, (byte) 201};
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
        selectHeliostat(rowId, heliostatAddress);
        serialController.open();
        sendPollerArray();
        Thread.sleep(100);        ///////////////////////////////// check SerialController timeouts values
        checkPollResponse();
        serialController.close();
    }

    /**
     * Adds the <code>Heliostat</code> address and the poller bytes to a buffer and sends it
     */
    private void sendPollerArray() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostat.getAddress());
        byteBuffer.put(POLL_ARRAY);
        serialController.send(byteBuffer.array());
    }

    /**
     * Checks the received bytes and uses <method>setHelioState</method> to set the <code>Heliostat</code> attributes
     */
    private void checkPollResponse() {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            throw new RuntimeException("El heliostato no responde al poll");
        } else {
            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
            heliostat.setAttributes(receivedBuffer);
            row.getHeliostats().put(heliostat.getAddress(), heliostat); //???????¿?¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿¿??????????????
        }
    }

    /**
     * It targets a <code>Row</code> and an <code>Heliostat</code> to send commands
     *
     * @param rowId
     * @param heliostatAddress
     * @param command
     */
    public void command(int rowId, int heliostatAddress, String command) throws InterruptedException {
        serialController.open();
        selectHeliostat(rowId, heliostatAddress);
        sendCommandArray(command);
        Thread.sleep(100);        ///////////////////////////////// check SerialController timeouts values
        checkCommandResponse();
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
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 97, 103, (byte) 184};
                break;
            case "b":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 102, 38, (byte) 122};
                break;
            case "d":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 100, (byte) 167, (byte) 187};
                break;
            case "e":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 101, 102, 123};
                break;
            case "i":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 105, 102, 126};
                break;
            case "l":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 108, (byte) 166, 125};
                break;
            case "n":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 110, 39, (byte) 188};
                break;
            case "q":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 113, 102, 116};
                break;
            case "s":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 115, (byte) 231, (byte) 181};
                break;
        }
        return bytes;
    }

    public void sendFocus(int rowId, int heliostatAddress, int n) throws InterruptedException {
        selectHeliostat(rowId, heliostatAddress);
        serialController.open();
        sendFocusArray(n);
        Thread.sleep(100);        ///////////////////////////////// check SerialController timeouts values
        checkCommandResponse();
        serialController.close();
    }

    private void sendFocusArray(int n) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(20);
        byteBuffer.put((byte) heliostat.getAddress());
//                byteBuffer.put();
        serialController.send(byteBuffer.array());
    }

    public void sendFocus(int rowId, int heliostatAddress, long x, long y, long z) {
    }

    public void sendPosition(int rowId, int heliostatAddress, int az, int el) {
    }

    private void selectHeliostat(int rowId, int heliostatAddress) {
        row = rows.get(rowId);
        heliostat = row.getHeliostats().get(heliostatAddress);
        serialController = new SerialController(row.getPortDir());
    }

    private void checkCommandResponse() {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            throw new RuntimeException("El heliostato no responde");
        }
    }
}
