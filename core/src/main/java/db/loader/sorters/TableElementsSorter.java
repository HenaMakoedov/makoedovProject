package db.loader.sorters;

import db.constants.DBObjects;
import treerealization.Node;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Makoiedov.H on 10/18/2017.
 */

/**
 * method sorts table children nodes
 * in the order in the TableElements enum
 */

@SorterAnnotation(type = DBObjects.ObjectNames.TABLE)
public class TableElementsSorter implements DBObjectSorter {
    @Override
    public void sortNode(Node node) {
        List<Node> tableElementsList = node.getChildren();
        Comparator<Node> tableElementsComparator = (o1, o2) -> {
                TableElements element1 = TableElements.valueOf(o1.getName());
                TableElements element2 = TableElements.valueOf(o2.getName());
                if (element1.ordinal() < element2.ordinal()) {
                    return -1;
                } else if (element1.ordinal() > element2.ordinal()) {
                    return 1;
                } else {
                    return 0;
                }

        };

        tableElementsList.sort(tableElementsComparator);
    }
}
