package com.co.sofkla.Kata.pruebas.controller;

import com.co.sofkla.Kata.pruebas.model.Person;
import com.co.sofkla.Kata.pruebas.repository.PersonRepository;
import com.co.sofkla.Kata.pruebas.service.PersonService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.swing.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;

@ExtendWith(SpringExtension.class)
@WebFluxTest(controllers = PersonController.class)
class PersonControllerTest {

    @SpyBean
    private PersonService personService;

    @MockBean
    private PersonRepository repository;

    @Captor
    private ArgumentCaptor<Person> argumentCaptor;


    @Autowired
    private WebTestClient webTestClient;

    @ParameterizedTest
    @CsvSource({"Raul Alzate,0", "Raul Alzate,1"})
    void post(String name, Integer times){

        if(times == 0) {
            Mockito.when(repository.findByName(name)).thenReturn(Mono.just(new Person()));
        }
        if(times == 1) {
            Mockito.when(repository.findByName(name)).thenReturn(Mono.empty());
        }

        var request = Mono.just(new Person(name));
        //Mockito.when(repository.findByName(name)).thenReturn(Mono.empty());
        //Mockito.when(repository.findByName(name)).thenReturn(Mono.just(new Person(name)));
        Mockito.when(repository.save(Mockito.any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/person")
                .body(request, Person.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();

        Mockito.verify(personService).insert(argumentCaptor.capture());
        Mockito.verify(repository).findByName(name);
        Mockito.verify(repository, times(times)).save(Mockito.any());

        var person = argumentCaptor.getValue();

        Assertions.assertEquals(name, person.getName());
    }

    @Test
    void get(){
        webTestClient.get()
                .uri("/person/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Person.class)
                .consumeWith(personEntityExchangeResult -> {
                    var person = personEntityExchangeResult.getResponseBody();
                    assert person != null;
                });
    }

    @Test
    void update(){
        var request = Mono.just(new Person());
        webTestClient.put()
                .uri("/person")
                .body(request, Person.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    @Test
    void delete(){
        webTestClient.delete()
                .uri("/person/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody().isEmpty();
    }

    /*@Test
    void list(){
        webTestClient.get()
                .uri("/person")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("DAVID")
                .jsonPath("$[1].name").isEqualTo("JESUS");
    }*/

    @Test
    void listPerson() {
        var list = Flux.just(
                new Person("Raul Alzate"),
                new Person("Pedro" )
        );
        Mockito.when(repository.findAll()).thenReturn(list);

        webTestClient.get()
                .uri("/person")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].name").isEqualTo("Raul Alzate")
                .jsonPath("$[1].name").isEqualTo("Pedro");

        Mockito.verify(personService).listAll();
        Mockito.verify(repository).findAll();
        //TODO: aplicar el captor aqui


    }

}