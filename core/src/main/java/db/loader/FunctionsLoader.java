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

@LoaderAnnotation(type = DBObjects.ObjectNames.FUNCTION, parent = SchemaLoader.class)
public class FunctionsLoader extends AbstractLoader {
    public FunctionsLoader() {
    }

    public FunctionsLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement functionStatement = connection.prepareStatement(Queries.FUNCTIONS_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            functionStatement.setString(i + 1, parameters.get(i));
        }

        ResultSet functionResultSet = functionStatement.executeQuery();

        Node currentFunctionNode = new Node(DBObjects.ObjectNames.FUNCTION);
        while (functionResultSet.next()) {
            for(int i = 1; i <= functionResultSet.getMetaData().getColumnCount(); i++) {
                currentFunctionNode.getAttributes().put(functionResultSet.getMetaData().getColumnLabel(i), functionResultSet.getString(i));
            }
        }
        return currentFunctionNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //add FUNCTIONS names
        parent.getChildren().add(new Node(DBObjects.ObjectNames.FUNCTIONS));
        List<Node> functionsList = parent.nameDepthSearch(DBObjects.ObjectNames.FUNCTIONS).getChildren();
        for(String name: getFunctionsNames(parameters)) {
            Node currentNode = new Node(DBObjects.ObjectNames.FUNCTION);
            currentNode.getAttributes().put(DBObjects.AttributeNames.NAME, name);
            functionsList.add(currentNode);
        }
    }

    private List<String> getFunctionsNames(List<String> parents) throws SQLException {
        PreparedStatement functionsStatement = connection.prepareStatement(Queries.FUNCTIONS_NAME_QUERY);

        for(int i = 0; i < parents.size(); i++) {
            functionsStatement.setString(i + 1, parents.get(i));
        }
        ResultSet functionsResultSet = functionsStatement.executeQuery();

        List<String> functionsNames = new ArrayList<>();
        while (functionsResultSet.next()) {
            functionsNames.add(functionsResultSet.getString(1));
        }
        return functionsNames;
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement functionStatement = connection.prepareStatement(Queries.ALL_FUNCTIONS_QUERY);
        functionStatement.setString(1, schemaName);

        ResultSet functionResultSet = functionStatement.executeQuery();
        List<Node> functionList = new ArrayList<>();
        while (functionResultSet.next()) {
            Node functionNode = new Node(DBObjects.ObjectNames.FUNCTION);
            for(int i = 1; i <= functionResultSet.getMetaData().getColumnCount(); i++) {
                functionNode.getAttributes().put(functionResultSet.getMetaData().getColumnLabel(i), functionResultSet.getString(i));
            }
            functionList.add(functionNode);
        }
        return functionList;
    }
}
