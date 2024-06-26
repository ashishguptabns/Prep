/* Maximum Subarray - Given an integer array nums, find the subarray with the largest sum, and return its sum.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const maxSubArray = (nums) => {

  /* pseudo code
    move through nums 
      keep tracking the sum of curr sub array and max found so far
      ditch the subarray when the sum becomes negative
  */

  let max = -Infinity;
  let currSubSum = 0;

  for (const num of nums) {
    currSubSum += num;
    max = Math.max(max, currSubSum);
    currSubSum = Math.max(currSubSum, 0);
  }

  return max;
};

/* Longest consecutive sequence - Given an unsorted array of integers nums, return the length of the longest consecutive elements sequence.

You must write an algorithm that runs in O(n) time.
 */

/**
 * @param {number[]} nums
 * @return {number}
 */
const longestConsecutive = (nums) => {

  /* pseudo code
    move through nums
      check if curr num is the end of a sequence
        loop through the set with decreasing num
          track the count
      else 
        continue
      track the max length of increasing seq
  */

  const set = new Set(nums);

  let max = 0;

  for (const num of nums) {
    if (set.has(num + 1)) {
      continue;
    }

    let counter = 1;
    let curr = num;

    while (set.has(--curr)) {
      counter++;
    }

    max = Math.max(max, counter);
  }

  return max;
};

/* Maximum Product Subarray - Given an integer array nums, find a subarray that has the largest product, and return the product
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const maxProductSubarray = (nums) => {

  /* pseudo code
    keep variables to track max and min products found so far
    move i through the nums array
      check if curr num increases max or min product or we should discard the prev subarray
      keep tracking the max
  */

  if (!nums.length) {
    return 0;
  }

  let maxProductSoFar = nums[0];
  let minProductSoFar = nums[0];

  let max = maxProductSoFar;

  for (let i = 1; i < nums.length; i++) {
    const curr = nums[i];

    const tempMax = Math.max(
      curr,
      Math.max(curr * maxProductSoFar, curr * minProductSoFar)
    );

    minProductSoFar = Math.min(
      curr,
      Math.min(curr * maxProductSoFar, curr * minProductSoFar)
    );

    maxProductSoFar = tempMax;

    max = Math.max(max, maxProductSoFar);
  }

  return max;
};

/* Insert Interval - You are given an array of non-overlapping intervals intervals where intervals[i] = [starti, endi] represent the start and the end of the ith interval and intervals is sorted in ascending order by starti. You are also given an interval newInterval = [start, end] that represents the start and end of another interval.

Insert newInterval into intervals such that intervals is still sorted in ascending order by starti and intervals still does not have any overlapping intervals (merge overlapping intervals if necessary).

Return intervals after the insertion. */

/**
 * @param {number[][]} intervals
 * @param {number[]} newInterval
 * @return {number[][]}
 */
const insert = (intervals, newInterval) => {

  /* pseudo code
    keep two left and right arrays for intervals on both sides
    move through the intervals
      end of curr interval is less than start of given interval
        push to left
      start of curr interval is more than end of given interval
        push to right
      else found an overlap
        keep min of starts and max of ends to find the merged interval
  */

  let [newS, newE] = newInterval;
  const left = [];
  const right = [];

  for (const interval of intervals) {
    const [start, end] = interval;

    if (end < newS) {
      left.push(interval);
    }
    else if (start > newE) {
      right.push(interval);
    }
    else {
      newS = Math.min(newS, start);
      newE = Math.max(newE, end);
    }
  }

  return [...left, [newS, newE], ...right];
};

/* Longest Common Prefix - Write a function to find the longest common prefix string amongst an array of strings.
If there is no common prefix, return an empty string ""
 */
/**
 * @param {string[]} strs
 * @return {string}
 */
const longestCommonPrefix = (strs) => {
  let lcp = ''
  for (let i = 0; i < strs[0].length; i++) {
    if (strs.every(str => str[i] === strs[0][i])) {
      lcp += strs[0][i]
    } else {
      break
    }
  }

  return lcp
};

/* Product of array except self - Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].

The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.

You must write an algorithm that runs in O(n) time and without using the division operation.
 */

/**
 * @param {number[]} nums
 * @return {number[]}
 */
const productExceptSelf = (nums) => {

  /* pseudo code
    keep an array res
      ith item tells the product of all items except ith in nums
    move i from start to end of nums
      assign leftProduct to res[i]
      keep track of leftProduct
    now at ith index in res we have product of items on left
    move i from end to start of nums
      multiply rightProduct to res[i] and assign to res[i]
      track rightProduct
    now at ith index in res we have product of left and right items
  */

  const size = nums.length;
  const res = new Array(size).fill(1);

  let leftProduct = 1;
  let rightProduct = 1;

  for (let i = 0; i < size; i++) {
    res[i] *= leftProduct;
    leftProduct *= nums[i];
  }

  for (let i = size - 1; i >= 0; i--) {
    res[i] *= rightProduct;
    rightProduct *= nums[i];
  }

  return res;
};

/* Sum of subarray ranges - You are given an integer array nums. The range of a subarray of nums is the difference between the largest and smallest element in the subarray.

Return the sum of all subarray ranges of nums.

A subarray is a contiguous non-empty sequence of elements within an array.
 */

/**
 * @param {number[]} nums
 * @return {number}
 */
const subArrayRanges = (nums) => {

  /* pseudo code
    move start through nums
      track min and max for subarray starting at start
      move end through nums starting at start + 1
        track min and max
        add to the sum
  */

  let sum = 0;
  for (let start = 0; start < nums.length; start++) {
    let min = nums[start];
    let max = nums[start];
    for (let end = start + 1; end < nums.length; end++) {
      min = Math.min(min, nums[end]);
      max = Math.max(max, nums[end]);

      sum += max - min;
    }
  }

  return sum;
};

/* Flip String to Monotone Increasing - A binary string is monotone increasing if it consists of some number of 0's (possibly none), followed by some number of 1's (also possibly none).

You are given a binary string s. You can flip s[i] changing it from 0 to 1 or from 1 to 0.

Return the minimum number of flips to make s monotone increasing.
 */
/**
 * @param {string} s
 * @return {number}
 */
const minFlipsMonoIncr = (s) => {

  let [numOnes, numFlips] = [0, 0]
  for (const c of s) {
    if (c === '1') {
      numOnes++
    } else {
      numFlips = Math.min(numFlips + 1, numOnes)
    }
  }

  return numFlips
};

/* String compression - Given an array of characters chars, compress it using the following algorithm:

Begin with an empty string s. For each group of consecutive repeating characters in chars:

If the group's length is 1, append the character to s.
Otherwise, append the character followed by the group's length.
The compressed string s should not be returned separately, but instead, be stored in the input character array chars. Note that group lengths that are 10 or longer will be split into multiple characters in chars.

After you are done modifying the input array, return the new length of the array.

You must write an algorithm that uses only constant extra space.
 */

/**
 * @param {character[]} chars
 * @return {number}
 */
const compress = (chars) => {

  /* pseudo code
      move i through chars
        track length of curr group of same chars
        place the curr char at newLen index
        place chars of group length of curr char at newLen indices
        move i by group length
  */

  let i = 0;
  let newLen = 0;

  while (i < chars.length) {
    let groupLen = 1;

    while (i + groupLen < chars.length && chars[i + groupLen] === chars[i]) {
      groupLen++;
    }

    chars[newLen++] = chars[i];

    if (groupLen > 1) {
      for (const n of groupLen.toString()) {
        chars[newLen++] = n;
      }
    }

    i += groupLen;
  }

  return newLen;
};

/* Max sum circular subarray - Given a circular integer array nums of length n, return the maximum possible sum of a non-empty subarray of nums.

A circular array means the end of the array connects to the beginning of the array. Formally, the next element of nums[i] is nums[(i + 1) % n] and the previous element of nums[i] is nums[(i - 1 + n) % n].

A subarray may only include each element of the fixed buffer nums at most once. Formally, for a subarray nums[i], nums[i + 1], ..., nums[j], there does not exist i <= k1, k2 <= j with k1 % n == k2 % n.
 */

const maxSubarraySumCircular = (nums) => {

  let curMax = 0,
    curMin = 0,
    sum = 0,
    maxSum = nums[0],
    minSum = nums[0];

  for (const num of nums) {
    curMax = Math.max(curMax, 0) + num;
    maxSum = Math.max(maxSum, curMax);

    curMin = Math.min(curMin, 0) + num;
    minSum = Math.min(minSum, curMin);

    sum += num;
  }

  if (sum === minSum) {
    return maxSum;
  }

  return Math.max(maxSum, sum - minSum);
};

/* Maximum Gap - Given an integer array nums, return the maximum difference between two successive elements in its sorted form. If the array contains less than two elements, return 0.

You must write an algorithm that runs in linear time and uses linear extra space. */

/*  Minimum Number of Increments on Subarrays to Form a Target Array - You are given an integer array target. You have an integer array initial of the same size as target with all elements initially zeros.

In one operation you can choose any subarray from initial and increment each value by one.

Return the minimum number of operations to form a target array from initial.

The test cases are generated so that the answer fits in a 32-bit integer. */

var minNumberOperations = function (target) {
  let res = target[0]
  for (let i = 1; i < target.length; i++) {
    res += Math.max(0, target[i] - target[i - 1])
  }

  return res
};

/* 714. Best Time to Buy and Sell Stock with Transaction Fee */

/**
 * @param {number[]} prices
 * @param {number} fee
 * @return {number}
 */
var maxProfit = function (prices, fee) {
  const n = prices.length;

  let profit = 0;
  let lastBuy = -prices[0];

  for (let i = 1; i < n; i++) {
    profit = Math.max(profit, lastBuy + prices[i] - fee);
    lastBuy = Math.max(lastBuy, profit - prices[i]);
  }

  return profit;
};

/* 442. Find All Duplicates in an Array */

/**
 * @param {number[]} nums
 * @return {number[]}
 */
var findDuplicates = function (nums) {
  const ans = []
  for (let i = 0; i < nums.length; i++) {
    const x = Math.abs(nums[i])
    if (nums[x - 1] < 0) {
      ans.push(x)
    }
    nums[x - 1] *= -1
  }
  console.log(ans, nums)
  return ans
};

/* Partition Array for Maximum Sum */

/**
 * @param {number[]} arr
 * @param {number} k
 * @return {number}
 */
var maxSumAfterPartitioning = function (arr, k) {
  const sumArr = Array(arr.length).fill(0)

  for (let i = 0; i < arr.length; i++) {
    let currMax = 0
    let currSum = 0

    for (let j = i; j >= Math.max(0, i + 1 - k); j--) {
      currMax = Math.max(currMax, arr[j])
      const curr = currMax * (i + 1 - j) + sumArr[j]
      currSum = Math.max(currSum, curr)
    }

    sumArr[i + 1] = currSum
  }

  return sumArr.at(-1)
};

/* 1387. Sort Integers by The Power Value */
var getKth = function (lo, hi, k) {
  const findPower = (num) => {
    let power = 0
    while (num > 1) {
      power++
      num = num % 2 ? 3 * num + 1 : num / 2
    }

    return power
  }

  const ans = []
  const map = {}
  for (let i = lo; i <= hi; i++) {
    ans.push(i)
    map[i] = findPower(i)
  }
  ans.sort((a, b) => map[a] - map[b])
  return ans[k - 1]
};