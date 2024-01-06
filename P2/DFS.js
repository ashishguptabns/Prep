/* Number of islands - Given an m x n 2D binary grid which represents a map of '1's (land) and '0's (water), return the number of islands. An island is surrounded by water and is formed by connecting adjacent lands horizontally or vertically. You may assume all four edges of the grid are all surrounded by water.
 */
/**
 * @param {character[][]} grid
 * @return {number}
 */
const numIslands = (grid) => {

    /* pseudo code
        
    */

    let ans = 0

    const dfs = (grid, row, col) => {
        const rows = grid.length
        const cols = grid[0].length

        if (row < 0 || row >= rows || col < 0 || col >= cols || grid[row][col] === '0') {
            return
        }

        //  mark as visited if there is a connecting 1 because that's the part of the same island
        grid[row][col] = '0'

        //  find other 1s and mark them visited
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
                    //  the moment we find a '1' automatically there is atleast one island
                    //  because other connecting 1s are marked as 0 or visited. 
                    //  Connected 1s are
                    //  counted as 1 island
                    ans++
                    dfs(grid, row, col)
                }
            }
        }
    }

    return ans
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
    //  use DFS
    const visited = new Set()
    let stoneGroups = 0

    const traverse = (x, y) => {
        const key = `${x}_${y}`
        if (!visited.has(key)) {
            visited.add(key)
            for (const [newX, newY] of stones) {
                if (newX === x || newY === y) {
                    traverse(newX, newY)
                }
            }
        }
    }

    for (const [x, y] of stones) {
        const key = `${x}_${y}`
        if (!visited.has(key)) {
            traverse(x, y)
            stoneGroups++
        }
    }

    return stones.length - stoneGroups
};

/* Battleships in a board - Given an m x n matrix board where each cell is a battleship 'X' or empty '.', return the number of the battleships on board.

Battleships can only be placed horizontally or vertically on board.In other words, they can only be made of the shape 1 x k(1 row, k columns) or k x 1(k rows, 1 column), where k can be of any size.At least one horizontal or vertical cell separates between two battleships(i.e., there are no adjacent battleships).
 */
/**
 * @param {character[][]} board
 * @return {number}
 */
const countBattleships = (board) => {

    //  solve using DFS

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

    //  visit each cell and mark adjacent cells
    for (let row = 0; row < board.length; row++) {
        for (let col = 0; col < board[row].length; col++) {
            if (board[row][col] === 'X') {
                //  found a ship as per def
                ships++
                clearAdjacent(board, row, col)
            }
        }
    }

    return ships
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
    const ROWS = grid.length
    const COLUMNS = grid[0].length

    const DFS = (row, column) => {
        //  we will store indices in this stack
        const stack = [[row, column]];
        let currRow, currColumn, resultArea = 0;
        while (stack.length !== 0) {
            [currRow, currColumn] = stack.pop();
            if (grid[currRow][currColumn] !== 1)
                continue;

            //  stop duplication
            grid[currRow][currColumn] = 2;

            //  found further land
            resultArea++;

            //  explore 4 directions
            if (currRow + 1 < ROWS)
                stack.push([currRow + 1, currColumn]);
            if (currRow - 1 >= 0)
                stack.push([currRow - 1, currColumn]);
            if (currColumn + 1 < COLUMNS)
                stack.push([currRow, currColumn + 1]);
            if (currColumn - 1 >= 0)
                stack.push([currRow, currColumn - 1]);
        }
        return resultArea;
    };

    let maxArea = 0

    //  visit each cell
    for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLUMNS; c++) {
            if (grid[r][c] === 1) {
                maxArea = Math.max(maxArea, DFS(r, c))
            }
        }
    }
    return maxArea
};

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
    // Get the number of rows and columns in the grid
    const ROWS = grid.length
    const COLS = grid[0].length

    // Depth First Search (DFS) function to explore adjacent cells
    const dfs = (r, c, grid, mins) => {
        // Base cases to stop recursion
        if (r < 0 || r >= ROWS || c < 0 || c >= COLS
            || grid[r][c] === 0 || (grid[r][c] > 1 && grid[r][c] < mins)) {
            return
        }

        // Mark the cell with the current time (minutes)
        grid[r][c] = mins

        // Explore adjacent cells in four directions
        dfs(r + 1, c, grid, mins + 1)
        dfs(r - 1, c, grid, mins + 1)
        dfs(r, c + 1, grid, mins + 1)
        dfs(r, c - 1, grid, mins + 1)
    }

    // Starting time for rotten oranges
    let mins = 2

    // Iterate over the grid to find initially rotten oranges and start DFS
    for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
            if (grid[r][c] === 2) {
                dfs(r, c, grid, mins)
            }
        }
    }

    // Check if there are any fresh oranges left or find the maximum time for all cells
    for (let r = 0; r < ROWS; r++) {
        for (let c = 0; c < COLS; c++) {
            if (grid[r][c] === 1) {
                // If there are fresh oranges left, return -1 (impossible to rot all oranges)
                return -1
            }
            // Update the maximum time encountered so far
            mins = Math.max(mins, grid[r][c])
        }
    }

    // Return the time taken to rot all oranges (subtract 2 to get the actual time)
    return mins - 2
};

/* The Maze - There is a ball in a maze with empty spaces (represented as 0) and walls (represented as 1). The ball can go through the empty spaces by rolling up, down, left or right, but it won't stop rolling until hitting a wall. When the ball stops, it could choose the next direction.

Given the m x n maze, the ball's start position and the destination, where start = [startrow, startcol] and destination = [destinationrow, destinationcol], return true if the ball can stop at the destination, otherwise return false.
 */
const hasPath = (maze, start, destination) => {
    const [startRow, startCol] = start;
    const [destRow, destCol] = destination;
    const rows = maze.length;
    const cols = maze[0].length;
    const visited = Array.from({ length: rows }, () => Array(cols).fill(false));

    const directions = [[-1, 0], [1, 0], [0, -1], [0, 1]];

    const dfs = (row, col) => {
        // Check if the current position is the destination
        if (row === destRow && col === destCol) {
            return true;
        }

        // Mark the current position as visited
        visited[row][col] = true;

        // Explore in all directions
        for (const [dr, dc] of directions) {
            let newRow = row;
            let newCol = col;

            // Keep rolling until hitting a wall
            while (
                newRow + dr >= 0 &&
                newRow + dr < rows &&
                newCol + dc >= 0 &&
                newCol + dc < cols &&
                maze[newRow + dr][newCol + dc] === 0
            ) {
                newRow += dr;
                newCol += dc;
            }

            // Check if the new position has not been visited
            if (!visited[newRow][newCol]) {
                // Recursively explore the new position
                if (dfs(newRow, newCol)) {
                    return true;
                }
            }
        }

        return false;
    };

    // Start DFS from the given start position
    return dfs(startRow, startCol);
};