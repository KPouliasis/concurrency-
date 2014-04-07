import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by existentialtype on 4/6/14.
 */
public class Node<T> {


         T value;
         Node<T> next;
        public Node(T value) {
            this.value = value;
            next = null;
        }
}
