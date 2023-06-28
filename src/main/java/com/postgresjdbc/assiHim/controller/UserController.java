package com.postgresjdbc.assiHim.controller;

import com.postgresjdbc.assiHim.dao.UserDAO;
import com.postgresjdbc.assiHim.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
public class UserController {
    @Autowired
    private UserDAO uDAO;

    @GetMapping("/users")
    public List<User> getUsers(){
        return uDAO.getAll();
    }
    @PostMapping("/users")
    public String saveUser(@RequestBody User user){
        return uDAO.save(user)+"Number of Row affected";
    }
   @GetMapping("/users/{id}")
    public User getUserById(@PathVariable int id){
        return uDAO.getById(id);
   }
    @GetMapping("/users/num/{number}")
    public User getUserByNumber(@PathVariable String number){
        return uDAO.getByNumber(number);
    }
   @PutMapping("/users/{id}")
    public String updateUser(@RequestBody User user,@PathVariable int id){
        return uDAO.update(user,id)+"Number of Row affected";
   }
    @DeleteMapping("/users/{id}")
    public  String deleteUserById( @PathVariable int id){
        return uDAO.delete(id)+" This is deleted";
    }

}
