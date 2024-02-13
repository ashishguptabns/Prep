/* Valid parentheses - Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.
 */
/**
 * @param {string} s
 * @return {boolean}
 */
const isValid = (s) => {

    /* pseudo code
        move through chars of s
            found a closing bracket
                check if top of stack is opposite bracket
            found an opening bracket
                push to the stack
    */

    const stack = []
    const bracketMap = {
        ')': '(',
        '}': '{',
        ']': '['
    };

    for (const currentBracket of s) {
        if (bracketMap[currentBracket]) {
            const topElement = stack.length === 0 ? '#' : stack.pop();
            if (bracketMap[currentBracket] !== topElement) {
                return false;
            }
        } else {
            stack.push(currentBracket);
        }
    }

    return stack.length === 0
};

/* Remove All Adjacent Duplicates in String  - You are given a string s and an integer k, a k duplicate removal consists of choosing k adjacent and equal letters from s and removing them, causing the left and the right side of the deleted substring to concatenate together.

We repeatedly make k duplicate removals on s until we no longer can.

Return the final string after all such duplicate removals have been made. It is guaranteed that the answer is unique.
 */
/**
 * @param {string} s
 * @param {number} k
 * @return {string}
 */
const removeDuplicates = (s, k) => {

    /* pseudo code
        keep a stack
            each element is [char, freq]
        move though chars of s
            check if found a duplicate
                increase the freq of top of stack
                freq matches k
                    remove the top of stack
            else keep pushing to the stack
        go through the stack and recreate the string
    */

    if (s.length < k) {
        return s
    }

    const stack = []

    for (const char of s) {
        if (stack.length && char === stack.at(-1)[0]) {
            stack.at(-1)[1]++

            if (stack.at(-1)[1] === k) {
                stack.pop()
            }
        } else {
            stack.push([char, 1])
        }
    }

    let res = ''
    for (const [char, count] of stack) {
        res += char.repeat(count)
    }

    return res
};

/* Minimum add to make parentheses valid - find the minimum number of parentheses to add to make a given string of parentheses valid
 */
const minAddToMakeValid = (s) => {

    /* pseudo code
        move through chars of s
            opening bracket
                move to stack
            closing bracket
                top of stack is opening bracket
                    pop from stack
                else
                    push to stack
        return length of stack
    */

    const stack = []

    for (const char of s) {
        if (char === '(') {
            stack.push(char)
        } else {
            if (stack.length > 0 && stack.at(-1) === '(') {
                stack.pop()
            } else {
                stack.push(char)
            }
        }
    }

    return stack.length
};

/* Min remove to make valid parentheses - Given a string s of '(' , ')' and lowercase English characters.

Your task is to remove the minimum number of parentheses ( '(' or ')', in any positions ) so that the resulting parentheses string is valid and return any valid string.

Formally, a parentheses string is valid if and only if:

It is the empty string, contains only lowercase characters, or
It can be written as AB (A concatenated with B), where A and B are valid strings, or
It can be written as (A), where A is a valid string.
 */

/**
 * @param {string} s
 * @return {string}
 */
const minRemoveToMakeValid = (s) => {

    /* pseudo code
        split the s into char array to make in place changes
        keep a stack to track indices of invalid brackets
        move through chars of s
            opening bracket
                push to stack
            closing bracket
                opening brackets in stack
                    pop from stack
                make current item blank to remove invalid bracket
        move through stack
            find the indices and make them blank
        join the s and return
    */

    s = s.split('')

    const stack = []

    for (let i = 0; i < s.length; i++) {
        if (s[i] === '(') {
            stack.push(i)
        } else if (s[i] === ')') {
            if (stack.length) {
                stack.pop()
            } else {
                s[i] = ''
            }
        }
    }

    for (let position of stack) {
        s[position] = ''
    }

    return s.join('')
};

/* Daily Temperatures - Given an array of integers temperatures represents the daily temperatures, return an array answer such that answer[i] is the number of days you have to wait after the ith day to get a warmer temperature. If there is no future day for which this is possible, keep answer[i] == 0 instead.
 */
/**
 * @param {number[]} temperatures
 * @return {number[]}
 */
const dailyTemperatures = (temps) => {

    /* pseudo code
        keep a stack to store indices of days for which we have to find warmer days
        move i through temps array
            loop through stack till we find a warmer temp than ith temp
                pop the cold index
                store num days for this cold index
            keep pushing i in the stack
    */

    const stack = []
    const answer = Array(temps.length).fill(0)

    for (let i = 0; i < temps.length; i++) {
        while (stack.length && temps[stack.at(-1)] < temps[i]) {
            const coldIndex = stack.pop()
            answer[coldIndex] = i - coldIndex
        }
        stack.push(i)
    }

    return answer
};

/* Online Stock Span - Design an algorithm that collects daily price quotes for some stock and returns the span of that stock's price for the current day.

The span of the stock's price in one day is the maximum number of consecutive days (starting from that day and going backward) for which the stock price was less than or equal to the price of that day.
 */
class StockSpanner {

    /* pseudo code
        keep a stack
            item is [price, span]
        loop through the stack till we find a greater price than curr price
            pop the item
            track num days
        push the price and span to the stack
        return span
    */

    stack = []
    next = (price) => {
        const { stack } = this

        let days = 1

        while (stack.length && stack.at(-1).price <= price) {
            days += stack.pop().days
        }

        stack.push({ price, days })
        return days
    }
}

/* Asteroid Collision - We are given an array asteroids of integers representing asteroids in a row.

For each asteroid, the absolute value represents its size, and the sign represents its direction (positive meaning right, negative meaning left). Each asteroid moves at the same speed.

Find out the state of the asteroids after all collisions. If two asteroids meet, the smaller one will explode. If both are the same size, both will explode. Two asteroids moving in the same direction will never meet.
 */

/**
 * @param {number[]} asteroids
 * @return {number[]}
 */
const asteroidCollision = (asteroids) => {

    /* pseudo code
        move through given asteroids
            negative speed
                loop through stack to find lighter asteroids moving right
                    keep popping
                found an equal weight asteroid moving to right
                    kill it and pop from stack
                found a heavier asteroid in the stack
                    ignore
            keep pushing to stack as current asteroid survives
        return left asteroids in the stack
    */

    const stack = []

    for (const a of asteroids) {
        if (a < 0) {
            while (stack.length > 0 && stack.at(-1) > 0 && stack.at(-1) < -a) {
                stack.pop()
            }

            if (stack.length > 0 && stack.at(-1) + a === 0) {
                stack.pop()
                continue
            }

            if (stack.length > 0 && stack.at(-1) > 0 && stack.at(-1) > -a) {
                continue
            }
        }

        stack.push(a)
    }

    return stack
};

/* Decode String - Given an encoded string, return its decoded string.

The encoding rule is: k[encoded_string], where the encoded_string inside the square brackets is being repeated exactly k times. Note that k is guaranteed to be a positive integer.
 */
/**
 * @param {string} s
 * @return {string}
 */
const decodeString = (s) => {

    /* pseudo code
        move thorugh chars of s
            not a closing bracket
                push to stack
            closing bracket
                pop from stack till opening bracket and make the string
                pop from stack till the top element is a num
            decode the string and push back to the stack for further travel
        join all elements of the stack
    */

    const stack = []
    for (const char of s) {
        if (char !== ']') {
            stack.push(char)
            continue
        }

        let stackTop = stack.pop()
        let currStr = ''

        while (stackTop !== '[') {
            currStr = stackTop + currStr
            stackTop = stack.pop()
        }
        stackTop = stack.pop()

        let num = ''
        while (!Number.isNaN(Number(stackTop))) {
            num = stackTop + num
            stackTop = stack.pop()
        }

        stack.push(stackTop)

        stack.push(currStr.repeat(Number(num)))
    }

    return stack.join('')
};

/* Next Greater Element - Given a circular integer array nums (i.e., the next element of nums[nums.length - 1] is nums[0]), return the next greater number for every element in nums.

The next greater number of a number x is the first greater number to its traversing-order next in the array, which means you could search circularly to find its next greater number. If it doesn't exist, return -1 for this number.
 */
/**
 * @param {number[]} nums
 * @return {number[]}
 */
const nextGreaterElements = (nums) => {

    /* pseudo code
        keep a stack to track next greater element's index
        move i through twice the nums size
            find the curr index
            keep removing the items from stack till top element is greater than curr item
            stack is empty
                assign -1
            else
                found greater element for curr index
            push curr index to stack
    */

    const res = []
    const stack = []

    for (let i = 2 * nums.length - 1; i >= 0; i--) {
        const currIndex = i % nums.length
        while (stack.length && nums[stack.at(-1)] <= nums[currIndex]) {
            stack.pop()
        }

        if (stack.length === 0) {
            res[currIndex] = -1
        } else {
            res[currIndex] = nums[stack.at(-1)]
        }

        stack.push(currIndex)
    }

    return res
};

/* Sum of Subarray Minimums - Given an array of integers arr, find the sum of min(b), where b ranges over every (contiguous) subarray of arr. Since the answer may be large, return the answer modulo 10**9 + 7.
 */
/**
 * @param {number[]} arr
 * @return {number}
 */
const sumSubarrayMins = (arr) => {
    const mod = 10 ** 9 + 7;
    const stack = [];
    const n = arr.length;
    const left = new Array(n);
    const right = new Array(n);

    for (let i = 0; i < n; i++) {
        while (stack.length > 0 && arr[i] <= arr[stack.at(-1)]) {
            stack.pop();
        }
        left[i] = stack.length === 0 ? -1 : stack.at(-1);
        stack.push(i);
    }

    stack.length = 0;

    for (let i = n - 1; i >= 0; i--) {
        while (stack.length > 0 && arr[i] < arr[stack.at(-1)]) {
            stack.pop();
        }
        right[i] = stack.length === 0 ? n : stack.at(-1);
        stack.push(i);
    }

    let result = 0;

    for (let i = 0; i < n; i++) {
        result = (result + arr[i] * (i - left[i]) * (right[i] - i)) % mod;
    }

    return result;
}

/* Min stack - Design a stack that supports push, pop, top, and retrieving the minimum element in constant time.

Implement the MinStack class:

MinStack() initializes the stack object.
void push(int val) pushes the element val onto the stack.
void pop() removes the element on the top of the stack.
int top() gets the top element of the stack.
int getMin() retrieves the minimum element in the stack.
You must implement a solution with O(1) time complexity for each function.
 */