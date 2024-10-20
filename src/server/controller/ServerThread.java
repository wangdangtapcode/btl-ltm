/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.controller;

import client.model.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.DAO.UserDAO;

/**
 *
 * @author quang
 */
public class ServerThread implements Runnable {

    private User user;
    private Socket socketOfServer;
    private int clientNumber;
    private BufferedReader is;
    private BufferedWriter os;
    private boolean isClosed;
//    private Room room;
    private UserDAO userDAO;
    private String clientIP;

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

//    public void setRoom(Room room) {
//        this.room = room;
//    }
    public int getClientNumber() {
        return clientNumber;
    }

    public User getUser() {
        return user;
    }

//    public Room getRoom() {
//        return room;
//    }
    public String getClientIP() {
        return clientIP;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        userDAO = new UserDAO();
        isClosed = false;
//        room = null;
        //Trường hợp test máy ở server sẽ lỗi do hostaddress là localhost
        if (this.socketOfServer.getInetAddress().getHostAddress().equals("127.0.0.1")) {
            clientIP = "127.0.0.1";
        } else {
            clientIP = this.socketOfServer.getInetAddress().getHostAddress();
        }

    }

    public String getStringFromUser(User user1) {
        return "" + user1.getID() + "," + user1.getUsername()
                + "," + user1.getPassword() + "," + user1.getNickname() + "," + user1.getNumberOfGame() + ","
                + user1.getNumberOfwin() + "," + user1.getNumberOfDraw() + "," + user1.getRank();
    }
//    
//    public void goToOwnRoom() throws IOException{
//        write("go-to-room," + room.getID()+","+room.getCompetitor(this.getClientNumber()).getClientIP()+",1,"+getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
//        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getID()+","+this.clientIP+",0,"+getStringFromUser(user));
//    }
//    
//    public void goToPartnerRoom() throws IOException{
//        write("go-to-room," + room.getID()+","+room.getCompetitor(this.getClientNumber()).getClientIP()+",0,"+getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
//         room.getCompetitor(this.clientNumber).write("go-to-room,"+ room.getID()+","+this.clientIP+",1,"+getStringFromUser(user));
//    }

    @Override
    public void run() {
        try {
            // Mở luồng vào ra trên Socket tại Server.
            is = new BufferedReader(new InputStreamReader(socketOfServer.getInputStream()));
            os = new BufferedWriter(new OutputStreamWriter(socketOfServer.getOutputStream()));
            System.out.println("Luong moi co ID là: " + clientNumber);
            write("server-send-id" + "," + this.clientNumber);
            String message;
            while (!isClosed) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                //Xác minh
                if (messageSplit[0].equals("client-verify")) {
                    System.out.println(message);
                    User user1 = userDAO.verifyUser(new User(messageSplit[1], messageSplit[2]));
                    if (user1 == null) {
                        write("wrong-user," + messageSplit[1] + "," + messageSplit[2]);
                    } else if (!user1.getIsOnline()) {
                        write("login-success," + getStringFromUser(user1));
                        this.user = user1;
                        userDAO.updateToOnline(this.user.getID());
//                        Server.serverThreadBus.boardCast(clientNumber, "chat-server,"+user1.getNickname()+" đang online");
                    } else {
                        write("dupplicate-login," + messageSplit[1] + "," + messageSplit[2]);
                    }
                }
                //Xử lý đăng kí
                if (messageSplit[0].equals("register")) {
                    boolean checkdup = userDAO.checkDuplicated(messageSplit[1]);
                    if (checkdup) {
                        write("duplicate-username,");
                    } else {
                        User userRegister = new User(messageSplit[1], messageSplit[2], messageSplit[3]);
                        userDAO.addUser(userRegister);
                        write("register-success,");
                    }
                }
                //Xử lý người chơi đăng xuất
                if (messageSplit[0].equals("offline")) {
                    userDAO.updateToOffline(this.user.getID());
//                    Server.admin.addMessage("[" + user.getID() + "] " + user.getNickname() + " đã offline");
//                    Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                    this.user = null;
                }
            }

        } catch (IOException e) {
            //Thay đổi giá trị cờ để thoát luồng
            isClosed = true;
            //Cập nhật trạng thái của user
//            if(this.user!=null){
//                userDAO.updateToOffline(this.user.getID());
//                userDAO.updateToNotPlaying(this.user.getID());
//                Server.serverThreadBus.boardCast(clientNumber, "chat-server,"+this.user.getNickname()+" đã offline");
//                Server.admin.addMessage("["+user.getID()+"] "+user.getNickname()+" đã offline");
//            }
//            
//            //remove thread khỏi bus
//            Server.serverThreadBus.remove(clientNumber);
//            System.out.println(this.clientNumber + " đã thoát");
//            if (room != null) {
//                try {
//                    if (room.getCompetitor(clientNumber) != null) {
//                        room.decreaseNumberOfGame();
//                        room.getCompetitor(clientNumber).write("left-room,");
//                        room.getCompetitor(clientNumber).room = null;
//                    }
//                    this.room = null;
//                } catch (IOException ex) {
//                    ex.printStackTrace();
//                }
//            }

        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }
}
