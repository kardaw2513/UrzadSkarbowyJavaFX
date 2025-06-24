package Services.dao;

import Model.Pracownik;
import Services.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PracownikDAO {

    public void save(Pracownik p) throws SQLException {
        String sql = "INSERT INTO Pracownik(id, imie, nazwisko) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getImie());
            ps.setString(3, p.getNazwisko());
            ps.executeUpdate();
        }
    }

    public Optional<Pracownik> findById(String id) throws SQLException {
        String sql = "SELECT imie, nazwisko FROM Pracownik WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Pracownik(id, rs.getString("imie"), rs.getString("nazwisko")));
                }
            }
        }
        return Optional.empty();
    }

    public List<Pracownik> findAll() throws SQLException {
        List<Pracownik> list = new ArrayList<>();
        String sql = "SELECT id, imie, nazwisko FROM Pracownik";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Pracownik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko")));
            }
        }
        return list;
    }

    public void update(Pracownik p) throws SQLException {
        String sql = "UPDATE Pracownik SET imie = ?, nazwisko = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getImie());
            ps.setString(2, p.getNazwisko());
            ps.setString(3, p.getId());
            ps.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Pracownik WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
