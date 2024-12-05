package com.mayab.quality.unittest.service;

import java.util.ArrayList;
import java.util.List;

import com.mayab.quality.unittest.dao.IDAOUser;
import com.mayab.quality.unittest.model.User;

public class UserService {
	
	private IDAOUser dao;
	
	public UserService(IDAOUser dao) {
		this.dao = dao;
	}
	
	public User createUser(String name, String email, String password) {
	    if (password.length() >= 8 && password.length() <= 16) {
	        User existingUser = dao.findUserByEmail(email);

	        if (existingUser == null) {
	            User newUser = new User(name, email, password);
	            int generatedId = dao.save(newUser);

	            if (generatedId > 0) {
	                newUser.setId(generatedId);
	                return newUser;
	            }
	        }
	    }
	    return null;
	}

	
	public List<User> findAllUsers(){
		List<User> users = new ArrayList<User>();
		users = dao.findAll();
	
		return users;
	}

	public User findUserByEmail(String email) {
		
		return dao.findUserByEmail(email);
	}

	public User findUserById(int id) {
		
		return dao.findById(id);
	}
    
    public User updateUser(User user) {
    	User userOld = dao.findById(user.getId());
    	userOld.setName(user.getName());
    	userOld.setPassword(user.getPassword());
    	return dao.updateUser(userOld);
    }

    public boolean deleteUser(int id) {
    	return dao.deleteById(id);
    }
}