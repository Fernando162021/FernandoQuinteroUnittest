package com.mayab.quality.unittest.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.mayab.quality.unittest.model.User;

public class UserMysqlDAO implements IDAOUser {

	public Connection getConnectionMySQL() {

		Connection con = null;
		try {
			// Establish the driver connector
			Class.forName("com.mysql.cj.jdbc.Driver");
			// Set the URI for connecting the MySql database
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbunit", "root", "123456");
		} catch (Exception e) {
			System.out.println(e);
		}
		return con;
	}

	@Override
	public User findByUsername(String name) {
		Connection connection = getConnectionMySQL();
		PreparedStatement preparedStatement;
		ResultSet rs;
		
		User result = null;

		try {
			// Declare statement query to run
			preparedStatement = connection.prepareStatement("SELECT * from usuarios WHERE name = ?");
			// Set the values to match in the ? on query
			preparedStatement.setString(1, name);
			rs = preparedStatement.executeQuery();

			// Obtain the pointer to the data in generated table
			rs.next();

			int id = rs.getInt(1);
			String username  = rs.getString(2);
			String email = rs.getString(3);
			String password = rs.getString(4);
			boolean isLogged = rs.getBoolean(5);

			result = new User(username, email, password);
			result.setId(id);
			result.setLogged(isLogged);

			// Return the values of the search
			System.out.println("\n");
			System.out.println("---usuario---");
			System.out.println("ID: " + result.getId());
			System.out.println("Nombre: " + result.getName());
			System.out.println("Email: " + result.getEmail());
			System.out.println("Tipo: " + result.isLogged() + "\n");
			// Close connection with the database
			connection.close();
			rs.close();
			preparedStatement.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		// Return statement
		return result;
					
	}

	@Override
	public int save(User user) {
	    int result = 0;

	    try (Connection connection = getConnectionMySQL();
	         PreparedStatement preparedStatement = connection.prepareStatement(
	                 "INSERT INTO usuarios(name, email, password, isLogged) VALUES (?, ?, ?, ?)",
	                 Statement.RETURN_GENERATED_KEYS)) {

	        preparedStatement.setString(1, user.getName());
	        preparedStatement.setString(2, user.getEmail());
	        preparedStatement.setString(3, user.getPassword());
	        preparedStatement.setBoolean(4, user.isLogged());

	        if (preparedStatement.executeUpdate() > 0) {
	            try (ResultSet rs = preparedStatement.getGeneratedKeys()) {
	                if (rs.next()) {
	                    result = rs.getInt(1);
	                }
	            }
	        }
	        System.out.println("Alumno añadido con éxito");
	        System.out.println(">> ID generado: " + result);

	    } catch (Exception e) {
	        System.out.println("Error en la inserción del usuario: " + e.getMessage());
	    }
	    
	    return result;
	}


	@Override
	public User findUserByEmail(String email) {
		Connection connection = getConnectionMySQL();
		PreparedStatement preparedStatement;
		ResultSet rs;
		
		User result = null;

		try {
			// Declare statement query to run
			preparedStatement = connection.prepareStatement("SELECT * from usuarios WHERE email = ?");
			// Set the values to match in the ? on query
			preparedStatement.setString(1, email);
			rs = preparedStatement.executeQuery();

			// Obtain the pointer to the data in generated table
			rs.next();

			int id = rs.getInt(1);
			String username  = rs.getString(2);
			String emailUser = rs.getString(3);
			String password = rs.getString(4);
			boolean isLogged = rs.getBoolean(5);

			result = new User(username, emailUser, password);
			result.setId(id);
			result.setLogged(isLogged);

			// Return the values of the search
			System.out.println("\n");
			System.out.println("---Alumno---");
			System.out.println("ID: " + result.getId());
			System.out.println("Nombre: " + result.getName());
			System.out.println("Email: " + result.getEmail());
			System.out.println("Tipo: " + result.isLogged() + "\n");
			// Close connection with the database
			connection.close();
			rs.close();
			preparedStatement.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		// Return statement
		return result;
					
	}

	@Override
	public List<User> findAll() {
		Connection connection = getConnectionMySQL();
		  PreparedStatement preparedStatement;
		  ResultSet rs;
		  boolean result = false;

		  User retrieved = null;

		  List<User> listaAlumnos = new ArrayList<User>();
		  
		  try {
		   // Declare statement query to run
		   preparedStatement = connection.prepareStatement("SELECT * from usuarios");
		   // Set the values to match in the ? on query
		   rs = preparedStatement.executeQuery();

		   // Obtain the pointer to the data in generated table
		   while (rs.next()) {

			   int id = rs.getInt(1);
			   String name = rs.getString(2);
			   String email = rs.getString(3);
			   String password = rs.getString(4);
			   boolean log = rs.getBoolean(5);		 
			   retrieved = new User(name, email, password);
			   retrieved.setId(id);
			   retrieved.setLogged(log);
			   listaAlumnos.add(retrieved);
		   }
		   
			   connection.close();
			   rs.close();
			   preparedStatement.close();
	
			  } catch (Exception e) {
			   System.out.println(e);
			  }
			  return listaAlumnos;
		  
		}

	

	@Override
	public User findById(int id) {
		Connection connection = getConnectionMySQL();
		PreparedStatement preparedStatement;
		ResultSet rs;
		
		User result = null;

		try {
			// Declare statement query to run
			preparedStatement = connection.prepareStatement("SELECT * from usuarios WHERE id = ?");
			// Set the values to match in the ? on query
			preparedStatement.setInt(1, id);
			rs = preparedStatement.executeQuery();

			// Obtain the pointer to the data in generated table
			rs.next();

			int idUser = rs.getInt(1);
			String username  = rs.getString(2);
			String email = rs.getString(3);
			String password = rs.getString(4);
			boolean isLogged = rs.getBoolean(5);

			result = new User(username, email, password);
			result.setId(id);
			result.setLogged(isLogged);

			// Return the values of the search
			System.out.println("\n");
			System.out.println("---Alumno---");
			System.out.println("ID: " + result.getId());
			System.out.println("Nombre: " + result.getName());
			System.out.println("Email: " + result.getEmail());
			System.out.println("Tipo: " + result.isLogged() + "\n");
			// Close connection with the database
			connection.close();
			rs.close();
			preparedStatement.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		// Return statement
		return result;
					
	}

	@Override
	public boolean deleteById(int id) {
	    try (Connection connection = getConnectionMySQL()) {
	        boolean result = false;

	        String query = "DELETE FROM usuarios WHERE id = ?";
	        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
	            preparedStatement.setInt(1, id);
	            int rowsAffected = preparedStatement.executeUpdate();
	            System.out.println("Rows affected: " + rowsAffected);

	            if (rowsAffected >= 1) {
	                result = true;
	                System.out.println("Registro eliminado con éxito");
	            } else {
	                System.out.println("No se encontró un registro con el id especificado");
	            }
	        } catch (Exception e) {
	            System.out.println("Error al eliminar el registro: " + e.getMessage());
	        }

	        return result;
	    } catch (Exception e) {
	        System.out.println("Error en la conexión: " + e.getMessage());
	        return false;
	    }
	}


	@Override
	public User updateUser(User newUser) {
		Connection connection = getConnectionMySQL();
		User result = null;

		try {
			// Declare statement query to run
			PreparedStatement preparedStatement;
			preparedStatement = connection.prepareStatement("UPDATE usuarios SET name = ?,password= ? WHERE id = ?");
			// Set the values to match in the ? on query
			preparedStatement.setString(1, newUser.getName());
			preparedStatement.setString(2, newUser.getPassword());
			preparedStatement.setInt(3, newUser.getId());
			// Return the result of connection and statement
			if (preparedStatement.executeUpdate() >= 1) {
				result = newUser;
			}
			System.out.println("\n");
			// Close connection with the database
			connection.close();
			preparedStatement.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		// Return statement
		return result;
	
	}

}