package psa.cesa.cesaom.model;

import java.nio.ByteBuffer;

/**
 * DAO which represents a physical heliostat.
 */
public class Heliostat {
    /**
     * @param id Represents the modbus slave address.
     * @param state Static and dynamic positions representation.
     * @param event Operation, security, communications and such events.
     * @param diagnosisAz Axis diagnosis.
     * @param diagnosisEl Axis diagnosis.
     * @param positionAz Actual azimuth position.
     * @param positionEL Actual elevation position.
     * @param setPointAZ Azimuth set point.
     * @param setPointEL Elevation set point.
     */

    private int id;
    private int state;
    private int event;
    private int diagnosisAZ, diagnosisEL;
    private int positionAZ, positionEL;
    private int setPointAZ, setPointEL;

    public Heliostat(int id) {
        this.id = id;
        event = 16;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getEvent() {
        return event;
    }

    public void setEvent(int event) {
        this.event = event;
    }

    public int getDiagnosysAZ() {
        return diagnosisAZ;
    }

    public void setDiagnosysAZ(int diagnosisAZ) {
        this.diagnosisAZ = diagnosisAZ;
    }

    public int getDiagnosysEL() {
        return diagnosisEL;
    }

    public void setDiagnosysEL(int diagnosisEL) {
        this.diagnosisEL = diagnosisEL;
    }

    public int getPositionAZ() {
        return positionAZ;
    }

    public void setPositionAZ(int positionAZ) {
        this.positionAZ = positionAZ;
    }

    public int getPositionEL() {
        return positionEL;
    }

    public void setPositionEL(int positionEL) {
        this.positionEL = positionEL;
    }

    public int getSetPointAZ() {
        return setPointAZ;
    }

    public void setSetPointAZ(int setPointAZ) {
        this.setPointAZ = setPointAZ;
    }

    public int getSetPointEL() {
        return setPointEL;
    }

    public void setSetPointEL(int setPointEL) {
        this.setPointEL = setPointEL;
    }

    /**
     * Sets the <code>Heliostat</code> attributes by separating the modbus frame into individual bytes.
     *
     * @param receivedBuffer bytes frame from the modbus RTU.
     */
    public void setAttributes(ByteBuffer receivedBuffer) {
        for (int i = 0; i < receivedBuffer.array().length; i++) {
            Byte b = receivedBuffer.get(i);
            switch (i) {
                case 4:
                    setState(b);
                    break;
                case 6:
                    setEvent(b);
                    break;
                case 8:
                    setDiagnosysAZ(b);
                    break;
                case 10:
                    setDiagnosysEL(b);
                    break;
                case 12:
                    setPositionAZ(b);
                    break;
                case 14:
                    setPositionEL(b);
                    break;
                case 16:
                    setSetPointAZ(b);
                    break;
                case 18:
                    setSetPointEL(b);
                    break;
            }
        }
    }
}