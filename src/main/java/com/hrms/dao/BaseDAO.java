package com.hrms.dao;

import java.sql.Connection;
import java.sql.SQLException;

import com.hrms.util.DatabaseConnection;

public abstract class BaseDAO {
    protected Connection getConn() throws SQLException {
        return DatabaseConnection.getInstance().getConnection();
    }
}
