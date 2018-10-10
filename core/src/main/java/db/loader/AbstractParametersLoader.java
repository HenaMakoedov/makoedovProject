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
 * Created by Makoiedov.H on 11/24/2017.
 */
public class AbstractParametersLoader extends AbstractLoader {
    protected String parameterRoutineName;
    protected String parametersRoutineName;
    public AbstractParametersLoader(Connection connection) {
        super(connection);
    }

    public AbstractParametersLoader() {
        super();
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement parametersStatement = connection.prepareStatement(Queries.PARAMETERS_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            parametersStatement.setString(i + 1, parameters.get(i));
        }

        ResultSet parameterResultSet = parametersStatement.executeQuery();
        Node parameterNode = new Node(parameterRoutineName);
        while (parameterResultSet.next()) {
            for(int i = 1; i <= parameterResultSet.getMetaData().getColumnCount(); i++) {
                parameterNode.getAttributes().put(parameterResultSet.getMetaData().getColumnLabel(i), parameterResultSet.getString(i));
            }
        }
        return parameterNode;
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        parent.getChildren().add(new Node(parametersRoutineName));
        List<Node> parametersList = parent.nameDepthSearch(parametersRoutineName).getChildren();
        for(String parameterName : getParametersNames(parameters)) {
            Node parameterNode = new Node(parameterRoutineName);
            parameterNode.getAttributes().put(DBObjects.AttributeNames.NAME, parameterName);
            parametersList.add(parameterNode);
        }
    }

    private List<String> getParametersNames(List<String> parents) throws SQLException {
        PreparedStatement parametersStatement = connection.prepareStatement(Queries.PARAMETERS_NAME_QUERY);
        for(int i = 0; i < parents.size(); i++) {
            parametersStatement.setString(i + 1, parents.get(i));
        }
        ResultSet ParametersResultSet = parametersStatement.executeQuery();

        List<String> parametersNames = new ArrayList<>();
        while (ParametersResultSet.next()) {
                parametersNames.add(ParametersResultSet.getString(1));
        }
        return parametersNames;
    }


    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        PreparedStatement functionParameterStatement = connection.prepareStatement(Queries.ALL_PARAMETERS_QUERY);
        functionParameterStatement.setString(1, schemaName);

        ResultSet functionParametersResultSet = functionParameterStatement.executeQuery();
        List<Node> functionParametersList = new ArrayList<>();
        while (functionParametersResultSet.next()) {
            Node functionParametersNode = new Node(parameterRoutineName);
            for(int i = 1; i <= functionParametersResultSet.getMetaData().getColumnCount(); i++) {
                functionParametersNode.getAttributes().put(functionParametersResultSet.getMetaData().getColumnLabel(i), functionParametersResultSet.getString(i));
            }
            functionParametersList.add(functionParametersNode);
        }
        return functionParametersList;
    }
}
