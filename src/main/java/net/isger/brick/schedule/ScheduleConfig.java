package net.isger.brick.schedule;

import java.util.HashMap;
import java.util.Map;

import net.isger.util.Callable;
import net.isger.util.Strings;

public class ScheduleConfig extends HashMap<String, Object> {

    private static final long serialVersionUID = 2904176780190370956L;

    private Map<String, Object> parameters;

    public ScheduleConfig() {
        this.put("parameters", this.parameters = new HashMap<String, Object>());
    }

    public String getInterval() {
        return Strings.empty(this.get("interval"));
    }

    public void setInterval(String interval) {
        this.put("interval", interval);
    }

    public void setAction(Callable<?> action) {
        this.setParameter("action", action);
    }

    public void setParameter(String key, Object value) {
        this.parameters.put(key, value);
    }

    public Object getParameter(String key) {
        return this.parameters.get(key);
    }

}
