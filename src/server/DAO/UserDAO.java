/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.DAO;

import client.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author quang
 */
public class UserDAO extends DAO {

    public UserDAO() {
        super();
    }

    public User verifyUser(User user) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT *\n"
                    + "FROM user\n"
                    + "WHERE username = ?\n"
                    + "AND password = ?");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7),
                        (rs.getInt(8) != 0),
                        (rs.getInt(9) != 0),
                        getRank(rs.getInt(1)));
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public int getRank(int ID) {
        int rank = 1;
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT user.ID\n"
                    + "FROM user\n"
                    + "ORDER BY (user.NumberOfGame+user.numberOfDraw*5+user.NumberOfWin*10) DESC");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) == ID) {
                    return rank;
                }
                rank++;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return -1;
    }

    public boolean checkDuplicated(String username) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT * FROM user WHERE username = ?");
            preparedStatement.setString(1, username);
            System.out.println(preparedStatement);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return true;
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void addUser(User user) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("INSERT INTO user(username, password, nickname)\n"
                    + "VALUES(?,?,?)");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getNickname());
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToOnline(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 1\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void updateToOffline(int ID) {
        try {
            PreparedStatement preparedStatement = con.prepareStatement("UPDATE user\n"
                    + "SET IsOnline = 0\n"
                    + "WHERE ID = ?");
            preparedStatement.setInt(1, ID);
            System.out.println(preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}