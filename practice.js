// Quick sort

const sort = (arr) => {
    if (arr.length <= 1) {
        return arr
    }
    const pivot = arr[0]
    const left = []
    const right = []
    const mid = []
    for (const num of arr) {
        if (num < pivot) {
            left.push(num)
        }
        else if (num > pivot) {
            right.push(num)
        } else {
            mid.push(num)
        }
    }
    return [...sort(left), ...mid, ...sort(right)]
}

console.log(sort([1, 0, 3, 2, 4, 1]))