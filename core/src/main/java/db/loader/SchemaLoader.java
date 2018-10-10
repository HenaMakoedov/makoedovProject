package db.loader;

import db.constants.DBObjects;
import db.constants.Queries;
import treerealization.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Makoiedov.H on 9/25/2017.
 */
@LoaderAnnotation(type = DBObjects.ObjectNames.SCHEMA)
public class SchemaLoader extends AbstractLoader {
    public SchemaLoader() {
    }

    public SchemaLoader(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement schemaStatement = connection.prepareStatement(Queries.SCHEMA_QUERY);
        for(int i = 0; i < parameters.size(); i++){
            schemaStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet resultSet = schemaStatement.executeQuery();
        Node rootNode = new Node(DBObjects.ObjectNames.SCHEMA);
        while (resultSet.next()) {
            for(int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                rootNode.getAttributes().put(resultSet.getMetaData().getColumnLabel(i), resultSet.getString(i));
            }
        }
        return rootNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        return;
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement schemaStatement = connection.prepareStatement(Queries.SCHEMA_QUERY);
        schemaStatement.setString(1, schemaName);

        ResultSet schemaResultSet = schemaStatement.executeQuery();
        List<Node> schemasList = new ArrayList<>();
        while (schemaResultSet.next()) {
            Node proceduresNode = new Node(DBObjects.ObjectNames.SCHEMA);
            for(int i = 1; i <= schemaResultSet.getMetaData().getColumnCount(); i++) {
                proceduresNode.getAttributes().put(schemaResultSet.getMetaData().getColumnLabel(i), schemaResultSet.getString(i));
            }
            schemasList.add(proceduresNode);
        }
        return schemasList;
    }
}
