import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by existentialtype on 4/7/14.
 */
public class Stack<T> {

    private class pushHandle implements Rooms.Handler{
        @Override
        public void onEmpty() {
            top.getAndIncrement();
        }
    }
    private class popHandle implements Rooms.Handler{
        @Override
        public void onEmpty() {
            top.getAndDecrement();
        }
    }
    private AtomicInteger top;
    private T[] items;
    private Rooms rooms;
    private pushHandle pushHandler=new pushHandle();
    private popHandle popHandler=new popHandle();

    public Stack(int capacity) {
        top = new AtomicInteger();
        items = (T[]) new Object[capacity];
        rooms.setExitHandler(0,pushHandler);
        rooms.setExitHandler(1,popHandler);
    }

    public class FullException extends Exception {}
    public class EmptyException extends Exception{}

    public void push(T x) throws FullException {
        int i = top.get();
        if (i < items.length) { // stack is NOT full

            rooms.enter(0);
            //entering the room is used like a lock!!
            items[i] = x;
        }
        else {throw new FullException();}

    }
    public T pop() throws EmptyException {
        int i = top.get()-1 ;
        if (i < 0) {
            // stack is empty
             throw new EmptyException();
        }
        rooms.enter(1);
        //entering the  room  1is used like a lock
        return items[i];
    }



}
