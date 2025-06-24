package Services.dao;

import Model.Podatnik;
import Services.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PodatnikDAO {
    public List<Podatnik> findAll() throws SQLException {
        List<Podatnik> list = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT id, imie, nazwisko FROM Podatnik";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Podatnik p = new Podatnik(rs.getString("imie"), rs.getString("nazwisko"));
                // ponieważ konstruktor generuje nowe ID, nadpisujemy aby zachować to z bazy:
                // więc w modelu musimy dodać konstruktor z ID lub użyć refleksji – ale prostsze: utworzymy osobny konstruktor w modelu:
                // tu załóżmy, że Podatnik ma protected konstruktor z ID:
                p = new Podatnik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko"));
                list.add(p);
            }
        }
        return list;
    }

    public Optional<Podatnik> findById(String id) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT id, imie, nazwisko FROM Podatnik WHERE id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Podatnik p = new Podatnik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko"));
                    return Optional.of(p);
                }
            }
        }
        return Optional.empty();
    }

    public List<Podatnik> searchByName(String filter) throws SQLException {
        List<Podatnik> list = new ArrayList<>();
        Connection conn = DatabaseManager.getConnection();
        String sql = "SELECT id, imie, nazwisko FROM Podatnik WHERE lower(imie || ' ' || nazwisko) LIKE ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + filter.toLowerCase() + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Podatnik p = new Podatnik(rs.getString("id"), rs.getString("imie"), rs.getString("nazwisko"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    public void save(Podatnik p) throws SQLException {
        Connection conn = DatabaseManager.getConnection();
        String sql = "INSERT INTO Podatnik(id, imie, nazwisko) VALUES (?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, p.getId());
            ps.setString(2, p.getImie());
            ps.setString(3, p.getNazwisko());
            ps.executeUpdate();
        }
    }

    // konstruktor w Model.Podatnik: dodać protected Podatnik(String id, String imie, String nazwisko)
}
