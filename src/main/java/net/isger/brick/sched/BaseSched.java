package net.isger.brick.sched;

import java.util.Map;

import net.isger.brick.Constants;
import net.isger.brick.core.CommandHandler;
import net.isger.brick.core.Handler;
import net.isger.brick.inject.Container;
import net.isger.brick.plugin.PluginCommand;
import net.isger.util.Strings;
import net.isger.util.anno.Alias;
import net.isger.util.anno.Ignore;
import net.isger.util.anno.Ignore.Mode;

public class BaseSched extends AbstractSched {

    public static final String PARAM_DOMAIN = "domain";

    public static final String PARAM_NAME = "name";

    public static final String PARAM_CREATE = "create";

    public static final String PARAM_ACTION = "action";

    public static final String PARAM_REMOVE = "remove";

    /** 容器 */
    @Ignore(mode = Mode.INCLUDE)
    @Alias(Constants.SYSTEM)
    private Container container;

    /** 处理器 */
    private Handler handler;

    @Ignore
    private PluginCommand command;

    @SuppressWarnings("unchecked")
    public void initial() {
        super.initial();
        if (handler == null) {
            handler = new CommandHandler();
        }
        container.inject(handler);
        command = new PluginCommand();
        command.setDomain((String) this.getParameter(PARAM_DOMAIN));
        command.setOperate(Strings.empty(
                (String) this.getParameter(PARAM_ACTION), PARAM_ACTION));
        command.setName((String) this.getParameter(PARAM_NAME));
        Map<String, Object> parameters = (Map<String, Object>) this
                .getParameter("parameters");
        if (parameters != null) {
            command.setParameter(parameters);
        }
    }

    protected PluginCommand getCommand() {
        return command;
    }

    public void create() {
        String operate = (String) this.getParameter(PARAM_CREATE);
        if (Strings.isNotEmpty(operate)) {
            PluginCommand cmd = (PluginCommand) getCommand().clone();
            cmd.setOperate(operate);
            handler.handle(cmd);
        }
    }

    public void action() {
        handler.handle((PluginCommand) getCommand().clone());
    }

    public void remove() {
        String operate = (String) this.getParameter(PARAM_REMOVE);
        if (Strings.isNotEmpty(operate)) {
            PluginCommand cmd = (PluginCommand) getCommand().clone();
            cmd.setOperate(operate);
            handler.handle(cmd);
        }
    }

}
