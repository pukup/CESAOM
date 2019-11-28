package psa.cesa.cesaom.model;

import psa.cesa.cesaom.controller.SerialController;
import psa.cesa.cesaom.model.dao.Heliostat;
import psa.cesa.cesaom.model.dao.Row;

import java.nio.ByteBuffer;
import java.util.List;

/**
 *
 */
public class HeliostatController {
    /**
     *
     */
    private static final byte[] POLLER_ARRAY = {(byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x0C, (byte) 0x45, (byte) 0xC5};
    private List<Row> rows;

    public HeliostatController(Heliostat heliostat) {
    }

    /**
     *
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
    public void order() {

    }

}
