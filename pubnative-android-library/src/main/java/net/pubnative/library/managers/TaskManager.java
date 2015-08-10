package net.pubnative.library.managers;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.pubnative.library.managers.task.TaskItem;
import net.pubnative.library.managers.task.TaskItem.TaskItemListener;

public class TaskManager implements TaskItemListener
{
    private static TaskManager       instance;
    private static final int         LOOPER_INTERVAL = 250;
    private ScheduledExecutorService executorService = null;
    private ArrayList<TaskItem>      tasks;

    private static TaskManager getInstance()
    {
        if (TaskManager.instance == null)
        {
            TaskManager.instance = new TaskManager();
            TaskManager.instance.tasks = new ArrayList<TaskItem>();
        }
        return TaskManager.instance;
    }

    public static void addLooperTask(TaskItem task)
    {
        TaskManager.getInstance().tasks.add(task);
        TaskManager.setRunning(true);
    }

    public static void onResume()
    {
        TaskManager.setRunning(true);
    }

    public static void onPause()
    {
        TaskManager.setRunning(false);
    }

    public static void onDestroy()
    {
        TaskManager.setRunning(false);
    }

    private static void setRunning(boolean running)
    {
        if (running)
        {
            TaskManager.getInstance().executorService = (ScheduledExecutorService) Executors.newSingleThreadScheduledExecutor();
            TaskManager.getInstance().executorService.scheduleAtFixedRate(looper, LOOPER_INTERVAL, LOOPER_INTERVAL, TimeUnit.MILLISECONDS);
        }
        else
        {
            if (TaskManager.getInstance().executorService != null)
            {
                TaskManager.getInstance().executorService.shutdownNow();
            }
        }
    }

    private static void executeTasks()
    {
        for (TaskItem taskItem : TaskManager.getInstance().tasks)
        {
            taskItem.execute(TaskManager.getInstance());
        }
    }

    private static final Runnable looper = new Runnable()
                                         {
                                             @Override
                                             public void run()
                                             {
                                                 TaskManager.executeTasks();
                                             }
                                         };

    @Override
    public void onTaskItemListenerFinished(TaskItem item)
    {
        TaskManager.getInstance().tasks.remove(item);
        if (TaskManager.getInstance().tasks.size() == 0)
        {
            TaskManager.setRunning(false);
        }
    }

    @Override
    public void onTaskItemListenerFailed(TaskItem item, Exception e)
    {
        TaskManager.getInstance().tasks.remove(item);
        if (TaskManager.getInstance().tasks.size() == 0)
        {
            TaskManager.setRunning(false);
        }
    }
}
