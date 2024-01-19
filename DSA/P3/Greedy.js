/* Buildings With an Ocean View - Find the indices of buildings that have an unobstructed view of the ocean, assuming buildings to the right can block the view.
 */
const findBuildingsWithOceanView = (heights) => {
  const result = [];
  let maxRightHeight = -1;

  for (let i = heights.length - 1; i >= 0; i--) {
    const currentHeight = heights[i];

    if (currentHeight > maxRightHeight) {
      result.push(i);
      maxRightHeight = currentHeight;
    }
  }

  return result;
};

/* Max chunks to make sorted - You are given an integer array arr of length n that represents a permutation of the integers in the range [0, n - 1].

We split arr into some number of chunks (i.e., partitions), and individually sort each chunk. After concatenating them, the result should equal the sorted array.

Return the largest number of chunks we can make to sort the array.
 */

const maxChunksToSorted = (arr) => {

  /* pseudo code
    notice nums are from 0 till n - 1
    move through arr
      keep tracking max num found so far
      max = index
        one sorted chunk can end at this index
        increase the count
  */

  let count = 0;
  let max = -Infinity;

  arr.forEach((num, index) => {
    max = Math.max(num, max);

    if (max === index) {
      count++;
    }
  });

  return count;
};

/* Break a palindrome - Given a palindromic string of lowercase English letters palindrome, replace exactly one character with any lowercase English letter so that the resulting string is not a palindrome and that it is the lexicographically smallest one possible.

Return the resulting string. If there is no way to replace a character to make it not a palindrome, return an empty string.
 */
/**
 * @param {string} palindrome
 * @return {string}
 */
const breakPalindrome = (palindrome) => {

  /* pseudo code
    move i till half length of palindrome
      curr char is not a
        put a and return
    put b at the last index and return
  */

  const len = palindrome.length;
  if (len === 1) {
    return "";
  }

  for (let i = 0; i < Math.floor(len / 2); i++) {
    if (palindrome[i] !== "a") {
      return palindrome.substring(0, i) + "a" + palindrome.substring(i + 1);
    }
  }
  return palindrome.substring(0, len - 1) + "b";
};

/* Gas Station - There are n gas stations along a circular route, where the amount of gas at the ith station is gas[i].
You have a car with an unlimited gas tank and it costs cost[i] of gas to travel from the ith station to its next (i + 1)th station. You begin the journey with an empty tank at one of the gas stations.
Given two integer arrays gas and cost, return the starting gas station's index if you can travel around the circuit once in the clockwise direction, otherwise return -1. If there exists a solution, it is guaranteed to be unique
*/
/**
 * @param {number[]} gas
 * @param {number[]} cost
 * @return {number}
 */
const canCompleteCircuit = (gases, costs) => {

  /* pseudo code
    move i through stations
      track gas left after curr trip and overall
      ran out of gas
        reset curr gas to 0
        change station to i + 1
    check if overall gas left was >=0
  */

  let startStation = 0;
  let currGasLeft = 0;
  let totalGasLeft = 0;

  for (let i = 0; i < gases.length; i++) {
    const gas = gases[i];
    const cost = costs[i];

    currGasLeft += gas - cost;
    totalGasLeft += gas - cost;

    if (currGasLeft < 0) {
      currGasLeft = 0;
      startStation = i + 1;
    }
  }

  if (totalGasLeft < 0) {
    return -1;
  } else {
    return startStation;
  }
};

/* Jump Game || - You are given a 0-indexed array of integers nums of length n. You are initially positioned at nums[0].
Each element nums[i] represents the maximum length of a forward jump from index i. In other words, if you are at nums[i], 
you can jump to any nums[i + j] where:
  0 <= j <= nums[i] and
  i + j < n
Return the minimum number of jumps to reach nums[n - 1]. The test cases are generated such that you can reach nums[n - 1]
*/

/**
 * @param {number[]} nums
 * @return {number}
 */
const jump = (nums) => {

  /* pseudo code
    move i through nums
      keep finding the biggest possible next index
      reached end of curr jump
        increase num of jumps
        update currJumpEnd
  */

  let numJumps = 0;
  let currJumpEnd = 0;
  let nextIndex = 0;

  for (let i = 0; i < nums.length - 1; i++) {
    nextIndex = Math.max(nextIndex, i + nums[i]);

    if (i === currJumpEnd) {
      numJumps++;

      currJumpEnd = nextIndex;
    }
  }

  return numJumps;
};

/* Jump Game - You are given an integer array nums. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position.

Return true if you can reach the last index, or false otherwise.
 */
/**
 * @param {number[]} nums
 * @return {boolean}
 */
const canJump = (nums) => {

  /* pseudo code
      
  */

  let lastPos = nums.length - 1;

  for (let i = nums.length - 2; i >= 0; i--) {
    if (i + nums[i] >= lastPos) {
      lastPos = i;
    }
  }

  return lastPos === 0;
};

/* Minimum Rounds to Complete All Tasks - You are given a 0-indexed integer array tasks, where tasks[i] represents the difficulty level of a task. In each round, you can complete either 2 or 3 tasks of the same difficulty level.

Return the minimum rounds required to complete all the tasks, or -1 if it is not possible to complete all the tasks.
 */
/**
 * @param {number[]} tasks
 * @return {number}
 */
const minimumRounds = (tasks) => {
  //  greedy approach

  let minRounds = 0;

  const map = {};

  //  track the frequency of tasks with same difficulty
  for (const difficulty of tasks) {
    map[difficulty] = (map[difficulty] || 0) + 1;
  }
  for (const [key, freq] of Object.entries(map)) {
    if (freq === 1) {
      //  as per definition
      return -1;
    }

    if (Math.floor(freq % 3) === 0) {
      minRounds += Math.floor(freq / 3);
    } else if (Math.floor(freq % 3) === 1) {
      //  even freq like 4
      minRounds += Math.floor(freq / 3) + 1;
    } else {
      //  odd freq like 5
      minRounds += Math.floor(freq / 3) + 1;
    }
  }

  return minRounds;
};

/* Two City Scheduling - A company is planning to interview 2n people. Given the array costs where costs[i] = [aCosti, bCosti], the cost of flying the ith person to city a is aCosti, and the cost of flying the ith person to city b is bCosti.

Return the minimum cost to fly every person to a city such that exactly n people arrive in each city.
 */
/**
 * @param {number[][]} costs
 * @return {number}
 */
const twoCitySchedCost = (costs) => {
  //  people with lower cost difference will come first
  //  preferable to the first city
  costs.sort((a, b) => a[0] - a[1] - (b[0] - b[1]));

  //  equal number of people in both cities
  let n = costs.length / 2;

  let minCost = 0;
  let count = 0;

  for (const cost of costs) {
    //  fill the first city then second
    minCost += count < n ? cost[0] : cost[1];
    count++;
  }

  return minCost;
};

/* Increasing Triplet Subsequence - Given an integer array nums, return true if there exists a triple of indices (i, j, k) such that i < j < k and nums[i] < nums[j] < nums[k]. If no such indices exists, return false.
 */
/**
 * @param {number[]} nums
 * @return {boolean}
 */
const increasingTriplet = (nums) => {
  let first = Infinity;
  let sec = Infinity;

  for (const num of nums) {
    if (num <= first) {
      first = num;
    } else if (num <= sec) {
      sec = num;
    } else {
      return true;
    }
  }

  return false;
};

/* Minimum Number of Arrows to Burst Balloons - There are some spherical balloons taped onto a flat wall that represents the XY-plane. The balloons are represented as a 2D integer array points where points[i] = [xstart, xend] denotes a balloon whose horizontal diameter stretches between xstart and xend. You do not know the exact y-coordinates of the balloons.

Arrows can be shot up directly vertically (in the positive y-direction) from different points along the x-axis. A balloon with xstart and xend is burst by an arrow shot at x if xstart <= x <= xend. There is no limit to the number of arrows that can be shot. A shot arrow keeps traveling up infinitely, bursting any balloons in its path.

Given the array points, return the minimum number of arrows that must be shot to burst all balloons.
 */
/**
 * @param {number[][]} points
 * @return {number}
 */
const findMinArrowShots = (points) => {
  //  sort by end points
  //  all points are x coords
  points.sort((a, b) => a[1] - b[1]);

  let numArrows = 1;

  //  travel through the balloons
  let currEnd = points[0][1];
  for (const [xStart, xEnd] of points) {
    if (xStart > currEnd) {
      //  need another arrow
      numArrows++;
      currEnd = xEnd;
    }
  }

  return numArrows;
};

/* Best Time to Buy and Sell Stock - You are given an integer array prices where prices[i] is the price of a given stock on the ith day.

On each day, you may decide to buy and/or sell the stock. You can only hold at most one share of the stock at any time. However, you can buy it then immediately sell it on the same day.

Find and return the maximum profit you can achieve.
 */
/**
 * @param {number[]} prices
 * @return {number}
 */
const maxProfit = function (prices) {
  //  use greedy

  let maxProfit = 0;

  for (let i = 1; i < prices.length; i++) {
    if (prices[i] > prices[i - 1]) {
      //  capture the incremental profit each day
      maxProfit += prices[i] - prices[i - 1];
    }
  }

  return maxProfit;
};

/* Largest Number - Given a list of non-negative integers nums, arrange them such that they form the largest number and return it.

Since the result may be very large, so you need to return a string instead of an integer
 */
/**
 * @param {number[]} nums
 * @return {string}
 */
const largestNumber = (nums) => {
  if (!nums || !nums.length) {
    return "0";
  }

  nums.sort((a, b) => `${b}${a}` - `${a}${b}`);

  if (nums[0] === 0) {
    return "0";
  }

  return nums.join("");
};

/* Minimum Increment to Make Array Unique - You are given an integer array nums. In one move, you can pick an index i where 0 <= i < nums.length and increment nums[i] by 1.

Return the minimum number of moves to make every value in nums unique.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const minIncrementForUnique = (nums) => {
  //  we will use greedy approach

  //  sort the array in increasing order
  nums.sort((a, b) => a - b);

  let count = 0;

  for (let i = 0; i < nums.length; i++) {
    if (nums[i] <= nums[i - 1]) {
      //  this less check might come because of in place changes to the items
      //  increment to make it unique
      const diff = nums[i - 1] - nums[i];
      nums[i] += diff + 1;
      //  as per definition
      count += diff + 1;
    }
  }

  return count;
};

/* Minimum Suffix Flips - You are given a 0-indexed binary string target of length n. You have another binary string s of length n that is initially set to all zeros. You want to make s equal to target.
In one operation, you can pick an index i where 0 <= i < n and flip all bits in the inclusive range [i, n - 1]. Flip means changing '0' to '1' and '1' to '0'.
Return the minimum number of operations needed to make s equal to target.
 */
/**
 * @param {string} target
 * @return {number}
 */
const minFlips = (target) => {
  //  we will solve this with greedy

  let flipCount = 0;
  let currChar = "0";

  for (let i = 0; i < target.length; i++) {
    if (target[i] != currChar) {
      //  this we are doing because we have a range to flip
      currChar = target[i];
      flipCount++;
    }
  }

  return flipCount;
};

/* Minimize product sum of two arrays
 */
const minimizeProductSum = (arr1, arr2) => {
  // Sort one array in ascending order
  arr1.sort((a, b) => a - b);

  // Sort the other array in descending order
  arr2.sort((a, b) => b - a);

  // Calculate the product sum
  let productSum = 0;
  for (let i = 0; i < arr1.length; i++) {
    productSum += arr1[i] * arr2[i];
  }

  return productSum;
};

/* Dot product of two sparse vectors - Given two sparse vectors, compute their dot product.
 */
const dotProduct = (sparseVector1, sparseVector2) => {
  let result = 0;

  // Iterate over keys in the first sparse vector
  for (let key in sparseVector1) {
    // Check if the same key exists in the second sparse vector
    if (sparseVector2.hasOwnProperty(key)) {
      // Multiply corresponding values and add to the result
      result += sparseVector1[key] * sparseVector2[key];
    }
  }

  return result;

  // Note: If sparse vectors are given in array format then convert into a map (index, value) ignoring the indices with zero values
};

/* Partitioning Into Minimum Number Of Deci-Binary Numbers - Given a string n that represents a positive decimal integer, return the minimum number of positive deci-binary numbers needed so that they sum up to n.
 */
/**
 * @param {string} n
 * @return {number}
 */
const minPartitions = (n) => {
  //  Insight is that we need to find the max digit in the given string that will give us number of deci-binary numbers needed

  let maxDigit = 0;
  for (const char of n) {
    const currDigit = parseInt(char);
    if (maxDigit < currDigit) {
      maxDigit = currDigit;
    }
  }

  return maxDigit;
};

/* Maximum Number of Coins You Can Get - There are 3n piles of coins of varying size, you and your friends will take piles of coins as follows:
In each step, you will choose any 3 piles of coins (not necessarily consecutive).
Of your choice, Alice will pick the pile with the maximum number of coins.
You will pick the next pile with the maximum number of coins.
Your friend Bob will pick the last pile.
Repeat until there are no more piles of coins.
Given an array of integers piles where piles[i] is the number of coins in the ith pile.

Return the maximum number of coins that you can have.
 */
/**
 * @param {number[]} piles
 * @return {number}
 */
const maxCoins = (piles) => {
  let ans = 0;

  //  sort the piles in decreasing order
  piles.sort((a, b) => b - a);

  let numRounds = piles.length / 3;

  //  in each round we can pick the 2nd largest pile
  //  we are giving numRounds piles from bottom to the 2rd person
  for (let i = 1; i < piles.length - numRounds; i += 2) {
    ans += piles[i];
  }
  return ans;
};

/* Minimize max pair sum in array - Given an array nums of even length n, pair up the elements of nums into n / 2 pairs such that:
Each element of nums is in exactly one pair, and
The maximum pair sum is minimized.
Return the minimized maximum pair sum after optimally pairing up the elements.
 */
const minPairSum = (nums) => {
  //  use greedy approach

  nums = nums.sort((a, b) => a - b);
  let maxSum = nums[0];

  for (let left = 0; left < nums.length / 2; left++) {
    const right = nums.length - left - 1;
    maxSum = Math.max(maxSum, nums[left] + nums[right]);
  }

  return maxSum;
};

/* Partition labels - You are given a string s. We want to partition the string into as many parts as possible so that each letter appears in at most one part.

Note that the partition is done so that after concatenating all the parts in order, the resultant string should be s.

Return a list of integers representing the size of these parts.
 */
const partitionLabels = (s) => {
  const lastIndex = {};

  //  maintain the lastIndex of a char
  for (let i = 0; i < s.length; i++) {
    lastIndex[s[i]] = i;
  }

  const res = [];

  //  start index of curr partition
  let currPartitionStart = 0;
  //  end index of curr partition
  let currPartitionEnd = 0;

  for (let i = 0; i < s.length; i++) {
    currPartitionEnd = Math.max(lastIndex[s[i]], currPartitionEnd);

    //  reached the end of curr repeat char
    if (i === currPartitionEnd) {
      //  curr partition size
      res.push(i + 1 - currPartitionStart);
      currPartitionStart = i + 1;
    }
  }

  return res;
};

/* Optimal partition of string - Given a string s, partition the string into one or more substrings such that the characters in each substring are unique. That is, no letter appears in a single substring more than once.

Return the minimum number of substrings in such a partition.
 */
const partitionString = (s) => {
  let count = 1;
  let currChars = [];
  for (const char of s) {
    if (currChars.includes(char)) {
      //  found a repeat char
      //  break this substring
      count++;
      //  update for next substring
      currChars = [char];
    } else {
      currChars.push(char);
    }
  }

  return count;
};

/* Find valid matrix given row sum and col sum - You are given two arrays rowSum and colSum of non-negative integers where rowSum[i] is the sum of the elements in the ith row and colSum[j] is the sum of the elements of the jth column of a 2D matrix. In other words, you do not know the elements of the matrix, but you do know the sums of each row and column.

Find any matrix of non-negative integers of size rowSum.length x colSum.length that satisfies the rowSum and colSum requirements.
 */
const restoreMatrix = (rowSum, colSum) => {

  /* pseudo code
      
  */

  const m = rowSum.length;
  const n = colSum.length;
  const matrix = Array(m).fill().map(() => Array(n).fill(0));

  for (let row = 0; row < m; row++) {
    for (let col = 0; col < n; col++) {
      const min = Math.min(rowSum[row], colSum[col]);

      matrix[row][col] = min;

      rowSum[row] -= min;
      colSum[col] -= min;
    }
  }
  return matrix;
};

/* Find Original Array From Doubled Array - An integer array original is transformed into a doubled array changed by appending twice the value of every element in original, and then randomly shuffling the resulting array.

Given an array changed, return original if changed is a doubled array. If changed is not a doubled array, return an empty array. The elements in original may be returned in any order.
*/

/**
 * @param {number[]} changed
 * @return {number[]}
 */
const findOriginalArray = (changed) => {

  /* pseudo code
    keep a map to keep double of items we find with freq
    sort the array
    move curr through changed arr
      curr is a double of some item
        freq is 1
          remove from the map
        else 
          decrease the freq by 1
      else
        map already has its double
          increase the freq of double
        else
          put the double in the map
        push to res array
    check if items are still left in the map
  */

  if (changed.length % 2 === 1) {
    return [];
  }
  const res = [];
  const map = new Map();

  changed.sort((a, b) => a - b);

  for (const curr of changed) {
    if (map.has(curr)) {
      const currValFreq = map.get(curr);

      if (currValFreq === 1) {
        map.delete(curr);
      } else {
        map.set(curr, currValFreq - 1);
      }
    } else {
      const double = curr * 2;

      if (map.has(double)) {
        map.set(double, map.get(double) + 1);
      } else {
        map.set(double, 1);
      }

      res.push(curr);
    }
  }

  return map.size > 0 ? [] : res;
};

