package psa.cesa.cesaom.controller;

import psa.cesa.cesaom.model.ComLine;
import psa.cesa.cesaom.model.Heliostat;

import java.util.TimerTask;

public class TimerPollTask extends TimerTask {

    /**
     * @param comLineId the identification of a <code>ComLine</code> .
     * @param fieldController for using its methods so it can send the proper frames.
     * @param cache it keeps the values before the first poll or from the last poll.
     */
    private FieldController fieldController;
    private ComLine comLine;
    private ComLine cache;

    public TimerPollTask(FieldController fieldController) {
        this.fieldController = fieldController;
        this.comLine = fieldController.getComLine();
        this.cache = comLine;
    }

    /**
     * @return a <code>ComLine<code> object keeping the values from the las poll.
     */
    public synchronized ComLine getComlineCache() {
        return cache;
    }

    /**
     * It calls <method>pollComLine</method>.
     */
    @Override
    public void run() {
        pollComLine();
    }

    /**
     * It polls the <code>ComLine</code> to get the updated values from the RTU.
     *
     * @return All <code>ComLine</code> objects and all its <code>Heliostat</code> objects values
     */
    public void pollComLine() {
        for (Heliostat heliostat : comLine.getHeliostats().values()) {
            fieldController.poll(heliostat.getId());
        }
        cache = comLine;
    }
}
