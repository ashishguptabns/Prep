/* Triangle - Given a triangle array, return the minimum path sum from top to bottom.

For each step, you may move to an adjacent number of the row below. More formally, if you are on index i on the current row, you may move to either index i or index i + 1 on the next row.
 */
/**
 * @param {number[][]} triangle
 * @return {number}
 */
const minimumTotal = (triangle) => {

    /* pseudo code
        move i from bottom second
            move j through columns
                find the min of j or j + 1 item on row i + 1 and add to curr cell [i, j]
    */

    for (let i = triangle.length - 2; i >= 0; i--) {
        for (let j = 0; j < triangle[i].length; j++) {
            triangle[i][j] += Math.min(triangle[i + 1][j], triangle[i + 1][j + 1])
        }
    }
    return triangle[0][0]
};

/* House robber 2 - You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed. All houses at this place are arranged in a circle. That means the first house is the neighbor of the last one. Meanwhile, adjacent houses have a security system connected, and it will automatically contact the police if two adjacent houses were broken into on the same night.

Given an integer array nums representing the amount of money of each house, return the maximum amount of money you can rob tonight without alerting the police.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const rob2 = (nums) => {

    /* pseudo code
        keep two dp arrays
            one robs first house, second robs second house
            notice the cicular nature
        fill both the dp arrays
        return max of the two 
    */

    if (nums.length < 2) {
        return nums[0] || 0;
    }

    const dp1 = [nums[0]];
    const dp2 = [0, nums[1]];

    for (let i = 1; i < nums.length - 1; i++) {
        dp1[i] = Math.max(nums[i] + (dp1[i - 2] || 0), dp1[i - 1]);
    }

    for (let i = 2; i < nums.length; i++) {
        dp2[i] = Math.max(nums[i] + dp2[i - 2], dp2[i - 1]);
    }

    return Math.max(dp1.pop(), dp2.pop());
};

/* Longest Increasing Subsequence - Given an integer array nums, return the length of the longest strictly increasing subsequence
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const lengthOfLIS = (nums) => {

    /* pseudo code
        keep a 1D dp array  
            ith item tells length of LIS till ith index
        move end index from 1 to nums length
            move i index from 0 to end subarray
                record the length of the subseq with curr item and items till ith index
    */

    const dp = Array(nums.length).fill(1);

    for (let end = 1; end < nums.length; end++) {
        for (let i = 0; i < end; i++) {
            if (nums[end] > nums[i]) {
                dp[end] = Math.max(dp[end], dp[i] + 1);
            }
        }
    }

    return Math.max(...dp);
};

/* Longest Palindromic substring - Given a string s, return the longest Palindromic substring in s. */
/**
 * @param {string} s
 * @return {string}
 */
const longestPalindrome = (s) => {

    /* pseudo code
        we will keep a 2D dp array 
            each [i, j] will have true/false for palindrome starting at i ending at j
        handle special cases of substrings of length 1 and 2
        move through lengths of 3 and more
            move i through the substring till (strLength - currLen)
                fill the dp index        
                track max length and start index
     */

    const strLength = s.length
    if (strLength <= 1) {
        return s
    }

    const dpMatrix = Array.from({ length: strLength }, () => Array(strLength).fill(false))

    //  all substrings of length 1 are palindrome
    for (let i = 0; i < strLength - 1; i++) {
        //  starting and ending at i
        dpMatrix[i][i] = true
    }

    // to track palindrome start index
    let startIndex = 0
    let maxLength = 1

    //  handle substrings of length 2
    for (let i = 0; i < strLength - 1; i++) {
        if (s[i] == s[i + 1]) {
            dpMatrix[i][i + 1] = true
            startIndex = i
            maxLength = 2
        }
    }

    //  handle substrings of length 3 or more
    for (let currLen = 3; currLen <= strLength; currLen++) {
        for (let i = 0; i <= strLength - currLen; i++) {
            //  end index for substring
            const j = i + currLen - 1

            //  check if the substring is a palindrome
            if (s[i] == s[j] && dpMatrix[i + 1][j - 1]) {
                dpMatrix[i][j] = true
                startIndex = i
                maxLength = currLen
            }
        }
    }

    return s.substring(startIndex, startIndex + maxLength)
};

/* House Robber - You are a professional robber planning to rob houses along a street. Each house has a certain amount of money stashed, the only constraint stopping you from robbing each of them is that adjacent houses have security systems connected and it will automatically contact the police if two adjacent houses were broken into on the same night.

Given an integer array nums representing the amount of money of each house, return the maximum amount of money you can rob tonight without alerting the police.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const rob = (nums) => {

    /* pseudo code
        build a dp array in which ith item is how much money has been collected till ith house
        bases cases are 1st and 2nd houses
            remember the adjacent condition
        at each ith house either take ith house and money till i-2 house or take money till i-1 house
    */

    if (!nums || !nums.length) {
        return 0;
    }

    const n = nums.length;

    if (n === 1) {
        return nums[0];
    }

    const dp = Array(n);

    dp[0] = nums[0];
    dp[1] = Math.max(nums[0], nums[1]);

    for (let i = 2; i < n; i++) {
        dp[i] = Math.max(dp[i - 1], dp[i - 2] + nums[i]);
    }

    return dp[n - 1];
};

/* Maximum Length of Repeated Subarray - Given two integer arrays nums1 and nums2, return the maximum length of a subarray that appears in both arrays.
 */
/**
 * @param {number[]} nums1
 * @param {number[]} nums2
 * @return {number}
 */
const findLength = (nums1, nums2) => {

    /* pseudo code
        we will have 2D dp array 
            each item [i, j] tells repeat subarray length in strings ending at i in nums1 and j in nums2
        move i through nums1
            move j through nums2
                fill the dp[i+1][j+1]
                track max len
    */

    const dp = Array(nums1.length + 1).fill().map(() => Array(nums2.length + 1).fill(0));

    let maxLen = 0;

    for (let i = 0; i < nums1.length; i++) {
        for (let j = 0; j < nums2.length; j++) {
            if (nums1[i] === nums2[j]) {
                dp[i + 1][j + 1] = dp[i][j] + 1;
                maxLen = Math.max(maxLen, dp[i + 1][j + 1]);
            }
        }
    }

    return maxLen;
};

/* Coin Change - You are given an integer array coins representing coins of different denominations and an integer amount representing a total amount of money.

Return the fewest number of coins that you need to make up that amount. If that amount of money cannot be made up by any combination of the coins, return -1.

You may assume that you have an infinite number of each kind of coin.
 */
/**
 * @param {number[]} coins
 * @param {number} amount
 * @return {number}
 */
const coinChange = (coins, amount) => {

    /* pseudo code
        keep a 1D dp array
            ith index tells about min number of coins needed to make i amount
        Go through each coin 
            run i from coin till amount
                either keep the (coin and the min coins to make up amount - coin) or leave the number as it is
    */

    const dp = Array(Infinity).fill(Infinity);

    dp[0] = 0;

    for (const coin of coins) {
        for (let i = coin; i <= amount; i++) {
            dp[i] = Math.min(dp[i], 1 + dp[i - coin]);
        }
    }

    return dp[amount] === Infinity ? -1 : dp[amount];
};

/* Perfect Squares - Given an integer n, return the least number of perfect square numbers that sum to n.
 */
/**
 * @param {number} n
 * @return {number}
 */
const numSquares = (n) => {

    /* pseudo code
        keep a 1D dp array
            each ith item tells number of min squares to make i
        move num from 1 to n
            try to find all the numbers k which square less than num
                either keep (k and dp[num - k**2]) or leave the count as it is
    */

    const dp = Array(n + 1).fill(Infinity)
    dp[0] = 0

    for (let i = 1; i <= n; i++) {
        for (let j = 1; j ** 2 <= i; j++) {
            dp[i] = Math.min(1 + dp[i - j ** 2], dp[i])
        }
    }
    return dp.at(-1)
};

/* Longest Common Subsequence - Given two strings text1 and text2, return the length of their longest common subsequence. If there is no common subsequence, return 0.
 */
/**
 * @param {string} text1
 * @param {string} text2
 * @return {number}
 */
const longestCommonSubsequence = (text1, text2) => {
    /* pseudo code
        keep a 2D dp array
            [i, j] tells longest common subseq in text1 till ith index and text2 till jth index
            Don't have to be contiguous 
        move i through text1 
            move j through text2 
                if i and j are same
                    increase the count
                else
                    max of dp[i - 1, j] or dp[i, j - 1]
    */

    const dp = Array(text1.length + 1).fill().map(() => Array(text2.length + 1).fill(0))

    for (let i = 1; i <= text1.length; i++) {
        for (let j = 1; j <= text2.length; j++) {
            if (text1[i - 1] === text2[j - 1]) {
                dp[i][j] = 1 + dp[i - 1][j - 1];
            } else {
                dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
            }
        }
    }

    return dp[text1.length][text2.length];
};

/* Unique Paths - There is a robot on an m x n grid. The robot is initially located at the top-left corner (i.e., grid[0][0]). The robot tries to move to the bottom-right corner (i.e., grid[m - 1][n - 1]). The robot can only move either down or right at any point in time.

Given the two integers m and n, return the number of possible unique paths that the robot can take to reach the bottom-right corner.
 */
/**
 * @param {number} m
 * @param {number} n
 * @return {number}
 */
const uniquePaths = (m, n) => {
    /* pseudo code
        keep a 2D dp array
            [i, j] contains number of ways to come to this coordinate
        base cases - first row and first col
            only one way
        go through all the cells
            add ways to come from top and left
    */

    const dp = Array.from({ length: m }, () => new Array(n).fill(0))

    for (let row = 0; row < m; row++) {
        dp[row][0] = 1
    }
    for (let col = 0; col < n; col++) {
        dp[0][col] = 1
    }

    for (let row = 1; row < m; row++) {
        for (let col = 1; col < n; col++) {
            dp[row][col] = dp[row - 1][col] + dp[row][col - 1]
        }
    }

    return dp[m - 1][n - 1]
};

/* Climbing Stairs - You are climbing a staircase. It takes n steps to reach the top.
Each time you can either climb 1 or 2 steps. In how many distinct ways can you climb to the top?
 */
/**
 * @param {number} n
 * @return {number}
 */
const climbStairs = (n) => {

    /* pseudo code
        two ways to reach any level
        base cases
            1st and 2nd stairs
    */

    if (n == 1) {
        return 1
    }
    const dp = Array(n + 1)

    dp[1] = 1
    dp[2] = 2

    for (let i = 3; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2]
    }

    return dp[n]
};

/* Count strictly increasing subarrays
 */
const countIncreasingSubarraysDP = (arr) => {

    /* pseudo code
        keep a dp array
            ith item tells num of increasing subarrays till ith index
        move through the array
            if curr item is more than last one
                dp[i] = dp[i - 1] + 1
            else 
                dp[i] = 1
            keep adding the count of subarrays
    */

    const n = arr.length;
    const dp = Array(n).fill(1);
    let count = 0;

    for (let i = 1; i < n; i++) {
        if (arr[i] > arr[i - 1]) {
            dp[i] = dp[i - 1] + 1;
        } else {
            dp[i] = 1;
        }

        count += dp[i];
    }

    return count;
};

/* Count square submatrices with all 1s - Given a m * n matrix of ones and zeros, return how many square submatrices have all ones.
 */
const countSquares = (matrix) => {

    /* pseudo code
        keep a 2D dp array
            each cell [i, j] will tell how many square submatrices with all 1s are there before this cell
        move through each row
            move through each col
                ignore cells with 0
                handle first row and col
                keep tracking the count
                handle normal cell
                    take min of 3 on top and left
    */

    let count = 0

    const dp = new Array(matrix.length).fill().map(() => new Array(matrix[0].length).fill(0))

    for (let row = 0; row < matrix.length; row++) {
        for (let col = 0; col < matrix[0].length; col++) {
            if (matrix[row][col] === 0) {
            } else if (row === 0 || col === 0) {
                dp[row][col] = 1
                count += dp[row][col]
            } else {
                const side = Math.min(dp[row - 1][col], dp[row - 1][col - 1], dp[row][col - 1])

                dp[row][col] = side + 1

                count += dp[row][col]
            }
        }
    }

    return count
};

/* Paint house - You are given a matrix where each row represents a house, and each column represents the cost of painting that house with a particular color. The goal is to find the minimum cost to paint all the houses such that no two adjacent houses have the same color. Do it for 3 colors or 3 columns
*/

/**
 * @param {number[][]} costs - Cost matrix where costs[i][j] represents the cost of painting house i with color j.
 * @return {number} - Minimum cost to paint all houses.
 */
const minCost = (costs) => {

    /* pseudo code
        keep a 2D dp array
            each cell [i, j] will tell the min cost of painting ith house with jth color
        imagine a matrix with rows as houses and cols as colors
        base case - first house can be painted by any of 3 colors
        move through each house
            choose the cost of painting with a color with a different color for prev house (min cost)
    */

    if (costs.length === 0) {
        return 0;
    }

    const n = costs.length;
    const dp = Array.from({ length: n }, () => Array(3).fill(0));

    dp[0] = [...costs[0]];

    for (let house = 1; house < n; house++) {
        dp[house][0] = costs[house][0] + Math.min(dp[house - 1][1], dp[house - 1][2]);
        dp[house][1] = costs[house][1] + Math.min(dp[house - 1][0], dp[house - 1][2]);
        dp[house][2] = costs[house][2] + Math.min(dp[house - 1][0], dp[house - 1][1]);
    }

    return Math.min(...dp[n - 1]);
};

/* Edit distance - Given two strings word1 and word2, return the minimum number of operations required to convert word1 to word2.

You have the following three operations permitted on a word:

Insert a character
Delete a character
Replace a character
 */

/**
 * @param {string} word1
 * @param {string} word2
 * @return {number}
 */
const minDistance = (word1, word2) => {

    /* pseudo code
        keep a 2D dp array
            each cell [i, j] tells min edits needed to convert word1 till ith into word2 till jth
        move through chars of word1
            move through chars of word2
                if we choose 0 chars of word1
                    edits needed = chars of word2
                same if we choose 0 chars of word2
                    edits needed = chars of word1
                if chars in word1 and word2 are same
                    no edit needed - same edit value till last chars selection
                else
                    choose min of 3 possible combos and add 1 as mandatory edit
    */

    const dp = Array(word1.length + 1).fill().map(() => Array(word2.length + 1));

    for (let r = 0; r <= word1.length; r++) {
        for (let c = 0; c <= word2.length; c++) {
            if (r === 0) {
                dp[r][c] = c;
            } else if (c === 0) {
                dp[r][c] = r;
            } else if (word1[r - 1] === word2[c - 1]) {
                dp[r][c] = dp[r - 1][c - 1];
            } else {
                dp[r][c] = Math.min(dp[r][c - 1], dp[r - 1][c - 1], dp[r - 1][c]) + 1;
            }
        }
    }

    return dp[word1.length][word2.length];
};

/* Unique Binary Search Trees - Given an integer n, return the number of structurally unique BST's (binary search trees) which has exactly n nodes of unique values from 1 to n.
 */
/**
 * @param {number} n
 * @return {number}
 */
const numTrees = (numNodes) => {
    /* pseudo code
        keep a 1D dp array
            ith item tells num of BSTs with i nodes
        base case is 0 node
            1 BST
        move i through num nodes
            move j from 1 till i
                j is the root
                left subtree is j - 1
                right is i - j
                dp[i] = product of dp[j - 1] and dp[i - j]
    */

    const dpArr = new Array(numNodes + 1).fill(0)

    dpArr[0] = 1

    for (let i = 1; i <= numNodes; i++) {
        for (let j = 1; j <= i; j++) {
            dpArr[i] += dpArr[j - 1] * dpArr[i - j]
        }
    }

    return dpArr[numNodes]
};

/* Maximal Square - Given an m x n binary matrix filled with 0's and 1's, find the largest square containing only 1's and return its area.
 */

/**
 * @param {character[][]} matrix
 * @return {number}
 */
const maximalSquare = (matrix) => {

    /* pseudo code
        keep a 2D dp array
            [i, j] tells size of square with 1s
        move r through rows
            move c throughs cols
                found a cell with 1
                    find min of 3 cells and add 1
                    track max
        return max**2
    */

    const dp = Array(m.length + 1).fill()
        .map(_ => Array(m[0].length + 1).fill(0))

    let ans = 0
    for (let r = 1; r <= m.length; r++) {
        for (let c = 1; c <= m[0].length; c++) {
            if (m[r - 1][c - 1] === '1') {
                dp[r][c] = 1 +
                    Math.min(dp[r - 1][c], dp[r - 1][c - 1], dp[r][c - 1])
                ans = Math.max(ans, dp[r][c] ** 2)
            }
        }
    }
    return ans
};

/* Minimum Path Sum - Given a m x n grid filled with non-negative numbers, find a path from top left to bottom right, which minimizes the sum of all numbers along its path.

Note: You can only move either down or right at any point in time.
*/
/**
 * @param {number[][]} grid
 * @return {number}
 */
const minPathSum = (grid) => {

    /* pseudo code
        move r through rows
            move c through cols
                ignore [0, 0] cell
                find path sum till top cell or left cell and add curr cell value
    */

    for (let r = 0; r < grid.length; r++) {
        for (let c = 0; c < grid[0].length; c++) {
            if (r === 0 && c === 0) {
                continue;
            }

            const leftMinSum = c > 0 ? grid[r][c - 1] : Infinity;
            const topLeftSum = r > 0 ? grid[r - 1][c] : Infinity;

            grid[r][c] = Math.min(leftMinSum, topLeftSum) + grid[r][c];
        }
    }

    return grid[grid.length - 1][grid[0].length - 1];
};

/* Partition Equal Subset Sum - Given an integer array nums, return true if you can partition the array into two subsets such that the sum of the elements in both subsets is equal or false otherwise.
 */

/**
 * @param {number[]} nums
 * @return {boolean}
 */
const canPartition = (nums) => {
    let sum = nums.reduce((prevVal, currValue) => prevVal + currValue, 0);

    if (sum % 2 !== 0) {
        return false;
    }

    let target = sum / 2;
    let dp = new Set();
    dp.add(0);

    for (let i = nums.length - 1; i >= 0; i--) {
        let nextDp = new Set();

        for (const ele of dp.values()) {
            let newVal = ele + nums[i];
            if (newVal === target) {
                return true;
            }

            nextDp.add(newVal);
        }

        dp = new Set([...dp, ...nextDp]);
    }

    return false;
};

/* Longest Increasing Path in a Matrix - Given an m x n integers matrix, return the length of the longest increasing path in matrix.

From each cell, you can either move in four directions: left, right, up, or down. You may not move diagonally or move outside the boundary (i.e., wrap-around is not allowed).
 */
/**
 * @param {number[][]} matrix
 * @return {number}
 */
const longestIncreasingPath = (matrix) => {
    //  Memoization table to store calculated results
    //  this will store the length of increasing path from a point [i, j]
    const dp = {};

    // Recursive function to find the length of the increasing path
    function solve(i, j, val) {
        // Base cases: out of bounds or not an increasing path
        if (i < 0 || j < 0 || i >= matrix.length || j >= matrix[0].length) {
            return 0;
        }
        if (matrix[i][j] <= val) {
            //  not an increasing path
            return 0;
        }

        // Check if the result for the current position is already memoized
        if (dp[`${i}-${j}`]) {
            return dp[`${i}-${j}`];
        }

        // Explore all possible directions and find the length of the increasing path
        let down = solve(i + 1, j, matrix[i][j]) + 1;
        let up = solve(i - 1, j, matrix[i][j]) + 1;
        let right = solve(i, j + 1, matrix[i][j]) + 1;
        let left = solve(i, j - 1, matrix[i][j]) + 1;

        // Save the maximum length for the current position
        dp[`${i}-${j}`] = Math.max(down, up, right, left);

        return dp[`${i}-${j}`];
    }

    // Iterate through each element in the matrix to find the maximum increasing path
    let max = 0;
    for (let i = 0; i < matrix.length; i++) {
        for (let j = 0; j < matrix[0].length; j++) {
            // Skip if the result for the current position is already calculated
            if (dp[`${i}-${j}`] != undefined) {
                continue;
            }
            // Update the maximum length by considering the increasing path starting from the current position
            max = Math.max(max, solve(i, j, -1));
        }
    }

    // Return the length of the longest increasing path
    return max;
};

/* Word break - Given a string s and a dictionary of strings wordDict, return true if s can be segmented into a space-separated sequence of one or more dictionary words.

Note that the same word in the dictionary may be reused multiple times in the segmentation.
 */
/**
 * @param {string} s
 * @param {string[]} wordDict
 * @return {boolean}
 */
const wordBreak = (s, wordDict) => {

    /* pseudo code
        keep a dp array
            each ith item tells if substring till ith index can be broken into words from dictionary
        move through the given string
            go through each word of dictionary
                see if current substring can be formed by combining curr word and (substring - word)
    */

    const n = s.length;
    const dp = new Array(n + 1).fill(false);
    dp[0] = true;

    for (let i = 1; i <= n; i++) {
        for (const word of wordDict) {
            if (i >= word.length && dp[i - word.length]
                && s.substring(i - word.length, i) === word) {
                dp[i] = true;
                break;
            }
        }
    }

    return dp[n];
};

/* Decode Ways - A message containing letters from A-Z can be encoded into numbers using the following mapping:

'A' -> "1"
'B' -> "2"
...
'Z' -> "26"
To decode an encoded message, all the digits must be grouped then mapped back into letters using the reverse of the mapping above (there may be multiple ways). For example, "11106" can be mapped into:

"AAJF" with the grouping (1 1 10 6)
"KJF" with the grouping (11 10 6)
Note that the grouping (1 11 06) is invalid because "06" cannot be mapped into 'F' since "6" is different from "06".

Given a string s containing only digits, return the number of ways to decode it.
*/

const numDecodings = (s) => {

    /* pseudo code
        keep a dp array
            ith item tells number of ways to decode s till ith index
        base cases
            length 0 and 1 -> only 1 way
        move through the string
            find last one and two digits of this substring
            last digit not 0
                add dp[i - 1]
            last two digits are between 10 and 26
                add dp[i - 2]
    */

    if (!s || s[0] === '0') {
        return 0
    }

    const dp = Array(s.length + 1).fill(0)
    dp[0] = 1
    dp[1] = 1

    for (let i = 2; i <= s.length; i++) {
        const lastDigit = Number(s[i - 1])
        const last2Digits = Number(s[i - 2] + s[i - 1])

        if (lastDigit !== 0) {
            dp[i] += dp[i - 1]
        }
        if (last2Digits >= 10 && last2Digits <= 26) {
            dp[i] += dp[i - 2]
        }
    }

    return dp.at(-1)
};

/* Knight Dialer - Given an integer n, return how many distinct phone numbers of length n we can dial.

You are allowed to place the knight on any numeric cell initially and then you should perform n - 1 jumps to dial a number of length n. All jumps should be valid knight jumps.
 */
/**
 * @param {number} n
 * @return {number}
 */
const knightDialer = (n) => {

    /* pseudo code
        keep a dp array 
            ith item tells num of phone numbers we can dial of length i
            base case is 1
                can place the knight on any digit
        move through diff lengths
            keep a diff dp array to store counts till curr len
                move through all digits till 10
                    move through next possible digits for curr digit
                        add count of nums till curr digit and next digit for prev length
            reassign the dp arr for prev length
    */

    const mod = 10 ** 9 + 7

    //  map of starting position vs next positions
    const adjMap = {
        1: [6, 8],
        2: [7, 9],
        3: [4, 8],
        4: [3, 9, 0],
        5: [],
        6: [1, 7, 0],
        7: [2, 6],
        8: [1, 3],
        9: [2, 4],
        0: [4, 6],
    }

    //  base case is length 1 as can place on any digit
    const currLenDpArr = new Array(10).fill(1)

    //  start from length 2 till n
    for (let currLen = 2; currLen <= n; currLen++) {
        //  store counts for current length
        const newLenDpArr = new Array(10).fill(0)
        for (let digit = 0; digit < 10; digit++) {
            for (const nextDigit of adjMap[digit]) {
                //  Add the count from the previous length at the next move's position to newDpArr[digit].
                newLenDpArr[digit] = (newLenDpArr[digit] + currLenDpArr[nextDigit]) % mod
            }
        }

        currLenDpArr = newLenDpArr
    }

    //  sum is the accumulator
    return currLenDpArr.reduce((sum, val) => (sum + val) % mod, 0)
};

/* Count sorted vowel strings - Given an integer n, return the number of strings of length n that consist only of vowels (a, e, i, o, u) and are lexicographically sorted.
 */
const countVowelStrings = (n) => {

    /* pseudo code
        backtrack with curr index and curr str length
            run i through the vowel string starting at curr index
                backtrack with i and curr str length + 1 (choosing one vowel)
            keep the count in memo for this index and str length
    */

    const memo = {}

    const backTrack = (index, strLength) => {
        const key = index + '_' + strLength

        if (memo[key]) {
            return memo[key]
        }
        if (strLength === n) {
            return 1
        }
        if (strLength > n) {
            return 0
        }

        let count = 0

        for (let i = index; i < 5; i++) {
            count += backTrack(i, strLength + 1)
        }

        memo[key] = count

        return count
    }

    return backTrack(0, 0)
};

/* All possible full binary trees - Given an integer n, return a list of all possible full binary trees with n nodes. Each node of each tree in the answer must have Node.val == 0.
 */
const allPossibleFBT = (n) => {
    const memo = {}
    const buildTree = (size) => {
        if (size === 1) {
            return [new TreeNode()]
        }
        if (size % 2 === 0) {
            return []
        }
        if (memo[size]) {
            return memo[size]
        }
        const trees = []
        for (let i = 1; i < size; i += 2) {
            const leftTs = buildTree(i)
            const rightTs = buildTree(size - i - 1)
            if (leftTs && rightTs) {
                for (const left of leftTs) {
                    for (const right of rightTs) {
                        const root = new TreeNode(0, left, right)
                        trees.push(root)
                    }
                }
            }
        }
        memo[size] = trees

        return trees
    }
    return buildTree(n)
};

/* Unique Paths 2 - You are given an m x n integer array grid. There is a robot initially located at the top-left corner (i.e., grid[0][0]). The robot tries to move to the bottom-right corner (i.e., grid[m - 1][n - 1]). The robot can only move either down or right at any point in time.

An obstacle and space are marked as 1 or 0 respectively in grid. A path that the robot takes cannot include any square that is an obstacle.

Return the number of possible unique paths that the robot can take to reach the bottom-right corner.
 */
/**
 * @param {number[][]} obstacleGrid
 * @return {number}
 */
const uniquePathsWithObstacles = (obstacleGrid) => {

    const arr = Array(m.length).fill().map(_ => Array(m[0].length).fill(0))

    for (let r = 0; r < m.length; r++) {
        if (m[r][0] === 1) {
            break
        }
        arr[r][0] = 1
    }
    for (let c = 0; c < m[0].length; c++) {
        if (m[0][c] === 1) {
            break
        }
        arr[0][c] = 1
    }

    for (let r = 1; r < m.length; r++) {
        for (let c = 1; c < m[0].length; c++) {
            if (m[r][c] !== 1) {
                arr[r][c] = arr[r - 1][c] + arr[r][c - 1]
            }
        }
    }
    return arr.at(-1).at(-1)
};

/* Dungeon Game - The demons had captured the princess and imprisoned her in the bottom-right corner of a dungeon. The dungeon consists of m x n rooms laid out in a 2D grid. Our valiant knight was initially positioned in the top-left room and must fight his way through dungeon to rescue the princess.

The knight has an initial health point represented by a positive integer. If at any point his health point drops to 0 or below, he dies immediately.

Some of the rooms are guarded by demons (represented by negative integers), so the knight loses health upon entering these rooms; other rooms are either empty (represented as 0) or contain magic orbs that increase the knight's health (represented by positive integers).

To reach the princess as quickly as possible, the knight decides to move only rightward or downward in each step.

Return the knight's minimum initial health so that he can rescue the princess.

Note that any room can contain threats or power-ups, even the first room the knight enters and the bottom-right room where the princess is imprisoned. */

var calculateMinimumHP = function (m) {

    const dp = Array(m.length + 1).fill()
        .map(() => Array(m[0].length + 1).fill(Infinity))

    dp[m.length][m[0].length - 1] = dp[m.length - 1][m[0].length] = 1

    for (let r = m.length - 1; r >= 0; r--) {
        for (let c = m[0].length - 1; c >= 0; c--) {
            const min = Math.min(dp[r + 1][c], dp[r][c + 1]) - m[r][c]
            dp[r][c] = Math.max(1, min)
        }
    }
    return dp[0][0]
};

/* Different Ways to Add Parentheses - Given a string expression of numbers and operators, return all possible results from computing all the different possible ways to group numbers and operators. You may return the answer in any order.

The test cases are generated such that the output values fit in a 32-bit integer and the number of different results does not exceed 104. */

var diffWaysToCompute = function (s) {
    if (s.length < 3) {
        return [s]
    }

    const ans = []
    for (let i = 0; i < s.length; i++) {
        const c = s[i]
        if (isNaN(c)) {
            const left = diffWaysToCompute(s.slice(0, i))
            const right = diffWaysToCompute(s.slice(i + 1))

            for (let l of left) {
                for (let r of right) {
                    l = Number(l)
                    r = Number(r)
                    if (c === '-') {
                        ans.push(l - r)
                    } else if (c === '+') {
                        ans.push(l + r)
                    } else {
                        ans.push(l * r)
                    }
                }
            }
        }
    }

    if (ans.length) {
        return ans
    }
    return [s]
};

/* Ugly Number II - An ugly number is a positive integer whose prime factors are limited to 2, 3, and 5.

Given an integer n, return the nth ugly number.
 */
/**
 * @param {number} n
 * @return {number}
 */
const nthUglyNumber = (n) => {

    /* pseudo code
        we will keep a dp array 
            ith item is ith ugly number
        keep tracking counts of 2, 3 and 5 we have considered so far
        move i till n
            find min of three possible numbers
            increase the count of chosen num accordingly
    */

    let dp = [1]
    let c2 = c3 = c5 = 0
    for (let i = 1; i < n; i++) {
        dp[i] = Math.min(2 * dp[c2], 3 * dp[c3], 5 * dp[c5])
        if (2 * dp[c2] === dp[i]) {
            c2++
        }
        if (3 * dp[c3] === dp[i]) {
            c3++
        }
        if (5 * dp[c5] === dp[i]) {
            c5++
        }
    }
    return dp[n - 1]
};

/* Palindrome Partitioning - Given a string s, partition s such that every 
substring of the partition is a palindrome. Return all possible palindrome partitioning of s.
 */
/**
 * @param {string} s
 * @return {string[][]}
 */
const partition = (s) => {

    /* pseudo code
        Keep a 2D DP array 
            store palindrome true/false for every substring starting at i and ending at j
        do a DFS and explore all possible substrings
    */

    const dp = Array.from({ length: s.length }, () => Array(s.length).fill(false))

    const res = []

    const dfs = (currIndex, currList) => {
        if (currIndex == s.length) {
            res.push([...currList])
        }

        for (let end = currIndex; end < s.length; end++) {
            if (s[currIndex] === s[end] && (end - currIndex <= 2 || dp[currIndex + 1][end - 1])) {
                dp[currIndex][end] = true
                dfs(end + 1, [...currList, s.slice(currIndex, end + 1)])
            }
        }
    }

    dfs(0, [])

    return res
};

/* Interleaving string - Given strings s1, s2, and s3, find whether s3 is formed by an interleaving of s1 and s2.

An interleaving of two strings s and t is a configuration where s and t are divided into n and m 
substrings
 respectively, such that:

s = s1 + s2 + ... + sn
t = t1 + t2 + ... + tm
|n - m| <= 1
The interleaving is s1 + t1 + s2 + t2 + s3 + t3 + ... or t1 + s1 + t2 + s2 + t3 + s3 + ...
Note: a + b is the concatenation of strings a and b.
 */

/**
 * @param {string} s1
 * @param {string} s2
 * @param {string} s3
 * @return {boolean}
 */
const isInterleave = (s1, s2, s3) => {

    /* pseudo code
        keep a 2D dp array
            [i, j] tells if (i + j) length of s3 can be made with i length of s1 and j length of s2
        move i through s1
            move j through s2
                s1 is empty
                    match chars of s2 and s3 at j - 1 plus dp[i][j-1] should be true
                s2 is empty
                    repeat for s2
                else
                    match end char of s3 with end char of s2 or s1
    */

    if (s1.length + s2.length !== s3.length) {
        return false;
    }

    const dp = [];

    for (let i = 0; i <= s1.length; i++) {
        dp[i] = [];
        for (let j = 0; j <= s2.length; j++) {
            if (i === 0 && j === 0) {
                dp[i][j] = true;
            } else if (i === 0) {
                dp[i][j] = dp[i][j - 1] && s2[j - 1] === s3[j - 1];
            } else if (j === 0) {
                dp[i][j] = dp[i - 1][j] && s1[i - 1] === s3[i - 1];
            } else {
                dp[i][j] =
                    (dp[i][j - 1] && s2[j - 1] === s3[i + j - 1]) ||
                    (dp[i - 1][j] && s1[i - 1] === s3[i + j - 1]);
            }
        }
    }

    return dp[s1.length][s2.length];
};

/* 1638. Count Substrings That Differ by One Character */

/**
 * @param {string} s
 * @param {string} t
 * @return {number}
 */
var countSubstrings = function (s, t) {
    const leftDp = Array(s.length + 1).fill()
        .map(_ => Array(t.length + 1).fill(0))
    const rightDp = Array(s.length + 1).fill()
        .map(_ => Array(t.length + 1).fill(0))

    for (let i = 1; i <= s.length; i++) {
        for (let j = 1; j <= t.length; j++) {
            if (s[i - 1] === t[j - 1]) {
                leftDp[i][j] = 1 + leftDp[i - 1][j - 1]
            }
        }
    }

    for (let i = s.length - 1; i >= 0; i--) {
        for (let j = t.length - 1; j >= 0; j--) {
            if (s[i] === t[j]) {
                rightDp[i][j] = 1 + rightDp[i + 1][j + 1]
            }
        }
    }

    let count = 0
    for (let i = 0; i < s.length; i++) {
        for (let j = 0; j < t.length; j++) {
            if (s[i] === t[j]) {
                continue
            }
            count += (leftDp[i][j] + 1) * (rightDp[i + 1][j + 1] + 1)
        }
    }

    return count
};

/* 877. Stone Game */

/* 647. Palindromic Substrings */

/* 2673. Make Costs of Paths Equal in a Binary Tree */

/**
 * @param {number} n
 * @param {number[]} cost
 * @return {number}
 */
var minIncrements = function (n, cost) {
    let ans = 0

    const travel = (node) => {
        if (node * 2 > n) {
            return cost[node - 1]
        }
        const left = travel(node * 2)
        const right = travel(node * 2 + 1)
        ans += Math.max(left, right) - Math.min(left, right)
        return cost[node - 1] + Math.max(left, right)
    }
    travel(1)

    return ans
};

/* 1786. Number of Restricted Paths From First to Last Node */

/* Minimum Time to Make Rope Colorful - Alice has n balloons arranged on a rope. You are given a 0-indexed string color where colors[i] is the color of the ith balloon.

Alice wants the rope to be colorful. She does not want two consecutive balloons to be of the same color, so she asks Bob for help. Bob can remove some balloons from the rope to make it colorful. You are given a 0-indexed integer array neededTime where neededTime[i] is the time (in seconds) that Bob needs to remove the ith balloon from the rope.

Return the minimum time Bob needs to make the rope colorful.
 */

/**
 * @param {string} colors
 * @param {number[]} neededTime
 * @return {number}
 */
var minCostColor = function (colors, neededTime) {
    let time = 0
    for (let i = 1; i < colors.length; i++) {
        if (colors[i] === colors[i - 1]) {
            time += Math.min(neededTime[i], neededTime[i - 1])
            neededTime[i] = Math.max(neededTime[i], neededTime[i - 1])
        }
    }

    return time
};

/* 678. Valid Parenthesis String */

/**
 * @param {string} s
 * @return {boolean}
 */
var checkValidString = function (s) {
    let openStack = [];
    let asteriskStack = [];

    for (let i = 0; i < s.length; i++) {
        if (s[i] === '(') {
            openStack.push(i);
        } else if (s[i] === '*') {
            asteriskStack.push(i);
        } else {
            if (openStack.length > 0) {
                openStack.pop();
            } else if (asteriskStack.length > 0) {
                asteriskStack.pop();
            } else {
                return false;
            }
        }
    }

    while (openStack.length > 0 && asteriskStack.length > 0) {
        if (openStack.pop() > asteriskStack.pop()) {
            return false;
        }
    }

    return openStack.length === 0;
};


/* 1334. Find the City With the Smallest Number of Neighbors at a Threshold Distance */

var findTheCity = function (n, edges, distanceThreshold) {
    const dp = Array(n).fill().map(_ => Array(n).fill(Infinity))

    for (let i = 0; i < n; i++) {
        dp[i][i] = 0
    }

    for (const [from, to, dist] of edges) {
        dp[from][to] = dp[to][from] = dist
    }

    for (let mid = 0; mid < n; mid++) {
        for (let from = 0; from < n; from++) {
            for (let to = 0; to < n; to++) {
                dp[from][to] = Math.min(dp[from][to],
                    dp[from][mid] + dp[mid][to])
            }
        }
    }

    let min = n
    let city = 0
    for (let from = 0; from < n; from++) {
        let allowedNextNum = 0
        for (let to = 0; to < n; to++) {
            if (dp[from][to] <= distanceThreshold) {
                allowedNextNum++
            }
        }
        if (allowedNextNum > min) {
            continue
        }
        min = allowedNextNum
        city = from
    }
    return city
};

/* 2304. Minimum Path Cost in a Grid */

var minPathCost = function (grid, moveCost) {
    const dp = Array(m.length).fill()
        .map(_ => Array(m[0].length).fill(0))

    for (let c = 0; c < m[0].length; c++) {
        dp[0][c] = m[0][c]
    }

    for (let r = 1; r < m.length; r++) {
        for (let c = 0; c < m[0].length; c++) {
            dp[r][c] = Infinity
            for (let k = 0; k < m[0].length; k++) {
                const rowCost = cost[m[r - 1][k]][c]
                dp[r][c] = Math.min(dp[r][c],
                    m[r][c] + dp[r - 1][k] + rowCost)
            }
        }
    }

    return Math.min(...dp.at(-1))
};

/* 1395. Count Number of Teams */

var numTeams = function (arr) {
    const dp = Array(arr.length).fill().map(_ => [0, 0])

    let ans = 0

    for (let i = 0; i < arr.length; i++) {
        for (let j = i + 1; j < arr.length; j++) {
            if (arr[j] > arr[i]) {
                dp[j][0]++
                ans += dp[i][0]
            }
            if (arr[i] > arr[j]) {
                dp[j][1]++
                ans += dp[i][1]
            }
        }
    }

    return ans
};

/* 983. Minimum Cost For Tickets */

var mincostTickets = function (days, costs) {
    const lastDay = days.at(-1)
    const dp = Array(lastDay + 1).fill(0)

    for (let day = 1; day <= lastDay; day++) {
        if (!days.includes(day)) {
            dp[day] = dp[day - 1]
        } else {
            dp[day] = Math.min(
                dp[day - 1] + costs[0],
                dp[Math.max(0, day - 7)] + costs[1],
                dp[Math.max(0, day - 30)] + costs[2],
            )
        }
    }

    return dp[lastDay]
};

/* 518. Coin Change II */

var change = function (amount, coins) {
    const dp = Array(amount + 1).fill(0)
    dp[0] = 1

    for (const coin of coins) {
        for (let j = coin; j <= amount; j++) {
            dp[j] += dp[j - coin]
        }
    }

    return dp.at(-1)
};