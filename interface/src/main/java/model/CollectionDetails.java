package model;

import db.constants.DBObjects;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Makoiedov.H on 11/2/2017.
 */

/**
 * class which describes the table of main window
 * with attributes some node
 */
public class CollectionDetails {
    private ObservableList<Details> detailsList;
    private List<String> definitions;

    public CollectionDetails() {
        detailsList = FXCollections.observableArrayList();
        definitions = new ArrayList<>();

        definitions.add(DBObjects.AttributeNames.VIEW_DEFINITION);
        definitions.add(DBObjects.AttributeNames.ROUTINE_DEFINITION);
        definitions.add(DBObjects.AttributeNames.ACTION_STATEMENT);
    }

    /**
     * Method determines the definition attributes
     * info about definitions not displayed in table
     * @param s attribute key
     * @return attribute key is definition
     */
    public boolean isDefinition(String s) {
        for(String definition : definitions) {
            if (s.equals(definition)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method clears table data
     */
    public void clear() {
        detailsList.clear();
    }

    /**
     * Method fills table data
     * @param detailsMap attributes of current node
     */
    public void fillDetails(Map<String, String> detailsMap) {
        for(Map.Entry<String, String> pair : detailsMap.entrySet()) {
            if(isDefinition(pair.getKey())) {
                continue;
            }
            Details detail = new Details(pair.getKey(), pair.getValue());
            detailsList.add(detail);
        }
    }

    /**
     * Details list getter
     * @return details list
     */
    public ObservableList<Details> getDetailsList() {
        return detailsList;
    }
}
