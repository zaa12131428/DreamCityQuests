package org.yukina.DataBase;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.bukkit.entity.Player;
import org.yukina.IO.FileUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    private static ComboPooledDataSource source;

    public static void initDataBase(){
        try {
            source = new ComboPooledDataSource();

            source.setDriverClass("com.mysql.cj.jdbc.Driver");
            source.setJdbcUrl("jdbc:mysql://"+ FileUtils.settings.getString("DBUrl")+"?useSSL=false&serverTimezone=GMT&autoReconnect=true&failOverReadOnly=false");
            source.setUser(FileUtils.settings.getString("DBUserName"));
            source.setPassword(FileUtils.settings.getString("DBPassWord"));
            source.setInitialPoolSize(FileUtils.settings.getInt("DBInitPoolSize"));
            source.setMaxPoolSize(FileUtils.settings.getInt("DBMaxPoolSize"));
            source.setMinPoolSize(FileUtils.settings.getInt("DBMinPoolSize"));
            source.setIdleConnectionTestPeriod(30);
            source.setAcquireIncrement(5);

            Connection connection = getConnection();
            Statement statement = connection.createStatement();

            statement.execute("CREATE DATABASE IF NOT EXISTS Mission");
            statement.execute("use Mission");
            statement.execute("CREATE TABLE IF NOT EXISTS MissionTable(PlayerID INT(10) NOT NULL AUTO_INCREMENT PRIMARY KEY,PlayerName VARCHAR (200), Day1 INT(10), Day2 INT (10), Day3 INT (10), Day4 INT (10), Day5 INT (10), Day6 INT (10), Day7 INT (10), Day8 INT (10), Day9 INT (10), Day10 INT (10), Day11 INT (10), Day12 INT (10), Day13 INT (10), Day14 INT (10), Day15 INT (10), Day16 INT (10), Day17 INT (10), Day18 INT (10), Day19 INT (10), Day20 INT (10), Day21 INT (10), Day22 INT (10), Day23 INT (10), Day24 INT (10), Day25 INT (10), Day26 INT (10), Day27 INT (10), Day28 INT (10), Day29 INT (10), Day30 INT (10), Day31 INT (10))");

            connection.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConnection(){
        try {
            return source.getConnection();
        }catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    public static boolean dataBaseHasPlayer(Player p) {
            try {
                Connection connection = getConnection();
                Statement statement = connection.createStatement();

                statement.execute("use Mission");
                ResultSet result = statement.executeQuery("SELECT * FROM MissionTable WHERE playername = '" + p.getName() + "'");
                result.last();
                boolean result1 = result.getRow() != 0;
                connection.close();
                return result1;
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return false;
    }
}
