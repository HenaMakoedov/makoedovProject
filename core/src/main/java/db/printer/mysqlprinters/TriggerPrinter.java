package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.TRIGGER)
public class TriggerPrinter extends AbstractPrinter {
    public TriggerPrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        String statement = node.getAttributes().get(DBObjects.AttributeNames.ACTION_STATEMENT);
        statement = statement.replace("&#xa", System.lineSeparator());
        statement = statement.replace("\n", " \n ");
        StringBuilder sb = new StringBuilder();
        sb.append("DELIMITER $$ ")
                .append(System.lineSeparator())
                .append(" CREATE DEFINER = `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.DEFINER).replaceFirst("@", "`@`"))
                .append("` TRIGGER `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("` ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.ACTION_TIMING))
                .append(" ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.EVENT_MANIPULATION))
                .append(" ON `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.EVENT_OBJECT_TABLE))
                .append("`" + System.lineSeparator())
                .append("FOR EACH ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.ACTION_ORIENTATION))
                .append(" ")
                .append(statement)
                .append(" $$ ")
                .append(System.lineSeparator())
                .append(" DELIMITER ");
        return sb.toString();
    }


}
