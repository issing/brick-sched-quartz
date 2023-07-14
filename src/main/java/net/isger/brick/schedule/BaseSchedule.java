package net.isger.brick.schedule;

import java.util.Map;

import net.isger.brick.Constants;
import net.isger.brick.core.CommandHandler;
import net.isger.brick.core.Handler;
import net.isger.brick.inject.Container;
import net.isger.brick.plugin.PluginCommand;
import net.isger.util.Callable;
import net.isger.util.Strings;
import net.isger.util.anno.Alias;
import net.isger.util.anno.Ignore;
import net.isger.util.anno.Ignore.Mode;

public class BaseSchedule extends AbstractSchedule {

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
    PluginCommand command;

    @Ignore
    private Callable<?> action;

    @SuppressWarnings("unchecked")
    public void initial() {
        super.initial();
        if (handler == null) {
            handler = new CommandHandler();
        }
        container.inject(handler);
        Object action = getParameter(PARAM_ACTION);
        if (!(action instanceof Callable)) {
            command = new PluginCommand();
            command.setDomain((String) getParameter(PARAM_DOMAIN));
            command.setOperate(Strings.empty((String) action, PARAM_ACTION));
            command.setName((String) this.getParameter(PARAM_NAME));
            Map<String, Object> parameters = (Map<String, Object>) this.getParameter("parameters");
            if (parameters != null) {
                command.setParameter(parameters);
            }
            action = new Callable.Runnable() {
                public void run(Object... args) {
                    if (args != null && args.length > 0) {
                        String operate = (String) args[0];
                        if (Strings.isNotEmpty(operate)) {
                            PluginCommand cmd = command.clone();
                            cmd.setOperate(operate);
                            handler.handle(cmd);
                        }
                    } else {
                        handler.handle(command.clone());
                    }
                }
            };
        }
        this.action = (Callable<?>) action;
    }

    public void action() {
        action.call();
    }

}
