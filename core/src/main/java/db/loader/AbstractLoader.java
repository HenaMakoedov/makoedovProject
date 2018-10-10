package db.loader;

import treerealization.Node;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Makoiedov.H on 9/22/2017.
 */
public abstract class AbstractLoader implements Loader {
    protected Connection connection;

    private List<AbstractLoader> childrenLoaders;

    public AbstractLoader(Connection connection)
    {
        this.connection = connection;
        childrenLoaders = new ArrayList<>();
    }


    public AbstractLoader() {
        childrenLoaders = new ArrayList<>();
    }

    /**
     * This method establish connection for each loader
     * @param connection
     */
    @Override
    public final void setConnection(Connection connection) {
        this.connection = connection;
    }


    @Override
    public final Node loadNode(List<String> parameters) throws SQLException {
        Node node = loadNodeWithoutChildren(parameters);
        loadChildren(node, parameters);
        return node;
    }


    /**
     * This method builds specific node on sql query
     * @param parameters list of parameters for building specific query
     * @return built node
     * @throws SQLException
     */

    public abstract Node loadNodeWithoutChildren(List<String> parameters) throws SQLException;

    /**
     * method hooks to the parent node all nodes of this loader,
     * the node contains only one attribute - the name of the database object
     * @param parent parent node
     * @param parameters parameters for creating sql query
     * @throws SQLException
     */
    public abstract void notifyParent(Node parent, List<String> parameters) throws SQLException;


    /**
     * Method adds children nodes for concrete node using reflection
     * @param node
     * @param parameters
     * @throws SQLException
     */
    private final void loadChildren(Node node, List<String> parameters) throws SQLException {
        for(AbstractLoader abstractLoader: childrenLoaders) {
            abstractLoader.notifyParent(node,parameters);
        }

    }

    @Override
    public final void addChildrenLoader(Loader loader) {
        childrenLoaders.add((AbstractLoader)loader);
    }
}
