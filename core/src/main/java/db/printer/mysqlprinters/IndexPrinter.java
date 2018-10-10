package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.INDEX, parent = TablePrinter.class)
public class IndexPrinter extends AbstractPrinter {
    public IndexPrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        String type = node.getAttributes().get(DBObjects.AttributeNames.INDEX_TYPE).equals("BTREE") ?
                "" : node.getAttributes().get(DBObjects.AttributeNames.INDEX_TYPE) + " ";
        String unique = node.getAttributes().get(DBObjects.AttributeNames.NON_UNIQUE).equals("0") ?
                "UNIQUE " : "";

        StringBuilder sb = new StringBuilder();

        sb.append(unique)
                .append(type)
                .append("KEY `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("` (")
                .append(node.getAttributes().get(DBObjects.AttributeNames.COLUMNS))
                .append(")");

        return sb.toString();
    }


}
