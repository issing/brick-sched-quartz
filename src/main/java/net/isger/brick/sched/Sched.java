package net.isger.brick.sched;

import java.util.Date;

import net.isger.brick.core.Gate;

public interface Sched extends Gate {

    public Date getEffective();

    public Date getDeadline();

    public int getDelay();

    public String getInterval();

    public String getGroup();

    public void create();

    public void action();

    public void remove();

}
