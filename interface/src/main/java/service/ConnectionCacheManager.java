package service;

import constants.PathsConstants;
import constants.SaveLoadProjectConstants;
import constants.SettingsConstants;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.ConnectionModel;
import strategypattern.SerializableClient;
import strategypattern.XMLSerializableStrategy;
import treerealization.Node;
import treerealization.Tree;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * Class manages the connection cache
 */
public class ConnectionCacheManager {
    private List<ConnectionModel> cacheList;
    private static ConnectionCacheManager instance;
    private int capacity;
    private SerializableClient client;
    private File connectionFile;


    /**
     * Method loads capacity from project settings file
     */
    private void loadCapacity() {
        InputStream inputStream = null;
        Properties properties = new Properties();
        File settingsFile = new File(PathsConstants.DEFAULT_SETTINGS_FILE);
        try {
            inputStream = new FileInputStream(settingsFile.getAbsolutePath());
            properties.load(inputStream);
            capacity = Integer.parseInt(properties.getProperty(SettingsConstants.CACHE_COUNT));
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method builds pull list from xml tree
     * @throws Exception
     */
    private void loadCacheList() throws Exception {
        Tree connectionPullTree = client.performDeserialize(connectionFile);
        Node rootNode = connectionPullTree.getRoot();
        List<Node> pullList = rootNode.getChildren();
        for(int i = 0; i < pullList.size(); i++) {
            String url = pullList.get(i).getAttributes().get(SaveLoadProjectConstants.URL);
            String port = pullList.get(i).getAttributes().get(SaveLoadProjectConstants.PORT);
            String user = pullList.get(i).getAttributes().get(SaveLoadProjectConstants.USER);
            String DBName = pullList.get(i).getAttributes().get(SaveLoadProjectConstants.DB_NAME);
            cacheList.add(new ConnectionModel(url, port, user, null, DBName));
        }
        while(cacheList.size() > capacity) {
            cacheList.remove(0);
        }

    }

    /**
     * default constructor
     */
    private ConnectionCacheManager() {
        cacheList = new LinkedList<>();
        client = new SerializableClient();
        client.setSerializableStrategy(new XMLSerializableStrategy());
        connectionFile = new File(PathsConstants.DEFAULT_CONNECTION_PULL_FILE);
        try {
            loadCapacity();
            loadCacheList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Singleton class
     * @return instance
     */
    public static ConnectionCacheManager getInstance() {
        if (instance == null) {
            instance = new ConnectionCacheManager();
        }
        return instance;
    }

    /**
     * Method returns observable list of connection models
     * @return connection list
     */
    public ObservableList<ConnectionModel> getConnectionPull() {
        ConnectionModel[] connectionCaches = new ConnectionModel[cacheList.size()];
        for(int i = 0; i < connectionCaches.length; i++) {
            connectionCaches[i] = cacheList.get(i);
        }
        return FXCollections.observableArrayList(connectionCaches);
    }

    /**
     * Method adds one connection record into connections list
     * @param connectionModel
     */
    public void addConnectionRecord(ConnectionModel connectionModel) {
        for(int i = 0; i < cacheList.size(); i++) {
            ConnectionModel tmpRecord = cacheList.get(i);
            if(tmpRecord.getDBName().equals(connectionModel.getDBName())) {
                cacheList.remove(i);
            }
        }
        cacheList.add(connectionModel);
        while (cacheList.size() > capacity) {
            cacheList.remove(0);
        }
    }

    /**
     * Method changes the size of the connection list
     * @param newSize
     */
    public void resize(int newSize) {
        capacity = newSize;
        while (cacheList.size() > capacity) {
            cacheList.remove(0);
        }
    }

    /**
     * Method saves connection list to program file
     */
    public void saveConnectionRecords() {
        Node rootNode = new Node(SaveLoadProjectConstants.CONNECTION_PULL);
        for(int i = 0; i < cacheList.size(); i++) {
            ConnectionModel tmpConnectionModel = cacheList.get(i);
            Node recordNode = new Node(tmpConnectionModel.getDBName());
            recordNode.getAttributes().put(SaveLoadProjectConstants.URL, tmpConnectionModel.getUrl());
            recordNode.getAttributes().put(SaveLoadProjectConstants.PORT, tmpConnectionModel.getPort());
            recordNode.getAttributes().put(SaveLoadProjectConstants.USER, tmpConnectionModel.getUser());
            recordNode.getAttributes().put(SaveLoadProjectConstants.DB_NAME,tmpConnectionModel.getDBName());

            rootNode.getChildren().add(recordNode);
        }
        try {
            client.performSerialize(new Tree(rootNode), connectionFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
