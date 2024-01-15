/* Moving average from data stream - Given a stream of integers and a window size, calculate the moving average of all integers in the sliding window.
 */
class MovingAverage {

  /* pseudo code
    add data
      push to the queue
      track sum
      queue size exceeds window length
        remove from queue
        track sum 
    find moving avg
      sum / queue length
  */

  constructor(windowSize) {
    this.windowSize = windowSize;
    this.queue = [];
    this.sum = 0;
  }

  addDataPoint(value) {
    this.queue.push(value);
    this.sum += value;

    if (this.queue.length > this.windowSize) {
      const removedValue = this.queue.shift();
      this.sum -= removedValue;
    }
  }

  getMovingAverage() {
    if (this.queue.length === 0) {
      return null;
    }
    return this.sum / this.queue.length;
  }
}

/* Implement stack using 2 queues - Implement a last-in-first-out (LIFO) stack using only two queues */
class MyStack {

  /* pseudo code
    push
      move to queue1
    pop
      we need to pop the last item which was inserted
      empty the queue1 into queue2 except the last item
      swap queue1 and queue2
      return popped item
    top
      empty the queue1 into queue2 except the last item
      hold the last item in a var
      swap names of queue1 and queue2
      return saved item
  */

  constructor() {
    this.queue1 = [];
    this.queue2 = [];
  }

  push = (x) => {
    this.queue1.push(x)
  };

  pop = () => {
    while (this.queue1.length > 1) {
      this.queue2.push(this.queue1.shift())
    }

    const poppedItem = this.queue1.shift()

    const temp = this.queue1;
    this.queue1 = this.queue2;
    this.queue2 = temp;

    return poppedItem

  };

  top = () => {
    while (this.queue1.length > 1) {
      this.queue2.push(this.queue1.shift());
    }

    const topItem = this.queue1[0];

    this.queue2.push(this.queue1.shift());

    const temp = this.queue1;
    this.queue1 = this.queue2;
    this.queue2 = temp;

    return topItem;
  }

  empty = () => {
    return this.queue1.length === 0 && this.queue2.length === 0;
  };
};

/* Design Circular Queue

MyCircularQueue(k) Initializes the object with the size of the queue to be k.
int Front() Gets the front item from the queue. If the queue is empty, return -1.
int Rear() Gets the last item from the queue. If the queue is empty, return -1.
boolean enQueue(int value) Inserts an element into the circular queue. Return true if the operation is successful.
boolean deQueue() Deletes an element from the circular queue. Return true if the operation is successful.
boolean isEmpty() Checks whether the circular queue is empty or not.
boolean isFull() Checks whether the circular queue is full or not.
 */

class MyCircularQueue {

  /* pseudo code
      enqueue
        increase the rear index
        put in the queue at rear index
        increase the size
      dequeue
        increase the front index
        decrease the size
      notice use of modulo
  */

  constructor(capacity) {
    this.capacity = capacity;
    this.queue = new Array(capacity);
    this.frontIndex = 0;
    this.rearIndex = -1;
    this.size = 0;
  }

  enQueue(item) {
    if (this.isFull()) {
      return false;
    }

    this.rearIndex = (this.rearIndex + 1) % this.capacity;
    this.queue[this.rearIndex] = item;
    this.size++;
    return true
  }

  deQueue() {
    if (this.isEmpty()) {
      return false;
    }

    this.frontIndex = (this.frontIndex + 1) % this.capacity;
    this.size--;
    return true
  }

  Front() {
    if (this.isEmpty()) {
      return -1;
    }

    return this.queue[this.frontIndex];
  }

  Rear() {
    if (this.isEmpty()) {
      return -1;
    }

    return this.queue[this.rearIndex];
  }

  isEmpty() {
    return this.size === 0;
  }

  isFull() {
    return this.size === this.capacity;
  }
}

/* Design most recently used queue */

class MRUQueue {

  /* pseudo code
      enqueue
        queue length > capacity
          remove from queue and map
        push to the queue
        store index of this item in the map
      fetch item at index
        find the element from queue array
        remove from the queue
        remove from the map
        enqueue this element
        return this element
      get MRU
        return the last element of the queue
  */

  constructor(capacity) {
    this.capacity = capacity;
    this.queue = [];
    this.map = {};
  }

  enqueue(element) {
    if (this.queue.length >= this.capacity) {
      const removedElement = this.queue.shift();
      delete this.map[removedElement];
    }

    this.queue.push(element);
    this.map[element] = this.queue.length - 1;
  }

  fetch(index) {
    if (index < 0 || index >= this.queue.length) {
      throw new Error("Invalid index");
    }

    const element = this.queue[index];
    this.queue.splice(index, 1);
    delete this.map[element];

    this.enqueue(element)

    return element;
  }

  getMRU() {
    return this.queue[this.queue.length - 1];
  }
}

