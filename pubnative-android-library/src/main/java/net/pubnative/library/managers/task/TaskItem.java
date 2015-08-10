package net.pubnative.library.managers.task;

public abstract class TaskItem
{
    public interface TaskItemListener
    {
        void onTaskItemListenerFinished(TaskItem item);

        void onTaskItemListenerFailed(TaskItem item, Exception e);
    }

    private TaskItemListener managerListener;
    private TaskItemListener listener;

    public TaskItem(TaskItemListener listener)
    {
        this.listener = listener;
    }

    public void execute(TaskItemListener listener)
    {
        this.managerListener = listener;
        this.onExecute();
    }

    public abstract void onExecute();

    protected void invokeOnTaskItemListenerFinished()
    {
        if (this.managerListener != null)
        {
            this.managerListener.onTaskItemListenerFinished(this);
        }
        if (this.listener != null)
        {
            this.listener.onTaskItemListenerFinished(this);
        }
    }

    protected void invokeOnTaskItemListenerFailed(Exception error)
    {
        if (this.managerListener != null)
        {
            this.managerListener.onTaskItemListenerFailed(this, error);
        }
        if (this.listener != null)
        {
            this.listener.onTaskItemListenerFailed(this, error);
        }
    }
}
