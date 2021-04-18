package ca.mcgill.ecse420.a3;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> {
    Object[] values;
    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    AtomicInteger size;
    int head, tail;
    int capacity;

    public BoundedQueue(int _capacity) {
        values = new Object[_capacity];
        head = 0;
        tail = 0;
        size = new AtomicInteger(0);
        enqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();
        deqLock = new ReentrantLock();
        notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T x) throws InterruptedException {
        boolean mustWakeDequeuers = false;
        enqLock.lock();
        try {
            while (tail - head == capacity) {
                notFullCondition.await();
            }

            values[tail % capacity] = x;
            tail++;
            if (tail - head == 1) {
                mustWakeDequeuers = true;
            }
        } finally {
            enqLock.unlock();
        }
        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

    public T deq() throws InterruptedException {
        T value;
        boolean mustWakeEnqueuers = true;
        deqLock.lock();
        try {
            while (tail == head) {
                notEmptyCondition.await();
            }
            value = (T) values[head % capacity];
            head++;
            if (tail - head + 1 == capacity) {
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
        return value;
    }
}
