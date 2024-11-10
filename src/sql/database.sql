create database ltm
CREATE TABLE `user`(
    ID int AUTO_INCREMENT PRIMARY KEY,
    `username` varchar(255) UNIQUE,
    `password` varchar(255),
    nickname varchar(255) UNIQUE,
    numberOfGame int DEFAULT 0,
    numberOfWin int DEFAULT 0,
    numberOfDraw int DEFAULT 0,
    IsOnline int DEFAULT 0,
    IsPlaying int DEFAULT 0
);
CREATE TABLE friend(
    ID_User1 int NOT NULL,
    ID_User2 int NOT NULL,
    FOREIGN KEY (ID_User1) REFERENCES `user`(ID),
    FOREIGN KEY (ID_User2) REFERENCES `user`(ID),
    CONSTRAINT PK_friend PRIMARY KEY (ID_User1,ID_User2)
);
CREATE TABLE history (
    ID int AUTO_INCREMENT PRIMARY KEY,                -- ID của trận đấu
    ID_Player int NOT NULL,          
    opponent_nickname varchar(255),                 -- ID của người chơi
    result varchar(50),                               -- Kết quả trận đấu (Win, Loss, Draw)
    score_player int,                                 -- Điểm của người chơi
    score_opponent int,                               -- Điểm của đối thủ
    time_end DATETIME DEFAULT CURRENT_TIMESTAMP,      -- Thời gian kết thúc trận đấu
    FOREIGN KEY (ID_Player) REFERENCES `user`(ID)    -- Khóa ngoại với bảng user để liên kết với người chơi
);

Select * from user