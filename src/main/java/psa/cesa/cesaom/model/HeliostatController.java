package psa.cesa.cesaom.model;

import psa.cesa.cesaom.controller.SerialController;
import psa.cesa.cesaom.model.dao.Row;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * It polls and command <code>Heliostat</code> objects within a <code>Row<code/>
 */
public class HeliostatController {
    /**
     * POLLER_ARRAY contents the bytes to send a poll request on any heliostat.
     * <p>
     * The address byte it must be added by <code>poll</code> method.
     */
    private static final byte[] POLLER_ARRAY = {(byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x08, (byte) 0x45, (byte) 0xC9};
    private Map<Integer, Row> rows;

    public HeliostatController(Map<Integer, Row> rows) {
        this.rows = rows;
    }

    public void getState(String rowId, int heliostatAddress) {
//        rows.get()
    }

    /**
     * @param rowId
     * @param heliostatAddress
     */
    public void poll(String rowId, int heliostatAddress) {
        SerialController serialController = new SerialController(rowId);
        serialController.open();
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostatAddress);
        byteBuffer.put(POLLER_ARRAY);
        serialController.send(byteBuffer.array());
        ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
    }

    /**
     *
     */
    public void sendCommand(String rowID, int heliostatAddress, String command) {
        SerialController serialController = new SerialController(rowID);
        serialController.open();
        ByteBuffer byteBuffer = ByteBuffer.allocate(32);
        byteBuffer.put((byte) heliostatAddress);
//        byteBuffer.put(getType(command));
    }

//    private byte[] getType(String command) {
//
//    }
}
