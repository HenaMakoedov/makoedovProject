package db.printer.mysqlprinters;

import db.constants.DBObjects;
import db.printer.AbstractPrinter;
import db.printer.PrinterAnnotation;
import treerealization.Node;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */
@PrinterAnnotation(type = DBObjects.ObjectNames.PRIMARY_KEYS, parent = TablePrinter.class)
public class PrimaryKeyPrinter extends AbstractPrinter {
    public PrimaryKeyPrinter() {
        super();
    }

    @Override
    public String printNode(Node node) {
        if (node.getChildren().size() == 0) return "";

        StringBuilder sb = new StringBuilder();
        StringBuilder keysBuilder = new StringBuilder();
        for(int i = 0; i < node.getChildren().size(); i++) {
            keysBuilder.append("`")
                    .append(node.getChildren().get(i).getAttributes().get(DBObjects.AttributeNames.NAME))
                    .append("`,");
        }
            keysBuilder.replace(keysBuilder.length() - 1, keysBuilder.length(), "");

        sb.append("PRIMARY KEY (")
                .append(keysBuilder)
                .append(")");

        return sb.toString();
    }


}
