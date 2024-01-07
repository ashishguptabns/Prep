/* Smallest set of vertices to reach all nodes - Given a directed acyclic graph, with n vertices numbered from 0 to n-1, and an array edges where edges[i] = [fromi, toi] represents a directed edge from node fromi to node toi.

Find the smallest set of vertices from which all nodes in the graph are reachable. It's guaranteed that a unique solution exists.
 */
const findSmallestSetOfVertices = (n, edges) => {

  /* pseudo code
    count indegrees of each node and find the node which has no indegree and hence the node we need to find 
  */

  //  nodes are numbered 0 to n - 1
  //  by default 0 edges are incoming
  const indegree = Array(n).fill(0)

  //  track how many edges are incoming in each node
  for (const [from, to] of edges) {
    //  maintain count of incoming edges to this node
    indegree[to]++
  }

  const res = []

  for (const node in indegree) {
    //  found a node which is unreachable from other nodes
    if (indegree[node] === 0) {
      res.push(node)
    }
  }

  return res
};

/* Surrounded regions -Given an m x n matrix board containing 'X' and 'O', capture all regions that are 4-directionally surrounded by 'X'.

A region is captured by flipping all 'O's into 'X's in that surrounded region.
*/
/**
 * @param {character[][]} board
 * @return {void} Do not return anything, modify board in-place instead.
 */
const solve = (board) => {

  /* pseudo code
    go to each border cell
      mark cell as visited which is O
      repeat for neighbour cells which are not on border
    
    go to each cell
      if it is marked visited
        restore to O
      else mark X
  */

  const dfs = (i, j) => {
    if (i < 0 || i >= board.length || j < 0 || j >= board[i].length
      || board[i][j] === 'V' || board[i][j] === 'X') {
      return
    }

    board[i][j] = 'V';
    dfs(i + 1, j);
    dfs(i - 1, j);
    dfs(i, j + 1);
    dfs(i, j - 1);
  }

  for (let r = 0; r < board.length; r++) {
    for (let c = 0; c < board[0].length; c++) {
      if (board[r][c] === 'O' && (r === 0 || c === 0 || r === board.length - 1
        || c === board[0].length - 1)) {
        dfs(r, c);
      }
    }
  }

  for (let r = 0; r < board.length; r++) {
    for (let c = 0; c < board[0].length; c++) {
      if (board[r][c] === 'V') {
        board[r][c] = 'O';
      } else {
        board[r][c] = 'X';
      }
    }
  }
};

/* All Paths From Source to Target - Given a directed acyclic graph(DAG) of n nodes labeled from 0 to n - 1, find all possible paths from node 0 to node n - 1 and return them in any order.

The graph is given as follows: graph[i] is a list of all nodes you can visit from node i(i.e., there is a directed edge from node i to node graph[i][j]).
 */
const allPathsSourceTarget = (graph) => {
  /* pseudo code
    we will solve using dfs
    srcIndex = 0, targetIndex = graphsize - 1 
    start DFS from 0
      keep tracking the nodes in curr path
      visit each neighbour of the curr node and do a DFS
      maintain curr node in visited map
  */

  const src = 0
  const target = graph.length - 1

  const allPaths = []

  const dfs = (startNode, path, visitedMap) => {
    path.push(startNode)
    if (startNode === target) {
      //  reached the end
      allPaths.push([...path])
      return
    }
    //  explore all the next nodes for current node
    for (let neighbour of graph[startNode]) {
      if (!visitedMap[neighbour]) {
        visitedMap[neighbour] = true
        //  call dfs with neighbour as a new start node
        dfs(neighbour, path, visitedMap)
        //  unmark for the other path
        visitedMap[neighbour] = false
        //  remove this neighbour also
        path.pop()
      }
    }
  }

  dfs(src, [], {})
  return allPaths
};

/* Possible Bipartition - We want to split a group of n people (labeled from 1 to n) into two groups of any size. Each person may dislike some other people, and they should not go into the same group.

Given the integer n and the array dislikes where dislikes[i] = [ai, bi] indicates that the person labeled ai does not like the person labeled bi, return true if it is possible to split everyone into two groups in this way.
 */
/**
 * @param {number} n
 * @param {number[][]} dislikes
 * @return {boolean}
 */
const possibleBipartition = (n, dislikes) => {

  /* pseudo code 
    create a graph from dislikes array
      a -> b and b -> a
    move through each node till n
      maintain color
      start a BFS 
        go through neighbours of current node 
          color should not match
          give a color
          push in the queue for next travel
  */

  //  Create the adjacency list graph
  //  notice label is from 1 to n
  const adjList = Array(n + 1).fill(null).map(() => []);
  for (const [a, b] of dislikes) {
    // Add edges to the adjacency list
    adjList[a].push(b);
    adjList[b].push(a);
  }

  // Initialize an array to store the color of each node
  const color = new Array(n + 1).fill(0);
  // 0: not colored, 1: color 1, -1: color 2

  for (let node = 1; node <= n; node++) {
    // Iterate through the nodes

    if (color[node] !== 0) {
      // Found a colored node; skip it as it is already processed
      continue;
    }

    // We will do BFS from this node
    const queue = [node];
    color[node] = 1;

    while (queue.length) {
      const currNode = queue.shift();

      // Check the neighbors and color them
      for (const neighbor of adjList[currNode]) {
        if (color[neighbor] === color[currNode]) {
          // Condition is broken - hateful neighbors
          return false;
        }
        if (color[neighbor] === 0) {
          // Color the neighbor with the opposite color
          color[neighbor] = -color[currNode];
          // Add in the queue for BFS
          queue.push(neighbor);
        }
      }
    }
  }
  // All nodes are colored without conflicts, so the graph can be bipartitioned
  return true;
};

/* Parallel courses - Find the minimum number of semesters needed to take all courses given a set of prerequisites and a maximum number of courses you can take per semester.
 */
const minSemesters = (n, prerequisites, k) => {

  /* pseudo code
    build a graph
      prereq -> array of courses
      maintain indegree of each course - how many prereqs are needed
    start BFS with courses which have 0 prereqs
      decide number of courses to process
      go through kids and reduce the indegree cause this course has been taken
      if a kid has 0 indegree 
        add to the queue as it is ready to be taken
      keep tracking num sems
    check if there are still some courses with indegree more than 0
      can't take all courses
  */

  const graph = new Map();
  const indegree = Array(n + 1).fill(0);

  for (const [course, prereq] of prerequisites) {
    if (!graph.has(prereq)) {
      //  maintain the courses which will be unlocked with this prereq
      graph.set(prereq, []);
    }
    graph.get(prereq).push(course);

    //  indegree tells you how many prereqs are needed for this course
    indegree[course]++;
  }

  //  start the BFS from courses which have zero prereqs or are unlocked
  //  queue will have only unlocked courses
  const queue = [];
  for (let i = 1; i <= n; i++) {
    if (indegree[i] === 0) {
      queue.push(i);
    }
  }

  let semesters = 0;
  while (queue.length > 0) {
    //  max number of courses we can take in one semester
    const size = Math.min(queue.length, k);
    for (let i = 0; i < size; i++) {
      const course = queue.shift();
      if (graph.has(course)) {
        //  find all the courses which will be unlocked having done this course
        for (const nextCourse of graph.get(course)) {
          //  decrease the indegree cause one prereq is done in this semester
          indegree[nextCourse]--;

          if (indegree[nextCourse] === 0) {
            //  this course is unlocked now
            queue.push(nextCourse);
          }
        }
      }
    }
    semesters++;
  }

  for (let i = 1; i <= n; i++) {
    if (indegree[i] > 0) {
      // If there are still courses with prerequisites, it's impossible to complete all courses
      return -1;
    }
  }

  return semesters;
}

/* All ancestors of a node in directed acyclic graph - You are given a positive integer n representing the number of nodes of a Directed Acyclic Graph (DAG). The nodes are numbered from 0 to n - 1 (inclusive).

You are also given a 2D integer array edges, where edges[i] = [fromi, toi] denotes that there is a unidirectional edge from fromi to toi in the graph.

Return a list answer, where answer[i] is the list of ancestors of the ith node, sorted in ascending order.

A node u is an ancestor of another node v if u can reach v via a set of edges.
 */
/**
 * @param {number} n
 * @param {number[][]} edges
 * @return {number[][]}
 */
const getAncestors = (n, edges) => {

  /* pseudo code
    build a graph
    go through all nodes till n
      do a BFS on neighbours of current node
        keep pushing current node as an ancestor of the node being visited in BFS
  */

  // Build a graph
  const graph = Array(n);
  for (let i = 0; i < graph.length; i++) {
    graph[i] = [];
  }
  for (const [from, to] of edges) {
    graph[from].push(to);
  }

  // Initialize output array
  const ancestors = Array(n);
  for (let i = 0; i < ancestors.length; i++) {
    ancestors[i] = [];
  }

  // Process all ancestors from 0 to n. This will ensure sorted order in the output.
  for (let originNode = 0; originNode < n; originNode++) {
    //  we can go to neighbours from this ancestor
    const neighbors = graph[originNode];

    // Use BFS to traverse the entire path from any ancestor
    let queue = neighbors;

    // A given node can be reached from an ancestor by different paths
    // We only want to record the ancestor once, so the first time you reach a node
    // mark that node as seen
    const seen = new Set();
    while (queue.length) {
      const nextQueue = [];

      for (let i = 0; i < queue.length; i++) {
        const currNode = queue[i];

        // Only record the ancestor if we haven't seen this node yet
        if (!seen.has(currNode)) {
          seen.add(currNode);
          ancestors[currNode].push(originNode);

          // The first time we reach the node, we populate the queue with the neighbors of current node
          // to continue the BFS
          for (const nextNode of graph[currNode]) {
            nextQueue.push(nextNode);
          }
        }
      }

      queue = nextQueue;
    }
  }

  return ancestors;
};

/* Course Schedule - There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1. You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must take course bi first if you want to take course ai.

For example, the pair[0, 1], indicates that to take course 0 you have to first take course 1.
return order of courses
 */
/**
 * @param {number} numCourses
 * @param {number[][]} prerequisites
 * @return {number[]}
 */
// Function to find the order of courses
const findOrder = (numCourses, prerequisites) => {

  /* pseudo code
    maintain indegree of each course
    start a BFS with courses with 0 indegree
      go through prereqs
        keep reducing the indegrees of courses for which this prereq was needed
        when a course has 0 indegree
          add to the queue
    check if all the courses have been taken
  */

  // Array to store the in-degrees of each course
  const inDegrees = Array(numCourses).fill(0);

  // Count the in-degrees for each course based on prerequisites
  for (const [to, from] of prerequisites) {
    inDegrees[to]++;
  }

  // Queue to store courses with in-degree 0 (can be taken first)
  const q = [];

  // Add courses with in-degree 0 to the queue
  for (let i = 0; i < inDegrees.length; i++) {
    if (inDegrees[i] === 0) {
      q.push(i);
    }
  }

  // Array to store the final result (order of courses)
  const res = [];

  // Process courses in topological order
  while (q.length) {
    // Take a course with in-degree 0 from the queue
    const currCourse = q.shift();

    // Add the course to the result
    res.push(currCourse);

    // Decrement the total number of courses remaining
    numCourses--;

    // Update in-degrees for courses dependent on the current course
    for (const [v, u] of prerequisites) {
      if (u === currCourse) {
        //  course v got one prereq unlock
        inDegrees[v]--;

        // If the in-degree becomes 0, add the course to the queue as we can take this course now
        if (inDegrees[v] === 0) {
          q.push(v);
        }
      }
    }
  }

  // If all courses can be taken, return the result; otherwise, return an empty array
  return numCourses === 0 ? res : [];
};

/* Find eventual safe states - There is a directed graph of n nodes with each node labeled from 0 to n - 1. The graph is represented by a 0 - indexed 2D integer array graph where graph[i] is an integer array of nodes adjacent to node i, meaning there is an edge from node i to each node in graph[i].

A node is a terminal node if there are no outgoing edges.A node is a safe node if every possible path starting from that node leads to a terminal node(or another safe node).

Return an array containing all the safe nodes of the graph.The answer should be sorted in ascending order.
 */
/**
 * @param {number[][]} graph
 * @return {number[]}
 */
const eventualSafeNodes = (graph) => {

  /* pseudo code
    go through each node in the graph
      run a DFS to check if it is a safe node
        keep a map to maintain safe/unsafe boolean for each node
        run a DFS for each neighbour
        set true in map if no unsafe nodes are found
  */

  const ans = [];
  const map = new Map();
  const dfs = (graph, node, map) => {
    if (map.has(node)) {
      return map.get(node);
    }
    //  this will help find circular or unsafe nodes
    map.set(node, false);

    //  check all the neighbors
    for (let nei of graph[node]) {
      if (!dfs(graph, nei, map)) {
        //  this neighbor is not safe
        return false;
      }
    }

    //  all the neighbors are safe
    map.set(node, true);
    return true;
  }
  for (let i = 0; i < graph.length; i++) {
    if (dfs(graph, i, map)) {
      ans.push(i);
    }
  }
  return ans;
};

/* Find Closest Node to Given Two Nodes - You are given a directed graph of n nodes numbered from 0 to n - 1, where each node has at most one outgoing edge.

The graph is represented with a given 0 - indexed array edges of size n, indicating that there is a directed edge from node i to node edges[i].If there is no outgoing edge from i, then edges[i] == -1.

You are also given two integers node1 and node2.

Return the index of the node that can be reached from both node1 and node2, such that the maximum between the distance from node1 to that node, and from node2 to that node is minimized.If there are multiple answers, return the node with the smallest index, and if no possible answer exists, return -1.

Note that edges may contain cycles.
 */
/**
 * @param {number[]} edges
 * @param {number} node1
 * @param {number} node2
 * @return {number}
 */
const closestMeetingNode = (edges, node1, node2) => {

  /* pseudo code
    keep two maps and store distances of each node from node1 and node2
      notice circular edges
    go through the edges
      for each node find the max distance between node1 and node2
      if distance is less than max so far
        update the distance and the node
  */

  // Initialize two maps to store the distances from node1 and node2 to each node in the graph.
  const map1 = {};
  const map2 = {};

  // Initialize a counter to keep track of the distance from the starting node (node1 or node2).
  let count = 0;

  // Populate map1 with distances from node1 to each node in the graph.
  while (map1[node1] == undefined && node1 != -1) {
    map1[node1] = count;
    count++;
    node1 = edges[node1];
  }

  // Reset the counter for map2.
  count = 0;

  // Populate map2 with distances from node2 to each node in the graph.
  while (map2[node2] == undefined && node2 != -1) {
    map2[node2] = count;
    count++;
    node2 = edges[node2];
  }

  // Initialize variables to find the maximum of the minimum distances between node1 and node2.
  let max = Infinity;
  let res = -1;

  // Iterate through each node in the graph.
  for (let i = 0; i < edges.length; i++) {
    // If either map1 or map2 doesn't have information about the distance to the current node, skip to the next iteration.
    if (map1[i] == undefined || map2[i] == undefined) {
      continue;
    }

    // Calculate the maximum of the distances from node1 and node2 to the current node.
    let localMax = Math.max(map1[i], map2[i]);

    // Update the result if the current node provides a smaller maximum distance.
    if (localMax < max) {
      max = localMax;
      res = i;
    }
  }

  // Return the node that minimizes the maximum distance from both node1 and node2.
  return res;
};

/* Alien Dictionary - There is a new alien language that uses the English alphabet. However, the order among the letters is unknown to you.

You are given a list of strings words from the alien language's dictionary, where the strings in words are sorted lexicographically by the rules of this new language.

Return a string of the unique letters in the new alien language sorted in lexicographically increasing order by the new language's rules. If there is no solution, return "". If there are multiple solutions, return any of them.
 */
const alienOrder = (words) => {

  /* pseudo code
    we will need a graph of chars and indegree of each char to find the order
    go through each word
      consider curr word and next word
      move j through smaller word
        if chars at j are different
          build an edge from curr word char to next word char
          increase the indegree of next word char
          break the loop for next word
    start a BFS with chars having 0 indegree
      visit neighbours of curr char and decrease indegrees
      move the char with 0 indegree to the queue
  */

  // Create a map to represent the graph of characters and their relationships
  const charGraph = new Map();
  // Create a map to store the indegree (number of incoming edges) for each character
  const inDegree = new Map();

  // Initialize the graph nodes and indegree for each node
  for (const word of words) {
    for (const char of word) {
      inDegree.set(char, 0);
      charGraph.set(char, new Set());
    }
  }

  // Iterate through each pair of adjacent words
  for (let i = 0; i < words.length - 1; i++) {
    const currWord = words[i];
    const nextWord = words[i + 1];

    // Check lexicographical order
    if (currWord.startsWith(nextWord) && currWord.length > nextWord.length) {
      // If the current word starts with the next word and is longer, it's not valid
      return "";
    }

    // Traverse through each character in the smaller word
    for (let j = 0; j < Math.min(currWord.length, nextWord.length); j++) {
      const currWordChar = currWord[j];
      const nextWordChar = nextWord[j];

      // If characters are different, create an edge in the graph
      if (currWordChar !== nextWordChar) {
        if (!charGraph.get(currWordChar).has(nextWordChar)) {
          // Build the edge
          charGraph.get(currWordChar).add(nextWordChar);
          // Increase the indegree of the next node
          inDegree.set(nextWordChar, (inDegree.get(nextWordChar) || 0) + 1);
        }
        // Take only one new edge per word
        break;
      }
    }
  }

  // Create a queue for Breadth-First Search (BFS)
  const bfsQueue = [];
  // Array to store the result (topological order)
  const result = [];
  // Set to keep track of visited nodes
  const seen = new Set();

  //  Initialize the BFS queue with nodes having an indegree of 0
  //  potential starting chars of the dictionary
  for (const [char, degree] of inDegree) {
    if (degree === 0) {
      bfsQueue.push(char);
    }
  }

  // Perform BFS
  while (bfsQueue.length > 0) {
    // Dequeue a character from the queue
    const currChar = bfsQueue.shift();
    // Append the character to the result
    result.push(currChar);
    // Mark the character as visited
    seen.add(currChar);

    // Update indegrees of adjacent characters and enqueue those with indegree 0
    for (const nextChar of charGraph.get(currChar)) {
      inDegree.set(nextChar, inDegree.get(nextChar) - 1);
      if (inDegree.get(nextChar) === 0) {
        //  unlocked a new char
        bfsQueue.push(nextChar);
      }
    }
  }

  // Check if all nodes have been visited (no cycles) and return the result as a string
  return seen.size === charGraph.size ? result.join('') : '';
}

/* Minimum Time to Collect All Apples in a Tree - Given an undirected tree consisting of n vertices numbered from 0 to n-1, which has some apples in their vertices. You spend 1 second to walk over one edge of the tree. Return the minimum time in seconds you have to spend to collect all apples in the tree, starting at vertex 0 and coming back to this vertex.

The edges of the undirected tree are given in the array edges, where edges[i] = [ai, bi] means that exists an edge connecting the vertices ai and bi. Additionally, there is a boolean array hasApple, where hasApple[i] = true means that vertex i has an apple; otherwise, it does not have any apple.
 */
/**
 * @param {number} n
 * @param {number[][]} edges
 * @param {boolean[]} hasApple
 * @return {number}
 */
const minTime = (n, edges, hasApple) => {

  /* pseudo code
    build a graph from the edges
      a -> b, b -> a
    start a DFS from the root with parent as -1
      run a DFS for all neighbours except parent
      keep adding the path lengths found from DFS
  */

  const adjlist = Array.from({ length: n }, () => new Array());
  for (const [from, to] of edges) {
    //  undirected edges
    adjlist[from].push(to);
    adjlist[to].push(from);
  }

  const dfs = (node, parent) => {
    let pathlen = 0;
    for (const neighbour of adjlist[node]) {
      if (neighbour == parent) {
        //  skip the parent cause counting is already done
        continue;
      }
      pathlen += dfs(neighbour, node);
    }
    if (node == 0) {
      return pathlen;
    }
    return pathlen > 0 || hasApple[node] ? pathlen + 2 : 0;
  }

  //  start with root
  return dfs(0, -1);
};

/* Dijkstra's algorithm for finding the shortest paths from a node to other nodes in a graph 

Function signature: dijkstra(graph, start)
Input: graph - a graph object with nodes, neighbors, and weights
       start - the starting node for the algorithm
*/

const dijkstra = (graph, startNode) => {

  /* pseudo code
    keep distance and visited array
      distance tracks ith item's distance from start node
    move through nodes
      find a node u at the min distance from unvisited nodes
        go through each unvisited node and compare the distance
      move v through adjacent nodes of u
        update the min distance till node v
  */

  // Number of vertices in the graph
  const numNodes = graph.length;

  // Array to store the shortest distance from startNode to each node
  const distance = Array(numNodes).fill(Infinity);

  // Array to track visited nodes
  const visited = Array(numNodes).fill(false);

  // Distance from startNode to itself is 0
  distance[startNode] = 0;

  // Helper function to find the vertex with the minimum distance value
  const findMinDistNode = (distance, visited) => {
    let min = Infinity;
    let minNode = -1;

    for (let v = 0; v < distance.length; v++) {
      // If the vertex v is not yet processed (not visited) and
      // the distance[v] is less than the current min value
      if (!visited[v] && distance[v] <= min) {
        // Update min value and minIndex
        min = distance[v];
        minNode = v;
      }
    }

    // Return the index of the vertex with the minimum distance
    return minNode;
  };

  // Loop through all nodes
  for (let count = 0; count < numNodes - 1; count++) {
    // Find the node with the minimum distance among the
    // nodes not yet processed (not visited)
    const u = findMinDistNode(distance, visited);

    // Mark the selected node as visited
    visited[u] = true;

    // Update distance value of the adjacent vertices of the
    // selected node
    for (let v = 0; v < numNodes; v++) {
      // Update distance[v] only if it's not visited, there is
      // an edge from u to v, and the total weight of path from
      // startNode to v through u is less than the current value
      // of distance[v]
      if (
        !visited[v] &&
        graph[u][v] !== 0 &&
        distance[u] !== Number.MAX_VALUE &&
        distance[u] + graph[u][v] < distance[v]
      ) {
        distance[v] = distance[u] + graph[u][v];
      }
    }
  }

  // Return the array of shortest distances
  return distance;
};

/* Number of Nodes in the Sub-Tree With the Same Label - You are given a tree (i.e. a connected, undirected graph that has no cycles) consisting of n nodes numbered from 0 to n - 1 and exactly n - 1 edges. The root of the tree is the node 0, and each node of the tree has a label which is a lower-case character given in the string labels (i.e. The node with the number i has the label labels[i]).

The edges array is given on the form edges[i] = [ai, bi], which means there is an edge between nodes ai and bi in the tree.

Return an array of size n where ans[i] is the number of nodes in the subtree of the ith node which have the same label as node i.
 */
/**
 * @param {number} n
 * @param {number[][]} edges
 * @param {string} labels
 * @return {number[]}
 */
const countSubTrees = (n, edges, labels) => {

  /* pseudo code
    build an undirected graph from the edges
    start DFS from the root
      do a DFS for each neighbour of curr node
    increment the count of curr char
    maintain count of same label in ans array
    update the count of each label in parent arr
  */

  // Create an adjacency list to represent the undirected graph
  const adjList = Array.from(Array(n), () => new Array())

  // Create the undirected graph
  for (const [from, to] of edges) {
    adjList[from].push(to)
    adjList[to].push(from)
  }

  // Initialize an array to store the result for each node
  const ans = Array(n).fill(0)

  // Initialize an array to count the occurrences of each character in the subtree
  const count = Array(26).fill(0)

  // Depth-first search (DFS) function to traverse the tree
  const dfs = (node, parent, pcount) => {
    // Initialize an array to count the occurrences of each character in the current subtree
    const count = Array(26).fill(0)

    // Traverse each neighbor of the current node
    for (const neighbour of adjList[node]) {
      // Skip the parent node to avoid revisiting it
      if (neighbour === parent) {
        continue
      }

      // Recursively call DFS for the current neighbor
      dfs(neighbour, node, count)
    }

    // Increment the count for the character at the current node
    count[labels.charCodeAt(node) - 97]++
    ans[node] = count[labels.charCodeAt(node) - 97]

    // Update the parent count array with the counts from the current subtree
    for (let i = 0; i < 26; i++) {
      pcount[i] += count[i]
    }
  }

  // Start DFS from the root node (node 0) with a dummy parent (-1) and initial count array
  dfs(0, -1, count)

  // Return the result array
  return ans
};
