package Services.dao;

import Model.Mandat;
import Services.DatabaseManager;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MandatDAO {

    public void save(Mandat m) throws SQLException {
        String sql = "INSERT INTO Mandat(id, podatnikId, kwota, oplacony, dataWystawienia) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getId());
            ps.setString(2, m.getPodatnikId());
            ps.setDouble(3, m.getKwota());
            ps.setInt(4, m.isOplacony() ? 1 : 0);
            ps.setString(5, m.getDataWystawienia().toString());
            ps.executeUpdate();
        }
    }

    public void update(Mandat m) throws SQLException {
        String sql = "UPDATE Mandat SET oplacony = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, m.isOplacony() ? 1 : 0);
            ps.setString(2, m.getId());
            ps.executeUpdate();
        }
    }

    public Optional<Mandat> findById(String id) throws SQLException {
        String sql = "SELECT podatnikId, kwota, oplacony, dataWystawienia FROM Mandat WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String podatnikId = rs.getString("podatnikId");
                    double kwota = rs.getDouble("kwota");
                    boolean oplacony = rs.getInt("oplacony") == 1;
                    LocalDate data = LocalDate.parse(rs.getString("dataWystawienia"));
                    return Optional.of(new Mandat(id, podatnikId, kwota, oplacony, data));
                }
            }
        }
        return Optional.empty();
    }

    public List<Mandat> findAll() throws SQLException {
        List<Mandat> list = new ArrayList<>();
        String sql = "SELECT id, podatnikId, kwota, oplacony, dataWystawienia FROM Mandat";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String id = rs.getString("id");
                String podatnikId = rs.getString("podatnikId");
                double kwota = rs.getDouble("kwota");
                boolean oplacony = rs.getInt("oplacony") == 1;
                LocalDate data = LocalDate.parse(rs.getString("dataWystawienia"));
                list.add(new Mandat(id, podatnikId, kwota, oplacony, data));
            }
        }
        return list;
    }

    public List<Mandat> findByPodatnik(String podatnikId) throws SQLException {
        List<Mandat> list = new ArrayList<>();
        String sql = "SELECT id, kwota, oplacony, dataWystawienia FROM Mandat WHERE podatnikId = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, podatnikId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String id = rs.getString("id");
                    double kwota = rs.getDouble("kwota");
                    boolean oplacony = rs.getInt("oplacony") == 1;
                    LocalDate data = LocalDate.parse(rs.getString("dataWystawienia"));
                    list.add(new Mandat(id, podatnikId, kwota, oplacony, data));
                }
            }
        }
        return list;
    }

    public void delete(String id) throws SQLException {
        String sql = "DELETE FROM Mandat WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        }
    }
}
