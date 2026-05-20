package vn.aeoc.base.util;

import org.jooq.Field;
import org.jooq.Record;
import org.jooq.impl.TableImpl;
import org.jooq.impl.TableRecordImpl;

import java.util.*;

import static java.util.stream.Collectors.toSet;

public class MysqlUtil {
    public static <R extends Record> Map<Field<?>, Object>
    toInsertQueries(TableImpl<R> table, Object o) {
        R record = table.newRecord();
        record.from(o);
        Map<Field<?>, Object> values = new HashMap<>();
        for (Field<?> f : record.fields()) {
            if (record.getValue(f) != null) {
                values.put(f, record.getValue(f));
            }
        }
        return values;
    }

    public static <R extends Record> Map<Field<?>, Object>
    toInsertQueries(TableImpl<R> table, Object o, List<String> fields) {
        R record = table.newRecord();
        record.from(o);
        final HashSet<String> fieldSet = new HashSet<>(fields);
        Map<Field<?>, Object> values = new HashMap<>();
        for (Field<?> f : record.fields()) {
            if (fieldSet.contains(f.getName())) {
                values.put(f, record.getValue(f));
            }
        }
        return values;
    }

    public static <T extends TableRecordImpl<T>> Map<Field<?>, Object>
    toUpdateQueries(T record, Object o, Field<?>... fields) {
        record.from(o);
        final Set<String> fieldSet = Arrays.stream(fields)
                .map(Field::getName)
                .collect(toSet());
        Map<Field<?>, Object> values = new HashMap<>();
        for (Field<?> f : record.fields()) {
            if (fieldSet.contains(f.getName())) {
                values.put(f, record.getValue(f));
            }
        }
        return values;
    }
}
