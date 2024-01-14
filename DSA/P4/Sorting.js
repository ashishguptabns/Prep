/* Group anagrams - Given an array of strings strs, group the anagrams together. You can return the answer in any order. */

/**
 * @param {string[]} strs
 * @return {string[][]}
 */
const groupAnagrams = (strs) => {
  //  sort the strings and maintain a map

  const map = new Map();

  const getHash = (str) => {
    return str.split("").sort().join("");
  };
  strs.forEach((str) => {
    const key = getHash(str);
    if (map.has(key)) {
      map.get(key).push(str);
    } else {
      map.set(key, [str]);
    }
  });

  return [...map.values()];
};

/* Sort the matrix diagonally - Given an m x n matrix mat of integers, sort each matrix diagonal in ascending order and return the resulting matrix.
 */
const diagonalSort = (mat) => {

  /* pseudo code
    go through the cols
      call sort function
        keep diagonal cells in an array
        sort
        put them back
    repeat for rows
  */

  const numRows = mat.length;
  const numCols = mat[0].length;

  const sort = (mat, row, col) => {
    const diagonal = [];

    while (row < mat.length && col < mat[0].length) {
      diagonal.push(mat[row++][col++]);
    }

    diagonal.sort((a, b) => a - b);

    while (row > 0 && col > 0) {
      //  put the elements back
      mat[--row][--col] = diagonal.pop();
    }
  };

  for (let col = 0; col < numCols; col++) {
    //  sort the upper half
    sort(mat, 0, col);
  }

  for (let row = 1; row < numRows; row++) {
    //  sort the lower half
    sort(mat, row, 0);
  }

  return mat;
};

/* Wiggle sort - Given an integer array nums, reorder it such that nums[0] < nums[1] > nums[2] < nums[3]....
 */
const wiggleSort = (nums) => {

  /* pseudo code
    keep a copy of nums
    sort the copy
    move through nums
      at each odd position in nums
        place an item from copy array starting from end moving left
      at each even pos in nums
        place remaining items from copy array
  */

  const copy = [...nums];
  copy.sort((a, b) => a - b);

  let index = copy.length - 1;

  for (let i = 1; i < nums.length; i += 2) {
    //  put big numbers at odd positions
    nums[i] = copy[index];
    index--;
  }

  for (let i = 0; i < nums.length; i += 2) {
    //  put remaining in even positions
    nums[i] = copy[index];
    index--;
  }
};

/* Explain merge sort */

const mergeSort = (nums) => {
  /* pseudo code
      divide the array in half recursively
      sort both halves up the last recursion
      merge both
   */
  if (nums.length < 2) {
    return nums;
  }

  const sortNMerge = (left, right) => {
    const sortedArr = [];
    //  notice that left and right arrays are already sorted
    while (left.length && right.length) {
      if (left[0] <= right[0]) {
        sortedArr.push(left.shift());
      } else {
        sortedArr.push(right.shift());
      }
    }

    //  include whatever is left of both arrays
    return [...sortedArr, ...left, ...right];
  };

  const mid = Math.floor(nums.length / 2);
  const left = nums.slice(0, mid);
  const right = nums.slice(mid);

  return sortNMerge(mergeSort(left), mergeSort(right));
};

/* Selection Sort - O(n^2) */

const selectionSort = (nums) => {
  /* pseudo code
        move i through nums
          keep i as min index
          move through right subarray
            find index j which is smaller than ith item and the smallest in right subarray
          swap items i and j
 */
  const n = nums.length;
  for (let i = 0; i < n - 1; i++) {
    let min = i;
    //  find the smallest index in right subarray which is smaller than i also
    for (let j = i + 1; j < n; j++) {
      if (nums[j] < nums[min]) {
        min = j;
      }
    }
    //  bring small item to left side
    [nums[i], nums[min]] = [nums[min], nums[i]];
  }
  return nums;

};

/* Bubble sort */

const bubbleSort = (nums) => {
  /* pseudo code
    move k from end of nums
      move i from 0 till k
        ith item is bigger than i + 1
          swap
  */

  for (let k = nums.length - 1; k >= 1; k--) {
    for (let i = 0; i < k; i++) {
      if (nums[i] > nums[i + 1]) {
        [nums[i], nums[i + 1]] = [nums[i + 1], nums[i]]
      }
    }
  }
  return nums;

};

/* Merge intervals - Given an array of intervals where intervals[i] = [starti, endi], merge all overlapping intervals, and return an array of the non-overlapping intervals that cover all the intervals in the input.
*/

/**
 * @param {number[][]} intervals
 * @return {number[][]}
 */
const merge = (intervals) => {

  /* pseudo code
    sort the intervals by their start times
    move through intervals
      overlapping interval
        update the end time
      non overlapping interval
        push last interval's [start, end] to merged array
        reassign start and end to current interval
    push last interval's [start, end] to merged array
  */

  if (!intervals || !intervals.length) {
    return [];
  }

  let merged = [];

  intervals.sort((a, b) => a[0] - b[0]);

  let [lastStart, lastEnd] = intervals[0];

  for (const [start, end] of intervals) {
    if (start <= lastEnd) {
      lastEnd = Math.max(end, lastEnd);
    } else {
      merged.push([lastStart, lastEnd]);
      lastStart = start;
      lastEnd = end;
    }
  }

  merged.push([lastStart, lastEnd]);

  return merged;
};

/* Maximum ice cream bars - At the store, there are n ice cream bars. You are given an array costs of length n, where costs[i] is the price of the ith ice cream bar in coins. The boy initially has coins coins to spend, and he wants to buy as many ice cream bars as possible. 

Note: The boy can buy the ice cream bars in any order.

Return the maximum number of ice cream bars the boy can buy with coins coins.

You must solve the problem by counting sort.
 */
/**
 * @param {number[]} costs
 * @param {number} coins
 * @return {number}
 */
const maxIceCream = (costs, coins) => {

  /* pseudo code
    counting sort
      keep a freq array to track freq of each cost in costs
      move cost through coins from 1 till coins
        track icecreams that can be bought with this cost
        update coins and max incecreams
  */

  //  track ice creams for each cost
  const frequencies = Array(Math.max(...costs) + 1).fill(0);
  for (const cost of costs) {
    frequencies[cost] += 1;
  }

  let max = 0;
  //  we are moving from low to high so count will be maximum
  for (let cost = 1; cost <= coins && cost < frequencies.length; ++cost) {
    //  either take all at this cost
    //  or take what remaining coins can afford
    const count = Math.min(frequencies[cost], Math.floor(coins / cost));

    //  update left coins
    coins -= cost * count;
    max += count;
  }
  return max;
};

/* H-index - Given an array of integers citations where citations[i] is the number of citations a researcher received for their ith paper, return the researcher's h-index.

According to the definition of h-index on Wikipedia: The h-index is defined as the maximum value of h such that the given researcher has published at least h papers that have each been cited at least h times.
 */

/**
 * @param {number[]} citations
 * @return {number}
 */
const hIndex = (citations) => {

  /* pseudo code
    sort the citations
    move through citations array
      track max of hIndex so far and (min of curr citation and citations on the right)
  */

  //  sort the citations
  //  travel through the array and keep finding the h index
  citations.sort((a, b) => a - b);

  let hIndex = 0;
  let n = citations.length;

  for (let i = 0; i < n; i++) {
    //  assume ith index has 6 citations but only 3 more papers have higher citation than 6
    hIndex = Math.max(hIndex, Math.min(citations[i], n - i));
  }

  return hIndex;
};

/* Insertion sort */

const insertionSort = (nums) => {
  /* pseudo code
    move i through nums
      keep ith item in value and i in hole
      move from hole to 0 till [hole - 1] > value
        keep shifting item at hole - 1 to hole 
        hole to hole - 1
      assign held value to hole index
  */

  for (let i = 1; i < nums.length; i++) {
    const value = nums[i];
    let hole = i;
    while (hole > 0 && nums[hole - 1] > value) {
      nums[hole] = nums[hole - 1];
      hole = hole - 1
    }
    nums[hole] = value;
  }
  return nums;

};

/* Quick sort */

const quickSort = (nums) => {

  function helper(nums, start, end) {
    if (start >= end) {
      return nums
    }

    const pivotValue = nums[start]
    let smaller = start
    for (var i = start + 1; i <= end; i++) {
      const bigger = i
      if (nums[bigger] < pivotValue) {
        smaller++
        [nums[smaller], nums[bigger]] = [nums[bigger], nums[smaller]]
      }
    }
    [nums[smaller], nums[start]] = [nums[start], nums[smaller]]

    helper(nums, start, smaller - 1)
    helper(nums, smaller + 1, end)
    return nums
  }

  return helper(nums, 0, nums.length - 1)

};
