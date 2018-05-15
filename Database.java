import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Database {
    private String name;
    public Database(String fileName) {
        name = "jdbc:sqlite:" + fileName;


        //skapar själva DB
        try (Connection conn = DriverManager.getConnection(name)) {
            if (conn != null) {
                DatabaseMetaData meta = conn.getMetaData();
                System.out.println("The driver name is " + meta.getDriverName());
                System.out.println("A new database has been created.");
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        //deklarerar users

        String users = "CREATE TABLE IF NOT EXISTS users(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	anv text NOT NULL,\n"
                + "	pw text NOT NULL,\n"
                + " admin integer NOT NULL, \n"
                + " cost integer NOT NULL, \n"
                + " CONSTRAINT unik_lev UNIQUE(anv)"
                + ");";

        //deklarerar levs
        String levs = "CREATE TABLE IF NOT EXISTS levs(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + " CONSTRAINT unik_lev UNIQUE(lev)"
                + ");";


        //deklarerar articles
        String articles = "CREATE TABLE IF NOT EXISTS articles(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + " name text NOT NULL, \n"
                + " nr text NOT NULL, \n"
                + " CONSTRAINT unik_lev_och_nr UNIQUE(lev,nr)"
                + ");";

        //alla orders

        String orders = "CREATE TABLE IF NOT EXISTS orders(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + "	name text NOT NULL,\n"
                + "	nr text NOT NULL,\n"
                + " pris text,\n"
                + "	proj text,\n"
                + " prio text, \n"
                + " chemText text, \n"
                + " ordered text, \n"
                + " date text, \n"
                + " user text NOT NULL, \n"
                + " kyl text DEFAULT RT, \n"
                + " mottagen text, \n"
                + " tabell text NOT NULL \n"
                + ");";

        try (
                Connection conn = DriverManager.getConnection(name);
                Statement stmt = conn.createStatement()) {
            stmt.execute(articles);
            stmt.execute(levs);
            stmt.execute(orders);
            stmt.execute(users);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
        // SQLite connection string
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(name);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }


    /* Lägger in en artikel i bests*/
    public void addBest(String lev, String name, String nr, String pris, String proj, String prio, String chem, String user) {
        String sql = "INSERT INTO orders(lev,name,nr,pris,proj,prio,chemText,date,user,tabell) VALUES(?,?,?,?,?,?,?,?,?,?)";
        Date d = new Date(System.currentTimeMillis());
        String date = d.toString();
        String currTable = "bests";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lev);
            pstmt.setString(2, name);
            pstmt.setString(3, nr);
            pstmt.setString(4, pris);
            pstmt.setString(5, proj);
            pstmt.setString(6, prio);
            pstmt.setString(7, chem);
            pstmt.setString(8, date);
            pstmt.setString(9, user);
            pstmt.setString(10,currTable);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* Returnerar data över en tabell i bests */
    public ObservableList <Article> getTable(String table){
        String sql = "SELECT id,lev,name,nr,pris,proj,prio,chemText,user,date,ordered,tabell,kyl,mottagen FROM orders WHERE tabell = ";
        if(table.equals("Godkänn")){
            sql += "'bests'";
        }else if(table.equals("Beställd")){
            sql += "'orders'";
        }else if(table.equals("Mottagen")){
            sql += "'deliveries'";
        }else if(table.equals("Ta bort")){
            sql += "'fintable'";
        }else if(table.equals("Rapporter")){
            sql += "'reports'";
        }
        ObservableList <Article> data = FXCollections.observableArrayList();
        try {
            Connection conn = connect();
            ResultSet rs = conn.createStatement().executeQuery(sql);

            while (rs.next()) {
                Article art = new Article();
                art.setID(rs.getInt("id"));
                art.setLev(rs.getString("lev"));
                art.setName(rs.getString("name"));
                art.setPris(rs.getString("pris"));
                art.setUser(rs.getString("user"));
                art.setProj(rs.getString("proj"));
                art.setNr(rs.getString("nr"));
                art.setPrio(rs.getString("prio"));
                art.setChemText(rs.getString("chemText"));
                art.setOrdered(rs.getString("ordered"));
                art.setDate(rs.getString("date"));
                art.setKyl(rs.getString("kyl"));
                art.setTable(rs.getString("tabell"));
                art.setReceived(rs.getString("mottagen"));

                data.add(art);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
    public ObservableList<String> getLevOptions(){
        ObservableList <String> levs = FXCollections.observableArrayList();
        String sql = "SELECT lev FROM levs";
        try {
            Connection conn = connect();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                levs.add(rs.getString("lev"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return levs;
    }
    public ObservableList<String> getProdOptions(String lev){
        ObservableList <String> prods = FXCollections.observableArrayList();
        String sql = "SELECT name FROM articles WHERE lev = ?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, lev);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                prods.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return prods;
    }
    public String getProdNr(String lev, String name){
        String nr = "";
        String sql = "SELECT nr FROM articles WHERE lev = ? AND name = ?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, lev);
            pstmt.setString(2, name);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                nr = rs.getString("nr");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return nr;
    }
    public boolean createArticle(String lev, String name, String nr){
        String sql = "INSERT INTO articles(lev,name,nr) VALUES(?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lev);
            pstmt.setString(2, name);
            pstmt.setString(3, nr);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        sql = "INSERT INTO levs(lev) VALUES(?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lev);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
    public void orderAccepted(String table, int id){
        String sql = "UPDATE orders SET tabell=? WHERE id=?";
        if(table.equals("bests")){
            table = "orders";
        }else if(table.equals("orders")){
            table = "deliveries";
        }else if(table.equals("deliveries")){
            setMottagen(id);
            table = "fintable";
        }else if(table.equals("fintable")){
            table = "reports";
        }

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,table);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void setKyl(int id, String kyl){
        String sql = "UPDATE orders SET kyl=? WHERE id=?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,kyl);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void setMottagen(int id){
        String sql = "UPDATE orders SET mottagen=? WHERE id=?";
        Date d = new Date(System.currentTimeMillis());
        String mottagen = d.toString();
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,mottagen);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public User logIn(String anv, String pw){
        User user = null;
        String anvDb = "", pwDb = "";
        int admin = 0, cost = 0;
        String sql = "SELECT pw,admin,cost FROM users WHERE anv = ?";

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, anv);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                pwDb = rs.getString("pw");
                admin = rs.getInt("admin");
                cost = rs.getInt("cost");

            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null; //ingen sådan användare
        }
        if(pw.equals(pwDb)){ //lyckad login
            user = new User(anv,cost==1,admin==1);
        }

        return user;

    }

}

