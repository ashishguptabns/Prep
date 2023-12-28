/* Water trap - Given n non-negative integers representing an elevation map where the width of each bar is 1, compute how much water it can trap after raining.
 */
/**
 * @param {number[]} height
 * @return {number}
 */
const trap = (heights) => {
  let leftMaxH = 0;
  let rightMaxH = 0;
  let left = 0;
  let right = heights.length - 1;

  let res = 0;

  while (left <= right) {
    //  find max height of water
    if (leftMaxH <= rightMaxH) {
      if (leftMaxH <= heights[left]) {
        //  found a taller wall
        leftMaxH = heights[left];
      } else {
        //  this much water can be held at this index
        res += Math.min(leftMaxH, rightMaxH) - heights[left];
      }
      left += 1;
    } else {
      if (rightMaxH <= heights[right]) {
        rightMaxH = heights[right];
      } else {
        res += Math.min(leftMaxH, rightMaxH) - heights[right];
      }
      right -= 1;
    }
  }

  return res;
};

/* Container With Most Water - You are given an integer array height of length n. There are n vertical lines drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]).
Find two lines that together with the x-axis form a container, such that the container contains the most water.
Return the maximum amount of water a container can store.
 */

const maxArea = (height) => {
  //  solve using two pointers

  let left = 0;
  let right = height.length - 1;

  let maxArea = 0;

  while (left < right) {
    //  Area will be minimum of two heights
    const currArea = (right - left) * Math.min(height[left], height[right]);
    if (currArea > maxArea) {
      maxArea = currArea;
    }

    //  search for better height
    if (height[left] < height[right]) {
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
  const res = []; // Array to store the result triplets

  // Sorting the input array to simplify the solution
  nums.sort((a, b) => a - b);

  for (let first = 0; first < nums.length - 2; first++) {
    // Skip duplicates of the first element
    if (first > 0 && nums[first] === nums[first - 1]) {
      continue;
    }

    let sec = first + 1; // Pointer for the second element
    let third = nums.length - 1; // Pointer for the third element

    while (sec < third) {
      const currSum = nums[first] + nums[sec] + nums[third];

      if (currSum === 0) {
        // Found a triplet with a sum of zero
        res.push([nums[first], nums[sec], nums[third]]);

        // Skip duplicates of the second and third elements
        while (sec < third && nums[sec] === nums[sec + 1]) {
          sec++;
        }
        while (sec < third && nums[third] === nums[third - 1]) {
          third--;
        }

        // Move the pointers towards each other
        sec++;
        third--;
      } else if (currSum < 0) {
        // If the sum is less than zero, move the second pointer to the right
        sec++;
      } else {
        // If the sum is greater than zero, move the third pointer to the left
        third--;
      }
    }
  }

  return res; // Return the array of unique triplets with a sum of zero
};

/* Next Permutation - The next permutation of an array of integers is the next lexicographically greater permutation of its integer.
 */
/**
 * @param {number[]} nums
 * @return {void} Do not return anything, modify nums in-place instead.
 */
const nextPermutation = (nums) => {
  //  consider 1, 5, 8, 4, 7, 6, 5, 3, 1

  let first = nums.length - 2;

  //  travel from right end and find a point firstPointer where [firstPointer + 1] element is higher than firstPointer element
  while (first >= 0 && nums[first] >= nums[first + 1]) {
    first--;
  }

  if (first >= 0) {
    let second = nums.length - 1;
    //  find a number which is greater than our firstPointer item
    while (nums[second] <= nums[first]) {
      second--;
    }
    //  swap 4 with 5 -> 1, 5, 8, 5, 7, 6, 4, 3, 1
    [nums[first], nums[second]] = [nums[second], nums[first]];
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
  reverse(nums, first + 1);
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
  let diff = Infinity;
  const size = nums.length;

  nums.sort((a, b) => a - b);

  let first = 0;
  while (first <= size - 3 && diff !== 0) {
    //  we want closest not exact

    let sec = first + 1;
    let third = size - 1;

    while (sec < third) {
      const sum = nums[first] + nums[sec] + nums[third];
      if (Math.abs(target - sum) < Math.abs(diff)) {
        //  found a closer one
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
  let zero = 0;
  let two = nums.length - 1;

  for (let i = 0; i < nums.length; i++) {
    while (nums[zero] === 0) {
      zero++;
    }
    while (nums[two] === 2) {
      two--;
    }
    const currNum = nums[i];
    if (currNum === 0 && i > zero) {
      [nums[zero], nums[i]] = [nums[i], nums[zero]];
      i--;
    }
    if (currNum === 2 && i < two) {
      [nums[two], nums[i]] = [nums[i], nums[two]];
      i--;
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
  //  we will use two pointers approach

  let len = nums.length;
  let output = [];

  let i = 0;
  let j = len - 1;

  let lowElementsIndex = 0;
  let highElementsIndex = len - 1;

  while (i < len) {
    //  fill small numbers to left
    if (nums[i] < pivot) {
      output[lowElementsIndex++] = nums[i];
    }
    //  fill large numbers to right
    if (nums[j] > pivot) {
      output[highElementsIndex--] = nums[j];
    }

    i++;
    j--;
  }

  //  if there is some space left
  while (lowElementsIndex <= highElementsIndex) {
    output[lowElementsIndex++] = pivot;
  }

  return output;
};

/* Two Sum Less Than K - 
 */

/**
 * @param {number[]} nums
 * @param {number} k
 * @return {number}
 */
const twoSumLessThanK = (nums, k) => {
  // Sort the array in ascending order
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

/* Two sum - variation - Given a 1-indexed array of integers numbers that is already sorted in non-decreasing order, find two numbers such that they add up to a specific target number. Let these two numbers be numbers[index1] and numbers[index2] where 1 <= index1 < index2 <= numbers.length.

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

/* Remove duplicates from sorted array - Given an integer array nums sorted in non-decreasing order, remove some duplicates in-place such that each unique element appears at most twice. The relative order of the elements should be kept the same.

Since it is impossible to change the length of the array in some languages, you must instead have the result be placed in the first part of the array nums. More formally, if there are k elements after removing the duplicates, then the first k elements of nums should hold the final result. It does not matter what you leave beyond the first k elements.

Return k after placing the final result in the first k slots of nums.

Do not allocate extra space for another array. You must do this by modifying the input array in-place with O(1) extra memory.
 */

/**
 * @param {number[]} nums
 * @return {number}
 */
const removeDuplicates = (nums) => {
  // Initialize a variable 'j' to 2. 'j' will keep track of the position
  // where unique elements will be stored in the array.
  let j = 2;

  // Loop through the array starting from the third element (index 2)
  for (let i = 2; i < nums.length; i++) {
    // Check if the current element is different from the element two positions back
    //  notice it is a sorted array
    if (nums[i] != nums[j - 2]) {
      // If different, store the current element at position 'j' and increment 'j'
      nums[j++] = nums[i];
    }
    // If the current element is the same as the element two positions back,
    // it is a duplicate, and we skip adding it to the modified array.
  }

  // 'j' now represents the length of the modified array with duplicates removed.
  return j;
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
  // Initialize two pointers: i for iterating over characters, and res for updating the result array
  let i = 0;
  let res = 0;

  // Iterate over the characters array
  while (i < chars.length) {
    // Initialize a variable to track the length of the current character group
    let groupLen = 1;

    // Check if there are consecutive characters equal to chars[i]
    while (i + groupLen < chars.length && chars[i + groupLen] === chars[i]) {
      groupLen++;
    }

    // Update the result array with the current character
    chars[res++] = chars[i];

    // If the character group has length greater than 1, update the result array with the count
    if (groupLen > 1) {
      // Convert the count to a string and iterate over its digits
      for (let n of groupLen.toString()) {
        // Update the result array with each digit
        chars[res++] = n;
      }
    }

    // Move the pointer to the next character group
    i += groupLen;
  }

  // Return the length of the compressed array
  return res;
};

/* Minimize size subarray sum - Given an array of positive integers nums and a positive integer target, return the minimal length of a 
subarray
 whose sum is greater than or equal to target. If there is no such subarray, return 0 instead. 
 */

/**
 * @param {number} target
 * @param {number[]} nums
 * @return {number}
 */
const minSubArrayLen = (target, nums) => {
  //  subarray is contiguous

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
