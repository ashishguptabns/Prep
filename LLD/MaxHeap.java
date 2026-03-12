package LLD;

/* 
Insert
Remove
Peak
Search
*/

import java.util.ArrayList;

public class MaxHeap {
    private ArrayList<Integer> heap = new ArrayList<>();

    public MaxHeap() {
    }

    // 1. Insert: Add to end, then "bubble up"
    public void insert(int value) {
        heap.add(value);
        siftUp(heap.size() - 1);
    }

    // 2. Remove: Swap root with last element, remove last, then "bubble down"
    public Integer remove() {
        if (heap.isEmpty()) {
            return null;
        }
        int max = heap.get(0);
        int lastElement = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, lastElement);
            siftDown(0);
        }
        return max;
    }

    // 3. Peek: Just look at the top
    public Integer peek() {
        return heap.isEmpty() ? null : heap.get(0);
    }

    // 4. Search: Heaps aren't optimized for searching (O(n))
    public boolean search(int value) {
        return heap.contains(value);
    }

    // Helper methods for maintaining heap property
    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index) > heap.get(parentIndex)) {
                swap(index, parentIndex);
                index = parentIndex;
            } else
                break;
        }
    }

    private void siftDown(int index) {
        int lastIndex = heap.size() - 1;
        while (true) {
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;
            int largest = index;

            if (leftChild <= lastIndex && heap.get(leftChild) > heap.get(largest)) {
                largest = leftChild;
            }
            if (rightChild <= lastIndex && heap.get(rightChild) > heap.get(largest)) {
                largest = rightChild;
            }
            if (largest != index) {
                swap(index, largest);
                index = largest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        int temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }

    public static void main(String[] a) {
        MaxHeap heap = new MaxHeap();
        heap.insert(10);
        heap.insert(20);
        System.out.println(heap.peek());
    }
}