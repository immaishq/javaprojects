package com.mpeservices.repository;

import java.util.LinkedHashMap;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.mpeservices.entity.UserRegistration;

@Repository
public interface UserRegistrationRepository extends JpaRepository<UserRegistration, Long>{

	UserRegistration findByMobileNumber(String mobileNumber);

	UserRegistration findByEmail(String email);

	UserRegistration findByUsername(String username);

	

}
