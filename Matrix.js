/* Rotate Image - You are given an n x n 2D matrix representing an image, rotate the image by 90 degrees (clockwise).

You have to rotate the image in-place, which means you have to modify the input 2D matrix directly. DO NOT allocate another 2D matrix and do the rotation. */

/**
 * @param {number[][]} matrix
 * @return {void} Do not return anything, modify matrix in-place instead.
 */
const rotate = (matrix) => {

    let row = 0;

    // Iterate over each row in the top half of the matrix (up to the middle row)
    while (row < matrix.length / 2) {

        let col = row;

        // Iterate over each column in the current row, up to the last column of the top half
        while (col < matrix.length - 1 - row) {

            // Store the top-left element in a temporary variable
            const temp = matrix[row][col];

            // Move the bottom-left element to the top-left position
            matrix[row][col] = matrix[matrix.length - 1 - col][row];

            // Move the bottom-right element to the bottom-left position
            matrix[matrix.length - 1 - col][row] = matrix[matrix.length - 1 - row][matrix.length - 1 - col];

            // Move the top-right element to the bottom-right position
            matrix[matrix.length - 1 - row][matrix.length - 1 - col] = matrix[col][matrix.length - 1 - row];

            // Move the top-left element (stored in temp) to the top-right position
            matrix[col][matrix.length - 1 - row] = temp;

            col++;
        }

        row++;
    }
};