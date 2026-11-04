package com.packtpub.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import com.packtpub.model.User;
import com.packtpub.model.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository; 
	public UserServiceImpl(UserRepository userRepository) { 
		this.userRepository = userRepository;
		}

	@Override
	public List<User> getAllUsers(){
		return userRepository.findAll(Sort.by("userid"));
	}
	
	@Override
	public User getUser(Integer userid) {
		return userRepository.findById(userid)
			    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + userid + " not found"));
	}
	
	@Override
	public void createUser(Integer userid, String username) {
		if (userRepository.existsById(userid)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "User " + userid + " already exists");
			}
		userRepository.save(new User(userid, username));
	}

	@Override
	public void updateUser(Integer userid, String username) {
		User user = userRepository.findById(userid)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + userid + " not found"));
		user.setUsername(username);
		userRepository.save(user);
		}

	@Override
	public void deleteUser(Integer userid) {
		if (!userRepository.existsById(userid)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User " + userid + " not found");
			}
		userRepository.deleteById(userid);
		}
	
}
