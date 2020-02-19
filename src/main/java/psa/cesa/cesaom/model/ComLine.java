package psa.cesa.cesaom.model;

import java.util.Map;

/**
 * DAO which represents a communications line
 */
public class ComLine {
    /**
     * @param id represents the number assigned to a communications line.
     * @param portDir is the OS serial port direction.
     * @param heliostats HashMap which contains <code>Heliostat</code> objects within a <code>ComLine</code>.
     */
    private int id;
    private String portDir;
    private Map<Integer, Heliostat> heliostats;

    public ComLine(int id) {
        this.id = id;
    }

    public ComLine(int id, String portDir, Map<Integer, Heliostat> heliostats) {
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