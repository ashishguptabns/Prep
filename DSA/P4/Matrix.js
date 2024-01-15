/* Rotate Image - You are given an n x n 2D matrix representing an image, rotate the image by 90 degrees (clockwise).

You have to rotate the image in-place, which means you have to modify the input 2D matrix directly. DO NOT allocate another 2D matrix and do the rotation. */

/**
 * @param {number[][]} matrix
 * @return {void} Do not return anything, modify matrix in-place instead.
 */
const rotate = (matrix) => {

    /* pseudo code
        move row till half all rows
            move col from row till end of column - row
                do the swapping
    */

    let row = 0;

    while (row < matrix.length / 2) {
        let col = row;
        while (col < matrix.length - 1 - row) {
            const temp = matrix[row][col];
            matrix[row][col] = matrix[matrix.length - 1 - col][row];
            matrix[matrix.length - 1 - col][row] = matrix[matrix.length - 1 - row][matrix.length - 1 - col];
            matrix[matrix.length - 1 - row][matrix.length - 1 - col] = matrix[col][matrix.length - 1 - row];
            matrix[col][matrix.length - 1 - row] = temp;
            col++;
        }
        row++;
    }
};

/* Set matrix zeroes - Given an m x n integer matrix matrix, if an element is 0, set its entire row and column to 0's.
 */
/**
 * @param {number[][]} matrix
 * @return {void} Do not return anything, modify matrix in-place instead.
 */
const setZeroes = (matrix) => {

    /* pseudo code
        keep row and col arrays to store indices
        visit each cell
            this cell has 0
                push r to row
                push c to col
        move r through indices in row
            move c through cols
                mark each cell as 0
        move c through indices in col
            move r through rows
                mark each cell as 0
    */

    const ROWS = matrix.length
    const COLS = matrix[0].length

    const row = []
    const col = []

    for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
            if (matrix[r][c] === 0) {
                row.push(r)
                col.push(c)
            }
        }
    }

    for (let r = 0; r < row.length; r++) {
        for (let c = 0; c < COLS; c++) {
            matrix[row[r]][c] = 0
        }
    }

    for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < col.length; c++) {
            matrix[r][col[c]] = 0
        }
    }
};

/* Search a 2D Matrix - You are given an m x n integer matrix matrix with the following two properties:

Each row is sorted in non-decreasing order.
The first integer of each row is greater than the last integer of the previous row.
Given an integer target, return true if target is in matrix or false otherwise.
 */
/**
 * @param {number[][]} matrix
 * @param {number} target
 * @return {boolean}
 */
const searchMatrix = (matrix, target) => {

    /* pseudo code
        start from bottom left
        loop till row and col are within bounds
            found a target
            more than target
                move up
            les than target
                move right
    */

    let col = 0, row = matrix.length - 1

    while (col <= matrix[0].length - 1 && row >= 0) {
        if (matrix[row][col] === target) {
            return true
        }
        else if (matrix[row][col] > target) {
            row--
        }
        else if (matrix[row][col] < target) {
            col++
        }
    }
    return false
};

/* Spiral matrix - Given an m x n matrix, return all elements of the matrix in spiral order.
 */

/**
 * @param {number[][]} matrix
 * @return {number[]}
 */
const spiralOrder = (matrix) => {
    let res = []
    let left = 0
    let right = matrix[0].length
    let top = 0
    let bottom = matrix.length

    while (left < right && top < bottom) {
        for (let c = left; c < right; c++) {
            res.push(matrix[top][c])
        }
        top++

        for (let r = top; r < bottom; r++) {
            res.push(matrix[r][right - 1])
        }
        right--

        if (!(left < right && top < bottom)) {
            break
        }

        for (let c = right - 1; c >= left; c--) {
            res.push(matrix[bottom - 1][c])
        }
        bottom--

        for (let r = bottom - 1; r >= top; r--) {
            res.push(matrix[r][left])
        }
        left++
    }

    return res
};

/* Game of life - The board is made up of an m x n grid of cells, where each cell has an initial state: live (represented by a 1) or dead (represented by a 0). Each cell interacts with its eight neighbors (horizontal, vertical, diagonal) using the following four rules (taken from the above Wikipedia article):

Any live cell with fewer than two live neighbors dies as if caused by under-population.
Any live cell with two or three live neighbors lives on to the next generation.
Any live cell with more than three live neighbors dies, as if by over-population.
Any dead cell with exactly three live neighbors becomes a live cell, as if by reproduction.
The next state is created by applying the above rules simultaneously to every cell in the current state, where births and deaths occur simultaneously. Given the current state of the m x n grid board, return the next state.
 */
/**
 * @param {number[][]} board
 * @return {void} Do not return anything, modify board in-place instead.
 */
const gameOfLife = (board) => {
    const dirs = [[0, 1], [1, 0], [-1, 0], [0, -1], [1, 1], [1, -1], [-1, -1], [-1, 1]];

    for (let r = 0; r < board.length; r++) {
        for (let c = 0; c < board[0].length; c++) {
            let neighbours = 0;

            for (const [x, y] of dirs) {
                const cell = board[r + x] ? board[r + x][c + y] : 0;

                if (cell === 1 || cell == 3) {
                    neighbours++;
                }
            }

            if (board[r][c] === 0 || board[r][c] === 3) {
                if (neighbours === 3) {
                    board[r][c] = -1;
                }
            } else {
                if (neighbours < 2 || neighbours > 3) {
                    board[r][c] = 3;
                }
            }
        }
    }

    for (var i = 0; i < board.length; i++) {
        for (var j = 0; j < board[0].length; j++) {
            if (board[i][j] == 3) board[i][j] = 0;
            if (board[i][j] == -1) board[i][j] = 1;
        }
    }
};

