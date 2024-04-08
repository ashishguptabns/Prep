const rand7 = () => {
    let random = Math.floor(Math.random() * 7)
    return random
}

const arr = []

for (let i = 0; i < 7000; i++) {
    const random = rand7()
    arr[random] = arr[random] || 0
    arr[random]++
}

const rand3 = () => {
    return Math.floor(rand7() / 3)
}

const arr3 = []

for (let i = 0; i < 9000; i++) {
    const random = rand3()
    arr3[random] = arr3[random] || 0
    arr3[random]++
}

console.log(arr3)