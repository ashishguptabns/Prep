/* Alien Dictionary - There is a new alien language that uses the English alphabet. However, the order among the letters is unknown to you.

You are given a list of strings words from the alien language's dictionary, where the strings in words are sorted lexicographically by the rules of this new language.

Return a string of the unique letters in the new alien language sorted in lexicographically increasing order by the new language's rules. If there is no solution, return "". If there are multiple solutions, return any of them.
 */

const findDict = (words) => {
    const letters = []

    for (let i = 0; i < words.length - 1; i++) {
        const curr = words[i]
        const next = words[i + 1]
        for (let j = 0; j < curr.length; j++) {
            if (curr[j] !== next[j] && !letters.includes(curr[j])) {
                letters.push(curr[j])
                break
            }
        }
    }

    for (const c of words.at(-1)) {
        if (!letters.includes(c)) {
            letters.push(c)
        }
    }

    return letters.length ? letters : ''
}

console.log(findDict(['baa', 'abcd', 'abca', 'cab', 'cad']))
console.log(findDict(['caa', 'aaa', 'aab']))
console.log(findDict(["kaa", "akcd", "akca", "cak", "cad"]))
console.log(findDict(["b","a"]))
console.log(findDict( ["ab","a","b"]))