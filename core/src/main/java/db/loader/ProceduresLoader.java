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

@LoaderAnnotation(type = DBObjects.ObjectNames.PROCEDURE, parent = SchemaLoader.class)
public class ProceduresLoader extends AbstractLoader {
    public ProceduresLoader() {
    }

    public ProceduresLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement procedureStatement = connection.prepareStatement(Queries.PROCEDURES_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            procedureStatement.setString(i + 1, parameters.get(i));
        }

        ResultSet procedureResultSet = procedureStatement.executeQuery();

        Node currentProcedureNode = new Node(DBObjects.ObjectNames.PROCEDURE);
        while (procedureResultSet.next()) {
            for(int i = 1; i <= procedureResultSet.getMetaData().getColumnCount(); i++) {
                currentProcedureNode.getAttributes().put(procedureResultSet.getMetaData().getColumnLabel(i), procedureResultSet.getString(i));
            }
        }
        return currentProcedureNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //add PROCEDURES names
        parent.getChildren().add(new Node(DBObjects.ObjectNames.PROCEDURES));
        List<Node> proceduresList = parent.nameDepthSearch(DBObjects.ObjectNames.PROCEDURES).getChildren();
        for(String name: getProceduresNames(parameters)) {
            Node currentNode = new Node(DBObjects.ObjectNames.PROCEDURE);
            currentNode.getAttributes().put(DBObjects.AttributeNames.NAME, name);
            proceduresList.add(currentNode);
        }
    }

    private List<String> getProceduresNames(List<String> parents) throws SQLException {
        PreparedStatement proceduresStatement = connection.prepareStatement(Queries.PROCEDURES_NAME_QUERY);

        for(int i = 0; i < parents.size(); i++) {
            proceduresStatement.setString(i + 1, parents.get(i));
        }
        ResultSet proceduresResultSet = proceduresStatement.executeQuery();

        List<String> proceduresNames = new ArrayList<>();
        while (proceduresResultSet.next()) {
            proceduresNames.add(proceduresResultSet.getString(1));
        }
        return proceduresNames;
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement proceduresStatement = connection.prepareStatement(Queries.ALL_PROCEDURES_QUERY);
        proceduresStatement.setString(1, schemaName);

        ResultSet proceduresResultSet = proceduresStatement.executeQuery();
        List<Node> proceduresList = new ArrayList<>();
        while (proceduresResultSet.next()) {
            Node proceduresNode = new Node(DBObjects.ObjectNames.PROCEDURE);
            for(int i = 1; i <= proceduresResultSet.getMetaData().getColumnCount(); i++) {
                proceduresNode.getAttributes().put(proceduresResultSet.getMetaData().getColumnLabel(i), proceduresResultSet.getString(i));
            }
            proceduresList.add(proceduresNode);
        }
        return proceduresList;
    }
}
