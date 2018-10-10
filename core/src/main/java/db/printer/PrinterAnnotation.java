package db.printer;

import treerealization.Node;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Makoiedov.H on 10/6/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface PrinterAnnotation {
    String type();
    Class<? extends AbstractPrinter> parent() default NULL.class;

    /**
     * This is special null AbstractPrinter
     */
    class NULL extends AbstractPrinter {
        @Override
        public String printNode(Node node) {
            return "";
        }
    }
}
