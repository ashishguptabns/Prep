/* Bubble sort */

const bubbleSort = (nums) => {
  /* pseudo code
    idea is to move the biggest item to the right and lock it
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

/* Selection Sort - O(n^2) */

const selectionSort = (nums) => {
  /* pseudo code
        move i through nums
          keep i as min index
          move j through right subarray
            find index j which is smaller than ith item and the smallest in right subarray
          swap items i and j
 */
  const n = nums.length;
  for (let i = 0; i < n - 1; i++) {
    let min = i;
    for (let j = i + 1; j < n; j++) {
      if (nums[j] < nums[min]) {
        min = j;
      }
    }
    [nums[i], nums[min]] = [nums[min], nums[i]];
  }
  return nums;

};

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
      mat[--row][--col] = diagonal.pop();
    }
  };

  for (let col = 0; col < numCols; col++) {
    sort(mat, 0, col);
  }

  for (let row = 1; row < numRows; row++) {
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
    nums[i] = copy[index];
    index--;
  }

  for (let i = 0; i < nums.length; i += 2) {
    nums[i] = copy[index];
    index--;
  }
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
      overlapping interval with last one
        update the end time of last interval
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

  citations.sort((a, b) => a - b);

  let hIndex = 0;
  let n = citations.length;

  for (let i = 0; i < n; i++) {
    hIndex = Math.max(hIndex, Math.min(citations[i], n - i));
  }

  return hIndex;
};

/* Insertion sort */

const insertionSort = (nums) => {
  /* pseudo code
    move i through nums
      keep ith item in value and i in blank
      move from blank to 0 till nums[blank - 1] > value
        keep shifting item at blank - 1 to blank 
        blank to blank - 1
      assign held value to blank index
  */

  for (let i = 1; i < nums.length; i++) {
    const value = nums[i];
    let blank = i;
    while (blank > 0 && nums[blank - 1] > value) {
      nums[blank] = nums[blank - 1];
      blank = blank - 1
    }
    nums[blank] = value;
  }
  return nums;

};

/* Explain merge sort */

const mergeSort = (nums) => {
  /* pseudo code
      divide the array in half and call merge sort for both halves recursively
        sort both halves up the last recursion
        merge both
          have a new array
            loop until both arrays are not empty
              compare and push
          return new array and left overs from both halves
   */
  if (nums.length < 2) {
    return nums;
  }

  const sortNMerge = (left, right) => {
    const sortedArr = [];
    while (left.length && right.length) {
      if (left[0] <= right[0]) {
        sortedArr.push(left.shift());
      } else {
        sortedArr.push(right.shift());
      }
    }

    return [...sortedArr, ...left, ...right];
  };

  const mid = Math.floor(nums.length / 2);
  const left = nums.slice(0, mid);
  const right = nums.slice(mid);

  return sortNMerge(mergeSort(left), mergeSort(right));
};

/* Maximum ice cream bars - At the store, there are n ice cream bars. You are given an array costs of length n, where costs[i] is the price of the ith ice cream bar in coins. The boy initially has coins coins to spend, and he wants to buy as many ice cream bars as possible. 

Note: The boy can buy the ice cream bars in any order.

Return the maximum number of ice cream bars the boy can buy with coins coins.

You must solve the problem by counting sort.
 */
/**
 * @param {number[]} costsArr
 * @param {number} amount
 * @return {number}
 */
const maxIceCream = (costsArr, amount) => {

  /* pseudo code
    counting sort
      keep a freq array to track freq of each cost in costsArr
      move cost from 1 till amount
        track num icecreams that can be bought with this cost
        update coins and max incecreams
  */

  const frequencies = Array(Math.max(...costsArr) + 1).fill(0);
  for (const cost of costsArr) {
    frequencies[cost] += 1;
  }

  let max = 0;
  for (let cost = 1; cost <= amount && cost < frequencies.length; ++cost) {
    const count = Math.min(frequencies[cost], Math.floor(amount / cost));

    amount -= cost * count;
    max += count;
  }
  return max;
};

/* Quick sort */

const quickSort = (nums) => {

  /* pseudo code
      keep a helper function with start and end indices
        choose the start index item as pivot value
        keep an index smaller to keep small values than pivot
        move i from start + 1 to end
          ith item is less than pivot value
            swap with item at smaller index
            increment smaller index
        swap items at smaller and start indices

        repeat for start to smaller - 1 and smaller + 1 to end
  */

  function helper(nums, start, end) {
    if (start >= end) {
      return nums
    }

    const pivotValue = nums[start]
    let smaller = start
    for (var i = start + 1; i <= end; i++) {
      if (nums[i] < pivotValue) {
        smaller++
        [nums[smaller], nums[i]] = [nums[i], nums[smaller]]
      }
    }
    [nums[smaller], nums[start]] = [nums[start], nums[smaller]]

    helper(nums, start, smaller - 1)
    helper(nums, smaller + 1, end)
    return nums
  }

  return helper(nums, 0, nums.length - 1)

};

/* Given a string and sort order, sort the string based on the sort order. */