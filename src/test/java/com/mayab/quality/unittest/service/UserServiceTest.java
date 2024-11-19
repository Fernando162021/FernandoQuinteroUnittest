package com.mayab.quality.unittest.service;

import static org.mockito.Mockito.mock;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import static org.hamcrest.Matchers.isEmptyOrNullString;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mayab.quality.unittest.dao.IDAOUser;
import com.mayab.quality.unittest.model.User;
import com.mayab.quality.unittest.service.UserService;

class UserServiceTest {
	
	private UserService service;
	private IDAOUser dao;
	private HashMap<Integer, User> db;
	
	@BeforeEach
	public void setUp() throws Exception {
		dao = mock(IDAOUser.class);
		service = new UserService(dao);
		db = new HashMap<Integer, User>();
	}
	
	/*
	@Test
	void whenPasswordShort_test() {
		//Initialization
		String shortPass = "123";
		String name = "user1";
		String email = "user1@email.com";
		User user = null;
		
		//Fake code for findUserByEmail & save methods
		when(dao.findUserByEmail(anyString())).thenReturn(null);
		when(dao.save(any(User.class))).thenReturn(0);
		
		//Exercise
		user = service.createUser(name, email, shortPass);
		
		//Verify
		assertThat(user, is(nullValue()));		
		
	}
	*/
	
	@Test
	void whenAllDataCorrect_saveUserTest() {
		//initialization
		int sizeBefore = db.size();
		
		User user = new User("newName", "newEmail", "newPassword");
		
		//Fake code for findUserByEmail & save
		when(dao.findByUsername(anyString())).thenReturn(null);
		when(dao.save(any(User.class))).thenAnswer(new Answer<Integer>() {
			public Integer answer(InvocationOnMock invocation) throws Throwable {
				// Set behavior in every invocation
		        User arg = (User) invocation.getArguments()[0];
		        // Simulate auto-increment ID by getting the current size of the db
		        int id = db.size() + 1;
		        db.put(id, arg);

		        System.out.println("Size after= " + db.size() + "\n");

		        // Return the ID of the saved user
		        return id;
			}
		}
		);
		
		//Exercise
		User savedUser = service.createUser(user.getName(), user.getEmail(), user.getPassword());
		
		//Verify
		assertThat(savedUser, notNullValue());
		assertThat(savedUser.getName(), is(user.getName()));
	    assertThat(savedUser.getEmail(), is(user.getEmail()));
	    assertThat(savedUser.getPassword(), is(user.getPassword()));
		
		int sizeAfter = db.size();
	    assertThat(sizeAfter, is(sizeBefore + 1));
	}
	
	@Test
	void duplicatedUserEmail_test() {
		// Initializer
		User existingUser = new User("Existing User", "duplicate@email.com", "password123");
	    existingUser.setId(1);
	    db.put(1, existingUser);
	    
	    int sizeBefore = db.size();
	    
	    // Fake code
	    when(dao.findUserByEmail("duplicate@email.com")).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				String email = (String) invocation.getArguments()[0];
				return db.values().stream()
						.filter(user -> user.getEmail().equals(email))
		                .findFirst()
		                .orElse(null);
			}
	    	
	    });
	    
	    // Exercise
	    User newUser = service.createUser("New User", "duplicate@email.com", "newPassword");
	    
	    // Verify
	    assertThat(newUser, is(nullValue()));
	    assertThat(db.size(), is(sizeBefore));
	    
	}
	
	@Test
	void whenUserUpdateData_test() {
		//Initialization
		User oldUser = new User("oldUser", "oldEmail", "oldPassword");
		oldUser.setId(1);
		db.put(1, oldUser);
		
		User newUser = new User("newUser", "newEmail", "newPassword");
		newUser.setId(1);
		//Fake code for findById and updateUser
		when(dao.findById(1)).thenReturn(oldUser);
		when(dao.updateUser(any(User.class))).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				User arg = (User) invocation.getArguments()[0];
				db.replace(arg.getId(), arg);
				
				return db.get(arg.getId());
			}
			
		}
		);
		
		//Exercise
		User result = service.updateUser(newUser);
		
		//Verification
		assertThat(result.getName(), is(newUser.getName()));
		assertThat(result.getPassword(), is(newUser.getPassword()));
	}
	
	@Test
	void whenUserDeleted_test() {
		User user = new User("deleteUser", "deleteEmail", "deletePassword123");
        user.setId(1);
        db.put(user.getId(), user);

        // Fake code for findById and deleteUser
        when(dao.findById(1)).thenReturn(user);
        when(dao.deleteById(1)).thenAnswer(new Answer<Boolean>() {
            @Override
            public Boolean answer(InvocationOnMock invocation) {
                int id = (int) invocation.getArguments()[0];
                return db.remove(id) != null;
            }
        });

        // Exercise
        boolean isDeleted = service.deleteUser(user.getId());

        // Verification
        assertThat(isDeleted, is(true));
        assertThat(db.containsKey(user.getId()), is(false));
	}
	
	@Test
	void findUserByEmail_test() {
		//Initialize
		User user = new User("Test User", "testuser@email.com", "testPassword");
	    user.setId(1);
	    db.put(1, user);

        // Fake code for findUserByEmail
        when(dao.findUserByEmail("testuser@email.com")).thenAnswer(new Answer<User>(){

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				String email = (String) invocation.getArguments()[0];
	            return db.values().stream()
	                     .filter(user -> user.getEmail().equals(email))
	                     .findFirst()
	                     .orElse(null);
			}
        	
        });

        // Exercise
        User result = service.findUserByEmail("testuser@email.com");

        // Verify
        assertThat(result, is(notNullValue()));
        assertThat(result.getName(), is(user.getName()));
        assertThat(result.getPassword(), is(user.getPassword()));
	}
	
	@Test
	void UserNotFoundByEmail_test() {
		// Initialization
		db.clear();
		
		// Fake code
		when(dao.findUserByEmail("nonexistentEmail")).thenAnswer(new Answer<User>() {

			@Override
			public User answer(InvocationOnMock invocation) throws Throwable {
				String email = (String) invocation.getArguments()[0];
				return db.values().stream()
		                 .filter(user -> user.getEmail().equals(email))
		                 .findFirst()
		                 .orElse(null);
			}
			
		});
		
		// Exercise
		User result = service.findUserByEmail("nonexistentEmail");
		
		// Verify
		assertThat(result, is(nullValue()));
	}
	
	@Test
	void findAllUsers_test() {
		//Initialization
		User user1 = new User("John Doe", "johndoe@email.com", "password123");
		user1.setId(1);
		User user2 = new User("Jane Smith", "janesmith@email.com", "password456");
		user2.setId(2);
		
		db.put(user1.getId(), user1);
        db.put(user2.getId(), user2);
        
		//Fake code for findAll
        when(dao.findAll()).thenReturn(new ArrayList<>(db.values()));
		 
		//Exercise
		List<User> result = service.findAllUsers();
		 
		//Verification
		assertThat(result, is(new ArrayList<>(db.values())));
        assertThat(result, hasSize(2));
        assertThat(result.get(0).getName(), is("John Doe"));
        assertThat(result.get(1).getName(), is("Jane Smith"));
		 
	}
	
}
