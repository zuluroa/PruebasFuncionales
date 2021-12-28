package com.co.sofkla.Kata.pruebas.repository;

import com.co.sofkla.Kata.pruebas.model.Person;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface PersonRepository extends ReactiveMongoRepository<Person,String> {
    Mono<Person> findByName(String name);
}
