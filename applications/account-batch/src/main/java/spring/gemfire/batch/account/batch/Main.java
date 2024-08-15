package spring.gemfire.batch.account.batch;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Main {

    private final String url = "jdbc:postgresql://34.171.65.113:5432/postgres-db";
    private final String user = "pgappuser";
    private final String password = "sRlI24ZRQ0rtFs9R3i4IEhk1gM634u";

    public Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url, user, password);
            System.out.println("Connected to the PostgreSQL server successfully.");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return conn;
    }

    public static void main(String[] args) throws SQLException {
        Main app = new Main();
        Connection connection = app.connect();
        Statement statement = connection.createStatement();
        statement.execute("DROP SCHEMA IF EXISTS cache_accounts CASCADE");

    }
}