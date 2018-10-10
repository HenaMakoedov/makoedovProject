package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/23/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.FUNCTION_PARAMETER, parent = FunctionPrinter.class)
public class FunctionParameterPrinter extends AbstractPrinter {
    @Override
    public String printNode(Node node) {
        StringBuilder sb = new StringBuilder();

        sb.append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append(" ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.DTD_IDENTIFIER));
        return sb.toString();

    }
}
