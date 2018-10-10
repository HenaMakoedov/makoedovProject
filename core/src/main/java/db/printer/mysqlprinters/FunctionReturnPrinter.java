package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/25/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.FUNCTION_RETURN, parent = FunctionPrinter.class)
public class FunctionReturnPrinter extends AbstractPrinter {
    @Override
    public String printNode (Node node) {
        StringBuilder sb = new StringBuilder();

        sb.append(" RETURNS ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.DTD_IDENTIFIER));
        return sb.toString();

    }
}
