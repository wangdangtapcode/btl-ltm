/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.model;

import java.io.IOException;
import server.DAO.UserDAO;
import server.controller.Server;
import server.controller.ServerThread;

/**
 *
 * @author quang
 */
public class Room {

    private int ID;
    private ServerThread user1;
    private ServerThread user2;
    private String password;
    private UserDAO userDAO;
    private int soHat;
    private int Time;
    public int getID() {
        return ID;
    }

    public int getSoHat() {
        return soHat;
    }

    public void setSoHat(int soHat) {
        this.soHat = soHat;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int Time) {
        this.Time = Time;
    }

    public ServerThread getUser2() {
        return user2;
    }

    public ServerThread getUser1() {
        return user1;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Room(ServerThread user1) {
        System.out.println("Tạo phòng thành công, ID là: " + Server.ID_room);
        this.password = " ";
        this.ID = Server.ID_room++;
        userDAO = new UserDAO();
        this.user1 = user1;
        this.user2 = null;
    }

    public int getNumberOfUser() {
        return user2 == null ? 1 : 2;
    }

    public void setUser2(ServerThread user2) {
        this.user2 = user2;
    }

    public void boardCast(String message) {
        try {
            user1.write(message);
            user2.write(message);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public int getCompetitorID(int ID_ClientNumber) {
        if (user1.getClientNumber() == ID_ClientNumber) {
            return user2.getUser().getID();
        }
        return user1.getUser().getID();
    }

    public ServerThread getCompetitor(int ID_ClientNumber) {
        if (user1.getClientNumber() == ID_ClientNumber) {
            return user2;
        }
        return user1;
    }

    public void setUsersToPlaying() {
        userDAO.updateToPlaying(user1.getUser().getID());
        if (user2 != null) {
            userDAO.updateToPlaying(user2.getUser().getID());
        }
    }

    public void setUsersToNotPlaying() {
        userDAO.updateToNotPlaying(user1.getUser().getID());
        if (user2 != null) {
            userDAO.updateToNotPlaying(user2.getUser().getID());
        }
    }

    public void increaseNumberOfGame() {
        userDAO.addGame(user1.getUser().getID());
        userDAO.addGame(user2.getUser().getID());
    }

    public void increaseNumberOfDraw() {
        userDAO.addDrawGame(user1.getUser().getID());
        userDAO.addDrawGame(user2.getUser().getID());
    }

    public void decreaseNumberOfGame() {
        userDAO.decreaseGame(user1.getUser().getID());
        userDAO.decreaseGame(user2.getUser().getID());
    }

}
