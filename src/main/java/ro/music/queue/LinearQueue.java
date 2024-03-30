package ro.music.queue;

public class LinearQueue<T extends Queueable> extends AbstractQueue<T>
{
    public LinearQueue(AbstractQueue<T> queue)
    {
        super(queue);
    }

    @Override
    public int add(T item)
    {
        list.add(item);
        return list.size() - 1;
    }

}
