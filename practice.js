/**
 * Palindrome
 * A string is a palindrome if it is read the same from forward or backward.
 * For example, 'dad' reads the same either from forward or backward.
 * So the word 'dad' is a palindrome.
 * Similarly, 'madam' is also a palindrome, as is 'noon'.
 * ˜
 * In your preferred language (either JS or Python), write a function to:
 * 
 * Part I - Check if a string is a palindrome using a For Loop
 * Part II - Check if a string is a palindrome using built-in language functions
 * 
*/

const isPalindrome = (s) => {
    let [left, right] = [0, s.length - 1]
    while (left < right) {
        if (s[left++] !== s[right--]) {
            return false
        }
    }
    return true
}

const words = ["racecar", "level", "cvic", "radar", "kayak", "elephant", "computer", "banana", "telescope", "firetruck"];
const result = [true, true, true,
    true, true, false,
    false, false, false,
    false]

const test = (result, words) => {
    let i = 0
    for (const word of words) {
        if (result[i] !== isPalindrome(word)) {
            throw new Error(`code is wrong. Output: ${isPalindrome(word)} Expected: ${result[i]}`)
        }
        i++
    }
    console.log('all tests passing')
}

test(result, words)