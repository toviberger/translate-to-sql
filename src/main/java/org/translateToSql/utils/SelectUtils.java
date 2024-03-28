package org.translateToSql.utils;

import net.sf.jsqlparser.expression.Alias;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SelectUtils {

    /***
     * Gets a PlainSelect query and create a map with the select items and their names \ aliases.
     * It adds alias when needed.
     * For example if we get -
     * SELECT max(a), b ...,
     * it changes it to -
     * SELECT max(a) AS col1, b
     * and returns {max(a) -> col1, b -> b}
     * @param select
     * @return
     */
    public static Map<String, String> handleSelectItemsMapping(PlainSelect select){
        int colIndex = 1;
        List<SelectItem<?>> selectItemsList = select.getSelectItems();
        Map<String, String> selectItemsMapping = new HashMap<>();

        // define a map with the select items and their names\ aliases
        for (SelectItem item: selectItemsList){
            Expression itemExpression = item.getExpression();

            // if there is an alias
            if (item.getAlias() != null)
                selectItemsMapping.put(itemExpression.toString(), item.getAlias().getName());
            else {
                if (itemExpression instanceof Column)
                    selectItemsMapping.put(itemExpression.toString(), itemExpression.toString());
                else {
                    // if it's not a column, give a new alias
                    selectItemsMapping.put(itemExpression.toString(), ExpressionUtils.COL_NAME + colIndex);
                    colIndex += 1;
                }
            }
        }

        // check if there is no duplicates in the aliases and names
        for (Map.Entry<String, String> entry : selectItemsMapping.entrySet()) {
            String key = entry.getKey();
            while (selectItemsMapping.containsKey(entry.getValue()) && !Objects.equals(key, entry.getValue())){
                selectItemsMapping.put(key, ExpressionUtils.COL_NAME + colIndex);
                colIndex += 1;
            }
        }

        // if there is a select item that is not a column and didn't have an alias, add it to the query
        for (SelectItem item: select.getSelectItems()){
            if (!(item.getExpression() instanceof Column) && item.getAlias() == null)
                item.setAlias(new Alias(selectItemsMapping.get(item.getExpression().toString())));
        }

        return selectItemsMapping;
    }
}
