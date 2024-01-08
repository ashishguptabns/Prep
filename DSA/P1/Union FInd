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
        find the root of curr index
        find the root of its category
        apply a union
  */

  //  this map associates each category with the index of first occurrence of this category
  const categoryIndexMap = new Map();

  // number of unique categories
  let count = 0;

  const unionFind = new UnionFind(data.length);

  data.forEach((item, index) => {
    if (item.category) {
      if (!categoryIndexMap.has(item.category)) {
        //  found a new category
        //  record the first occurence
        categoryIndexMap.set(item.category, index);
        count++;
      } else {
        // group with the same category
        // perform the union operation between roots of current index and first occurrence index of this category
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
    //  store the parent of each element
    this.parent = new Array(size);
    for (let i = 0; i < size; i++) {
      //  each element is its own parent - individual disjoint sets
      this.parent[i] = i;
    }
  }

  //  find the root of the set to which x element belongs
  find(x) {
    if (this.parent[x] !== x) {
      //  move up the ancestors and keep updating the parent
      this.parent[x] = this.find(this.parent[x]);
    }
    //  found the root of this set
    return this.parent[x];
  }

  //  unites the sets to which x and y belong by making the root of one set the parent of root of other set
  union(x, y) {
    const rootX = this.find(x);
    const rootY = this.find(y);
    if (rootX !== rootY) {
      this.parent[rootX] = rootY;
    }
  }
}

