package psa.cesa.cesaom.model;

import com.fazecast.jSerialComm.SerialPort;
import psa.cesa.cesaom.controller.SerialController;
import psa.cesa.cesaom.model.dao.Heliostat;
import psa.cesa.cesaom.model.dao.Row;

import javax.print.attribute.HashAttributeSet;
import java.nio.ByteBuffer;
import java.util.Map;

/**
 * It polls and commands <code>Heliostat</code> objects within a <code>Row<code/>
 */
public class FieldController {
    /**
     * POLLER_ARRAY contents the bytes to send a poll request on any heliostat.
     * <p>
     * The address byte must be added by <code>poll</code> method.
     */
    private static final byte[] POLLER_ARRAY = {(byte) 0x03, (byte) 0x00, (byte) 0x10, (byte) 0x00, (byte) 0x08, (byte) 0x45, (byte) 0xC9};
    private Map<Integer, Row> rows;

    /**
     * @param rows
     */
    public FieldController(Map<Integer, Row> rows) {
        this.rows = rows;
    }

    /**
     *
     */
    public void poll(int rowId, int heliostatAddress) {
        Row row = rows.get(rowId);
        Heliostat heliostat = selectHeliostat(rowId, heliostatAddress);
        if (heliostat != null) {
            SerialController serialController = new SerialController(row.getPortDir());
            serialController.open();
            ByteBuffer byteBuffer = ByteBuffer.allocate(8);
            byteBuffer.put((byte) heliostat.getAddress());
            byteBuffer.put(POLLER_ARRAY);
            serialController.send(byteBuffer.array());
            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
            serialController.close();
            setHelioState(heliostat, receivedBuffer);
            row.getHeliostats().put(heliostatAddress, heliostat);
        }
    }

    /**
     * @param rowId
     * @param heliostatAddress
     */
    private Heliostat selectHeliostat(int rowId, int heliostatAddress) {
        Row row = rows.get(rowId);
        Heliostat heliostat = row.getHeliostats().get(heliostatAddress);
        return heliostat;
    }

    private void setHelioState(Heliostat heliostat, ByteBuffer receivedBuffer) {
        heliostat.setState(0);
        heliostat.setEvent(0);
        heliostat.setDiagnosys(0);
        heliostat.setPositionAZ(0);
        heliostat.setPositionEL(0);
        heliostat.setSetPointAZ(0);
        heliostat.setSetPointEL(0);
    }

    /**
     *
     */
    public void sendCommand(int rowId, int heliostatAddress, String command) {
        Row row = rows.get(rowId);
        Heliostat heliostat = selectHeliostat(rowId, heliostatAddress);
        if (heliostat != null) {
            SerialController serialController = new SerialController(row.getPortDir());
            serialController.open();
            ByteBuffer byteBuffer = ByteBuffer.allocate(16);
            byteBuffer.put((byte) heliostat.getAddress());
            byteBuffer.put(getType(command));
            serialController.send(byteBuffer.array());
        }
    }

    private byte[] getType(String command) {
        return null;
    }
}