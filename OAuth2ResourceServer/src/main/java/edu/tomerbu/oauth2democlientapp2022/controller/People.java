package edu.tomerbu.oauth2democlientapp2022.controller;

import edu.tomerbu.oauth2democlientapp2022.entitiy.Person;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//@RestController(value = "/people")

@RestController
@RequestMapping("/people")
public class People {
    @GetMapping("/all")
    public Flux<Person> all() {
        return Flux.just(new Person("John", "Doe"));
    }
}
