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

    public ComLine getComLine() {
        return comLine;
    }

    /**
     * Gets the <code>ComLine</code> portDir and opens it.
     */
    private void openSerialController() {
        serialController = new SerialController(comLine.getPortDir());
        serialController.open();
    }

    /**
     * It targets a <code>ComLine</code> and an <code>Heliostat</code> to send and receive the poll bytes from it.
     *
     * @param heliostatId represents a modbus slave address.
     */
    public void poll(int heliostatId) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setPollerFrame(heliostatId));
            Thread.sleep(250);
            checkPollResponse(heliostat);
            comLine.getHeliostats().put(heliostatId, heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds the <object>Heliostat</object> address, the poller bytes and CRC into an array.
     *
     * @param heliostatId represents the number or position of the comLine.
     * @return The poller frame for an specific <code>Heliostat</code>.
     */
    private byte[] setPollerFrame(int heliostatId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(POLL_ARRAY);
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 6));
//        System.out.println(bufferToString(byteBuffer));
        return byteBuffer.array();
    }

    /**
     * Checks received bytes from the <code>SerialController</code> port.
     * <p>
     * If there are any bytes, it uses <method>setHelioState</method> to update the <code>Heliostat</code> attributes.
     * <p>
     * If there aren't any bytes, updates the <code>Heliostat</code> event to com failure.
     *
     * @param heliostat represents the RTU itself.
     */
    private synchronized void checkPollResponse(Heliostat heliostat) {
        if (serialController.getPort().bytesAvailable() < 1) {
//            System.out.println("no poll response");
            heliostat.setEvent(0x10);
            heliostat.setState(1);
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(serialController.receive());
//            System.out.println(bufferToString(byteBuffer));
            heliostat.setAttributes(byteBuffer);
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
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String command(int heliostatId, String command) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setCommandFrame(heliostatId, command));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    /**
     * Adds the <code>Heliostat</code> address, the command bytes and CRC into an array.
     *
     * @param heliostatId RTU slave address.
     * @param command     ASCII representation to switch between different commands.
     * @return modbus frame.
     */
    private byte[] setCommandFrame(int heliostatId, String command) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(11);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(selectCommand(command));
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 9));
        System.out.println("command: " + bufferToString(byteBuffer));
        return byteBuffer.array();
    }

    /**
     * Checks if there are any received bytes from the <code>SerialController</code> port and if not updates the <code>Heliostat</code> event to com failure.
     *
     * @param heliostat
     * @return if the RTU has sent any bytes back.
     */
    private String checkCommandResponse(Heliostat heliostat) {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            System.out.println("no response");
            return "No responde";
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

    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to send a predefined focus.
     *
     * @param heliostatId RTU slave address.
     * @param focus       the focus identifier.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String focus(int heliostatId, int focus) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setFocusFrame(heliostatId, focus));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    /**
     * Adds the <class>Heliostat</class> address, the focus bytes and CRC into a buffer.
     *
     * @param heliostatId the RTU slave address.
     * @param focus       the focus identifier.
     * @return modbus frame.
     */
    private byte[] setFocusFrame(int heliostatId, int focus) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(13);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(new byte[]{16, 0, 0, 0, 2, 4, 0, 102, 0, (byte) focus});
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 11));
        System.out.println("focus: " + bufferToString(byteBuffer));
        return byteBuffer.array();
    }

    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to set a focus new coordinates.
     *
     * @param heliostatId RTU slave address.
     * @param focus       focus identifier.
     * @param x           coordinate.
     * @param y           coordinate.
     * @param z           coordinate.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String newFocus(int heliostatId, int focus, int x, int y, int z) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setNewFocusFrame(heliostatId, focus, x, y, z));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    /**
     * Adds the <class>Heliostat</class> address, the focus and its new values bytes so as CRC into a buffer.
     *
     * @param heliostatId RTU slave address.
     * @param focus       focus identifier.
     * @param x           coordinate.
     * @param y           coordinate.
     * @param z           coordinate.
     * @return modbus frame.
     */
    private byte[] setNewFocusFrame(int heliostatId, int focus, int x, int y, int z) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(25);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(new byte[]{16, 0, 0, 0, 8, 16, 0, 70, 0, (byte) focus, 0, 0}); //loooooooooooooooool
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 23));
        System.out.println("new focus: " + bufferToString(byteBuffer));
        return byteBuffer.array();
    }

    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to set an azimuth value.
     *
     * @param heliostatId RTU slave address.
     * @param azimuth     value.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String setAzimuth(int heliostatId, int azimuth) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setAzElFrame(heliostatId, azimuth));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    /**
     * Adds the <class>Heliostat</class> address, the azimuth or elevation value byte, and CRC into a buffer.
     *
     * @param heliostatId RTU slave address.
     * @param azElValue   value.
     * @return modbus frame.
     */
    private byte[] setAzElFrame(int heliostatId, int azElValue) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(25);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(new byte[]{16, 0, 0, 0, 8, 16, 0, 70, 0, (byte) azElValue, 0, 0}); //loooooooooooooooool
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 23));
        System.out.println("focus: " + bufferToString(byteBuffer));
        return byteBuffer.array();
    }

    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to set an elevation value.
     *
     * @param heliostatId RTU slave address.
     * @param elevation   value.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String setElevation(int heliostatId, int elevation) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setAzElFrame(heliostatId, elevation));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }


    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to get offset values from it.
     *
     * @param heliostatId RTU slave address.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String getOffsetAz(int heliostatId) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(getOffsetAzElFrame(heliostatId));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    /**
     * Adds the <class>Heliostat</class> address, the memory address where offset is stored, and CRC into a buffer.
     *
     * @param heliostatId RTU slave address.
     * @return modbus frame.
     */
    private byte[] getOffsetAzElFrame(int heliostatId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(25);
        byteBuffer.put((byte) heliostatId);
        return byteBuffer.array();
    }

    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to get offset values from it.
     *
     * @param heliostatId RTU slave address.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String getOffsetEl(int heliostatId) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(getOffsetAzElFrame(heliostatId));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to set an offset azimuth value.
     *
     * @param heliostatId RTU slave address.
     * @param offsetAz    azimuth value.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String setOffsetAz(int heliostatId, int offsetAz) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setOffsetAzElFrame(heliostatId, offsetAz));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }

    /**
     * Adds the <class>Heliostat</class> address, the offset bytes and CRC into a buffer.
     *
     * @param heliostatId RTU salve address.
     * @param offset      value.
     * @return modbus frame.
     */
    private byte[] setOffsetAzElFrame(int heliostatId, int offset) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(25);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(new byte[]{16, 0, 0, 0, 8, 16, 0, 70, 0, (byte) offset, 0, 0}); //loooooooooooooooool
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 23));
        System.out.println("focus: " + bufferToString(byteBuffer));
        return byteBuffer.array();
    }

    /**
     * It targets a <code>ComLine</code> and <code>Heliostat</code> to set an offset elevation value.
     *
     * @param heliostatId RTU slave address.
     * @param offsetEl    elevation value.
     * @return if the RTU has sent back any bytes frame as received bytes confirmation.
     */
    public String setOffsetEl(int heliostatId, int offsetEl) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setOffsetAzElFrame(heliostatId, offsetEl));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            serialController.close();
            return e.toString();
        } catch (RuntimeException e) {
            return e.toString();
        }
    }


    /**
     * It targets a <class>Heliostat</class> and an <class>Heliostat</class> to ask 3 bytes from the 218 address which stores hour.
     *
     * @param heliostatId the RTU slave address.
     * @return if the RTU has sent back any hour bytes frame.
     */
    public String getDate(int heliostatId) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setHourGetterFrame(heliostatId));
            Thread.sleep(250);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return returnDate(heliostat);
        } catch (InterruptedException e) {
            return e.getMessage();
        }
    }

    /**
     * Checks if there are any received bytes from the <code>SerialController</code> port and if not updates the <code>Heliostat</code> event to com failure.
     *
     * @param heliostat
     * @return if the RTU has sent any bytes back.
     */
    private String returnDate(Heliostat heliostat) {
        if (serialController.getPort().bytesAvailable() < 1) {
            heliostat.setEvent(0x10);
            System.out.println("command response");
            return "No responde";
        } else {
            ByteBuffer byteBuffer = ByteBuffer.wrap(serialController.receive());
            System.out.println("respuesta" + bufferToString(byteBuffer));
            return bufferToString(byteBuffer);
        }
    }

    /**
     * It targets a <class>Heliostat</class> and an <class>Heliostat</class> to ask 3 bytes from the 218 address which stores hour.
     *
     * @param heliostatId the RTU slave address.
     * @return if the RTU has sent back any hour bytes frame.
     */
    public String getHour(int heliostatId) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            serialController.send(setHourGetterFrame(heliostatId));
            Thread.sleep(250);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            return e.getMessage();
        }
    }

    /**
     * Adds the <class>Heliostat</class> address, the hour bytes and CRC into a buffer.
     *
     * @param heliostatId RTU slave address.
     * @return modbus frame.
     */
    private byte[] setHourGetterFrame(int heliostatId) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.put((byte) heliostatId);
        byteBuffer.put(HOUR_ARRAY);
        byteBuffer.put(CRC.calculate(byteBuffer.array(), 6));
        return byteBuffer.array();
    }

    public String setDate(int heliostatId) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            //            serialController.send(setHourGetterFrame(heliostatId));
            Thread.sleep(500);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            return e.getMessage();
        }
    }

    public String setHour(int heliostatId) {
        try {
            if (!serialController.isOpen())
                openSerialController();
            Heliostat heliostat = comLine.getHeliostats().get(heliostatId);
            //            serialController.send(setHourGetterFrame(heliostatId));
            Thread.sleep(500);
            comLine.getHeliostats().put(heliostatId, heliostat);
            return checkCommandResponse(heliostat);
        } catch (InterruptedException e) {
            return e.getMessage();
        }
    }
}
