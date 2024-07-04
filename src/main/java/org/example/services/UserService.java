package org.example.services;

import org.example.models.User;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Service
public class UserService {

    private final RestTemplate restTemplate = new RestTemplate();
    private final String baseUrl = "http://94.198.50.185:7081/api/users";
    private String sessionId;

    public void performOperations() {
        try {
            ResponseEntity<List> responseEntity = getAllUsers();
            sessionId = Objects.requireNonNull(responseEntity.getHeaders().get("Set-Cookie")).get(0);
            System.out.println("Session ID: " + sessionId);

            User newUser = new User();
            newUser.setId(3);
            newUser.setName("James");
            newUser.setLastName("Brown");
            newUser.setAge(25);
            String part1 = saveUser(newUser);
            System.out.println("1: " + part1);

            newUser.setName("Thomas");
            newUser.setLastName("Shelby");
            String part2 = updateUser(newUser);
            System.out.println("2: " + part2);

            // Step 4: Delete user
            String part3 = deleteUser(3);
            System.out.println("3: " + part3);

            // Concatenate and print the code
            String code = part1 + part2 + part3;
            System.out.println("Final: " + code);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ResponseEntity<List> getAllUsers() {
        return restTemplate.exchange(baseUrl, HttpMethod.GET, null, List.class);
    }

    private String saveUser(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", sessionId);

        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.POST, requestEntity, String.class);

        return response.getBody();
    }

    private String updateUser(User user) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Cookie", sessionId);

        HttpEntity<User> requestEntity = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.exchange(baseUrl, HttpMethod.PUT, requestEntity, String.class);

        return response.getBody();
    }

    private String deleteUser(int id) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cookie", sessionId);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl).path("/" + id);
        ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE, requestEntity, String.class);

        return response.getBody();
    }
}