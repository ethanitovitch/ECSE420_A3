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

    /**
     * Enqueues an element into the list
     * @param x is a value to enqueue
     * @return void
     * */
    public void enq(T x) throws InterruptedException {
        boolean mustWakeDequeuers = false;
        enqLock.lock();
        try {
            // if the queue is full wait for an element to get deuqeued
            while (tail - head == capacity) {
                notFullCondition.await();
            }

            // add the element to the tail list and increment it
            values[tail % capacity] = x;
            tail++;

            if (tail - head == 1) {
                mustWakeDequeuers = true;
            }
        } finally {
            enqLock.unlock();
        }

        // wake up any pending dequeuers
        if (mustWakeDequeuers) {
            deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

    /**
     * Dequeues an element from the list
     * @return value
     * */
    public T deq() throws InterruptedException {
        T value;
        boolean mustWakeEnqueuers = true;
        deqLock.lock();
        try {
            // if the queue is empty wait for an element to be enqueued
            while (tail == head) {
                notEmptyCondition.await();
            }

            // get the value from the head and increment the head
            value = (T) values[head % capacity];
            head++;

            if (tail - head + 1 == capacity) {
                mustWakeEnqueuers = true;
            }
        } finally {
            deqLock.unlock();
        }

        // wake up any pending enqueuers
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
