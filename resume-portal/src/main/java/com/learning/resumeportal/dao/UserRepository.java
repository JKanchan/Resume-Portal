package com.learning.resumeportal.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.learning.resumeportal.model.User;

public interface UserRepository extends JpaRepository<User,Integer>{
	
	Optional<User> findByUserName(String userName);

}
