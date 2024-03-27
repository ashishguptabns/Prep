/* Deepest leaves sum - Given the root of a binary tree, return the sum of values of its deepest leaves.
 */
/**
 * @param {TreeNode} root
 * @return {number}
 */
const deepestLeavesSum = (root) => {

    /* pseudo code
        do a level order travel
        keep tracking the sum of each level and return the last level's 
    */

    if (!root) {
        return 0
    }

    const queue = [root]
    let levelSum

    while (queue.length > 0) {
        const currLevelSize = queue.length
        levelSum = 0

        for (let i = 0; i < currLevelSize; i++) {
            const node = queue.shift()
            levelSum += node.val

            if (node.left) {
                queue.push(node.left)
            }
            if (node.right) {
                queue.push(node.right)
            }
        }
    }

    return levelSum
};

/* Binary tree zigzag level order traversal - Given the root of a binary tree, return the zigzag level order traversal of its nodes' values. (i.e., from left to right, then right to left for the next level and alternate between).
*/
/**
 * @param {TreeNode} root
 * @return {number[][]}
 */
const zigzagLevelOrder = (root) => {

    /* pseudo code
        do a level order travel and keep level information in the queue
            if the level is odd
                push to the last of res[level]
            else
                push to start of res[level]
    */

    let res = []

    if (root) {
        const queue = [[root, 0]]
        while (queue.length) {
            const [node, level] = queue.shift()

            res[level] ??= []

            if (level % 2 == 1) {
                res[level].push(node.val)
            } else {
                res[level].unshift(node.val)
            }
            if (node.right) {
                queue.push([node.right, level + 1])
            }
            if (node.left) {
                queue.push([node.left, level + 1])
            }
        }
    }

    return res
};

/* Binary tree right side view - Given the root of a binary tree, imagine yourself standing on the right side of it, return the values of the nodes you can see ordered from top to bottom.
 */
/**
 * @param {TreeNode} root
 * @return {number[]}
 */
const rightSideView = (root, ans = [], depth = 0) => {

    /* pseudo code
        visit each node from left to right with depth info
        keep setting the value for a depth in an array/map
    */

    if (!root) {
        return ans
    }
    ans[depth] = root.val

    rightSideView(root.left, ans, depth + 1)
    return rightSideView(root.right, ans, depth + 1)
};

/* Same tree - Given the roots of two binary trees p and q, write a function to check if they are the same or not.
 */
/**
 * @param {TreeNode} p
 * @param {TreeNode} q
 * @return {boolean}
 */
const isSameTree = (p, q) => {
    if (!p && !q) {
        return true
    }
    if ((p && !q) || (!p && q) || (p.val !== q.val)) {
        return false
    }

    return isSameTree(p.left, q.left) && isSameTree(p.right, q.right)
};

/* Inorder traversal - Given the root of a binary tree, return the inorder traversal of its nodes' values.
 */
const inorderTraversal = (root) => {
    const inorderList = []

    const travel = (node, inorderList) => {
        if (node) {
            travel(node.left, inorderList)
            inorderList.push(node.val)
            travel(node.right, inorderList)
        }
    }

    travel(root, inorderList)

    return inorderList
};

/* Convert a BST into a GST(greater sum tree) - Given the root of a Binary Search Tree(BST), convert it to a Greater Tree such that every key of the original BST is changed to the original key plus the sum of all keys greater than the original key in BST.
 */
/**
 * @param {TreeNode} root
 * @return {TreeNode}
 */
const bstToGst = (root) => {
    //  we will do reverse inorder traversal and maintain the sum while coming from right

    let sum = 0

    const reverseInorder = (node) => {
        if (node) {
            reverseInorder(node.right)

            sum += node.val
            node.val = sum

            reverseInorder(node.left)
        }
    }
    reverseInorder(root)

    return root
};

/* Inorder Successor in BST - Given the root of a binary search tree and a node p in it, return the in -order successor of that node in the BST.If the given node has no in -order successor in the tree, return null.

The successor of a node p is the node with the smallest key greater than p.val.
 */

const inorderSuccessor = (root, p) => {

    /* pseudo code
        loop through the nodes
            if a node is greater than target node
                go to left
                also this node can be the successor
            else go to right
    */

    let currNode = root
    let successor

    while (currNode != null) {
        if (currNode.val > p.val) {
            successor = currNode
            //  we have to go towards left to find the successor
            currNode = currNode.left
        } else {
            //  go towards the right
            currNode = currNode.right
        }
    }

    return successor
};

/* Binary Tree Level Order Traversal
 */
/**
 * @param {TreeNode} root
 * @return {number[][]}
 */
const levelOrder = (root) => {

    const res = []

    if (root) {
        const queue = [root]

        while (queue.length) {
            const levelSize = queue.length
            const currLevelNodes = []
            for (let i = 0; i < levelSize; i++) {
                const currNode = queue.shift()
                if (currNode.left) {
                    queue.push(currNode.left)
                }
                if (currNode.right) {
                    queue.push(currNode.right)
                }
                currLevelNodes.push(currNode.val)
            }
            res.push([...currLevelNodes])
        }
    }
    return res
};

/* Kth Smallest Element in a BST
 */
/**
 * @param {TreeNode} root
 * @param {number} k
 * @return {number}
 */
const kthSmallest = (root, k) => {
    const inorderArr = []

    const inorder = (root) => {
        if (root) {
            inorder(root.left)
            inorderArr.push(root.val)
            inorder(root.right)
        }
    }

    inorder(root)

    //  k = 1 means 0th element
    return inorderArr[k - 1]
};

/* All Nodes Distance K in Binary Tree - Given the root of a binary tree, the value of a target node target, and an integer k, return an array of the values of all nodes that have a distance k from the target node.
 */
/**
 * @param {TreeNode} root
 * @param {TreeNode} target
 * @param {number} k
 * @return {number[]}
 */
const distanceK = (root, target, k) => {

    /* pseudo code 
        travel to the target node and assign parent to each node visited till target node
        start DFS from target node with k distance
            do not do a repeat visit
            if distance k is covered
                push the node to res array 
            else
                mark the node as visited
                travel in 3 dirs - left, right, parent with distance - 1 left
    */

    if (!root) {
        return
    }

    const res = []

    const findNode = (node, k) => {
        if (node && !node.visited) {
            node.visited = true
            if (k === 0) {
                res.push(node.val)
            } else {
                findNode(node.left, k - 1)
                findNode(node.right, k - 1)
                findNode(node.parent, k - 1)
            }
        }
    }

    const travel = (node, parent) => {
        if (node) {
            node.parent = parent
            if (node.val === target.val) {
                findNode(node, k)
            } else {
                travel(node.left, node)
                travel(node.right, node)
            }
        }
    }
    travel(root, null)

    return res
};

/* Sum of nodes with even valued grandparent - Given the root of a binary tree, return the sum of values of nodes with an even - valued grandparent.If there are no nodes with an even - valued grandparent, return 0.
 */
const sumEvenGrandparent = (root) => {

    /* pseudo code
        visit each node and maintain the parent and gparent 
    */

    let sum = 0

    const dfs = (node, parent, gparent) => {
        if (node) {
            if (gparent && gparent.val % 2 === 0) {
                sum += node.val
            }
            dfs(node.left, node, parent)
            dfs(node.right, node, parent)
        }
    }
    dfs(root, null, null)

    return sum

};

/* Validate Binary Search Tree - Given the root of a binary tree, determine if it is a valid binary search tree (BST).

A valid BST is defined as follows:
- The left subtree of a node contains only nodes with keys less than the node's key.
- The right subtree of a node contains only nodes with keys greater than the node's key.
- Both the left and right subtrees must also be binary search trees.
 */
/**
 * @param {TreeNode} root
 * @return {boolean}
 */
const isValidBST = (root, min = -Infinity, max = Infinity) => {

    /* pseudo code
        visit each node with min and max bounds
    */

    if (!root) {
        return true
    }

    if (root.val <= min || root.val >= max) {
        return false
    }

    return isValidBST(root.left, min, root.val) && isValidBST(root.right, root.val, max)
};

/* Maximum Difference Between Node and Ancestor - Given the root of a binary tree, find the maximum value v for which there exist different nodes a and b where v = |a.val - b.val| and a is an ancestor of b.

A node a is an ancestor of b if either: any child of a is equal to b or any child of a is an ancestor of b.
 */
const maxAncestorDiff = (root) => {

    /* pseudo code
        visit each node with min and max found so far
    */

    let maxDiff = 0

    const findDiff = (node, maxSoFar, minSoFar) => {
        //  notice that max and min are coming from the ancestors
        if (node) {
            maxSoFar = Math.max(maxSoFar, node.val)
            minSoFar = Math.min(minSoFar, node.val)

            maxDiff = Math.max(maxSoFar - minSoFar, maxDiff)

            findDiff(node.left, maxSoFar, minSoFar)
            findDiff(node.right, maxSoFar, minSoFar)
        }
    }
    findDiff(root, root.val, root.val)

    return maxDiff
};

/* Lowest Common Ancestor of two nodes in a Binary Tree - Given a binary tree, find the lowest common ancestor (LCA) of two given nodes in the tree.

According to the definition of LCA on Wikipedia: “The lowest common ancestor is defined between two nodes p and q as the lowest node in T that has both p and q as descendants (where we allow a node to be a descendant of itself).” */

const lowestCommonAncestor = (root, p, q) => {

    /* pseudo code
        visit each node 
            check if p or q is found
            visit left and right subtree recursively
            if both were found in either left or right subtree then this node is the LCA
            else return left or right whichever found p or q
    */

    if (!root || root === p || root === q) {
        return root;
    }

    //  check if any node is present in left or right subtree
    const left = lowestCommonAncestor(root.left, p, q);
    const right = lowestCommonAncestor(root.right, p, q);

    if (left && right) {
        //  we found one node each in left and right subtree hence this is the LCA
        return root;
    } else {
        return left || right;
    }
}

/* Add One Row to Tree - Given the root of a binary tree and two integers val and depth, add a row of nodes with value val at the given depth depth.
 */
/**
 * @param {TreeNode} root
 * @param {number} val
 * @param {number} depth
 * @return {TreeNode}
 */
const addOneRow = (root, val, depth) => {

    /* pseudo code
        special case
            depth = 1
            make new node with root as left kid and return
        visit each node with its depth
            just before the mentioned depth
                add new left node with curr node's left as left child
                add new right node with curr node's right as right child
    */

    if (!root) {
        return root
    }

    if (depth === 1) {
        return new TreeNode(val, root)
    }

    const refactor = (node, currDepth) => {
        if (!node) {
            return
        }

        if (currDepth === depth - 1) {
            const { left, right } = node
            node.left = new TreeNode(val, left)
            node.right = new TreeNode(val, null, right)

            return
        }

        refactor(node.left, currDepth + 1)
        refactor(node.right, currDepth + 1)
    }
    refactor(root, 1)

    return root
};

/* Binary Tree Maximum Path Sum - A path in a binary tree is a sequence of nodes where each pair of adjacent nodes in the sequence has an edge connecting them.A node can only appear in the sequence at most once.Note that the path does not need to pass through the root.

The path sum of a path is the sum of the node's values in the path.

Given the root of a binary tree, return the maximum path sum of any non - empty path.
 */
/**
 * @param {TreeNode} root
 * @return {number}
 */
const maxPathSum = (root) => {

    /* pseudo code
        visit each node
            find max sum from left and right subtrees recursively
            keep tracking the max so far
            return either left or right max with curr node's value
    */

    let max = -Infinity

    const travel = (node) => {
        let sum = 0
        if (node) {
            const left = travel(node.left)
            const right = travel(node.right)
            sum = node.val + left + right
            max = Math.max(sum, max)
            return Math.max(0, node.val + Math.max(left, right))
        }
        return sum
    }
    travel(root)

    return max
};

/* Construct Binary Tree from Preorder and Inorder Traversal - Given two integer arrays preorder and inorder where preorder is the preorder traversal of a binary tree and inorder is the inorder traversal of the same tree, construct and return the binary tree.
 */
/**
 * @param {number[]} preorder
 * @param {number[]} inorder
 * @return {TreeNode}
 */
const buildTree = (preorder, inorder) => {

    /* pseudo code
        preorder
            root is first, left subtree is after then right subtree
        inorder
            left subtree is first, then root then right subtree

        find root from preorder
        find root index in inorder

        find preorder and inorder for left subtree and build
        repeat for right and build

        return curr root
    */

    if (preorder.length) {
        const root = new TreeNode(preorder[0])
        const rootIndexInInorder = inorder.indexOf(root.val)
        root.left = buildTree(preorder.slice(1, rootIndexInInorder + 1),
            inorder.slice(0, rootIndexInInorder))
        root.right = buildTree(preorder.slice(rootIndexInInorder + 1),
            inorder.slice(rootIndexInInorder + 1))
        return root
    }
    return null
};

/* Populating Next Right Pointers in Each Node - You are given a perfect binary tree where all leaves are on the same level, and every parent has two children.Populate each next pointer to point to its next right node.If there is no next right node, the next pointer should be set to NULL.
 */
/**
 * @param {Node} root
 * @return {Node}
 */
const connect = (root) => {

    /* pseudo code
        start from left most node at each level
            keep a curr node to move through the level
                connect left child to right
                connect right child to neighbour's left
                move curr node to next
            move left most node to left kid
    */

    if (!root) {
        return root
    }

    //  we will try to go towards the left most node and connect all the nodes in the next level
    let leftMostNode = root
    while (leftMostNode.left) {
        let currNode = leftMostNode
        while (currNode) {
            //  make the connect of direct kids           
            currNode.left.next = currNode.right

            //  make the connect with neighbors' kids            
            if (currNode.next) {
                currNode.right.next = currNode.next.left
            }

            //  move to right side and repeat
            currNode = currNode.next
        }

        leftMostNode = leftMostNode.left
    }

    return root
};

/* Check Completeness of a Binary Tree - Given the root of a binary tree, determine if it is a complete binary tree.

In a complete binary tree, every level, except possibly the last, is completely filled, and all nodes in the last level are as far left as possible.It can have between 1 and 2h nodes inclusive at the last level h.
 */
/**
 * @param {TreeNode} root
 * @return {boolean}
 */
const isCompleteTree = (root) => {

    /* pseudo code
        do a level order travel
            at each level, track if a node was null and right side neighbours are not null
                not a complete tree
    */

    if (root) {
        const queue = [root]
        let foundNullNode = false
        while (queue.length) {
            const node = queue.shift()

            if (!node) {
                foundNullNode = true
            } else {
                if (foundNullNode) {
                    //  not allowed as per condition
                    return false
                }

                queue.push(node.left)
                queue.push(node.right)
            }
        }
    }

    return true
};

/* Find Duplicate Subtrees - Given the root of a binary tree, return all duplicate subtrees.
For each kind of duplicate subtrees, you only need to return the root node of any one of them.
Two trees are duplicate if they have the same structure with the same node values.
 */
/**
 * @param {TreeNode} root
 * @return {TreeNode[]}
 */
const findDuplicateSubtrees = (root) => {

    /* pseudo code
        keep a map to store preorder vs freq
        do a DFS through each node
            store preorder of subtree with curr node as root
            check the freq of this preorder and track duplicates
    */

    const map = new Map()
    const result = []

    const dfs = (currNode) => {
        if (!currNode) {
            return '#'
        }

        const subtree = `${currNode.val},${dfs(currNode.left)},${dfs(currNode.right)}`
        const freq = map.get(subtree) || 0

        if (freq == 1) {
            result.push(currNode)
        }

        map.set(subtree, freq + 1)

        return subtree
    }
    dfs(root)

    return result
};

/* Count nodes equal to average of subtress - Given the root of a binary tree, return the number of nodes where the value of the node is equal to the average of the values in its subtree. 
Subtree includes the root
 */
/**
 * @param {TreeNode} root
 * @return {number}
 */
const averageOfSubtree = (root) => {

    /* pseudo code
        do a DFS at each node
            DFS for left and right recursively
            find curr subtree sum
            curr subtree nodes count
            find avg
            track count as per condition
            return [count, sum]
    */

    let countAvgNodes = 0
    const dfs = (node) => {
        if (node) {
            const left = dfs(node.left)
            const right = dfs(node.right)

            const subTreeSum = left.sum + right.sum + node.val
            const subTreeCount = 1 + left.count + right.count

            const avg = Math.floor(subTreeSum / subTreeCount)

            if (avg === node.val) {
                countAvgNodes++
            }

            //  send the count also to track
            return { count: subTreeCount, sum: subTreeSum }
        }

        return { count: 0, sum: 0 }
    }
    dfs(root)

    return countAvgNodes
};

/* Maximum binary tree - You are given an integer array nums with no duplicates.A maximum binary tree can be built recursively from nums using the following algorithm:
- Create a root node whose value is the maximum value in nums.
- Recursively build the left subtree on the subarray prefix to the left of the maximum value.
- Recursively build the right subtree on the subarray suffix to the right of the maximum value.
Return the maximum binary tree built from nums.
 */
/**
 * @param {number[]} nums
 * @return {TreeNode}
 */
const constructMaximumBinaryTree = (nums) => {

    /* pseudo code
        find the max item of the array and make it the root
        use left and right subarray to do this recursively
        return curr root
    */

    if (nums.length === 0) {
        return null;
    }

    const getMaxIndex = (nums) => {
        let maxIndex = 0;
        for (let i = 1; i < nums.length; i++) {
            if (nums[i] > nums[maxIndex]) {
                maxIndex = i;
            }
        }
        return maxIndex;
    }
    const maxIndex = getMaxIndex(nums);

    const root = new TreeNode(nums[maxIndex]);
    root.left = constructMaximumBinaryTree(nums.slice(0, maxIndex));
    root.right = constructMaximumBinaryTree(nums.slice(maxIndex + 1));

    return root;
};

/* Balance a BST - Given the root of a binary search tree, return a balanced binary search tree with the same node values.If there is more than one answer, return any of them.

A binary search tree is balanced if the depth of the two subtrees of every node never differs by more than 1.
 */
const balanceBST = (root) => {

    /* pseudo code
        get the inorder array of given BST
        create a new BST
            handle cases of 0 and 1 node
            use the mid item for the root and left, right subarrays for children
            do this recursively
    */

    const inorder = []

    const findInOrder = (root) => {
        if (root) {
            findInOrder(root.left)
            inorder.push(root.val)
            findInOrder(root.right)
        }
    }
    findInOrder(root)

    const makeBST = (inorder) => {
        if (!inorder.length) {
            return
        }
        const mid = Math.floor(inorder.length / 2)
        const root = new TreeNode(inorder[mid],
            makeBST(inorder.slice(0, mid)),
            makeBST(inorder.slice(mid + 1)))

        return root
    }
    return makeBST(inorder)
};

/* Reverse odd levels of binary tree - Given the root of a perfect binary tree, reverse the node values at each odd level of the tree.
 */
const reverseOddLevels = (root) => {
    /* pseudo code
        do a BFS starting from root node
            keep next level nodes in an array
            if an odd level
                go through curr nodes in the queue and reverse the values
            assign next level nodes to the queue for next travel
    */

    let queue = [root]
    let level = 0
    while (queue.length) {
        //  record next level to preserve order
        const nextLevel = []
        for (let i = 0; i < queue.length; i++) {
            const node = queue[i]
            if (node.left) {
                nextLevel.push(node.left)
            }
            if (node.right) {
                nextLevel.push(node.right)
            }
        }

        if (level % 2) {
            //  odd level; reverse the current queue
            for (let i = 0, j = queue.length - 1; i < j; i++, j--) {
                const temp = queue[i].val
                queue[i].val = queue[j].val
                queue[j].val = temp
            }
        }

        //  move to next level
        queue = nextLevel
        level++
    }

    return root
};

/* Boundary of binary tree 
*/
const printBoundary = (root) => {
    const printRightBoundary = (node) => {
        if (node) {
            // Ignore leaf nodes in the right boundary

            if (node.right) {
                printRightBoundary(node.right);
                console.log(node.value);
            } else if (node.left) {
                printRightBoundary(node.left);
                console.log(node.value);
            }
        }
    }

    const printLeaves = (node) => {
        if (node) {
            printLeaves(node.left);

            if (!node.left && !node.right) {
                console.log(node.value); // Leaf node
            }

            printLeaves(node.right);
        }
    }
    const printLeftBoundary = (node) => {
        if (node) {
            if (node.left) {
                console.log(node.value);
                printLeftBoundary(node.left);
            } else if (node.right) {
                console.log(node.value);
                printLeftBoundary(node.right);
            }
            // Ignore leaf nodes in the left boundary
        }
    }

    if (root !== null) {
        // start with root
        console.log(root.value);

        // Print the left boundary (excluding the leaf)
        printLeftBoundary(root.left);

        // Print the leaves
        printLeaves(root.left);
        printLeaves(root.right);

        // Print the right boundary (excluding the leaf)
        printRightBoundary(root.right);
    }
}

/* BST from preorder - Given an array of integers preorder, which represents the preorder traversal of a BST(i.e., binary search tree), construct the tree and return its root.
 */
/**
 * @param {number[]} preorder
 * @return {TreeNode}
 */
const bstFromPreorder = (preorder) => {

    /* pseudo code
        keep a function buildNode with lower and upper bound
            take the first item
                remove this item from preorder array
                build left and right nodes recursively
                return the node
    */
    const buildNode = (lower, upper) => {
        if (preorder.length === 0) {
            return null;
        }
        if (preorder[0] < lower || preorder[0] > upper) {
            return null;
        }
        const node = new TreeNode(preorder.shift());
        node.left = buildNode(lower, node.val);
        node.right = buildNode(node.val, upper);
        return node;
    }
    return buildNode(-Infinity, Infinity);
};

/* Sum root to leaf numbers - You are given the root of a binary tree containing digits from 0 to 9 only.

Each root-to-leaf path in the tree represents a number.

For example, the root-to-leaf path 1 -> 2 -> 3 represents the number 123.
Return the total sum of all root-to-leaf numbers. Test cases are generated so that the answer will fit in a 32-bit integer.
*/
const sumNumbers = (root) => {

    /* pseudo code
        run a DFS from root with node and sum so far
            leaf node
                add to the total
            else
                explore left and right with modified sum
    */

    let total = 0;

    const dfs = (node, sum) => {
        if (!node.left && !node.right) {
            total += sum * 10 + node.val;
        } else {
            if (node.left) {
                dfs(node.left, sum * 10 + node.val);
            }
            if (node.right) {
                dfs(node.right, sum * 10 + node.val);
            }
        }
    }

    dfs(root, 0);

    return total;
};

/* Construct tree from inorder and postorder traversal */
const buildTreeN = (inorder, postorder) => {
    if (inorder.length == 0 || postorder.length == 0) {
        return null;
    }
    const rootVal = postorder.at(-1);
    const root = new TreeNode(rootVal);
    const rootIndex = inorder.indexOf(rootVal);
    const leftInorder = inorder.slice(0, rootIndex);
    const rightInorder = inorder.slice(rootIndex + 1);
    const leftPostorder = postorder.slice(0, leftInorder.length);
    const rightPostorder = postorder.slice(leftInorder.length, postorder.length - 1);
    root.left = buildTree(leftInorder, leftPostorder);
    root.right = buildTree(rightInorder, rightPostorder);
    return root;
};

/* Flatten tree to linked list - Given the root of a binary tree, flatten the tree into a "linked list":

The "linked list" should use the same TreeNode class where the right child pointer points to the next node in the list and the left child pointer is always null.
The "linked list" should be in the same order as a pre-order traversal of the binary tree.
*/
const flatten = (root) => {
    let prev = null
    const traverse = node => {
        if (node === null) {
            return
        }
        traverse(node.right)
        traverse(node.left)
        node.left = null
        node.right = prev
        prev = node
    }
    traverse(root)
};

/* Path In Zigzag Labelled Binary Tree - In an infinite binary tree where every node has two children, the nodes are labelled in row order.

In the odd numbered rows(ie., the first, third, fifth, ...), the labelling is left to right, while in the even numbered rows(second, fourth, sixth, ...), the labelling is right to left.

Given the label of a node in this tree, return the labels in the path from the root of the tree to the node with that label.
 */
/**
 * @param {number} label
 * @return {number[]}
 */
const pathInZigZagTree = (label) => {
    let level = 0;

    //  find the level of label
    while (Math.pow(2, level + 1) <= label) {
        level++;
    }

    const getParent = (label, lvl) => {
        if (label === 1) {
            //  no parent of root
            return [];
        }
        const lvlMaxVal = Math.pow(2, lvl + 1) - 1, lvlMinVal = Math.pow(2, lvl);
        const mirror = lvlMinVal + (lvlMaxVal - label);

        //  parent by def
        const parent = Math.floor(mirror / 2);

        //  find parent to this parent and so on
        return [...getParent(parent, lvl - 1), parent];
    }
    return [...getParent(label, level), label];
};

/* Construct quad tree - Given a n * n matrix grid of 0's and 1's only.We want to represent grid with a Quad - Tree.

Return the root of the Quad - Tree representing grid.
 */
/**
 * @param {number[][]} grid
 * @return {Node}
 */
const construct = (grid) => {

    /* pseudo code
        keep a function with rs, re, cs, ce as inputs
            return a leaf node for a cell
            recursively build 4 children - topLeft, topRight, bottomLeft, bottomRight
            if all children are leaf nodes and have same value
                return a leaf node with any child's value
            return a normal node with four children and value as 1
    */

    //  n * n grid
    let n = grid.length;

    //  rowStart, rowEnd, colStart, colEnd
    const buildQuadNode = (rs, re, cs, ce) => {
        const len = re - rs;
        if (len === 1) {
            //  leaf node
            return new Node(grid[rs][cs], 1);
        }
        let mid = Math.floor(len / 2);

        //  find children
        let topLeft = buildQuadNode(rs, rs + mid, cs, cs + mid);
        let topRight = buildQuadNode(rs, rs + mid, cs + mid, ce);
        let bottomLeft = buildQuadNode(rs + mid, re, cs, cs + mid);
        let bottomRight = buildQuadNode(rs + mid, re, cs + mid, ce);

        const topLeftVal = topLeft.val
        //  all children are leafs
        if (topLeft.isLeaf && topRight.isLeaf && bottomLeft.isLeaf && bottomRight.isLeaf) {
            //  all children have same values
            if (topLeftVal === topRight.val && topLeftVal === bottomLeft.val && topLeftVal === bottomRight.val) {
                //  leaf node
                return new Node(topLeftVal, 1);
            }
        }

        //  not a leaf node
        return new Node(1, 0, topLeft, topRight, bottomLeft, bottomRight);
    }
    const head = buildQuadNode(0, n, 0, n);
    return head;
};

/* BST to sorted DLL 
*/
const bstToDoublyLinkedList = (root) => {

    /* pseudo code
        
    */

    if (root === null) {
        return null;
    }

    const convert = (currNode, prevNode) => {
        if (currNode === null) {
            return prevNode;
        }

        prevNode = convert(currNode.left, prevNode);

        const currentNode = new DoublyLinkedListNode(currNode.val);
        currentNode.prev = prevNode;
        if (prevNode) {
            prevNode.next = currentNode;
        }

        return convert(currNode.right, currentNode);
    };

    const head = convert(root, null);

    while (head.prev !== null) {
        head = head.prev;
    }

    return head;
};

/* Maximum Product of Splitted Binary Tree - Given the root of a binary tree, split the binary tree into two subtrees by removing one edge such that the product of the sums of the subtrees is maximized.

Return the maximum product of the sums of the two subtrees. Since the answer may be too large, return it modulo 109 + 7.

Note that you need to maximize the answer before taking the mod and not after taking it.
 */
/**
 * @param {TreeNode} root
 * @return {number}
 */
const maxProduct = (root) => {
    // Initialize an array to store the sums of nodes in different subtrees.
    let res = [];

    // Define a constant for modulo operation.
    const MOD = 10 ** 9 + 7;

    // Define a recursive function to perform depth-first search (DFS) on the tree.
    const dfs = (node) => {
        if (!node) {
            return 0; // Return 0 for null nodes.
        }

        if (!node.left && !node.right) {
            return node.val; // Return the value for leaf nodes.
        }

        // Recursively calculate the sums of left and right subtrees.
        const leftSum = dfs(node.left);
        const rightSum = dfs(node.right);

        // Add the sums to the result array.
        res.push(leftSum, rightSum);

        // Return the sum of the current subtree.
        return leftSum + node.val + rightSum;
    };

    // Calculate the total sum of the tree using DFS.
    const totalSum = dfs(root);

    // Initialize a variable to store the maximum product.
    let max = 0;

    // Iterate through the sums in the result array and calculate the product.
    for (const sum of res) {
        const curr = sum * (totalSum - sum);
        max = Math.max(max, curr); // Update the maximum product.
    }

    // Return the maximum product modulo MOD.
    return max % MOD;
};

/* Unique BSTs - Given an integer n, return all the structurally unique BST's (binary search trees), which has exactly n nodes of unique values from 1 to n. Return the answer in any order.
 */
const generateTrees = (end, start = 1) => {
    if (end < start) {
        return [null];
    }
    if (end === start) {
        return [new TreeNode(end)];
    }
    const trees = [];
    for (let i = start; i <= end; i++) {
        const leftTrees = generateTrees(i - 1, start);
        const rightTrees = generateTrees(end, i + 1);
        for (const left of leftTrees) {
            for (const right of rightTrees) {
                trees.push(new TreeNode(i, left, right));
            }
        }
    }
    return trees;
};

/* Find lowest common ancestor in 'n' ary tree */
const findLCA = (root, node1, node2) => {

    /* pseudo code
        
    */

    if (!root) {
        return null;
    }

    if (root === node1 || root === node2) {
        return root;
    }

    let commonAncestor = null;

    for (const child of root.children) {
        const childLCA = findLCA(child, node1, node2);

        if (childLCA) {
            if (!commonAncestor) {
                commonAncestor = childLCA;
            } else {
                return root;
            }
        }
    }

    return commonAncestor;
}

/* Recover Binary Search Tree - You are given the root of a binary search tree (BST), where the values of exactly two nodes of the tree were swapped by mistake. Recover the tree without changing its structure. */

/* Binary Search Tree Iterator - Implement the BSTIterator class that represents an iterator over the in-order traversal of a binary search tree (BST):

- BSTIterator(TreeNode root) Initializes an object of the BSTIterator class. The root of the BST is given as part of the constructor. The pointer should be initialized to a non-existent number smaller than any element in the BST.
- boolean hasNext() Returns true if there exists a number in the traversal to the right of the pointer, otherwise returns false.
- int next() Moves the pointer to the right, then returns the number at the pointer.

Notice that by initializing the pointer to a non-existent smallest number, the first call to next() will return the smallest element in the BST.

You may assume that next() calls will always be valid. That is, there will be at least a next number in the in-order traversal when next() is called. */

/* Path Sum III - Given the root of a binary tree and an integer targetSum, return the number of paths where the sum of the values along the path equals targetSum.

The path does not need to start or end at the root or a leaf, but it must go downwards (i.e., traveling only from parent nodes to child nodes). */

/* 1261. Find Elements in a Contaminated Binary Tree */

/* 701. Insert into a Binary Search Tree */

/* 979. Distribute Coins in Binary Tree */

/* 2196. Create Binary Tree From Descriptions */