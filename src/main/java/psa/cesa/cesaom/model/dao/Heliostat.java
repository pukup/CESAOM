package psa.cesa.cesaom.model.dao;

/**
 * DAO which     represents the physical heliostat state
 */
public class Heliostat {

    private Row row;
    private int address;
    private int state;
    private int event;
    private int diagnosys;

    public Heliostat(int address) {
        this.address = address;
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
}
