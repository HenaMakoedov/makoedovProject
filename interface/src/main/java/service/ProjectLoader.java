package service;

import constants.SaveLoadProjectConstants;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;
import model.ConnectionModel;
import model.ProjectSettings;
import model.ViewNode;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;
import strategypattern.SerializableClient;
import strategypattern.XMLSerializableStrategy;
import treerealization.Node;
import treerealization.Tree;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Makoiedov.H on 11/20/2017.
 */
public class ProjectLoader {
    private static Logger logger = Logger.getLogger(ProjectLoader.class);

    /**
     * Method loads project settings from project directory
     * @param directory project directory
     * @return project settings
     * @throws Exception
     */
    public static ProjectSettings loadProject(File directory) throws Exception {
        if (!directory.isDirectory()) {
            logger.warn("No select directory");
            return null;
        }
        File[] files = directory.listFiles();
        List<File> filesList = new ArrayList<>();
        for(File currFile : files) {
            if (currFile.getName().equals(SaveLoadProjectConstants.XML_TREE_FILE_NAME) ||
                    currFile.getName().equals(SaveLoadProjectConstants.STATE_FILE_NAME) ||
                    currFile.getName().equals(SaveLoadProjectConstants.SETTINGS_FILE_NAME)) {
                filesList.add(currFile);
            }
        }

        if (filesList.size() != 3) {
            logger.warn("dont found 3 files");
            return null;
        }
        File XMLTreeFile = null;
        File stateFile = null;
        File settingFile = null;

        for(File currFile : filesList) {
            if (currFile.getName().equals(SaveLoadProjectConstants.XML_TREE_FILE_NAME)) {
                XMLTreeFile = currFile;
            } else if (currFile.getName().equals(SaveLoadProjectConstants.STATE_FILE_NAME)) {
                stateFile = currFile;
            } else if (currFile.getName().equals(SaveLoadProjectConstants.SETTINGS_FILE_NAME)) {
                settingFile = currFile;
            }
        }

        //validation
        try {
            XMLValidator.validateXMLFile(XMLTreeFile);
        } catch (SAXException e) {
            logger.warn("no valid tree xml file");
            logger.error(e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid file");
            alert.setHeaderText("Invalid xmlFile with tree");
            alert.setContentText("Please, check you file 'tree.xml' in selected directory");
            alert.showAndWait();
            return null;
        }


        SerializableClient client = new SerializableClient();
        client.setSerializableStrategy(new XMLSerializableStrategy());

        Tree XMLTree = client.performDeserialize(XMLTreeFile);
        Tree stateTree = client.performDeserialize(stateFile);
        Tree settingTree = client.performDeserialize(settingFile);

        //load settings
        Map<String, String> settingAttributesMap = settingTree.getRoot().getAttributes();
        boolean isColorScript = settingAttributesMap.get(SaveLoadProjectConstants.IS_COLOR_SCRIPT).equals(SaveLoadProjectConstants.YES);
        ProjectSettings.Size size = null;
        String sizeStr = settingAttributesMap.get(SaveLoadProjectConstants.FONT_SIZE);

        if (sizeStr.equals(SaveLoadProjectConstants.SMALL)) {
            size = ProjectSettings.Size.SMALL;
        } else if (sizeStr.equals(SaveLoadProjectConstants.MEDIUM)) {
            size = ProjectSettings.Size.MEDIUM;
        } else if (sizeStr.equals(SaveLoadProjectConstants.BIG)) {
            size = ProjectSettings.Size.BIG;
        }

        String url = settingAttributesMap.get(SaveLoadProjectConstants.URL);
        String port = settingAttributesMap.get(SaveLoadProjectConstants.PORT);
        String user = settingAttributesMap.get(SaveLoadProjectConstants.USER);
        String DBName = settingAttributesMap.get(SaveLoadProjectConstants.DB_NAME);

        ConnectionModel model = new ConnectionModel(url, port, user, null, DBName);

        //load main tree
        TreeItem<ViewNode> rootItem = new TreeItem<>();
        createTreeView(XMLTree.getRoot(), rootItem);

        //load tree state
        loadTreeViewExpandState(rootItem, stateTree.getRoot()); //expand items
        TreeItem<ViewNode> selectedItem = findSelectedItem(rootItem, stateTree.getRoot());

        ProjectSettings settings = new ProjectSettings(rootItem, selectedItem, isColorScript, size, model);

        return settings;
    }

    private static void createTreeView(Node currNode, TreeItem<ViewNode> currItem) {
        currItem.setValue(new ViewNode(currNode));
        for(Node childNode : currNode.getChildren()) {
            TreeItem<ViewNode> childItem = new TreeItem<>();
            currItem.getChildren().add(childItem);
            createTreeView(childNode, childItem);
        }
    }

    private static void loadTreeViewExpandState(TreeItem<ViewNode> currItem, Node stateNode) {
        currItem.setExpanded(stateNode.getAttributes().get(SaveLoadProjectConstants.IS_EXPANDED).equals(SaveLoadProjectConstants.YES));
        for(int i = 0; i < currItem.getChildren().size(); i++) {
            loadTreeViewExpandState(currItem.getChildren().get(i), stateNode.getChildren().get(i));
        }
    }

    private static TreeItem<ViewNode> findSelectedItem(TreeItem<ViewNode> currItem, Node stateNode) {
        if (stateNode.getAttributes().get(SaveLoadProjectConstants.IS_SELECTED).equals(SaveLoadProjectConstants.YES)) {
            return currItem;
        }

        for(int i = 0; i < currItem.getChildren().size(); i++) {
            TreeItem<ViewNode> foundItem = findSelectedItem(currItem.getChildren().get(i), stateNode.getChildren().get(i));
            if (foundItem == null) {
                continue;
            } else {
                return foundItem;
            }
        }
        return null;
    }

}
