import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by existentialtype on 4/4/14.
 */
public class BoundedArrayQueue {


    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    AtomicInteger size, enqueuePoint, dequeuePoint;
    private Object[] elements;
    //  volatile Node head, tail;
    private int capacity;
    //
    private int count;

    public BoundedArrayQueue(int _capacity) {
        enqueuePoint = new AtomicInteger(0);
        dequeuePoint = new AtomicInteger(0);
        capacity = _capacity;
        size = new AtomicInteger(0);
        elements = new Object[capacity];
        enqLock = new ReentrantLock();
        deqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();
        deqLock = new ReentrantLock();
        notEmptyCondition = deqLock.newCondition();
    }

    public void enqueue(Object newElement) throws InterruptedException {
        boolean mustWakeDequers = false;
        try {
            while (size.get() == capacity)
                notFullCondition.await();
            elements[enqueuePoint.getAndIncrement()] = newElement;
            if (size.getAndIncrement() == 0)
                mustWakeDequers = true;

        } finally {
            enqLock.unlock();
        }
        if (mustWakeDequers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();


            } finally {
                deqLock.unlock();
            }

        }
    }


    public Object dequeue() throws InterruptedException {
        Object result;
        boolean mustWakeEnqueuers = false;
        deqLock.lock();
        try {
            while (size.get() == 0)
                notEmptyCondition.await();
            result = elements[dequeuePoint.getAndIncrement()];
            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true;
            }


        } finally {
            deqLock.unlock();
        }
        if (mustWakeEnqueuers) {
            enqLock.lock();
            try {
                notFullCondition.signalAll();
            } finally {
                enqLock.unlock();
            }
        }
        return result;
    }

}