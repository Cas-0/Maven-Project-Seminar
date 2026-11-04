package com.packtpub.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.packtpub.model.User;


public interface UserRepository extends JpaRepository<User, Integer>{
	
}