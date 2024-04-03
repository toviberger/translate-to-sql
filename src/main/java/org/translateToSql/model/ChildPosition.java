package org.translateToSql.model;

/***
 * Enum for representing a child node position in an AST (abstract syntax tree) - left or right child
 */
public enum ChildPosition {
    LEFT("left"),
    RIGHT("right");

    private final String value;

    ChildPosition(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
