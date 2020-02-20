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
    private int comLineId;
    private FieldController fieldController;
    private ComLine cache;

    public TimerPollTask(int comLineId, FieldController fieldController) {
        this.comLineId = comLineId;
        this.fieldController = fieldController;
        this.cache = fieldController.getComLines().get(comLineId);
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
        pollComLine();          //DELAYED ITERATIONS??
    }

    /**
     * It polls the <code>ComLine</code> to get the updated values from the RTU.
     *
     * @return All <code>ComLine</code> objects and all its <code>Heliostat</code> objects values
     */
    public void pollComLine() {
        ComLine comLine = fieldController.getComLines().get(comLineId);
        for (Heliostat heliostat : comLine.getHeliostats().values()) {
            fieldController.pollOne(comLine.getId(), heliostat.getId());
        }
        cache = comLine;
    }
}
