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

@LoaderAnnotation(type = DBObjects.ObjectNames.TABLE, parent = SchemaLoader.class)
public class TablesLoader extends AbstractLoader {
    public TablesLoader() {
    }

    public TablesLoader(Connection connection) throws SQLException {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement tableStatement = connection.prepareStatement(Queries.TABLES_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            tableStatement.setString(i + 1, parameters.get(i));
        }

        ResultSet tableResultSet = tableStatement.executeQuery();

        Node currentTableNode = new Node(DBObjects.ObjectNames.TABLE);
        while (tableResultSet.next()) {
            for(int i = 1; i <= tableResultSet.getMetaData().getColumnCount(); i++) {
                currentTableNode.getAttributes().put(tableResultSet.getMetaData().getColumnLabel(i), tableResultSet.getString(i));
            }
        }

        return currentTableNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //add TABLES names
        parent.getChildren().add(new Node(DBObjects.ObjectNames.TABLES));
        List<Node> tablesList = parent.nameDepthSearch(DBObjects.ObjectNames.TABLES).getChildren();
        for(String name: getTablesNames(parameters)) {
            Node currentNode = new Node(DBObjects.ObjectNames.TABLE);
            currentNode.getAttributes().put(DBObjects.AttributeNames.NAME, name);
            tablesList.add(currentNode);
        }
    }


    private List<String> getTablesNames(List<String> parents) throws SQLException {
        PreparedStatement tablesStatement = connection.prepareStatement(Queries.TABLES_NAME_QUERY);

        for(int i = 0; i < parents.size(); i++) {
            tablesStatement.setString(i + 1, parents.get(i));
        }
        ResultSet tablesResultSet = tablesStatement.executeQuery();

        List<String> tablesNames = new ArrayList<>();
        while (tablesResultSet.next()) {
            tablesNames.add(tablesResultSet.getString(1));
        }
        return tablesNames;
    }


    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement tablesStatement = connection.prepareStatement(Queries.ALL_TABLES_QUERY);
        tablesStatement.setString(1, schemaName);

        ResultSet tablesResultSet = tablesStatement.executeQuery();
        List<Node> tablesList = new ArrayList<>();
        while (tablesResultSet.next()) {
            Node proceduresNode = new Node(DBObjects.ObjectNames.TABLE);
            for(int i = 1; i <= tablesResultSet.getMetaData().getColumnCount(); i++) {
                proceduresNode.getAttributes().put(tablesResultSet.getMetaData().getColumnLabel(i), tablesResultSet.getString(i));
            }
            tablesList.add(proceduresNode);
        }
        return tablesList;
    }


}
