package de.jandev.falldown.sql;

import org.bukkit.Bukkit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ExperienceRepository {

    private static final Logger LOGGER = Bukkit.getLogger();
    private final SQLHelper sqlHelper;

    public ExperienceRepository(SQLHelper sqlHelper) {
        this.sqlHelper = sqlHelper;
        createTableIfNotExists();
    }

    public void createTableIfNotExists() {
        try {
            sqlHelper.queryUpdate("CREATE TABLE IF NOT EXISTS experience (uuid VARCHAR(50) NOT NULL, level INT, PRIMARY KEY (uuid))");
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Falldown - SQL Error: \n", e);
        }
    }

    // Cannot be negative, because setLevel() cannot save negative numbers due to reading out the players level
    public int getLevel(String uuid) throws SQLException {
        prepareConnection();
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = sqlHelper.getConnection().prepareStatement("SELECT * FROM experience WHERE uuid=?");
            st.setString(1, uuid);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("level");
            }
        } finally {
            sqlHelper.closeResources(rs, st);
        }
        return -1;
    }

    public void setLevel(String uuid, int level) throws SQLException {
        prepareConnection();
        PreparedStatement st = null;
        PreparedStatement stWrite = null;
        ResultSet rs = null;
        try {
            st = sqlHelper.getConnection().prepareStatement("SELECT * FROM experience WHERE uuid=?");
            st.setString(1, uuid);
            rs = st.executeQuery();
            if (rs.next()) {
                stWrite = sqlHelper.getConnection().prepareStatement("UPDATE experience SET level=? WHERE uuid=?");
                stWrite.setInt(1, level);
                stWrite.setString(2, uuid);
            } else {
                stWrite = sqlHelper.getConnection().prepareStatement("INSERT INTO experience(uuid, level) " +
                        "VALUES (?, ?)");
                stWrite.setString(1, uuid);
                stWrite.setInt(2, level);
            }
            stWrite.executeUpdate();
        } finally {
            sqlHelper.closeResources(rs, st);
            sqlHelper.closeResources(null, stWrite);
        }
    }

    public void prepareConnection() throws SQLException {
        if (!sqlHelper.hasConnection()) {
            sqlHelper.openConnection();
        }
    }

}
