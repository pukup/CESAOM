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
        serialController.open();
        sendPollerArray();
        Thread.sleep(25); // check serial controller timeouts values
        receivePolledArray();
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
    private void receivePolledArray() {
        if (serialController.getPort().bytesAvailable() > 0) {
            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
            setHelioState(receivedBuffer);
            row.getHeliostats().put(heliostat.getAddress(), heliostat);
        } else {
            System.out.println("To do: Set failed coms");
            //            buffer communication loss
            //            setHelioState();
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
    public void sendCommand(int rowId, int heliostatAddress, int command) {
        Row row = rows.get(rowId);
        Heliostat heliostat = row.getHeliostats().get(heliostatAddress);
        SerialController serialController = new SerialController(row.getPortDir());
        serialController.open();
        ByteBuffer byteBuffer = ByteBuffer.allocate(16);
        byteBuffer.put((byte) heliostat.getAddress());
        byteBuffer.put(selectCommand(command));
        serialController.send(byteBuffer.array());
        serialController.close();
    }

    private byte[] selectCommand(int command) {
        return new byte[0];
    }

}