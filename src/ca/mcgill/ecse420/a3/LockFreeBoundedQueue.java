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

    public void enq(T x) {
        int currentHead = head.get();
        int currentTail = tail.get();
        while (currentTail - currentHead == capacity) {
            currentHead = head.get();
            currentTail = tail.get();
        }
        int newtail = currentTail + 1;
        while (!tail.compareAndSet(currentTail, newtail)) {
            currentTail = tail.get();
        }
        values[currentTail % capacity] = x;
    }

    public T deq() {
        int currentHead = head.get();
        int currentTail = tail.get();
        while (currentTail == currentHead) {
            currentHead = head.get();
            currentTail = tail.get();
        }
        int newHead = currentHead + 1;
        while (!tail.compareAndSet(currentHead, newHead)) {
            currentHead = head.get();
        }
        return (T) values[currentHead % capacity];
    }
}
