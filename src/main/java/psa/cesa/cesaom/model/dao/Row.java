
package psa.cesa.cesaom.model.dao;

import java.util.Map;

/**
 * DAO which represents a communications line
 */
public class Row {
    /**
     * @param id represents the number or position of the row within the field
     * @param portDir is the OS serial port direction
     * @param heliostats HashMap which contains <code>Heliostat</code> objects within a <code>Row</code>
     */
    private int id;
    private String portDir;
    private Map<Integer, Heliostat> heliostats;

    public Row(int id) {
        this.id = id;
    }

    public Row(int id, String portDir, Map<Integer, Heliostat> heliostats) {
        this.id = id;
        this.portDir = portDir;
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