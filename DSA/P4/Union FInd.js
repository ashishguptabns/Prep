/* Number of Unique Categories - You are given two inputs:

n: The number of elements (1 <= n <= 10^5).
categoryHandler: An array of length n where each element represents the category of the corresponding element (1 <= categoryHandler[i] <= n).
Additionally, you have access to a hidden function called haveSameCategory(a, b) that returns True if elements a and b belong to the same category, and False otherwise.

Write a function that returns the number of unique categories in the array categoryHandler.

Input: n = 6, categoryHandler = [1,1,2,2,3,3]
Output: 3
Explanation: There are 3 unique categories in this example. 1, 2, and 3. */

const countUniqueCategoriesWithUnionFind = (data) => {

  /* pseudo code
    keep a map to track a category and its first occurence
    move through data
      new category
        record in the map
        track category count
      repeat category
        find the root index for curr index
        find the root index for curr category
        apply a union
  */

  const categoryIndexMap = new Map();

  let count = 0;

  const unionFind = new UnionFind(data.length);

  data.forEach((item, index) => {
    if (item.category) {
      if (!categoryIndexMap.has(item.category)) {
        categoryIndexMap.set(item.category, index);
        count++;
      } else {
        const root1 = unionFind.find(index);
        const root2 = unionFind.find(categoryIndexMap.get(item.category));

        if (root1 !== root2) {
          unionFind.union(index, categoryIndexMap.get(item.category));
        }
      }
    }
  });

  return count;
}

//  disjoint set union - efficiently tracks relationships among elements and identifies connected components within a set
class UnionFind {

  /* pseudo code
    keep a parent array where each index is its own parent in the staring
    find the root of set to which x belongs
      keep moving up the parent array till ith item is its own parent and assign the same to parent of x
      return parent of x
    union to unite two indices
      find root of x
      find root of y
      both are not equal
        make rootY as parent of rootX
  */

  constructor(size) {
    this.parent = new Array(size);
    for (let i = 0; i < size; i++) {
      this.parent[i] = i;
    }
  }

  find(x) {
    if (this.parent[x] !== x) {
      this.parent[x] = this.find(this.parent[x]);
    }
    return this.parent[x];
  }

  union(x, y) {
    const rootX = this.find(x);
    const rootY = this.find(y);
    if (rootX !== rootY) {
      this.parent[rootX] = rootY;
    }
  }
}

