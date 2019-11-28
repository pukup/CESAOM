package psa.cesa.cesaom.model.dao;

import java.util.List;

/**
 * Dao which represents a communications line
 */
public class Row {

    private String id;
    private List<Heliostat> heliostats;

    public Row() {
    }

    /**
     * @param id
     * @param heliostats list with the <code>Heliostat<code/> objects within the <code>Row<code/>
     */
    public Row(String id, List<Heliostat> heliostats) {
        this.id = id;
        this.heliostats = heliostats;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Heliostat> getHeliostats() {
        return heliostats;
    }

    public void setHeliostats(List<Heliostat> heliostats) {
        this.heliostats = heliostats;
    }
}
