package net.isger.brick.schedule;

import java.util.Date;

import net.isger.brick.core.Gate;

public interface Schedule extends Gate {

    public Date getEffective();

    public Date getDeadline();

    public int getDelay();

    public String getInterval();

    public String getGroup();

    public void action();

}
