package de.jverhoelen.cryptoalerts.datautils.repo;


import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.io.Serializable;
import java.util.List;

/**
 * Repository interface which offers functionality of paging and sorting and indexing entities with elasticsearch.
 *
 * @param <T>  entity to be maintained by this repository
 * @param <ID> entity of the primary key
 */
@NoRepositoryBean
public interface Repository<T, ID extends Serializable> extends PagingAndSortingRepository<T, ID> {

    /**
     * Save a bunch of new entities.
     *
     * @param entities an iterable of the entities to save
     */
    <S extends T> List<S> save(Iterable<S> entities);

    /**
     * Get a list of all entities.
     */
    List<T> findAll();

    /**
     * Get a sorted list of all entities.
     */
    List<T> findAll(Sort sort);
}
