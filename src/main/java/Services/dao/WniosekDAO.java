package Services.dao;

import Model.Wniosek;
import Services.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WniosekDAO {

    public void save(Wniosek w) throws SQLException {
        String sql = "INSERT INTO Wniosek(id, podatnikId, tresc, status, dataZlozenia) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, w.getId());
            ps.setString(2, w.getPodatnikId());
            ps.setString(3, w.getTresc());
            ps.setString(4, w.getStatus().name());
            ps.setString(5, w.getDataZlozenia().toString());
            ps.executeUpdate();
        }
    }

    public void updateStatus(String id, Wniosek.Status status) throws SQLException {
        String sql = "UPDATE Wniosek SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setString(2, id);
            ps.executeUpdate();
        }
    }

    public Optional<Wniosek> findById(String id) throws SQLException {
        String sql = "SELECT podatnikId, tresc, status, dataZlozenia FROM Wniosek WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String podatnikId = rs.getString("podatnikId");
                    String tresc = rs.getString("tresc");
                    Wniosek.Status status = Wniosek.Status.valueOf(rs.getString("status"));
                    LocalDate data = LocalDate.parse(rs.getString("dataZlozenia"));
                    return Optional.of(new Wniosek(id, podatnikId, tresc, status, data));
                }
            }
        }
        return Optional.empty();
    }

    public List<Wniosek> findAll() throws SQLException {
        List<Wniosek> list = new ArrayList<>();
        String sql = "SELECT id, podatnikId, tresc, status, dataZlozenia FROM Wniosek";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String podatnikId = rs.getString("podatnikId");
                String tresc = rs.getString("tresc");
                Wniosek.Status status = Wniosek.Status.valueOf(rs.getString("status"));
                LocalDate data = LocalDate.parse(rs.getString("dataZlozenia"));
                list.add(new Wniosek(id, podatnikId, tresc, status, data));
            }
        }
        return list;
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Wniosek WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
