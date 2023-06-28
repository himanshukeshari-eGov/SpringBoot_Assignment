package com.postgresjdbc.assiHim.dao;

import com.postgresjdbc.assiHim.model.User;

import java.util.List;

public interface UserDAO {
     int save(User user);

     int update(User user, int id);

    int delete(int id);
    List<User> getAll();

    User getById(int id);

    User getByNumber(String number);

}
