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
     * It fills the <code>timerPollTasks</code> Map and <code>timers</code> with new objects and schedule the <class>TimerPollTask</class>.
     */
    private void startTimers() {
        for (ComLine comLine : comLineMap.values()) {
            fieldControllers.put(comLine.getId(), new FieldController(comLine));
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
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, ASCII command and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param command     the ASCII value command.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/command", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String command(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam String command) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).command(heliostatId, command);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, an int representing a focus and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param focus       identifier.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/focus", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String focus(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam int focus) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).focus(heliostatId, focus);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, four int representing a focus with its new coordinates, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param focus       identifier.
     * @param x           value.
     * @param y           value.
     * @param z           value.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/newFocus", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String newFocus(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam int focus, @RequestParam int x, @RequestParam int y, @RequestParam int z) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).newFocus(heliostatId, focus, x, y, z);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, an int representing an azimuth value, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param azimuth     value.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/setAzimuth", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setAzimuth(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam int azimuth) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).setAzimuth(heliostatId, azimuth);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, an int representing an elevation value, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param elevation   value.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/setElevation", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setElevation(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam int elevation) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).setElevation(heliostatId, elevation);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, the memory address where offset is stored, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @return bytes frame as the RTU response.
     */
    @GetMapping(value = "/getOffsetAz", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getOffsetAz(@RequestParam int comLineId, @RequestParam int heliostatId) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).getOffsetAz(heliostatId);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, the memory address where offset is stored, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @return bytes frame as the RTU response.
     */
    @GetMapping(value = "/getOffsetEl", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getOffsetEl(@RequestParam int comLineId, @RequestParam int heliostatId) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).getOffsetEl(heliostatId);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, an int representing an azimuth offset value, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param offsetAz    offset azimuth value.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/setOffsetAz", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setOffsetAz(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam int offsetAz) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).setOffsetAz(heliostatId, offsetAz);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, an int representing an elevation offset value, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @param offsetEl    offset elevation value.
     * @return bytes frame if the RTU answers or a "No response" <class>String</class> if not.
     */
    @GetMapping(value = "/setOffsetEl", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setOffsetEl(@RequestParam int comLineId, @RequestParam int heliostatId, @RequestParam int offsetEl) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).setOffsetEl(heliostatId, offsetEl);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, the memory address where date is stored, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @return bytes frame as the RTU response.
     */
    @GetMapping(value = "/getDate", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getDate(@RequestParam int comLineId, @RequestParam int heliostatId) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).getDate(heliostatId);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, the memory address where hour is stored, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @return bytes frame as the RTU response.
     */
    @GetMapping(value = "/getHour", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String getHour(@RequestParam int comLineId, @RequestParam int heliostatId) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).getHour(heliostatId);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, the date values, its position in memory, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @return bytes frame as the RTU response.
     */
    @GetMapping(value = "/setDate", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setDate(@RequestParam int comLineId, @RequestParam int heliostatId) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).setDate(heliostatId);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }

    /**
     * It sends a command bytes frame with modbus function code 16, <code>ComLine</code> id, <code>Heliostat</code> id, the hour values, its position in memory, and CRC.
     *
     * @param comLineId   the <class>ComLine</class> id.
     * @param heliostatId the <class>Heliostat</class> modbus id.
     * @return bytes frame as the RTU response.
     */
    @GetMapping(value = "/setHour", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String setHour(@RequestParam int comLineId, @RequestParam int heliostatId) {
        timerPollTasks.get(comLineId).pause(true);
        String s = fieldControllers.get(comLineId).setHour(heliostatId);
        timerPollTasks.get(comLineId).pause(false);
        return s;
    }
}
