/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import client.model.User;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 *
 * @author quang
 */
public class SocketHandle implements Runnable {

    private BufferedWriter os;
    private BufferedReader is;
    private Socket socketOfClient;
    private int ID_Server;

    public User getUserFromString(int start, String[] message) {
        return new User(Integer.parseInt(message[start]),
                message[start + 1],
                message[start + 2],
                message[start + 3],
                Integer.parseInt(message[start + 4]),
                Integer.parseInt(message[start + 5]),
                Integer.parseInt(message[start + 6]),
                Integer.parseInt(message[start + 7]));
    }

    public List<User> getListUser(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 4) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2].equals("1"),
                    message[i + 3].equals("1")));
        }
        return friend;
    }

    public List<User> getListRank(String[] message) {
        List<User> friend = new ArrayList<>();
        for (int i = 1; i < message.length; i = i + 8) {
            friend.add(new User(Integer.parseInt(message[i]),
                    message[i + 1],
                    message[i + 2],
                    message[i + 3],
                    Integer.parseInt(message[i + 4]),
                    Integer.parseInt(message[i + 5]),
                    Integer.parseInt(message[i + 6]),
                    Integer.parseInt(message[i + 7])));
        }
        return friend;
    }

    @Override
    public void run() {

        try {
            // Gửi yêu cầu kết nối tới Server đang lắng nghe
            socketOfClient = new Socket("127.0.0.1", 7777);
            System.out.println("Kết nối thành công!");
            // Tạo luồng đầu ra tại client (Gửi dữ liệu tới server)
            os = new BufferedWriter(new OutputStreamWriter(socketOfClient.getOutputStream()));
            // Luồng đầu vào tại Client (Nhận dữ liệu từ server).
            is = new BufferedReader(new InputStreamReader(socketOfClient.getInputStream()));
            String message;
            while (true) {
                message = is.readLine();
                if (message == null) {
                    break;
                }
                String[] messageSplit = message.split(",");
                if (messageSplit[0].equals("server-send-id")) {
                    ID_Server = Integer.parseInt(messageSplit[1]);
                }
                //Đăng ky thành công
                if (messageSplit[0].equals("register-success")) {
                    Client.closeAllViews();
                    JOptionPane.showMessageDialog(Client.registerFrm, "Đăng ký thành công");
                    Client.openView(Client.View.LOGIN);
                }
                //Xử lý register trùng tên
                if (messageSplit[0].equals("duplicate-username")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.REGISTER);
                    JOptionPane.showMessageDialog(Client.registerFrm, "Tên tài khoản đã được người khác sử dụng");
                }
                //Xử lý register trùng nickname
                if (messageSplit[0].equals("duplicate-nickname")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.REGISTER);
                    JOptionPane.showMessageDialog(Client.registerFrm, "Nickname đã được người khác sử dụng");
                }
                //Đăng nhập thành công
                if (messageSplit[0].equals("login-success")) {
                    System.out.println("Đăng nhập thành công");
                    Client.closeAllViews();
                    for(String x : messageSplit){
                        System.out.println(x+";");
                    }
                    User user = getUserFromString(1, messageSplit);
                    Client.user = user;
                    Client.openView(Client.View.HOMEPAGE);
                }
                //Thông tin tài khoản sai
                if (messageSplit[0].equals("wrong-user")) {
                    System.out.println("Thông tin sai");
                    Client.closeView(Client.View.GAMENOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản hoặc mật khẩu không chính xác");
                }
                //Tài khoản đã đăng nhập ở nơi khác
                if (messageSplit[0].equals("dupplicate-login")) {
                    System.out.println("Đã đăng nhập");
                    Client.closeView(Client.View.GAMENOTICE);
                    Client.openView(Client.View.LOGIN, messageSplit[1], messageSplit[2]);
                    Client.loginFrm.showError("Tài khoản đã đăng nhập ở nơi khác");
                }
                //Xử lý nhận thông tin, chat từ toàn server
                if (messageSplit[0].equals("chat-server")) {
                    if (Client.homePageFrm != null) {
                        Client.homePageFrm.addMessage(messageSplit[1]);
                    }
                }
                //Xử lý khong tim thay nickname khi ket ban
                if (messageSplit[0].equals("unavailable-nickname")) {
                    Client.openView(Client.View.MAKEFRIEND);
                    Client.makeFriendFrm.showError("Nickname khong ton tai");
                }
                //Xử lý khong online khi ket ban
                if (messageSplit[0].equals("not-online")) {
                    Client.openView(Client.View.MAKEFRIEND);
                    Client.makeFriendFrm.showError("Nickname khong online");
                }
                //Xử lý da la ban khi ket ban
                if (messageSplit[0].equals("nickname-is-friend")) {
                    Client.openView(Client.View.MAKEFRIEND);
                    Client.makeFriendFrm.showError("Nickname da la ban");
                }
                //Xử lý yêu cầu kết bạn tới
                if (messageSplit[0].equals("make-friend-request")) {
                    int ID = Integer.parseInt(messageSplit[1]);
                    String nickname = messageSplit[2];
                    Client.openView(Client.View.FRIENDREQUEST, ID, nickname);
                }
                if (messageSplit[0].equals("return-friend-list")) {
                    if (Client.homePageFrm != null) {
                        Client.homePageFrm.updateFriendList(getListUser(messageSplit));
                    }
                }
                //Yeu cau tao phong va server tra ve
                if (messageSplit[0].equals("your-created-room")) {
                    Client.closeAllViews();
                    Client.openView(Client.View.WAITINGROOM);
                    Client.waitingRoomFrm.setRoomName(messageSplit[1]);
                }
                //Xử lý lấy danh sách phòng
                if (messageSplit[0].equals("room-list")) {
                    Vector<String> rooms = new Vector<>();
                    Vector<String> nn = new Vector<>();
                    for (int i = 1; i < messageSplit.length; i = i + 2) {
                        rooms.add("Room " + messageSplit[i]);
                        nn.add(messageSplit[i + 1]);
                    }
                    Client.roomListFrm.updateRoomList(rooms, nn);
                }
                //Xử lý xem rank
                if (messageSplit[0].equals("return-get-rank-charts")) {
                    if (Client.bxhFrm != null) {
                        Client.bxhFrm.setDataToTable(getListRank(messageSplit));
                    }
                }
                // phong full
                if (messageSplit[0].equals("go-to-room")) {
                    System.out.println("Vào phòng");
                    int roomID = Integer.parseInt(messageSplit[1]);
                    String competitorIP = messageSplit[2];
                    int isKey = Integer.parseInt(messageSplit[3]);

                    User competitor = getUserFromString(4, messageSplit);
                    if (Client.waitingRoomFrm != null) {
                        Client.waitingRoomFrm.setDoiThu(competitor);
                        Client.waitingRoomFrm.HaveJoin();
                    } else {

                        Client.closeAllViews();
                        Client.openView(Client.View.WAITINGROOM);
                        Client.waitingRoomFrm.setDoiThu(competitor);
                        Client.waitingRoomFrm.setRoomNameDoiThu(roomID + "", competitor.getNickname());
                        Client.waitingRoomFrm.Join();
                    }
                }
                //Doi thu out phong (khong phai chu phong)
                if (messageSplit[0].equals("doithu-out-room")) {
                    Client.waitingRoomFrm.DoiThuRoiPhong();
                }
                //Doi thu out phong (chu phong)
                if (messageSplit[0].equals("chuphong-out-room")) {
                    Client.waitingRoomFrm.ChuPhongRoiPhong();
                }                 
                // co nguoi out dot ngot khi dang choi game 
                if (messageSplit[0].equals("left-room")) {
                    Client.gameFrm.stopTimer();
                    Client.closeAllViews();
                    Client.openView(Client.View.GAMENOTICE, "Đối thủ đã thoát khỏi phòng", "Đang trở về trang chủ");
                    Thread.sleep(3000);
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                }
                // bat dau game
                if (messageSplit[0].equals("go-to-game")) {
                    System.out.println("Vào Game");
                    int roomID = Integer.parseInt(messageSplit[1]);
                    String competitorIP = messageSplit[2];
                    int isKey = Integer.parseInt(messageSplit[3]);

                    User competitor = getUserFromString(4, messageSplit);
                    if (isKey == 0) { // la key
                        Client.closeAllViews();
                        Client.openView(Client.View.GAMECLIENT, competitor, roomID, isKey);
                        Client.gameFrm.newgame();
                    } else {
                        Client.closeAllViews();
                        Client.openView(Client.View.GAMECLIENT, competitor, roomID, isKey);
                        Client.gameFrm.newgame();
                    }
                }
                // mot nguoi xong
                if (messageSplit[0].equals("doithu-xong")) {

                    if (Client.gameFrm.getIsDone()) {
                        Client.closeView(Client.View.GAMENOTICE);
                        String ketqua = "";
                        int diemDoiThu = Integer.parseInt(messageSplit[1]);
                        Client.gameFrm.setDiemDoithu(diemDoiThu);
                        if (Client.gameFrm.getDiemDoithu() == Client.gameFrm.tongDiem()) {
                            ketqua = "draw";
                        } else if (Client.gameFrm.getDiemDoithu() < Client.gameFrm.tongDiem()) {
                            ketqua = "win";
                        } else {
                            ketqua = "lose";
                        }
                        if (!ketqua.isEmpty()) {
                            if(ketqua.equals("win")){
                                Client.gameFrm.guiKQ("lose");
                            }else if(ketqua.equals("draw")){
                                Client.gameFrm.guiKQ("draw");
                            }else{
                                Client.gameFrm.guiKQ("win");
                            }
                            Client.gameFrm.ketQua(ketqua);

                        }
                    } else {
                        int diemDoiThu = Integer.parseInt(messageSplit[1]);
                        Client.gameFrm.setDiemDoithu(diemDoiThu);
                        Client.gameFrm.chuaXong();
                    }
                }
                //
                if (messageSplit[0].equals("doithu-chua-xong")) {
                    Client.openView(Client.View.GAMENOTICE, "Đối thủ chưa xong", "Đang đợi dối thủ");
                }
                // 
                if (messageSplit[0].equals("tro-ve-home")) {
                    User a = getUserFromString(1, messageSplit);
                    Client.user = a;                    
                    Client.closeAllViews();
                    Client.openView(Client.View.GAMENOTICE, "Trận đấu đã xong", "Đang trở về trang chủ");
                    Thread.sleep(3000);
                    Client.closeAllViews();
                    Client.openView(Client.View.HOMEPAGE);
                }
                //
                if (messageSplit[0].equals("tra-ket-qua")) {
                    String rs = messageSplit[1];
                    Client.gameFrm.ketQua(rs);
                }
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public void write(String message) throws IOException {
        os.write(message);
        os.newLine();
        os.flush();
    }

    public Socket getSocketOfClient() {
        return socketOfClient;
    }

}
