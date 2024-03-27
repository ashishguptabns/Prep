/* Valid Palindrome II - Given a string s, return true if the s can be palindrome after deleting at most one character from it.
 */
/**
 * @param {string} s
 * @return {boolean}
 */
const validPalindrome = (str) => {
  function isPalindromeRange(start, end) {
    while (start < end) {
      if (str[start] !== str[end]) {
        return false;
      }
      start++;
      end--;
    }
    return true;
  }

  let left = 0;
  let right = str.length - 1;

  while (left < right) {
    if (str[left] !== str[right]) {
      if (isPalindromeRange(left + 1, right)) {
        return true;
      }

      if (isPalindromeRange(left, right - 1)) {
        return true;
      }

      return false;
    }

    left++;
    right--;
  }

  return true;
};

/* Container With Most Water - You are given an integer array height of length n. There are n vertical lines drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]).
Find two lines that together with the x-axis form a container, such that the container contains the most water.
Return the maximum amount of water a container can store.
 */

const maxArea = (heights) => {
  /* pseudo code
    keep two pointers at start and end
    move through the array
      find the curr area of water
      update the area
      search for a better height on either side
  */

  let left = 0;
  let right = heights.length - 1;

  let maxArea = 0;

  while (left < right) {
    //  Area will be minimum of two heights
    const currArea = (right - left) * Math.min(heights[left], heights[right]);
    if (currArea > maxArea) {
      maxArea = currArea;
    }

    //  search for better height
    if (heights[left] < heights[right]) {
      left++;
    } else {
      right--;
    }
  }

  return maxArea;
};

/* 3Sum - Given an integer array nums, return all the triplets [nums[i], nums[j], nums[k]] such that i != j, i != k, and j != k, and nums[i] + nums[j] + nums[k] == 0.

Notice that the solution set must not contain duplicate triplets.
 */
/**
 * @param {number[]} nums
 * @return {number[][]}
 */
const threeSum = (nums) => {

  /* pseudo code
    sort the nums array
    move first till nums length - 2 leaving space for two more pointers
      skip duplicates
      keep sec pointer at first + 1 and third pointer at nums length - 1
      loop until sec < third
        curr sum = 0
          record
          skip duplicates of sec and third pointers
          move sec and third towards each other
        curr sum < 0
          move sec pointer
        curr sum > 0
          move third pointer
  */

  const res = [];

  nums.sort((a, b) => a - b);

  for (let first = 0; first < nums.length - 2; first++) {
    if (first > 0 && nums[first] === nums[first - 1]) {
      continue;
    }

    let sec = first + 1;
    let third = nums.length - 1;

    while (sec < third) {
      const currSum = nums[first] + nums[sec] + nums[third];

      if (currSum === 0) {
        res.push([nums[first], nums[sec], nums[third]]);

        while (sec < third && nums[sec] === nums[sec + 1]) {
          sec++;
        }
        while (sec < third && nums[third] === nums[third - 1]) {
          third--;
        }

        sec++;
        third--;
      } else if (currSum < 0) {
        sec++;
      } else {
        third--;
      }
    }
  }

  return res;
};

/* 3Sum Closest - Given an integer array nums of length n and an integer target, find three integers in nums such that the sum is closest to target.
Return the sum of the three integers.
 */
/**
 * @param {number[]} nums
 * @param {number} target
 * @return {number}
 */
const threeSumClosest = (nums, target) => {

  /* pseudo code
    sort the nums array
    keep first pointer at 0
    move through nums array
      keep sec at first + 1 and third at nums length - 1
      loop until sec < third
        keep updating closest sum
        sum < target
          move sec pointer
        else
          move third pointer
  */

  let diff = Infinity;
  const size = nums.length;

  nums.sort((a, b) => a - b);

  let first = 0;
  while (first <= size - 3 && diff !== 0) {

    let sec = first + 1;
    let third = size - 1;

    while (sec < third) {
      const sum = nums[first] + nums[sec] + nums[third];
      if (Math.abs(target - sum) < Math.abs(diff)) {
        diff = target - sum;
      }
      if (sum < target) {
        //  move second pointer
        sec++;
      } else {
        third--;
      }
    }

    first++;
  }

  //  closest sum
  return target - diff;
};

/* Sort Colors - Given an array nums with n objects colored red, white, or blue, sort them in-place so that objects of the same color are adjacent, with the colors in the order red, white, and blue.

We will use the integers 0, 1, and 2 to represent the color red, white, and blue, respectively.
 */
/**
 * @param {number[]} nums
 * @return {void} Do not return anything, modify nums in-place instead.
 */
const sortColors = (nums) => {

  let left = 0

  for (let i = 0; i < nums.length; i++) {
    if (nums[i] == 0) {
      [nums[i], nums[left]] = [nums[left], nums[i]]
      left++
    }
  }
  for (let i = left; i < nums.length; i++) {
    if (nums[i] == 1) {
      [nums[i], nums[left]] = [nums[left], nums[i]]
      left++
    }
  }
};

/* Partition array as per given pivot - You are given a 0-indexed integer array nums and an integer pivot. 
Every element less than pivot appears before every element greater than pivot.
Every element equal to pivot appears in between the elements less than and greater than pivot.
The relative order of the elements less than pivot and the elements greater than pivot is maintained.
Return nums after the rearrangement.
 */
/**
 * @param {number[]} nums
 * @param {number} pivot
 * @return {number[]}
 */
const pivotArray = (nums, pivot) => {
  const arr = Array(nums.length).fill(pivot)
  let left = 0
  let pivotCount = 0
  for (const num of nums) {
    if (num < pivot) {
      arr[left++] = num
    }
    if (num === pivot) {
      pivotCount++
    }
  }
  left += pivotCount
  for (const num of nums) {
    if (num > pivot) {
      arr[left++] = num
    }
  }

  return arr
};

/* Two Sum Less Than K - 
 */

/**
 * @param {number[]} nums
 * @param {number} k
 * @return {number}
 */
const twoSumLessThanK = (nums, k) => {
  /* pseudo code
    sort the nums array
    keep left and right pointers
    loop array until left < right
      curr sum < k
        update max sum as per condition
        move left
      else
        move right
  */

  nums.sort((a, b) => a - b);

  let left = 0;
  let right = nums.length - 1;
  let maxSum = -1;

  while (left < right) {
    const sum = nums[left] + nums[right];

    // If the sum is less than k, update maxSum and move the left pointer to the right
    if (sum < k) {
      maxSum = Math.max(maxSum, sum);
      left++;
    } else {
      // If the sum is greater than or equal to k, move the right pointer to the left
      right--;
    }
  }

  return maxSum;
};

/* Two sum II - Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order, find two numbers such that they add up to a specific target number. Let these two numbers be numbers[index1] and numbers[index2] where 1 <= index1 < index2 <= numbers.length.

Return the indices of the two numbers, index1 and index2, added by one as an integer array [index1, index2] of length 2.

The tests are generated such that there is exactly one solution. You may not use the same element twice.

Your solution must use only constant extra space.
 */

/**
 * @param {number[]} numbers
 * @param {number} target
 * @return {number[]}
 */
var twoSum = function (nums, k) {
  let left = 0;
  let right = nums.length - 1;
  let ans;

  while (left < right) {
    const sum = nums[left] + nums[right];

    if (sum < k) {
      left++;
    } else if (sum > k) {
      right--;
    } else {
      //  one sol as per condition
      ans = [left + 1, right + 1];
      break;
    }
  }

  return ans;
};

/* Remove Duplicates from Sorted Array II - Given an integer array nums sorted in non-decreasing order, remove some duplicates in-place such that each unique element appears at most twice. The relative order of the elements should be kept the same.

Since it is impossible to change the length of the array in some languages, you must instead have the result be placed in the first part of the array nums. More formally, if there are k elements after removing the duplicates, then the first k elements of nums should hold the final result. It does not matter what you leave beyond the first k elements.

Return k after placing the final result in the first k slots of nums.

Do not allocate extra space for another array. You must do this by modifying the input array in-place with O(1) extra memory.
 */

/**
 * @param {number[]} nums
 * @return {number}
 */
const removeDuplicates = (nums) => {

  /* pseudo code
      keep an index j to store max 2 duplicates
      move i through nums
        check if its a new group
          put at jth index 
          move j
  */

  let j = 2;

  for (let i = 2; i < nums.length; i++) {
    if (nums[i] != nums[j - 2]) {
      nums[j++] = nums[i];
    }
  }

  return j;
};

/* Minimize size subarray sum - Given an array of positive integers nums and a positive integer target, return the minimal length of a 
subarray whose sum is greater than or equal to target. If there is no such subarray, return 0 instead. 
 */

/**
 * @param {number} target
 * @param {number[]} nums
 * @return {number}
 */
const minSubArrayLen = (target, nums) => {
  /* pseudo code
    move right through the nums array
      update the sum
      while sum >= target
        update minLen
        keep shring window from left
  */

  let minLen = Infinity;
  let left = 0;
  let sum = 0;

  for (let right = 0; right < nums.length; right++) {
    sum += nums[right];

    while (sum >= target) {
      minLen = Math.min(minLen, right - left + 1);

      //  shrink the window from left
      sum -= nums[left++];
    }
  }

  return minLen === Infinity ? 0 : minLen;
};

/* Trapping Rain Water - Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.
 */
const trap = (heights) => {

  /* pseudo code
    notice that the width of each bar is given
    keep two pointers starting at 0 and end of array
    go through the heights
      keep tracking max heights on left and right side
      if left max height is smaller than right max height means right max h can support water
        if curr left height is bigger than left max height means no way to hold water
          update
        else water can be held here
          add the water at this index to res
        move the left pointer
      else 
        repeat above logic for right side item
  */

  let leftMaxH = 0;
  let rightMaxH = 0;
  let left = 0;
  let right = heights.length - 1;

  let res = 0;

  while (left <= right) {
    if (leftMaxH <= rightMaxH) {
      if (leftMaxH <= heights[left]) {
        leftMaxH = heights[left];
      } else {
        res += leftMaxH - heights[left]
      }
      left += 1;
    } else {
      if (rightMaxH <= heights[right]) {
        rightMaxH = heights[right];
      } else {
        res += rightMaxH - heights[right]
      }
      right -= 1;
    }
  }

  return res;
};

/* Next Permutation - The next permutation of an array of integers is the next lexicographically greater permutation of its integer.
 */
/**
 * @param {number[]} nums
 * @return {void} Do not return anything, modify nums in-place instead.
 */
const nextPermutation = (nums) => {

  /* pseudo code
    //  consider 1, 5, 8, 4, 7, 6, 5, 3, 1 
    move i from right to left till [i + 1] item is higher than [i] item
      found 4
    move j from right to left till ith item is smaller than jth item
      found 5
    swap i and j
    reverse elements starting at i + 1
  */

  let i = nums.length - 2;

  while (i >= 0 && nums[i] >= nums[i + 1]) {
    i--;
  }

  if (i >= 0) {
    let j = nums.length - 1;
    while (nums[j] <= nums[i]) {
      j--;
    }
    //  swap 4 with 5 -> 1, 5, 8, 5, 7, 6, 4, 3, 1
    [nums[i], nums[j]] = [nums[j], nums[i]];
  }

  const reverse = (nums, start) => {
    let left = start;
    let right = nums.length - 1;
    while (left < right) {
      [nums[left], nums[right]] = [nums[right], nums[left]];
      left++;
      right--;
    }
  };
  //  to find the next larger number -> 1, 5, 8, 5, 1, 3, 4, 6, 7
  reverse(nums, i + 1);
};

/* 1963. Minimum Number of Swaps to Make the String Balanced */

/* 1850. Minimum Adjacent Swaps to Reach the Kth Smallest Number */

/* 1237. Find Positive Integer Solution for a Given Equation */
