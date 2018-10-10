package treerealization;

import java.util.*;

/**
 * Created by Makoiedov.H on 8/28/2017.
 */

public class Node {
    private String name;
    private List<Node> children;
    private String Text;

    private Map<String, String> attributes;
    private Node parent;
    private Node root;

    /**
     * default constructor
     * sets up collections of this node
     */
    public Node() {
        children = new NodeChildrenList(this);
        attributes = new HashMap<String, String>();
        root = this;
        Text = "";
    }
    public Node(String name) {
        this.name = name;
        children = new NodeChildrenList(this);
        attributes = new HashMap<>();
        root = this;
        Text = "";
    }


    public Node(String name, Map<String, String> attributes) {
        this.name = name;
        this.attributes = attributes;
        children = new NodeChildrenList(this);
        root = this;
        Text = "";
    }

    /**
     * This is support recursive method for depth search by name
     * @param name of none
     * @param node currentNode
     * @return appropriate node
     */
    private Node recNameDepthSearch(String name, Node node){
        if (node == null) return null;

        if (node.getName().equals(name)) {
            return node;
        }
        for(int i = 0; i < node.getChildren().size(); i++){
            Node tmpNode = recNameDepthSearch(name, node.getChildren().get(i));
            if (tmpNode == null){
                continue;
            } else {
                return tmpNode;
            }
        }
        return null;
    }

    /**
     * This method find node with appropriate name
     * using depth search
     * @param name of node
     * @return appropriate node
     */
    public Node nameDepthSearch(String name) {
        return recNameDepthSearch(name, this);
    }
    ///////////////////////////////////////////////////////////////////////////


    /**
     * Auxiliary recursive method for depth search
     * with attributes
     * @param key of attribute
     * @param value of attribute
     * @param node current node
     * @return appropriate node
     */
    private Node recAttrDepthSearch(String key, String value, Node node){
        for(Map.Entry<String, String> pair : node.getAttributes().entrySet()) {
            if (pair.getKey().equals(key) && pair.getValue().equals(value)) {
                return node;
            }
        }
        for(int i = 0; i < node.getChildren().size(); i++){
            Node tmpNode = recAttrDepthSearch(key, value , node.getChildren().get(i));
            if (tmpNode == null){
                continue;
            } else {
                return tmpNode;
            }
        }
        return null;
    }

    /**
     * This method find node with appropriate attributes
     * using depth search
     * @param key of attribute
     * @param value of attribute
     * @return appropriate node
     */
    public Node attrDepthSearch(String key, String value) {
        return recAttrDepthSearch(key, value, this);
    }

    /**
     * This method find node with appropriate name
     * using wide search
     * @param name of node
     * @return appropriate node
     */
    public Node nameWideSearch(String name) {
        Queue<Node> nodes = new LinkedList<Node>();
        nodes.add(this);
        do {
            for(int i = 0; i < nodes.element().getChildren().size(); i++) {
                nodes.add(nodes.element().getChildren().get(i));
            }
            if (nodes.element().getName().equals(name)) {
                return nodes.element();
            }
            nodes.remove();
        } while (!nodes.isEmpty());

        return null;
    }

    /**
     * This method find node with appropriate attributes
     * using wide search
     * @param key of attribute
     * @param value of attribute
     * @return appropriate node
     */
    public Node attrWideSearch(String key, String value) {
        Queue<Node> nodes = new LinkedList<Node>();
        nodes.add(this);
        do {
            for(int i = 0; i < nodes.element().getChildren().size(); i++) {
                nodes.add(nodes.element().getChildren().get(i));
            }
            for(Map.Entry<String, String> pair : nodes.element().getAttributes().entrySet()) {
                if (pair.getKey().equals(key) && pair.getValue().equals(value)) {
                    return nodes.element();
                }
            }
            nodes.remove();
        } while (!nodes.isEmpty());

        return null;
    }

    /**
     * This method return name of node
     * @return name of node
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method establish name of node
     * @param name name of node
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method return child children of node
     * @return child children of node
     */
    public List<Node> getChildren() {
        return children;
    }

    /**
     * This method establish child children of node
     * @param children list of child children
     */
    public void setChildren(List<Node> children){
        if (children == null) {
            this.children = null;
            return;
        }

        for(Node node : children) {
            node.setParent(this);
        }
        for(int i = 0; i < children.size(); i++) {
            children.get(i).setRoot(this.getRoot());
        }
        this.children = children;
    }

    /**
     * This method return attributes of node
     * @return attributes of node
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * This method establish attributes of node
     * @param attributes of node
     */
    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    /**
     * This method return text of node
     * @return text of node
     */
    public String getText() {
        return Text;
    }

    /**
     * This method establish text of node
     * @param text
     */
    public void setText(String text) {
        Text = text;
    }

    /**
     * This method return parent of node
     * @return parent of node
     */
    public Node getParent() {
        return parent;
    }

    /**
     * This method establish parent of node
     * @param parent
     */
    public void setParent(Node parent){
        this.parent = parent;
    }

    /**
     * This method return root of node
     * @return root of node
     */
    public Node getRoot() {
        return root;
    }

    /**
     * This method establish root of node
     * @param root
     */
    public void setRoot(Node root) {
        if (root == null) {
            this.root = this;
            return;
        }

        this.root = root;
        for(int i = 0; i < this.getChildren().size(); i++) {
            this.getChildren().get(i).setRoot(root);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (name != null ? !name.equals(node.name) : node.name != null) return false;
        if (children != null ? !children.equals(node.children) : node.children != null) return false;
        if (Text != null ? !Text.equals(node.Text) : node.Text != null) return false;
        if (attributes != null ? !attributes.equals(node.attributes) : node.attributes != null) return false;
        if (parent.getName() != null ? !parent.getName().equals(node.parent.getName()) : node.parent.getName() != null) return false;
        return root.getName().equals(node.root.getName());
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (Text != null ? Text.hashCode() : 0);
        result = 31 * result + (attributes != null ? attributes.hashCode() : 0);
        result = 31 * result + (parent != null ? parent.hashCode() : 0);
        result = 31 * result + root.hashCode();
        return result;
    }

    @Override
    public String toString() {
        String parentName = (parent != null) ? parent.getName() : null;
        String rootName = (root != null) ? root.getName() : null;
        return "Node{" +
                "name='" + name + '\'' +
                ", children=" + children +
                ", Text='" + Text + '\'' +
                ", attributes=" + attributes +
                ", parent=" + parentName +
                ", root=" + rootName +
                '}';
    }
}