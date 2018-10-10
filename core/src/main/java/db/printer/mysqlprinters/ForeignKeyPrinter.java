package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.Printer;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.FOREIGN_KEY, parent = TablePrinter.class)
public class ForeignKeyPrinter extends AbstractPrinter {
    public ForeignKeyPrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        StringBuilder sb = new StringBuilder();
        sb.append("CONSTRAINT `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("` FOREIGN KEY (`")
                .append(node.getAttributes().get(DBObjects.AttributeNames.COLUMN_NAME))
                .append("`) REFERENCES `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.REFERENCED_TABLE_NAME))
                .append("` (`")
                .append(node.getAttributes().get(DBObjects.AttributeNames.REFERENCED_COLUMN_NAME))
                .append("`) ")
                .append(" ON DELETE ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.DELETE_RULE))
                .append(" ON UPDATE ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.UPDATE_RULE));

        return sb.toString();
    }


}
