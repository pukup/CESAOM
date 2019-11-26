package psa.cesa.cesaom.model.dao;

import java.util.List;

/**
 * Dao which represents a communications line
 */
public class Row {

    private String id;
    private List<Integer> addresses;

    public Row() {
    }

    /**
     * @param id
     * @param addresses list with the modbus addresses
     */
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
