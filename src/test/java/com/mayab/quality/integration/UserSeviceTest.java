package com.mayab.quality.integration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.greaterThan;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.util.List;

import org.dbunit.Assertion;
import org.dbunit.DBTestCase;
import org.dbunit.PropertiesBasedJdbcDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.ext.mysql.MySqlDataTypeFactory;
import org.dbunit.operation.DatabaseOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.mayab.quality.unittest.dao.IDAOUser;
import com.mayab.quality.unittest.dao.UserMysqlDAO;
import com.mayab.quality.unittest.model.User;
import com.mayab.quality.unittest.service.UserService;

class UserSeviceTest extends DBTestCase{
	
	private IDAOUser dao;
	private UserService service;
	private String dataSetFile = "src/test/resources/initDB.xml";
	
	public UserSeviceTest() {
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_DRIVER_CLASS, "com.mysql.cj.jdbc.Driver");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_CONNECTION_URL, "jdbc:mysql://127.0.0.1:3306/dbunit");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_USERNAME, "root");
		System.setProperty(PropertiesBasedJdbcDatabaseTester.DBUNIT_PASSWORD, "123456");
	}

	@BeforeEach
	protected void setUp() throws Exception {
		dao = new UserMysqlDAO();
		service = new UserService(dao);
		
		IDatabaseConnection connection = getConnection();
		System.out.println("Database URL: " + connection.getConnection().getMetaData().getURL());
		System.out.println("Database Username: " + connection.getConnection().getMetaData().getUserName());

		try {
			DatabaseOperation.TRUNCATE_TABLE.execute(connection,getDataSet());
			DatabaseOperation.CLEAN_INSERT.execute(connection, getDataSet());						
		} catch(Exception e) {
			e.printStackTrace(); 
			fail("Error in setup: "+ e.getMessage()); 
		} finally {
			connection.close(); 
		}
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
        return new FlatXmlDataSetBuilder().build(new FileInputStream(dataSetFile));
	}
	
	private void verifyDatabaseState(String expectedDataSetFilePath) throws Exception {
        IDataSet expectedDataSet = new FlatXmlDataSetBuilder().build(new FileInputStream(expectedDataSetFilePath));
        ITable expectedTable = expectedDataSet.getTable("usuarios");

        IDatabaseConnection connection = getConnection();
        ITable actualTable = connection.createDataSet().getTable("usuarios");
        
        Assertion.assertEquals(expectedTable, actualTable);
    }
	
	@Test
	void whenAllDataCorrect_saveUser_test() throws Exception {
	    User user = new User("new user", "newuser@example.com", "12345678");
	    User result = service.createUser(user.getName(), user.getEmail(), user.getPassword());
	    System.out.println("Result user: " + result);

	    assertThat("User should be saved successfully", result, is(notNullValue()));
	    assertThat("Generated user ID should be valid", result.getId(), is(greaterThan(0)));

	    verifyDatabaseState("src/test/resources/expectedSaveUser.xml");
	}


	
	@Test
	void duplicatedUserEmail_test() throws Exception {
	    User existingUser = new User("existing user", "existing@example.com", "12345678");
	    service.createUser(existingUser.getName(), existingUser.getEmail(), existingUser.getPassword());

	    User duplicateUser = new User("duplicate user", "existing@example.com", "87654321");   
	    User result = service.createUser(duplicateUser.getName(), duplicateUser.getEmail(), duplicateUser.getPassword());

	    assertThat(result, is(nullValue()));
	    verifyDatabaseState("src/test/resources/expectedDuplicateUserEmail.xml");
	}
	
	@Test
	void whenPasswordShort_test() throws Exception {
	    User shortPasswordUser = new User("short password user", "shortpassword@example.com", "123");

	    User result = service.createUser(shortPasswordUser.getName(), shortPasswordUser.getEmail(), shortPasswordUser.getPassword());

	    assertThat(result, is(nullValue()));
	    verifyDatabaseState("src/test/resources/initDB.xml");
	}
	
	
	@Test
	void whenPasswordLong_test() throws Exception {
	    User longPasswordUser = new User("long password user", "longpassword@example.com", "123456789012345678901");

	    User result = service.createUser(longPasswordUser.getName(), longPasswordUser.getEmail(), longPasswordUser.getPassword());

	    assertThat(result, is(nullValue()));
	    verifyDatabaseState("src/test/resources/initDB.xml");
	}
	
	@Test
	void whenUserUpdateData_test() throws Exception {
	    User user = new User("user updated", "updated@example.com", "12345678");
	    User savedUser = service.createUser(user.getName(), user.getEmail(), user.getPassword());

	    User updatedUser = service.updateUser(savedUser);

	    assertThat(updatedUser.getName(), is("user updated"));
	    assertThat(updatedUser.getEmail(), is("updated@example.com"));

	    verifyDatabaseState("src/test/resources/expectedUpdateUser.xml");
	}
	
	@Test
	void whenUserDeleted_test() throws Exception {

	    User userToDelete = new User("user to delete", "delete@example.com", "12345678");
	    User savedUser = service.createUser(userToDelete.getName(), userToDelete.getEmail(), userToDelete.getPassword());

	    boolean delete = service.deleteUser(savedUser.getId());

	    assertThat(delete, is(true));

	    verifyDatabaseState("src/test/resources/initDB.xml");
	}

	@Test
	void findAllUsers_test() throws Exception {
	    User user1 = new User("user1", "user1@example.com", "12345678");
	    User user2 = new User("user2", "user2@example.com", "12345678");
	    service.createUser(user1.getName(), user1.getEmail(), user1.getPassword());
	    service.createUser(user2.getName(), user2.getEmail(), user2.getPassword());

	    List<User> users = service.findAllUsers();

	    assertThat(users, hasSize(2));
	    assertThat(users.get(0).getName(), is("user1"));
	    assertThat(users.get(1).getName(), is("user2"));

	    verifyDatabaseState("src/test/resources/expectedFindAllUsers.xml");
	}
	
	@Test
	void findUserByEmail_test() throws Exception {
	    User user = new User("user email", "email@example.com", "12345678");
	    service.createUser(user.getName(), user.getEmail(), user.getPassword());

	    User foundUser = service.findUserByEmail(user.getEmail());

	    assertThat(foundUser, is(notNullValue()));
	    assertThat(foundUser.getEmail(), is(user.getEmail()));

	    verifyDatabaseState("src/test/resources/expectedFindUserByEmail.xml");
	}
	
	@Test
	void findUserById_test() throws Exception {
	    User user = new User("user by id", "id@example.com", "12345678");
	    User savedUser = service.createUser(user.getName(), user.getEmail(), user.getPassword());

	    User foundUser = service.findUserById(savedUser.getId());

	    assertThat(foundUser, is(notNullValue()));
	    assertThat(foundUser.getId(), is(savedUser.getId()));

	    verifyDatabaseState("src/test/resources/expectedFindUserById.xml");
	}


}
