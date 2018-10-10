package strategypattern;
import java.io.File;
import treerealization.Tree;

/**
 * Created by Makoiedov.H on 8/29/2017.
 */
public class SerializableClient {
    private SerializableStrategy serializableStrategy;

    /**
     * This method establish serializable strategy
     * @param serializableStrategy
     */
    public void setSerializableStrategy(SerializableStrategy serializableStrategy) {
        this.serializableStrategy = serializableStrategy;
    }

    /**
     * This method perform serialize with your selected serializable strategy.
     * Method serialize your concrete tree into file
     * @param tree that needs serialization
     * @param file in which you want to serialize a tree
     * @throws Exception
     */
    public void performSerialize(Tree tree, File file) throws Exception {
            serializableStrategy.serialize(tree, file);
    }

    /**
     * This method deserialize tree from your file
     * using your established serializable strategy
     * @param file with the serialized tree
     * @return deserialized tree
     * @throws Exception
     */
    public Tree performDeserialize(File file) throws Exception {
        return serializableStrategy.deserialize(file);
    }
}
