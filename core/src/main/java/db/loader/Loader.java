package db.loader;

import treerealization.Node;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Makoiedov.H on 9/22/2017.
 */
public interface Loader {
    Node loadNode(List<String> parents) throws SQLException;
    void setConnection(Connection connection);
    void addChildrenLoader(Loader loader);
    List<Node> loadAllNodes(String schemaName) throws SQLException;
}
