package ca.mcgill.ecse420.a3;

public class FineGrainedSet<T> {
    private Node<T> head;

    public FineGrainedSet() {
        head = null;
    }

    /**
     * Contains method returns true if the element is in the set otherwise false
     */
    public boolean contains(T item) {
        if (head == null) {
            return false;
        }
        head.lock();
        Node<T> pred = head;
        try {
            Node<T> cur = pred.next;
            // if the current node is null then the only element is pred so
            // check if its the value we're looking for
            if (cur == null || pred.key == item.hashCode()) {
                return pred.key == item.hashCode();
            }
            cur.lock();
            try {
                // move through the list until the current value is gretaer than the items
                // and we are not at the end
                while (cur.key < item.hashCode() && cur.next != null) {
                    pred.unlock();
                    pred = cur;
                    cur = cur.next;
                    cur.lock();
                }
                // return whether or not we found the item
                return cur.key == item.hashCode();
            } finally {
                cur.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    /**
     * print method implemented for testing purposes
     * Loops through the set printing every element
     */
    public void print() {
        Node cur = head;
        while (cur != null) {
            System.out.println(cur.item);
            cur = cur.next;
        }
    }

    /**
     * Add method implemented for testing purposes
     * Returns true if the item was successfully added otherwise false
     */
    public boolean add(T item) {
        // if the set is empty the element
        if (head == null) {
            head = new Node<>(item);
            return true;
        }

        // if the item is less than the head key then
        // create a new node and set it to the head
        if (item.hashCode() < head.key) {
            Node<T> newNode = new Node<>(item);
            newNode.next = head;
            head = newNode;
            return true;
        }

        Node<T> pred = head;
        Node<T> cur = head.next;

        // while we are not at the end of the set
        while (cur != null) {
            // if the key is less than the target hashcode, advance in the set
            // else if the item has already been added return false
            // otherwise create a new item and add it in the middle of pred and cur
            if (cur.key < item.hashCode()) {
                pred = cur;
                cur = pred.next;
            } else if (cur.key == item.hashCode()) {
                return false;
            } else {
                Node<T> newNode = new Node<>(item);
                pred.next = newNode;
                newNode.next = cur;
                return true;
            }
        }

        // if we make it to the end of the set add the item to the end
        pred.next = new Node<>(item);
        return true;
    }
}

