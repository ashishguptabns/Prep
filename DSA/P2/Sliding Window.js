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
    we can choose max of 2 groups of fruits and can have unlimited fruits in each group 
    move throught the array
        keep collecting the fruits
        maintain num groups
            remove a fruit the left most group
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
            //  found a new group of fruit

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
                decrease 0 count if the left most element is a 0
                keep shrinking the window from left
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
            //  found a repeat character
            subStringLeftIndex = Math.max(charIndexMap[currChar] + 1, subStringLeftIndex)
        }

        charIndexMap[currChar] = j

        maxLength = Math.max(maxLength, j - subStringLeftIndex + 1)
    }

    return maxLength
};

/* Count Subarrays With Fixed Bounds - You are given an integer array nums and two integers minK and maxK.

A fixed-bound subarray of nums is a subarray that satisfies the following conditions:
The minimum value in the subarray is equal to minK.
The maximum value in the subarray is equal to maxK.
Return the number of fixed-bound subarrays.

A subarray is a contiguous part of an array.
 */
/**
 * @param {number[]} nums
 * @param {number} minK
 * @param {number} maxK
 * @return {number}
 */
const countSubarrays = (nums, minK, maxK) => {
    /* pseudo code
        move through the array
            track index which is out of range
            equal to min
            equal to max
            add the num of subarrays for this range 
    */

    let minPos = -1
    let maxPos = -1

    //  most recent index out of range    
    let startIndex = -1

    let numSubArrays = 0

    for (let i = 0; i < nums.length; i++) {
        //  update out of range index        
        if (nums[i] < minK || nums[i] > maxK) {
            startIndex = i
        }

        //  update recent positions if any
        if (nums[i] === minK) {
            minPos = i
        }
        if (nums[i] === maxK) {
            maxPos = i
        }

        //  number of subarrays is number of elements between startIndex and smaller of two indices
        numSubArrays += Math.max(0, Math.min(maxPos, minPos) - startIndex)
    }

    return numSubArrays
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
    let count = 0
    let sum = 0

    //  find the sum of the first k length subarray
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
const maxConsecutiveAnswers = (answerKey, k) => {

    /* pseudo code
        we will find max length of subseqs having max k Ts or Fs
        run through the array
            keep tracking the count of given char - T or F
            more than k chars
                shrink windown from left
            keep tracking the max window length
        return max length found so far
    */

    const maxConsequtive = (char) => {
        let left = 0, charCount = 0, max = 0
        //  Iterating over the answer key using right pointer
        for (let right = 0; right < answerKey.length; right++) {
            //  currentChar is the char that should be atmost k times
            if (answerKey[right] === char) {
                charCount++
            }

            //  we have more than k chars 
            //  we have to shrink the window
            while (charCount > k) {
                if (answerKey[left] === char) {
                    charCount--
                }
                left++
            }
            //  Current window has atmost k chars
            max = Math.max(max, right - left + 1)
        }
        return max
    }

    return Math.max(maxConsequtive('T'), maxConsequtive('F'))
};

/* Max consecutive Ones - Given a binary array nums and an integer k, return the maximum number of consecutive 1's in the array if you can flip at most k 0's.
 */

/**
* @param {number[]} nums
* @param {number} k
* @return {number}
*/
const longestOnes = (nums, k) => {
    //  use sliding window

    let left = 0, right = 0

    while (right < nums.length) {
        if (nums[right] === 0) {
            //  found a zero hence a flip is used
            k--
        }
        if (k < 0) {
            //  window has to be shrinked from left
            if (nums[left] === 0) {
                //  gained one flip
                k++
            }
            left++
        }
        right++
    }

    return right - left
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
            run another loop with starting points in digits string
                take a substring from digits of length size with different starting points
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
                record the min of numWhites
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
const maxVowels = (s, k) => {

    /* pseudo code
        go through the string
            keep tracking vowel count
            shrink the window from left
    */

    let maxCount = 0, count = 0;
    const vowel = ['a', "e", "i", "o", "u"]
    for (let i = 0; i < s.length; i++) {
        if (vowel.includes(s[i])) {
            //  found a vowel
            count++
        }

        if (i >= k && vowel.includes(s[i - k])) {
            //  removing the vowel from start of substring
            count--
        }

        //  max possible number
        if (count == k) {
            return k;
        }

        //  update max
        if (maxCount < count) {
            maxCount = count
        }
    }
    return maxCount
};

/* Sliding window maximum - You are given an array of integers nums, there is a sliding window of size k which is moving from the very left of the array to the very right. You can only see the k numbers in the window. Each time the sliding window moves right by one position.

Return the max sliding window.
 */

/**
 * @param {number[]} nums
 * @param {number} k
 * @return {number[]}
 */
const maxSlidingWindow = (nums, k) => {

    /* pseudo code
        move through the array
            maintain the max index of curr window using queue
            keep pushing max of current window in res array
    */

    // Result array to store the maximum elements in each sliding window
    const res = [];
    // Variables to represent the left and right pointers of the sliding window
    let left = 0;
    let right = 0;
    // Deque to store indices of elements in the array
    const q = [];

    // Iterate through the array elements using the sliding window approach
    while (right < nums.length) {
        // Remove elements from the back of the deque that are less than the current element
        while (q.length > 0 && nums[right] > nums[q.at(-1)]) {
            q.pop();
        }
        // Add the current index to the deque
        q.push(right);

        // Remove the front element of the deque if it's outside the current sliding window
        if (left > q[0]) {
            q.shift();
        }

        // If the window has reached the size of 'k' or more, add the maximum element to the result array
        if (right + 1 >= k) {
            res.push(nums[q[0]]);
            // Move the left pointer to the next position within the window
            left++;
        }
        // Move the right pointer to the next element in the array
        right++;
    }

    // Return the result array containing maximum elements in each sliding window
    return res;
};

/* Maximum Length of Subarray With Positive Product - Given an array of integers nums, find the maximum length of a subarray where the product of all its elements is positive.

A subarray of an array is a consecutive sequence of zero or more values taken out of that array.

Return the maximum length of a subarray with positive product.
*/

/**
 * @param {number[]} nums
 * @return {number}
 */
const getMaxLen = (nums) => {
    // Initialize variables to keep track of the result and counts of positive and negative numbers
    let result = positive = negative = 0;

    // Iterate through each element in the input array
    for (const num of nums) {
        // Check if the current element is zero
        if (num === 0) {
            // subarray is broken
            // Reset positive and negative counts if the current element is zero
            positive = negative = 0;
        }
        // Check if the current element is positive
        else if (num > 0) {
            // Increment the positive count
            positive += 1;

            // If negative count is non-zero, increment it as well
            negative && (negative += 1);
        }
        // Check if the current element is negative
        else {
            // Swap positive and negative counts, and update negative count
            const current = positive;
            positive = negative ? negative + 1 : 0;
            negative = current + 1;
        }

        // Update the result with the maximum positive count
        result = Math.max(result, positive);
    }

    // Return the maximum length of subarrays with a product of positive integers
    return result;
};

/* Find All Anagrams in a String - Given two strings s and p, return an array of all the start indices of p's anagrams in s. You may return the answer in any order.
 */
/**
 * @param {string} s
 * @param {string} p
 * @return {number[]}
 */
const findAnagrams = (s, p) => {
    // Record characters and count in the small string
    const targetCharArray = Array(26).fill(0);

    // Count the frequency of each character in the pattern 'p'
    for (const char of p) {
        targetCharArray[char.charCodeAt(0) - 'a'.charCodeAt(0)]++;
    }

    // Initialize an array to represent the sliding window's character count
    const windowArr = Array(26).fill(0);

    // Initialize pointers for the sliding window
    let left = 0;
    let right = 0;

    // Array to store the starting indices of anagrams
    const resultList = [];

    // Helper function to check if two arrays are equal
    const arraysAreEqual = (arr1, arr2) => {
        for (let i = 0; i < arr1.length; i++) {
            if (arr1[i] !== arr2[i]) {
                return false;
            }
        }
        return true;
    };

    // Iterate through the characters in the string 's'
    while (right < s.length) {
        // Add the current character to the sliding window
        const currChar = s[right];
        windowArr[currChar.charCodeAt(0) - 'a'.charCodeAt(0)]++;

        // Check if the window size is equal to the length of the pattern 'p'
        if (right - left + 1 === p.length) {
            // Check if the current window is an anagram
            if (arraysAreEqual(windowArr, targetCharArray)) {
                // Found an anagram, push the starting index to the result array
                resultList.push(left);
            }

            // Reduce the count of the leftmost character to move the window forward
            const charToDelete = s[left];
            windowArr[charToDelete.charCodeAt(0) - 'a'.charCodeAt(0)]--;
            left++;
        }

        // Move the right pointer to expand the window
        right++;
    }

    // Return the array of starting indices of anagrams
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
            maintain frequency of the char
            track max count of a char in current window
            if max char count with k exceeds window size
                shrink window from left with freq of left most char
    */

    // Initialize left and right pointers for the sliding window
    let left = 0;
    let right = 0;

    // Initialize the maximum count of a character in the current window
    let maxCharCount = 0;

    // Create a dictionary to keep track of character frequencies in the current window
    const freqMap = {};

    // Iterate through the string using the sliding window
    while (right < s.length) {
        // Current character in the window
        const char = s[right];

        // Update the frequency of the current character in the visited dictionary
        freqMap[char] = freqMap[char] ? freqMap[char] + 1 : 1;

        // Update the maximum count of a character in the current window
        if (freqMap[char] > maxCharCount) {
            maxCharCount = freqMap[char];
        }

        // Check if the length of the window - maximum character count is greater than 'k'
        //  we need to have a single char in the window
        if (right - left + 1 - maxCharCount > k) {
            // Move the left pointer to shrink the window
            freqMap[s[left]]--;  // Reduce the frequency of the character at the left end
            left++;
        }

        // Expand the window to the right
        right++;
    }

    // The length of the longest substring with at most 'k' replacements
    return right - left;
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

    // Initialize an empty queue to store elements of the current subarray.
    const queue = [];

    // Initialize pointers and variables to track the current subarray.
    let left = -1;  // `left` represents the left boundary of the current subarray.
    let max = min = nums[0];  // `max` and `min` represent the maximum and minimum values in the current subarray.

    // Loop through each element in the input array.
    for (const num of nums) {
        // Update the maximum and minimum values in the current subarray.
        max = Math.max(max, num);
        min = Math.min(min, num);

        // Add the current element to the queue.
        queue.push(num);

        // Check if the difference between max and min is within the limit.
        if (max - min <= limit) {
            // If within limit, continue with the next element.
            continue;
        }

        // If the difference exceeds the limit, remove elements from the left of the subarray.
        const cut = queue.shift();  // Remove the leftmost element.
        left += 1;  // Move the left boundary to the right.

        // Update max and min if the removed element was equal to the current max or min.
        max === cut && (max = Math.max(...queue));
        min === cut && (min = Math.min(...queue));
    }

    // Return the length of the longest contiguous subarray.
    return nums.length - left - 1;
};

/* Minimum Swaps to Group All 1's Together 
    - You're given a binary array data (containing just 0s and 1s).
    - Your goal is to minimize the number of swaps needed to bring all the 1s together in any part of the array.
*/
const minSwaps = (arr) => {

    /* pseudo code
      
    */

    // Count the number of 1's in the array
    const countOnes = arr.reduce((acc, val) => acc + val, 0);

    // If there are no 1's or only one 1, no swaps are needed for grouping
    if (countOnes <= 1) {
        return 0;
    }

    let windowSize = countOnes;
    let onesInWindow = arr
        .slice(0, windowSize)
        .reduce((acc, val) => acc + val, 0);

    //  these swaps are needed for the left most window
    let minSwaps = countOnes - onesInWindow;

    // Slide the window through the array and find the minimum number of swaps
    for (let i = windowSize; i < arr.length; i++) {
        onesInWindow = onesInWindow + arr[i] - arr[i - windowSize];
        minSwaps = Math.min(minSwaps, countOnes - onesInWindow);
    }

    return minSwaps;
};
