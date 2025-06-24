package Services.dao;

import Model.Wizyta;
import Services.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WizytaDAO {

    public void save(Wizyta w) throws SQLException {
        String sql = "INSERT INTO Wizyta(id, podatnikId, data, pracownikId) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, w.getId());
            ps.setString(2, w.getPodatnikId());
            ps.setString(3, w.getData().toString());
            ps.setString(4, w.getPracownikId());
            ps.executeUpdate();
        }
    }

    public Optional<Wizyta> findById(String id) throws SQLException {
        String sql = "SELECT podatnikId, data, pracownikId FROM Wizyta WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String podatnikId = rs.getString("podatnikId");
                    LocalDate data = LocalDate.parse(rs.getString("data"));
                    String pracownikId = rs.getString("pracownikId");
                    return Optional.of(new Wizyta(id, podatnikId, data, pracownikId));
                }
            }
        }
        return Optional.empty();
    }

    public List<Wizyta> findAll() throws SQLException {
        List<Wizyta> list = new ArrayList<>();
        String sql = "SELECT id, podatnikId, data, pracownikId FROM Wizyta";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String podatnikId = rs.getString("podatnikId");
                LocalDate data = LocalDate.parse(rs.getString("data"));
                String pracownikId = rs.getString("pracownikId");
                list.add(new Wizyta(id, podatnikId, data, pracownikId));
            }
        }
        return list;
    }

    public List<Wizyta> findByPodatnik(String podatnikId) throws SQLException {
        List<Wizyta> list = new ArrayList<>();
        String sql = "SELECT id, data, pracownikId FROM Wizyta WHERE podatnikId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, podatnikId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    LocalDate data = LocalDate.parse(rs.getString("data"));
                    String pracownikId = rs.getString("pracownikId");
                    list.add(new Wizyta(id, podatnikId, data, pracownikId));
                }
            }
        }
        return list;
    }

    public void update(Wizyta w) throws SQLException {
        // Jeśli chcesz umożliwić edycję daty/pracownika, np.:
        String sql = "UPDATE Wizyta SET podatnikId = ?, data = ?, pracownikId = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, w.getPodatnikId());
            ps.setString(2, w.getData().toString());
            ps.setString(3, w.getPracownikId());
            ps.setString(4, w.getId());
            ps.executeUpdate();
        }
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Wizyta WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
