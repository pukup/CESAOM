package psa.cesa.cesaom;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.ComLinesReader;
import psa.cesa.cesaom.model.FieldController;
import psa.cesa.cesaom.model.dao.Heliostat;
import psa.cesa.cesaom.model.dao.ComLine;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * It contains the REST server methods for giving HTTP access to clients.
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    /**
     * @param fieldController contains methods for poll and command <code>Heliostat</code>
     */
    private FieldController fieldController;

    /**
     * Initializes a <code>FieldsController</code> object for getting access to its methods.
     */
    public RestController() {
        try {
            fieldController = new FieldController(ComLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("fieldComLines.xml")));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return A List including all the rows from the xml file
     */
    @RequestMapping(value = "/loadField", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComLine> loadField() {
        List<ComLine> comLines = new ArrayList<>();
        for (ComLine comLine : fieldController.getComLines().values()) {
            comLines.add(comLine);
        }
        return comLines;
    }

    /**
     * @return A list with all <code>ComLine</code> objects and all its <code>Heliostat</code> objects values
     */
    @RequestMapping(value = "/pollField", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComLine> pollField() {
        List<ComLine> comLines = new ArrayList<>();
        try {
            for (ComLine comLine : fieldController.getComLines().values()) {
                for (Heliostat heliostat : comLine.getHeliostats().values()) {
                    Heliostat heliostat1 = fieldController.poll(comLine.getId(), heliostat.getAddress());
                    comLine.getHeliostats().put(heliostat1.getAddress(), heliostat1);
                }
                comLines.add(comLine);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return comLines;
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

    /**
     * @param rowId
     * @param heliostatAddress
     * @param command
     * @return
     */
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
