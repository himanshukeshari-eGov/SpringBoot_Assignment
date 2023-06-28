package com.postgresjdbc.assiHim;


import com.postgresjdbc.assiHim.dao.UserDAOimpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AssiHimApplication  {


	public static void main(String[] args) {
		SpringApplication.run(AssiHimApplication.class, args);
	}


}
