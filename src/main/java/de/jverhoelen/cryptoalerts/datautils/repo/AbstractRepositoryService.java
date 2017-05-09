package de.jverhoelen.cryptoalerts.datautils.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * An abstract repository service that provides default implementations to access the repository of an entity T with the primary key ID.
 * It automatically utilizes the underlying repository by autowiring Repository<T, ID>.
 *
 * @param <T>  the entity to be maintained
 * @param <ID> its primary key entity
 */
public abstract class AbstractRepositoryService<T, ID extends Serializable> implements RepositoryService<T, ID> {

    @Autowired
    protected Repository<T, ID> repository;

    @Override
    public long count() {
        return this.repository.count();
    }

    @Override
    public boolean isEmpty() {
        return this.repository.count() == 0L;
    }

    @Override
    public T get(final ID id) {
        return repository.findOne(id);
    }

    @Override
    public Iterable<T> getAll(final Iterable<ID> ids) {
        return repository.findAll(ids);
    }

    @Override
    public List<T> getAll() {
        return repository.findAll();
    }

    @Override
    public List<T> getAll(Pageable pageable) {
        return repository.findAll(pageable).getContent();
    }

    @Override
    public T add(final T entity) {
        return repository.save(entity);
    }

    @Override
    public Iterable<T> add(final Collection<T> entities) {
        return repository.save(entities);
    }

    @Override
    public void remove(final T product) {
        repository.delete(product);
    }

    @Override
    public void remove(Collection<T> products) {
        repository.delete(products);
    }

    @Override
    public T remove(final ID id) {
        T entity = get(id);
        if (entity != null) {
            remove(entity);
        }
        return entity;
    }

    @Override
    public void clear() {
        repository.deleteAll();
    }
}
