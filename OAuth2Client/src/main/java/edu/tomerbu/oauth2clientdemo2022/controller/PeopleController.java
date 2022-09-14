/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tomerbu.oauth2clientdemo2022.controller;


import java.util.List;

import edu.tomerbu.oauth2clientdemo2022.entity.Person;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class PeopleController {

    private final RestTemplate restTemplate;

    public PeopleController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @GetMapping("/people")
    public String getOrders(Model model, @RegisteredOAuth2AuthorizedClient("users-client-oidc") OAuth2AuthorizedClient client) {
        System.out.println(client.getAccessToken().getTokenValue());

        String url = "http://127.0.0.1:8081/people/all";
        //import org.springframework.http.HttpHeaders;
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION,"Bearer " + client.getAccessToken().getTokenValue());
        var httpEntity = new HttpEntity<>(headers);

        ResponseEntity<List<Person>> responseEntity= restTemplate.exchange(
                url, HttpMethod.GET, httpEntity,
                new ParameterizedTypeReference<>(){});
        List<Person> people = responseEntity.getBody();
      //  List<Person> people = new ArrayList<>();
        model.addAttribute("people", people);
        return "people-page";
    }
}
