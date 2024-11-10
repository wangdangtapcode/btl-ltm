/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import server.DAO.DAO;

/**
 *
 * @author quang
 */
public class Server {

    public static volatile ServerThreadBus serverThreadBus;
    public static Socket socketOfServer;
    public static int ID_room;
//    public static volatile Admin admin;

    public static void main(String[] args) {
        ServerSocket listener = null;
        serverThreadBus = new ServerThreadBus();
        System.out.println("Server is waiting to accept user...");
        int clientNumber = 0;
        ID_room = 100;
        
        try {
            listener = new ServerSocket(7777);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                10, // corePoolSize
                100, // maximumPoolSize
                10, // thread timeout
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(8) // queueCapacity
        );
        try {
            while (true) {
                // Chấp nhận một yêu cầu kết nối từ phía Client.
                // Đồng thời nhận được một đối tượng Socket tại server.
                socketOfServer = listener.accept();
                System.out.println(socketOfServer.getInetAddress().getHostAddress());
                System.out.println("clientNumber trc khi tao serverthread:" +clientNumber );
                ServerThread serverThread = new ServerThread(socketOfServer, clientNumber++);
                System.out.println("clientNumber sau khi tao serverthread:"+clientNumber);
                serverThreadBus.add(serverThread);
                System.out.println("Số thread đang chạy là: "+serverThreadBus.getLength());
                executor.execute(serverThread);  
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            try {
                listener.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
