package com.co.sofkla.Kata.pruebas.model;

public class Person {
    private String name;
    private Boolean active;

    public Person() {
    }

    public Person(String name) {
        this.name = name;
    }

    public Person(String nombre, Boolean active) {
        this.name = nombre;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public Boolean getActive() {
        return active;
    }
}
