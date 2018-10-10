package db.loader;

import treerealization.Node;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by Makoiedov.H on 10/3/2017.
 */
@Retention(RetentionPolicy.RUNTIME)

public @interface LoaderAnnotation {
    String type();
    Class<? extends AbstractLoader> parent() default NULL.class;


    /**
    * This is special null abstractLoader class
     */
    class NULL extends AbstractLoader {

        @Override
        public Node loadNodeWithoutChildren(List<String> parameters) throws SQLException {
            return null;
        }

        @Override
        public void notifyParent(Node parent, List<String> parameters) throws SQLException {
        }

        @Override
        public List<Node> loadAllNodes(String schemaName) throws SQLException {
            return null;
        }
    }
}
