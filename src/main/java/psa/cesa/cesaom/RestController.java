package psa.cesa.cesaom;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.FieldController;
import psa.cesa.cesaom.model.RowsReader;
import psa.cesa.cesaom.model.dao.Heliostat;
import psa.cesa.cesaom.model.dao.Row;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * It contains the api methods to act as an interface between the clients and the application itself
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    /**
     * @param fieldController contains methods for poll and command <code>Heliostat</code>
     */
    private FieldController fieldController;

    public RestController() {
        try {
            fieldController = new FieldController(RowsReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("fieldRows.xml")));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param rowId
     * @param heliostatAddress
     * @return
     */
    @RequestMapping(value = "/poll/{rowId}/{heliostatAddress}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Heliostat poll(@RequestParam(defaultValue = "1", name = "rowId") int rowId, @RequestParam(defaultValue = "1", name = "heliostatAddress") int heliostatAddress) {
        Heliostat heliostat = null;
        try {
            heliostat = fieldController.poll(rowId, heliostatAddress);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return heliostat;
    }

    @RequestMapping(value = "/pollField", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Heliostat> pollField() {
        List<Heliostat> heliostats = new ArrayList<>();
        try {
            for (Row row : fieldController.getRows().values()) {
                for (Heliostat heliostat : row.getHeliostats().values()) {
                    Heliostat heliostat1 = fieldController.poll(row.getId(), heliostat.getAddress());
                    heliostats.add(heliostat1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return heliostats;
    }

    @RequestMapping(value = "/command", method = {RequestMethod.GET})
    public String command(@RequestParam(defaultValue = "1") int rowId, @RequestParam(defaultValue = "1") int heliostatAddress, @RequestParam(defaultValue = "a") String command) {
        String response = "KO";
        try {
            response = (fieldController.command(rowId, heliostatAddress, command)) ? "OK" : "KO";
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }
}
