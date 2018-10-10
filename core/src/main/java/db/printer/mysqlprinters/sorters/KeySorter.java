package db.printer.mysqlprinters.sorters;

import db.constants.DBObjects;
import treerealization.Node;
import treerealization.Tree;

import java.util.List;

@SorterAnnotation
public class KeySorter implements DBObjectSorter {
    public void sortTree(Tree tree) {
        List<Node> tablesList = tree.nameDepthSearch(DBObjects.ObjectNames.TABLES).getChildren();

        //insert sort
        for(int i = 0; i < tablesList.size(); i++) {
            for(int j = i + 1; j < tablesList.size(); j++) {
                List<Node> keysList = tablesList.get(i).nameDepthSearch(DBObjects.ObjectNames.FOREIGN_KEYS).getChildren();
                for(Node key : keysList) {
                    if (key.getAttributes().get(DBObjects.AttributeNames.REFERENCED_TABLE_NAME).
                            equals(tablesList.get(j).getAttributes().get(DBObjects.AttributeNames.NAME))) {
                        Node tmp = tablesList.get(i);
                        tablesList.set(i, tablesList.get(j));
                        tablesList.set(j, tmp);
                        continue;
                    }
                }
            }
        }
    }
}
