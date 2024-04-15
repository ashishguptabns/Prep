//  concept rate limiting - throttling

const funcA = (userProps, num) => {
    userProps.lastTs = userProps.lastTs || Date.now()
    if (Date.now() - userProps.lastTs < 60000) {
        if (userProps.count <= 100) {
            userProps.count++
            console.log(num)
        } else {
            console.log('throttled', userProps.count++)
        }
    } else {
        userProps.count = 0
        userProps.lastTs = Date.now()
    }
}

const map = {
    '123': { count: 0, lastTs: null },
    '112': { count: 0, lastTs: null }
}

for (let i = 0; i < 200; i++) {
    setTimeout(() => {
        funcA(map['123'], i + 10)
    }, 0);
}