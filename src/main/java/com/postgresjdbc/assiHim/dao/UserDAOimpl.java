
package com.postgresjdbc.assiHim.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.postgresjdbc.assiHim.model.Address;
import com.postgresjdbc.assiHim.model.Coordinates;
import com.postgresjdbc.assiHim.model.User;
import com.postgresjdbc.assiHim.model.UserSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class UserDAOimpl implements UserDAO {

    @Value("${api.address}")
    private String url;


    @Autowired
    public JdbcTemplate jdbcTemplate;

    private static final UserMapper USER_MAPPER = new UserMapper();

    /**
     *
     * @param name of the user
     * @param number of the user
     * @param userId
     * @return true or false according to the name and number unique combination present or not.
     */

    @Override
    public boolean isUniqueUser(String name, String number, UUID userId) {
        String query = "SELECT COUNT(*) FROM user_table WHERE (name = ? AND number = ?) AND id <> ?";
        int count = jdbcTemplate.queryForObject(query, Integer.class, name, number, userId);
        return count == 0;
    }

    /**
     * In updating the data we take
     * @param user that you have to update with
     * @param id this.
     * @return this will return updated user.
     */
    @Override
    public User update(User user, UUID id) {
        User old_user= getById(id);

        Long currentTime = old_user.getCreatedTime();
        if(currentTime==null)
            currentTime = System.currentTimeMillis();

        jdbcTemplate.update("UPDATE user_table SET name = ?, gender = ?,  number = ?, active = ? , createdTime=? WHERE id = ?",
                user.getName(), user.getGender(), user.getNumber(), user.getActive(),currentTime ,id);
        old_user=getById(id);
        return old_user;
    }


    /**
     * This is for delete the user by using id
     * @param id taking the id .
     * @return 1 or 0 . 1 mean successfully deleted and 0 means unsuccessful.
     */
    @Override
    public int delete(UUID id) {
        return jdbcTemplate.update("DELETE FROM user_table WHERE id = ?", id);
    }



    /**
     * This is for getting your all user.
     * @return list of all users.
     */
    @Override
    public List<User> getAll() {
        return jdbcTemplate.query("SELECT * FROM user_table", USER_MAPPER);
    }



    /**
     * to Getting the user by using Id.
     * @param id taking id
     * @return the user whose id is equal to the id provided in param.
     */
    public User getById(UUID id) {
        return jdbcTemplate.queryForObject("SELECT * FROM user_table WHERE id = ?", USER_MAPPER, id);
    }


    /**
     * Getting the user by using MobileNumber
     * @param number taking number as param
     * @return user.
     */
    @Override
    public User getByNumber(String number) {
        return jdbcTemplate.queryForObject("SELECT * FROM user_table WHERE number = ?", USER_MAPPER, number);
    }


    /**
     * Getting the users on the basis of active or not.
     * @param active take param as true or false
     * @return all users whose active status is true or false according to param.
     */
    @Override
    public List<User> getByActive(Boolean active) {
        return jdbcTemplate.query("SELECT * FROM user_table WHERE active = ?", USER_MAPPER, active);
    }



    @Override
    public User getByUserSearchCriteria(UserSearchCriteria searchCriteria) {
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM user_table WHERE id = ? AND number = ?", USER_MAPPER,
                    searchCriteria.getId(), searchCriteria.getNumber());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }


    /**
     * At the time of creation of users. Here we get Address by using an API and also added the creation time.
     */
    @Autowired
    private RestTemplateBuilder restTemplateBuilder;
    @Override
    public int save(User user) {
        // Check if the user already exists
        if (isUserExists(user.getName(), user.getNumber())) {
            return 0; // User already exists, return 0 to indicate not created
        }
        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        Map<String, Object> data = response.getBody();
        Map<String, Object> addressData = (Map<String, Object>) data.get("address");
        Address address = new Address();
        address.setCity((String) addressData.get("city"));
        address.setStreet_name((String) addressData.get("street_name"));
        address.setStreet_address((String) addressData.get("street_address"));
        address.setZip_code((String) addressData.get("zip_code"));
        address.setState((String) addressData.get("state"));
        address.setCountry((String) addressData.get("country"));
        Map<String, Object> coordinatesData = (Map<String, Object>) addressData.get("coordinates");
        Coordinates coordinates = new Coordinates();
        coordinates.setLat((Double) coordinatesData.get("lat"));
        coordinates.setLng((Double) coordinatesData.get("lng"));
        address.setCoordinates(coordinates);

        ObjectMapper objectMapper = new ObjectMapper();
        String addressJson;
        try {
            addressJson = objectMapper.writeValueAsString(address);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing address object to JSON", e);
        }
        UUID uuid = UUID.randomUUID(); // Generate a UUID
        Long currentTime = System.currentTimeMillis();
        user.setCreatedTime(currentTime);
        user.setId(uuid); // Set the UUID as the id of the user
        user.setAddress(address);

        return jdbcTemplate.update("INSERT INTO user_table (id, name, gender, address, number, active, createdTime) VALUES (?, ?, ?, ?::json, ?, ?, ?)",
                user.getId(), user.getName(), user.getGender(),addressJson, user.getNumber(), user.getActive(), currentTime);
    }


    /**
     * Helper method to check if a user with the same name and mobile number already exists
     * @param name
     * @param number
     * @return true or false
     */
    private boolean isUserExists(String name, String number) {
        String query = "SELECT COUNT(*) FROM user_table WHERE name = ? AND number = ?";
        int count = jdbcTemplate.queryForObject(query, Integer.class, name, number);
        return count > 0;
    }


    /**
     * This the User Mapper in place of Bean Row Mapper
     */
    private static class UserMapper implements RowMapper<User> {
        @Override
        public User mapRow(ResultSet rs, int rowNum) throws SQLException {
            User user = new User();
            user.setId((UUID) rs.getObject("id"));
            user.setName(rs.getString("name"));
            user.setNumber(rs.getString("number"));
            user.setGender(rs.getString("gender"));
            String addressJson = rs.getString("address");
            if (addressJson != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                Address address = null;
                try {
                    address = objectMapper.readValue(addressJson, Address.class);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                user.setAddress(address);
            }


            user.setActive(rs.getBoolean("active"));
            user.setCreatedTime(rs.getLong("createdTime"));
            return user;
        }
    }
}
