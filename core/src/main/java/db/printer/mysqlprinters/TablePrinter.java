package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.Printer;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.TABLE)
public class TablePrinter extends AbstractPrinter {
    public TablePrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {


        StringBuilder sb = new StringBuilder();
        String signature = printSignature(node);
        String defaultCharset = node.getAttributes().get(DBObjects.AttributeNames.TABLE_COLLATION).contains("latin1") ? "latin1" : "utf8";

        sb.append("CREATE TABLE IF NOT EXISTS `")
                .append(node.getAttributes().get(DBObjects.AttributeNames.NAME))
                .append("` (" + System.lineSeparator() + " ")
                .append(signature)
                .append(") ENGINE = ")
                .append(node.getAttributes().get(DBObjects.AttributeNames.ENGINE))
                .append(" DEFAULT CHARSET = ")
                .append(defaultCharset);
        return sb.toString();
    }

    @Override
    protected String printSignature(Node node) {
        StringBuilder builder = new StringBuilder();
        buildSignature(builder, node);
        builder.replace(builder.length() - 5, builder.length() - 3, "");
        return builder.toString();
    }

    private void buildSignature(StringBuilder builder, Node node) {
        Printer printer = signaturePrinters.get(node.getName());
        if (printer != null) {
            if (printer.printNode(node).equals("")) return;
            builder.append(printer.printNode(node))
                    .append(",")
                    .append(" ")
                    .append(System.lineSeparator())
                    .append(" ");
        }

        for(Node childNode : node.getChildren()) {
            buildSignature(builder, childNode);
        }
    }


}
