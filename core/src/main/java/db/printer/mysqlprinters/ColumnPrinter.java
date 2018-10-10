package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/*
 * Created by Makoiedov.H on 10/9/2017.
 */

@PrinterAnnotation(type = DBObjects.ObjectNames.COLUMN, parent = TablePrinter.class)
public class ColumnPrinter extends AbstractPrinter {

    public ColumnPrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        String nullable = node.getAttributes().get(DBObjects.AttributeNames.IS_NULLABLE).equals("YES") ?
                " DEFAULT NULL" : " NOT NULL";

        String columnDefault;

        if (node.getAttributes().get(DBObjects.AttributeNames.COLUMN_DEFAULT) == null) {
            columnDefault = "";
        } else {
            columnDefault = "DEFAULT ";
            if (node.getAttributes().get(DBObjects.AttributeNames.COLUMN_TYPE).startsWith("enum")) {
                columnDefault = " " + columnDefault + "'" + node.getAttributes().get(DBObjects.AttributeNames.COLUMN_DEFAULT) + "'";
            }   else {
                columnDefault = " " + columnDefault + node.getAttributes().get(DBObjects.AttributeNames.COLUMN_DEFAULT);
            }
        }

        String extra = node.getAttributes().get(DBObjects.AttributeNames.EXTRA).equals("") ||
                node.getAttributes().get(DBObjects.AttributeNames.EXTRA) == null ?
                "" : " " + node.getAttributes().get(DBObjects.AttributeNames.EXTRA);


        StringBuilder sb = new StringBuilder();
        sb.append("`")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("` ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.COLUMN_TYPE))
                .append(" ")
                .append(nullable)
                .append(columnDefault)
                .append(extra);
        return sb.toString();
    }


}
