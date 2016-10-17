package com.github.dhunziker.jmeter.extensions;

import com.github.dhunziker.jmeter.extensions.util.JMeterUtils;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

import static java.util.stream.Collectors.toList;

/**
 * Created by Dennis Hunziker on 17/10/16.
 */
public class Pivot {

    public static List<Map<String, Object>> pivot(String input, List<String> rowKeys, String columnKey, String valueKey, BinaryOperator<Integer> valueFunction) {
        return pivot((List<Map<String, Object>>) JMeterUtils.getVariables().get(input), rowKeys, columnKey, valueKey, valueFunction);
    }

    public static List<Map<String, Object>> pivot(List<Map<String, Object>> input, List<String> rowKeys, String columnKey, String valueKey, BinaryOperator<Integer> valueFunction) {
        List<Object> columnValues = new LinkedList<>(rowKeys);
        columnValues.addAll(input.stream().map(row -> row.get(columnKey)).distinct().collect(toList()));

        List<List<Object>> rowKeyValues = input.stream().map(row -> rowKeyValues(row, rowKeys)).distinct().collect(toList());

        List<Map<String, Object>> result = new LinkedList<>();

        for (List<Object> rowKeyValue : rowKeyValues) {
            Map<String, Object> newRow = new LinkedHashMap<>();

            for (int i = 0; i < rowKeyValue.size(); i++) {
                newRow.put(rowKeys.get(i), rowKeyValue.get(i));
            }

            for (Object column : columnValues.subList(rowKeys.size(), columnValues.size())) {
                Object newValue = input.stream().filter(row -> rowKeyValues(row, rowKeys).equals(rowKeyValue) && row.get(columnKey).equals(column))
                        .map(row -> Integer.valueOf(row.get(valueKey).toString())).reduce(0, valueFunction);
                newRow.put(column.toString(), newValue);
            }

            result.add(newRow);
        }
        return result;
    }

    private static List<Object> rowKeyValues(Map<String, Object> row, List<String> rowKeys) {
        return rowKeys.stream().map(row::get).collect(toList());
    }

}
