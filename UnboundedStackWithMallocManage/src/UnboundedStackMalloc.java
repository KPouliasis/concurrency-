import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by existentialtype on 4/6/14.
 */
public class UnboundedStackMalloc <T> {

    AtomicReference<Node> top = new AtomicReference<Node>(null);

    ThreadLocal<Node<T>> freeList = new ThreadLocal<Node<T>>() {
        protected Node<T> initialValue() {
            return null;
        }
    };




//static final int MIN_DELAY = ...;
    //static final int MAX_DELAY = ...;
    // Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
    protected boolean tryPush(Node node) {
        Node<T> oldTop = top.get();
        node.next = oldTop;
        return (top.compareAndSet(oldTop, node));
    }

    public void push(T value) {
        Node node = new Node(value);
        while (true) {
            if (tryPush(node)) {
                return;
            } else {
                //backoff.backoff();
            }
        }
    }

    public class EmptyException extends Exception {
    }

    protected Node tryPop() throws EmptyException {

        Node<T> oldTop = top.get();

        if (oldTop == null) {
            throw new EmptyException();
        }
        Node newTop = oldTop.next;
        if (top.compareAndSet(oldTop, newTop)) {
            return oldTop;
        } else {
            return null;
        }
    }

    public T pop() throws EmptyException {
        while (true) {
            Node<T> returnNode = tryPop();
            if (returnNode != null) {
                return returnNode.value;
            } else {
                //backoff.backoff();
            }
        }
    }

}
