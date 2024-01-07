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
            //  odd index, make it even
            mid -= 1
        }

        if (nums[mid] === nums[mid + 1]) {
            //  left part is fine
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
        //  max of all weights
        minCapacity = Math.max(weight, minCapacity)

        //  total of all weights for 1 day coverage
        maxCapacity += weight
    }

    const isFeasible = (weights, capacity, days) => {
        let currentLoad = 0
        let daysNeeded = 1

        for (const weight of weights) {
            currentLoad += weight
            //  now load is more than the capacity
            if (currentLoad > capacity) {
                //  need another day for next load
                daysNeeded++
                //  prev weights were done in prev days
                currentLoad = weight
            }
        }

        return daysNeeded <= days
    }

    while (minCapacity < maxCapacity) {
        const mid = Math.floor((minCapacity + maxCapacity) / 2)
        if (isFeasible(weights, mid, days)) {
            //  solution might be on the left side
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
            //  peak is on the right
            //  notice the condition of a peak
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
                count nums which are smalled than mid item
            if count is more than mid item
                duplicate is on the left side
            else
                on the right side
    */

    let left = 0
    let right = nums.length - 1
    let duplicate = -1
    while (left <= right) {
        //  it's a numbered array
        const midNum = Math.floor((left + right) / 2)
        let smallNumsCount = 0
        for (const num of nums) {
            if (num <= midNum) {
                smallNumsCount++
            }
        }

        if (smallNumsCount > midNum) {
            //  duplicate is on the left side
            right = midNum - 1
            duplicate = midNum
        } else {
            //  duplicate is on the right side
            left = midNum + 1
        }
    }
    return duplicate
};

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

    //  search for start index
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

    //  search for end index
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
    // Initialize left and right pointers for binary search
    let left = 0;
    let right = nums.length - 1;

    // Perform binary search until the left and right pointers meet
    while (left < right) {
        // Calculate the middle index
        const mid = Math.floor((left + right) / 2);

        // Check if the middle element is greater than the rightmost element
        if (nums[mid] > nums[right]) {
            // If true, the minimum element must be in the right half
            left = mid + 1;
        } else {
            // If false, the minimum element is in the left half or is the current middle element
            right = mid;
        }
    }

    // The left pointer now points to the minimum element in the rotated sorted array
    return nums[left];
};

/* Median of two sorted arrays - Given two sorted arrays nums1 and nums2 of size m and n respectively, return the median of the two sorted arrays.

The overall run time complexity should be O(log (m+n)).
 */
/**
 * @param {number[]} nums1
 * @param {number[]} nums2
 * @return {number}
 */
var findMedianSortedArrays = function (nums1, nums2) {
    // Ensure nums1 is the smaller array
    if (nums1.length > nums2.length) {
        [nums1, nums2] = [nums2, nums1];
    }

    const m = nums1.length;
    const n = nums2.length;
    const totalLength = m + n;

    let low = 0;
    let high = m;

    while (low <= high) {
        const partitionX = Math.floor((low + high) / 2);
        const partitionY = Math.floor((totalLength + 1) / 2) - partitionX;

        const maxX = (partitionX === 0) ? Number.NEGATIVE_INFINITY : nums1[partitionX - 1];
        const minX = (partitionX === m) ? Number.POSITIVE_INFINITY : nums1[partitionX];

        const maxY = (partitionY === 0) ? Number.NEGATIVE_INFINITY : nums2[partitionY - 1];
        const minY = (partitionY === n) ? Number.POSITIVE_INFINITY : nums2[partitionY];

        if (maxX <= minY && maxY <= minX) {
            // Found the correct partition
            if (totalLength % 2 === 0) {
                // Even total length, take the average of the middle elements
                return (Math.max(maxX, maxY) + Math.min(minX, minY)) / 2;
            } else {
                // Odd total length, return the middle element
                return Math.max(maxX, maxY);
            }
        } else if (maxX > minY) {
            // Move left in nums1
            high = partitionX - 1;
        } else {
            // Move right in nums1
            low = partitionX + 1;
        }
    }

    return -1
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
    // Initialize left and right pointers for binary search
    let left = 0;
    let right = arr.length - k;

    // Perform binary search
    while (left < right) {
        // Calculate mid index
        const mid = Math.floor((right + left) / 2);

        // Check if x is closer to arr[mid + k]
        if (x - arr[mid] > arr[mid + k] - x) {
            // x is closer to arr[mid + k], search in the right subarray
            left = mid + 1;
        } else {
            // x is closer to arr[mid], search in the left subarray
            right = mid;
        }
    }

    // Create a list to store the result
    const resultList = [];

    // Add k elements starting from the left index to the result list
    for (let counter = left; counter < left + k; counter++) {
        resultList.push(arr[counter]);
    }

    // Return the result list
    return resultList;
};

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

    //  per hour speeds
    let minSpeed = 1
    let maxSpeed = 1

    for (const num of piles) {
        //  max one pile is allowed in an hour
        maxSpeed = Math.max(num, maxSpeed)
    }

    while (minSpeed < maxSpeed) {
        const mid = Math.floor((minSpeed + maxSpeed) / 2)
        let hoursNeeded = 0
        for (const num of piles) {
            //  take the max of hours
            hoursNeeded += Math.ceil(num / mid)
        }

        if (hoursNeeded > h) {
            //  increase the speed
            minSpeed = mid + 1
        } else {
            maxSpeed = mid
        }
    }

    return minSpeed
};

/* Minimum Time to Complete Trips - You are given an array time where time[i] denotes the time taken by the ith bus to complete one trip.

Each bus can make multiple trips successively; that is, the next trip can start immediately after completing the current trip. Also, each bus operates independently; that is, the trips of one bus do not influence the trips of any other bus.

You are also given an integer totalTrips, which denotes the number of trips all buses should make in total. Return the minimum time required for all buses to complete at least totalTrips trips.
 */
/**
 * @param {number[]} time
 * @param {number} totalTrips
 * @return {number}
 */
const minimumTime = (time, totalTrips) => {
    // Initialize 'min' with a very large value.
    let min = 10 ** 9

    // Find the minimum value in the 'time' array.
    for (const t of time) {
        min = Math.min(min, t)
    }

    // Initialize binary search range.
    let left = 1
    let right = min * totalTrips

    // Helper function to check if it's possible to complete 'totalTrips' within a given time.
    const canComplete = (possibleTime) => {
        let numTrips = 0

        // Count the number of trips that can be made within 'possibleTime' for each bus.
        for (const busTime of time) {
            numTrips += Math.floor(possibleTime / busTime)
            if (numTrips >= totalTrips) {
                return true
            }
        }

        return false
    }

    // Binary search loop to find the minimum possible time.
    while (left < right) {
        // Calculate the middle point of the current search range.
        let mid = Math.floor((left + right) / 2)

        // Check if it's possible to complete all trips within 'mid' time.
        const isPossible = canComplete(mid)

        // Update the search range based on the result.
        if (isPossible) {
            right = mid
        } else {
            left = mid + 1
        }
    }

    // Return the minimum time required to complete all trips.
    return left
};

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
        
    */

    let left = matrix[0][0]
    let right = matrix[matrix.length - 1][matrix.length - 1]

    while (left < right) {
        const mid = Math.floor((left + right) / 2)
        let numElements = 0
        let currCol = matrix.length - 1

        for (let currRow = 0; currRow < matrix.length; currRow++) {
            while (currCol >= 0 && matrix[currRow][currCol] > mid) {
                currCol--
            }
            //  these many elements are less than mid in this row
            numElements += currCol + 1
        }

        if (numElements < k) {
            //  we need more elements
            left = mid + 1
        } else {
            right = mid
        }
    }

    return left
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
            //  if left half is sorted
            if (nums[low] <= target && target <= nums[mid]) {
                high = mid - 1
            } else {
                // can discard the left half now cause it was sorted
                low = mid + 1
            }
        } else {
            //  right half is sorted
            if (nums[mid] <= target && target <= nums[high]) {
                low = mid + 1
            } else {
                // can discard the right half now cause it was sorted
                high = mid - 1
            }
        }
    }

    return -1
};

/* Smallest common number sorted rows - Given a matrix mat where every row is sorted in increasing order, return the smallest common element in all rows.
 */
const smallestCommonElement = (mat) => {
    const rows = mat.length;
    const cols = mat[0].length;

    // Binary search function to check if a value is present in all rows
    const isPresentInAllRows = (value) => {
        for (let i = 0; i < rows; i++) {
            if (binarySearch(mat[i], value) === -1) {
                return false;
            }
        }
        return true;
    }

    // Binary search function to find the index of a value in a sorted array
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

        return -1; // Element not found
    }

    // Find the minimum and maximum values in the first row
    let low = mat[0][0];
    let high = mat[0][cols - 1];

    // Binary search for the smallest common element
    while (low <= high) {
        const mid = Math.floor((low + high) / 2);

        if (isPresentInAllRows(mid)) {
            // If mid is present in all rows, search in the left half
            high = mid - 1;
        } else {
            // If mid is not present in all rows, search in the right half
            low = mid + 1;
        }
    }

    return low;
}

/* Kth Smallest Subarray Sum - Given an integer array nums of length n and an integer k, return the kth smallest subarray sum.
 */
const kthSmallestSubarraySum = (nums, k) => {
    // Helper function to count the number of subarrays with sum less than or equal to maxSum
    const countSubarrays = (maxSum) => {
        let count = 0;
        let sum = 0;
        let left = 0;

        // Use a sliding window approach to count subarrays
        for (let right = 0; right < nums.length; right++) {
            sum += nums[right];

            // Shrink the window from the left side if the sum becomes greater than maxSum
            while (sum > maxSum) {
                sum -= nums[left];
                left++;
            }

            // Count the subarrays with sum less than or equal to maxSum
            count += right - left + 1;
        }

        return count;
    }

    // Initialize the search range with the minimum and maximum values in the array
    let left = Math.min(...nums);
    let right = Math.max(...nums);

    // Perform binary search to find the kth smallest subarray sum
    while (left < right) {
        const mid = Math.floor((left + right) / 2);
        const count = countSubarrays(mid);

        // Adjust the search range based on the count of subarrays
        if (count < k) {
            left = mid + 1;
        } else {
            right = mid;
        }
    }

    // Return the final result
    return left;
}

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
    // Function to check if it's possible to cut ribbons into segments with a given length
    const canCut = (length) => {
        let count = 0;
        for (const ribbon of ribbons) {
            // Count how many segments can be obtained from each ribbon with the given length
            count += Math.floor(ribbon / length);
        }
        // Check if the total count of segments is greater than or equal to the target (k)
        return count >= k;
    };

    // Binary search to find the maximum valid ribbon length
    let left = 1; // The minimum possible ribbon length
    let right = Math.max(...ribbons); // The maximum possible ribbon length

    while (left <= right) {
        const mid = Math.floor((left + right) / 2); // Calculate the middle length

        if (canCut(mid)) {
            // If it's possible to cut the ribbons with the current length, search in the right half
            left = mid + 1;
        } else {
            // If it's not possible to cut the ribbons with the current length, search in the left half
            right = mid - 1;
        }
    }

    // The result is right since we want to maximize the ribbon length
    return right;
};

