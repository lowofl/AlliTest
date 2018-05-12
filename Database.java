import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;

public class Database {
    private String name;
    public Database(String fileName) {
        name = "jdbc:sqlite:C:\\Users\\lowofl\\IdeaProjects\\Alligator2\\db\\" + fileName;


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

        //deklarerar tabellen bests
        String bests = "CREATE TABLE IF NOT EXISTS bests(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + "	name text NOT NULL,\n"
                + "	nr text NOT NULL,\n"
                + " pris text NOT NULL, \n"
                + "	proj text NOT NULL,\n"
                + " prio text NOT NULL, \n"
                + " chemText text, \n"
                + " date text NOT NULL, \n"
                + " user text NOT NULL \n"
                + ");";


        String orders = "CREATE TABLE IF NOT EXISTS orders(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + "	name text NOT NULL,\n"
                + "	nr text NOT NULL,\n"
                + " pris text NOT NULL, \n"
                + "	proj text NOT NULL,\n"
                + " prio text NOT NULL, \n"
                + " chemText text, \n"
                + " date text NOT NULL, \n"
                + " user text NOT NULL \n"
                + ");";

        String deliveries = "CREATE TABLE IF NOT EXISTS deliveries(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + "	name text NOT NULL,\n"
                + "	nr text NOT NULL,\n"
                + " pris text NOT NULL, \n"
                + "	proj text NOT NULL,\n"
                + " prio text NOT NULL, \n"
                + " chemText text, \n"
                + " date text NOT NULL, \n"
                + " user text NOT NULL \n"
                + ");";

        String fintable = "CREATE TABLE IF NOT EXISTS fintable(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + "	name text NOT NULL,\n"
                + "	nr text NOT NULL,\n"
                + " pris text NOT NULL, \n"
                + "	proj text NOT NULL,\n"
                + " prio text NOT NULL, \n"
                + " chemText text, \n"
                + " date text NOT NULL, \n"
                + " user text NOT NULL, \n"
                + " kyl text NOT NULL\n"
                + ");";

        try (
                Connection conn = DriverManager.getConnection(name);
                Statement stmt = conn.createStatement()) {
            stmt.execute(articles);
            stmt.execute(levs);
            stmt.execute(bests);
            stmt.execute(orders);
            stmt.execute(deliveries);
            stmt.execute(fintable);
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
        String sql = "INSERT INTO bests(lev,name,nr,pris,proj,prio,chemText,user,date) VALUES(?,?,?,?,?,?,?,?,?)";
        Date d = new Date(System.currentTimeMillis());
        String date = d.toString();

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lev);
            pstmt.setString(2, name);
            pstmt.setString(3, nr);
            pstmt.setString(4, pris);
            pstmt.setString(5, proj);
            pstmt.setString(6, prio);
            pstmt.setString(7, chem);
            pstmt.setString(8, user);
            pstmt.setString(9, date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    /* Returnerar data över en tabell i bests */
    public ObservableList <Article> getTable(String name){
        ObservableList <Article> data = FXCollections.observableArrayList();
        String sql = "SELECT lev,name,nr,pris,proj,prio,chemText,user,date";
        switch(name){
            case "Godkänn":
                sql += " FROM bests";
                break;
            case "Beställd":
                sql += " FROM orders";
                break;
            case "Mottagen":
                sql += " FROM deliveries";
                break;
            case "Ta bort": /// ta bort??? ska inte ha någon add
                sql += ",kyl FROM fintable";
                break;
            default:
                break;

        }

        try {
            Connection conn = connect();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                Article art = new Article();
                art.setLev(rs.getString("lev"));
                art.setName(rs.getString("name"));
                art.setNr(rs.getString("nr"));
                art.setPris(rs.getString("pris"));
                art.setProj(rs.getString("proj"));
                art.setPrio(rs.getString("prio"));
                art.setChemText(rs.getString("chemText"));
                art.setUser(rs.getString("user"));
                art.setDate(rs.getString("date"));
                if(name == "Ta bort"){
                    art.setKyl(rs.getString("kyl"));
                    System.out.println(rs.getString("kyl"));
                }

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
        }
        sql = "INSERT INTO levs(lev) VALUES(?)";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lev);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }

    /* En beställning är blir godkänd - och därför borttagen ur Articles och inlagd i Orders */
    public void orderAccepted(String table, String lev, String name, String nr, String pris, String proj, String prio, String chem, String user, String date, String kyl){
        String sql = "", sql2 = "";
        boolean fin = false;
        System.out.println(kyl);
        switch (table){
            case "Godkänn":
                sql = "DELETE FROM bests WHERE lev=? AND name=? AND nr=? AND pris = ? AND proj = ? AND prio = ? AND chemText = ? AND user = ? AND date = ?";
                sql2 = "INSERT INTO orders(lev,name,nr,pris,proj,prio,chemText,user,date) VALUES(?,?,?,?,?,?,?,?,?)";
                break;
            case "Beställd":
                sql = "DELETE FROM orders WHERE lev=? AND name=? AND nr=? AND pris = ? AND proj = ? AND prio = ? AND chemText = ? AND user = ? AND date = ?";
                sql2 = "INSERT INTO deliveries(lev,name,nr,pris,proj,prio,chemText,user,date) VALUES(?,?,?,?,?,?,?,?,?)";
                break;
            case "Mottagen":
                sql = "DELETE FROM deliveries WHERE lev=? AND name=? AND nr=? AND pris = ? AND proj = ? AND prio = ? AND chemText = ? AND user = ? AND date = ?";
                sql2 = "INSERT INTO fintable(lev,name,nr,pris,proj,prio,chemText,user,date,kyl) VALUES(?,?,?,?,?,?,?,?,?,?)";
                break;
            case "Ta bort":
                sql = "DELETE FROM fintable WHERE lev=? AND name=? AND nr=? AND pris = ? AND proj = ? AND prio = ? AND chemText = ? AND user = ? AND date = ? AND kyl = ?";
                fin = true;
                break;
            default:
                break;
        }
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, lev);
            pstmt.setString(2, name);
            pstmt.setString(3, nr);
            pstmt.setString(4, pris);
            pstmt.setString(5, proj);
            pstmt.setString(6, prio);
            pstmt.setString(7, chem);
            pstmt.setString(8, user);
            pstmt.setString(9, date);
            if(fin){
                pstmt.setString(10, kyl);
            }
            pstmt.executeUpdate(); //borttagen ur bests // eller liknande
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }


        if(!fin) { //går endast hit ifall det INTE är sista tabellen //se case: ta bort
            try {
                Connection conn = connect();
                PreparedStatement pstmt = conn.prepareStatement(sql2);
                pstmt.setString(1, lev);
                pstmt.setString(2, name);
                pstmt.setString(3, nr);
                pstmt.setString(4, pris);
                pstmt.setString(5, proj);
                pstmt.setString(6, prio);
                pstmt.setString(7, chem);
                pstmt.setString(8, user);
                pstmt.setString(9, date);
                if(name == "Mottagen"){
                    pstmt.setString(10, kyl);
                }
                pstmt.executeUpdate(); //inlagd i orders //eller liknande
            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
