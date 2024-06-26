package com.mpeservices.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mpeservices.bean.RequestBean;
import com.mpeservices.entity.UserRegistration;
import com.mpeservices.repository.UserRegistrationRepository;
import com.mpeservices.security.JwtUtil;

@RestController
@RequestMapping("/api/t1")
@CrossOrigin("*")
public class UserRegistrationController<LoginRequest> {
	
	  @Autowired
	    private UserRegistrationRepository userRegistrationRepository;

	    @Autowired
	    private PasswordEncoder passwordEncoder;

	    @Autowired
	    private JwtUtil jwtUtil;

	    @Autowired
	    private UserDetailsService userDetailsService; 

	@PostMapping("/user-registration")
	public ResponseEntity<ApiResponse<UserRegistration>> createUser(@RequestBody UserRegistration userRegistration) {
	   
		// Check if the email already exists
	    UserRegistration existingUserWithEmail = userRegistrationRepository.findByEmail(userRegistration.getEmail());
	    if (existingUserWithEmail != null) {
	        ApiResponse<UserRegistration> response = new ApiResponse<>(false, "Email already exists", null);
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	    
        //	    Check if the username already exists
	    UserRegistration existingUserWithUsername = userRegistrationRepository.findByUsername(userRegistration.getUsername());
	    if (existingUserWithUsername != null) {
	        ApiResponse<UserRegistration> response = new ApiResponse<>(false, "Username already exists", null);
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }

	    // Check if the mobile number already exists
	    UserRegistration existingUserWithMobileNumber = userRegistrationRepository.findByMobileNumber(userRegistration.getMobileNumber());
	    if (existingUserWithMobileNumber != null) {
	        ApiResponse<UserRegistration> response = new ApiResponse<>(false, "Mobile number already exists", null);
	        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
	    }
	    
	    // Encode password using BCryptPasswordEncoder before saving
        String encodedPassword = passwordEncoder.encode(userRegistration.getPassword());
        userRegistration.setPassword(encodedPassword);

	    // If email and mobile number are unique, proceed with user registration
	    UserRegistration savedUser = userRegistrationRepository.save(userRegistration);
	    ApiResponse<UserRegistration> response = new ApiResponse<>(true, "User registered successfully", savedUser);
	    return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	 @GetMapping("/get-users")
	    public ResponseEntity<ApiResponse<List<UserRegistration>>> getAllUsers() {
	        try {
	            List<UserRegistration> users = userRegistrationRepository.findAll();
	            ApiResponse<List<UserRegistration>> response = new ApiResponse<>(true, "Users retrieved successfully", users);
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } catch (Exception e) {
	            ApiResponse<List<UserRegistration>> response = new ApiResponse<>(false, "Failed to retrieve users: " + e.getMessage(), null);
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }
	 
//	 @PostMapping("/login")
//	 public ResponseEntity<ApiResponse<UserRegistration>> login(@RequestBody RequestBean userRegistration) {
//	     String username = userRegistration.getUsername();
//	     String password = userRegistration.getPassword();
//	     
//	     System.out.println(username);
//	     System.out.println(password);
//	     
//	     System.out.println("This is user registration Details");
//
//	     // Find user by username
//	     UserRegistration user = userRegistrationRepository.findByUsername(username);
//
//	     if (user == null) {
//	         // User not found
//	         ApiResponse<UserRegistration> response = new ApiResponse<>(false, "Invalid username or password", null);
//	         return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//	     }
//
//	     // Check if the user has a password set
//	     if (user.getPassword() == null) {
//	         // Password not set
//	         ApiResponse<UserRegistration> response = new ApiResponse<>(false, "Password not set for the user", null);
//	         System.out.println(response);
//	         return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//	     }
//
//	     // Check if the password matches
//	     if (!user.getPassword().equals(password)) {
//	         // Incorrect password
//	         ApiResponse<UserRegistration> response = new ApiResponse<>(false, "Invalid username or password", null);
//	         return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
//	     }
//
//	     // Authentication successful
//	     System.out.println("Login successful for user: " + username);
//	     System.out.println("User details: " + user.toString()); // Assuming 'user' has a meaningful toString() method
//
//	     ApiResponse<UserRegistration> response = new ApiResponse<>(true, "Login successful", user);
//	     return new ResponseEntity<>(response, HttpStatus.OK);
//	 }

	 @PostMapping("/generate-token")
	    public ResponseEntity<ApiResponse<String>> login(@RequestBody RequestBean requestBean) {
	        String username = requestBean.getUsername();
	        String password = requestBean.getPassword();

	        // Validate username and password (implement your logic)
	        UserRegistration user = userRegistrationRepository.findByUsername(username);

	        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
	            return new ResponseEntity<>(new ApiResponse<>(false, "Invalid username or password", null), HttpStatus.UNAUTHORIZED);
	        }

	        // Load UserDetails from UserDetailsService
	        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

	        // Generate JWT token using UserDetails
	        String token = jwtUtil.generateToken(username, user.getFirstName(), user.getLastName());
	        
	        System.out.println("Token Generated successfully");

	        // Return token in response
	        return new ResponseEntity<>(new ApiResponse<>(true, "Token Generated successful", token), HttpStatus.OK);
	        
	       
	    }
	 
//	 return the details of current user
	 @GetMapping("/current-user")
	    public ResponseEntity<ApiResponse<UserRegistration>> getCurrentUser(Principal principal) {
	        try {
	            UserRegistration user = (UserRegistration) this.userDetailsService.loadUserByUsername(principal.getName());
	            ApiResponse<UserRegistration> response = new ApiResponse<>(true, "User fetched successfully", user);
	            return new ResponseEntity<>(response, HttpStatus.OK);
	        } catch (Exception e) {
	            ApiResponse<UserRegistration> response = new ApiResponse<>(false, "Failed to fetch user", null);
	            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	    }

     
}
