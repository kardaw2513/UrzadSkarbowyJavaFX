package Services.dao;

import Model.Pracownik;
import Services.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PracownikDAO {
    public List<Pracownik> findAll() throws SQLException {
        List<Pracownik> list = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT id, imie, nazwisko FROM Pracownik";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Pracownik p = new Pracownik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko"));
                list.add(p);
            }
        }
        return list;
    }

    public Optional<Pracownik> findById(String id) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT id, imie, nazwisko FROM Pracownik WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Pracownik p = new Pracownik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko"));
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    public List<Pracownik> searchByName(String filter) throws SQLException {
        List<Pracownik> list = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT id, imie, nazwisko FROM Pracownik WHERE lower(imie || ' ' || nazwisko) LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + filter.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Pracownik p = new Pracownik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    public void save(Pracownik p) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "INSERT INTO Pracownik(id, imie, nazwisko) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getImie());
            ps.setString(3, p.getNazwisko());
            ps.executeUpdate();
        }
    }

    // Konstruktor w Model.Pracownik: dodać protected Pracownik(String id, String imie, String nazwisko)
}
