package com.postgresjdbc.assiHim.controller;

import com.postgresjdbc.assiHim.dao.UserDAO;
import com.postgresjdbc.assiHim.model.User;
import com.postgresjdbc.assiHim.model.UserSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserDAO uDAO;

    @GetMapping("/users")
    public List<User> getUsers(){
        return uDAO.getAll();
    }



    @PostMapping("/users")
    public ResponseEntity<String> saveUsers(@RequestBody List<User> users) {
        List<User> createdUsers = new ArrayList<>();
        List<User> notCreatedUsers = new ArrayList<>();

        for (User user : users) {
            int rowsAffected = uDAO.save(user);
            if (rowsAffected > 0) {

                createdUsers.add(user);
            } else {
                notCreatedUsers.add(user);
            }
        }

        String response = "Successfully created: " + createdUsers.size() + " users\n";
        response += "Not created: " + notCreatedUsers.size() + " users\n";

        // Append UUIDs of created users to the response
        for (User user : createdUsers) {
            response += "Created user: " + user.toString()  + "\n";
        }

        response += "Not created users: " + notCreatedUsers.toString();

        return ResponseEntity.ok(response);
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
    public ResponseEntity<String> updateUsers(@RequestBody List<User> users) {
        List<User> updatedUsers = new ArrayList<>();
        List<User> notUpdatedUsers = new ArrayList<>();

        for (User user : users) {
            UUID id = user.getId();
            if (uDAO.isUniqueUser(user.getName(), user.getNumber(), id)) {
                User updatedUser = uDAO.update(user, id);
                updatedUsers.add(updatedUser);
            } else {
                notUpdatedUsers.add(user);
            }
        }

        String response = "Updated users: " + updatedUsers.size() + " users\n";
        response += "Not updated users: " + notUpdatedUsers.size() + " users\n";
        response += "Updated users: " + updatedUsers.toString() + "\n";
        response += "Not updated users: " + notUpdatedUsers.toString();

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
