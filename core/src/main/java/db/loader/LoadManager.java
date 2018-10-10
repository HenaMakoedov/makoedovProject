package db.loader;

import db.constants.DBObjects;
import db.loader.sorters.SortManager;
import db.utils.ReflectionUtils;
import treerealization.Node;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

/**
 * Created by Makoiedov.H on 10/3/2017.
 */
public class LoadManager {
    private static LoadManager instance;
    private SortManager sortManager;

    private Connection connection;
    private Map<String, Loader> loaders;

    private LoadManager(){
    }


    /**
     * Singleton with lazy loading
     * @return instance of our LoadManager
     */
    public static LoadManager getInstance() {
        if (instance == null) {
            instance = new LoadManager();
        }
        return instance;
    }

    /**
     * This method establishes connection for loadManager
     * and registers all loaders
     * @param connection
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
        loaders = new HashMap<>();
        sortManager = SortManager.getInstance();
        registerLoaders();
    }

    /**
     * This method load full information for node
     * @param node incomplete node
     * @throws SQLException
     */
    public void loadNode(Node node) throws SQLException {
        Loader loader = loaders.get(node.getName());
        if (loader != null) {
            List<String> parameters = getNodeQueryParameters(node);
            Node fullNode = loader.loadNode(parameters);
            node.setAttributes(fullNode.getAttributes());
            node.setChildren(fullNode.getChildren());
            sortManager.sortNode(node);
        }
        if (!node.getName().equals(DBObjects.ObjectNames.SCHEMA)) {
            for(Node childNode : node.getChildren()) {
                    loadNode(childNode);
            }
        }
    }

    /**
     * This method reload information for node
     * @param node
     * @throws SQLException
     */
    public void reloadCategory(Node node) throws SQLException {
        String categoryName = node.getName();
        Node parent = node.getParent();
        List<String> parameters = new ArrayList<>();
        parameters.add(parent.getAttributes().get(DBObjects.AttributeNames.NAME));

        switch (categoryName) {
            case DBObjects.ObjectNames.TABLES : {
                for(int i = 0; i < parent.getChildren().size(); i++) {
                    if (parent.getChildren().get(i).getName().equals(DBObjects.ObjectNames.TABLES)) {
                        parent.getChildren().remove(i);
                    }
                }
                new TablesLoader(connection).notifyParent(parent, parameters);
                break;
            }

            case DBObjects.ObjectNames.VIEWS : {
                for(int i = 0; i < parent.getChildren().size(); i++) {
                    if (parent.getChildren().get(i).getName().equals(DBObjects.ObjectNames.VIEWS)) {
                        parent.getChildren().remove(i);
                    }
                }
                new ViewsLoader(connection).notifyParent(parent, parameters);
                break;

            }

            case DBObjects.ObjectNames.PROCEDURES : {
                for(int i = 0; i < parent.getChildren().size(); i++) {
                    if (parent.getChildren().get(i).getName().equals(DBObjects.ObjectNames.PROCEDURES)) {
                        parent.getChildren().remove(i);
                    }
                }
                new ProceduresLoader(connection).notifyParent(parent, parameters);
                break;

            }

            case DBObjects.ObjectNames.FUNCTIONS : {
                for(int i = 0; i < parent.getChildren().size(); i++) {
                    if (parent.getChildren().get(i).getName().equals(DBObjects.ObjectNames.FUNCTIONS)) {
                        parent.getChildren().remove(i);
                    }
                }
                new FunctionsLoader(connection).notifyParent(parent, parameters);
                break;

            }

            default: return;
        }
        sortManager.sortNode(parent);
    }

    /**
     * Method goes up the tree to the root
     * and collects nodes names for building sql query
     * and reverses list parameters to get the right order
     * @param node
     * @return list of parameters
     */
    private List<String> getNodeQueryParameters(Node node) {
        List<String> parameters = new ArrayList<>();
        while(node != null) {
            if (node.getAttributes().get(DBObjects.AttributeNames.NAME) != null) {
                parameters.add(node.getAttributes().get(DBObjects.AttributeNames.NAME));
            }
            node = node.getParent();
        }
        List<String> reverseParameters = new ArrayList<>();
        for(int i = parameters.size() - 1; i >= 0; i--) {
            reverseParameters.add(parameters.get(i));
        }
        return reverseParameters;
    }

    /**
     * This method goes through the folder with loaders and
     * collects loaders marked with annotations in map of loadManager
     */
    private void registerLoaders() {
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getAnnotatedClasses(LoaderAnnotation.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(classes != null && classes.size() != 0) {
            for(Class aClass : classes) {
                try {
                    Class<?> clazz = aClass;
                    if (clazz.isAnnotationPresent(LoaderAnnotation.class)) {
                        Loader loader = (Loader)clazz.newInstance();
                        loader.setConnection(connection);
                        registerChildrenLoaders(loader);
                        loaders.put(clazz.getAnnotation(LoaderAnnotation.class).type(), loader);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }

    /**
     * This method goes through the folder with loaders and
     * collects loaders marked with annotations in map of specific loader
     */
    private void registerChildrenLoaders(Loader loader) {
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getAnnotatedClasses(LoaderAnnotation.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(classes != null && classes.size() != 0) {
            for(Class aClass : classes) {
                try {
                    Class<?> clazz = aClass;
                    if (clazz.isAnnotationPresent(LoaderAnnotation.class)
                            && clazz.getAnnotation(LoaderAnnotation.class).parent() == loader.getClass()) {
                        Loader childLoader = (AbstractLoader)clazz.newInstance();
                        childLoader.setConnection(connection);
                        loader.addChildrenLoader(childLoader);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
