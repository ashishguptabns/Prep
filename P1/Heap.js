/* Make Max Heap */

class MaxHeap {
    //  max heap is a binary tree in which value of each node is greater than or equal to the values of the children

    constructor() {
        this.heap = []
    }

    push = (value) => {
        //  push as the last item
        this.heap.push(value)
        //  start from last position and move the item to its right position
        this.heapifyUp()
    }

    isEmpty = () => {
        return this.heap.length === 0
    }

    pop = () => {
        if (this.isEmpty()) {
            return null
        }

        //  return the top item
        const root = this.heap[0]

        //  put the last item to top and move down to right position
        const last = this.heap.pop()
        if (!this.isEmpty()) {
            this.heap[0] = last
            this.heapifyDown()
        }

        return root
    }

    heapifyUp = () => {
        //  start from the last item
        let currentIdx = this.heap.length - 1;

        //  keep going up the tree
        while (currentIdx > 0) {
            //  parent is at (i - 1) / 2
            const parentIdx = Math.floor((currentIdx - 1) / 2);

            if (this.heap[currentIdx] > this.heap[parentIdx]) {
                //  child is bigger than parent hence swap
                [this.heap[currentIdx], this.heap[parentIdx]] = [
                    this.heap[parentIdx],
                    this.heap[currentIdx],
                ];
                //  move up
                currentIdx = parentIdx;
            } else {
                break;
            }
        }
    }

    heapifyDown() {
        //  start from the top
        let currentIdx = 0;

        while (true) {
            const leftChildIdx = 2 * currentIdx + 1;
            const rightChildIdx = 2 * currentIdx + 2;
            let swapIdx = null;

            if (leftChildIdx < this.heap.length && this.heap[leftChildIdx] > this.heap[currentIdx]) {
                //  left child is bigger than parent
                swapIdx = leftChildIdx;
            }

            if (rightChildIdx < this.heap.length && this.heap[rightChildIdx] > this.heap[currentIdx]) {
                //  right child is bigger than parent
                if (swapIdx === null || this.heap[rightChildIdx] > this.heap[swapIdx]) {
                    //  right child can be better option than left
                    swapIdx = rightChildIdx;
                }
            }

            if (swapIdx === null) {
                //  item is at right position
                break;
            }

            //  swap and move down
            [this.heap[currentIdx], this.heap[swapIdx]] = [this.heap[swapIdx], this.heap[currentIdx]];
            currentIdx = swapIdx;
        }
    }
}

/* Make Min Heap */

class MinHeap {
    //  min heap is a binary tree wherein each node should be less than or equal to its children

    constructor() {
        this.heap = [];
    }

    push = (val) => {
        this.heap.push(val);
        //  start from the last position and move to its right position
        this.heapifyUp();
    }

    pop = () => {
        if (this.isEmpty()) {
            return null;
        }

        const root = this.heap[0];
        const lastNode = this.heap.pop();

        if (this.heap.length > 0) {
            this.heap[0] = lastNode;
            //  move down the tree
            this.heapifyDown();
        }

        return root;
    }

    isEmpty = () => {
        return this.heap.length === 0;
    }

    heapifyUp = () => {
        //  start from end
        let index = this.heap.length - 1;

        while (index > 0) {
            const parentIndex = Math.floor((index - 1) / 2);

            //  child should be bigger
            if (this.heap[index] < this.heap[parentIndex]) {
                [this.heap[parentIndex], this.heap[index]] = [this.heap[index], this.heap[parentIndex]]
                index = parentIndex;
            } else {
                break;
            }
        }
    }

    heapifyDown = () => {
        let index = 0;
        const length = this.heap.length;

        while (true) {
            const leftChildIndex = 2 * index + 1;
            const rightChildIndex = 2 * index + 2;
            let smallestChildIndex = index;

            if (leftChildIndex < length && this.heap[leftChildIndex] < this.heap[smallestChildIndex]) {
                smallestChildIndex = leftChildIndex;
            }

            if (rightChildIndex < length && this.heap[rightChildIndex] < this.heap[smallestChildIndex]) {
                smallestChildIndex = rightChildIndex;
            }

            if (smallestChildIndex !== index) {
                [this.heap[smallestChildIndex], this.heap[index]] = [this.heap[index], this.heap[smallestChildIndex]]
                index = smallestChildIndex;
            } else {
                break;
            }
        }
    }
}

/* Given an array of meeting time intervals intervals where intervals[i] = [starti, endi], return the minimum number of conference rooms required.
 */
const minMeetingRoom = (intervals) => {
    // Sort the intervals based on start time
    intervals.sort((a, b) => a[0] - b[0]);

    // Min priority queue to store end times of meeting rooms
    const pq = new MinPriorityQueue();

    for (const [start, end] of intervals) {
        // If the priority queue is not empty and the start time of the current interval
        // is greater than or equal to the end time of the meeting at the front of the queue
        if (!pq.isEmpty && start >= pq.peek().value) {
            // No extra meeting room needed as end time is less than the current one's start time
            // Remove the time that ends earliest as this room will be blocked till curr end time
            pq.dequeue();
        }
        //  Add the end time of the current meeting to the priority queue
        //  this meeting room will be occupied till this time
        pq.enqueue({ value: end });
    }

    //  The size of the priority queue represents the minimum number of meeting rooms required 
    //  these many meetings are happening at the same time
    return pq.size;
}

/* Top K Frequent Elements - Given an integer array nums and an integer k, return the k most frequent elements. You may return the answer in any order. */

/**
 * @param {number[]} nums
 * @param {number} k
 * @return {number[]}
 */
const topKFrequent = (nums, k) => {
    //  maintain frequency in the map
    const map = {}
    //  maintain elements by their frequency in the heap
    const minHeap = new MinPriorityQueue()

    const res = []

    //  track frequency of each num
    for (const n of nums) {
        map[n] = map[n] + 1 || 1
    }

    for (const [element, count] of Object.entries(map)) {
        minHeap.enqueue(element, count)
        while (minHeap.size() > k) {
            //  only keep k elements
            //  notice we are removing items with smallest frequencies
            minHeap.dequeue()
        }
    }

    while (minHeap.size()) {
        res.push(minHeap.dequeue().element)
    }

    return res
};

/* Kth largest element in an array - Given an integer array nums and an integer k, return the kth largest element in the array.

Note that it is the kth largest element in the sorted order, not the kth distinct element.

Can you solve it without sorting?
 */
/**
 * @param {number[]} nums
 * @param {number} k
 * @return {number}
 */
const findKthLargest = (nums, k) => {
    const minHeap = new MinHeap()

    //  push all items
    for (const num of nums) {
        minHeap.push(num)
    }

    for (let i = 0; i < nums.length; i++) {
        //  keep popping
        const element = minHeap.pop()
        if (nums.length - i === k) {
            //  kth largest
            return element
        }
    }

    return -1
};

/* Maximum score from removing stones - You are playing a solitaire game with three piles of stones of sizes a​​​​​​, b,​​​​​​ and c​​​​​​ respectively. Each turn you choose two different non-empty piles, take one stone from each, and add 1 point to your score. The game stops when there are fewer than two non-empty piles (meaning there are no more available moves).

Given three integers a​​​​​, b,​​​​​ and c​​​​​, return the maximum score you can get.
 */
/**
 * @param {number} a
 * @param {number} b
 * @param {number} c
 * @return {number}
 */
const maximumScore = (a, b, c) => {

    //  use max heap to track sizes of piles
    //  take two largest piles and get a score as per condition
    //  then put the piles back

    const maxHeap = new MaxHeap()
    maxHeap.push(a)
    maxHeap.push(b)
    maxHeap.push(c)

    let score = 0

    while (maxHeap.heap.length >= 2) {
        const pile1 = maxHeap.pop()
        const pile2 = maxHeap.pop()

        if (pile1 > 1) {
            //  take out one stone and push back
            maxHeap.push(pile1 - 1)
        }
        if (pile2 > 1) {
            //  take out one stone and push back
            maxHeap.push(pile2 - 1)
        }

        score++
    }

    return score
};

/* K closest points to origin - Given an array of points where points[i] = [xi, yi] represents a point on the X-Y plane and an integer k, return the k closest points to the origin (0, 0).

The distance between two points on the X-Y plane is the Euclidean distance (i.e., √(x1 - x2)2 + (y1 - y2)2).

You may return the answer in any order. The answer is guaranteed to be unique (except for the order that it is in). */

/**
 * @param {number[][]} points
 * @param {number} k
 * @return {number[][]}
 */
const kClosest = (points, k) => {
    //  use minHeap to keep the distance from origin for each point

    const minHeap = new MinPriorityQueue()
    points.forEach(([x, y]) => minHeap.enqueue([x, y], (x ** 2 + y ** 2)))

    const res = []
    while (res.length < k) {
        res.push(minHeap.dequeue().element)
    }

    return res
};

/* Remove Stones to Minimize the Total - You are given a 0-indexed integer array piles, where piles[i] represents the number of stones in the ith pile, and an integer k. You should apply the following operation exactly k times:

Choose any piles[i] and remove floor(piles[i] / 2) stones from it.
Notice that you can apply the operation on the same pile more than once.

Return the minimum possible total number of stones remaining after applying the k operations.
 */
/**
* @param {number[]} piles
* @param {number} k
* @return {number}
*/
const minStoneSum = (piles, k) => {
    //  heap is important because we want to find the pile with most stones after each operation
    const maxHeap = new MaxPriorityQueue()
    let stones = 0

    piles.forEach((pile) => {
        //  max pile of stones will be on top of heap            
        maxHeap.enqueue(pile)

        //  track total stones
        stones += pile
    })

    for (let i = 0; i < k; i++) {
        //  remove stones for k times            
        const topPile = maxHeap.dequeue().element
        //  remove from the highest number to optimize            
        const leftPile = Math.ceil(topPile / 2)

        //  keep reducing left stones
        stones -= topPile - leftPile

        //  keep the left stones back in the heap for this pile to be placed at right index
        maxHeap.enqueue(leftPile)
    }

    return stones
};

/* Kth largest integer in an array - You are given an array of strings nums and an integer k. Each string in nums represents an integer without leading zeros.

Return the string that represents the kth largest integer in nums.
 */
/**
 * @param {string[]} nums
 * @param {number} k
 * @return {string}
 */
const kthLargestNumber = (nums, k) => {
    const maxHeap = new MaxPriorityQueue({ compare: (a, b) => { return b - a } })

    nums.forEach(num => maxHeap.enqueue(BigInt(num)))

    while (k > 1) {
        maxHeap.dequeue()
        k--
    }
    return maxHeap.front().toString()
};

// todo
/* Meeting rooms - You are given an integer n. There are n rooms numbered from 0 to n - 1.

You are given a 2D integer array meetings where meetings[i] = [starti, endi] means that a meeting will be held during the half-closed time interval [starti, endi). All the values of starti are unique.

Meetings are allocated to rooms in the following manner:
Each meeting will take place in the unused room with the lowest number.
If there are no available rooms, the meeting will be delayed until a room becomes free. The delayed meeting should have the same duration as the original meeting.
When a room becomes unused, meetings that have an earlier original start time should be given the room.

Return the number of the room that held the most meetings. If there are multiple rooms, return the room with the lowest number.

A half-closed interval [a, b) is the interval between a and b including a and not including b.
 */