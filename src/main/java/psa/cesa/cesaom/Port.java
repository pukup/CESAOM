package psa.cesa.cesaom;

import java.util.List;

public class Port {

    private int id;
    private List<Integer> addresses;

    public Port() {
    }

    public Port(int id, List<Integer> addresses) {
        this.id = id;
        this.addresses = addresses;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Integer> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Integer> addresses) {
        this.addresses = addresses;
    }
}
