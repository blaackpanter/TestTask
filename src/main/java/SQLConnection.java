import java.sql.*;

public class SQLConnection {
    private static Statement st;
    private static ResultSet rs;
    private static Connection conn = null;

    public SQLConnection() {

        try {
            String url = "jdbc:sqlite:/Users/elenapranova/Documents/ExampleSQLlite.db";
            conn = DriverManager.getConnection(url);
            System.out.println("Connection has been established ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public String showAllMessages() throws SQLException {
        String messages = "";
        st = conn.createStatement();
        rs = st.executeQuery("select * from Student");
        while (rs.next()) {
            messages += rs.getString(1) + '\n';
        }
        return messages;
    }

    public void addMessage(String msg) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("insert into Student (message) values (?)");
        ps.setString(1, msg);
        ps.executeUpdate();
    }
}
