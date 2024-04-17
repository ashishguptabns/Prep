/* Find k length substrings without repeat characters -
 */
/**
 * @param {string} s
 * @param {number} k
 * @return {string[]}
 */
const findSubstrings = (s, k) => {
    const hasRepeatCharacters = (str) => {
        const set = new Set();

        for (let char of str) {
            if (set.has(char)) {
                return true; // Repeat character found
            }
            set.add(char);
        }

        return false; // No repeat characters
    }

    const result = [];

    for (let i = 0; i <= s.length - k; i++) {
        const substring = s.substring(i, i + k);

        if (!hasRepeatCharacters(substring)) {
            result.push(substring);
        }
    }

    return result;
};

/* Fruit Into Baskets - You are visiting a farm that has a single row of fruit trees arranged from left to right.The trees are represented by an integer array fruits where fruits[i] is the type of fruit the ith tree produces.

You want to collect as much fruit as possible.However, the owner has some strict rules that you must follow:

You only have two baskets, and each basket can only hold a single type of fruit.There is no limit on the amount of fruit each basket can hold.
Starting from any tree of your choice, you must pick exactly one fruit from every tree(including the start tree) while moving to the right.The picked fruits must fit in one of your baskets.
Once you reach a tree with fruit that cannot fit in your baskets, you must stop.
Given the integer array fruits, return the maximum number of fruits you can pick. 
*/

/**
 * @param {number[]} fruits
 * @return {number}
 */
const totalFruit = (fruits) => {
    /* pseudo code
    we can choose max 2 groups of fruits and can have unlimited fruits in each group 
    move through the array as we have to pick a fruit from each tree
        keep collecting the fruits
        maintain num groups
            remove a fruit from the left most group
            decrement fruits count
            remove the group if no fruits are left
            shrink the window from left
    */

    let left = 0
    let right = 0
    let totalNum = 0

    const map = {}

    while (right < fruits.length) {
        map[fruits[right]] = (map[fruits[right]] || 0) + 1

        totalNum++

        if (Object.keys(map).length > 2) {
            map[fruits[left]] = map[fruits[left]] - 1
            totalNum--
            if (map[fruits[left]] === 0) {
                delete map[fruits[left]]
            }
            left++
        }
        right++
    }

    return totalNum
};

/* Longest Subarray of 1's After Deleting One Element - Given a binary array nums, you should delete one element from it.

Return the size of the longest non-empty subarray containing only 1's in the resulting array. Return 0 if there is no such subarray.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const longestSubarray = (nums) => {
    /* pseudo code
        move through the arr
            track count of 0s
            if zero count is more than 1
                keep shrinking the window from left
                decrease 0 count if the left most element is a 0
            keep tracking the max length of sub array
    */

    let left = 0
    let right = 0
    let zeroCount = 0

    let maxLength = 0

    //  move from left to right
    while (right < nums.length) {
        if (nums[right] == 0) {
            //  track num 0s
            zeroCount++
        }

        //  keep max one 0
        while (zeroCount > 1) {
            if (nums[left] === 0) {
                zeroCount--
            }
            //  shrink the window
            left++
        }

        //  record window size
        maxLength = Math.max(maxLength, right - left)

        right++
    }

    return maxLength
};

/* Longest substring without repeating characters - Given a string s, find the length of the longest substring without repeating characters.
 */
/**
 * @param {string} s
 * @return {number}
 */
const lengthOfLongestSubstring = (s) => {

    /* pseudo code
        move through the array of chars
            maintain a map to keep last index of a char
            if a repeat is found then discard a part of the substring 
                update substring start index - either curr substring left or last occurence of char
            keep tracking max length
    */

    let maxLength = 0
    const charIndexMap = {}
    let subStringLeftIndex = 0

    for (j = 0; j < s.length; j++) {
        const currChar = s[j]
        if (charIndexMap[currChar] !== undefined) {
            //  found a repeat character hence discard the left subarray
            subStringLeftIndex = Math.max(charIndexMap[currChar] + 1, subStringLeftIndex)
        }

        charIndexMap[currChar] = j

        maxLength = Math.max(maxLength, j - subStringLeftIndex + 1)
    }

    return maxLength
};

/* Number of Sub-arrays of Size K and Average Greater than or Equal to Threshold - Given an array of integers arr and two integers k and threshold, return the number of sub-arrays of size k and average greater than or equal to threshold.
 */
/**
 * @param {number[]} arr
 * @param {number} k
 * @param {number} threshold
 * @return {number}
 */
const numOfSubarrays = (arr, k, threshold) => {

    /* pseudo code
        find the sum of k length subarray
        move i from k through arr
            avg is as per condition
                maintain count
            shrink window from left and increase on right
                maintain sum
    */

    let count = 0
    let sum = 0

    for (let i = 0; i < k; i++) {
        sum += arr[i]
    }

    //  continue for further sub arrays
    for (let i = k; i <= arr.length; i++) {

        //  avg of current subarray
        const avg = sum / k

        if (avg >= threshold) {
            count++
        }

        //  remove the first element of this subarray
        sum -= arr[i - k]
        //  add the next
        sum += arr[i]
    }

    return count
};

/* Max consecutive Ones III - Given a binary array nums and an integer k, return the maximum number of consecutive 1's in the array if you can flip at most k 0's.
 */

/**
* @param {number[]} nums
* @param {number} k
* @return {number}
*/
const longestOnes = (nums, k) => {
    let max = 0
    let numZeroes = 0
    let start = 0
    for (let i = 0; i < nums.length; i++) {
        const num = nums[i]
        if (num !== 1) {
            numZeroes++
        }
        while (numZeroes > k) {
            if (nums[start] === 0) {
                numZeroes--
            }
            start++
        }
        max = Math.max(max, i + 1 - start)
    }

    return max
};

/* Sequential Digits - An integer has sequential digits if and only if each digit in the number is one more than the previous digit.

Return a sorted list of all the integers in the range [low, high] inclusive that have sequential digits.
 */

/**
 * @param {number} low
 * @param {number} high
 * @return {number[]}
 */
const sequentialDigits = (low, high) => {

    /* pseudo code
        run a size loop from minLen to maxLen
            run i through digits string
                take a substring from digits of length size from i
                compare and push to ans array
    */

    const digits = '123456789'
    const ans = []

    const minLen = low.toString().length
    const maxLen = high.toString().length

    for (let size = minLen; size <= maxLen; ++size) {
        for (let i = 0; i + size <= digits.length; ++i) {
            const num = parseInt(digits.substring(i, i + size))

            if (num >= low && num <= high) {
                ans.push(num)
            }
        }
    }

    return ans
};

/* Min recolors to get k consectuive black blocks - You are given a 0-indexed string blocks of length n, where blocks[i] is either 'W' or 'B', representing the color of the ith block. The characters 'W' and 'B' denote the colors white and black, respectively.

You are also given an integer k, which is the desired number of consecutive black blocks.

In one operation, you can recolor a white block such that it becomes a black block.

Return the minimum number of operations needed such that there is at least one occurrence of k consecutive black blocks.
 */

/**
 * @param {string} blocks
 * @param {number} k
 * @return {number}
 */
const minimumRecolors = (blocks, k) => {
    /* pseudo code
        go through the blocks array
            track number of whites
            when found a subarray of length k
                track min whites so far
                shrink the window from left
                    numWhites also if possible
    */

    let min = Infinity
    let start = 0
    let numWhites = 0
    for (let end = 0; end < blocks.length; end++) {
        if (blocks[end] === 'W') {
            numWhites++
        }
        if (end - start + 1 === k) {
            //  found the window
            min = Math.min(numWhites, min)

            //  shrink window from left
            if (blocks[start++] === 'W') {
                //  remove the count
                numWhites--
            }
        }
    }

    return min
};

/* Max num of vowels in a substring of given length - Given a string s and an integer k, return the maximum number of vowel letters in any substring of s with length k.
 */

/**
 * @param {string} s
 * @param {number} k
 * @return {number}
 */
var maxVowels = function (s, k) {
    const vowels = ['a', 'e', 'i', 'o', 'u']
    let max = 0
    let start = 0
    let count = 0
    for (let i = 0; i < s.length; i++) {
        const c = s[i]
        if (vowels.includes(c)) {
            count++
        }
        max = Math.max(count, max)

        if (i - start + 1 === k) {
            if (vowels.includes(s[start])) {
                count--
            }
            start++
        }
    }

    return max
};

/* Longest Continuous Subarray With Absolute Diff Less Than or Equal to Limit - Given an array of integers nums and an integer limit, return the size of the longest non-empty subarray such that the absolute difference between any two elements of this subarray is less than or equal to limit.
*/

/**
 * @param {number[]} nums
 * @param {number} limit
 * @return {number}
 */
const longestCSubarray = (nums, limit) => {

    /* pseudo code
        go through nums array
            track max and min in current window
            keep a queue to represent curr window
            if diff crosses limit
                shrink window from left - remove first item of queue
                update max and min accordingly
    */

    const queue = [];

    let left = -1;
    let max = min = nums[0];

    for (const num of nums) {
        max = Math.max(max, num);
        min = Math.min(min, num);

        queue.push(num);

        if (max - min <= limit) {
            continue;
        }

        const cut = queue.shift();
        left += 1;

        max === cut && (max = Math.max(...queue));
        min === cut && (min = Math.min(...queue));
    }

    return nums.length - left - 1;
};

/* Sliding window maximum - You are given an array of integers nums, there is a sliding window of size k which is moving from the very left of the array to the very right. You can only see the k numbers in the window. Each time the sliding window moves right by one position.

Return the max sliding window.
 */

const maxSlidingWindow = (nums, k) => {

    /* pseudo code
        keep a queue to store indices
        move through the array
            maintain the max index of curr window using queue
            keep shrinking the window and remove item from queue
            keep pushing max of current window in res array
    */

    const q = [];
    const res = [];
    for (let i = 0; i < nums.length; i++) {
        while (q && nums[q.at(-1)] <= nums[i]) {
            q.pop();
        }
        q.push(i);
        if (q[0] === i - k) {
            q.shift();
        }
        if (i >= k - 1) {
            res.push(nums[q[0]]);
        }
    }
    return res;
};

/* Maximum Length of Subarray With Positive Product - Given an array of integers nums, find the maximum length of a subarray where the product of all its elements is positive.

A subarray of an array is a consecutive sequence of zero or more values taken out of that array.

Return the maximum length of a subarray with positive product.
*/

const getMaxLen = (nums) => {
    let [res, pos, neg] = [0, 0, 0];

    for (const num of nums) {
        if (num === 0) {
            pos = neg = 0;
        }
        else if (num > 0) {
            pos += 1;
            neg && (neg += 1);
        }
        else {
            const current = pos;
            pos = neg ? neg + 1 : 0;
            neg = current + 1;
        }

        res = Math.max(res, pos);
    }

    return res;
};

/* Find All Anagrams in a String - Given two strings s and p, return an array of all the start indices of p's anagrams in s. You may return the answer in any order.
 */
/**
 * @param {string} s
 * @param {string} p
 * @return {number[]}
 */
const findAnagrams = (s, p) => {
    const targetCharArray = Array(26).fill(0);

    for (const char of p) {
        targetCharArray[char.charCodeAt(0) - 'a'.charCodeAt(0)]++;
    }

    const windowArr = Array(26).fill(0);

    let left = 0;
    let right = 0;

    const resultList = [];

    const arraysAreEqual = (arr1, arr2) => {
        for (let i = 0; i < arr1.length; i++) {
            if (arr1[i] !== arr2[i]) {
                return false;
            }
        }
        return true;
    };

    while (right < s.length) {
        const currChar = s[right];
        windowArr[currChar.charCodeAt(0) - 'a'.charCodeAt(0)]++;

        if (right - left + 1 === p.length) {
            if (arraysAreEqual(windowArr, targetCharArray)) {
                resultList.push(left);
            }

            const charToDelete = s[left];
            windowArr[charToDelete.charCodeAt(0) - 'a'.charCodeAt(0)]--;
            left++;
        }

        right++;
    }

    return resultList;
};

/* Longest repeating character replacement - You are given a string s and an integer k. You can choose any character of the string and change it to any other uppercase English character. You can perform this operation at most k times.

Return the length of the longest substring containing the same letter you can get after performing the above operations.
 */

/**
 * @param {string} s
 * @param {number} k
 * @return {number}
 */
const characterReplacement = (s, k) => {

    /* pseudo code
        go through the string chars
            maintain frequency of the char in curr window
            track max count of a char in current window
            if max char count with k exceeds window size
                shrink window from left and maintain freq of left most char
    */

    const map = {}
    let maxCount = 0
    let start = 0
    let maxLen = 0
    for (let i = 0; i < s.length; i++) {
        const c = s[i]
        map[c] = (map[c] || 0) + 1
        maxCount = Math.max(maxCount, map[c])
        if (i + 1 - start - maxCount > k) {
            map[s[start]]--
            start++
        }
        maxLen = Math.max(maxLen, i + 1 - start)
    }

    return maxLen
};

/* Longest Substring with At Least K Repeating Characters - Given a string s and an integer k, return the length of the longest substring of s such that the frequency of each character in this substring is greater than or equal to k.

if no such substring exists, return 0. */

var longestSubstring = function (s, k) {
    if (s.length < k) {
        return 0;
    }

    const charCount = new Map();

    for (const char of s) {
        if (!charCount.has(char)) {
            charCount.set(char, 0);
        }
        charCount.set(char, charCount.get(char) + 1);
    }
    for (const [char, count] of charCount) {
        if (count < k) {
            const substrings = s.split(char);
            console.log(substrings)
            let maxLength = 0;

            for (const substr of substrings) {
                maxLength = Math.max(maxLength, longestSubstring(substr, k));
            }

            return maxLength;
        }
    }

    return s.length;
};

/* Minimum Swaps to Group All 1's Together II - A swap is defined as taking two distinct positions in an array and swapping the values in them.

A circular array is defined as an array where we consider the first element and the last element to be adjacent.

Given a binary circular array nums, return the minimum number of swaps required to group all 1's present in the array together at any location. */

const minSwaps2 = (nums) => {
    let ones = 0
    for (const num of nums) {
        num && ones++
    }

    let maxOnesInWindow = 0
    for (let i = 0; i < ones; i++) {
        nums[i] && maxOnesInWindow++
    }

    let curr = maxOnesInWindow

    for (let i = ones; i < ones + nums.length - 1; i++) {
        if (nums[i - ones] === 1) {
            curr--
        }
        if (nums[i % nums.length] === 1) {
            curr++
        }

        maxOnesInWindow = Math.max(maxOnesInWindow, curr)
    }

    return ones - maxOnesInWindow
};

/* Maximize the confusion in exam - A teacher is writing a test with n true/false questions, with 'T' denoting true and 'F' denoting false. He wants to confuse the students by maximizing the number of consecutive questions with the same answer (multiple trues or multiple falses in a row).

You are given a string answerKey, where answerKey[i] is the original answer to the ith question. In addition, you are given an integer k, the maximum number of times you may perform the following operation:

Change the answer key for any question to 'T' or 'F' (i.e., set answerKey[i] to 'T' or 'F').
Return the maximum number of consecutive 'T's or 'F's in the answer key after performing the operation at most k times.
 */

/**
 * @param {string} answerKey
 * @param {number} k
 * @return {number}
 */
const maxConsecutiveAnswers = (arr, k) => {

    const find = (char) => {
        let left = 0, charCount = 0, max = 0

        for (let r = 0; r < arr.length; r++) {
            if (arr[r] === char) {
                charCount++
            }

            while (charCount > k) {
                if (arr[left] === char) {
                    charCount--
                }
                left++
            }
            max = Math.max(max, r + 1 - left)
        }

        return max
    }

    return Math.max(find('T'), find('F'))
};

/* 1358. Number of Substrings Containing All Three Characters */

/**
 * @param {string} s
 * @return {number}
 */
var numberOfSubstrings = function (str) {
    let ans = 0
    let [a, b, c] = [0, 0, 0]

    for (let i = 0, j = 0; i < str.length; i++) {
        const curr = str[i]
        if (curr === 'a') {
            a++
        } else if (curr === 'b') {
            b++
        } else {
            c++
        }
        while (a && b && c) {
            ans += str.length - i
            if (str[j] === 'a') {
                a--
            } else if (str[j] === 'b') {
                b--
            } else {
                c--
            }
            j++
        }
    }

    return ans
};

/* 1248. Count Number of Nice Subarrays */

var numberOfSubarrays = function (nums, k) {
    let countOdd = 0;
    let l = 0;
    let niceSubarray = 0;
    let result = 0;
    for (let r = 0; r < nums.length; r++) {
        if (nums[r] % 2 !== 0) {
            countOdd++;
        }
        if (countOdd === k) {
            niceSubarray = 0;
        }
        while (countOdd >= k) {
            if (nums[l] % 2 !== 0) {
                countOdd--;
            }
            l++;
            niceSubarray++;
        }
        result += niceSubarray;
    }
    return result;
};

/* 1031. Maximum Sum of Two Non-Overlapping Subarrays */

/* 1052. Grumpy Bookstore Owner */

/* Minimum Swaps to Group All 1's Together 
    - You're given a binary array data (containing just 0s and 1s).
    - Your goal is to minimize the number of swaps needed to bring all the 1s together in any part of the array.
*/
const minSwaps = (arr) => {
    const countOnes = arr.reduce((acc, val) => acc + val, 0);

    if (countOnes <= 1) {
        return 0;
    }

    let windowSize = countOnes;
    let onesInWindow = arr
        .slice(0, windowSize)
        .reduce((acc, val) => acc + val, 0);

    let minSwaps = countOnes - onesInWindow;

    for (let i = windowSize; i < arr.length; i++) {
        onesInWindow = onesInWindow + arr[i] - arr[i - windowSize];
        minSwaps = Math.min(minSwaps, countOnes - onesInWindow);
    }

    return minSwaps;
};

