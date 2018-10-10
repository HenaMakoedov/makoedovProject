package db;

import db.loader.*;
import db.constants.DBObjects;
import treerealization.Node;
import treerealization.Tree;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Makoiedov.H on 10/2/2017.
 */
public class MainLoader {
    private Connection connection;

    public MainLoader(String URL, String user, String password, DBConnectionStrategy strategy) throws SQLException, ClassNotFoundException {
        DBManager dbManager = new DBManager();
        dbManager.setStrategy(strategy);
        connection = dbManager.getConnection(URL, user, password);
    }

    public Tree getDB(String schemaName) throws SQLException {
            //get all database object
            Node schema = new SchemaLoader(connection).loadAllNodes(schemaName).get(0);
            List<Node> tables = new TablesLoader(connection).loadAllNodes(schemaName);
            List<Node> views = new ViewsLoader(connection).loadAllNodes(schemaName);
            List<Node> procedures = new ProceduresLoader(connection).loadAllNodes(schemaName);
            List<Node> functions = new FunctionsLoader(connection).loadAllNodes(schemaName);
            List<Node> columns = new ColumnsLoader(connection).loadAllNodes(schemaName);
            List<Node> primaryKeys = new PrimaryKeysLoader(connection).loadAllNodes(schemaName);
            List<Node> indexes = new IndexesLoader(connection).loadAllNodes(schemaName);
            List<Node> foreignKeys = new ForeignKeysLoader(connection).loadAllNodes(schemaName);
            List<Node> triggers = new TriggersLoader(connection).loadAllNodes(schemaName);
            List<Node> procedureParameters = new ProcedureParametersLoader(connection).loadAllNodes(schemaName);
            List<Node> functionParameters = new FunctionParametersLoader(connection).loadAllNodes(schemaName);
            List<Node> functionReturns = new FunctionReturnLoader(connection).loadAllNodes(schemaName);


            //add first level db objects
            Node tablesNode = new Node(DBObjects.ObjectNames.TABLES);
            schema.getChildren().add(tablesNode);
            tablesNode.getChildren().addAll(tables);

            Node viewsNode = new Node(DBObjects.ObjectNames.VIEWS);
            schema.getChildren().add(viewsNode);
            viewsNode.getChildren().addAll(views);

            Node proceduresNode = new Node(DBObjects.ObjectNames.PROCEDURES);
            schema.getChildren().add(proceduresNode);
            proceduresNode.getChildren().addAll(procedures);

            Node functionsNode = new Node(DBObjects.ObjectNames.FUNCTIONS);
            schema.getChildren().add(functionsNode);
            functionsNode.getChildren().addAll(functions);

            ///////////////////////////////////////////////////////////////////
            addTablesCategories(tables);
            addProceduresCategories(procedures);
            addFunctionsCategories(functions);

            scatterColumns(tables, columns);
            scatterPrimaryKeys(tables, primaryKeys);
            scatterIndexes(tables, indexes);
            scatterForeignKeys(tables, foreignKeys);
            scatterTriggers(tables, triggers);
            deleteDuplicateFk(tables);

            scatterProceduresParameters(procedures, procedureParameters);
            scatterFunctionReturns(functions, functionReturns);
            scatterFunctionParameters(functions, functionParameters);


            return new Tree(schema);
        }




    private void deleteDuplicateFk(List<Node> tables) {
        for(Node table: tables) {
            List<Node> foreignKeysList = table.nameDepthSearch(DBObjects.ObjectNames.FOREIGN_KEYS).getChildren();
            for(int i = 0; i < foreignKeysList.size(); i++) {
                for(int j = i + 1; j < foreignKeysList.size(); j++) {
                    if (foreignKeysList.get(i).getAttributes().get(DBObjects.AttributeNames.NAME)
                            .equals(foreignKeysList.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)) &&
                            foreignKeysList.get(i).getAttributes().get(DBObjects.AttributeNames.COLUMN_NAME)
                            .equals(foreignKeysList.get(j).getAttributes().get(DBObjects.AttributeNames.COLUMN_NAME))) {
                        foreignKeysList.remove(j);
                        j--;
                    }
                }
            }


        }

    }

    public Node getAllTables(String schemaName) throws SQLException {
        Node root = new Node(DBObjects.ObjectNames.TABLES);

        List<Node> tables = new TablesLoader(connection).loadAllNodes(schemaName);
        root.getChildren().addAll(tables);

        List<Node> columns = new ColumnsLoader(connection).loadAllNodes(schemaName);
        List<Node> primaryKeys = new PrimaryKeysLoader(connection).loadAllNodes(schemaName);
        List<Node> indexes = new IndexesLoader(connection).loadAllNodes(schemaName);
        List<Node> foreignKeys = new ForeignKeysLoader(connection).loadAllNodes(schemaName);
        List<Node> triggers = new TriggersLoader(connection).loadAllNodes(schemaName);
        addTablesCategories(tables);
        scatterColumns(tables, columns);
        scatterPrimaryKeys(tables, primaryKeys);
        scatterIndexes(tables, indexes);
        scatterForeignKeys(tables, foreignKeys);
        scatterTriggers(tables, triggers);
        return root;
    }

    public Node getAllViews(String schemaName) throws SQLException {
        Node root = new Node(DBObjects.ObjectNames.VIEWS);
        List<Node> views = new ViewsLoader(connection).loadAllNodes(schemaName);
        root.getChildren().addAll(views);
        return root;
    }

    public Node getAllProcedures(String schemaName) throws SQLException {
        Node root = new Node(DBObjects.ObjectNames.PROCEDURES);
        List<Node> procedures = new ProceduresLoader(connection).loadAllNodes(schemaName);
        root.getChildren().addAll(procedures);

        List<Node> procedureParameters = new ProcedureParametersLoader(connection).loadAllNodes(schemaName);
        scatterProceduresParameters(procedures, procedureParameters);
        return root;
    }

    public Node getAllFunctions(String schemaName) throws SQLException {
        Node root = new Node(DBObjects.ObjectNames.FUNCTIONS);
        List<Node> functions = new FunctionsLoader(connection).loadAllNodes(schemaName);
        root.getChildren().addAll(functions);

        List<Node> functionParameters = new FunctionParametersLoader(connection).loadAllNodes(schemaName);
        List<Node> functionReturns = new FunctionReturnLoader(connection).loadAllNodes(schemaName);
        scatterFunctionParameters(functions, functionParameters);
        scatterFunctionReturns(functions, functionReturns);
        return root;
    }

    private void addTablesCategories(List<Node> tables) {
        for(Node table: tables) {
            table.getChildren().add(new Node(DBObjects.ObjectNames.COLUMNS));
            table.getChildren().add(new Node(DBObjects.ObjectNames.PRIMARY_KEYS));
            table.getChildren().add(new Node(DBObjects.ObjectNames.INDEXES));
            table.getChildren().add(new Node(DBObjects.ObjectNames.FOREIGN_KEYS));
            table.getChildren().add(new Node(DBObjects.ObjectNames.TRIGGERS));
        }
    }

    private void addProceduresCategories(List<Node> procedures) {
        for(Node procedure: procedures) {
            procedure.getChildren().add(new Node(DBObjects.ObjectNames.PROCEDURE_PARAMETERS));
        }
    }

    private void addFunctionsCategories(List<Node> functions) {
        for(Node function: functions) {
            function.getChildren().add(new Node(DBObjects.ObjectNames.FUNCTION_RETURNS));
            function.getChildren().add(new Node(DBObjects.ObjectNames.FUNCTION_PARAMETERS));
        }
    }

    private void scatterColumns(List<Node> tables, List<Node> columns) {
        label:
        for(int i = 0; i < columns.size(); i++) {
            for(int j = 0; j < tables.size(); j++) {
                if (tables.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(columns.get(i).getAttributes().get(DBObjects.AttributeNames.TABLE_NAME))) {
                    Node columnsNode = tables.get(j).nameWideSearch(DBObjects.ObjectNames.COLUMNS);
                    columnsNode.getChildren().add(columns.get(i));
                    continue label;
                }
            }
        }
    }

    private void scatterPrimaryKeys(List<Node> tables, List<Node> primaryKeys) {
        label:
        for(int i = 0; i < primaryKeys.size(); i++) {
            for (int j = 0; j < tables.size(); j++) {
                if (tables.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(primaryKeys.get(i).getAttributes().get(DBObjects.AttributeNames.TABLE_NAME))) {
                    Node primaryKeysNode = tables.get(j).nameWideSearch(DBObjects.ObjectNames.PRIMARY_KEYS);
                    primaryKeysNode.getChildren().add(primaryKeys.get(i));
                    continue label;
                }
            }
        }
    }

    private void scatterIndexes(List<Node> tables, List<Node> indexes) {
        label:
        for(int i = 0; i < indexes.size(); i++) {
            for(int j = 0; j < tables.size(); j++) {
                if (tables.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(indexes.get(i).getAttributes().get(DBObjects.AttributeNames.TABLE_NAME))) {
                    Node indexesNode = tables.get(j).nameWideSearch(DBObjects.ObjectNames.INDEXES);
                    indexesNode.getChildren().add(indexes.get(i));
                    continue label;
                }
            }
        }
    }

    private void scatterForeignKeys(List<Node> tables, List<Node> foreignKeys) {
        label:
        for(int i = 0; i < foreignKeys.size(); i++) {
            for(int j = 0; j < tables.size(); j++) {
                if (tables.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(foreignKeys.get(i).getAttributes().get(DBObjects.AttributeNames.TABLE_NAME))) {
                    Node foreignKeysNode = tables.get(j).nameWideSearch(DBObjects.ObjectNames.FOREIGN_KEYS);
                    foreignKeysNode.getChildren().add(foreignKeys.get(i));
                    continue label;
                }
            }
        }
    }

    private void scatterTriggers(List<Node> tables, List<Node> triggers) {
        label:
        for(int i = 0; i < triggers.size(); i++) {
            for(int j = 0; j < tables.size(); j++) {
                if (tables.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(triggers.get(i).getAttributes().get(DBObjects.AttributeNames.EVENT_OBJECT_TABLE))) {
                    Node triggersNode = tables.get(j).nameWideSearch(DBObjects.ObjectNames.TRIGGERS);
                    triggersNode.getChildren().add(triggers.get(i));
                    continue label;
                }
            }
        }

    }

    private void scatterProceduresParameters(List<Node> procedures, List<Node> parameters) {
        label:
        for(int i = 0; i < parameters.size(); i++) {
            for(int j = 0; j < procedures.size(); j++) {
                if (procedures.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(parameters.get(i).getAttributes().get(DBObjects.AttributeNames.SPECIFIC_NAME))) {
                    Node foreignKeysNode = procedures.get(j).nameWideSearch(DBObjects.ObjectNames.PROCEDURE_PARAMETERS);
                    foreignKeysNode.getChildren().add(parameters.get(i));
                    continue label;
                }
            }
        }
    }

    private void scatterFunctionParameters(List<Node> functions, List<Node> parameters) {
        label:
        for(int i = 0; i < parameters.size(); i++) {
            for(int j = 0; j < functions.size(); j++) {
                if (functions.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(parameters.get(i).getAttributes().get(DBObjects.AttributeNames.SPECIFIC_NAME))) {
                    Node foreignKeysNode = functions.get(j).nameWideSearch(DBObjects.ObjectNames.FUNCTION_PARAMETERS);
                    foreignKeysNode.getChildren().add(parameters.get(i));
                    continue label;
                }
            }
        }
    }

    private void scatterFunctionReturns(List<Node> functions, List<Node> returns) {
        label:
        for(int i = 0; i < returns.size(); i++) {
            for(int j = 0; j < functions.size(); j++) {
                if (functions.get(j).getAttributes().get(DBObjects.AttributeNames.NAME)
                        .equals(returns.get(i).getAttributes().get(DBObjects.AttributeNames.SPECIFIC_NAME))) {
                    Node foreignKeysNode = functions.get(j).nameWideSearch(DBObjects.ObjectNames.FUNCTION_RETURNS);
                    foreignKeysNode.getChildren().add(returns.get(i));
                    continue label;
                }
            }
        }

    }

}
