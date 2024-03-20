class TrieNode {
  constructor() {
    this.children = new Map();
    this.isEndOfWord = false;
  }
}

class Trie {

  /* pseudo code
    insert a word
      start node from the root
      move through chars of word
        node doesn't have curr char as a child
          set a child against a new TrieNode
        move node to this child
      mark curr node as end of word

    search a prefix
      start node from the root
      move through chars of prefix
        node doesn't have curr char as a child
          return false
        move node to this child
      return if curr node is marked as end of word
  */

  constructor() {
    this.root = new TrieNode();
  }

  insert = (word) => {
    let node = this.root;
    for (const char of word) {
      if (!node.children.has(char)) {
        //  create a new node for the missing character
        node.children.set(char, new TrieNode());
      }
      node = node.children.get(char);
    }
    node.isEndOfWord = true;
  }

  search(prefix) {
    let node = this.root;
    for (const char of prefix) {
      if (!node.children.has(char)) {
        //  prefix is not present so the word doesn’t exist
        return false;
      }
      node = node.children.get(char);
    }
    //  reached the end of given prefix; check if it is a word
    return node.isEndOfWord;
  }
}

/* Search suggestions system - You are given an array of strings products and a string searchWord.

Design a system that suggests at most three product names from products after each character of searchWord is typed. Suggested products should have common prefix with searchWord. If there are more than three products with a common prefix return the three lexicographically minimums products.

Return a list of lists of the suggested products after each character of searchWord is typed.
 */
/**
 * @param {string[]} products
 * @param {string} searchWord
 * @return {string[][]}
 */
const suggestedProducts = (products, searchWord) => {

  /* pseudo code
    sort the products
    keep a trie
    move p through products
      start node from trie root
      move through chars of p
        if node doesn't have this char as key
          put sugg array against this key
        if sugg array has less than 3 items
          push p into the array
        move node to curr char node

    move through chars of search word
      move node to node of curr char
      keep pushing the suggestions in the res array
  */

  products.sort();

  const trie = {};
  for (let p of products) {
    let node = trie;
    for (const char of p) {
      if (!node[char]) {
        node[char] = { 'sug': [] };
      }
      if (node[char]['sug'].length < 3) {
        node[char]['sug'].push(p);
      }
      node = node[char];
    }
  }

  let node = trie, res = [];
  for (let i = 0; i < searchWord.length; i++) {
    if (node) {
      node = node[searchWord[i]];
    }
    res.push(!node ? [] : node['sug']);
  }

  return res;
};

/* Implement Trie - prefix tree*/

const Trie = function () {
  this.root = {}
};

/** 
 * @param {string} word
 * @return {void}
 */
Trie.prototype.insert = function (word) {
  let node = this.root
  for (const c of word) {
    if (!node[c]) {
      //  register this node
      node[c] = {}
    }
    //  keep moving down
    node = node[c]
  }
  //  mark this node as the end of a word
  node.isWord = true
};

/** 
 * @param {string} word
 * @return {boolean}
 */
Trie.prototype.search = function (word) {
  let node = this.root
  for (const c of word) {
    if (!node[c]) {
      return false
    }
    node = node[c]
  }
  return node.isWord == true ? true : false
};

/** 
 * @param {string} prefix
 * @return {boolean}
 */
Trie.prototype.startsWith = function (prefix) {
  let node = this.root
  for (const c of prefix) {
    if (!node[c]) {
      return false
    }
    node = node[c]
  }

  return true
};

/**
 * Your Trie object will be instantiated and called as such:
 * var obj = new Trie()
 * obj.insert(word)
 * var param_2 = obj.search(word)
 * var param_3 = obj.startsWith(prefix)
 */

// todo
/* Longest Word With All Prefixes - Given an array of strings words, find the longest string in words such that every prefix of it is also in words.

  Input: words = ["a", "banana", "app", "appl", "ap", "apple"]
Output: "apple"
Explanation:
- "apple" is the longest word in words.
- Every prefix of "apple" is also in words: ("a", "app", "appl", "ap"). */

