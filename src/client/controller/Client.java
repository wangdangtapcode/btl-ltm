/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import client.model.User;
import client.view.BXHFrm;
import client.view.FriendRequestFrm;
import client.view.GameFrm;
import client.view.GameNoticeFrm;
import client.view.HomePageFrm;
import client.view.LoginFrm;
import client.view.MakeFriendFrm;
import client.view.RegisterFrm;
import client.view.RoomListFrm;
import client.view.ThongtinDoiThuFrm;
import client.view.WaittingRoomFrm;
import javax.swing.JFrame;

/**
 *
 * @author quang
 */
public class Client {

    public enum View {
        LOGIN,
        REGISTER,
        HOMEPAGE,
        ROOMLIST,
        MAKEFRIEND,
        FRIENDLIST,
        FINDROOM,
        WAITINGROOM,
        GAMECLIENT,
        CREATEROOMPASSWORD,
        JOINROOMPASSWORD,
        COMPETITORINFO,
        RANK,
        GAMENOTICE,
        FRIENDREQUEST,
        ROOMNAMEFRM
    }
    public static User user;
    //Danh sách giao diện
    public static LoginFrm loginFrm;
    public static RegisterFrm registerFrm;
    public static HomePageFrm homePageFrm;
    public static GameNoticeFrm gameNoticeFrm;
    public static MakeFriendFrm makeFriendFrm;
    public static FriendRequestFrm friendRequestFrm;
    public static WaittingRoomFrm waitingRoomFrm;
    public static RoomListFrm roomListFrm;
    public static BXHFrm bxhFrm;
    public static ThongtinDoiThuFrm doiThuFrm;
    public static GameFrm gameFrm;
 
    public static SocketHandle socketHandle;
    public static JFrame getVisibleJFrame(){
        if(roomListFrm!=null&&roomListFrm.isVisible())
            return roomListFrm;
        if(bxhFrm!=null&&bxhFrm.isVisible()){
            return bxhFrm;
        }
        return homePageFrm;
    }
    public void initView() {

        loginFrm = new LoginFrm();
        loginFrm.setVisible(true);
        socketHandle = new SocketHandle();
        socketHandle.run();
    }

    public static void closeView(View viewName) {
        if (viewName != null) {
            switch (viewName) {
                case LOGIN:
                    loginFrm.dispose();
                    break;
                case REGISTER:
                    registerFrm.dispose();
                    break;
                case HOMEPAGE:
                    homePageFrm.dispose();
                    break;
                case GAMENOTICE:
                    gameNoticeFrm.dispose();
                    break;
                case MAKEFRIEND:
                    makeFriendFrm.dispose();
                    break;
                case WAITINGROOM:
                    waitingRoomFrm.dispose();
                    break;
                case ROOMLIST:
                    roomListFrm.dispose();
                    break;
                case RANK:
                    bxhFrm.dispose();
                    break;
                case GAMECLIENT:
//                    gameFrm.stopAllThread();
                    gameFrm.dispose();
                    break;
            }
        }
    }

    public static void closeAllViews() {
        if (loginFrm != null) {
            loginFrm.dispose();
        }
        if (registerFrm != null) {
            registerFrm.dispose();
        }
        if (homePageFrm != null) {
            homePageFrm.dispose();
        }
        if (gameNoticeFrm != null) {
            gameNoticeFrm.dispose();
        }
        if (homePageFrm != null) {
            homePageFrm.dispose();
        }
        if (makeFriendFrm != null) {
            makeFriendFrm.dispose();
        }
        if (waitingRoomFrm != null) {
            waitingRoomFrm.dispose();
        }
        if (roomListFrm != null) {
            roomListFrm.dispose();
        }
        if(bxhFrm!=null) bxhFrm.dispose();
        if(gameFrm!=null){
//            gameFrm.stopAllThread();
            gameFrm.dispose();
        }         
    }

    public static void openView(View viewName, int arg1, String arg2) {
        if (viewName != null) {
            switch (viewName) {
                case FRIENDREQUEST:
                    friendRequestFrm = new FriendRequestFrm(arg1, arg2);
                    friendRequestFrm.setVisible(true);
            }
        }
    }
    public static void openView(View viewName, User competitor, int room_ID, int isStart){
        if(viewName != null){
            switch(viewName){
                case GAMECLIENT:
                    gameFrm = new GameFrm(competitor, room_ID, isStart);
                    gameFrm.setVisible(true);
                    break;
            }
        }
    }
    public static void openView(View viewName) {
        if (viewName != null) {
            switch (viewName) {
                case LOGIN:
                    loginFrm = new LoginFrm();
                    loginFrm.setVisible(true);
                    break;
                case REGISTER:
                    registerFrm = new RegisterFrm();
                    registerFrm.setVisible(true);
                    break;
                case HOMEPAGE:
                    homePageFrm = new HomePageFrm();
                    homePageFrm.setVisible(true);
                    break;
                case MAKEFRIEND:
                    makeFriendFrm = new MakeFriendFrm();
                    makeFriendFrm.setVisible(true);
                    break;
                case WAITINGROOM:
                    waitingRoomFrm = new WaittingRoomFrm();
                    waitingRoomFrm.setVisible(true);
                    break;
                case ROOMLIST:
                    roomListFrm = new RoomListFrm();
                    roomListFrm.setVisible(true);
                    break;
                case RANK:
                    bxhFrm = new BXHFrm();
                    bxhFrm.setVisible(true);
                    break;

            }
        }
    }

    public static void openView(View viewName, String arg1, String arg2) {
        if (viewName != null) {
            switch (viewName) {
                case GAMENOTICE:
                    gameNoticeFrm = new GameNoticeFrm(arg1, arg2);
                    gameNoticeFrm.setVisible(true);
                    break;
                case LOGIN:
                    loginFrm = new LoginFrm(arg1, arg2);
                    loginFrm.setVisible(true);
            }
        }
    }
    public static void openView(View viewName,User u) {
        if (viewName != null) {
            switch (viewName) {
                case COMPETITORINFO:
                    doiThuFrm = new ThongtinDoiThuFrm(u);
                    doiThuFrm.setVisible(true);
            }
        }
    }
    public static void main(String[] args) {
        new Client().initView();
    }
}
