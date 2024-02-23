// /////////Output based questions//////////
// /////////Event loop//////////////////////

console.log(0);
setTimeout(() => console.log(1));
new Promise((res) => {
    console.log(2);
    res(false);
})
    .then(() => console.log(3))
    .catch(() => console.log(4));
console.log(5);

//output -> 
// 0
// 2
// 3
// 5
// 1

// /////////Output based questions//////////
// ////////Promise Chaining////////////////

Promise.resolve()
    .then(() => 1)
    .catch(() => 2)
    .then((a) => console.log(a))
    .then((b) => console.log(b))
    .then(() => {
        throw new Error();
    })
    .catch(() => 3)
    .then((c) => console.log(c));

//output -> 
// 1
// undefined
// 3

// /////////Output based questions//////////
// //////////////////////Event Propogation////////////////////

// outerDiv ->
//     innerDiv ->
//          button


const outerDiv = document.getElementById("outer");
const innerDiv = document.getElementById("inner");

outerDiv.addEventListener("click", () => console.log("outerDiv -> bubble"));
outerDiv.addEventListener("click", () => console.log("outerDiv -> capture"), { capture: true, });
outerDiv.addEventListener("click", () => console.log("outerDiv -> bubble 2"));

innerDiv.addEventListener("click", () => console.log("innerDiv -> bubble 3"));
innerDiv.addEventListener("click", () => console.log("innerDiv -> capture 2"), { capture: true, })

/* design custom promise all */

const promiseAllCustom = (promises) => {
    return new Promise((resolve, rej) => {
        const resArr = []
        let count = 0
        for (let i = 0; i < promises.length; i++) {
            const promise = promises[i]
            promise
                .then(res => {
                    count++
                    resArr[i] = res
                    if (count === promises.length) {
                        resolve(resArr)
                    }
                })
                .catch(err => rej(err))
        }
    })
}

const promises = [
    new Promise((res) => res("promise1 resolved")),
    new Promise((res) => {
        setTimeout(function () {
            res("promise3 resolved");
        }, 1000);
    }),
    Promise.resolve(`promise2 resolved`),]

promiseAllCustom(promises).then(res => console.log(res)).catch(err => console.log(err))

/* Print output of below */
setTimeout(() => console.log(1), 0);
console.log(2);
new Promise(res => {
    console.log(3)
    res();
}).then(() => console.log(4));
console.log(5);

// 2 -> 3 -> 4 -> 5 -> 1

/* Event loop */

console.log('Start');
setTimeout(function () {
    console.log('First setTimeout');
}, 0);
Promise.resolve().then(function () {
    console.log('Promise 1');
});
Promise.resolve().then(function () {
    console.log('Promise 2');
});
console.log('End');

// Start
// End
// Promise 1
// Promise 2
// First setTimeout

/* event propogation */

<div id="outer">
    <div id="middle">
        <div id="inner">Click me</div>
    </div>
</div>

document.getElementById('outer').addEventListener('click', function () {
    console.log('Outer div clicked');
}, true); // true for capturing phase

document.getElementById('middle').addEventListener('click', function () {
    console.log('Middle div clicked');
}, true); // true for capturing phase

document.getElementById('inner').addEventListener('click', function () {
    console.log('Inner div clicked');
}); // No capture phase specified, defaults to bubbling phase

// Outer div clicked
// Middle div clicked
// Inner div clicked
// Middle div clicked
// Outer div clicked
