package com.postgresjdbc.assiHim.consumer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgresjdbc.assiHim.dao.UserDAO;
import com.postgresjdbc.assiHim.dao.UserDAOimpl;
import com.postgresjdbc.assiHim.model.User;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class Consumer {
    private UserDAO userDAO;


    public Consumer(UserDAO userDAO) {
        this.userDAO = userDAO;
    }
    @KafkaListener(topics = "topic-create")
    public void consumeCreate(String userString){
        try{
            ObjectMapper mapper=new ObjectMapper();
            User user=mapper.readValue(userString,User.class);
            userDAO.save(user);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    @KafkaListener(topics = "topic-update")
    public void consumerUpdate(String userString){
        try{
            ObjectMapper mapper= new ObjectMapper();
            User user= mapper.readValue(userString,User.class);

            UUID id=user.getId();
            userDAO.update(user,id);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
