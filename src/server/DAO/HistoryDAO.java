/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server.DAO;

import client.model.History;
import client.model.User;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.sql.Timestamp;
public class HistoryDAO extends DAO {

    public HistoryDAO() {
        super();
    }

    public void saveMatchHistory(int ID_Player, String Opponent_NN, String result, int score_player, int score_opponent) {
        try {
            // Câu lệnh SQL để lưu kết quả trận đấu và điểm vào bảng history
            String sql = "INSERT INTO history (ID_Player, opponent_nickname, result, score_player, score_opponent, time_end) "
                    + "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";

            // Tạo prepared statement
            PreparedStatement preparedStatement = con.prepareStatement(sql);

            // Gán giá trị cho các tham số trong câu lệnh SQL
            preparedStatement.setInt(1, ID_Player);         // Người chơi
            preparedStatement.setString(2, Opponent_NN);       // Đối thủ
            preparedStatement.setString(3, result);         // Kết quả trận đấu ("Win", "Loss", "Draw")
            preparedStatement.setInt(4, score_player);      // Điểm của người chơi
            preparedStatement.setInt(5, score_opponent);    // Điểm của đối thủ

            // Thực thi câu lệnh SQL
            preparedStatement.executeUpdate();

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    public List<History> getPlayerHistory(int playerID) {
        List<History> historyList = new ArrayList<>();
        try {
            // Câu lệnh SQL để lấy lịch sử đấu của người chơi chỉ khi người chơi là người tham gia chính
            String sql = "SELECT * FROM history WHERE ID_Player = ?";

            // Tạo PreparedStatement
            PreparedStatement preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, playerID);  // Lọc theo ID người chơi

            // Thực thi câu lệnh và lấy kết quả
            ResultSet resultSet = preparedStatement.executeQuery();

            // Duyệt qua kết quả và tạo đối tượng History cho từng trận đấu
            while (resultSet.next()) {
                int id = resultSet.getInt("ID");
                int idPlayer = resultSet.getInt("ID_Player");
                String opponentnn = resultSet.getString("opponent_nickname");
                String result = resultSet.getString("result");
                int scorePlayer = resultSet.getInt("score_player");
                int scoreOpponent = resultSet.getInt("score_opponent");
                Timestamp timeEnd = resultSet.getTimestamp("time_end");

                // Tạo đối tượng History và thêm vào danh sách
                History history = new History(id, idPlayer, opponentnn, result, scorePlayer, scoreOpponent, timeEnd);
                historyList.add(history);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        // Trả về danh sách lịch sử đấu
        return historyList;
    }
}
