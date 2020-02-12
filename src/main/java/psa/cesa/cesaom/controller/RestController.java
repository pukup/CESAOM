package psa.cesa.cesaom.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.XmlComLinesReader;
import psa.cesa.cesaom.model.dao.ComLine;
import psa.cesa.cesaom.model.dao.Heliostat;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

/**
 * It contains the REST server methods for giving HTTP access to clients.
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    /**
     * @param fieldController contains methods for polling and commanding <code>Heliostat</code> objects.
     */
    private FieldController fieldController;
    private TimerPollController[] timerPollControllers;
    private Timer timer;

    /**
     * Initializes a <code>FieldController</code> object to gain access to its methods.
     * <p>
     * Initializes a <code>TimerPollController</code> array with its size equal to the number of <code>ComLines</code>.
     * <p>
     * Initializes a <code>Timer</code> and launches scheduled <code>TimerPollController</code> objects within the array.
     */
    public RestController() {
        try {
            fieldController = new FieldController(XmlComLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("fieldComLines.xml")));
            timerPollControllers = new TimerPollController[fieldController.getComLines().size()];
            timer = new Timer("pollTimer");
            pollStart();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private void pollStart() {
        int i = 0;
        for (ComLine comLine : fieldController.getComLines().values()) {
            timerPollControllers[i] = new TimerPollController(fieldController, comLine.getId());
            timer.schedule(timerPollControllers[i], new Date(), 10000);
            i++;
        }
    }

    /**
     * @return All the <code>ComLine</code> objects from the xml file.
     */
    @RequestMapping(value = "/getComLines", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ComLine> getComLines() {
        List<ComLine> comLines = new ArrayList<>();
        for (ComLine comLine : fieldController.getComLines().values()) {
            comLines.add(comLine);
        }
        return comLines;
    }

    /**
     * It sends a polling byte frame with modbus function code 03, <code>ComLine</code> id, <code>Heliostat</code> id, and CRC to get a response from any RTU.
     *
     * @param comLineId   the <code>ComLine</code>> modbus id.
     * @param heliostatId the <code>Heliostat</code> modbus id.
     * @return
     */
    @RequestMapping(value = "/pollOne/{comLineId}/{heliostatId}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Heliostat pollOne(@RequestParam int comLineId, @RequestParam int heliostatId) {
        Heliostat heliostat = null;
        heliostat = fieldController.pollOne(comLineId, heliostatId);
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


    //        /**
    //         * @return All <code>ComLine</code> objects and all its <code>Heliostat</code> objects values
    //         */
    //        @RequestMapping(value = "/pollField", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    //        public List<ComLine> pollField() {
    //            List<ComLine> comLines = new ArrayList<>();
    //            try {
    //                for (ComLine comLine : fieldController.getComLines().values()) {
    //                    for (Heliostat heliostat : comLine.getHeliostats().values()) {
    //                        Heliostat heliostat1 = fieldController.poll(comLine.getId(), heliostat.getId());
    //                        comLine.getHeliostats().put(heliostat1.getId(), heliostat1);
    //                    }
    //                    comLines.add(comLine);
    //                }
    //            } catch (Exception e) {
    //                e.printStackTrace();
    //            }
    //            return comLines;
    //        }
}
