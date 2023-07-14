package net.isger.brick.schedule;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import net.isger.brick.Constants;
import net.isger.brick.core.Context;
import net.isger.brick.core.Gate;
import net.isger.brick.core.GateCommand;
import net.isger.brick.core.GateModule;
import net.isger.brick.plugin.PluginCommand;
import net.isger.util.Asserts;
import net.isger.util.Dates;
import net.isger.util.Strings;

/**
 * 调度模块
 * 
 * @author issing
 * 
 */
public class ScheduleModule extends GateModule {

    private static final String SCHEDULE = "schedule";

    private static final String META_SCHEDULE = "meta.schedule";

    private static final String META_CONTEXT = "meta.context";

    private Scheduler scheduler;

    private Map<Schedule, JobKey> jobKeys;

    public ScheduleModule() {
        jobKeys = new HashMap<Schedule, JobKey>();
    }

    public Class<? extends Gate> getTargetClass() {
        return Schedule.class;
    }

    @SuppressWarnings("unchecked")
    public Class<? extends Gate> getImplementClass() {
        Class<? extends Gate> implClass = (Class<? extends Gate>) getImplementClass(SCHEDULE, null);
        if (implClass == null) {
            implClass = super.getImplementClass();
        }
        return implClass;
    }

    public Class<? extends Gate> getBaseClass() {
        return BaseSchedule.class;
    }

    public void initial() {
        super.initial();
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            for (Entry<String, Gate> entry : getGates().entrySet()) {
                createJob(entry.getKey(), (Schedule) entry.getValue());
            }
            scheduler.start();
        } catch (Exception e) {
            throw Asserts.state("Failure to create scheduler", e);
        }
    }

    @SuppressWarnings("unchecked")
    public void create(GateCommand cmd) {
        super.create(cmd);
        Map<String, Schedule> scheds = (Map<String, Schedule>) cmd.getResult();
        for (Entry<String, Schedule> entry : scheds.entrySet()) {
            try {
                createJob(entry.getKey(), entry.getValue());
            } catch (Exception e) {
                throw Asserts.state("Failure to create schedule", e);
            }
        }
    }

    public void pause() {
        GateCommand cmd = GateCommand.getAction();
        Schedule sched;
        for (Entry<String, Object> entry : cmd.getParameter().entrySet()) {
            if ((sched = (Schedule) getGate(entry.getKey())) != null) {
                try {
                    JobKey key = jobKeys.get(sched);
                    if (key != null) {
                        synchronized (key) {
                            scheduler.pauseJob(key);
                        }
                    }
                } catch (Exception e) {
                    throw Asserts.state("Failure to pause schedule", e);
                }
            }
        }
    }

    public void resume(GateCommand cmd) {
        Schedule sched;
        for (Entry<String, Object> entry : cmd.getParameter().entrySet()) {
            if ((sched = (Schedule) getGate(entry.getKey())) != null) {
                try {
                    JobKey key = jobKeys.get(sched);
                    if (key != null) {
                        synchronized (key) {
                            scheduler.resumeJob(key);
                        }
                    }
                } catch (Exception e) {
                    throw Asserts.state("Failure to pause schedule", e);
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void remove(GateCommand cmd) {
        super.remove(cmd);
        Map<String, Schedule> scheds = (Map<String, Schedule>) cmd.getResult();
        for (Schedule sched : scheds.values()) {
            try {
                JobKey key = jobKeys.get(sched);
                if (key != null) {
                    synchronized (key) {
                        scheduler.deleteJob(key);
                    }
                    jobKeys.remove(sched);
                }
            } catch (Exception e) {
                throw Asserts.state("Failure to remove schedule", e);
            }
        }
    }

    private void createJob(String name, Schedule sched) throws Exception {
        if (sched instanceof BaseSchedule) {
            PluginCommand cmd = ((BaseSchedule) sched).command;
            if (cmd != null && Strings.isEmpty(cmd.getDomain())) {
                cmd.setDomain(name);
            }
        }
        String group = Strings.empty(sched.getGroup(), Constants.DEFAULT);
        TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
        triggerBuilder.withIdentity(name, group);
        /* 触发生命周期 */
        triggerBuilder.startAt(Dates.getDate(sched.getEffective(), sched.getDelay()));
        Date deadline = sched.getDeadline();
        if (deadline != null) {
            triggerBuilder.endAt(deadline);
        }
        /* 触发执行频率 */
        String interval = sched.getInterval();
        if (Strings.isNotEmpty(interval)) {
            triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(interval));
        }
        /* 任务详情参数 */
        JobBuilder jobBuilder = JobBuilder.newJob(BaseJob.class);
        jobBuilder.withIdentity(name, group);
        JobDetail detail = jobBuilder.build();
        JobDataMap data = detail.getJobDataMap();
        data.put(META_SCHEDULE, sched);
        data.put(META_CONTEXT, Context.getAction()); // 设定定时任务上下文
        scheduler.scheduleJob(detail, triggerBuilder.build());
        jobKeys.put(sched, detail.getKey());
    }

    public void destroy() {
        try {
            scheduler.shutdown(true);
        } catch (Exception e) {
        }
        super.destroy();
    }

    public static final class BaseJob implements Job {

        public void execute(JobExecutionContext context) throws JobExecutionException {
            JobDataMap data = context.getJobDetail().getJobDataMap();
            Context.setAction((Context) data.get(META_CONTEXT));
            ((Schedule) data.get(META_SCHEDULE)).action();
        }

    }
}