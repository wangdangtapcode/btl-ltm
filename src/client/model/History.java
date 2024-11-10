/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package client.model;

/**
 *
 * @author quang
 */
import java.sql.Timestamp;

public class History {
    private int id;              // ID trận đấu
    private int idPlayer;        // ID người chơi
    private String opponentNickname;      // ID đối thủ
    private String result;       // Kết quả trận đấu (Win, Loss, Draw)
    private int scorePlayer;     // Điểm của người chơi
    private int scoreOpponent;   // Điểm của đối thủ
    private Timestamp timeEnd;   // Thời gian kết thúc trận đấu

    // Constructor
    public History(int id, int idPlayer, String opponentNickname, String result, 
                   int scorePlayer, int scoreOpponent, Timestamp timeEnd) {
        this.id = id;
        this.idPlayer = idPlayer;
        this.opponentNickname = opponentNickname;
        this.result = result;
        this.scorePlayer = scorePlayer;
        this.scoreOpponent = scoreOpponent;
        this.timeEnd = timeEnd;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdPlayer() {
        return idPlayer;
    }

    public void setIdPlayer(int idPlayer) {
        this.idPlayer = idPlayer;
    }

    public String getOpponentNickname() {
        return opponentNickname;
    }

    public void setOpponentNickname(String opponentNickname) {
        this.opponentNickname = opponentNickname;
    }


    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getScorePlayer() {
        return scorePlayer;
    }

    public void setScorePlayer(int scorePlayer) {
        this.scorePlayer = scorePlayer;
    }

    public int getScoreOpponent() {
        return scoreOpponent;
    }

    public void setScoreOpponent(int scoreOpponent) {
        this.scoreOpponent = scoreOpponent;
    }

    public Timestamp getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(Timestamp timeEnd) {
        this.timeEnd = timeEnd;
    }

}

