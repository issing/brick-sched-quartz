package net.isger.brick.schedule;

import java.util.Date;

import net.isger.brick.core.BaseGate;

public abstract class AbstractSchedule extends BaseGate implements Schedule {

    private String group;

    private Date effective;

    private Date deadline;

    private String interval;

    private int delay;

    public String getGroup() {
        return group;
    }

    public Date getEffective() {
        return effective;
    }

    public Date getDeadline() {
        return deadline;
    }

    public int getDelay() {
        return delay;
    }

    public String getInterval() {
        return interval;
    }

}
