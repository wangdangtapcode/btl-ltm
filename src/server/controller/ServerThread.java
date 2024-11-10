/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.controller;

import client.model.History;
import client.model.Room;
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
import server.DAO.HistoryDAO;

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
    private Room room;
    private UserDAO userDAO;
    private HistoryDAO hisDAO;
    private String clientIP;

    public BufferedReader getIs() {
        return is;
    }

    public BufferedWriter getOs() {
        return os;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public int getClientNumber() {
        return clientNumber;
    }

    public User getUser() {
        return user;
    }

    public Room getRoom() {
        return room;
    }

    public String getClientIP() {
        return clientIP;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserDAO getUserDAO() {
        return userDAO;
    }

    public ServerThread(Socket socketOfServer, int clientNumber) {
        this.socketOfServer = socketOfServer;
        this.clientNumber = clientNumber;
        System.out.println("Server thread number " + clientNumber + " Started");
        userDAO = new UserDAO();
        hisDAO = new HistoryDAO();
        isClosed = false;
        room = null;
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
    public String getStringFromHis(History his) {
        return "" + his.getId() + "," + his.getIdPlayer()
                + "," + his.getOpponentNickname() + "," + his.getResult() + "," + his.getScorePlayer() + ","
                + his.getScoreOpponent() + "," + his.getTimeEnd() ;
    }
    public void goToOwnRoom() throws IOException {
        write("go-to-room-duel," + room.getID() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",1," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room-duel," + room.getID() + "," + this.clientIP + ",0," + getStringFromUser(user));
    }

    public void goToPartnerRoom() throws IOException {
        write("go-to-room," + room.getID() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",0," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-room," + room.getID() + "," + this.clientIP + ",1," + getStringFromUser(user));
    }

    public void goToGame() throws IOException {
        write("go-to-game," + room.getID() + "," + room.getCompetitor(this.getClientNumber()).getClientIP() + ",0," + getStringFromUser(room.getCompetitor(this.getClientNumber()).getUser()));
        room.getCompetitor(this.clientNumber).write("go-to-game," + room.getID() + "," + this.clientIP + ",1," + getStringFromUser(user));
    }

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
                        Server.serverThreadBus.boardCast(clientNumber, "chat-server," + user1.getNickname() + " đang online");
                    } else {
                        write("dupplicate-login," + messageSplit[1] + "," + messageSplit[2]);
                    }
                }
                //Xử lý đăng kí
                if (messageSplit[0].equals("register")) {
                    boolean checkdup = userDAO.checkDuplicated(messageSplit[1]);
                    boolean checknndup = userDAO.checkNicknameDuplicated(messageSplit[3]);
                    if (checkdup) {
                        write("duplicate-username,");
                    } else if (checknndup) {
                        write("duplicate-nickname,");
                    } else {
                        User userRegister = new User(messageSplit[1], messageSplit[2], messageSplit[3]);
                        userDAO.addUser(userRegister);
                        write("register-success,");
                    }
                }
                //Xử lý người chơi đăng xuất
                if (messageSplit[0].equals("offline")) {
                    userDAO.updateToOffline(this.user.getID());
                    Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
                    this.user = null;
                }
                //Xử lý chat toàn server
                if (messageSplit[0].equals("chat-server")) {
                    Server.serverThreadBus.boardCast(clientNumber, messageSplit[0] + "," + user.getNickname() + " : " + messageSplit[1]);
                }
                //Xử lý gui loi moi ket ban
                if (messageSplit[0].equals("send-make-friend")) {
                    Boolean checknn = userDAO.checkNicknameDuplicated(messageSplit[1]);
                    User user2 = userDAO.getUserByNickname(messageSplit[1]);
                    System.out.println(user2);
                    Boolean checkIsOnline = false;
                    if (user2 != null) {
                        checkIsOnline = userDAO.checkIsOnline(user2.getID());
                    }
                    Boolean checkIsFriend = false;
                    if (user2 != null) {
                        checkIsFriend = userDAO.checkIsFriend(Integer.parseInt(messageSplit[2]), user2.getID());
                    }
                    if (!checknn) {
                        write("unavailable-nickname,");
                    } else if (!checkIsOnline && user2 != null) {
                        write("not-online,");
                    } else if (checkIsFriend && user2 != null) {
                        write("nickname-is-friend,");
                    } else {
                        Server.serverThreadBus.getServerThreadByUserID(user2.getID())
                                .write("make-friend-request," + this.user.getID() + "," + this.user.getNickname());
                    }
                }
                //Xử lý xác nhận kết bạn
                if (messageSplit[0].equals("make-friend-confirm")) {
                    userDAO.makeFriend(this.user.getID(), Integer.parseInt(messageSplit[1]));
                    System.out.println("Kết bạn thành công");

                }
                //Xử lý xem danh sách bạn bè
                if (messageSplit[0].equals("view-friend-list") && this.user != null) {
                    List<User> friends = userDAO.getListFriend(this.user.getID());
                    String res = "return-friend-list,";
                    for (User friend : friends) {
                        res += friend.getID() + "," + friend.getNickname() + "," + (friend.getIsOnline() == true ? 1 : 0) + "," + (friend.getIsPlaying() == true ? 1 : 0) + ",";
                    }
                    System.out.println(res);
                    write(res);
                }
                //Xử lý tạo phòng
                if (messageSplit[0].equals("create-room")) {
                    room = new Room(this);

                    write("your-created-room," + room.getID());
                    System.out.println("Tạo phòng mới thành công");

//                    userDAO.updateToPlaying(this.user.getID());
                }
                //Xử lý huy phòng
                if (messageSplit[0].equals("cancel-room")) {
//                    userDAO.updateToNotPlaying(this.user.getID());
                    System.out.println("Đã hủy phòng");
                    this.room = null;
                }
                //Xử lý không phải chủ phòng rời phòng
                if (messageSplit[0].equals("not-key-out-room")) {
                    room.getCompetitor(clientNumber).write("doithu-out-room,");
                    this.room = null;
                }
                //Xử lý  chủ phòng rời phòng
                if (messageSplit[0].equals("key-out-room")) {
                    room.getCompetitor(clientNumber).write("chuphong-out-room,");
                    this.room = null;
                }
                //Xử lý  chủ phòng rời phòng bên đối thủ
                if (messageSplit[0].equals("chuphong-out-room")) {
                    this.room = null;
                }
                //Xử lý xem danh sách phòng 
                if (messageSplit[0].equals("view-room-list")) {
                    String res = "room-list,";
                    int number = 1;
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (number > 8) {
                            break;
                        }
                        if (serverThread.room != null && serverThread.room.getNumberOfUser() == 1) {
                            res += serverThread.room.getID() + "," + serverThread.room.getUser1().getUser().getNickname() + ",";
                        }
                        number++;
                    }
                    write(res);
                    System.out.println(res);
                }
                //Xử lý lấy danh sách bảng xếp hạng
                if (messageSplit[0].equals("get-rank-charts")) {
                    List<User> ranks = userDAO.getUserStaticRank();
                    String res = "return-get-rank-charts,";
                    for (User user : ranks) {
                        res += getStringFromUser(user) + ",";
                    }
                    System.out.println(res);
                    write(res);
                }
                //Xử lý khi có người chơi thứ 2 vào phòng
                if (messageSplit[0].equals("join-room")) {
                    int ID_room = Integer.parseInt(messageSplit[1]);
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.room != null && serverThread.room.getID() == ID_room) {
                            serverThread.room.setUser2(this);
                            this.room = serverThread.room;
                            System.out.println("Đã vào phòng " + room.getID());
                            goToPartnerRoom();
                            break;
                        }
                    }
                }
                if (messageSplit[0].equals("start-room")) {
                    int ID_room = Integer.parseInt(messageSplit[1]);
                    for (ServerThread serverThread : Server.serverThreadBus.getListServerThreads()) {
                        if (serverThread.room.getID() == ID_room) {
                            room.increaseNumberOfGame();
                            room.setUsersToPlaying();
                            goToGame();
                            break;
                        }
                    }
                }
                // mot nguoi bam da xong
                if (messageSplit[0].equals("done")) {
                    int diem = Integer.parseInt(messageSplit[1]);
                    room.getCompetitor(clientNumber).write("doithu-xong," + diem);
                }
                //Doi thu chua xong
                if (messageSplit[0].equals("chua-xong")) {
                    room.getCompetitor(clientNumber).write("doithu-chua-xong,");
                }
                // duoc gui ket qua
                if (messageSplit[0].equals("gui-ket-qua")) {
                    String rs = messageSplit[1];
                    room.getCompetitor(clientNumber).write("tra-ket-qua," + rs);
                }
                //win
                if (messageSplit[0].equals("win")) {
                    int diem = Integer.parseInt(messageSplit[1]);
                    int diemdoithu = Integer.parseInt(messageSplit[2]);
                    hisDAO.saveMatchHistory(user.getID(),room.getCompetitor(clientNumber).user.getNickname() , "win", diem, diemdoithu);
                    userDAO.addWinGame(this.user.getID());
                    userDAO.updateToNotPlaying(this.user.getID());
                    this.room = null;
                    User a = userDAO.getUserByNickname(this.user.getNickname());
                    write("tro-ve-home," + getStringFromUser(a));
                }
                //draw
                if (messageSplit[0].equals("draw")) {
                    int diem = Integer.parseInt(messageSplit[1]);
                    int diemdoithu = Integer.parseInt(messageSplit[2]);               
                    hisDAO.saveMatchHistory(user.getID(),room.getCompetitor(clientNumber).user.getNickname() , "draw", diem, diemdoithu);
                    userDAO.addDrawGame(this.user.getID());
                    userDAO.updateToNotPlaying(this.user.getID());
                    this.room = null;
                    User a = userDAO.getUserByNickname(this.user.getNickname());
                    write("tro-ve-home," + getStringFromUser(a));
                }
                //lose
                if (messageSplit[0].equals("lose")) {
                    int diem = Integer.parseInt(messageSplit[1]);
                    int diemdoithu = Integer.parseInt(messageSplit[2]);               
                    hisDAO.saveMatchHistory(user.getID(),room.getCompetitor(clientNumber).user.getNickname() , "lose", diem, diemdoithu);              
                    userDAO.updateToNotPlaying(this.user.getID());
                    this.room = null;
                    User a = userDAO.getUserByNickname(this.user.getNickname());
                    write("tro-ve-home," + getStringFromUser(a));
                }
                //Xử lý khi gửi yêu cầu thách đấu tới bạn bè
                if (messageSplit[0].equals("duel-request")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]),
                            "duel-notice," + this.user.getID() + "," + this.user.getNickname());
                }
                //Xử lý khi đối thủ đồng ý thách đấu
                if (messageSplit[0].equals("agree-duel")) {
                    this.room = new Room(this);
                    int ID_User2 = Integer.parseInt(messageSplit[1]);
                    ServerThread user2 = Server.serverThreadBus.getServerThreadByUserID(ID_User2);
                    room.setUser2(user2);
                    user2.setRoom(room);
                    goToOwnRoom();
                }
                //Xử lý khi không đồng ý thách đấu
                if (messageSplit[0].equals("disagree-duel")) {
                    Server.serverThreadBus.sendMessageToUserID(Integer.parseInt(messageSplit[1]), message);
                }
                // lich su dau
                if (messageSplit[0].equals("get-history")) {
                    List<History> hisList = hisDAO.getPlayerHistory(user.getID());
                    String res = "return-get-history,";
                    for (History his : hisList) {
                        res += getStringFromHis(his) + ",";
                    }
                    System.out.println(res);
                    write(res);
                }          
            }

        } catch (IOException e) {
            //Thay đổi giá trị cờ để thoát luồng
            isClosed = true;
            //Cập nhật trạng thái của user
            if (this.user != null) {
                userDAO.updateToOffline(this.user.getID());
                userDAO.updateToNotPlaying(this.user.getID());
                Server.serverThreadBus.boardCast(clientNumber, "chat-server," + this.user.getNickname() + " đã offline");
            }

            //remove thread khỏi bus
            Server.serverThreadBus.remove(clientNumber);
            System.out.println(this.clientNumber + " đã thoát");
            ServerThread DoiThu = room.getCompetitor(clientNumber);
            if (room != null) {
                try {
                    if (DoiThu != null && DoiThu.getUserDAO().getUserByNickname(DoiThu.getUser().getNickname()).getIsPlaying()) {
                        room.decreaseNumberOfGame();
                        room.getCompetitor(clientNumber).write("left-room,");
                        DoiThu.getUserDAO().updateToNotPlaying(clientNumber);
                        room.getCompetitor(clientNumber).room = null;
                    } else if (DoiThu != null && !DoiThu.getUserDAO().getUserByNickname(DoiThu.getUser().getNickname()).getIsPlaying()) {

                    }
                    this.room = null;
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }
}
