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
      multiply rightProduct to res[i]
      track rightProduct
    now at ith index in res we have product of left and right items
  */

  const size = nums.length;
  const res = new Array(size).fill(1);

  let leftProduct = 1;
  let rightProduct = 1;

  for (let i = 0; i < size; i++) {
    //  put the product of elements on the left
    res[i] *= leftProduct;
    //  update the product
    leftProduct *= nums[i];
  }

  for (let i = size - 1; i >= 0; i--) {
    //  put the product of elements on the right
    res[i] *= rightProduct;
    //  update the product
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
    //  min max for subarrays starting with i
    let min = nums[start];
    let max = nums[start];
    for (let end = start + 1; end < nums.length; end++) {
      //  min max for subarrays ending with j
      min = Math.min(min, nums[end]);
      max = Math.max(max, nums[end]);

      sum += max - min;
    }
  }

  return sum;
};

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
    // If the current subarray sum becomes negative, reset it to 0, as we want to find the maximum subarray sum
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
      track the max length of increasing seq
  */

  // Create a set to efficiently check the presence of elements.
  const set = new Set(nums);

  // Initialize a variable to store the maximum length.
  let max = 0;

  // Iterate through each element in the array.
  for (const num of nums) {
    // If the next consecutive element is present in the set, skip to the next iteration.
    if (set.has(num + 1)) {
      continue;
    }

    // If the next consecutive element is not present, start counting the consecutive elements backward.
    let counter = 1;
    let curr = num;

    // Continue counting consecutive elements backward.
    while (set.has(--curr)) {
      counter++;
    }

    // Update the maximum length.
    max = Math.max(max, counter);
  }

  // Return the maximum length of consecutive subsequence.
  return max;
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

  /* pseudo code
    move through s
      found a 1
        track number of 1s
      found a 0
        either flip all 1s or flip curr char
  */

  let numOfOnesSoFar = 0
  let numFlips = 0

  for (const c of s) {
    if (c === '1') {
      //  this is monotone increasing, no flips required             
      numOfOnesSoFar++
    } else {
      //  we might have to flip this character '0' or all the 1s we found so far                
      numFlips = Math.min(numOfOnesSoFar, numFlips + 1)
    }
  }

  return numFlips
};

/* Minimum Time to Make Rope Colorful - Alice has n balloons arranged on a rope. You are given a 0-indexed string color where colors[i] is the color of the ith balloon.

Alice wants the rope to be colorful. She does not want two consecutive balloons to be of the same color, so she asks Bob for help. Bob can remove some balloons from the rope to make it colorful. You are given a 0-indexed integer array neededTime where neededTime[i] is the time (in seconds) that Bob needs to remove the ith balloon from the rope.

Return the minimum time Bob needs to make the rope colorful.
 */

/**
 * @param {string} colors
 * @param {number[]} neededTime
 * @return {number}
 */
const minCostRope = (colors, neededTime) => {
  let time = 0;

  //  start from 2nd balloon
  for (let i = 1; i < colors.length; i++) {
    //  found the consecutive same color balloons
    if (colors[i] === colors[i - 1]) {
      // If the needed time for the current balloon is greater than the needed time for the previous balloon
      if (neededTime[i] > neededTime[i - 1]) {
        // Add the needed time of the previous balloon to the total time
        time += neededTime[i - 1];
      } else {
        // Add the needed time of the current balloon to the total time
        time += neededTime[i];
      }
    }
  }

  return time;
};

/* Maximum Product Subarray - Given an integer array nums, find a subarray that has the largest product, and return the product
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const maxProductS = (nums) => {

  /* pseudo code
    track max and min products found so far
    move i through the nums array
      check if curr num increase max or min product or we should discard the prev subarray
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

    //  either we discard the prev subarray or we choose
    const tempMax = Math.max(
      curr,
      Math.max(curr * maxProductSoFar, curr * minProductSoFar)
    );

    //  because we have negative numbers; minProductSoFar can make big number later
    minProductSoFar = Math.min(
      curr,
      Math.min(curr * maxProductSoFar, curr * minProductSoFar)
    );

    maxProductSoFar = tempMax;

    max = Math.max(max, maxProductSoFar);
  }

  return max;
};

/* Max sum circular subarray - Given a circular integer array nums of length n, return the maximum possible sum of a non-empty subarray of nums.

A circular array means the end of the array connects to the beginning of the array. Formally, the next element of nums[i] is nums[(i + 1) % n] and the previous element of nums[i] is nums[(i - 1 + n) % n].

A subarray may only include each element of the fixed buffer nums at most once. Formally, for a subarray nums[i], nums[i + 1], ..., nums[j], there does not exist i <= k1, k2 <= j with k1 % n == k2 % n.
 */

/**
 * @param {number[]} nums
 * @return {number}
 */
const maxSubarraySumCircular = (nums) => {
  // Initialize variables to keep track of current maximum and minimum subarray sums,
  // overall sum, maximum sum, and minimum sum
  let curMax = 0,
    curMin = 0,
    sum = 0,
    maxSum = nums[0],
    minSum = nums[0];

  // Iterate through the array
  for (let num of nums) {
    // Calculate the current maximum subarray sum using Kadane's algorithm
    curMax = Math.max(curMax, 0) + num;
    // Update the maximum sum if the current maximum is greater
    maxSum = Math.max(maxSum, curMax);

    // Calculate the current minimum subarray sum using Kadane's algorithm
    curMin = Math.min(curMin, 0) + num;
    // Update the minimum sum if the current minimum is smaller
    minSum = Math.min(minSum, curMin);

    // Calculate the overall sum of the array
    sum += num;
  }

  // If the overall sum is equal to the minimum sum, it means all elements are negative,
  // so return the maximum sum (the maximum subarray sum in this case)
  if (sum === minSum) {
    return maxSum;
  }

  // Otherwise, return the maximum of the maximum sum and the difference between
  // the overall sum and the minimum sum (considering the circular nature of the array)
  return Math.max(maxSum, sum - minSum);
};

