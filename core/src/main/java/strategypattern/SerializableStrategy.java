package strategypattern;

import treerealization.Tree;

import java.io.File;

/**
 * Created by Makoiedov.H on 8/29/2017.
 */
public interface SerializableStrategy {
    /**
     * This method serialize your selected tree into file
     * @param tree that needs serialization
     * @param file in which you want to serialize a tree
     * @throws Exception
     */
    void serialize(Tree tree, File file) throws Exception;

    /**
     * This method deserialize tree from you file
     * @param file with the serialized tree
     * @return deserialized tree
     * @throws Exception
     */
    Tree deserialize(File file) throws Exception;
}
