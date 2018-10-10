package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.VIEW)
public class ViewPrinter extends AbstractPrinter {

    public ViewPrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        String algorithm = node.getAttributes().get(DBObjects.AttributeNames.ALGORITHM).equals("NONE") ?
                "UNDEFINED" : node.getAttributes().get(DBObjects.AttributeNames.ALGORITHM);
        String definition = node.getAttributes().get(DBObjects.AttributeNames.VIEW_DEFINITION);

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE\n")
                .append("ALGORITHM = ")
                .append(algorithm + System.lineSeparator())
                .append("DEFINER = `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.DEFINER).replace("@", "`@`"))
                .append("` " + System.lineSeparator() + " ")
                .append("SQL SECURITY ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.SECURITY_TYPE))
                .append(System.lineSeparator())
                .append("VIEW `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("` AS" + System.lineSeparator())
                .append(definition);

        return sb.toString();
    }

}
