package db.loader.sorters;


import db.utils.ReflectionUtils;
import treerealization.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Makoiedov.H on 10/18/2017.
 */
public class SortManager {
    private static SortManager instance;
    private Map<String, DBObjectSorter> sorters;

    private SortManager() {
        sorters = new HashMap<>();
    }

    /**
     * Method for creating or receiving an instance for this class
     * @return singleton instance
     */
    public static SortManager getInstance() {
        if (instance == null) {
            instance = new SortManager();
            instance.registerSorters();
        }
        return instance;
    }

    /**
     * Method sort a particular node using a specific sorter
     * @param node particular node
     */
    public void sortNode(Node node) {
        DBObjectSorter sorter = sorters.get(node.getName());
        if (sorter != null) {
            sorter.sortNode(node);
        }
    }

    /**
     * Method goes through the sorters directory and
     * collects sorters in map of sorters which have marked by sorter annotation
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
                        DBObjectSorter sorter = (DBObjectSorter)clazz.newInstance();
                        sorters.put(clazz.getAnnotation(SorterAnnotation.class).type(), sorter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
