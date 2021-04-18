package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class LockFreeBoundedQueue<T> {
    Object[] values;
    AtomicInteger head;
    AtomicInteger tail;
    int capacity;

    public LockFreeBoundedQueue(int _capacity) {
        values = new Object[_capacity];
        head = new AtomicInteger(0);
        tail = new AtomicInteger(0);
        capacity = _capacity;
    }

    /**
     * Enqueues an element into the list
     * @param x is a value to enqueue
     * @return void
     * */
    public void enq(T x) {
        int currentHead = head.get();
        int currentTail = tail.get();

        // wait while the list is at capacity
        while (currentTail - currentHead == capacity) {

            // get the updated head and tail to see if there is room
            currentHead = head.get();
            currentTail = tail.get();
        }

        // while someone else is trying to update the currentail to the new tail
        int newtail = currentTail + 1;
        while (!tail.compareAndSet(currentTail, newtail)) {
            // get the updated current tail
            currentTail = tail.get();
        }
        // once we have successfully set the tail to the new tail
        // we can enqueue x to the currentTail position
        values[currentTail % capacity] = x;
    }

    /**
     * Dequeues an element from the list
     * @return value
     * */
    public T deq() {
        int currentHead = head.get();
        int currentTail = tail.get();

        // wait while the list is empty
        while (currentTail == currentHead) {

            // get the updated head and tail to see if it has elements
            currentHead = head.get();
            currentTail = tail.get();
        }

        // while someone else is trying to update the currenHead to the new head
        int newHead = currentHead + 1;
        while (!tail.compareAndSet(currentHead, newHead)) {
            // get the updated current head
            currentHead = head.get();
        }

        // once we have successfully set the head to the new head
        // we can dequeue a value from the currentHead position
        return (T) values[currentHead % capacity];
    }
}
