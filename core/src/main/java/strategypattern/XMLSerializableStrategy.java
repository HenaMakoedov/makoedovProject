package strategypattern;

import serializers.XMLSerializer;
import treerealization.Tree;

import java.io.File;

/**
 * Created by Makoiedov.H on 8/29/2017.
 */
public class XMLSerializableStrategy implements SerializableStrategy {
    private XMLSerializer serializer;

    public XMLSerializableStrategy(){
        serializer = new XMLSerializer();
    }

    /**
     * This method serialize your selected tree into file
     * using XML strategy
     * @param tree that needs serialization
     * @param file in which you want to serialize a tree
     * @throws Exception
     */
    public void serialize(Tree tree, File file) throws Exception {
        serializer.serialize(tree, file);
    }

    /**
     * This method deserialize tree from you file
     * using XML strategy
     * @param file with the serialized tree
     * @return deserialized tree
     * @throws Exception
     */
    public Tree deserialize(File file) throws Exception {
        return serializer.deserialize(file);
    }
}
