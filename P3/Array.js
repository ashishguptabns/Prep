/* Product of array except self - Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].

The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.

You must write an algorithm that runs in O(n) time and without using the division operation.
 */

/**
 * @param {number[]} nums
 * @return {number[]}
 */
const productExceptSelf = (nums) => {
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
      if (neededTime[i] > neededTime[i - 1]) {
        time += neededTime[i - 1];
      } else {
        time += neededTime[i];
      }
    }
  }

  return time;
};

/* 
Sum of subarray ranges - You are given an integer array nums. The range of a subarray of nums is the difference between the largest and smallest element in the subarray.

Return the sum of all subarray ranges of nums.

A subarray is a contiguous non-empty sequence of elements within an array.
 */

/**
 * @param {number[]} nums
 * @return {number}
 */
const subArrayRanges = (nums) => {
  let sum = 0;
  for (let i = 0; i < nums.length; i++) {
    //  min max for subarrays starting with i
    let min = nums[i];
    let max = nums[i];
    for (let j = i + 1; j < nums.length; j++) {
      //  min max for subarrays ending with j
      min = Math.min(min, nums[j]);
      max = Math.max(max, nums[j]);

      sum += max - min;
    }
  }

  return sum;
};

/* Minimum Swaps to Group All 1's Together 
    - You're given a binary array data (containing just 0s and 1s).
    - Your goal is to minimize the number of swaps needed to bring all the 1s together in any part of the array.
*/

const minSwaps = (arr) => {
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

/* Maximum Subarray - Given an integer array nums, find the subarray with the largest sum, and return its sum.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const maxSubArray = (nums) => {
  // Initialize a variable max to negative infinity to ensure it gets updated in the loop
  let max = -Infinity;
  // Initialize a variable currSubSum to 0 to keep track of the current subarray sum
  let currSubSum = 0;

  // Iterate through each element (num) in the input array (nums)
  for (const num of nums) {
    // Add the current element to the current subarray sum
    currSubSum += num;
    // Update the maximum subarray sum (max) by taking the maximum of the current subarray sum and the existing max
    max = Math.max(max, currSubSum);
    // If the current subarray sum becomes negative, reset it to 0, as we want to find the maximum subarray sum
    currSubSum = Math.max(currSubSum, 0);
  }

  // Return the maximum subarray sum found during the iteration
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

/* Maximum Product Subarray - Given an integer array nums, find a subarray that has the largest product, and return the product
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const maxProductS = (nums) => {
  if (!nums.length) {
    return 0;
  }

  let maxProductSoFar = nums[0];
  let minProductSoFar = nums[0];

  let result = maxProductSoFar;

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

    result = Math.max(result, maxProductSoFar);
  }

  return result;
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