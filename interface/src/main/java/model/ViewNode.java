package model;

import db.constants.DBObjects;
import treerealization.Node;

import java.util.List;

/**
 * Created by Makoiedov.H on 11/1/2017.
 */

/**
 * Wrapper class under node
 * using in tree items
 */
public class ViewNode {
    private Node node;

    public ViewNode(Node node) {
        this.node = node;
    }

    public List<Node> getChildren() {
        return node.getChildren();
    }

    public Node getNode() {
        return node;
    }

    @Override
    public String toString() {
        return node.getAttributes().get(DBObjects.AttributeNames.NAME) == null ||
                node.getAttributes().get(DBObjects.AttributeNames.NAME).equals("") ||
                node.getAttributes().get(DBObjects.AttributeNames.NAME).equals(" ")? node.getName() :
                node.getAttributes().get(DBObjects.AttributeNames.NAME);
    }

}
