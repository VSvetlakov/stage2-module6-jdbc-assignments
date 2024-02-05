package jdbc;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SimpleJDBCRepository {

    private Connection connection = null;
    private PreparedStatement ps = null;
    private Statement st = null;

    private static final String createUserSQL = "INSERT INTO myusers (\"firstName\", \"lastName\", age) VALUES (?, ?, ?)";
    private static final String updateUserSQL = "UPDATE myusers SET \"firstName\" = ?, \"lastName\" = ?, age = ? WHERE id = ?";
    private static final String deleteUser = "DELETE FROM myusers WHERE id = ?";
    private static final String findUserByIdSQL = "SELECT * FROM myusers WHERE id = ?";
    private static final String findUserByNameSQL = "SELECT * FROM myusers WHERE \"firstName\" = ?";
    private static final String findAllUserSQL = "SELECT * FROM myusers";

    public Long createUser(User user) {
        Long generatedKey = null;
        try {
            ps = connection.prepareStatement(createUserSQL,Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.executeUpdate();

            ResultSet keyset  = ps.getGeneratedKeys();
            if (keyset .next()) {
                generatedKey = (long) keyset.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generatedKey;
    }

    public User findUserById(Long userId) {
        User user = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(findUserByIdSQL);
            ps.setLong(1, userId);

            resultSet = ps.executeQuery();
            if (resultSet.next()) {

                user = new User();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getInt("age"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public User findUserByName(String userName) {
        User user = null;
        ResultSet resultSet = null;
        try {
            ps = connection.prepareStatement(findUserByNameSQL);
            ps.setString(1, userName);

            resultSet = ps.executeQuery();
            if (resultSet.next()) {

                user = new User();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getInt("age"));

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public List<User> findAllUser()  {

        List<User> users = new ArrayList<>();

        ResultSet resultSet = null;

        try {
            st = connection.createStatement();
            resultSet = st.executeQuery(findAllUserSQL);
            while (resultSet.next()) {
                User user = new User();
                user.setId(resultSet.getLong("id"));
                user.setFirstName(resultSet.getString("firstName"));
                user.setLastName(resultSet.getString("lastName"));
                user.setAge(resultSet.getInt("age"));
                users.add(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return users;
    }

    public User updateUser(User user) {

        try {
            ps = connection.prepareStatement(updateUserSQL);
            ps.setString(1, user.getFirstName());
            ps.setString(2, user.getLastName());
            ps.setInt(3, user.getAge());
            ps.setLong(4, user.getId());
            int rowAffected = ps.executeUpdate();
            if (rowAffected>0) {
                ps.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }

    private void deleteUser(Long userId) {

        try {
            ps = connection.prepareStatement(deleteUser);
            ps.setLong(1, userId);
            int rowAffected = ps.executeUpdate();
            if (rowAffected>0) {
                ps.close();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
