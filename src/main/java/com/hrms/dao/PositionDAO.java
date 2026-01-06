package com.hrms.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.hrms.model.Position;
import com.hrms.util.DatabaseConnection;

/**
 * Pozisyon veritabanı işlemleri için DAO sınıfı
 */
public class PositionDAO {
    
    private Connection connection;
    
    public PositionDAO() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }
    
    /**
     * Yeni pozisyon ekler
     */
    public boolean insert(Position position) {
        String sql = "INSERT INTO POSITIONS (position_title, description, min_salary, max_salary, level) " +
                    "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, position.getPositionTitle());
            stmt.setString(2, position.getDescription());
            stmt.setBigDecimal(3, position.getMinSalary());
            stmt.setBigDecimal(4, position.getMaxSalary());
            stmt.setString(5, position.getLevel());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    position.setPositionId(rs.getInt(1));
                }
                System.out.println("✓ Pozisyon eklendi: " + position.getPositionTitle());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Pozisyon ekleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Pozisyon günceller
     */
    public boolean update(Position position) {
        String sql = "UPDATE POSITIONS SET position_title = ?, description = ?, " +
                    "min_salary = ?, max_salary = ?, level = ? WHERE position_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, position.getPositionTitle());
            stmt.setString(2, position.getDescription());
            stmt.setBigDecimal(3, position.getMinSalary());
            stmt.setBigDecimal(4, position.getMaxSalary());
            stmt.setString(5, position.getLevel());
            stmt.setInt(6, position.getPositionId());
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Pozisyon güncellendi: " + position.getPositionTitle());
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Pozisyon güncelleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * Pozisyon siler
     */
    public boolean delete(int positionId) {
        String sql = "DELETE FROM POSITIONS WHERE position_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, positionId);
            
            int affected = stmt.executeUpdate();
            
            if (affected > 0) {
                System.out.println("✓ Pozisyon silindi: ID=" + positionId);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("✗ Pozisyon silme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * ID'ye göre pozisyon getirir
     */
    public Position findById(int positionId) {
        String sql = "SELECT * FROM POSITIONS WHERE position_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, positionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return extractPositionFromResultSet(rs);
            }
        } catch (SQLException e) {
            System.err.println("✗ Pozisyon bulma hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    /**
     * Tüm pozisyonları getirir
     */
    public List<Position> findAll() {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM POSITIONS ORDER BY position_title";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                positions.add(extractPositionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Pozisyonları listeleme hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return positions;
    }
    
    /**
     * Seviyeye göre pozisyonları getirir
     */
    public List<Position> findByLevel(String level) {
        List<Position> positions = new ArrayList<>();
        String sql = "SELECT * FROM POSITIONS WHERE level = ? ORDER BY position_title";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, level);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                positions.add(extractPositionFromResultSet(rs));
            }
        } catch (SQLException e) {
            System.err.println("✗ Pozisyon arama hatası: " + e.getMessage());
            e.printStackTrace();
        }
        return positions;
    }
    
    /**
     * ResultSet'ten Position nesnesi oluşturur
     */
    private Position extractPositionFromResultSet(ResultSet rs) throws SQLException {
        Position pos = new Position();
        pos.setPositionId(rs.getInt("position_id"));
        pos.setPositionTitle(rs.getString("position_title"));
        pos.setDescription(rs.getString("description"));
        pos.setMinSalary(rs.getBigDecimal("min_salary"));
        pos.setMaxSalary(rs.getBigDecimal("max_salary"));
        pos.setLevel(rs.getString("level"));
        
        Timestamp createdTs = rs.getTimestamp("created_at");
        if (createdTs != null) {
            pos.setCreatedAt(createdTs.toLocalDateTime());
        }
        
        return pos;
    }
}
