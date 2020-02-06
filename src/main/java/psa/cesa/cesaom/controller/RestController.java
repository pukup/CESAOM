package psa.cesa.cesaom.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.XmlComLinesReader;
import psa.cesa.cesaom.controller.FieldController;
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
     * @param fieldController contains methods for polling and commanding <code>Heliostat</code> objects.
     */
    private FieldController fieldController;

    /**
     * Initializes a <code>FieldController</code> object for getting access to its methods.
     */
    public RestController() {
        try {
            fieldController = new FieldController(XmlComLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("fieldComLines.xml")));
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * It sends a polling byte frame with modbus function code 03, <code>ComLine</code> id, <code>Heliostat</code> id, and CRC to get a response from any RTU.
     *
     * @param comLineId   the <code>ComLine</code>> modbus id.
     * @param heliostatId the <code>Heliostat</code> modbus id.
     * @return
     */
    @RequestMapping(value = "/poll/{comLineId}/{heliostatId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Heliostat poll(@RequestParam int comLineId, @RequestParam int heliostatId) {
        Heliostat heliostat = null;
        try {
            heliostat = fieldController.poll(comLineId, heliostatId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return heliostat;
    }

    /**
     * It sends a command byte frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, ASCII command, and CRC.
     *
     * @param comLineId   the <code>ComLine</code>> modbus id.
     * @param heliostatId the <code>Heliostat</code> modbus id.
     * @param command     the ASCII value of the command.
     * @return
     */
    @RequestMapping(value = "/command", method = {RequestMethod.GET})
    public boolean command(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam String command) {
        try {
            return fieldController.command(comLineId, heliostatId, command);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    //    /**
    //     * @return All the <code>ComLine</code> objects from the xml file.
    //     */
    //    @RequestMapping(value = "/loadField", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    //    public List<ComLine> loadField() {
    //        List<ComLine> comLines = new ArrayList<>();
    //        for (ComLine comLine : fieldController.getComLines().values()) {
    //            comLines.add(comLine);
    //        }
    //        return comLines;
    //    }

    //    /**
    //     * @return All <code>ComLine</code> objects and all its <code>Heliostat</code> objects values
    //     */
    //    @RequestMapping(value = "/pollField", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    //    public List<ComLine> pollField() {
    //        List<ComLine> comLines = new ArrayList<>();
    //        try {
    //            for (ComLine comLine : fieldController.getComLines().values()) {
    //                for (Heliostat heliostat : comLine.getHeliostats().values()) {
    //                    Heliostat heliostat1 = fieldController.poll(comLine.getId(), heliostat.getId());
    //                    comLine.getHeliostats().put(heliostat1.getId(), heliostat1);
    //                }
    //                comLines.add(comLine);
    //            }
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //        return comLines;
    //    }
}
