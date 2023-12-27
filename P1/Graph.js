/* Smallest set of vertices to reach all nodes - Given a directed acyclic graph, with n vertices numbered from 0 to n-1, and an array edges where edges[i] = [fromi, toi] represents a directed edge from node fromi to node toi.

Find the smallest set of vertices from which all nodes in the graph are reachable. It's guaranteed that a unique solution exists.
 */
const findSmallestSetOfVertices = (n, edges) => {

  //  count indegrees of each node and find the node which has no indegree and hence the node we need to find

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

/* Parallel courses - Find the minimum number of semesters needed to take all courses given a set of prerequisites and a maximum number of courses you can take per semester.
 */
const minSemesters = (n, prerequisites, k) => {
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
  for (let ancestor = 0; ancestor < graph.length; ancestor++) {
    //  we can go to neighbours from this ancestor
    const neighbors = graph[ancestor];

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
          ancestors[currNode].push(ancestor);

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
Return true if you can finish all courses.Otherwise, return false.
 */
/**
 * @param {number} numCourses
 * @param {number[][]} prerequisites
 * @return {number[]}
 */
// Function to find the order of courses
const findOrder = (numCourses, prerequisites) => {

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
    const degree = inDegrees[i];
    if (degree === 0) {
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
        //  course v got unlocked now
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