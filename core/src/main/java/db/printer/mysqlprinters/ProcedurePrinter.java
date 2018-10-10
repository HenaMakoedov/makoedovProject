package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.Printer;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.PROCEDURE)
public class ProcedurePrinter extends AbstractPrinter {
    public ProcedurePrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        StringBuilder sb = new StringBuilder();
        String body = node.getAttributes().get(DBObjects.AttributeNames.ROUTINE_DEFINITION);
        body.replace("&#xa;", System.lineSeparator());
        body.replace("&#x9;", " ");
        body = body.replace("\n", " \n ");
        String deterministic = node.getAttributes().get(DBObjects.AttributeNames.IS_DETERMINISTIC).equals("YES") ?
                "DETERMINISTIC" + System.lineSeparator() : "";



        sb.append("DELIMITER $$ ")
                .append(System.lineSeparator())
                .append(" CREATE DEFINER = `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.DEFINER).replace("@", "`@`"))
                .append("` ")
                .append("PROCEDURE `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("` ")
                .append(printSignature(node) + System.lineSeparator() + " ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.SQL_DATA_ACCESS))
                .append(System.lineSeparator())
                .append(deterministic)
                .append(System.lineSeparator())
                .append(body)
                .append(" $$ ")
                .append(System.lineSeparator())
                .append(" DELIMITER ");

        return sb.toString();
    }

    @Override
    protected String printSignature(Node node) {
        StringBuilder sb = new StringBuilder();
        sb.append("(");
        buildSignature(sb, node);
        if (sb.length() > 1) {
            sb.replace(sb.length() - 1, sb.length(), "");
        }
        sb.append(")");
        return sb.toString();
    }

    private void buildSignature(StringBuilder sb, Node node) {
        Printer printer = signaturePrinters.get(node.getName());
        if (printer != null) {
            sb.append(printer.printNode(node) + ",");
        }

        for(int i = 0; i < node.getChildren().size(); i++) {
            buildSignature(sb, node.getChildren().get(i));
        }

    }
}
