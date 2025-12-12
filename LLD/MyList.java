package LLD;

import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

interface MyIterator<T> {
    boolean hasNext();

    T next();

    void reset();
}

public class MyList<T> {
    Object[] elements;
    int size;
    public int modCount = 0;
    static final int DEFAULT_CAPACITY = 10;

    public MyList() {
        this.elements = new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    void add(T element) {
        ensureCapacity(size + 1);
        elements[size++] = element;
        modCount++;
    }

    void ensureCapacity(int minCapacity) {
        if (minCapacity <= elements.length) {
            return;
        }
        int newCapacity = Math.max(minCapacity, 2 * elements.length);
        Object[] newArr = new Object[newCapacity];
        System.arraycopy(elements, 0, newArr, 0, size);
        elements = newArr;
        modCount++;
    }

    MyIterator<T> getIterator() {
        return new MyListIterator<>();
    }

    void clear() {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
        size = 0;
        modCount++;
    }

    class MyListIterator<T> implements MyIterator<T> {

        int cursor = 0;
        final int expectedModCount;

        public MyListIterator() {
            this.expectedModCount = MyList.this.modCount;
        }

        void checkForCoModification() {
            if (this.expectedModCount != MyList.this.modCount) {
                throw new ConcurrentModificationException();
            }
        }

        @Override
        public boolean hasNext() {
            checkForCoModification();
            return cursor < size;
        }

        @Override
        public T next() {
            checkForCoModification();
            if (cursor >= size) {
                throw new NoSuchElementException();
            }
            return (T) elements[cursor++];
        }

        @Override
        public void reset() {
            checkForCoModification();
            cursor = 0;
        }
    }
}
