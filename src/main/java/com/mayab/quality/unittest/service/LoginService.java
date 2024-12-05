package com.mayab.quality.unittest.service;

import com.mayab.quality.unittest.dao.IDAOUser;
import com.mayab.quality.unittest.model.User;

public class LoginService {
	private IDAOUser dao;
	
	public LoginService(IDAOUser dao) {
		this.dao = dao;
	}
	
	public boolean login(String email, String pass) {
		User user = dao.findByUsername(email);
		if(user != null) {
			if(user.getPassword()==pass) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return false;
		}
	}
}
