package db.printer.mysqlprinters.sorters;

import db.constants.DBObjects;
import treerealization.Node;
import treerealization.Tree;

import java.util.List;

@SorterAnnotation
public class TriggerSorter implements DBObjectSorter {
    @Override
    public void sortTree(Tree tree) {
        List<Node> tablesList = tree.nameDepthSearch(DBObjects.ObjectNames.TABLES).getChildren();


        //insert sort
        for(int i = 0; i < tablesList.size(); i++) {
            for(int j = i + 1; j < tablesList.size(); j++) {
                List<Node> triggersList = tablesList.get(i).nameDepthSearch(DBObjects.ObjectNames.TRIGGERS).getChildren();
                for(Node key : triggersList) {
                    if (key.getAttributes().get(DBObjects.AttributeNames.EVENT_OBJECT_TABLE).
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
