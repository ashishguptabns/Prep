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

    let curr = head
    const check = (node) => {
        if (node) {
            const res = check(node.next) && node.val === curr.val
            if (!res) {
                return false
            }
            curr = curr.next
        }
        return true
    }
    return check(head)
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
        return dummy's next 
    */

    const minHeap = new MinPriorityQueue({ priority: node => node.val })

    for (const head of lists) {
        head && minHeap.enqueue(head)
    }

    let result = new ListNode()
    const head = result

    while (!minHeap.isEmpty()) {
        const { val, next } = minHeap.dequeue().element
        result.next = new ListNode(val)
        result = result.next

        if (next) {
            minHeap.enqueue(next)
        }
    }

    return head.next
};

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
        node.val = node.next.val
        node.next = node.next.next
    }
};

/* Swap nodes in pairs - Given a linked list, swap every two adjacent nodes and return its head. You must solve the problem without modifying the values in the list's nodes (i.e., only nodes themselves may be changed.)
 */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
const swapPairs = (head) => {

    /* pseudo code
        have a temp node to track head    
        move through the list - start from temp
            do the swapping
            move curr to the node before two nodes which will be swapped
        return next of temp
    */

    if (head && head.next) {

        const temp = new ListNode(0)
        temp.next = head

        let curr = temp
        while (curr.next && curr.next.next) {
            let node1 = curr.next
            let node2 = curr.next.next

            curr.next = node2
            node1.next = node2.next
            node2.next = node1

            curr = curr.next.next
        }
        return temp.next
    }
    return head
};

/* LRUCache - Design a data structure that follows the constraints of a Least Recently Used(LRU) cache.
 */
var LRUCache = function (capacity) {
    this.cache = {}
    this.max = capacity
    this.head = {}
    this.tail = {}
    this.head.next = this.tail
    this.tail.prev = this.head
    this.size = 0
};

LRUCache.prototype.get = function (key) {
    if (this.cache[key]) {
        this.removeNode(this.cache[key])
        this.moveToFront(this.cache[key])
        return this.cache[key].val
    }
    return -1
};

LRUCache.prototype.moveToFront = function (node) {
    node.next = this.head.next
    node.prev = this.head
    this.head.next.prev = node
    this.head.next = node
}

LRUCache.prototype.removeNode = function (node) {
    node.next.prev = node.prev
    node.prev.next = node.next
}

LRUCache.prototype.put = function (key, value) {
    if (this.cache[key]) {
        const node = this.cache[key]
        node.val = value
        this.removeNode(node)
        this.moveToFront(node)
    } else {
        if (this.isFull()) {
            const lastNode = this.tail.prev
            this.removeNode(lastNode)
            delete this.cache[lastNode.key]
        } else {
            this.size++
        }
        const node = { key: key, val: value }
        this.moveToFront(node)
        this.cache[key] = node
    }
};

LRUCache.prototype.isFull = function () {
    return this.size >= this.max
}

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
        oddNode.next = evenNode.next
        oddNode = oddNode.next

        evenNode.next = oddNode.next
        evenNode = evenNode.next
    }
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
            return prev
        }

        curr.prev = prev
        prev.next = curr

        const tempNext = curr.next
        const tail = flattenDFS(curr, curr.child)
        curr.child = null

        return flattenDFS(tail, tempNext)
    }

    if (!head) {
        return head
    }

    const temp = new Node(-1)
    temp.next = head
    flattenDFS(temp, head)

    temp.next.prev = null

    return temp.next
};

/* 142. Linked List Cycle II - Given the head of a linked list, return the node where the cycle begins. If there is no cycle, return null. */
/**
 * @param {ListNode} head
 * @return {ListNode}
 */
const detectCycle = (head) => {

    /* pseudo code
        take a slow pointer and a fast pointer moving with double speed
            metNode is where these two pointers meet
        if fastNode has null as next - no cycle exists
        start two pointers; one from head another from meetPoint and where these two meet is the start of cycle 
     */

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
            if (Array.isArray(element)) {
                totalSum += dfs(element, depth + 1);
            } else {
                totalSum += element * depth;
            }
        }
        return totalSum;
    }
    return dfs(nestedList, 1);
}

/* Spiral matrix - You are given two integers m and n, which represent the dimensions of a matrix.

You are also given the head of a linked list of integers.

Generate an m x n matrix that contains the integers in the linked list presented in spiral order (clockwise), starting from the top-left of the matrix. If there are remaining empty spaces, fill them with -1.

Return the generated matrix.
 */
const spiralMatrix = (m, n, head) => {

    /* pseudo code
        keep curr row and col
        maintain direction of movement in the matrix
    */

    let currRow = 0
    let currCol = 0

    let top = 1, right = n - 1, bottom = m - 1, left = 0

    let direction = 'r'

    const mat = Array(m).fill().map(() => Array(n).fill(-1))

    while (head) {
        mat[currRow][currCol] = head.val

        if (currCol === right && direction === 'r') {
            right--
            direction = 'd'
        }
        if (currRow === bottom && direction === 'd') {
            bottom--
            direction = 'l'
        }
        if (currCol === left && direction === 'l') {
            left++
            direction = 'u'
        }
        if (currRow === top && direction === 'u') {
            top++
            direction = 'r'
        }

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
        return dummy's next 
    */

    let [dummy, currNode] = [head, head]
    let prev = currNode

    while (currNode) {
        if (currNode.val === 0) {
            prev.next = currNode.next
        } else {
            while (currNode.next.val !== 0) {
                currNode.next.val = currNode.next.val + currNode.val

                prev.next = currNode.next
                currNode = currNode.next
            }
            prev = currNode
        }
        currNode = currNode.next
    }

    return dummy.next
};

/* Sort list - Given the head of a linked list, return the list after sorting it in ascending order.
 */
const sortList = (head) => {


    if (!head || !head.next) {
        return head
    }

    let [prev, slow, fast] = [null, head, head]

    while (fast && fast.next) {
        prev = slow
        slow = slow.next
        fast = fast.next.next
    }
    if (prev) {
        prev.next = null
    }

    const first = sortList(head)
    const second = sortList(slow)

    const merge = (left, right) => {
        if (!left) {
            return right
        }
        if (!right) {
            return left
        }
        if (left.val < right.val) {
            left.next = merge(left.next, right)
            return left
        } else {
            right.next = merge(left, right.next)
            return right
        }
    }

    return merge(first, second)
};

/* Rotate list - Given the head of a linked list, rotate the list to the right by k places.
*/
const rotateRight = (head, k) => {

    /* pseudo code

    */

    if (!head) {
        return head;
    }
    let [count, curr] = [0, head]

    while (curr) {
        count++;
        curr = curr.next;
    }

    k = k % count;
    let prev = head;
    curr = head;

    while (k--) {
        curr = curr.next;
    }

    while (curr.next) {
        prev = prev.next;
        curr = curr.next;
    }

    curr.next = head;
    head = prev.next;
    prev.next = null;
    return head;
};

/* Reorder List - You are given the head of a singly linked-list. The list can be represented as:

L0 → L1 → … → Ln - 1 → Ln
Reorder the list to be on the following form:

L0 → Ln → L1 → Ln - 1 → L2 → Ln - 2 → …
You may not modify the values in the list's nodes. Only nodes themselves may be changed.
 */
const reorderList = (head) => {

    /* pseudo code
        find the slow pointer pointing to second half of list
        reverse the second half
            keep prev and curr
            loop till curr is not null
                do the reversal and move prev and curr
        keep h1 and h2 at head of first half and reversed head of second half - prev
    */

    let slow = head
    let fast = head
    while (fast.next && fast.next.next) {
        slow = slow.next
        fast = fast.next.next
    }

    let prev = null
    let curr = slow.next
    while (curr) {
        let temp = curr.next
        curr.next = prev
        prev = curr
        curr = temp
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

/* Remove Nth Node From End of List - Given the head of a linked list, remove the nth node from the end of the list and return its head.
 */
/**
 * @param {ListNode} head
 * @param {number} n
 * @return {ListNode}
 */
const removeNthFromEnd = (head, n) => {

    /* pseudo code
        move fast pointer by k
        move fast and slow till fast reaches then end
        skip the next node of slow
    */

    let fast = head, slow = head;

    for (let i = 0; i < n; i++) {
        fast = fast.next;
    }

    if (!fast) {
        return head.next;
    }

    while (fast.next) {
        fast = fast.next;
        slow = slow.next;
    }

    slow.next = slow.next.next;

    return head;
};

/* Convert Sorted List to Binary Search Tree - Given the head of a singly linked list where elements are sorted in ascending order, convert it to a height-balanced binary search tree. */

/* 1669. Merge In Between Linked Lists */

var mergeInBetween = function (list1, a, b, list2) {
    let [prevA, prevB] = [null, null]

    let curr = list1
    while (a > 0) {
        a--
        prevA = curr
        curr = curr.next
    }

    curr = list1
    while (b >= 0) {
        b--
        prevB = curr
        curr = curr.next
    }

    console.log(prevA, prevB)

    prevA.next = list2

    curr = list2
    while (curr.next) {
        curr = curr.next
    }
    curr.next = prevB.next

    return list1
};

/* 1472. Design Browser History */

/**
 * @param {string} homepage
 */
var BrowserHistory = function (homepage) {
    this.history = [homepage]
    this.curr = 0
    this.limit = 0
};

/** 
 * @param {string} url
 * @return {void}
 */
BrowserHistory.prototype.visit = function (url) {
    this.curr++
    if (this.curr === this.history.length) {
        this.history.push(url)
    } else {
        this.history[this.curr] = url
    }
    this.limit = this.curr
};

/** 
 * @param {number} steps
 * @return {string}
 */
BrowserHistory.prototype.back = function (steps) {
    this.curr = Math.max(0, this.curr - steps)
    return this.history[this.curr]
};

/** 
 * @param {number} steps
 * @return {string}
 */
BrowserHistory.prototype.forward = function (steps) {
    this.curr = Math.min(this.limit, this.curr + steps)
    return this.history[this.curr]
};

/* 1721. Swapping Nodes in a Linked List */

var swapNodes = function (head, k) {
    let first, sec
    const travel = (node, index) => {
        if (node) {
            if (index === k - 1) {
                first = node
            }
            travel(node.next, index + 1)
            k--
            if (k == 0) {
                sec = node
            }
        }
    }
    travel(head, 0)
    const temp = sec.val
    sec.val = first.val
    first.val = temp

    return head
};

/* 2487. Remove Nodes From Linked List */

var removeNodes = function (head) {
    const dummy = new ListNode(-1)
    dummy.next = head
    let max = 0
    const travel = (prev, curr) => {
        if (curr) {
            travel(curr, curr.next)
            max = Math.max(max, curr.val)
            if (max !== curr.val) {
                prev.next = curr.next
            }
        }
    }
    travel(dummy, head)
    return dummy.next
};

/* 725. Split Linked List in Parts */

var splitListToParts = function (head, k) {
    const mainK = k
    let node = head
    let totalLen = 0
    while (node) {
        totalLen++
        node = node.next
    }

    let [prevHead, curr] = [head, head]

    const res = Array(k)
    let currLen = 0
    let currIndex = 0
    while (curr) {
        currLen++
        if (currLen >= Math.ceil(totalLen / k)) {
            res[currIndex++] = prevHead
            prevHead = curr.next
            curr.next = null
            curr = prevHead
            totalLen -= currLen
            currLen = 0
            k--
        } else {
            curr = curr?.next
        }
    }

    while (currIndex < mainK) {
        res[currIndex++] = prevHead
    }

    return res
};

/* 1019. Next Greater Node In Linked List */