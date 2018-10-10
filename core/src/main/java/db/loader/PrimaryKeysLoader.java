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
 @LoaderAnnotation(type = DBObjects.ObjectNames.PRIMARY_KEY, parent = TablesLoader.class)
public class PrimaryKeysLoader extends AbstractLoader {
    public PrimaryKeysLoader() {
    }

    public PrimaryKeysLoader(Connection connection) {
        super(connection);
    }

    @Override
    public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
        PreparedStatement primaryKeyStatement = connection.prepareStatement(Queries.PRIMARY_KEYS_QUERY);
        for(int i = 0; i < parameters.size(); i++) {
            primaryKeyStatement.setString(i + 1, parameters.get(i));
        }
        ResultSet primaryKeyResultSet = primaryKeyStatement.executeQuery();

        Node primaryKeyNode = new Node(DBObjects.ObjectNames.PRIMARY_KEY);
        while (primaryKeyResultSet.next()) {
            for(int i = 1; i <= primaryKeyResultSet.getMetaData().getColumnCount(); i++) {
                primaryKeyNode.getAttributes().put(primaryKeyResultSet.getMetaData().getColumnLabel(i), primaryKeyResultSet.getString(i));
            }
        }
        return primaryKeyNode;
    }

     @Override
     public void notifyParent(Node parent, List<String> parameters) throws SQLException {
         //load primary key nodes with name
         parent.getChildren().add(new Node(DBObjects.ObjectNames.PRIMARY_KEYS));
         List<Node> primaryKeyList = parent.nameDepthSearch(DBObjects.ObjectNames.PRIMARY_KEYS).getChildren();
         for(String primaryKeyName: getPrimaryKeysNames(parameters)) {
             Node primaryKeyNode = new Node(DBObjects.ObjectNames.PRIMARY_KEY);
             primaryKeyNode.getAttributes().put(DBObjects.AttributeNames.NAME, primaryKeyName);
             primaryKeyList.add(primaryKeyNode);
         }

     }

     private List<String> getPrimaryKeysNames(List<String> parents) throws SQLException {
         PreparedStatement primaryKeysStatement = connection.prepareStatement(Queries.PRIMARY_KEYS_NAME_QUERY);
         for(int i = 0; i < parents.size(); i++) {
             primaryKeysStatement.setString(i + 1, parents.get(i));
         }
         ResultSet primaryKeysResultSet = primaryKeysStatement.executeQuery();

         List<String> primaryKeysNames = new ArrayList<>();
         while (primaryKeysResultSet.next()) {
             primaryKeysNames.add(primaryKeysResultSet.getString(1));
         }
         return primaryKeysNames;
     }

     @Override
     public List<Node> loadAllNodes(String schemaName) throws SQLException {
         PreparedStatement PKStatement = connection.prepareStatement(Queries.ALL_PRIMARY_KEYS_QUERY);
         PKStatement.setString(1, schemaName);

         ResultSet PKResultSet = PKStatement.executeQuery();
         List<Node> PKList = new ArrayList<>();
         while (PKResultSet.next()) {
             Node PKNode = new Node(DBObjects.ObjectNames.PRIMARY_KEY);
             for(int i = 1; i <= PKResultSet.getMetaData().getColumnCount(); i++) {
                 PKNode.getAttributes().put(PKResultSet.getMetaData().getColumnLabel(i), PKResultSet.getString(i));
             }
             PKList.add(PKNode);
         }
         return PKList;
     }


 }
