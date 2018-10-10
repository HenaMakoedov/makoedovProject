package db.loader.sorters;

import db.constants.DBObjects;
import treerealization.Node;

import java.util.Comparator;
import java.util.List;

/**
 * Created by Makoiedov.H on 10/18/2017.
 */

/**
 * method sorts schema children nodes
 * in the order in the schemaElements enum
 */
@SorterAnnotation(type = DBObjects.ObjectNames.SCHEMA)
public class SchemaElementsSorter implements DBObjectSorter {
    @Override
    public void sortNode(Node node) {
        List<Node> schemaElementsList = node.getChildren();
        Comparator<Node> schemaElementsComparator = (o1, o2) -> {

            SchemaElements element1 = SchemaElements.valueOf(o1.getName());
            SchemaElements element2 = SchemaElements.valueOf(o2.getName());

            if (element1.ordinal() < element2.ordinal()) {
                return -1;
            } else if (element1.ordinal() > element2.ordinal()) {
                return 1;
            } else {
                return 0;
            }
        };
        schemaElementsList.sort(schemaElementsComparator);
    }
}
