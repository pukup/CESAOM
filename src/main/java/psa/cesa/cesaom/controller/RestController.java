package psa.cesa.cesaom.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.ComLine;
import psa.cesa.cesaom.model.XmlLinesReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;

/**
 * It contains the REST server methods for giving HTTP access to clients.
 */
@org.springframework.web.bind.annotation.RestController
public class RestController {

    /**
     * @param comLineMap <class>ComLine</class> objects.
     * @param fieldControllers contains one <class>FieldController</class> for every <<class>ComLine</class>
     * @param timerPollControllers contains one <class>TimerTask</class> for every <code>ComLine</code>.
     * @param timers contains one <class>Timer</class> for every <class>ComLine</class>.
     */
    private Map<Integer, ComLine> comLineMap;
    private Map<Integer, FieldController> fieldControllers;
    private Map<Integer, TimerPollTask> timerPollTasks;
    private Map<Integer, Timer> timers;

    /**
     * Keeps all the <class>ComLine</class> objects from the xml file.
     * <p>
     * Initializes a <class>FieldController</class> objects to gain access to its methods.
     * <p>
     * Initializes a <class>TimerPollTask</class> <class>Map</class> so it can refresh the <class>ComLine</class> status.
     * <p>
     * Initializes a <class>Timer</class> <class>Map</class> so it can schedule the <class>TimerPollTask</class> objects.
     * <p>
     * Calls <method>setTimerPollTask</method>
     */
    public RestController() {
        try {
            comLineMap = XmlLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("fieldComLines.xml"));
            fieldControllers = new HashMap<>();
            timers = new HashMap<>();
            timerPollTasks = new HashMap<>();
            setFieldControllers();
            startTimers();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * It fills the <code>fieldsControllers</code> with its respective objects.
     */
    private void setFieldControllers() {
        for (ComLine comLine : comLineMap.values()) {
            fieldControllers.put(comLine.getId(), new FieldController(comLine));
        }
    }

    /**
     * It fills the <class>timerPollTasks</code> and <code>timers</code> with new objects and schedule the <class>TimerPollTask</class>.
     */
    private void startTimers() {
        for (ComLine comLine : comLineMap.values()) {
            timerPollTasks.put(comLine.getId(), new TimerPollTask(fieldControllers.get(comLine.getId())));
            timers.put(comLine.getId(), new Timer("Timer: " + comLine.getId()));
            timers.get(comLine.getId()).schedule(timerPollTasks.get(comLine.getId()), 0, 5000);
        }
    }

    /**
     * @return All <code>ComLine</code> objects with its <code>Heliostat</code> objects values.
     */
    @GetMapping(value = "/getCache", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ComLine getCache(@RequestParam int comLineId) {
        return timerPollTasks.get(comLineId).getComlineCache();
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, ASCII command, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> modbus id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param command     the ASCII value command.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/command", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String command(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam String command) {
        cancelTimers();
        String s = fieldControllers.get(comLineId).command(heliostatId, command);
        startTimers();
        return s;
    }

    /**
     * It stops every <class>Timer</class> within <code>timers</code>.
     */
    public void cancelTimers() {
        for (Timer timer : timers.values()) {
            timer.cancel();
        }
    }


    //        /**
    //         * @return All the <code>ComLine</code> objects from the xml file.
    //         */
    //        @RequestMapping(value = "/getCache", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    //        public Map<Integer, ComLine> getCache() {
    //            Map<Integer, ComLine> comLines = new HashMap<>();
    //            for (TimerPollTask timerPollController : timerPollControllers.values()) {
    //                comLines.put(timerPollController.getComlineCache().getId(), timerPollController.getComlineCache());
    //            }
    //            return comLines;
    //        }
}
