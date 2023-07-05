package com.postgresjdbc.assiHim.dao;

import com.postgresjdbc.assiHim.model.User;
import com.postgresjdbc.assiHim.model.UserSearchCriteria;

import java.util.List;
import java.util.UUID;

public interface UserDAO {
     int save(User user);

     User update(User user, UUID id);

    int delete(UUID id);
    List<User> getAll();

    User getById(UUID id);

    User getByNumber(String number);

    List<User> getByActive(Boolean active);

    User getByUserSearchCriteria(UserSearchCriteria user);


    boolean isUniqueUser(String name, String number, UUID userId);



}
