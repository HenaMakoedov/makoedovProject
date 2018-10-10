package db;



import db.printer.AbstractPrinter;
import db.printer.PrintManager;
import db.printer.PrinterAnnotation;
import db.printer.mysqlprinters.sorters.DBObjectSorter;
import db.printer.mysqlprinters.sorters.SorterAnnotation;
import db.utils.ReflectionUtils;
import treerealization.Node;
import treerealization.Tree;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Makoiedov.H on 10/9/2017.
 */

public class MainPrinter {

    private List<DBObjectSorter> sorters;

    public MainPrinter() {
        sorters = new ArrayList<>();
        registerSorters();
    }

    /**
     * Method creates file with sql queries from tree with database metainformation
     * @param tree tree with database metainformation
     * @param file output file with sql queries
     * @throws IOException
     */
    public void createDDLScript(Tree tree, File file) throws IOException {
        for(DBObjectSorter sorter : sorters) {
            sorter.sortTree(tree);
        }


        PrintManager printManager = PrintManager.getInstance();
        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
        ////////////////////////////////////////////////////
        bufferedWriter.write("SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;" + System.lineSeparator());
        bufferedWriter.write("SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;" + System.lineSeparator());
        bufferedWriter.write("SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';" + System.lineSeparator());
        bufferedWriter.flush();
        ///////////////////////////////////////////////////////
        appendNodeScript(printManager, bufferedWriter, tree.getRoot());

        /////////////////////////////////////////////////////////////////
        bufferedWriter.write("SET SQL_MODE=@OLD_SQL_MODE;" + System.lineSeparator());
        bufferedWriter.write("SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;" + System.lineSeparator());
        bufferedWriter.write("SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;" + System.lineSeparator());
        ////////////////////////////////////////////////////////////////////////////////////
        bufferedWriter.close();
    }

    /**
     * Method runs all over the tree and adds specific sql query into file
     * for particular node
     * @param printManager
     * @param bufferedWriter
     * @param node
     * @throws IOException
     */
    private void appendNodeScript(PrintManager printManager, BufferedWriter bufferedWriter, Node node) throws IOException {
        bufferedWriter.write(printManager.printNode(node));
        if (!(printManager.printNode(node).equals(""))) {
            bufferedWriter.write(";" + System.lineSeparator() + System.lineSeparator());
            bufferedWriter.flush();
        }
        for(Node childNode : node.getChildren()) {
            appendNodeScript(printManager, bufferedWriter, childNode);
        }
    }

    /**
     * Method goes through the sorters directory and
     * collects sorters in list of sorters which have marked by sorter annotation
     */
    private void registerSorters() {
        List<Class> classes = null;
        try {
            classes = ReflectionUtils.getAnnotatedClasses(SorterAnnotation.class);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        if(classes != null && classes.size() != 0) {
            for(Class aClass : classes) {
                try {
                    Class<?> clazz = aClass;
                    if (clazz.isAnnotationPresent(SorterAnnotation.class)) {
                        sorters.add((DBObjectSorter)clazz.newInstance());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
