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
  *Created by Makoiedov.H on 9/27/2017.
 */


@LoaderAnnotation(type = DBObjects.ObjectNames.FOREIGN_KEY, parent = TablesLoader.class)
public class ForeignKeysLoader extends AbstractLoader {
    public ForeignKeysLoader() {
    }

    public ForeignKeysLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement foreignKeyStatement = connection.prepareStatement(Queries.FOREIGN_KEYS_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            foreignKeyStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet foreignKeyResultSet = foreignKeyStatement.executeQuery();

        Node foreignKeyNode = new Node(DBObjects.ObjectNames.FOREIGN_KEY);
        while (foreignKeyResultSet.next()) {
            for(int i = 1; i <= foreignKeyResultSet.getMetaData().getColumnCount(); i++) {
                foreignKeyNode.getAttributes().put(foreignKeyResultSet.getMetaData().getColumnLabel(i), foreignKeyResultSet.getString(i));
            }
        }
        return foreignKeyNode;
    }



    @Override
    public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        //load foreign key nodes with name
        parent.getChildren().add(new Node(DBObjects.ObjectNames.FOREIGN_KEYS));
        List<Node> foreignKeyList = parent.nameDepthSearch(DBObjects.ObjectNames.FOREIGN_KEYS).getChildren();
        for(String foreignKeyName : getForeignKeysNames(parameters)) {
            Node foreignKeyNode = new Node(DBObjects.ObjectNames.FOREIGN_KEY);
            foreignKeyNode.getAttributes().put(DBObjects.AttributeNames.NAME, foreignKeyName);
            foreignKeyList.add(foreignKeyNode);
        }

    }


    private List<String> getForeignKeysNames(List<String> parents) throws SQLException {
        PreparedStatement foreignKeysStatement = connection.prepareStatement(Queries.FOREIGN_KEYS_NAME_QUERY);
        for(int i = 0; i < parents.size(); i++) {
            foreignKeysStatement.setString(i + 1, parents.get(i));
        }
        ResultSet foreignKeysResultSet = foreignKeysStatement.executeQuery();

        List<String> foreignKeysNames = new ArrayList<>();
        while (foreignKeysResultSet.next()) {
            foreignKeysNames.add(foreignKeysResultSet.getString(1));
        }
        return foreignKeysNames;

    }

     @Override
     public List<Node> loadAllNodes(String schemaName) throws SQLException {
         PreparedStatement foreignKeysStatement = connection.prepareStatement(Queries.ALL_FOREIGN_KEYS_QUERY);
         foreignKeysStatement.setString(1, schemaName);

         ResultSet FKResultSet = foreignKeysStatement.executeQuery();
         List<Node> FKList = new ArrayList<>();
         while (FKResultSet.next()) {
             Node FKNode = new Node(DBObjects.ObjectNames.FOREIGN_KEY);
             for(int i = 1; i <= FKResultSet.getMetaData().getColumnCount(); i++) {
                 FKNode.getAttributes().put(FKResultSet.getMetaData().getColumnLabel(i), FKResultSet.getString(i));
             }
             FKList.add(FKNode);
         }
         return FKList;
     }
 }
