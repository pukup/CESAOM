package psa.cesa.cesaom.controller;

import psa.cesa.cesaom.model.CRC;
import psa.cesa.cesaom.model.ComLine;
import psa.cesa.cesaom.model.Heliostat;

import java.nio.ByteBuffer;

/**
 * It polls and commands <code>Heliostat</code> objects within a <code>ComLine<code/>.
 */
public class FieldController {
    /**
     * @param POLL_ARRAY Contents the bytes to send a poll request on any heliostat.
     * Address and CRC bytes must be added by <method>pollOne</method> which calls <method>setPollerFrame</method>.
     * @param HOUR_AARRAY Contents the bytes to send a poll request on any heliostat.
     * Address and CRC bytes must be added by <method>getHour</method> which calls <method>setHourFrame</method>.
     * @param comLine Contents a <object>ComLine</object> from the xml file.
     * @param serialController contains the methods to control the jSerialComm API.
     */
    private static final byte[] POLL_ARRAY = {0x03, 0x00, 0x10, 0x00, 0x08};
    private static final byte[] HOUR_ARRAY = {0x03, 0x03, (byte) 0xEB, 0x00, 0x02};
    private ComLine comLine;
    private SerialController serialController;

    public FieldController(ComLine comLine) {
        this.comLine = comLine;
        openSerialController();
    }

    /**
     * Gets the <code>ComLine</code> portDir and opens it.
     */
    private void openSerialController() {
        try {
            serialController = new SerialController(comLine.getPortDir());
            serialController.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ComLine getComLine() {
        return comLine;
    }

    /**
     * It targets a <code>ComLine</code> and an <code>Heliostat</code> to send and receive the poll bytes from it.
     *
     * @param heliostatId represents the modbus slave address.
     */
    public void pollOne(int heliostatId) {
        try {
            if (!serialController.getPort().isOpen())
                serialController.open();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setPollerFrame(heliostatId));
            Thread.sleep(250);
            checkPollResponse(heliostat);
            comLine.getHeliostats().put(heliostatId, heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            e.printStackTrace();
        }
    }

    /**
     * Adds the <object>Heliostat</object> address, the poller bytes and CRC into an array.
     *
     * @param heliostatId represents the number or position of the comLine.
     * @return The poller frame for an specific <code>Heliostat</code>.
     */
    private synchronized byte[] setPollerFrame(int heliostatId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(POLL_ARRAY);
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 6));
        System.out.println(bufferToString(byteBuffer));
        return byteBuffer.array();
    }

    /**
     * Checks if there are any received bytes from the <code>SerialController</code> port and uses <method>setHelioState</method> to update the <code>Heliostat</code> attributes.
     *
     * @param heliostat represents the RTU itself.
     */
    private synchronized void checkPollResponse(Heliostat heliostat) {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            System.out.println("No response");
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(serialController.receive());
            heliostat.setAttributes(byteBuffer);
            System.out.println(bufferToString(byteBuffer));
        }
    }

    /**
     * Gets the received bytes and put them into a string.
     *
     * @param byteBuffer wraps bytes from the received or send frames.
     * @return Containing the byteBuffer formatted into hexadecimal.
     */
    public String bufferToString(ByteBuffer byteBuffer) {
        StringBuffer s = new StringBuffer();
        for (byte b : byteBuffer.array()) {
            s.append(String.format("%02x ", b));
        }
        return s.toString();
    }

    /**
     * It targets a <code>ComLine</code> and an <code>Heliostat</code> to send commands.
     *
     * @param heliostatId
     * @param command
     */
    public String command(int heliostatId, String command) {
        try {
            if (!serialController.getPort().isOpen())
                serialController.open();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setCommandFrame(heliostatId, command));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        }
    }

    /**
     * Adds the <code>Heliostat</code> address and the command bytes to a buffer and sends it.
     *
     * @param command ASCII representation to switch between different commands.
     */
    private byte[] setCommandFrame(int heliostatId, String command) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(11);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(selectCommand(command));
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 9));
        return byteBuffer.array();
    }

    /**
     * @param heliostat
     * @return
     */
    private String checkCommandResponse(Heliostat heliostat) {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            System.out.println("No response");
            return "No response.";
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(serialController.receive());
            System.out.println(bufferToString(byteBuffer));
            return bufferToString(byteBuffer);
        }
    }

    /**
     * It adds modbus function code 16 byte, <code>ComLine</code> id, <code>Heliostat</code> id, and one ASCII command selected from the switch case.
     * <p>
     * It adds switches between commands with no extra parameters needed.
     * <p>
     * "a" Dejection.
     * "b" Down security aisle.
     * "d" Kilter tracking.
     * "e" Boiler focus.
     * "i" Immobilize.
     * "l" Out of service.
     * "n" Normal tracking.
     * "q" Emergency focus.
     * "s" Kilter tracking throw security aisle.
     *
     * @param command the ASCII value of the command.
     * @return byte array with the function and the command.
     */
    private byte[] selectCommand(String command) {
        byte[] bytes = new byte[]{16, 0, 0, 0, 1, 2, 0, 0};
        switch (command) {
            case "a":
                bytes[7] = 97;
                break;
            case "b":
                bytes[7] = 102;
                break;
            case "d":
                bytes[7] = 100;
                break;
            case "e":
                bytes[7] = 101;
                break;
            case "i":
                bytes[7] = 105;
                break;
            case "l":
                bytes[7] = 108;
                break;
            case "n":
                bytes[7] = 110;
                break;
            case "q":
                bytes[7] = 113;
                break;
            case "s":
                bytes[7] = 115;
                break;
        }
        return bytes;
    }

    //TO DO: setHour in the whole field

    //    /**
    //     * @param comLineID
    //     * @param heliostatId
    //     * @throws InterruptedException
    //     */
    //    public void getHour(int comLineID, int heliostatId) throws InterruptedException {
    //        selectHeliostat(comLineID, heliostatId);
    //        serialController.open();
    //        sendHourFrame();
    //        Thread.sleep(100);
    //        if (serialController.getPort().bytesAvailable() < 1) {
    //            heliostat.setEvent(0x10);
    //            //            throw new RuntimeException("El heliostato no responde al poll");
    //        } else {
    //            ByteBuffer receivedBuffer = ByteBuffer.wrap(serialController.receive());
    //            for (byte bits : receivedBuffer.array()) {
    //                System.out.format("0x%h ", bits);
    //            }
    //        }
    //        serialController.close();
    //    }
    //
    //    /**
    //     * It targets a <code>ComLine</code> and an <code>Heliostat</code> to ask 3 bytes from the 218 address which keeps hour
    //     */
    //    private void sendHourFrame() {
    //        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
    //        byteBuffer.put((byte) heliostat.getId());
    //        byteBuffer.put(HOUR_ARRAY);
    //        byteBuffer.put(CRC.calculate(byteBuffer.array(), 6));
    //        serialController.send(byteBuffer.array());
    //    }
    //
    //    /**
    //     * @param comLineId
    //     * @param heliostatId
    //     * @param n
    //     * @return
    //     * @throws InterruptedException
    //     */
    //    public void sendFocus(int comLineId, int heliostatId, int n) throws InterruptedException {
    //        selectHeliostat(comLineId, heliostatId);
    //        serialController.open();
    //        setFocusArray(n);
    //        Thread.sleep(100);
    //        checkCommandResponse();
    //        serialController.close();
    //    }
    //
    //    /**
    //     * @param n
    //     */
    //    private void setFocusArray(int n) {
    //        ByteBuffer byteBuffer = ByteBuffer.allocate(0);
    //        byteBuffer.put((byte) heliostat.getId());
    //        //                        byteBuffer.put();
    //        //        CRC.calculate(byteBuffer.array());
    //        serialController.send(byteBuffer.array());
    //    }
    //
    //    /**
    //     * @param rowId
    //     * @param heliostatAddress
    //     * @param x
    //     * @param y
    //     * @param z
    //     */
    //    public void sendFocus(int rowId, int heliostatAddress, long x, long y, long z) {
    //    }
    //
    //    /**
    //     * @param rowId
    //     * @param heliostatAddress
    //     * @param az
    //     * @param el
    //     */
    //    public void sendPosition(int rowId, int heliostatAddress, int az, int el) {
    //    }
}
