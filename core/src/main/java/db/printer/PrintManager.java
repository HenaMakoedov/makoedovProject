package db.printer;

import db.utils.ReflectionUtils;
import treerealization.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Makoiedov.H on 10/6/2017.
 */


public class PrintManager {
    private static PrintManager instance;
    private Map<String, Printer> printers;

    private PrintManager(){
        printers = new HashMap<>();
        registerPrinters();
    }

    public static PrintManager getInstance() {
        if (instance == null) {
            instance = new PrintManager();

        }
        return instance;
    }

    public String printNode(Node node) {
        Printer printer = printers.get(node.getName());
        if (printer != null) {
            return printer.printNode(node);
        }
        return "";
    }

    /**
     * This method goes through the folder with printers and
     * collects printers marked with annotations in map of printManager
     */
    private void registerPrinters() {
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getAnnotatedClasses(PrinterAnnotation.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        if(classes != null && classes.size() != 0) {
            for(Class aClass : classes) {
                try {
                    Class<?> clazz = aClass;
                    if (clazz.isAnnotationPresent(PrinterAnnotation.class) &&
                            clazz.getAnnotation(PrinterAnnotation.class).parent() == PrinterAnnotation.NULL.class) {
                        AbstractPrinter printer = (AbstractPrinter) clazz.newInstance();
                        registerSignaturePrinters(printer);
                        printers.put(clazz.getAnnotation(PrinterAnnotation.class).type(), printer);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * recursive method goes through the folder with printers and
     * collects children printers marked with annotations in map of specific printer
     */
    private void registerSignaturePrinters(AbstractPrinter printer) {
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getAnnotatedClasses(PrinterAnnotation.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(classes != null && classes.size() != 0) {
            for(Class aClass : classes) {
                try {
                    Class<?> clazz = aClass;
                    if (clazz.isAnnotationPresent(PrinterAnnotation.class)
                            && clazz.getAnnotation(PrinterAnnotation.class).parent() == printer.getClass()) {
                        AbstractPrinter signaturePrinter = (AbstractPrinter)clazz.newInstance();
                        printer.addSignaturePrinter(clazz.getAnnotation(PrinterAnnotation.class).type(), signaturePrinter);
                        registerSignaturePrinters(signaturePrinter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
