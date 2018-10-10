package db.printer;


import db.loader.Loader;
import treerealization.Node;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Makoiedov.H on 10/6/2017.
 */
public abstract class AbstractPrinter implements Printer {
    protected Map<String, Printer> signaturePrinters;
    public AbstractPrinter(){
        signaturePrinters = new HashMap<>();
    }

    /**
     * Method print sql creating query for specific node
     * @param node
     * @return sql query
     */
    @Override
    public abstract String printNode(Node node);


    /**
     * Method print signature sql query for specific node
     * @param node
     * @return sql code which contains signature of full sql query
     */
    protected String printSignature(Node node) {
        return "";
    }



    public final void addSignaturePrinter(String type, AbstractPrinter printer) {
        signaturePrinters.put(type, printer);
    }




}
