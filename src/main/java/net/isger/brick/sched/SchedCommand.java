package net.isger.brick.sched;

import net.isger.brick.core.BaseCommand;
import net.isger.brick.core.Command;
import net.isger.brick.core.GateCommand;

public class SchedCommand extends GateCommand {

    public static final String OPERATE_PAUSE = "pause";

    public static final String OPERATE_RESUME = "resume";

    public SchedCommand() {
    }

    public SchedCommand(Command cmd) {
        super(cmd);
    }

    public SchedCommand(boolean hasShell) {
        super(hasShell);
    }

    public static SchedCommand getAction() {
        return cast(BaseCommand.getAction());
    }

    public static SchedCommand cast(BaseCommand cmd) {
        return cmd == null || cmd.getClass() == SchedCommand.class ? (SchedCommand) cmd
                : cmd.infect(new SchedCommand(false));
    }

}
