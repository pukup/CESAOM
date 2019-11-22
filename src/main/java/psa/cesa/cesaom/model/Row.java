package psa.cesa.cesaom.model;

import java.util.List;

public class Row {

    private String id;
    private List<Integer> addresses;

    public Row() {
    }

    public Row(String id, List<Integer> addresses) {
        this.id = id;
        this.addresses = addresses;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Integer> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Integer> addresses) {
        this.addresses = addresses;
    }
}
