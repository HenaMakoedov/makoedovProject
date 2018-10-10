package model;

import javafx.scene.control.TreeItem;

/**
 * Created by Makoiedov.H on 11/20/2017.
 */

public class ProjectSettings {
    private TreeItem<ViewNode> rootItem;
    private TreeItem<ViewNode> selectedItem;
    private boolean isColorScript;
    private Size size;
    private ConnectionModel connectionModel;

    public ProjectSettings(ConnectionModel model) {
        rootItem = null;
        selectedItem = null;
        isColorScript = false;
        size = Size.MEDIUM;
        this.connectionModel = model;
    }

    public ProjectSettings(TreeItem<ViewNode> rootItem, TreeItem<ViewNode> selectedItem, boolean isColorScript, Size size, ConnectionModel connectionModel) {
        this.rootItem = rootItem;
        this.selectedItem = selectedItem;
        this.isColorScript = isColorScript;
        this.size = size;
        this.connectionModel = connectionModel;
    }

    public boolean isColorScript() {
        return isColorScript;
    }

    public void setColorScript(boolean colorScript) {
        isColorScript = colorScript;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public ConnectionModel getConnectionModel() {
        return connectionModel;
    }

    public void setConnectionModel(ConnectionModel connectionModel) {
        this.connectionModel = connectionModel;
    }

    public TreeItem<ViewNode> getRootItem() {
        return rootItem;
    }

    public TreeItem<ViewNode> getSelectedItem() {
        return selectedItem;
    }

    public void setRootItem(TreeItem<ViewNode> rootItem) {
        this.rootItem = rootItem;
    }

    public void setSelectedItem(TreeItem<ViewNode> selectedItem) {
        this.selectedItem = selectedItem;
    }

    public enum Size {
        SMALL,
        MEDIUM,
        BIG
    }
}
