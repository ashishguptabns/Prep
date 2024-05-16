/* Generate Parentheses - Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses. */

const generateParenthesis = (n) => {

    /* pseudo code 
        backtrack with num left brackets, right brackets and formed string so far
            if left brackets are less than n
                keep backtracking with more left braces
            if right braces are less than left ones
                keep backtracking with more right braces 
    */

    const res = []

    //  l: left taken, r: right taken
    const backTrack = (l, r, currStr) => {
        if (currStr.length === 2 * n) {
            res.push(currStr)
            return
        }

        //  we still have left brackets to place
        if (l < n) {
            backTrack(l + 1, r, currStr + '(')
        }
        //  close the left bracket
        if (r < l) {
            backTrack(l, r + 1, currStr + ')')
        }
    }

    backTrack(0, 0, '')

    return res
};

/* Non-decreasing Subsequences - Given an integer array nums, return all the different possible non-decreasing subsequences of the given array with at least two elements. You may return the answer in any order.
 */
/**
 * @param {number[]} nums
 * @return {number[][]}
 */
const findSubsequences = (nums) => {

    /* pseudo code
        back track with curr index in nums array
            keep pushing to res array as per condition
            move through nums array
                back track with a valid item
    */

    const res = []

    const map = {}
    const backtrack = (index, currSeq) => {
        if (map[currSeq]) {
            return;
        }
        map[currSeq] = true;

        if (currSeq.length >= 2) {
            res.push(currSeq)
        }
        for (let i = index; i < nums.length; i++) {
            if (currSeq.at(-1) > nums[i]) {
                continue;
            }
            backtrack(i + 1, [...currSeq, nums[i]])
        }
    }
    backtrack(0, [])
    return res;
};

/* Permutations - Given an array nums of distinct integers, return all the possible permutations. You can return the answer in any order.
 */
const permute = (nums) => {

    /* pseudo code
        backtrack with curr arr and nums arr
            run through whole nums array
                backtrack with (curr arr + curr item) and (nums except curr item)
    */

    const res = []

    const backTrack = (currArr, nums) => {
        if (nums.length === 0) {
            res.push(currArr)
            return
        }
        for (let i = 0; i < nums.length; i++) {
            //  add ith item to currArr and remove from nums for further iteration
            backTrack([...currArr, nums[i]], [...nums.slice(0, i), ...nums.slice(i + 1)], res)
        }
    }

    backTrack([], nums)
    return res
};

/* Combination Sum - Given an array of distinct integers candidates and a target integer target, return a list of all unique combinations of candidates where the chosen numbers sum to target. You may return the combinations in any order.

The same number may be chosen from candidates an unlimited number of times. Two combinations are unique if the 
frequency of at least one of the chosen numbers is different.
 */
/**
 * @param {number[]} candidates
 * @param {number} target
 * @return {number[][]}
 */
const combinationSum = (candidates, target) => {

    /* pseudo code
        backtrack with curr index, curr arr and left target    
            run through the candidates from curr index
                backtrack with target left 
    */

    const res = []

    const backtrack = (arr, index, left) => {
        if (left === 0) {
            res.push(arr)
        } else if (left > 0) {
            for (let i = index; i < candidates.length; i++) {
                const num = candidates[i]
                backtrack([...arr, num], i, left - num)
            }
        }
    }
    backtrack([], 0, target)

    return res
};

/* Subsets - Given an integer array nums of unique elements, return all possible 
subsets (the power set).

The solution set must not contain duplicate subsets. Return the solution in any order
 */
const subsets = (nums) => {

    /* pseudo code
        backtrack with curr index and curr subset
            run through nums array from curr index
                backtrack with new index and new subset with curr item
    */

    //  this will contain all the subsets    
    const result = []

    const backTrack = (startIndex, currSubset) => {
        //  we need all possible subsets
        result.push([...currSubset])

        //  find all possible subsets going forward
        for (let i = startIndex; i < nums.length; i++) {
            //  all possible subsets with the current element
            backTrack(i + 1, [nums[i], ...currSubset])
        }
    }

    backTrack(0, [])

    return result
};

/* Letter tile possibilities - You have n tiles, where each tile has one letter tiles[i] printed on it.

Return the number of possible non-empty sequences of letters you can make using the letters printed on those tiles.
 */
const numTilePossibilities = (tiles) => {

    /* pseudo code
        backtrack with curr seq and left tiles array
            move i through given tiles array
                backtrack with new seq and tiles array except ith tile
    */

    const set = new Set()

    const backTrack = (currSeq, leftTiles) => {
        if (currSeq.length > 0) {
            set.add(currSeq)
        }
        for (let i = 0; i < leftTiles.length; i++) {
            backTrack(currSeq + leftTiles[i], leftTiles.slice(0, i) + leftTiles.slice(i + 1))
        }
    }
    backTrack('', tiles)
    return set.size
};

/* Combination Sum II - Given a collection of candidate numbers (candidates) and a target number (target), find all unique combinations in candidates where the candidate numbers sum to target.

Each number in candidates may only be used once in the combination.

Note: The solution set must not contain duplicate combinations.
 */
/**
 * @param {number[]} candidates
 * @param {number} target
 * @return {number[][]}
 */
const combinationSum2 = (candidates, target) => {

    /* pseudo code
        sort the candidates array
        backtrack with curr index, target left and curr seq
            loop through candidates
                backtrack
    */

    candidates.sort((a, b) => a - b)

    let res = []

    const backtrack = (left, currPath, index) => {
        if (left === 0) {
            res.push([...currPath])
            return
        }

        while (index < candidates.length && left - candidates[index] >= 0) {
            backtrack(left - candidates[index],
                [...currPath, candidates[index]], index + 1)
            index++

            while (candidates[index - 1] === candidates[index]) {
                index++
            }
        }
    }

    backtrack(target, [], 0)

    return res
};

/* 93. Restore IP Addresses - A valid IP address consists of exactly four integers separated by single dots. Each integer is between 0 and 255 (inclusive) and cannot have leading zeros.

For example, "0.1.2.201" and "192.168.1.1" are valid IP addresses, but "0.011.255.245", "192.168.1.312" and "192.168@1.1" are invalid IP addresses.
Given a string s containing only digits, return all possible valid IP addresses that can be formed by inserting dots into s. You are not allowed to reorder or remove any digits in s. You may return the valid IP addresses in any order.
 */

var restoreIpAddresses = function (s) {
    if (!s || !s.length) {
        return []
    }

    const ans = []

    const backtrack = (index, dotsLeft, str) => {
        if (dotsLeft === 0) {
            if (index === s.length) {
                ans.push(str.substring(0, str.length - 1))
            }
        } else {
            for (let i = 1; i <= 3; i++) {
                if (index + i > s.length) {
                    break
                }

                const sub = s.substring(index, index + i)
                if (sub[0] === '0' && sub.length > 1) {
                    return
                }
                if (Number(sub) <= 255) {
                    backtrack(index + i, dotsLeft - 1, str + sub + '.')
                }
            }
        }
    }
    backtrack(0, 4, '')

    return ans
};

/* Letter Combinations of a Phone Number - Given a string containing digits from 2-9 inclusive, return all possible letter combinations that the number could represent. Return the answer in any order.
 */
/**
 * @param {string} digits
 * @return {string[]}
 */
const letterCombinations = (digits) => {

    /* pseudo code
        notice that we are given digits
        backtrack with curr digit index
            run through chars of this digit
                append curr char to curr string and backtrack
    */

    const letterMap = {
        '2': 'abc',
        '3': 'def',
        '4': 'ghi',
        '5': 'jkl',
        '6': 'mno',
        '7': 'pqrs',
        '8': 'tuv',
        '9': 'wxyz'
    }
    const list = []

    const backtrack = (currDigitIndex, currString) => {
        if (currDigitIndex == digits.length) {
            //  considered all the characters
            list.push(currString)
        } else {
            const currDigit = digits[currDigitIndex]
            const currChars = letterMap[currDigit]
            for (const c of currChars) {
                //  run the backtracking for each character for each digit
                backtrack(currDigitIndex + 1, currString + c)
            }
        }
    }

    if (digits.length > 0) {
        backtrack(0, '')
    }

    return list
};

/* 784. Letter Case Permutation */

/**
 * @param {string} s
 * @return {string[]}
 */
var letterCasePermutation = function (s) {
    const set = new Set()
    const backtrack = (curr, index) => {
        if (curr.length === s.length) {
            set.add(curr)
        } else {
            for (let i = index; i < s.length; i++) {
                backtrack(curr + s[i].toLowerCase(), i + 1)
                backtrack(curr + s[i].toUpperCase(), i + 1)
            }
        }
    }
    backtrack('', 0)

    return [...set]
};

/* 77. Combinations */

var combine = function (n, k) {
    const res = []

    const backtrack = (arr, num) => {
        if (arr.length === k) {
            res.push(arr)
        } else {
            for (let i = num; i <= n; i++) {
                backtrack([...arr, i], i + 1)
            }
        }
    }
    backtrack([], 1)

    return res
};

/* 1238. Circular Permutation in Binary Representation */

/* 216. Combination Sum III - Find all valid combinations of k numbers that sum up to n such that the following conditions are true:

Only numbers 1 through 9 are used.
Each number is used at most once.
Return a list of all possible valid combinations. The list must not contain the same combination twice, and the combinations may be returned in any order.*/

var combinationSum3 = function (k, n) {
    const res = []

    const backtrack = (arr, sum, start) => {
        if (sum > n) {
            return
        }
        else if (arr.length === k) {
            sum === n && res.push(arr)
        }
        else {
            for (let i = start; i <= 9; i++) {
                backtrack([...arr, i], sum + i, i + 1)
            }
        }
    }
    backtrack([], 0, 1)
    return res
};

/* 526. Beautiful Arrangement */
var countArrangement = function (n) {
    if (n < 3) {
        return n
    }

    const seen = Array(n + 1).fill(false)
    let ans = 0
    const backtrack = (num) => {
        if (num > n) {
            ans++
        } else {
            for (let i = 1; i <= n; i++) {
                if (!seen[i] && (i % num === 0 || num % i === 0)) {
                    seen[i] = true
                    backtrack(num + 1)
                    seen[i] = false
                }
            }
        }
    }
    backtrack(1)

    return ans
};

/* 2305. Fair Distribution of Cookies */

var distributeCookies = function (cookies, k) {
    let ans = Infinity
    const backtrack = (arr, index) => {
        if (index < cookies.length) {
            for (let i = 0; i < k; i++) {
                arr[i] += cookies[index]
                backtrack(arr, index + 1)
                arr[i] -= cookies[index]
            }
        } else {
            ans = Math.min(ans, Math.max(...arr))
        }
    }
    backtrack(Array(k).fill(0), 0)

    return ans
};

/* 2178. Maximum Split of Positive Even Integers */