package de.jandev.falldown.sql;

import java.sql.*;

public class SQLHelper {

    private final String host;
    private final int port;
    private final String user;
    private final String password;
    private final String database;
    private Connection connection;

    public SQLHelper(String host, int port, String user, String password, String database) throws SQLException {
        this.host = host;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;

        this.openConnection();
    }

    public void openConnection() throws SQLException {
        connection = DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database, this.user, this.password);
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean hasConnection() {
        try {
            return connection != null && connection.isValid(5);
        } catch (SQLException e) {
            return false;
        }
    }

    public void queryUpdate(String query) throws SQLException {
        PreparedStatement st = null;
        try {
            st = connection.prepareStatement(query);
            st.executeUpdate();
        } finally {
            this.closeResources(null, st);
        }
    }

    public void closeResources(ResultSet rs, PreparedStatement st) throws SQLException {
        if (rs != null) {
            rs.close();
        }
        if (st != null) {
            st.close();
        }
    }

}
