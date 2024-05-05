/* 1557. Minimum Number of Vertices to Reach All Nodes - Given a directed acyclic graph, with n vertices numbered from 0 to n-1, and an array edges where edges[i] = [fromi, toi] represents a directed edge from node fromi to node toi.

Find the smallest set of vertices from which all nodes in the graph are reachable. It's guaranteed that a unique solution exists.
 */
const findSmallestSetOfVertices = (n, edges) => {

  /* pseudo code
    count indegrees of each node and find the node which has no indegree and hence the node we need to find 
  */

  const indegree = Array(n).fill(0)

  for (const [from, to] of edges) {
    indegree[to]++
  }

  const res = []

  for (const node in indegree) {
    if (indegree[node] === 0) {
      res.push(node)
    }
  }

  return res
};

/* All Paths From Source to Target - Given a directed acyclic graph(DAG) of n nodes labeled from 0 to n - 1, find all possible paths from node 0 to node n - 1 and return them in any order.

The graph is given as follows: graph[i] is a list of all nodes you can visit from node i(i.e., there is a directed edge from node i to node graph[i][j]).
 */
/**
 * @param {number[][]} graph
 * @return {number[][]}
 */
const allPathsSourceTarget = (graph) => {
  /* pseudo code
    we will solve using dfs
    srcIndex = 0, targetIndex = graphsize - 1 
    start DFS from source - 0
      keep tracking the nodes in curr path
      visit each neighbour of the curr node and do a DFS
  */

  const src = 0
  const target = graph.length - 1

  const allPaths = []

  const dfs = (startNode, currPath) => {
    if (startNode === target) {
      //  reached the end
      allPaths.push([...currPath])
      return
    }
    for (const neighbour of graph[startNode]) {
      dfs(neighbour, [...currPath, neighbour])
    }
  }

  dfs(src, [0])
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
    keep a color array for each node
    move through each node till n
      start a BFS if this node is not colored
        go through neighbours of current node 
          color should not match
          give opposite color
          push in the queue for next travel
  */

  const graph = {}
  for (const [from, to] of edges) {
    graph[from] = graph[from] || []
    graph[from].push(to)

    graph[to] = graph[to] || []
    graph[to].push(from)
  }

  const color = Array(n + 1).fill(0)

  for (let node = 1; node <= n; node++) {
    if (color[node] !== 0) {
      continue
    }
    color[node] = 1
    const q = [node]
    while (q.length) {
      const curr = q.shift()
      if (graph[curr]) {
        for (const next of graph[curr]) {
          if (color[next] === color[curr]) {
            return false
          }
          if (color[next] === 0) {
            color[next] = -color[curr]
            q.push(next)
          }
        }
      }
    }
  }

  return true
};

/* Parallel courses - Find the minimum number of semesters needed to take all courses given a set of prerequisites and a maximum number of courses you can take per semester.
 */
const minSemesters = (n, prerequisites, k) => {

  /* pseudo code
    build a graph
      prereq -> array of courses
      maintain indegree of each course - how many prereqs are needed
    start BFS with courses which have 0 prereqs
      decide number of courses to process in one sem
      go through next courses and reduce the indegree cause this course has been taken
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
      graph.set(prereq, []);
    }
    graph.get(prereq).push(course);

    indegree[course]++;
  }

  const queue = [];
  for (let i = 1; i <= n; i++) {
    if (indegree[i] === 0) {
      queue.push(i);
    }
  }

  let semesters = 0;
  while (queue.length > 0) {
    const size = Math.min(queue.length, k);
    for (let i = 0; i < size; i++) {
      const course = queue.shift();
      if (graph.has(course)) {
        for (const nextCourse of graph.get(course)) {
          indegree[nextCourse]--;

          if (indegree[nextCourse] === 0) {
            queue.push(nextCourse);
          }
        }
      }
    }
    semesters++;
  }

  for (const deg of indegree) {
    if (deg > 0) {
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

const getAncestors = (n, edges) => {

  /* pseudo code
    build a graph
    go through all nodes till n
      do a BFS on neighbours of current node
        keep pushing current node as an ancestor of the node being visited in BFS
  */

  const graph = {}
  for (const [from, to] of edges) {
    graph[from] = graph[from] || []
    graph[from].push(to)
  }

  const ans = Array(n).fill().map(() => [])

  for (let node = 0; node < n; node++) {
    const q = [node]
    const seen = {}
    while (q.length) {
      const curr = q.shift()
      if (!seen[curr]) {
        seen[curr] = true
        if (curr !== node && !ans[curr].includes(node)) {
          ans[curr].push(node)
        }
        if (graph[curr]) {
          for (const next of graph[curr]) {
            q.push(next)
          }
        }
      }
    }
  }

  return ans
};

/* Course Schedule II - There are a total of numCourses courses you have to take, labeled from 0 to numCourses - 1. You are given an array prerequisites where prerequisites[i] = [ai, bi] indicates that you must take course bi first if you want to take course ai.

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

  const inDegrees = Array(numCourses).fill(0);

  for (const [to, from] of prerequisites) {
    inDegrees[to]++;
  }

  const q = [];

  for (let i = 0; i < inDegrees.length; i++) {
    if (inDegrees[i] === 0) {
      q.push(i);
    }
  }

  const res = [];

  while (q.length) {
    const currCourse = q.shift();

    res.push(currCourse);

    numCourses--;

    for (const [v, u] of prerequisites) {
      if (u === currCourse) {
        inDegrees[v]--;

        if (inDegrees[v] === 0) {
          q.push(v);
        }
      }
    }
  }

  return numCourses === 0 ? res : [];
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

  const charGraph = new Map();
  const inDegree = new Map();

  for (const word of words) {
    for (const char of word) {
      inDegree.set(char, 0);
      charGraph.set(char, new Set());
    }
  }

  for (let i = 0; i < words.length - 1; i++) {
    const currWord = words[i];
    const nextWord = words[i + 1];

    if (currWord.startsWith(nextWord) && currWord.length > nextWord.length) {
      return "";
    }

    for (let j = 0; j < Math.min(currWord.length, nextWord.length); j++) {
      const currWordChar = currWord[j];
      const nextWordChar = nextWord[j];

      if (currWordChar !== nextWordChar) {
        if (!charGraph.get(currWordChar).has(nextWordChar)) {
          charGraph.get(currWordChar).add(nextWordChar);
          inDegree.set(nextWordChar, (inDegree.get(nextWordChar) || 0) + 1);
        }
        // Take only one new edge per word
        break;
      }
    }
  }

  const bfsQueue = [];
  const result = [];
  const seen = new Set();

  for (const [char, degree] of inDegree) {
    if (degree === 0) {
      bfsQueue.push(char);
    }
  }

  while (bfsQueue.length > 0) {
    const currChar = bfsQueue.shift();
    result.push(currChar);
    seen.add(currChar);

    for (const nextChar of charGraph.get(currChar)) {
      inDegree.set(nextChar, inDegree.get(nextChar) - 1);
      if (inDegree.get(nextChar) === 0) {
        bfsQueue.push(nextChar);
      }
    }
  }

  return seen.size === charGraph.size ? result.join('') : '';
}

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

  const ans = []

  const safe = {}
  const dfs = (node) => {
    if (safe[node] !== undefined) {
      return safe[node]
    }
    safe[node] = false
    for (const next of graph[node]) {
      if (!dfs(next)) {
        return false
      }
    }
    safe[node] = true

    return true
  }

  for (let node = 0; node < graph.length; node++) {
    if (dfs(node)) {
      ans.push(node)
    }
  }

  return ans
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

  const dist1 = {}
  const dist2 = {}

  let dist = 0
  while (dist1[node1] === undefined && node1 !== -1) {
    dist1[node1] = dist++
    node1 = edges[node1]
  }

  dist = 0
  while (dist2[node2] === undefined && node2 !== -1) {
    dist2[node2] = dist++
    node2 = edges[node2]
  }

  console.log(dist1, dist2)

  let [max, closestNode] = [Infinity, -1]
  for (let node = 0; node < edges.length; node++) {
    if (dist1[node] === undefined || dist2[node] === undefined) {
      continue
    }

    const localMax = Math.max(dist1[node], dist2[node])

    if (localMax < max) {
      max = localMax
      closestNode = node
    }
  }

  return closestNode
};

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

  const graph = {}
  for (const [a, b] of edges) {
    graph[a] = graph[a] || []
    graph[a].push(b)
    graph[b] = graph[b] || []
    graph[b].push(a)
  }

  const seen = {}
  const findTime = (node) => {
    if (!seen[node]) {
      seen[node] = true

      let time = 0

      if (graph[node]) {
        for (const next of graph[node]) {
          time += findTime(next)
        }
      }

      if (node === 0) {
        return time
      }

      return hasApple[node] || time > 0 ? time + 2 : time
    }
    return 0
  }
  return findTime(0)
};

/* Dijkstra's algorithm for finding the shortest paths from a node to other nodes in a graph 

Function signature: dijkstra(graph, start)
Input: graph - a graph object with nodes, neighbors, and weights
       start - the starting node for the algorithm
*/

const dijkstra = (graph, start) => {

  /* pseudo code
    keep distance and visited array
      distance tracks ith item's distance from start node
  */

  const distances = {};
  const visited = new Set();
  const nodes = Object.keys(graph);

  for (const node of nodes) {
    distances[node] = Infinity;
  }

  distances[start] = 0;

  while (nodes.length) {
    nodes.sort((a, b) => distances[a] - distances[b]);
    const closestNode = nodes.shift();

    if (distances[closestNode] === Infinity) {
      break;
    }

    visited.add(closestNode);

    for (const neighbor in graph[closestNode]) {
      if (!visited.has(neighbor)) {
        const newDistance = distances[closestNode] + graph[closestNode][neighbor];

        if (newDistance < distances[neighbor]) {
          distances[neighbor] = newDistance;
        }
      }
    }
  }

  return distances;
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
      a -> b, b -> a
    start DFS from the root
      do a DFS for each neighbour of curr node
    increment the count of curr char
    maintain count of same label in ans array
    update the count of each label in parent arr
  */

  const adjList = Array(n).fill().map(() => [])

  for (const [from, to] of edges) {
    adjList[from].push(to)
    adjList[to].push(from)
  }

  const ans = Array(n).fill(0)

  const countArr = Array(26).fill(0)

  const dfs = (node, parent, parentCountArr) => {
    const countArr = Array(26).fill(0)

    for (const neighbour of adjList[node]) {
      if (neighbour === parent) {
        continue
      }

      dfs(neighbour, node, countArr)
    }

    countArr[labels.charCodeAt(node) - 97]++
    ans[node] = countArr[labels.charCodeAt(node) - 97]

    for (let i = 0; i < 26; i++) {
      parentCountArr[i] += countArr[i]
    }
  }

  dfs(0, -1, countArr)

  return ans
};

/* Suppose you have a graph of Google's organizational structure where: Each node N corresponds to a person 

If N reports to M (i.e. M is N's boss), M is the parent of N 
  M -> N
If P reports to N (i.e. N is P's boss), P is the child of N 
  N -> P
At Google, someone's "employee score" is the total number of reports (including themselves). 
Write a function to compute anyone's employee score.
 */

function employeeScoreMemoized(graph, node, memo) {
  if (memo[node] !== undefined) {
    return memo[node];
  }

  if (!graph[node] || graph[node].length === 0) {
    return 1;
  }

  let score = 1;

  for (const child of graph[node]) {
    score += employeeScoreMemoized(graph, child, memo);
  }

  memo[node] = score;

  return score;
}

/* 841. Keys and Rooms */

var canVisitAllRooms = function (rooms) {
  const seen = {}
  const dfs = (room) => {
    if (!seen[room]) {
      seen[room] = true
      const nextRooms = rooms[room]
      for (const next of nextRooms) {
        dfs(next)
      }
    }
  }
  dfs(0)
  return Object.keys(seen).length === rooms.length
};

/* 547. Number of Provinces */

/* 1615. Maximal Network Rank */

/**
 * @param {number} n
 * @param {number[][]} roads
 * @return {number}
 */
var maximalNetworkRank = function (n, roads) {
  const graph = {}
  const deg = Array(n).fill(0)

  for (const [a, b] of roads) {
    graph[a] = graph[a] || []
    graph[a].push(b)

    graph[b] = graph[b] || []
    graph[b].push(a)

    deg[a]++
    deg[b]++
  }

  let ans = 0
  for (let node = 0; node < n; node++) {
    for (let next = node + 1; next < n; next++) {
      let count = deg[node] + deg[next]
      if (graph[node] && graph[node].includes(next)) {
        count--
      }

      ans = Math.max(ans, count)
    }
  }
  return ans
};

/* 2685. Count the Number of Complete Components */

var countCompleteComponents = function (n, edges) {
  const graph = {}
  for (const [a, b] of edges) {
    graph[a] = graph[a] || []
    graph[a].push(b)
    graph[b] = graph[b] || []
    graph[b].push(a)
  }

  const seen = {}

  let count = 0
  for (let node = 0; node < n; node++) {
    if (seen[node]) {
      continue
    }
    const q = [node]
    let nodesCount = 0
    let edgesCount = 0

    while (q.length) {
      const curr = q.shift()
      if (seen[curr]) {
        continue
      }
      seen[curr] = true
      nodesCount++
      if (graph[curr]) {
        for (const next of graph[curr]) {
          if (!seen[next]) {
            q.push(next)
            edgesCount++
          }
        }
      }
    }
    const idealEdges = nodesCount * (nodesCount - 1) / 2
    if (edgesCount === idealEdges) {
      count++
    }
  }

  return count
};

/* 2477. Minimum Fuel Cost to Report to the Capital */
var minimumFuelCost = function (roads, seats) {
  const graph = {}
  for (const [a, b] of roads) {
    graph[a] = graph[a] || []
    graph[a].push(b)

    graph[b] = graph[b] || []
    graph[b].push(a)
  }

  let ans = 0

  const dfs = (node, prev) => {
    let pplCount = 1
    if (graph[node]) {
      for (const next of graph[node]) {
        if (next !== prev) {
          pplCount += dfs(next, node)
        }
      }
    }
    if (node) {
      ans += Math.ceil(pplCount / seats)
    }
    return pplCount
  }
  dfs(0, -1)

  return ans
};

/* 1466. Reorder Routes to Make All Paths Lead to the City Zero */

var minReorder = function (n, edges) {
  const graph = {}
  for (const [from, to] of edges) {
    graph[from] = graph[from] || []
    graph[from].push({ city: to, dist: 1 })

    graph[to] = graph[to] || []
    graph[to].push({ city: from, dist: 0 })
  }

  const seen = {}
  const q = [0]
  let ans = 0

  while (q.length) {
    const node = q.shift()
    if (seen[node]) {
      continue
    }
    seen[node] = true

    for (const next of graph[node]) {
      if (seen[next.city]) {
        continue
      }
      ans += Number(next.dist)
      q.push(next.city)
    }
  }

  return ans
};

/* 684. Redundant Connection */

/**
 * @param {number[][]} edges
 * @return {number[]}
 */
var findRedundantConnection = function (edges) {
  const graph = {}
  for (const [a, b] of edges) {
    graph[a] = graph[a] || []
    graph[a].push(b)
    graph[b] = graph[b] || []
    graph[b].push(a)
  }

  for (let node = edges.length - 1; node >= 0; node--) {
    const [start, skip] = edges[node]
    const seen = {}
    seen[start] = true
    let count = 0;

    let queue = [start];
    while (queue.length) {
      const size = queue.length
      for (let i = 0; i < size; i++) {
        const node = queue.shift();
        count++;

        for (const next of graph[node]) {
          if (node === start && next === skip) {
            continue;
          }

          if (!seen[next]) {
            seen[next] = true
            queue.push(next);
          }
        }
      }
    }

    if (count === edges.length) {
      return edges[node];
    }
  }
};

/* 1319. Number of Operations to Make Network Connected */

var makeConnected = function (n, edges) {
  if (edges.length < n - 1) {
    return -1
  }

  const graph = {}
  for (const [a, b] of edges) {
    graph[a] = graph[a] || []
    graph[a].push(b)

    graph[b] = graph[b] || []
    graph[b].push(a)
  }

  const seen = {}
  const dfs = (node) => {
    if (!seen[node]) {
      seen[node] = true
      if (graph[node]) {
        for (const next of graph[node]) {
          dfs(next)
        }
      }
      return true
    }
    return false
  }
  let networks = 0
  for (let node = 0; node < n; node++) {
    if (dfs(node)) {
      networks++
    }
  }
  return networks - 1
};

/* 399. Evaluate Division - You are given an array of variable pairs equations and an array of real numbers values, where equations[i] = [Ai, Bi] and values[i] represent the equation Ai / Bi = values[i]. Each Ai or Bi is a string that represents a single variable.

You are also given some queries, where queries[j] = [Cj, Dj] represents the jth query where you must find the answer for Cj / Dj = ?.

Return the answers to all queries. If a single answer cannot be determined, return -1.0.

Note: The input is always valid. You may assume that evaluating the queries will not result in division by zero and that there is no contradiction.

Note: The variables that do not occur in the list of equations are undefined, so the answer cannot be determined for them.*/

var calcEquation = function (equations, values, queries) {
  const graph = {};

  for (let i = 0; i < equations.length; i++) {

    const [num, den] = equations[i];

    const value = values[i];

    graph[num] = graph[num] || {};
    graph[den] = graph[den] || {};

    graph[num][den] = value;
    graph[den][num] = 1 / value;
  }

  const evaluateQuery = (num, den, visited) => {
    if (!graph[num] || !graph[den]) {
      return -1.0;
    }
    if (num === den) {
      return 1.0;
    }
    visited.add(num);
    for (let neighbor in graph[num]) {
      if (!visited.has(neighbor)) {
        const result = evaluateQuery(neighbor, den, visited);
        if (result !== -1.0) {
          return graph[num][neighbor] * result;
        }
      }
    }
    return -1.0;
  };

  const results = [];

  for (const [num, den] of queries) {
    let visited = new Set();
    let result = evaluateQuery(num, den, visited);
    results.push(result);
  }

  return results;
};

/* 2285. Maximum Total Importance of Roads */

var maximumImportance = function (n, roads) {
  const deg = Array(n).fill(0)
  for (const [a, b] of roads) {
    deg[a]++
    deg[b]++
  }

  deg.sort((a, b) => a - b)

  let total = 0

  for (let node = 0; node < n; node++) {
    total += (node + 1) * deg[node]
  }

  return total
};

/* 851. Loud and Rich */

/**
 * @param {number[][]} richer
 * @param {number[]} quiet
 * @return {number[]}
 */
var loudAndRich = function (arr, quiet) {
  const graph = {}

  for (const [more, less] of arr) {
    graph[less] = graph[less] || []
    graph[less].push(more)
  }

  const memo = new Map();
  const getQuietest = (person) => {
    if (memo.has(person)) {
      return memo.get(person);
    }
    const richerList = graph[person]
    let min = quiet[person];
    let quietest = person;
    if (!richerList) {
      memo.set(person, quietest);
      return quietest;
    }
    for (const rich of richerList) {
      if (quiet[getQuietest(rich)] < min) {
        min = quiet[getQuietest(rich)];
        quietest = getQuietest(rich);
      }
    }
    memo.set(person, quietest);
    return quietest;
  }
  const answer = [];
  for (let i = 0; i < quiet.length; i++) {
    answer.push(getQuietest(i));
  }
  return answer;
};

/* 2368. Reachable Nodes With Restrictions */

/**
 * @param {number} n
 * @param {number[][]} edges
 * @param {number[]} restricted
 * @return {number}
 */
var reachableNodes = function (n, edges, restricted) {
  const graph = {};

  for (const [u, v] of edges) {
    graph[u] = graph[u] || []
    graph[v] = graph[v] || []

    graph[u].push(v)
    graph[v].push(u)
  }

  const restrictedSet = new Set(restricted);
  const visited = new Set();

  let ans = 0;

  function dfs(node) {
    if (restrictedSet.has(node) || visited.has(node)) {
      return;
    }

    ans++;
    visited.add(node);

    for (const next of graph[node]) {
      dfs(next);
    }
  }

  dfs(0);

  return ans;
};

/* 1976. Number of Ways to Arrive at Destination - You are in a city that consists of n intersections numbered from 0 to n - 1 with bi-directional roads between some intersections. The inputs are generated such that you can reach any intersection from any other intersection and that there is at most one road between any two intersections.

You are given an integer n and a 2D integer array roads where roads[i] = [ui, vi, timei] means that there is a road between intersections ui and vi that takes timei minutes to travel. You want to know in how many ways you can travel from intersection 0 to intersection n - 1 in the shortest amount of time.

Return the number of ways you can arrive at your destination in the shortest amount of time. Since the answer may be large, return it modulo 109 + 7.*/

var countPaths = function (n, roads) {
  const mod = 1e9 + 7;
  const distances = Array(n).fill(Infinity)
  const ways = Array(n).fill(0)
  const visited = new Set()
  const graph = Array.from({ length: n }, () => [])
  distances[0] = 0
  ways[0] = 1

  for (var [u, v, weight] of roads) {
    graph[u].push([v, weight])
    graph[v].push([u, weight])
  }

  // get the edge with minimum total weight (distance)
  var getMin = () => {
    var minDistance = Infinity
    var minNode = -1
    for (var node = 0; node < n; node++) {
      if (!visited.has(node) && distances[node] < minDistance) {
        minDistance = distances[node]
        minNode = node
      }
    }
    return [minDistance, minNode]
  }

  // dijkstra's algorithm
  for (var i = 1; i <= n; i++) {
    var [weight, node] = getMin()
    if (node === -1) { break }
    visited.add(node)
    for (var [nextNode, nextWeight] of graph[node]) {
      if (weight + nextWeight === distances[nextNode]) {
        ways[nextNode] += ways[node]
        ways[nextNode] %= mod
      } else if (weight + nextWeight < distances[nextNode]) {
        distances[nextNode] = weight + nextWeight
        ways[nextNode] = ways[node]
      }
    }
  }
  return ways[n - 1]
};

/* 2492. Minimum Score of a Path Between Two Cities */

var minScore = function (n, roads) {
  const graph = {}
  for (const [a, b, cost] of roads) {
    graph[a] = graph[a] || []
    graph[a].push([b, cost])
    graph[b] = graph[b] || []
    graph[b].push([a, cost])
  }

  const q = [1]
  const seen = {}
  seen[1] = true
  let ans = Infinity

  while (q.length) {
    const node = q.shift()
    if (graph[node]) {
      for (const [next, cost] of graph[node]) {
        ans = Math.min(cost, ans)
        if (!seen[next]) {
          seen[next] = true
          q.push(next)
        }
      }
    }
  }

  return ans
};

/* 785. Is Graph Bipartite? */

var isBipartite = function (graph) {
  const color = []
  for (let node = 0; node < graph.length; node++) {
    if (color[node] !== undefined) {
      continue
    }
    const q = [node]
    color[node] = 1
    while (q.length) {
      const node = q.shift()
      for (const next of graph[node]) {
        if (color[next] === undefined) {
          color[next] = -color[node]
          q.push(next)
        }
        else if (color[next] === color[node]) {
          return false
        }
      }
    }
  }

  for (const c of color) {
    if (c === undefined) {
      return false
    }
  }
  return true
};