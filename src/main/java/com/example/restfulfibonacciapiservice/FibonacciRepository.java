package com.example.restfulfibonacciapiservice;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Interface declaring methods for interacting with the database
 * More Info:
 * <ul>
 *     <li>
 * JPA vs CRUD Repository: https://stackoverflow.com/questions/14014086/what-is-difference-between-crudrepository-and-jparepository-interfaces-in-spring
 *     </li>
 * </ul>
 */
public interface FibonacciRepository extends JpaRepository<com.example.restfulfibonacciapiservice.Fibonacci, Integer> {
    com.example.restfulfibonacciapiservice.Fibonacci getById(Integer id);
}
