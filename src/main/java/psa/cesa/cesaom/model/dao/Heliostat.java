package psa.cesa.cesaom.model.dao;

/**
 * DAO which represents a physical heliostat
 */
public class Heliostat {
    /**
     * @param rowID it keeps it's <code>Row</code> number
     * @param address Represents the modbus slave address
     * @param state static and dynamic positions representation
     * @param diagnosys
     * @param positionAz
     * @param positionEL
     * @param setPointAZ
     * @param setPointEL
     */
    private int rowID;
    private int address;
    private int state;
    private int event;
    private int diagnosys;
    private int positionAZ, positionEL;
    private int setPointAZ, setPointEL;

    public Heliostat(int address) {
        this.address = address;
    }

    public int getRowID() {
        return rowID;
    }

    public void setRowID(int rowID) {
        this.rowID = rowID;
    }

    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
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

    public int getDiagnosys() {
        return diagnosys;
    }

    public void setDiagnosys(int diagnosys) {
        this.diagnosys = diagnosys;
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
}
