/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.controller;

import client.model.User;
import client.view.FriendRequestFrm;
import client.view.GameNoticeFrm;
import client.view.HomePageFrm;
import client.view.LoginFrm;
import client.view.MakeFriendFrm;
import client.view.RegisterFrm;


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
        GAMEAI,
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
    public static SocketHandle socketHandle;

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
    }
    public static void openView(View viewName, int arg1, String arg2){
        if(viewName != null){
            switch(viewName){
                case FRIENDREQUEST:
                    friendRequestFrm = new FriendRequestFrm(arg1, arg2);
                    friendRequestFrm.setVisible(true);
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

    public static void main(String[] args) {
        new Client().initView();
    }
}
