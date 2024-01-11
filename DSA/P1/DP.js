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

    const dp = Array(n + 1).fill(Infinity);

    dp[0] = 0;

    for (let num = 1; num <= n; num++) {
        for (let k = 1; k ** 2 <= num; k++) {
            dp[num] = Math.min(dp[num], dp[num - (k ** 2)] + 1);
        }
    }

    return dp[n];
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
        DP array's [i, j] contains number of ways to come to this coordinate
            base cases - first row and first col
        go through all the cells
            add ways to come from top and left
    */

    const dp = Array.from({ length: m }, () => new Array(n).fill(0))

    //  only one way to cover all rows in first column
    for (let row = 0; row < m; row++) {
        dp[row][0] = 1
    }
    //  only one way to cover all columns in first row
    for (let col = 0; col < n; col++) {
        dp[0][col] = 1
    }

    //  fill the remaining cells
    for (let row = 1; row < m; row++) {
        for (let col = 1; col < n; col++) {
            //  can come from top or left
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
    */

    if (n == 1) {
        return 1
    }
    const dp = Array(n + 1)

    //  one way to climb
    dp[1] = 1
    //  two ways to climb
    dp[2] = 2

    for (let i = 3; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2]
    }

    return dp[n]
};

/* Longest Increasing Subsequence - Given an integer array nums, return the length of the longest strictly increasing subsequence
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const lengthOfLIS = (nums) => {

    /* pseudo code
        keep a dp array where ith item tells length of LIS till ith index
        move end index from 1 to nums length
            move i index from 0 to end subarray
                keep checking if we find a longer subseq
    */

    // Initialize an array dp to store the length of LIS ending at each index
    const dp = Array(nums.length).fill(1);

    // Loop through each element in the array starting from index 1
    for (let end = 1; end < nums.length; end++) {
        // Nested loop to compare the current element with previous elements
        for (let i = 0; i < end; i++) {
            // Check if the current element is greater than the previous element
            if (nums[end] > nums[i]) {
                //  Update the length of LIS ending at the current index
                //  either choose the array till j with current element or array till i
                dp[end] = Math.max(dp[end], dp[i] + 1);
            }
        }
    }

    // Return the overall maximum length of LIS
    return Math.max(...dp);
};

/* Count strictly increasing subarrays
 */
const countIncreasingSubarraysDP = (arr) => {

    /* pseudo code
        keep a dp array
            ith item tells num of increasing subarrays till ith index
        move through the array
            if curr item is more than last one
                dp[i - 1] + 1
            keep adding the count of subarrays
    */

    const n = arr.length;
    const dp = Array(n).fill(1); // Initialize dp array with all elements set to 1
    let count = 0;

    for (let i = 1; i < n; i++) {
        if (arr[i] > arr[i - 1]) {
            // If the current element is greater than the previous one,
            // update dp[i] with dp[i-1] + 1
            dp[i] = dp[i - 1] + 1;
        } else {
            // If the current element is not greater than the previous one,
            // reset dp[i] to 1
            dp[i] = 1;
        }

        // Add the length of the increasing subarray ending at index i to the count
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
                handle first row and col
                keep tracking the count
                handle normal cell
                    take min of 3 on top and left
    */

    let count = 0

    //  dp array same as matrix
    const dp = new Array(matrix.length).fill().map(() => new Array(matrix[0].length).fill(0))

    //  visit each cell of the matrix
    for (let row = 0; row < matrix.length; row++) {
        for (let col = 0; col < matrix[0].length; col++) {
            if (matrix[row][col] === 0) {
                //  ignore 0s
            } else if (row === 0 || col === 0) {
                //  only one sub matrix possible for each [i, j]
                dp[row][col] = 1
                count += dp[row][col]
            } else {
                //  find the cell which has min submatrices in adjacent cells
                const side = Math.min(dp[row - 1][col], dp[row - 1][col - 1], dp[row][col - 1])

                //  include this cell in the count
                dp[row][col] = side + 1

                //  count all submatrices
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

    // base case - first row of the dp matrix - costs of painting the first house
    dp[0] = [...costs[0]];

    // Iterate through the houses starting from the second one
    for (let house = 1; house < n; house++) {
        //  painting ith house with the first color
        //  prev house can't be of same color
        dp[house][0] = costs[house][0] + Math.min(dp[house - 1][1], dp[house - 1][2]);

        dp[house][1] = costs[house][1] + Math.min(dp[house - 1][0], dp[house - 1][2]);

        dp[house][2] = costs[house][2] + Math.min(dp[house - 1][0], dp[house - 1][1]);
    }

    // The result is the minimum cost among the last row of the dp matrix
    return Math.min(...dp[n - 1]);
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

    // Length of the input string
    const n = s.length;
    // dp[i] is true if the substring s[0...i-1] can be segmented into words from the dictionary
    const dp = new Array(n + 1).fill(false);
    // An empty string is considered segmented by default
    dp[0] = true;

    // Iterate through the characters of the string
    for (let i = 1; i <= n; i++) {
        // Iterate through words in the dictionary
        for (const word of wordDict) {
            // Check if the current substring can be formed by appending the current word
            if (i - word.length >= 0 && dp[i - word.length]
                && s.substring(i - word.length, i) === word) {
                dp[i] = true;
                // Break out of the inner loop since we found a valid word for substring till ith index
                break;
            }
        }
    }

    // The final value of dp[n] indicates whether the entire string can be segmented
    return dp[n];
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

    // Create a 2D array to store the minimum edit distances
    const dp = Array(word1.length + 1).fill().map(() => Array(word2.length + 1));

    // Initialize the first row and column of the array
    for (let r = 0; r <= word1.length; r++) {
        for (let c = 0; c <= word2.length; c++) {
            // we need c edits to make word1 equal to word2
            if (r === 0) {
                dp[r][c] = c;
            }
            // we need r edits to make word1 equal to word2
            else if (c === 0) {
                dp[r][c] = r;
            }
            // If the letters at the current positions are the same, no additional cost
            else if (word1[r - 1] === word2[c - 1]) {
                dp[r][c] = dp[r - 1][c - 1];
            }
            // If letters are different, choose the minimum cost of insertion, deletion, or substitution
            else {
                dp[r][c] = Math.min(dp[r][c - 1], dp[r - 1][c - 1], dp[r - 1][c]) + 1;
            }
        }
    }

    // Return the minimum edit distance for the entire words
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
        keep a dp array
            ith item tells num of BSTs with i nodes
        base case is 0 node
        move i through num nodes
            move j from 1 till i
                j is the root
                left subtree is j - 1
                right is i - j
                dp[i] = product of dp[j - 1] and dp[i - j]
    */

    //  store number of unique BSTs for each number of nodes till numNodes
    const dpArr = new Array(numNodes + 1).fill(0)

    //  only 1 BST for 0 nodes
    dpArr[0] = 1

    //  find unique BSTs for number of nodes till numNodes
    for (let i = 1; i <= numNodes; i++) {
        //  for each possible root node, find unique BSTs for left and right subtrees        
        for (let j = 1; j <= i; j++) {
            //  number of unique BSTs for a given root is the product of number of unique BSTs for left and right subtrees
            //  j is the root here            
            dpArr[i] += dpArr[j - 1] * dpArr[i - j]
        }
    }

    return dpArr[numNodes]
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
            length 0 and 1
        move through the string
            find last one and two digits of this substring
            last digit not 0
                add dp[i - 1]
            last two digits are between 10 and 26
                add dp[i - 2]
    */

    // Check for edge cases: empty string or leading zero
    if (!s || s[0] === '0') {
        return 0;
    }

    // Get the length of the input string
    const n = s.length;

    // Initialize an array dp to store the number of ways to decode at each position
    const dp = Array(n + 1).fill(0);

    // There is one way to decode a string of length 0 and 1
    dp[0] = 1;
    dp[1] = 1;

    // Iterate through the string starting from the third character
    for (let i = 2; i <= n; ++i) {
        // Extract one and two-digit numbers from the string
        const lastDigit = parseInt(s[i - 1]);
        const lastTwoDigits = parseInt(s.substring(i - 2, i));

        // If the one-digit number is not zero, add the number of ways to decode at the previous position
        if (lastDigit !== 0) {
            dp[i] += dp[i - 1];
        }

        // If the two-digit number is between 10 and 26 (inclusive), add the number of ways to decode two positions back
        if (10 <= lastTwoDigits && lastTwoDigits <= 26) {
            dp[i] += dp[i - 2];
        }
    }

    // The final result is stored in dp[n], representing the number of ways to decode the entire string
    return dp[n];
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
            //  reached the end
            res.push([...currList])
        }

        //  explore all substrings
        for (let end = currIndex; end < s.length; end++) {
            //  found a palindrome
            if (s[currIndex] === s[end] && (end - currIndex <= 2 || dp[currIndex + 1][end - 1])) {
                dp[currIndex][end] = true
                dfs(end + 1, [...currList, s.slice(currIndex, end + 1)])
            }
        }
    }

    dfs(0, [])

    return res
};

/* Maximum Profit in Job Scheduling - We have n jobs, where every job is scheduled to be done from startTime[i] to endTime[i], obtaining a profit of profit[i].

You're given the startTime, endTime and profit arrays, return the maximum profit you can take such that there are no two jobs in the subset with overlapping time range.

If you choose a job that ends at time X you will be able to start another job that starts at time X.
 */
/**
 * @param {number[]} startTime
 * @param {number[]} endTime
 * @param {number[]} profit
 * @return {number}
 */
const jobScheduling = (startTime, endTime, profit) => {

    /* pseudo code
        keep a dp array
            ith item tells max profit till ith job
        keep a jobs array with start, end and profit
            sort by start time
        base case
            start from the last job and its profit
        move through jobs in reverse
            find a next job which starts when ith job ends
            either keep the curr job's profit and profit till next compatible job
            or carry the profit accrued so far
    */

    const numJobs = jobs.length
    const dpArr = Array(numJobs)

    // Create an array to store the jobs as [start time, end time, profit]
    const jobs = []
    for (let i = 0; i < startTime.length; i++) {
        jobs.push([startTime[i], endTime[i], profit[i]])
    }

    // Sort the jobs array based on start times in ascending order
    jobs.sort((a, b) => a[0] - b[0])

    // Base case: Set the profit for the last job in the profitArr array
    dpArr[numJobs - 1] = jobs.at(-1)[2]

    // Iterate through the jobs array in reverse order to fill the profitArr array
    for (let i = numJobs - 2; i >= 0; i--) {
        const [start, end, profit] = jobs[i]
        let next = i + 1

        // Find the next job whose start time is greater than or equal to the current job's end time
        while (next < numJobs && jobs[next][0] < end) {
            next++
        }

        // Calculate the maximum profit for the current job by considering two cases:
        // 1. Include the current job and add its profit to the profit of the next compatible job
        // 2. Exclude the current job and consider the profit of the next job
        dpArr[i] = Math.max(profit + (next < numJobs ? dpArr[next] : 0),
            dpArr[i + 1])
    }

    // Return the maximum profit for scheduling jobs
    return dpArr[0]
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

    // Memoization object to store computed results and avoid redundant calculations
    const memo = {}

    // Backtracking function to explore all possible combinations of vowels
    const backTrack = (index, strLength) => {
        // Generate a unique key for the current state using index and string length
        const key = index + '_' + strLength

        // Check if the result for the current state is already memoized
        if (memo[key]) {
            return memo[key]
        }

        // Base case: if the string has reached the desired length, it is a valid vowel string
        if (strLength === n) {
            return 1
        }

        // If the string length exceeds n, it is not a valid string
        if (strLength > n) {
            return 0
        }

        // Initialize count for the current state
        let count = 0

        // Explore all possible vowel combinations starting from the current index
        for (let i = index; i < 5; i++) {
            // Recursively call the backTrack function for the next position
            count += backTrack(i, strLength + 1)
        }

        // Memoize the count for the current state
        memo[key] = count

        // Return the count for the current state
        return count
    }

    // Start exploring all possible combinations of vowels from the beginning (index 0) with an empty string
    return backTrack(0, 0)
};

/* Maximal Square - Given an m x n binary matrix filled with 0's and 1's, find the largest square containing only 1's and return its area.
 */

/**
 * @param {character[][]} matrix
 * @return {number}
 */
const maximalSquare = (matrix) => {

    // Extend each row by appending '0' to the end
    for (let r = 0; r < matrix.length; r++) {
        matrix[r].push('0');
    }

    // Add a new row filled with '0' to the bottom of the matrix
    matrix.push(Array(matrix[0].length).fill('0'));

    // Variable to store the maximum square size
    let maxSquare = 0;

    // Loop through the matrix in reverse order
    for (let r = matrix.length - 2; r >= 0; r--) {
        for (let c = matrix[0].length - 2; c >= 0; c--) {
            // If the current cell contains '1', update it with the size of the maximal square
            if (matrix[r][c] === '1') {
                // Compute the size of the maximal square at the current cell
                matrix[r][c] = Math.min(
                    parseInt(matrix[r + 1][c]),
                    parseInt(matrix[r + 1][c + 1]),
                    parseInt(matrix[r][c + 1])
                ) + 1;

                // Update the maximum square size if needed
                maxSquare = Math.max(maxSquare, matrix[r][c] ** 2);
            }
        }
    }

    // Return the maximum square size found in the matrix
    return maxSquare;
};

/* All possible full binary trees - Given an integer n, return a list of all possible full binary trees with n nodes. Each node of each tree in the answer must have Node.val == 0.
 */
const allPossibleFBT = (n) => {
    // we will use DP to keep precomputed trees
    const memo = {}

    const createFBT = (size) => {
        //  FBT of one node can be created
        if (size === 1) {
            return [new TreeNode()]
        }
        //  can not form FBT with even number of nodes
        if (size % 2 == 0) {
            return []
        }
        //  we have already computed the result
        if (memo[size]) {
            return memo[size]
        }

        const trees = []

        // iterate over all possible odd numbers
        for (let left = 1; left < size; left += 2) {
            //  get all possible left subtress
            const leftTrees = createFBT(left)
            //  get all possible right subtress
            const rightTress = createFBT(size - left - 1)
            if (leftTrees && rightTress) {
                //  create combination of trees
                for (const l of leftTrees) {
                    for (r of rightTress) {
                        const root = new TreeNode(0, l, r)
                        //  track the root of all trees
                        trees.push(root)
                    }
                }
            }
        }

        //  keep for future use
        memo[size] = trees
        return trees
    }

    return createFBT(n)

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
    // Check if the total length of s1 and s2 is equal to s3
    if (s1.length + s2.length !== s3.length) {
        return false;
    }

    // Initialize a 2D array to store intermediate results
    const dp = [];

    // Iterate over the lengths of s1 and s2
    for (let i = 0; i <= s1.length; i++) {
        dp[i] = [];
        for (let j = 0; j <= s2.length; j++) {
            // Base case: an empty s1 and s2 can form an empty s3
            if (i === 0 && j === 0) {
                dp[i][j] = true;
            } else if (i === 0) {
                // If s1 is empty, check if s2 and s3 match
                dp[i][j] = dp[i][j - 1] && s2[j - 1] === s3[j - 1];
            } else if (j === 0) {
                // If s2 is empty, check if s1 and s3 match
                dp[i][j] = dp[i - 1][j] && s1[i - 1] === s3[i - 1];
            } else {
                // General case: Check if s1 or s2 contributed to the current character of s3
                dp[i][j] =
                    (dp[i][j - 1] && s2[j - 1] === s3[i + j - 1]) ||
                    (dp[i - 1][j] && s1[i - 1] === s3[i + j - 1]);
            }
        }
    }

    // The result is stored in the bottom-right corner of the dp array
    return dp[s1.length][s2.length];
};

/* Minimum Path Sum - Given a m x n grid filled with non-negative numbers, find a path from top left to bottom right, which minimizes the sum of all numbers along its path.

Note: You can only move either down or right at any point in time.
*/
/**
 * @param {number[][]} grid
 * @return {number}
 */
const minPathSum = (grid) => {
    // Iterate through each row of the grid
    for (let r = 0; r < grid.length; r++) {
        // Iterate through each column of the grid
        for (let c = 0; c < grid[0].length; c++) {
            // Skip the top-left corner (starting point) as it has no incoming paths
            if (r === 0 && c === 0) {
                continue;
            }

            // Calculate the minimum sum by considering the top and left neighboring values
            // If there's a value to the left of the current cell, get its value; otherwise, use Infinity
            const left = c > 0 ? grid[r][c - 1] : Infinity;

            // If there's a value above the current cell, get its value; otherwise, use Infinity
            const top = r > 0 ? grid[r - 1][c] : Infinity;

            // Update the current cell with the minimum sum of the top and left values plus its own value
            grid[r][c] = Math.min(left, top) + grid[r][c];
        }
    }

    // Return the minimum path sum found in the bottom-right corner of the grid
    return grid[grid.length - 1][grid[0].length - 1];
};

/* Partition Equal Subset Sum - Given an integer array nums, return true if you can partition the array into two subsets such that the sum of the elements in both subsets is equal or false otherwise.
 */

/**
 * @param {number[]} nums
 * @return {boolean}
 */
// Define a function to check if it's possible to partition an array into two subsets with equal sums
const canPartition = (nums) => {
    let sum = nums.reduce((prevVal, currValue) => prevVal + currValue, 0); // Calculate the sum of all values in the array

    // If the sum is odd, it cannot be partitioned into two subsets with equal sums
    if (sum % 2 !== 0) {
        return false;
    }

    let target = sum / 2; // Calculate the target sum for each subset
    let dp = new Set(); // Use a set to keep track of unique sums
    dp.add(0); // Initialize with 0 as an empty subset has a sum of 0

    // Iterate through the array in reverse order
    for (let i = nums.length - 1; i >= 0; i--) {
        let nextDp = new Set(); // Create a new set for the next iteration

        // Iterate through the values in the current set
        for (const ele of dp.values()) {
            let newVal = ele + nums[i]; // Calculate the new sum by adding the current element
            if (newVal === target) {
                return true;
            } // If the new sum is equal to the target, return true

            nextDp.add(newVal); // Add the new sum to the next set
        }

        dp = new Set([...dp, ...nextDp]); // Combine the current set and the next set for the next iteration
    }

    return false; // If no subset with the target sum is found, return false
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

