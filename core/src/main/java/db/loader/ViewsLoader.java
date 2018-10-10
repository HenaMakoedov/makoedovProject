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
@LoaderAnnotation(type = DBObjects.ObjectNames.VIEW, parent = SchemaLoader.class)
public class ViewsLoader extends AbstractLoader {
    public ViewsLoader() {
    }

    public ViewsLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement viewStatement = connection.prepareStatement(Queries.VIEWS_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            viewStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet viewResultSet = viewStatement.executeQuery();

        Node currentViewNode = new Node(DBObjects.ObjectNames.VIEW);
        while (viewResultSet.next()) {
            for(int i = 1; i <= viewResultSet.getMetaData().getColumnCount(); i++) {
                currentViewNode.getAttributes().put(viewResultSet.getMetaData().getColumnLabel(i), viewResultSet.getString(i));
            }
        }
        return currentViewNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //add VIEWS names
        parent.getChildren().add(new Node(DBObjects.ObjectNames.VIEWS));
        List<Node> viewsList = parent.nameDepthSearch(DBObjects.ObjectNames.VIEWS).getChildren();
        for(String name : getViewsNames(parameters)) {
            Node currentNode = new Node(DBObjects.ObjectNames.VIEW);
            currentNode.getAttributes().put(DBObjects.AttributeNames.NAME, name);
            viewsList.add(currentNode);
        }
    }

    private List<String> getViewsNames(List<String> parents) throws SQLException {
        PreparedStatement viewsStatement = connection.prepareStatement(Queries.VIEWS_NAME_QUERY);

        for(int i = 0; i < parents.size(); i++) {
            viewsStatement.setString(i + 1, parents.get(i));
        }
        ResultSet viewsResultSet = viewsStatement.executeQuery();

        List<String> viewsNames = new ArrayList<>();
        while (viewsResultSet.next()) {
            viewsNames.add(viewsResultSet.getString(1));
        }
        return viewsNames;
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement viewsStatement = connection.prepareStatement(Queries.ALL_VIEWS_QUERY);
        viewsStatement.setString(1, schemaName);

        ResultSet viewsResultSet = viewsStatement.executeQuery();
        List<Node> viewsList = new ArrayList<>();
        while (viewsResultSet.next()) {
            Node viewsNode = new Node(DBObjects.ObjectNames.VIEW);
            for(int i = 1; i <= viewsResultSet.getMetaData().getColumnCount(); i++) {
                viewsNode.getAttributes().put(viewsResultSet.getMetaData().getColumnLabel(i), viewsResultSet.getString(i));
            }
            viewsList.add(viewsNode);
        }
        return viewsList;
    }
}
