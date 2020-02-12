package psa.cesa.cesaom.controller;

import psa.cesa.cesaom.model.dao.ComLine;
import psa.cesa.cesaom.model.dao.Heliostat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TimerTask;

public class TimerPollController extends TimerTask {

    private FieldController fieldController;
    private int comLineId;
    private ComLine cache;

    public TimerPollController(FieldController fieldController, int comLineId) {
        this.fieldController = fieldController;
        this.comLineId = comLineId;
    }

    public synchronized ComLine getComlineCache() {
        return cache;
    }

    @Override
    public void run() {
        cache = pollComLine();
    }

    /**
     * @return All <code>ComLine</code> objects and all its <code>Heliostat</code> objects values
     */
    public ComLine pollComLine() {
        ComLine comLine = fieldController.getComLines().get(comLineId);
        for (Heliostat heliostat : comLine.getHeliostats().values()) {
            heliostat = fieldController.pollOne(comLine.getId(), heliostat.getId());
            comLine.getHeliostats().put(heliostat.getId(), heliostat);
        }
        return comLine;
    }
}
