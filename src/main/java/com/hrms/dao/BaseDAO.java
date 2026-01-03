package com.hrms.dao;

import com.hrms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class BaseDAO {
    protected Connection getConn() throws SQLException {
        return DatabaseConnection.getConnection();
    }
}
