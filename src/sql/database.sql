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
    ID int AUTO_INCREMENT PRIMARY KEY,                
    ID_Player int NOT NULL,          
    opponent_nickname varchar(255),                 
    result varchar(50),                               
    score_player int,                                 
    score_opponent int,                               
    time_end DATETIME DEFAULT CURRENT_TIMESTAMP,      
    FOREIGN KEY (ID_Player) REFERENCES `user`(ID)    
);

Select * from user