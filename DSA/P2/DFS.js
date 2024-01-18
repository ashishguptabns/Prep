/* Number of islands - Given an m x n 2D binary grid which represents a map of '1's (land) and '0's (water), return the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.
 */
/**
 * @param {character[][]} grid
 * @return {number}
 */
const numIslands = (grid) => {

    /* pseudo code
        move r through rows
            move c through cols
                if found a land
                    increment the count
                    run a DFS to mark each connected 1 as 0
                        connected 1s are part of same island
    */

    let ans = 0

    const dfs = (grid, row, col) => {
        const rows = grid.length
        const cols = grid[0].length

        if (row < 0 || row >= rows || col < 0 || col >= cols || grid[row][col] === '0') {
            return
        }

        grid[row][col] = '0'

        dfs(grid, row, col - 1)
        dfs(grid, row, col + 1)
        dfs(grid, row + 1, col)
        dfs(grid, row - 1, col)
    }

    if (grid && grid.length) {
        const rows = grid.length
        const cols = grid[0].length

        for (let row = 0; row < rows; row++) {
            for (let col = 0; col < cols; col++) {
                if (grid[row][col] === '1') {
                    ans++
                    dfs(grid, row, col)
                }
            }
        }
    }

    return ans
};

/* Battleships in a board - Given an m x n matrix board where each cell is a battleship 'X' or empty '.', return the number of the battleships on board.

Battleships can only be placed horizontally or vertically on board.In other words, they can only be made of the shape 1 x k(1 row, k columns) or k x 1(k rows, 1 column), where k can be of any size. At least one horizontal or vertical cell separates between two battleships(i.e., there are no adjacent battleships).
 */
/**
 * @param {character[][]} board
 * @return {number}
 */
const countBattleships = (board) => {

    /* pseudo code
        visit each cell of the matrix
            if a new ship X is found
                increase the count
                do a DFS from the cell to mark adjacent cells as empty
    */

    let ships = 0

    const clearAdjacent = (board, row, col) => {
        if (row < 0 || row >= board.length || col < 0 || col >= board[row].length
            || board[row][col] === '.') {
            return
        }

        board[row][col] = '.'

        clearAdjacent(board, row + 1, col)
        clearAdjacent(board, row - 1, col)
        clearAdjacent(board, row, col + 1)
        clearAdjacent(board, row, col - 1)
    }

    for (let row = 0; row < board.length; row++) {
        for (let col = 0; col < board[row].length; col++) {
            if (board[row][col] === 'X') {
                ships++
                clearAdjacent(board, row, col)
            }
        }
    }

    return ships
};

/* Most Stones Removed with Same Row or Column - On a 2D plane, we place n stones at some integer coordinate points.Each coordinate point may have at most one stone.
A stone can be removed if it shares either the same row or the same column as another stone that has not been removed.
Given an array stones of length n where stones[i] = [xi, yi] represents the location of the ith stone, return the largest possible number of stones that can be removed.
 */
/**
 * @param {number[][]} stones
 * @return {number}
 */
const removeStones = (stones) => {

    /* pseudo code
        move through each stone
            track unvisited stone groups
            run a DFS to mark stones on the same row and col as visited
        return stones given - (stones that can not be removed)
    */

    const visited = new Set()
    let newStoneGroups = 0

    const dfs = (r, c) => {
        const key = `${r}_${c}`
        if (!visited.has(key)) {
            visited.add(key)
            for (const [newR, newC] of stones) {
                if (newR === r || newC === c) {
                    dfs(newR, newC)
                }
            }
        }
    }

    for (const [r, c] of stones) {
        const key = `${r}_${c}`
        if (!visited.has(key)) {
            dfs(r, c)
            newStoneGroups++
        }
    }

    return stones.length - newStoneGroups
};

/* Max area of island - You are given an m x n binary matrix grid. An island is a group of 1's (representing land) connected 4-directionally (horizontal or vertical.) You may assume all four edges of the grid are surrounded by water.

The area of an island is the number of cells with a value 1 in the island.

Return the maximum area of an island in grid. If there is no island, return 0.
 */
/**
 * @param {number[][]} grid
 * @return {number}
 */
const maxAreaOfIsland = (grid) => {

    /* pseudo code
        visit each cell of the matrix
            if an island is found
                run a DFS to find the area  
                    mark cell as water
                        return 1 + DFS areas in 4 dirs
                keep tracking the max area
    */

    let maxArea = 0
    const n = grid.length, m = grid[0].length
    const DFS = (r, c) => {
        if (r < 0 || c < 0 || r >= n || c >= m || !grid[r][c]) {
            return 0
        }
        grid[r][c] = 0
        return 1 + DFS(r - 1, c) + DFS(r, c - 1) + DFS(r + 1, c) + DFS(r, c + 1)
    }
    for (let r = 0; r < n; r++) {
        for (let c = 0; c < m; c++) {
            if (grid[r][c]) {
                maxArea = Math.max(maxArea, DFS(r, c))
            }
        }
    }
    return maxArea
};

/* The Maze - There is a ball in a maze with empty spaces (represented as 0) and walls (represented as 1). The ball can go through the empty spaces by rolling up, down, left or right, but it won't stop rolling until hitting a wall. When the ball stops, it could choose the next direction.

Given the m x n maze, the ball's start position and the destination, where start = [startrow, startcol] and destination = [destinationrow, destinationcol], return true if the ball can stop at the destination, otherwise return false.
 */
const hasPath = (maze, start, destination) => {

    /* pseudo code
        start a DFS from starting cell
            keep doing DFS in adjacent cells till we find the destination cell
            keep a visited map to avoid cycles
    */

    const [startRow, startCol] = start;
    const [destRow, destCol] = destination;
    const ROWS = maze.length;
    const COLS = maze[0].length;
    const visited = Array.from({ length: ROWS }, () => Array(COLS).fill(false));

    const directions = [[-1, 0], [1, 0], [0, -1], [0, 1]];

    const isValidCell = (r, c) => r >= 0 && r < ROWS && c >= 0 && c < COLS

    const dfs = (row, col) => {
        if (row === destRow && col === destCol) {
            return true;
        }

        visited[row][col] = true;

        for (const [dr, dc] of directions) {
            let newRow = row;
            let newCol = col;

            while (isValidCell(newRow + dr, newCol + dc) &&
                maze[newRow + dr][newCol + dc] === 0
            ) {
                newRow += dr;
                newCol += dc;
            }

            if (!visited[newRow][newCol]) {
                if (dfs(newRow, newCol)) {
                    return true;
                }
            }
        }

        return false;
    };

    return dfs(startRow, startCol);
};

