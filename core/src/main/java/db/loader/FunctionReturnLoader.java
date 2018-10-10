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
 * Created by Makoiedov.H on 10/24/2017.
 */
@LoaderAnnotation(type = DBObjects.ObjectNames.FUNCTION_RETURN, parent = FunctionsLoader.class)
public class FunctionReturnLoader extends AbstractLoader {
    public FunctionReturnLoader(Connection connection) {
        super(connection);
    }

    public FunctionReturnLoader() {
        super();
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement parametersStatement = connection.prepareStatement(Queries.FUNCTION_RETURN_QUERY);
        for(int i = 0; i < parameters.size() - 1; i++) {
            parametersStatement.setString(i + 1, parameters.get(i));
        }

        ResultSet parameterResultSet = parametersStatement.executeQuery();
        Node parameterNode = new Node(DBObjects.ObjectNames.FUNCTION_RETURN);
        while (parameterResultSet.next()) {
            for(int i = 1; i <= parameterResultSet.getMetaData().getColumnCount(); i++) {
                parameterNode.getAttributes().put(parameterResultSet.getMetaData().getColumnLabel(i), parameterResultSet.getString(i));
            }
        }
        return parameterNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        parent.getChildren().add(new Node(DBObjects.ObjectNames.FUNCTION_RETURNS));
        List<Node> parametersList = parent.nameDepthSearch(DBObjects.ObjectNames.FUNCTION_RETURNS).getChildren();
        Node parameterNode = new Node(DBObjects.ObjectNames.FUNCTION_RETURN);
        parameterNode.getAttributes().put(DBObjects.AttributeNames.NAME, "");
        parametersList.add(parameterNode);
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement functionReturnsStatement = connection.prepareStatement(Queries.ALL_FUNCTION_RETURNS_QUERY);
        functionReturnsStatement.setString(1, schemaName);

        ResultSet functionReturnsResultSet = functionReturnsStatement.executeQuery();
        List<Node> functionReturnsList = new ArrayList<>();
        while (functionReturnsResultSet.next()) {
            Node functionReturnNode = new Node(DBObjects.ObjectNames.FUNCTION_RETURN);
            for(int i = 1; i <= functionReturnsResultSet.getMetaData().getColumnCount(); i++) {
                functionReturnNode.getAttributes().put(functionReturnsResultSet.getMetaData().getColumnLabel(i), functionReturnsResultSet.getString(i));
            }
            functionReturnsList.add(functionReturnNode);
        }
        return functionReturnsList;
    }
}
