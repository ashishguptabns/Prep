/* Koko Eating Bananas - Koko loves to eat bananas. There are n piles of bananas, the ith pile has piles[i] bananas. The guards have gone and will come back in h hours.

Koko can decide her bananas-per-hour eating speed of k. Each hour, she chooses some pile of bananas and eats k bananas from that pile. If the pile has less than k bananas, she eats all of them instead and will not eat any more bananas during this hour.

Koko likes to eat slowly but still wants to finish eating all the bananas before the guards return.

Return the minimum integer k such that she can eat all the bananas within h hours.
 */
/**
 * @param {number[]} piles
 * @param {number} h
 * @return {number}
 */
const minEatingSpeed = (piles, h) => {

    /* pseudo code
        find min and max bananas that can be eaten in an hour
        loop till min < max
            find the mid
            find num hours to eat all the piles
            more than h
                discard left
            else
                discard right
    */

    let minSpeed = 1
    let maxSpeed = 1

    for (const num of piles) {
        maxSpeed = Math.max(num, maxSpeed)
    }

    while (minSpeed < maxSpeed) {
        const mid = Math.floor((minSpeed + maxSpeed) / 2)
        let hoursNeeded = 0
        for (const num of piles) {
            hoursNeeded += Math.ceil(num / mid)
        }

        if (hoursNeeded > h) {
            minSpeed = mid + 1
        } else {
            maxSpeed = mid
        }
    }

    return minSpeed
};

/* Smallest common number sorted rows - Given a matrix mat where every row is sorted in increasing order, return the smallest common element in all rows.
 */
const smallestCommonElement = (mat) => {

    /* pseudo code
        keep left at smallest and right at highest item
        loop till left <= right
            find the mid
            check if it is present in all rows
                discard the right
            else
                discard the left
    */

    const rows = mat.length;
    const cols = mat[0].length;

    const isPresentInAllRows = (value) => {
        for (let i = 0; i < rows; i++) {
            if (binarySearch(mat[i], value) === -1) {
                return false;
            }
        }
        return true;
    }

    const binarySearch = (arr, target) => {
        let low = 0;
        let high = arr.length - 1;

        while (low <= high) {
            const mid = Math.floor((low + high) / 2);

            if (arr[mid] === target) {
                return mid;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }

        return -1;
    }

    let left = mat[0][0];
    let right = mat[0][cols - 1];

    while (left <= right) {
        const mid = Math.floor((left + right) / 2);

        if (isPresentInAllRows(mid)) {
            right = mid - 1;
        } else {
            left = mid + 1;
        }
    }

    return left;
}

/* Median of two sorted arrays - Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.

The overall run time complexity should be O(log (m+n)).
 */
/**
 * @param {number[]} nums1
 * @param {number[]} nums2
 * @return {number}
 */
const findMedianSortedArrays = (nums1, nums2) => {

    /* pseudo code
        keep an array to merge both arrays
        move i through nums1 and j through nums2
            nums1[i] < nums2[j]
                push ith item of nums1
            else
                push jth item of nums2
        push remaining items of nums1 and nums2
        find the median of merged array
    */

    const merged = [];
    let i = 0, j = 0;

    while (i < nums1.length && j < nums2.length) {
        if (nums1[i] < nums2[j]) {
            merged.push(nums1[i++]);
        } else {
            merged.push(nums2[j++]);
        }
    }

    while (i < nums1.length) {
        merged.push(nums1[i++]);
    }
    while (j < nums2.length) {
        merged.push(nums2[j++]);
    }

    let mid = Math.floor(merged.length / 2);
    if (merged.length % 2 === 0) {
        return (merged[mid - 1] + merged[mid]) / 2;
    } else {
        return merged[mid];
    }
};

/* Minimum Time to Complete Trips - You are given an array time where time[i] denotes the time taken by the ith bus to complete one trip.

Each bus can make multiple trips successively; that is, the next trip can start immediately after completing the current trip. Also, each bus operates independently; that is, the trips of one bus do not influence the trips of any other bus.

You are also given an integer totalTrips, which denotes the number of trips all buses should make in total. Return the minimum time required for all buses to complete at least totalTrips trips.
 */
/**
 * @param {number[]} busTimes
 * @param {number} totalTrips
 * @return {number}
 */
const minimumTime = (busTimes, totalTrips) => {

    /* pseudo code
        find min time
        keep left at 1 and right at min * totalTrips
        loop until left < right
            find the mid
            find if all trips can be completed in this time
                move busTime through busTimes
                    track sum of all trips
                    true if more than totalTrips needed to complete
            yes
                discard right half
            else
                discard left half
    */

    let min = Infinity

    for (const t of busTimes) {
        min = Math.min(min, t)
    }

    let left = 1
    let right = min * totalTrips

    const canComplete = (possibleTime) => {
        let numTrips = 0

        for (const busTime of busTimes) {
            numTrips += Math.floor(possibleTime / busTime)
            if (numTrips >= totalTrips) {
                return true
            }
        }

        return false
    }

    while (left < right) {
        let mid = Math.floor((left + right) / 2)

        const isPossible = canComplete(mid)

        if (isPossible) {
            right = mid
        } else {
            left = mid + 1
        }
    }

    return left
};

/* Kth Smallest Subarray Sum - Given an integer array nums of length n and an integer k, return the kth smallest subarray sum.
 */
const kthSmallestSubarraySum = (nums, k) => {

    /* pseudo code
        smallest could be the min or max of sums and anything in between
        keep left at min and right at max
        loop till left < right
            find the mid
            count subarrays with max sum capped at mid
                move right through nums
                    track sum
                    sum > maxSum
                        shrink window from left
                    find subarrays ending at right and add to count
            count > k
                discard right half
            else
                discard left half
    */

    const countSubarrays = (maxSum) => {
        let count = 0;
        let sum = 0;
        let left = 0;

        for (let right = 0; right < nums.length; right++) {
            sum += nums[right];

            while (sum > maxSum) {
                sum -= nums[left];
                left++;
            }

            count += right - left + 1;
        }

        return count;
    }

    let left = Math.min(...nums);
    let right = Math.max(...nums);

    while (left < right) {
        const mid = Math.floor((left + right) / 2);
        const count = countSubarrays(mid);

        if (count < k) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }

    return left;
}

/* Find First and Last Position of Element in Sorted Array - Given an array of integers nums sorted in non-decreasing order, find the starting and ending position of a given target value.

If target is not found in the array, return [-1, -1].

You must write an algorithm with O(log n) runtime complexity.
*/

/**
 * @param {number[]} nums
 * @param {number} target
 * @return {number[]}
 */
const searchRange = (nums, target) => {
    let start = -1
    let end = -1

    let left = 0
    let right = nums.length - 1

    while (left <= right) {
        let mid = Math.floor((left + right) / 2)
        if (nums[mid] === target) {
            start = mid
            right = mid - 1
        } else if (nums[mid] < target) {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }

    left = 0
    right = nums.length - 1

    while (left <= right) {
        let mid = Math.floor((left + right) / 2)
        if (nums[mid] === target) {
            end = mid
            left = mid + 1
        } else if (nums[mid] < target) {
            left = mid + 1
        } else {
            right = mid - 1
        }
    }
    return [start, end]
};

/* Single Element in a Sorted Array - You are given a sorted array consisting of only integers where every element appears exactly twice, except for one element which appears exactly once. Return the single element that appears only once. Your solution must run in O(log n) time and O(1) space.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const singleNonDuplicate = (nums) => {
    /* pseudo code
        insight is that ideally all the even indexes starting from 0 should be equal to the following odd indexed item
        when we cross an item which doesn't have a partner, all the items at odd indexes are equal to their predecessors at just before even indexes         
    */

    let left = 0
    let right = nums.length - 1

    while (left < right) {
        let mid = Math.floor((left + right) / 2)
        if (mid % 2 === 1) {
            mid -= 1
        }

        if (nums[mid] === nums[mid + 1]) {
            left = mid + 2
        } else {
            right = mid
        }
    }

    return nums[left]
};

/* Capacity To Ship Packages Within D Days - A conveyor belt has packages that must be shipped from one port to another within given days.

The ith package on the conveyor belt has a weight of weights[i]. Each day, we load the ship with packages on the conveyor belt (in the order given by weights). We may not load more weight than the maximum weight capacity of the ship.

Return the least weight capacity of the ship that will result in all the packages on the conveyor belt being shipped within given days.
 */
/**
 * @param {number[]} weights
 * @param {number} days
 * @return {number}
 */
const shipWithinDays = (weights, days) => {
    /* pseudo code
        move through weights
            find min weight as min capacity
            sum of all weights as max capacity
        run a loop till min capacity is less than max capacity
            find the mid capacity
            check if all weights can be carried in given days
                move through weights
                    keep adding weights in one day till capacity is reached
                    new day if capacity has been reached
            discard right or left for further search
    */

    let minCapacity = 0
    let maxCapacity = 0

    for (const weight of weights) {
        minCapacity = Math.max(weight, minCapacity)
        maxCapacity += weight
    }

    const isFeasible = (weights, capacity, days) => {
        let currentLoad = 0
        let daysNeeded = 1

        for (const weight of weights) {
            currentLoad += weight
            if (currentLoad > capacity) {
                daysNeeded++
                currentLoad = weight
            }
        }

        return daysNeeded <= days
    }

    while (minCapacity < maxCapacity) {
        const mid = Math.floor((minCapacity + maxCapacity) / 2)
        if (isFeasible(weights, mid, days)) {
            maxCapacity = mid
        } else {
            minCapacity = mid + 1
        }
    }

    return minCapacity
};

/* Peak Index in a Mountain Array - Given a mountain array arr, return the index i such that arr[0] < arr[1] < ... < arr[i - 1] < arr[i] > arr[i + 1] > ... > arr[arr.length - 1].

You must solve it in O(log(arr.length)) time complexity.
 */
/**
 * @param {number[]} arr
 * @return {number}
 */
const peakIndexInMountainArray = (arr) => {
    /* pseudo code
        keep a left and right
            check if mid item is less than next item
                peak is on right side
            else
                peak is on left side
    */

    let left = 0
    let right = arr.length

    while (left < right) {
        const mid = Math.floor((left + right) / 2)
        if (arr[mid + 1] > arr[mid]) {
            left = mid + 1
        } else {
            right = mid
        }
    }

    return left
};

/* Find the Duplicate Number - Given an array of integers nums containing n + 1 integers where each integer is in the range [1, n] inclusive.

There is only one repeated number in nums, return this repeated number.
 */
/**
 * @param {number[]} nums
 * @return {number}
 */
const findDuplicate = (nums) => {
    /* pseudo code
        keep left and right
            find the mid item
            move through nums
                count nums which are smaller than mid item
            if count is more than mid item
                duplicate is on the left side
            else
                on the right side
    */

    let left = 0
    let right = nums.length - 1
    let duplicate = -1
    while (left <= right) {
        const midNum = Math.floor((left + right) / 2)
        let smallNumsCount = 0
        for (const num of nums) {
            if (num <= midNum) {
                smallNumsCount++
            }
        }

        if (smallNumsCount > midNum) {
            right = midNum - 1
            duplicate = midNum
        } else {
            left = midNum + 1
        }
    }
    return duplicate
};

/* Find min in rotated sorted array - Suppose an array of length n sorted in ascending order is rotated between 1 and n times. For example, the array nums = [0,1,2,4,5,6,7] might become:

[4,5,6,7,0,1,2] if it was rotated 4 times.
[0,1,2,4,5,6,7] if it was rotated 7 times.
Notice that rotating an array [a[0], a[1], a[2], ..., a[n-1]] 1 time results in the array [a[n-1], a[0], a[1], a[2], ..., a[n-2]].

Given the sorted rotated array nums of unique elements, return the minimum element of this array.

You must write an algorithm that runs in O(log n) time.
 */

/**
 * @param {number[]} nums
 * @return {number}
 */
const findMin = (nums) => {

    /* pseudo code
        keep left at start and right pointer at end
        loop till left < right
            find the mid
            mid > right
                discard left half
            else
                discard right half
    */

    let left = 0;
    let right = nums.length - 1;

    while (left < right) {
        const mid = Math.floor((left + right) / 2);

        if (nums[mid] > nums[right]) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }

    return nums[left];
};

/* Find K Closest Elements - Given a sorted integer array arr, two integers k and x, return the k closest integers to x in the array. The result should also be sorted in ascending order.
 */
/**
 * @param {number[]} arr
 * @param {number} k
 * @param {number} x
 * @return {number[]}
 */
const findClosestElements = (arr, k, x) => {

    /* pseudo code
        loop till left < right
            find the mid
            if mid item is farther than [mid + k] item
                discard the left half
            else
                discard the right half
    */

    let left = 0;
    let right = arr.length - k;

    while (left < right) {
        const mid = Math.floor((right + left) / 2);

        if (x - arr[mid] > arr[mid + k] - x) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }

    const resultList = [];

    for (let counter = left; counter < left + k; counter++) {
        resultList.push(arr[counter]);
    }

    return resultList;
};

/* Search in rotated array - Given the array nums after the possible rotation and an integer target, return the index of target if it is in nums, or -1 if it is not in nums.

You must write an algorithm with O(log n) runtime complexity.
 */
/**
 * @param {number[]} nums
 * @param {number} target
 * @return {number}
 */
const search = (nums, target) => {

    /* pseudo code
        keep low and high
            find the mid item
            check if left num is less than mid
                left half is sorted
                    check if mid item is within this array
                    else discard the half array
                else right is sorted
                    check if mid item is within this array
                    else discard the half array
    */

    let low = 0
    let high = nums.length - 1

    while (low <= high) {
        let mid = Math.floor((low + high) / 2)
        if (nums[mid] === target) {
            return mid
        }

        if (nums[low] <= nums[mid]) {
            if (nums[low] <= target && target <= nums[mid]) {
                high = mid - 1
            } else {
                low = mid + 1
            }
        } else {
            if (nums[mid] <= target && target <= nums[high]) {
                low = mid + 1
            } else {
                high = mid - 1
            }
        }
    }

    return -1
};

/* Cutting Ribbons 
- You're given an array ribbons where ribbons[i] represents the length of the i-th ribbon.
- You're also given an integer k.
- You can cut ribbons into smaller segments of positive integer lengths, or leave them intact.
- Your goal is to obtain k ribbons of all the same positive integer length.
- You can discard excess ribbon material.
Return the maximum possible positive integer length of those k ribbons, or 0 if it's not achievable.
 */
/**
* @param {number[]} ribbons - Array of ribbon lengths
* @param {number} k - Target number of segments
* @return {number} - Maximum valid ribbon length
*/
const maxLength = (ribbons, k) => {

    /* pseudo code
        keep left at 1 and max at max of ribbons
            find the mid length
            if k ribbons can be cut with this length
                discard left half
            else
                discard right half
    */

    const canCut = (length) => {
        let count = 0;
        for (const ribbon of ribbons) {
            count += Math.floor(ribbon / length);
        }
        return count >= k;
    };

    let left = 1;
    let right = Math.max(...ribbons);

    while (left <= right) {
        const midLen = Math.floor((left + right) / 2);

        if (canCut(midLen)) {
            left = midLen + 1;
        } else {
            right = midLen - 1;
        }
    }

    return right;
};

/* Find Minimum in Rotated Sorted Array II - Suppose an array of length n sorted in ascending order is rotated between 1 and n times. For example, the array nums = [0,1,4,4,5,6,7] might become:

[4,5,6,7,0,1,4] if it was rotated 4 times.
[0,1,4,4,5,6,7] if it was rotated 7 times.
Notice that rotating an array [a[0], a[1], a[2], ..., a[n-1]] 1 time results in the array [a[n-1], a[0], a[1], a[2], ..., a[n-2]].

Given the sorted rotated array nums that may contain duplicates, return the minimum element of this array.

You must decrease the overall operation steps as much as possible. */

/* Count of Smaller Numbers After Self - Given an integer array nums, return an integer array counts where counts[i] is the number of smaller elements to the right of nums[i].
 */

/* Kth Smallest Element in a Sorted Matrix - Given an n x n matrix where each of the rows and columns is sorted in ascending order, return the kth smallest element in the matrix.

Note that it is the kth smallest element in the sorted order, not the kth distinct element.

You must find a solution with a memory complexity better than O(n**2).
 */
/**
 * @param {number[][]} matrix
 * @param {number} k
 * @return {number}
 */
const kthSmallest = (matrix, k) => {
    /* pseudo code
        start left at top left and right at right bottom
        loop till left < right
            find the mid
            move r through rows
                move c left through cols till curr cell > mid
                track smaller num items in this row
            num items > k
                discard right half
            else
                discard left half
    */

    let left = matrix[0][0]
    let right = matrix[matrix.length - 1][matrix.length - 1]

    while (left < right) {
        const mid = Math.floor((left + right) / 2)
        let numElements = 0
        let c = matrix.length - 1

        for (let r = 0; r < matrix.length; r++) {
            while (c >= 0 && matrix[r][c] > mid) {
                c--
            }
            numElements += c + 1
        }

        if (numElements < k) {
            left = mid + 1
        } else {
            right = mid
        }
    }

    return left
};

/* Find Minimum in Rotated Sorted Array II - Suppose an array of length n sorted in ascending order is rotated between 1 and n times. For example, the array nums = [0,1,4,4,5,6,7] might become:

[4,5,6,7,0,1,4] if it was rotated 4 times.
[0,1,4,4,5,6,7] if it was rotated 7 times.
Notice that rotating an array [a[0], a[1], a[2], ..., a[n-1]] 1 time results in the array [a[n-1], a[0], a[1], a[2], ..., a[n-2]].

Given the sorted rotated array nums that may contain duplicates, return the minimum element of this array.

You must decrease the overall operation steps as much as possible. */

/**
 * @param {number[]} nums
 * @return {number}
 */
var findMin2 = function (nums) {
    let left = 0;
    let right = nums.length - 1;

    while (left < right) {
        const mid = Math.floor((left + right) / 2);

        if (nums[mid] > nums[right]) {
            left = mid + 1;
        } else if (nums[mid] < nums[right]) {
            right = mid;
        } else {
            right--
        }
    }

    return nums[left];
};

/* Split Array Largest Sum - Given an integer array nums and an integer k, split nums into k non-empty subarrays such that the largest sum of any subarray is minimized.

Return the minimized largest sum of the split.

A subarray is a contiguous part of the array. */

