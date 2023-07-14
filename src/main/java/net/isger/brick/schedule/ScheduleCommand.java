package net.isger.brick.schedule;

import net.isger.brick.core.BaseCommand;
import net.isger.brick.core.Command;
import net.isger.brick.core.GateCommand;

public class ScheduleCommand extends GateCommand {

    public static final String OPERATE_PAUSE = "pause";

    public static final String OPERATE_RESUME = "resume";

    public ScheduleCommand() {
    }

    public ScheduleCommand(Command cmd) {
        super(cmd);
    }

    public ScheduleCommand(boolean hasShell) {
        super(hasShell);
    }

    public static ScheduleCommand getAction() {
        return cast(BaseCommand.getAction());
    }

    public static ScheduleCommand cast(BaseCommand cmd) {
        return cmd == null || cmd.getClass() == ScheduleCommand.class ? (ScheduleCommand) cmd : cmd.infect(new ScheduleCommand(false));
    }

}
