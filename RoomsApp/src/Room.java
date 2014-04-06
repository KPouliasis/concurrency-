import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by existentialtype on 3/6/14.
 */

public class Room {
    public interface Handler {
        void onEmpty();
    }
    Lock lock;
    List<List<Long>> collection;
    boolean[] flags;
    Condition condition;
    int size;
    public Room(int m) {
        flags=new boolean[m];size=m;for (int i=0;i<m;i++) flags[i]=false;
        condition= lock.newCondition(); collection= new ArrayList<List<Long>>();
        for (int i=0;i<size;i++){collection.add(Collections.<Long>emptyList());}
    lock=new ReentrantLock();room_conds=new Condition[m];}
    Condition[] room_conds;
    int enter_helper(){

        for (int k=0;k<size;k++)
            if (flags[k]) return k;
        return -1;
    }
    void enter(int i) throws InterruptedException {
            lock.lock();
            try{while((enter_helper()!=-1)&(enter_helper()!=i)){condition.await();}
                 collection.get(i).add(Thread.currentThread().getId()) ;
                 flags[enter_helper()]=true;
                }
            finally { lock.unlock();}
    }
    int in_room() {for (int i=0;i<size;i++)
                        if (collection.get(i).contains(Thread.currentThread().getId())) return i;
                    return -1;}
    boolean exit() {lock.lock();try {int size=-1;int room=in_room();
                                     if (room!=-1){collection.get(room).remove(Thread.currentThread().getId());
                                                   size=collection.get(room).size();
                                     if (size==0) {flags[room]=false;condition.signalAll();return true;}
                                     else {return false;}
                                     } }finally {lock.unlock();return false;}}


   // public void setExitHandler(int i, Room.Handler h) { ... };
}
