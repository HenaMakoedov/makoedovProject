package treerealization;

import java.util.*;

/**
 * Created by Makoiedov.H on 10/4/2017.
 */
public class NodeChildrenList extends ArrayList<Node> {
    private Node parent;


    public NodeChildrenList(Node parent) {
        this.parent = parent;
    }

    public NodeChildrenList(int initialCapacity, Node parent) {
        super(initialCapacity);
        this.parent = parent;
    }

    public NodeChildrenList(Collection<? extends Node> c, Node parent) {
        super(c);
        this.parent = parent;
    }

    public NodeChildrenList(int initialCapacity) {
        super(initialCapacity);
    }

    @Override
    public boolean add(Node node) {
        if (node.getParent() != null) {
            throw new AppendChildException("current node has parent");
        }
        node.setParent(parent);
        node.setRoot(getRoot(node));
        return super.add(node);
    }

    @Override
    public boolean addAll(Collection<? extends Node> c) {
        Iterator<? extends Node> iterator = c.iterator();
        while (iterator.hasNext()) {
            Node tmpNode = iterator.next();
            tmpNode.setParent(parent);
            tmpNode.setRoot(getRoot(tmpNode));
        }
        return super.addAll(c);
    }

    @Override
    public void add(int index, Node node) {
        if (node.getParent() != null) {
            throw new AppendChildException("current node has parent");
        }
        node.setParent(parent);
        node.setRoot(getRoot(node));
        super.add(index, node);
    }

    @Override
    public Node remove(int index) {
        Node node = super.get(index);
        try {
            return super.remove(index);
        }
        finally {
            node.setParent(null);
            resetRoot(node);
        }
    }

    @Override
    public boolean remove(Object o) {
        Node node = (Node) o;
        try {
            return super.remove(o);
        }
        finally {
            node.setParent(null);
            resetRoot(node);
        }
    }


    private Node getRoot(Node node) {
        if (node.getParent() == null && node.getRoot() == node) {
            return node;
        }
        return getRoot(node.getParent());
    }
    private void resetRoot(Node node) {
        resetChildRoot(node, node);
    }
    private void resetChildRoot(Node node, Node root) {
        node.setRoot(root);
        for(int i = 0; i < node.getChildren().size(); i++) {
            resetChildRoot(node.getChildren().get(i), root);
        }
    }
}
