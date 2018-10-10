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
  Created by Makoiedov.H on 9/27/2017.
*/

@LoaderAnnotation(type = DBObjects.ObjectNames.TRIGGER, parent = TablesLoader.class)
public class TriggersLoader extends AbstractLoader {
    public TriggersLoader() {
    }

    public TriggersLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement triggerStatement = connection.prepareStatement(Queries.TRIGGERS_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            triggerStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet triggerResultSet = triggerStatement.executeQuery();

        Node triggerNode = new Node(DBObjects.ObjectNames.TRIGGER);
        while (triggerResultSet.next()) {
            for(int i = 1; i <= triggerResultSet.getMetaData().getColumnCount(); i++) {
                triggerNode.getAttributes().put(triggerResultSet.getMetaData().getColumnLabel(i), triggerResultSet.getString(i));
            }
        }
        return triggerNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //add trigger nodes with name
        parent.getChildren().add(new Node(DBObjects.ObjectNames.TRIGGERS));
        List<Node> triggerList = parent.nameDepthSearch(DBObjects.ObjectNames.TRIGGERS).getChildren();
        for(String triggerName : getTriggersNames(parameters)) {
            Node triggerNode = new Node(DBObjects.ObjectNames.TRIGGER);
            triggerNode.getAttributes().put(DBObjects.AttributeNames.NAME, triggerName);
            triggerList.add(triggerNode);
        }
    }

    private List<String> getTriggersNames(List<String> parents) throws SQLException {
        PreparedStatement triggersStatement = connection.prepareStatement(Queries.TRIGGERS_NAME_QUERY);
        for(int i = 0; i < parents.size(); i++) {
            triggersStatement.setString(i + 1, parents.get(i));
        }
        ResultSet triggersResultSet = triggersStatement.executeQuery();

        List<String> triggersNames = new ArrayList<>();
        while (triggersResultSet.next()) {
            triggersNames.add(triggersResultSet.getString(1));
        }
        return triggersNames;
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement triggersStatement = connection.prepareStatement(Queries.ALL_TRIGGERS_QUERY);
        triggersStatement.setString(1, schemaName);

        ResultSet triggersResultSet = triggersStatement.executeQuery();
        List<Node> triggersList = new ArrayList<>();
        while (triggersResultSet.next()) {
            Node triggerNode = new Node(DBObjects.ObjectNames.TRIGGER);
            for(int i = 1; i <= triggersResultSet.getMetaData().getColumnCount(); i++) {
                triggerNode.getAttributes().put(triggersResultSet.getMetaData().getColumnLabel(i), triggersResultSet.getString(i));
            }
            triggersList.add(triggerNode);
        }
        return triggersList;
    }
}
