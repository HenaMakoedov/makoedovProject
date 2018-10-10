package model;

/**
 * Created by Makoiedov.H on 11/2/2017.
 */

/**
 * Data of main table
 */
public class Details {
    private String attribute;
    private String value;


    public Details(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }


    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
