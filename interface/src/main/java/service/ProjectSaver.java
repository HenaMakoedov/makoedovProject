package service;

import constants.SaveLoadProjectConstants;
import db.constants.DBObjects;
import javafx.scene.control.TreeItem;
import model.ProjectSettings;
import model.ViewNode;
import strategypattern.SerializableClient;
import strategypattern.XMLSerializableStrategy;
import treerealization.Node;
import treerealization.Tree;

import java.io.File;

/**
 * Created by Makoiedov.H on 11/20/2017.
 */
public class ProjectSaver {
    /**
     * Method saves info about current project in selected directory
     * @param projectSettings
     * @param directory output directory
     * @throws Exception
     */
    public static void saveProject(ProjectSettings projectSettings, File directory) throws Exception {
        if (!directory.isDirectory()) {
            return;
        }

        //create Tree with loaded nodes
        Node rootNode = projectSettings.getRootItem().getValue().getNode();
        Tree XMLTree = new Tree(rootNode);


        //create Tree with state of treeView
        Node stateRootNode = new Node();
        createStateTree(stateRootNode, projectSettings.getRootItem(), projectSettings.getSelectedItem());
        Tree stateTree = new Tree(stateRootNode);

        //create Tree with project settings
        Node settingsNode = new Node(SaveLoadProjectConstants.SETTING);
        if (projectSettings.isColorScript()) {
            settingsNode.getAttributes().put(SaveLoadProjectConstants.IS_COLOR_SCRIPT, SaveLoadProjectConstants.YES);
        } else {
            settingsNode.getAttributes().put(SaveLoadProjectConstants.IS_COLOR_SCRIPT, SaveLoadProjectConstants.NO);
        }

        if (projectSettings.getSize() == ProjectSettings.Size.SMALL) {
            settingsNode.getAttributes().put(SaveLoadProjectConstants.FONT_SIZE, SaveLoadProjectConstants.SMALL);
        } else if (projectSettings.getSize() == ProjectSettings.Size.MEDIUM) {
            settingsNode.getAttributes().put(SaveLoadProjectConstants.FONT_SIZE, SaveLoadProjectConstants.MEDIUM);
        } else if (projectSettings.getSize() == ProjectSettings.Size.BIG) {
            settingsNode.getAttributes().put(SaveLoadProjectConstants.FONT_SIZE, SaveLoadProjectConstants.BIG);
        }
        settingsNode.getAttributes().put(SaveLoadProjectConstants.URL, projectSettings.getConnectionModel().getUrl());
        settingsNode.getAttributes().put(SaveLoadProjectConstants.PORT, projectSettings.getConnectionModel().getPort());
        settingsNode.getAttributes().put(SaveLoadProjectConstants.USER, projectSettings.getConnectionModel().getUser());
        settingsNode.getAttributes().put(SaveLoadProjectConstants.DB_NAME, projectSettings.getConnectionModel().getDBName());
        Tree settingTree = new Tree(settingsNode);


        File containerDirectory = new File(directory, projectSettings.getRootItem().getValue().getNode().getAttributes().get(DBObjects.AttributeNames.NAME));
        containerDirectory.mkdirs();

        File XMLTreeFile = new File(containerDirectory, SaveLoadProjectConstants.XML_TREE_FILE_NAME);
        XMLTreeFile.createNewFile();

        File stateFile = new File(containerDirectory, SaveLoadProjectConstants.STATE_FILE_NAME);
        stateFile.createNewFile();

        File settingFile = new File(containerDirectory, SaveLoadProjectConstants.SETTINGS_FILE_NAME);
        settingFile.createNewFile();

        SerializableClient client = new SerializableClient();
        client.setSerializableStrategy(new XMLSerializableStrategy());
        client.performSerialize(XMLTree, XMLTreeFile);
        client.performSerialize(stateTree, stateFile);
        client.performSerialize(settingTree, settingFile);
    }

    private static void createStateTree(Node currNode, TreeItem<ViewNode> currItem, TreeItem<ViewNode> selectedItem) {
        currNode.setName(currItem.getValue().getNode().getName());
        boolean isExpanded = currItem.isExpanded();
        if (isExpanded) {
            currNode.getAttributes().put(SaveLoadProjectConstants.IS_EXPANDED, SaveLoadProjectConstants.YES);
        } else {
            currNode.getAttributes().put(SaveLoadProjectConstants.IS_EXPANDED, SaveLoadProjectConstants.NO);
        }

        if (currItem == selectedItem || currItem.equals(selectedItem)) {
            currNode.getAttributes().put(SaveLoadProjectConstants.IS_SELECTED, SaveLoadProjectConstants.YES);
        } else {
            currNode.getAttributes().put(SaveLoadProjectConstants.IS_SELECTED, SaveLoadProjectConstants.NO);
        }
        for(int i = 0; i < currItem.getChildren().size(); i++) {
            Node childNode = new Node();
            currNode.getChildren().add(childNode);
            createStateTree(childNode, currItem.getChildren().get(i), selectedItem);
        }
    }
}
