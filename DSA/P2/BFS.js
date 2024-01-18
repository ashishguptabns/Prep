/* Word Ladder - A transformation sequence from word beginWord to word endWord using a dictionary wordList is a sequence of words beginWord -> s1 -> s2 -> ... -> sk such that:
Every adjacent pair of words differs by a single letter.
Every si for 1 <= i <= k is in wordList.Note that beginWord does not need to be in wordList.
    sk == endWord
Given two words, beginWord and endWord, and a dictionary wordList, return the number of words in the shortest transformation sequence from beginWord to endWord, or 0 if no such sequence exists.
 */
/**
 * @param {string} beginWord
 * @param {string} endWord
 * @param {string[]} wordList
 * @return {number}
 */
const ladderLength = (beginWord, endWord, wordList) => {

    /* pseudo code
        notice that in each word change only one char is allowed to change
        start the BFS with starting word
            loop until queue is empty
                loop through curr items in the queue
                    move i through the curr word
                        move j through all chars till 26
                            make a potential word
                            check if it belongs to word list
                                push to queue for next visit
                keep incrementing num steps
    */

    const wordSet = new Set(wordList);

    if (!wordSet.has(endWord)) {
        return 0;
    }

    let steps = 1;

    let queue = [beginWord];
    while (queue.length) {
        const next = [];
        for (const curr of queue) {
            if (curr === endWord) {
                return steps;
            }

            for (let i = 0; i < curr.length; i++) {
                for (let j = 0; j < 26; j++) {
                    const nextWord = curr.slice(0, i) + String.fromCharCode(97 + j) + curr.slice(i + 1);

                    if (wordSet.has(nextWord)) {
                        next.push(nextWord);
                        wordSet.delete(nextWord);
                    }
                }
            }
        }
        queue = next;
        steps++;
    }

    return 0;
};

/* Minimum Genetic Mutation - Given the two gene strings startGene and endGene and the gene bank bank, return the minimum number of mutations needed to mutate from startGene to endGene. If there is no such a mutation, return -1.

Note that the starting point is assumed to be valid, so it might not be included in the bank.
 */
/**
 * @param {string} startGene
 * @param {string} endGene
 * @param {string[]} bank
 * @return {number}
 */
const minMutation = (startGene, endGene, bank) => {

    /* pseudo code
        start BFS with curr gene
            loop until queue is empty
                move i through curr gene
                    move through valid mutations
                        create a new Gene
                        check if this gene can be added to the queue for next travel
    */

    const validMutations = ['A', 'C', 'G', 'T'];

    const geneSet = new Set(bank);

    if (!geneSet.has(endGene)) {
        return -1;
    }

    const visitedGenes = new Set();

    const queue = [[startGene, 0]];

    visitedGenes.add(startGene);

    while (queue.length > 0) {
        const [currGene, numMutations] = queue.shift();

        if (currGene === endGene) {
            return numMutations;
        }

        for (let i = 0; i < currGene.length; i++) {
            for (let mutationChar of validMutations) {
                if (currGene[i] === mutationChar) {
                    continue;
                }

                const newGene = currGene.slice(0, i) + mutationChar + currGene.slice(i + 1);

                if (geneSet.has(newGene) && !visitedGenes.has(newGene)) {
                    queue.push([newGene, numMutations + 1]);
                    visitedGenes.add(newGene);
                }
            }
        }
    }
    return -1;
};

/* Shortest Path to Get Food - You are starving and you want to eat food as quickly as possible. You want to find the shortest path to arrive at any food cell.

You are given an m x n character matrix, grid, of these different types of cells:

'*' is your location. There is exactly one '*' cell.
'#' is a food cell. There may be multiple food cells.
'O' is free space, and you can travel through these cells.
'X' is an obstacle, and you cannot travel through these cells.
You can travel to any adjacent cell north, east, south, or west of your current location if there is not an obstacle.

Return the length of the shortest path for you to reach any food cell. If there is no path for you to reach food, return -1.
 */

const getFood = grid => {

    /* pseudo code
        find the curr pos in the matrix
        start BFS with the curr pos
            loop through the queue
                check if neighbours can be pushed in the queue for next travel
    */

    const ROWS = grid.length;
    const COLS = grid[0].length;

    let startX, startY;
    for (let i = 0; i < ROWS; i++) {
        for (let j = 0; j < COLS; j++) {
            if (grid[i][j] === '*') {
                startX = i;
                startY = j;
                break;
            }
        }
    }

    const directions = [[-1, 0], [1, 0], [0, -1], [0, 1]];

    const visited = new Set();

    const isValidCell = (x, y) => x >= 0 && x < ROWS && y >= 0 && y < COLS

    const queue = [[startX, startY, 0]];

    while (queue.length > 0) {
        const [x, y, steps] = queue.shift();

        if (grid[x][y] === '#') {
            return steps;
        }

        for (const [dx, dy] of directions) {
            const newX = x + dx;
            const newY = y + dy;

            if (isValidCell(newX, newY) && grid[newX][newY] !== 'X' && !visited.has(`${newX}-${newY}`)) {
                visited.add(`${newX}-${newY}`);
                queue.push([newX, newY, steps + 1]);
            }
        }
    }
    return -1;
};

/* Minimum Knight Moves - In an infinite chess board with coordinates from -infinity to +infinity, you have a knight at square [0, 0].
A knight has 8 possible moves it can make, as illustrated below. Each move is two squares in a cardinal direction, then one square in an orthogonal direction.
Return the minimum number of steps needed to move the knight to the square [x, y]. It is guaranteed the answer exists.
 */
const minKnightMoves = (x, y) => {

    /* pseudo code
        we have to do BFS travel from start point to end point
            start the queue with [0, 0]
            loop through queue items
                loop through curr items
                    add neighbour points for next travel
                        we do not want to deal with -ve coords
                            consider [302, 302] as [0, 0]
                increment num steps
    */

    let minSteps = 0;

    const offsets = [
        [1, 2], [-1, 2], [-2, 1], [-2, -1],
        [2, 1], [2, -1], [1, -2], [-1, -2]
    ];

    const visitedArr = Array(607).fill(null).map(() => Array(607).fill(false));

    const queue = [];
    queue.push([0, 0]);

    while (queue.length > 0) {
        const queueSize = queue.length;

        for (let counter = 0; counter < queueSize; counter++) {
            const currPair = queue.shift();

            if (currPair[0] === x && currPair[1] === y) {
                return minSteps;
            }

            for (const [dx, dy] of offsets) {
                const nextX = currPair[0] + dx;
                const nextY = currPair[1] + dy;

                if (!visitedArr[nextX + 302][nextY + 302]) {
                    queue.push([nextX, nextY]);
                    visitedArr[nextX + 302][nextY + 302] = true;
                }
            }
        }

        minSteps++;
    }

    return -1;
}

/* Rotting oranges - You are given an m x n grid where each cell can have one of three values:

- 0 representing an empty cell,
- 1 representing a fresh orange, or
- 2 representing a rotten orange.

Every minute, any fresh orange that is 4-directionally adjacent to a rotten orange becomes rotten.

Return the minimum number of minutes that must elapse until no cell has a fresh orange. If this is impossible, return -1.
 */

/**
 * @param {number[][]} grid
 * @return {number}
 */
const orangesRotting = (grid) => {
    const qu = [];
    let noOfFreshOranges = 0;
    let time = 0;
    for (let i = 0; i < grid.length; i++) {
        for (let j = 0; j < grid[i].length; j++) {
            if (grid[i][j] === 2) qu.push([i, j]);
            if (grid[i][j] === 1) noOfFreshOranges++;
        }
    }

    while (qu.length) {
        const n = qu.length;

        let freshOrngCount = 0;
        for (let i = 0; i < n; i++) {
            const [x, y] = qu.shift();
            // right
            if ((y < grid[x].length - 1) && (grid[x][y + 1] === 1)) {
                freshOrngCount++;
                grid[x][y + 1] = 2
                qu.push([x, y + 1]);
            }
            // bottom
            if ((x < grid.length - 1) && (grid[x + 1][y] === 1)) {
                freshOrngCount++;
                grid[x + 1][y] = 2
                qu.push([x + 1, y]);
            }
            // top
            if ((x >= 1) && (grid[x - 1][y] === 1)) {
                freshOrngCount++;
                grid[x - 1][y] = 2
                qu.push([x - 1, y]);
            }
            // left
            if ((y >= 1) && (grid[x][y - 1] === 1)) {
                freshOrngCount++;
                grid[x][y - 1] = 2
                qu.push([x, y - 1]);
            }
        }
        if (freshOrngCount) time++;
        noOfFreshOranges -= freshOrngCount;
    }

    // if any fresh orange remains
    return noOfFreshOranges === 0 ? time : -1;
};