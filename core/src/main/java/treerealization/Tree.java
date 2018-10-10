package treerealization;

/**
 * Created by Makoiedov.H on 8/28/2017.
 */

public class Tree {
    private Node root;

    public Tree() {root = null;}
    public Tree(Node root) {
        this.root = root;
    }

    /**
     * This method find node with appropriate name
     * using depth search
     * @param name of node
     * @return appropriate node
     */
    public Node nameDepthSearch(String name) {
        return getRoot().nameDepthSearch(name);
    }


    /**
     * This method find node with appropriate attributes
     * using depth search
     * @param key of attribute
     * @param value of attribute
     * @return appropriate node
     */
    public Node attrDepthSearch(String key, String value) {
        return getRoot().attrDepthSearch(key, value);
    }

    /**
     * This method find node with appropriate name
     * using wide search
     * @param name of node
     * @return appropriate node
     */
    public Node nameWideSearch(String name) {
        return getRoot().nameWideSearch(name);
    }

    /**
     * This method find node with appropriate attributes
     * using wide search
     * @param key of attribute
     * @param value of attribute
     * @return appropriate node
     */
    public Node attrWideSearch(String key, String value) {
        return getRoot().attrWideSearch(key, value);
    }

    /**
     * This method establish root of tree
     * @param root of tree
     */
    public void setRoot(Node root) {
        this.root = root;
    }

    /**
     * This method return root of tree
     * @return root of tree
     */
    public Node getRoot() {
        return root;
    }

    @Override
    public String toString() {
        return "treerealization.Tree{" +
                "root=" + root +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree tree = (Tree) o;
        return root != null ? root.equals(tree.root) : tree.root == null;
    }

    @Override
    public int hashCode() {
        return root != null ? root.hashCode() : 0;
    }
}
