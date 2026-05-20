package vn.aeoc.base.service;

import org.springframework.beans.factory.annotation.Autowired;
import vn.aeoc.base.config.extention.paging.Page;
import vn.aeoc.base.config.extention.paging.Pageable;
import vn.aeoc.base.repo.AbsMysqlRepository;
import vn.aeoc.base.util.QueryExecutor;
import vn.aeoc.base.util.Tuple2;

import java.util.List;
import java.util.function.Supplier;

public abstract class BaseService<
        R extends AbsMysqlRepository<?, P, ID>,
        P,
        ID> {

    protected final R repository;

    @Autowired
    protected QueryExecutor queryExecutor;

    protected BaseService(R repository) {
        this.repository = repository;
    }

    public R getRepository() {
        return repository;
    }

    /* ==============================
     * CRUD
     * ============================== */

    public P getById(ID id) {
        return repository.getById(id);
    }

    public P insert(P pojo) {
        return repository.insertReturning(pojo);
    }

    public List<P> insertBatchReturning(List<P> pojos) {
        return repository.insertBatchReturning(pojos);
    }

    public List<P> updateBatchReturning(List<P> pojos) {
        return repository.updateBatchReturning(pojos);
    }

    public P update(ID id, P pojo) {
        return repository.update(id, pojo);
    }

    public Integer delete(ID id) {
        return repository.delete(id);
    }

    public List<P> getListByIds(List<ID> ids) {
        return repository.getListByIds(ids);
    }

    /* ==============================
     * PARALLEL PAGE
     * ============================== */

    protected Page<P> pageQuery(
            Pageable pageable,
            Supplier<List<P>> listSupplier,
            Supplier<Long> countSupplier
    ) {

        Tuple2<List<P>, Long> result =
                queryExecutor.parallel2(
                        listSupplier,
                        countSupplier
                );

        pageable.setTotal(result.v2());

        return new Page<>(pageable, result.v1());
    }

}