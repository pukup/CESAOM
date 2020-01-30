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
    private static final byte[] POLL_ARRAY = {0x03, 0x00, 0x10, 0x00, 0x08};
    private static final byte[] HOUR_ARRAY = {0x03, 0x03, (byte) 0xEB, 0x00, 0x02};
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
    public Heliostat poll(int rowId, int heliostatAddress) throws Exception {
        selectHeliostat(rowId, heliostatAddress);
        serialController.open();
        sendPollerArray();
        Thread.sleep(100);
        checkPollResponse();
        serialController.close();
        return heliostat;
    }

    /**
     * Adds the <code>Heliostat</code> address and the poller bytes to a buffer and sends it
     */
    private void sendPollerArray() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostat.getAddress());
        byteBuffer.put(POLL_ARRAY);
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 6));
        serialController.send(byteBuffer.array());
    }

    /**
     * Checks the received bytes and uses <method>setHelioState</method> to set the <code>Heliostat</code> attributes
     */
    private Heliostat checkPollResponse() {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            throw new RuntimeException("El heliostato no responde al poll");
        } else {
            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
            heliostat.setAttributes(receivedBuffer);
            row.getHeliostats().put(heliostat.getAddress(), heliostat);
            return heliostat;
        }
    }

    /**
     * Checks the received bytes and put them into a string
     *
     * @param rowId
     * @param heliostatAddress
     * @throws InterruptedException
     */
    public String printReceivedBuffer(int rowId, int heliostatAddress) throws InterruptedException {
        StringBuffer s = new StringBuffer("| ");
        selectHeliostat(rowId, heliostatAddress);
        serialController.open();
        sendPollerArray();
        Thread.sleep(100);
        if (serialController.getPort().bytesAvailable() < 1) {
            s.append("No contesta");
        } else {
            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
            for (byte b : receivedBuffer.array()) {
                s.append(String.format("%02x ", b));
            }
        } serialController.close();
        return s.toString();
    }

    /**
     * It targets a <code>Row</code> and an <code>Heliostat</code> to send commands
     *
     * @param rowId
     * @param heliostatAddress
     * @param command
     */
    public boolean command(int rowId, int heliostatAddress, String command) throws InterruptedException {
        selectHeliostat(rowId, heliostatAddress);
        serialController.open();
        sendCommandArray(command);
        Thread.sleep(100);
        boolean b = checkCommandResponse();
        serialController.close();
        return b;
    }

    /**
     * Adds the <code>Heliostat</code> address and the command bytes to a buffer and sends it
     *
     * @param command
     */
    private void sendCommandArray(String command) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(11);
        byteBuffer.put((byte) heliostat.getAddress());
        byteBuffer.put(selectCommand(command));
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 9));
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
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 97};
                break;
            case "b":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 102};
                break;
            case "d":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 100};
                break;
            case "e":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 101};
                break;
            case "i":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 105};
                break;
            case "l":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 108};
                break;
            case "n":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 110};
                break;
            case "q":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 113};
                break;
            case "s":
                bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 115};
                break;
        }
        return bytes;
    }

    //TO DO: setHour in the whole field

    /**
     * @param rowId
     * @param heliostatAddress
     * @throws InterruptedException
     */
    public void getHour(int rowId, int heliostatAddress) throws InterruptedException {
        selectHeliostat(rowId, heliostatAddress);
        serialController.open();
        sendHourFrame();
        Thread.sleep(100);
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            //            throw new RuntimeException("El heliostato no responde al poll");
        } else {
            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
            for (byte bits : receivedBuffer.array()) {
                System.out.format("0x%h ", bits);
            }
        }
        serialController.close();
    }

    /**
     * It targets a <code>Row</code> and an <code>Heliostat</code> to ask 3 bytes from the 218 address which keeps hour
     */
    private void sendHourFrame() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostat.getAddress());
        byteBuffer.put(HOUR_ARRAY);
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 6));
        serialController.send(byteBuffer.array());
    }

    public boolean sendFocus(int rowId, int heliostatAddress, int n) throws InterruptedException {
        selectHeliostat(rowId, heliostatAddress);
        serialController.open();
        sendFocusArray(n);
        Thread.sleep(100);
        boolean bool = checkCommandResponse();
        serialController.close();
        return bool;
    }

    private void sendFocusArray(int n) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(0);
        byteBuffer.put((byte) heliostat.getAddress());
        //                        byteBuffer.put();
        //        CRC.calculate(byteBuffer.array());
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

    private boolean checkCommandResponse() {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            throw new RuntimeException("El heliostato no responde al comando");
        } else {
            return true;
        }
    }
}
