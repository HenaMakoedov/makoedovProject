package consoleLogic;

import strategypattern.SerializableClient;
import strategypattern.XMLSerializableStrategy;
import treerealization.Tree;

import java.io.*;
import java.util.Properties;

/**
 * Created by Makoiedov.H on 9/7/2017.
 */

public class ConsoleReceiver {
    private Tree tree;
    private SerializableClient client;

    public ConsoleReceiver() {
        client = new SerializableClient();
    }

    public Tree getTree() {
        return tree;
    }

    /**
     * This method establishes serializable strategy
     * @param strategy name of strategy
     */
    public void setStrategy(String strategy) {
        if (strategy.toLowerCase().equals("xml")) {
            client.setSerializableStrategy(new XMLSerializableStrategy());
            System.out.println("strategy command. OK");
        } else {
            System.out.println("Set strategy command error.");
            System.out.println("Don't find strategy " + strategy + "." );
        }
    }

    /**
     * This method deserialize tree from file
     * @param filePath
     */
    public void input(String filePath) {
        try {
            tree = client.performDeserialize(new File(filePath));
            System.out.println("input command. OK");
            System.out.println(tree + "");
        } catch (Exception e) {
            System.out.println("Input command error.");
            System.out.println("Don't find file, or your file don't have serializable tree,");
            System.out.println("or you didn't set serializable strategy.");
        }
    }

    /**
     * This method serialize your deserialize tree into file
     * @param filePath
     */
    public void output(String filePath) {
        try {
            if (tree != null) {
                client.performSerialize(tree, new File(filePath));
                System.out.println("output command. OK");
            }
            else {
                System.out.println("Output command error.");
                System.out.println("Please, deserialize your tree from file,");
                System.out.println("with command -input");
            }
        } catch (Exception e) {
            System.out.println("Output command error.");
            System.out.println("Unavailable path file.");
        }
    }

    /**
     * This method shows instruction to use console
     */
    public void help() {
        Properties prop = new Properties();
        InputStream input = null;

        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("help/english.properties").getFile());

        try {
            input = new FileInputStream(file.getAbsolutePath());
            prop.load(input);

            System.out.println(prop.getProperty("help"));
            System.out.println("Help command. OK");

        } catch (IOException ex) {
            System.out.println("Help command error.");
            System.out.println("don't found file with support information.");
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * This method finds node of the tree by node name
     * using depth search algorithm
     * @param name node name
     */
    public void nameDepthSearch(String name) {
        System.out.println(tree.nameDepthSearch(name));
    }

    /**
     * This method finds node of the tree by node attributes
     * using depth search algorithm
     * @param key key attribute
     * @param value value attribute
     */
    public void attrDepthSearch(String key, String value) {
        System.out.println(tree.attrDepthSearch(key, value));
    }

    /**
     * This method finds node of the tree by node name
     * using wide search algorithm
     * @param name node name
     */
    public void nameWideSearch(String name) {
        System.out.println(tree.nameWideSearch(name));
    }


    /**
     * This method finds node of the tree by node attributes
     * using wide search algorithm
     * @param key key attribute
     * @param value value attribute
     */
    public void attrWideSearch(String key, String value) {
        System.out.println(tree.attrWideSearch(key, value));
    }

}
