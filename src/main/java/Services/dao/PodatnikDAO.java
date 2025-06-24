package Services.dao;

import Model.Podatnik;
import Services.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PodatnikDAO {

    public void save(Podatnik p) throws SQLException {
        String sql = "INSERT INTO Podatnik(id, imie, nazwisko) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getImie());
            ps.setString(3, p.getNazwisko());
            ps.executeUpdate();
        }
    }

    public Optional<Podatnik> findById(String id) throws SQLException {
        String sql = "SELECT imie, nazwisko FROM Podatnik WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Podatnik(id, rs.getString("imie"), rs.getString("nazwisko")));
                }
            }
        }
        return Optional.empty();
    }

    public List<Podatnik> findAll() throws SQLException {
        List<Podatnik> list = new ArrayList<>();
        String sql = "SELECT id, imie, nazwisko FROM Podatnik";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Podatnik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko")));
            }
        }
        return list;
    }

    public void update(Podatnik p) throws SQLException {
        String sql = "UPDATE Podatnik SET imie = ?, nazwisko = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getImie());
            ps.setString(2, p.getNazwisko());
            ps.setString(3, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Podatnik WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
