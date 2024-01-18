/* Add Two Numbers - You are given two non-empty linked lists representing two non-negative integers. The digits are stored in reverse order, and each of their nodes contains a single digit. Add the two numbers and return the sum as a linked list.

You may assume the two numbers do not contain any leading zero, except the number 0 itself.
 */
/**
 * @param {ListNode} l1
 * @param {ListNode} l2
 * @return {ListNode}
 */
const addTwoNumbers = (l1, l2) => {
    const dummyHead = new ListNode(0); // Create a dummy node to simplify code
    let currentNode = dummyHead;
    let carry = 0;

    while (l1 || l2 || carry) {
        // Calculate the sum of current digits and carry
        const sum = (l1 ? l1.val : 0) + (l2 ? l2.val : 0) + carry;

        // Update carry for the next calculation
        carry = Math.floor(sum / 10);

        // Create a new node with the current digit and link it to the result linked list
        currentNode.next = new ListNode(sum % 10);
        currentNode = currentNode.next;

        // Move to the next nodes in the input linked lists if they exist
        if (l1) {
            l1 = l1.next;
        }
        if (l2) {
            l2 = l2.next;
        }
    }

    // Return the result linked list (excluding the dummy head)
    return dummyHead.next;
};

/* Odd Even Linked List - Given the head of a singly linked list, group all the nodes with odd indices together followed by the nodes with even indices, and return the reordered list.
 */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
const oddEvenList = (head) => {
    if (!head) {
        return head
    }
    let oddNode = head
    let evenNode = head.next
    let evenHead = head.next

    while (evenNode !== null && evenNode.next !== null) {
        //  keep connecting odd ones to each other
        oddNode.next = evenNode.next
        oddNode = oddNode.next
        //  keep connecting even ones to each other
        evenNode.next = oddNode.next
        evenNode = evenNode.next
    }
    //  all even nodes are on the right side now
    oddNode.next = evenHead

    return head
};

/* Flatten a Multilevel Doubly Linked List - Given the head of the first level of the list, flatten the list so that all the nodes appear in a single-level, doubly linked list. Let curr be a node with a child list. The nodes in the child list should appear after curr and before curr.next in the flattened list.

Return the head of the flattened list. The nodes in the list must have all of their child pointers set to null.
 */
/**
 * @param {Node} head
 * @return {Node}
 */
const flatten = (head) => {

    /* pseudo code
    have a temp node which has head node as next node
    apply flattenDFS with temp and head as input
        input are prev and curr node
        if curr is null - we reached the end and prev is the tail to return
        link the curr as prev's next node and vice versa
        hold next of curr as we explore the kids of curr
        appply flattenDFS with curr and curr's child - this will give the tail
        remove the child
        apply flattenDFS with tail of nested flattening and temp next of curr
    remove the prev pointer of head
    return next of temp i.e. head node */

    const flattenDFS = (prev, curr) => {
        if (!curr) {
            //  prev is the tail
            return prev
        }

        //  establish links
        curr.prev = prev
        prev.next = curr

        //  this will be needed after flattening
        const tempNext = curr.next

        //  find the tail of flattened children
        const tail = flattenDFS(curr, curr.child)

        //  remove the child
        curr.child = null

        //  establish the pointers and travel further
        return flattenDFS(tail, tempNext)
    }

    if (!head) {
        return head
    }

    const temp = new Node(-1)
    temp.next = head
    flattenDFS(temp, head)

    //  remove the temp pointer
    temp.next.prev = null

    return temp.next
};

/* Linked List Cycle - Given the head of a linked list, return the node where the cycle begins. If there is no cycle, return null. */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
const detectCycle = (head) => {

    /* pseudo code
     take a slow pointer and a fast pointer moving with double speed
        metNode is where these two pointers meet
     if fastNode has null as next - no cycle exists
     start two pointers; one from head another from meetPoint and where these two meet is the start of cycle */

    if (!head) {
        return head
    }

    let slowNode = head
    let fastNode = head

    while (fastNode?.next) {
        slowNode = slowNode.next
        fastNode = fastNode.next.next

        if (slowNode === fastNode) {
            break
        }
    }

    if (!fastNode?.next) {
        //  no cycle
        return null
    }

    slowNode = head
    while (slowNode != fastNode) {
        slowNode = slowNode.next
        fastNode = fastNode.next
    }

    return slowNode
};

/* Palindrome Linked List - Given the head of a singly linked list, return true if it is a 
palindrome or false otherwise.
 */
/**
 * @param {ListNode} head
 * @return {boolean}
 */
const isPalindrome = (head) => {

    /* pseudo code
    keep a global var to track left pointer
    go to the end of list
    compare the right pointer to left pointer */

    let currNode = head
    let isPalindrome = true
    const moveToEnd = (node) => {
        if (node) {
            moveToEnd(node.next)

            if (node.val !== currNode.val) {
                isPalindrome = false
            }
            currNode = currNode.next
        }
    }
    moveToEnd(head)

    return isPalindrome
};

/* Merge k Sorted Lists - You are given an array of k linked - lists lists, each linked - list is sorted in ascending order.

Merge all the linked - lists into one sorted linked - list and return it.
 */
/**
 * @param {ListNode[]} lists
 * @return {ListNode}
 */
const mergeKLists = (lists) => {

    /* pseudo code
    add all heads into a min heap
    min heap will hold heads in sorted order
    have a dummy node to track the head
    keep a moving node - curr
    loop through the heap
        pop a node and assign it as next node of curr
        move the curr node to next
        push the next of the node which was popped from the heap into the heap
    return dummy's next */

    //  we will solve this using min heap
    const minHeap = new MinHeap()

    //  push the heads of each list in the heap
    for (const head of lists) {
        if (head) {
            minHeap.push(head)
        }
    }

    const dummy = new ListNode()
    let currNode = dummy

    while (!minHeap.isEmpty()) {
        //  pop the node from minHeap with smallest value
        const node = minHeap.pop()

        currNode.next = node
        currNode = currNode.next

        //  move to the next node of this list
        if (node.next) {
            minHeap.push(node.next)
        }
    }

    // return the head of final sorted list
    return dummy.next
};

/* Insert greatest common divisors - Given the head of a linked list head, in which each node contains an integer value.

Between every pair of adjacent nodes, insert a new node with a value equal to the greatest common divisor of them.
 */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
const insertGreatestCommonDivisors = (head) => {

    const findGCD = (a, b) => {
        while (b !== 0) {
            const temp = b;
            b = a % b;
            a = temp;
        }
        return a;
    }

    let currNode = head

    while (currNode && currNode.next) {
        const gcd = findGCD(currNode.val, currNode.next.val)

        const gcdNode = new ListNode(gcd)
        gcdNode.next = currNode.next
        currNode.next = gcdNode

        currNode = gcdNode.next
    }

    return head
};

/* Nested List Weight Sum - Given a nested list of integers, return the sum of all integers in the list weighted by their depth.

Each element is either an integer, or a list-- whose elements may also be integers or other lists.
 */
const depthSum = (nestedList) => {

    /* pseudo code
    we have to use DFS approach to solve this problem
    DFS will receive the curr node or array with depth and return the weighted sum
        loop through the array
        call DFS if the curr node is an array and find the sum
        return the sum 
    */

    const dfs = (nestedArr, depth) => {
        let totalSum = 0;

        for (const element of nestedArr) {
            //  check for nesting
            if (Array.isArray(element)) {
                //  get the sum of this list
                totalSum += dfs(element, depth + 1);
            } else {
                //  add weighted sum
                totalSum += element * depth;
            }
        }

        return totalSum;
    }

    return dfs(nestedList, 1);
}

/* Max twin sum of a linked list - Given the head of a linked list with even length, return the maximum twin sum of the linked list.
 */
/**
 * @param {ListNode} head
 * @return {number}
 */
const pairSum = (head) => {
    let maxTwinSum = 0
    let leftPointer = head
    const countSum = (node) => {
        if (!node || node.next === leftPointer) {
            return
        }
        //  move the node to end first
        countSum(node.next)
        const currTwinSum = leftPointer.val + node.val
        maxTwinSum = Math.max(currTwinSum, maxTwinSum)

        //  keep moving forward as the node moves backward
        leftPointer = leftPointer.next
    }
    countSum(head)
    return maxTwinSum
};

/* Delete node in a linked list - Head is not given
 */
const deleteNode = (node) => {
    if (node) {
        //  take the value of next node
        node.val = node.next.val
        //  skip next node
        node.next = node.next.next
    }
};

/* Spiral matrix - You are given two integers m and n, which represent the dimensions of a matrix.

You are also given the head of a linked list of integers.

Generate an m x n matrix that contains the integers in the linked list presented in spiral order (clockwise), starting from the top-left of the matrix. If there are remaining empty spaces, fill them with -1.

Return the generated matrix.
 */
const spiralMatrix = (m, n, head) => {
    let currRow = 0
    let currCol = 0

    let top = 1, right = n - 1, bottom = m - 1, left = 0

    let direction = 'r'

    //  -1 by default
    const mat = Array(m).fill().map(() => Array(n).fill(-1))

    //  iterate till linked list finishes
    while (head) {
        mat[currRow][currCol] = head.val

        //  reached right
        if (currCol === right && direction === 'r') {
            right--
            direction = 'd'
        }
        //  reached bottom
        if (currRow === bottom && direction === 'd') {
            bottom--
            direction = 'l'
        }
        //  reached left
        if (currCol === left && direction === 'l') {
            left++
            direction = 'u'
        }
        //  reached top
        if (currRow === top && direction === 'u') {
            top++
            direction = 'r'
        }

        //  handle moving pointers
        if (direction === 'r') {
            currCol++
        }
        if (direction === 'd') {
            currRow++
        }
        if (direction === 'l') {
            currCol--
        }
        if (direction === 'u') {
            currRow--
        }

        head = head.next
    }

    return mat
};


/* Merge Nodes in Between Zeros - You are given the head of a linked list, which contains a series of integers separated by 0's. The beginning and end of the linked list will have Node.val == 0.

For every two consecutive 0's, merge all the nodes lying in between them into a single node whose value is the sum of all the merged nodes. The modified list should not contain any 0's.

Return the head of the modified linked list.
 */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
const mergeNodes = (head) => {

    /* pseudo code
    keep a dummy to track the head
    keep a curr node and a prev node both starting with head
    move through the list with curr node
        curr node  is 0
            connnect prev to next of curr and skip the 0 node
        curr node is non 0
            move through to 0 node
                keep passing the sum to next node
                keep skipping the curr node - connect prev to next
            now curr node is the node just before 0 - assign to prev
    return dummy's next */

    //  use this to keep track of the head
    let dummy = head

    //  this node will move
    let currNode = head

    //  this node is used to skip current nodes
    let prev = currNode

    while (currNode) {
        if (currNode.val === 0) {
            //  skip the 0 node
            prev.next = currNode.next
        } else {
            while (currNode.next.val !== 0) {
                //  pass the values 
                currNode.next.val = currNode.next.val + currNode.val

                //  skip the current node
                prev.next = currNode.next
                currNode = currNode.next
            }
            //  node before the 0 node
            prev = currNode
        }
        currNode = currNode.next
    }

    //  first node was 0
    return dummy.next
};

/* Swap nodes in pairs - Given a linked list, swap every two adjacent nodes and return its head. You must solve the problem without modifying the values in the list's nodes (i.e., only nodes themselves may be changed.)
 */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
var swapPairs = function (head) {

    /* pseudo code
        have a temp node to track head    
        move through the list - start from temp
            do the swapping
            move curr to the node before two nodes which will be swapped
        return next of temp
    */

    if (head && head.next) {

        //  temp node to track the head
        const temp = new ListNode(0)
        temp.next = head

        //  moving pointer
        let curr = temp
        while (curr.next && curr.next.next) {
            //  nodes to swap
            let node1 = curr.next
            let node2 = curr.next.next

            curr.next = node2
            node1.next = node2.next
            node2.next = node1

            //  skip one node
            curr = curr.next.next
        }
        return temp.next
    }
    return head
};

/* Remove Nth Node From End of List - Given the head of a linked list, remove the nth node from the end of the list and return its head.
 */
/**
 * @param {ListNode} head
 * @param {number} n
 * @return {ListNode}
 */
const removeNthFromEnd = (head, n) => {
    // Initialize two pointers, fast and slow, both pointing to the head of the linked list
    let fast = head, slow = head;

    // Move the fast pointer n nodes ahead
    for (let i = 0; i < n; i++) {
        fast = fast.next;
    }

    // If fast becomes null, it means we need to remove the head of the linked list
    // Return head.next in this case
    if (!fast) {
        return head.next;
    }

    // Move both fast and slow pointers until fast reaches the end of the linked list
    while (fast.next) {
        fast = fast.next;
        slow = slow.next;
    }

    // Now, slow is pointing to the node just before the one to be removed
    // Adjust the pointers to skip the node to be removed
    slow.next = slow.next.next;

    // Return the head of the modified linked list
    return head;
};

/* Sort list - Given the head of a linked list, return the list after sorting it in ascending order.
 */
/**
 * Definition for singly-linked list.
 * function ListNode(val, next) {
 *     this.val = (val===undefined ? 0 : val)
 *     this.next = (next===undefined ? null : next)
 * }
 */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
const sortList = (head) => {
    // Base case: if the list is empty or has only one element, it is already sorted
    if (!head || !head.next) {
        return head;
    }

    // Initialize pointers for finding the middle of the linked list
    let [prev, slow, fast] = [null, head, head];

    // Move 'fast' two steps and 'slow' one step until 'fast' reaches the end
    while (fast && fast.next) {
        prev = slow;
        slow = slow.next;
        fast = fast.next.next;
    }

    // If 'prev' is not null, it means we have reached the middle of the list, so disconnect the first half
    if (prev) {
        prev.next = null;
    }

    // Recursively sort the first half and the second half of the linked list
    const firstHalf = sortList(head);
    const secHalf = sortList(slow);

    // Merge the sorted halves
    const merge = (a, b) => {
        // Base cases: if either list is empty, return the other list
        if (!a) {
            return b;
        }
        if (!b) {
            return a;
        }

        // Compare values of nodes and merge the lists accordingly
        if (a.val < b.val) {
            a.next = merge(a.next, b);
            return a;
        } else {
            b.next = merge(a, b.next);
            return b;
        }
    };

    // Return the merged and sorted list
    return merge(firstHalf, secHalf);
};

/* Rotate list - Given the head of a linked list, rotate the list to the right by k places.
*/
/**
 * Definition for singly-linked list.
 * function ListNode(val, next) {
 *     this.val = (val===undefined ? 0 : val)
 *     this.next = (next===undefined ? null : next)
 * }
 */
/**
 * @param {ListNode} head
 * @param {number} k
 * @return {ListNode}
 */
const rotateRight = (head, k) => {
    // Initialize variables to calculate the length of the linked list and create a dummy node for traversal
    let len = 0, dummy = head;

    // Calculate the length of the linked list
    while (dummy) {
        len++;
        dummy = dummy.next;
    }

    // If the length is less than 2, or if the number of shifts is 0, return the original list as no rotations are needed
    if (len < 2 || k === 0) {
        return head;
    }

    // Calculate the effective number of shifts to avoid unnecessary rotations
    k = k % len;

    // If the effective number of shifts is 0, return the original list
    if (k === 0) {
        return head;
    }

    // Reset the dummy node to the original head
    dummy = head;

    // Move the head to the new position after rotation
    while (len - k++) {
        head = head.next;
    }

    // Save the new head position in a temporary variable
    temp = head;

    // Traverse to the end of the rotated list
    while (temp.next) {
        temp = temp.next;
    }

    // Connect the end of the rotated list to the original head to complete the rotation
    temp.next = dummy;

    // Traverse to the end of the rotated list and set the next to null to avoid cycles
    while (temp.next != head) {
        temp = temp.next;
    }
    temp.next = null;

    // Return the head of the rotated list
    return head;
};

/* LRUCache - Design a data structure that follows the constraints of a Least Recently Used(LRU) cache.
 */
class DLLNode {
    constructor(key, value) {
        this.key = key; // Node's key
        this.value = value; // Node's value
        this.prev = null; // Reference to the previous node
        this.next = null; // Reference to the next node
    }
}

// Define a class for the LRU cache
class LRUCache {
    // Constructor initializes the cache with a specified capacity
    constructor(capacity) {
        this.capacity = capacity; // Maximum number of elements the cache can hold
        this.cache = new Map(); // Map to store key-value pairs
        this.head = new DLLNode(); // Dummy head node for the doubly linked list
        this.tail = new DLLNode(); // Dummy tail node for the doubly linked list
        this.head.next = this.tail; // Connect head's next to tail
        this.tail.prev = this.head; // Connect tail's prev to head
    }

    // Helper function to remove a node from the doubly linked list
    removeNode(node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    // Helper function to add a node to the front of the doubly linked list
    addToFront(node) {
        node.next = this.head.next;
        node.prev = this.head;
        this.head.next.prev = node;
        this.head.next = node;
    }

    // Retrieve a value from the cache
    get(key) {
        if (this.cache.has(key)) {
            const node = this.cache.get(key);
            // Move the accessed node to the front of the doubly linked list
            this.removeNode(node);
            this.addToFront(node);
            return node.value;
        } else {
            // Key not found in the cache
            return -1;
        }
    }

    // Add or update a key-value pair in the cache
    put(key, value) {
        if (this.cache.has(key)) {
            // If the key already exists, update its value
            const node = this.cache.get(key);
            node.value = value;
            // Move the updated node to the front of the doubly linked list
            this.removeNode(node);
            this.addToFront(node);
        } else {
            // If the key doesn't exist
            if (this.cache.size >= this.capacity) {
                // If the cache is at its capacity, remove the least recently used node
                const lastNode = this.tail.prev;
                this.removeNode(lastNode);
                this.cache.delete(lastNode.key);
            }

            // Create a new node with the specified key and value
            const newNode = new DLLNode(key, value);
            // Add the new node to the front of the doubly linked list
            this.addToFront(newNode);
            // Add the new node to the cache
            this.cache.set(key, newNode);
        }
    }
}

/* Reorder List - You are given the head of a singly linked-list. The list can be represented as:

L0 → L1 → … → Ln - 1 → Ln
Reorder the list to be on the following form:

L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …
You may not modify the values in the list's nodes. Only nodes themselves may be changed.
 */
/**
 * Definition for singly-linked list.
 * function ListNode(val, next) {
 *     this.val = (val===undefined ? 0 : val)
 *     this.next = (next===undefined ? null : next)
 * }
 */
/**
 * @param {ListNode} head
 * @return {void} Do not return anything, modify head in-place instead.
 */
const reorderList = (head) => {

    /* pseudo code
        find the slow pointer pointing to second half of list
        reverse the second half
        
    */

    let slow = head
    let fast = head
    while (fast.next && fast.next.next) {
        slow = slow.next
        fast = fast.next.next
    }

    let prev = null
    let cur = slow.next
    while (cur) {
        let temp = cur.next
        cur.next = prev
        prev = cur
        cur = temp
    }

    slow.next = null

    let h1 = head
    let h2 = prev

    while (h2) {
        let temp = h1.next
        h1.next = h2
        h1 = h2
        h2 = temp
    }
};