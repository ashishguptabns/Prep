/* fibonacci */

const map = { 1: 1, 2: 1 }

const findNumOptimised = (count) => {
    if (count > 0) {
        if (map[count]) {
            return map[count]
        }
        map[count] = findNumOptimised(count - 1) + findNumOptimised(count - 2)
        return map[count]
    }
}


// for (let i = 1; i < 294; i++) {
//     console.log(findNumOptimised(i))
// }

/* Write a function that returns the length of the longest word in the provided sentence */

const sentence = 'Write a function that returns the length of the longest word in the provided sentences'

const findLength = (str) => {
    const arr = str.split(' ')
    let max = 0
    for (const s of arr) {
        max = Math.max(max, s.length)
    }

    return max
}

console.log(findLength(sentence))