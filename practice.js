const findLCA = (root, node1, node2) => {
    if (!root) {
        return null
    }
    if (root === node1 || root === node2) {
        return root
    }
    let ancestor
    for (const child of root.children) {
        const childLCA = findLCA(child, node1, node2)

        if (childLCA) {
            if (!ancestor) {
                ancestor = childLCA
            } else {
                return root
            }
        }
    }

    return ancestor
}