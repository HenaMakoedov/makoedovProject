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

/*
 Created by Makoiedov.H on 9/26/2017.
*/

@LoaderAnnotation(type = DBObjects.ObjectNames.COLUMN, parent = TablesLoader.class)
public class ColumnsLoader extends AbstractLoader {
    public ColumnsLoader() {
    }

    public ColumnsLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement columnStatement = connection.prepareStatement(Queries.COLUMNS_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            columnStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet columnResultSet = columnStatement.executeQuery();

        Node columnNode = new Node(DBObjects.ObjectNames.COLUMN);
        while(columnResultSet.next()) {
            for(int i = 1; i <= columnResultSet.getMetaData().getColumnCount(); i++) {
                columnNode.getAttributes().put(columnResultSet.getMetaData().getColumnLabel(i), columnResultSet.getString(i));
            }
        }
        return columnNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //add column nodes with name
        parent.getChildren().add(new Node(DBObjects.ObjectNames.COLUMNS));
        List<Node> columnsList = parent.nameDepthSearch(DBObjects.ObjectNames.COLUMNS).getChildren();
        for(String columnName: getColumnsNames(parameters)) {
            Node columnNode = new Node(DBObjects.ObjectNames.COLUMN);
            columnNode.getAttributes().put(DBObjects.AttributeNames.NAME, columnName);
            columnsList.add(columnNode);
        }


    }

    private List<String> getColumnsNames(List<String> parameters) throws SQLException {
        PreparedStatement columnsStatement = connection.prepareStatement(Queries.COLUMNS_NAME_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            columnsStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet columnsResultSet = columnsStatement.executeQuery();

        List<String> columnsNames = new ArrayList<>();
        while (columnsResultSet.next()) {
            columnsNames.add(columnsResultSet.getString(1));
        }
        return columnsNames;
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement columnsStatement = connection.prepareStatement(Queries.ALL_COLUMNS_QUERY);
        columnsStatement.setString(1, schemaName);

        ResultSet columnsResultSet = columnsStatement.executeQuery();
        List<Node> columnsList = new ArrayList<>();
        while (columnsResultSet.next()) {
            Node columnNode = new Node(DBObjects.ObjectNames.COLUMN);
            for(int i = 1; i <= columnsResultSet.getMetaData().getColumnCount(); i++) {
                columnNode.getAttributes().put(columnsResultSet.getMetaData().getColumnLabel(i), columnsResultSet.getString(i));
            }
            columnsList.add(columnNode);
        }
        return columnsList;
    }
}
