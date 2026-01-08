import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        return new Product(
            rs.getInt("out_id"),
            rs.getString("out_name"),
            rs.getFloat("out_price"),
            rs.getString("out_title"),
            rs.getDate("out_created"),
            rs.getString("out_catalog"),
            rs.getInt("out_status")
        );
    }

    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM get_all_products()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean addProduct(Product p) {
        Connection conn = null;
        String sql = "CALL add_product(?, ?, ?, ?, ?, ?)";
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Begin Transaction

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setString(1, p.getName());
                stmt.setFloat(2, p.getPrice());
                stmt.setString(3, p.getTitle());
                stmt.setDate(4, p.getCreated());
                stmt.setString(5, p.getCatalog());
                stmt.setInt(6, p.getStatus());
                stmt.execute();
            }

            conn.commit(); // Commit Transaction
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try {
                    System.err.println("Transaction Rolling Back...");
                    conn.rollback(); // Rollback Transaction
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean updateProduct(Product p) {
        Connection conn = null;
        String sql = "CALL update_product(?, ?, ?, ?, ?, ?)";
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setInt(1, p.getId());
                stmt.setString(2, p.getName());
                stmt.setFloat(3, p.getPrice());
                stmt.setString(4, p.getTitle());
                stmt.setString(5, p.getCatalog());
                stmt.setInt(6, p.getStatus());
                stmt.execute();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean deleteProduct(int id) {
        Connection conn = null;
        String sql = "CALL delete_product(?)";
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            try (CallableStatement stmt = conn.prepareCall(sql)) {
                stmt.setInt(1, id);
                stmt.execute();
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) try { conn.close(); } catch (SQLException e) {}
        }
    }

    public List<Product> searchByName(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM search_product_by_name(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, keyword);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Product> sortProductsByPrice() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM sort_products_by_price_asc()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void statByCatalog() {
        String sql = "SELECT * FROM stat_product_by_catalog()";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            System.out.printf("%-20s | %s\n", "Catalog", "Quantity");
            System.out.println("--------------------------------");
            while (rs.next()) {
                System.out.printf("%-20s | %d\n", rs.getString("out_catalog"), rs.getLong("out_count"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public Product getProductById(int id) {
        String sql = "SELECT * FROM get_product_by_id(?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
             stmt.setInt(1, id);
             ResultSet rs = stmt.executeQuery();
             if(rs.next()) {
                 return mapResultSetToProduct(rs);
             }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
