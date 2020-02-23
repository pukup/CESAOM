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
     * @param fieldController contains methods for polling and commanding <code>Heliostat</code> objects.
     * @param timerPollControllers contains one <code>TimerTask</code> for every <code>ComLine</code>.
     * @param timers contains one <code>Timer</code> for every <code>ComLine</code>.
     */
    private Map<Integer, FieldController> fieldControllers;
    private Map<Integer, TimerPollTask> timerPollTasks;
    private Map<Integer, Timer> timers;

    /**
     * Initializes a <code>FieldController</code> object to gain access to its methods.
     * <p>
     * Initializes a <code>TimerPollController</code> Map with its size equal to the number of <code>ComLines</code>.
     * <p>
     * Initializes a <code>Timer</code> Map with <code>TimerPollController</code> objects within it.
     * <p>
     * Calls <method>setTimerPollControllers</method>
     */
    public RestController() {
        try {
            fieldControllers = new HashMap<>();
            timers = new HashMap<>();
            timerPollTasks = new HashMap<>();
            setTimerPollControllers();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /**
     * It fills the Maps with its respective objects and schedule the <code>TimerPollControllers</code>.
     */
    private void setTimerPollControllers() throws IOException, SAXException, ParserConfigurationException {
        Map<Integer, ComLine> comLineMap = XmlLinesReader.getXmlRows(getClass().getClassLoader().getResourceAsStream("fieldComLines.xml"));
        for (ComLine comLine : comLineMap.values()) {
            timerPollTasks.put(comLine.getId(), new TimerPollTask(comLine));
            timers.put(comLine.getId(), new Timer(String.valueOf("Timer: " + comLine.getId())));
            timers.get(comLine.getId()).scheduleAtFixedRate(timerPollTasks.get(comLine.getId()), 0, 50000);
        }
    }

    /**
     * @return All <code>ComLine</code> objects and all its <code>Heliostat</code> objects values
     */
    @GetMapping(value = "/getCache", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ComLine getCache(@RequestParam int comLineId) {
        return timerPollTasks.get(comLineId).getComlineCache();
    }

    /**
     * It sends a command byte frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, ASCII command, and CRC.
     *
     * @param comLineId   the <code>ComLine</code>> modbus id.
     * @param heliostatId the <code>Heliostat</code> modbus id.
     * @param command     the ASCII value of the command.
     * @return
     */
    @GetMapping(value = "/command")
    @ResponseBody
    public String command(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam String command) {
        pauseTimers();
        String s = fieldControllers.get(comLineId).command(heliostatId, command);
        restartTimers();
        return s;
    }

    public void pauseTimers() {
        for (Timer timer : timers.values()) {
            timer.cancel();
        }
    }

    private void restartTimers() {
    }

    //        /**
    //         * @return All the <code>ComLine</code> objects from the xml file.
    //         */
    //        @RequestMapping(value = "/getCache", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    //        public Map<Integer, ComLine> getCache() {
    //            Map<Integer, ComLine> comLines = new HashMap<>();
    //            for (TimerPollController timerPollController : timerPollControllers.values()) {
    //                comLines.put(timerPollController.getComlineCache().getId(), timerPollController.getComlineCache());
    //            }
    //            return comLines;
    //        }
}
