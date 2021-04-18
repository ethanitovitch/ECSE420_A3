package ca.mcgill.ecse420.a3;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Node<T> {
    private Lock lock;

    public T item;
    public Node<T> next;
    public int key;

    public Node(T item) {
        this.item = item;
        key = item.hashCode();
        lock = new ReentrantLock();
        next = null;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}
