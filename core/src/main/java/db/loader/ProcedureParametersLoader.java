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
 * Created by Makoiedov.H on 10/23/2017.
 */

@LoaderAnnotation(type = DBObjects.ObjectNames.PROCEDURE_PARAMETER, parent = ProceduresLoader.class)
public class ProcedureParametersLoader extends AbstractParametersLoader {

    public ProcedureParametersLoader(Connection connection) {
        super(connection);
        this.parameterRoutineName = DBObjects.ObjectNames.PROCEDURE_PARAMETER;
        this.parametersRoutineName = DBObjects.ObjectNames.PROCEDURE_PARAMETERS;
    }

    public ProcedureParametersLoader() {
        super();
        this.parameterRoutineName = DBObjects.ObjectNames.PROCEDURE_PARAMETER;
        this.parametersRoutineName = DBObjects.ObjectNames.PROCEDURE_PARAMETERS;
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        return super.loadNodeWithoutChildren(parameters);
    }

    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        super.notifyParent(parent, parameters);
    }

    @Override
    public List<Node> loadAllNodes(String schemaName) throws SQLException {
        return super.loadAllNodes(schemaName);
    }
}
