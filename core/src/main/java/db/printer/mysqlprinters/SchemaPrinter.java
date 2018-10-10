package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.SCHEMA)
public class SchemaPrinter extends AbstractPrinter {
    public SchemaPrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        StringBuilder sb = new StringBuilder();
        sb.append("CREATE DATABASE `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("`; " + System.lineSeparator())
                .append(" ")
                .append("USE " )
                .append("`")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("`");
        return sb.toString();
    }


}
