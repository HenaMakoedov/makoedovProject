package db.loader.sorters;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by Makoiedov.H on 10/18/2017.
 */

/**
 * Marker annotation. The annotation marks the sorting classes
 * for the next loading marked classes with reflection
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SorterAnnotation {

    //type defines which node needs to be sorted
    String type();
}
