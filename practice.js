


const graph = {
    A: ['B'],
    B: ['D', 'C'],
    C: ['E', 'F'],
    D: [],
    E: [],
    F: [],
};
const memo = {}
console.log(employeeScoreMemoized(graph, 'A', memo))
console.log(memo)