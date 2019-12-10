package psa.cesa.cesaom;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.xml.sax.SAXException;
import psa.cesa.cesaom.model.FieldController;
import psa.cesa.cesaom.model.RowsReader;
import psa.cesa.cesaom.model.dao.Heliostat;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    FieldController fieldController;

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

    @RequestMapping(value = "/poll/{rowId}/{heliostatAddress}", method = {RequestMethod.GET}, produces = MediaType.APPLICATION_JSON_VALUE)
    public Heliostat poll(@RequestParam(defaultValue = "1", name = "rowId") int rowId, @RequestParam(defaultValue = "1", name = "heliostatAddress") int heliostatAddress) {
        return fieldController.poll(rowId, heliostatAddress);
    }

    @RequestMapping(value = "/command", method = {RequestMethod.GET})
    public String command(@RequestParam(defaultValue = "1") int rowId, @RequestParam(defaultValue = "1") int heliostatAddress) {
        return String.valueOf(rowId);
    }
}
