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
    //  move all elements from queue1 to queue2 except the last element
    while (this.queue1.length > 1) {
      //  follow FIFO
      this.queue2.push(this.queue1.shift())
    }

    const poppedItem = this.queue1.shift()

    // Swap the names of queue1 and queue2 using a temporary variable
    const temp = this.queue1;
    this.queue1 = this.queue2;
    this.queue2 = temp;

    return poppedItem

  };

  top = () => {
    // Similar to pop, move all elements from queue1 to queue2 except the last one
    while (this.queue1.length > 1) {
      this.queue2.push(this.queue1.shift());
    }

    // The last element in queue1 is the top of the stack
    const topItem = this.queue1[0];

    // Move it to queue2
    this.queue2.push(this.queue1.shift());

    // Swap the names of queue1 and queue2 using a temporary variable
    const temp = this.queue1;
    this.queue1 = this.queue2;
    this.queue2 = temp;

    // Return the top item
    return topItem;
  }

  empty = () => {
    return this.queue1.length === 0 && this.queue2.length === 0;
  };
};

/* Moving average from data stream - Given a stream of integers and a window size, calculate the moving average of all integers in the sliding window.
 */
class MovingAverage {

  /* pseudo code
    
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
      //  follow FIFO
      const removedValue = this.queue.shift();
      //  maintain sum of values in the queue
      this.sum -= removedValue;
    }
  }

  getMovingAverage() {
    if (this.data.length === 0) {
      // No data available
      return null;
    }

    return this.sum / this.data.length;
  }
}

/* Design most recently used queue */

class MRUQueue {
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

    this.queue.push(element);
    this.map[element] = this.queue.length - 1;

    return element;
  }

  getMRU() {
    return this.queue[this.queue.length - 1];
  }
}

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
  constructor(capacity) {
    this.capacity = capacity;
    this.queue = new Array(capacity);
    this.front = 0;
    this.rear = -1;
    this.size = 0;
  }

  enQueue(item) {
    if (this.isFull()) {
      return false;
    }

    this.rear = (this.rear + 1) % this.capacity;
    this.queue[this.rear] = item;
    this.size++;
    return true
  }

  deQueue() {
    if (this.isEmpty()) {
      return false;
    }

    this.front = (this.front + 1) % this.capacity;
    this.size--;
    return true
  }

  Front() {
    if (this.isEmpty()) {
      return -1;
    }

    return this.queue[this.front];
  }

  Rear() {
    if (this.isEmpty()) {
      return -1;
    }

    return this.queue[this.rear];
  }

  isEmpty() {
    return this.size === 0;
  }

  isFull() {
    return this.size === this.capacity;
  }
}