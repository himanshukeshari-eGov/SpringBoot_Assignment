package com.postgresjdbc.assiHim.dao;

import com.postgresjdbc.assiHim.model.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class UserDAOimpl implements UserDAO{
    @Autowired
  public JdbcTemplate jdbcTemplate;


    @Override
    public int save(User user) {
        return jdbcTemplate.update("insert into egov (id,name,gender,address,number) values(?,?,?,?,?)", new Object[]{user.getId(),user.getName(),user.getGender(),user.getAddress(),user.getNumber()});

    }

    @Override
    public int update(User user, int id) {
        return jdbcTemplate.update("update egov set name=?, gender=?,address=?,number=? where id=?", new Object[]{user.getName(),user.getGender(),user.getAddress(),user.getNumber(),id});
    }

    @Override
    public int delete(int id) {
        return jdbcTemplate.update("delete from egov where id=?",id);
    }

    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("select * from egov", new BeanPropertyRowMapper<User>(User.class));
    }

    public User getById(int id) {
        return jdbcTemplate.queryForObject("select * from egov where id=?", new BeanPropertyRowMapper<User>(User.class), id);
    }

    @Override
    public User getByNumber(String number) {
        return jdbcTemplate.queryForObject("select * from egov where number=?", new BeanPropertyRowMapper<User>(User.class),number.toString());
    }


}