package de.jverhoelen.cryptoalerts.datautils.repo;


import org.springframework.data.domain.Pageable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * Interface that defines methods that should be provided to RepositoryServices as facade for repositories.
 *
 * @param <T>  the entitiy to be maintained by the repository
 * @param <ID> primary key entity of <T>
 */
public interface RepositoryService<T, ID extends Serializable> {

    /**
     * Counts the objects of the repository.
     *
     * @return number of objects in the repository
     */
    long count();

    /**
     * Checks if the repository is empty.
     *
     * @return whether the repository is empty or not
     */
    boolean isEmpty();

    /**
     * Fetches the object being resolved by its ID.
     *
     * @param id primary key of the searched object
     * @return the resulting object or null
     */
    T get(ID id);

    /**
     * Fetches all object matching to a set of IDs.
     *
     * @param ids an Iterable with IDs of the searched objects
     * @return all matching objects in an Iterable
     */
    Iterable<T> getAll(Iterable<ID> ids);

    /**
     * Fetches all objects.
     *
     * @return a list of all objects
     */
    List<T> getAll();


    List<T> getAll(Pageable pageable);

    /**
     * Adds an object to the repository.
     *
     * @param entity new object in the repository
     * @return the added object
     */
    T add(T entity);

    /**
     * Adds multiple objects to the repository.
     *
     * @param entities a collection of objects to be added
     * @return the added Collection as Iterable
     */
    Iterable<T> add(Collection<T> entities);

    /**
     * Removes a certain object from the repository.
     *
     * @param entity
     */
    void remove(T entity);

    void remove(Collection<T> entity);

    /**
     * Removes an object from repository by its ID.
     *
     * @param id ID of the object
     * @return the found/deleted object or null
     */
    T remove(ID id);

    /**
     * Delete all objects in this repository.
     */
    void clear();
}
