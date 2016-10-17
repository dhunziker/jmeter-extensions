package com.github.dhunziker.jmeter.extensions;

import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * Created by Dennis Hunziker on 17/10/16.
 */
public class PivotTest {

    @Test
    public void should_pivot_with_single_row_key() throws Exception {
        List<Map<String, Object>> data = new LinkedList<>();

        Map<String, Object> row1 = new LinkedHashMap<>();
        row1.put("colKey", 1);
        row1.put("rowKey", "Foo");
        row1.put("value", 4);
        data.add(row1);

        Map<String, Object> row2 = new LinkedHashMap<>();
        row2.put("colKey", 2);
        row2.put("rowKey", "Foo");
        row2.put("value", 3);
        data.add(row2);

        Map<String, Object> row3 = new LinkedHashMap<>();
        row3.put("colKey", 3);
        row3.put("rowKey", "Bar");
        row3.put("value", 1);
        data.add(row3);

        Map<String, Object> row4 = new LinkedHashMap<>();
        row4.put("colKey", 3);
        row4.put("rowKey", "Bar");
        row4.put("value", 1);
        data.add(row4);

        List<Map<String, Object>> result = Pivot.pivot(data, Arrays.asList("rowKey"), "colKey", "value", Integer::sum);
        assertEquals(4, getValue(result, "Foo", "1"));
        assertEquals(3, getValue(result, "Foo", "2"));
        assertEquals(2, getValue(result, "Bar", "3"));
    }

    private int getValue(List<Map<String, Object>> result, String rowKey, String column) {
        return (int) result.stream().filter(row -> row.get("rowKey").equals(rowKey)).map(row -> row.get(column)).findFirst().get();
    }

}