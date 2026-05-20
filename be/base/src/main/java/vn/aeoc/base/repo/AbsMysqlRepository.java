package vn.aeoc.base.repo;

import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jooq.*;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
import org.jooq.impl.TableRecordImpl;
import org.springframework.beans.factory.annotation.Autowired;
import vn.aeoc.base.config.extention.paging.Pageable;

import java.lang.reflect.ParameterizedType;
import java.util.*;

import static java.util.Optional.ofNullable;
import static vn.aeoc.base.util.MysqlUtil.toInsertQueries;
import static vn.aeoc.base.util.MysqlUtil.toUpdateQueries;

public abstract class AbsMysqlRepository<R extends TableRecordImpl<R>, P, ID> {

    @Autowired
    protected DSLContext dslContext;

    @SneakyThrows
    @PostConstruct
    public void init() {

        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();

        this.pojoClass = (Class<P>) type.getActualTypeArguments()[1];
        
        this.fieldID = (TableField<R, ID>) Arrays.stream(getTable().fields())
                .filter(field -> field.getName().equalsIgnoreCase("id"))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Table must have ID field"));
    }

    protected Class<P> pojoClass;

    protected TableField<R, ID> fieldID;

    private Object getIdValue(P pojo) {
        try {
            return pojo.getClass()
                    .getMethod("getId")
                    .invoke(pojo);
        } catch (Exception e) {
            throw new RuntimeException("Cannot get ID", e);
        }
    }

    protected abstract TableImpl<R> getTable();

    protected Field<?>[] getFiled() {
        return getTable().fields();
    }

    protected List<Field<?>> getFields() {
        return Arrays.asList(getTable().fields());
    }

    /* =====================================================
                        QUERY BASIC
       ===================================================== */

    public List<P> getAll() {

        return dslContext
                .select(getFiled())
                .from(getTable())
                .fetchInto(pojoClass);
    }

    public P getById(ID id) {

        return dslContext
                .select(getFiled())
                .from(getTable())
                .where(fieldID.eq(id))
                .fetchOptionalInto(pojoClass)
                .orElse(null);
    }

    public P getOne(Condition... condition) {

        return dslContext
                .select(getFiled())
                .from(getTable())
                .where(condition)
                .limit(1)
                .fetchOptionalInto(pojoClass)
                .orElse(null);
    }

    public List<P> getListByIds(List<ID> ids) {

        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        return dslContext
                .select(getFiled())
                .from(getTable())
                .where(fieldID.in(ids))
                .fetchInto(pojoClass);
    }

    /* =====================================================
                        EXISTS
       ===================================================== */

    public boolean existsById(ID id) {

        return dslContext.fetchExists(
                dslContext.selectOne()
                        .from(getTable())
                        .where(fieldID.eq(id))
        );
    }

    public boolean exists(Condition condition) {

        return dslContext.fetchExists(
                dslContext.selectOne()
                        .from(getTable())
                        .where(condition)
        );
    }

    /* =====================================================
                        PAGING
       ===================================================== */

    public Long getCountWithCriteria(Condition condition) {

        return dslContext
                .selectCount()
                .from(getTable())
                .where(condition)
                .fetchOneInto(Long.class);
    }

    public List<P> getListByCriteria(
            Condition condition
    ) {
        SelectConditionStep<Record> select = dslContext
                .select(getFiled())
                .from(getTable())
                .where(condition);

        return select
                .fetchInto(pojoClass);
    }

    public List<P> getListByCriteria(
            Condition condition,
            OrderField<?> order
    ) {
        SelectConditionStep<Record> select = dslContext
                .select(getFiled())
                .from(getTable())
                .where(condition);

        if (order != null) {
            select.orderBy(order);
        }
        return select
                .fetchInto(pojoClass);
    }

    public List<P> getListByCriteria(
            Condition condition,
            OrderField<?> order,
            Pageable pageable
    ) {
        SelectConditionStep<Record> select = dslContext
                .select(getFiled())
                .from(getTable())
                .where(condition);

        if (order != null) {
            select.orderBy(order);
        }
        return select
                .limit(pageable.getLimit())
                .offset(pageable.getOffset())
                .fetchInto(pojoClass);
    }

    /* =====================================================
                        JOIN QUERY
       ===================================================== */

    protected List<P> getListByCriteriaWithJoin(
            List<Field<?>> fields,
            Table<?> from,
            Condition condition,
            OrderField<?> order,
            Pageable pageable
    ) {

        SelectConditionStep<Record> select = dslContext
                .select(fields)
                .from(from)
                .where(condition);

        if (order != null) {
            select.orderBy(order);
        }

        return select
                .limit(pageable.getLimit())
                .offset(pageable.getOffset())
                .fetchInto(pojoClass);
    }

    protected List<P> getListByCriteriaWithJoin(
            List<Field<?>> fields,
            Table<?> from,
            Condition condition,
            OrderField<?> order
    ) {

        SelectConditionStep<Record> select = dslContext
                .select(fields)
                .from(from)
                .where(condition);

        if (order != null) {
            select.orderBy(order);
        }

        return select
                .fetchInto(pojoClass);
    }

    protected P getOneByCriteriaWithJoin(
            List<Field<?>> fields,
            Table<?> from,
            Condition condition
    ) {

        SelectConditionStep<Record> select = dslContext
                .select(fields)
                .from(from)
                .where(condition);

        return select.fetchOneInto(pojoClass);
    }

    protected List<Field<?>> mergeFields(Field<?>... extraFields) {

        List<Field<?>> fields = new ArrayList<>(Arrays.asList(getFiled()));

        if (extraFields != null) {
            fields.addAll(Arrays.asList(extraFields));
        }

        return fields;
    }

    /* =====================================================
                        INSERT
       ===================================================== */

    public Integer insert(P pojo) {

        return dslContext
                .insertInto(getTable())
                .set(toInsertQueries(getTable(), pojo))
                .execute();
    }

    public P insertReturning(P pojo) {
        return insertBlocking(pojo, dslContext).orElse(null);
    }

    protected @NotNull Optional<P> insertBlocking(P pojo, DSLContext context) {

        return ofNullable(
                context.insertInto(getTable())
                        .set(toInsertQueries(getTable(), pojo))
                        .returning()
                        .fetchOne()
        ).map(r -> r.into(pojoClass));
    }

    public List<P> insertBatchReturning(List<P> pojos) {

        if (pojos == null || pojos.isEmpty()) {
            return List.of();
        }

        List<InsertSetMoreStep<R>> queries = pojos.stream()
                .map(p -> dslContext.insertInto(getTable())
                        .set(toInsertQueries(getTable(), p)))
                .toList();

        Arrays.stream(dslContext.batch(queries).execute())
                .boxed()
                .toList();
        return pojos;
    }

    /* =====================================================
                        UPDATE
       ===================================================== */

    public P update(ID id, P pojo) {

        dslContext.update(getTable())
                .set(toInsertQueries(getTable(), pojo))
                .where(fieldID.eq(id))
                .execute();

        return pojo;
    }

    public Integer update(ID id, P pojo, List<String> fields) {

        return dslContext.update(getTable())
                .set(toInsertQueries(getTable(), pojo, fields))
                .where(fieldID.eq(id))
                .execute();
    }

    public Integer update(ID id, P pojo, Field<?>... fields) {

        return dslContext.update(getTable())
                .set(toUpdateQueries(getTable().newRecord(), pojo, fields))
                .where(fieldID.eq(id))
                .execute();
    }

    public Integer updateField(ID id, Field<?> field, Object value) {

        return dslContext.update(getTable())
                .set((Field<Object>) field, value)
                .where(fieldID.eq(id))
                .execute();
    }

    public Integer updateFieldByCondition(Condition condition, Field<?> field, Object value) {

        return dslContext.update(getTable())
                .set((Field<Object>) field, value)
                .where(condition)
                .execute();
    }

    public List<P> updateBatchReturning(List<P> pojos) {

        if (pojos == null || pojos.isEmpty()) {
            return List.of();
        }

        List<UpdateConditionStep<R>> queries = pojos.stream()
                .map(p -> dslContext.update(getTable())
                        .set(toInsertQueries(getTable(), p))
                        .where(fieldID.eq((ID) getIdValue(p))))
                .toList();

        dslContext.batch(queries).execute();

        return pojos;
    }

    /* =====================================================
                        DELETE
       ===================================================== */

    public Integer delete(ID id) {

        return dslContext.delete(getTable())
                .where(fieldID.eq(id))
                .execute();
    }

    public Integer delete(Condition condition) {

        return dslContext.delete(getTable())
                .where(condition)
                .execute();
    }

    /* =====================================================
                    MAP QUERY
   ===================================================== */

    protected <K, V> Map<K, V> getMapByCriteria(
            Field<K> keyField,
            Field<V> valueField,
            Table<?> from,
            Condition condition
    ) {

        return dslContext
                .select(keyField, valueField)
                .from(from)
                .where(condition)
                .fetchMap(keyField, valueField);
    }

    protected <K> Map<K, Integer> countGroupBy(
            Field<K> groupField,
            Field<?> countField,
            Table<?> from,
            Condition condition
    ) {

        return dslContext
                .select(
                        groupField,
                        DSL.count(countField).as("total")
                )
                .from(from)
                .where(condition)
                .groupBy(groupField)
                .fetchMap(
                        groupField,
                        r -> r.get("total", Integer.class)
                );
    }
}