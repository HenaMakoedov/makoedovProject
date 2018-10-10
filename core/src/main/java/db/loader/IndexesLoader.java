package db.loader;

import db.constants.DBObjects;
import db.constants.Queries;
import treerealization.Node;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

 /*
  Created by Makoiedov.H on 9/26/2017.
 */

@LoaderAnnotation(type = DBObjects.ObjectNames.INDEX, parent = TablesLoader.class)
public class IndexesLoader extends AbstractLoader {
    public IndexesLoader() {
    }

    public IndexesLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement indexStatement = connection.prepareStatement(Queries.INDEXES_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            indexStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet indexResultSet = indexStatement.executeQuery();

        Node indexKeyNode = new Node(DBObjects.ObjectNames.INDEX);
        while (indexResultSet.next()) {
            for(int i = 1; i <= indexResultSet.getMetaData().getColumnCount(); i++) {
                indexKeyNode.getAttributes().put(indexResultSet.getMetaData().getColumnLabel(i), indexResultSet.getString(i));
            }
        }
        return indexKeyNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //load index nodes with name
        parent.getChildren().add(new Node(DBObjects.ObjectNames.INDEXES));
        List<Node> indexList = parent.nameDepthSearch(DBObjects.ObjectNames.INDEXES).getChildren();
        for(String indexName : getIndexesNames(parameters)) {
            Node indexNode = new Node(DBObjects.ObjectNames.INDEX);
            indexNode.getAttributes().put(DBObjects.AttributeNames.NAME, indexName);
            indexList.add(indexNode);
        }

    }

    private Set<String> getIndexesNames(List<String> parents) throws SQLException {
        PreparedStatement indexesStatement = connection.prepareStatement(Queries.INDEXES_NAME_QUERY);
        for(int i = 0; i < parents.size(); i++) {
            indexesStatement.setString(i + 1, parents.get(i));
        }
        ResultSet indexesResultSet = indexesStatement.executeQuery();

       Set<String> indexesNames = new LinkedHashSet<>();
        while (indexesResultSet.next()) {
            indexesNames.add(indexesResultSet.getString(1));
        }
        return indexesNames;
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement indexesStatement = connection.prepareStatement(Queries.ALL_INDEXES_QUERY);
        indexesStatement.setString(1, schemaName);

        ResultSet indexesResultSet = indexesStatement.executeQuery();
        List<Node> indexesList = new ArrayList<>();
        while (indexesResultSet.next()) {
            Node FKNode = new Node(DBObjects.ObjectNames.INDEX);
            for(int i = 1; i <= indexesResultSet.getMetaData().getColumnCount(); i++) {
                FKNode.getAttributes().put(indexesResultSet.getMetaData().getColumnLabel(i), indexesResultSet.getString(i));
            }
            indexesList.add(FKNode);
        }
        return indexesList;
    }
}
