package com.postgresjdbc.assiHim.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgresjdbc.assiHim.dao.UserDAO;
import com.postgresjdbc.assiHim.model.User;
import com.postgresjdbc.assiHim.model.UserSearchCriteria;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;



@RestController
public class UserController {
    @Autowired
    private UserDAO uDAO;

    private final KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    public UserController(UserDAO uDAO, KafkaTemplate<String, String> kafkaTemplate) {
        this.uDAO = uDAO;
        this.kafkaTemplate = kafkaTemplate;
    }





    @GetMapping("/users")
    public List<User> getUsers(){
        return uDAO.getAll();
    }



    @PostMapping("/users")
    public ResponseEntity<String> saveUser(@RequestBody List<User> users) throws JsonProcessingException {
        int created_user=0;
        int uncreated_user=0;
        for(User user:users){
            ObjectMapper mapper = new ObjectMapper();
            String json = mapper.writeValueAsString(user);


            if (isUserExists(user.getName(), user.getNumber())) {
                uncreated_user+=1;
            }
            else{
                created_user+=1;
                kafkaTemplate.send("topic-create", json);
            }
        }




        String response="Successfully created "+created_user;
        response+=" Not created "+uncreated_user;

        return ResponseEntity.ok(response);
    }


    @Autowired
    public JdbcTemplate jdbcTemplate;
    private boolean isUserExists(String name, String number) {
        String query = "SELECT COUNT(*) FROM user_table WHERE name = ? AND number = ?";
        int count = jdbcTemplate.queryForObject(query, Integer.class, name, number);
        return count > 0;
    }
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable("id") UUID id) {
        try {
            User user = uDAO.getById(id);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            // Exception handling code
            // ...
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/users/is_active/{active}")
    public List<User> getUserByActive(@PathVariable Boolean active){
        return uDAO.getByActive(active);
    }
    @GetMapping("/users/num/{number}")
    public User getUserByNumber(@PathVariable String number){
        return uDAO.getByNumber(number);
    }



    @PutMapping("/users")
    public ResponseEntity<String> updateUsers(@RequestBody List<User> users) throws JsonProcessingException {


int updated_user=0;
int not_updated=0;
        for (User user : users) {
            UUID id = user.getId();
            if (uDAO.isUniqueUser(user.getName(), user.getNumber(), id)) {

                updated_user+=1;
                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(user);
                kafkaTemplate.send("topic-update", json);
            } else {
                not_updated+=1;

            }
        }

        String response = "Updated users: " + updated_user+ " and";
        response += "Not updated users: " + not_updated ;




        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/users/{id}")
    public  String deleteUserById( @PathVariable UUID id){
        return uDAO.delete(id)+" This is deleted";
    }

    @PostMapping("/users/search")
    public User getUserByNumber(@RequestBody UserSearchCriteria user){
        return uDAO.getByUserSearchCriteria(user);
    }
}
