package psa.cesa.cesaom.model.dao;

import java.util.Map;

/**
 * Dao which represents a communications line
 */
public class Row {

    private int id;
    private String portDir;
    private Map<Integer, Heliostat> heliostats;

    /**
     * Default constructor
     */
    public Row(int id) {
        this.id = id;
    }

    /**
     * @param id         <code>Row</code> identifier
     * @param heliostats HashMap with the <code>Heliostat<code/> objects within the <code>Row<code/>
     */
    public Row(int id, Map<Integer, Heliostat> heliostats) {
        this.id = id;
        this.heliostats = heliostats;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPortDir() {
        return portDir;
    }

    public void setPortDir(String portDir) {
        this.portDir = portDir;
    }

    public Map<Integer, Heliostat> getHeliostats() {
        return heliostats;
    }

    public void setHeliostats(Map<Integer, Heliostat> heliostats) {
        this.heliostats = heliostats;
    }
}