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
                + " kyl text \n"
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
        String sql = "";
        switch(name){
            case "Godkänn":
                sql = "SELECT lev,name,pris,proj,chemText,date FROM bests";
                break;
            case "Beställd":
                sql = "SELECT lev,name,nr,prio,user,date FROM orders";
                break;
            case "Mottagen":
                sql = "SELECT lev,name,nr,user,ordered FROM deliveries";
                break;
            case "Ta bort": /// ta bort??? ska inte ha någon add
                sql = "SELECT lev,name,nr,prio,user,date,kyl from fintable";
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
    public void orderAccepted(String table, String lev, String name, String nr, String pris, String proj,
                              String prio, String chem, String user, String date, String best, String rec, String kyl){
        String sql = "", sql2 = "";


        switch (table){
            case "Godkänn":
                sql = "DELETE FROM bests WHERE lev=? AND name=? AND pris = ? AND proj = ? AND chemText = ? AND date = ?";
                sql2 = "INSERT INTO orders(lev,name,nr,prio,user,date) VALUES(?,?,?,?,?,?)";

                try {
                    Connection conn = connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, lev);
                    pstmt.setString(2, name);
                    pstmt.setString(3, pris);
                    pstmt.setString(4, proj);
                    pstmt.setString(5, chem);
                    pstmt.setString(6, date);
                    pstmt.executeUpdate();

                    pstmt = conn.prepareStatement(sql2);
                    pstmt.setString(1, lev);
                    pstmt.setString(2, name);
                    pstmt.setString(3, nr);
                    pstmt.setString(4, prio);
                    pstmt.setString(5, user);
                    pstmt.setString(6, date);
                    pstmt.executeUpdate(); //inlagd i orders
                }catch (SQLException e) {
                    System.out.println(e.getMessage());
                }


                break;
            case "Beställd":
                sql = "DELETE FROM orders WHERE lev=? AND name=? AND nr=? AND prio = ? AND user = ? AND date = ?";
                sql2 = "INSERT INTO deliveries(lev,name,nr,user,ordered) VALUES(?,?,?,?,?)";

                try {
                    Connection conn = connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, lev);
                    pstmt.setString(2, name);
                    pstmt.setString(3, nr);
                    pstmt.setString(4, prio);
                    pstmt.setString(5, user);
                    pstmt.setString(6, date);
                    pstmt.executeUpdate();

                    pstmt = conn.prepareStatement(sql2);
                    pstmt.setString(1, lev);
                    pstmt.setString(2, name);
                    pstmt.setString(3, nr);
                    pstmt.setString(4, user);
                    pstmt.setString(5, best);
                    pstmt.executeUpdate(); //inlagd i deliveries
                }catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "Mottagen":
                sql = "DELETE FROM deliveries WHERE lev=? AND name=? AND nr=? AND user = ? AND ordered = ?";
                sql2 = "INSERT INTO fintable(lev,name,nr,prio,user,date,kyl) VALUES(?,?,?,?,?,?,?)";

                try {
                    Connection conn = connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, lev);
                    pstmt.setString(2, name);
                    pstmt.setString(3, nr);
                    pstmt.setString(4, user);
                    pstmt.setString(5, best);
                    pstmt.executeUpdate();

                    pstmt = conn.prepareStatement(sql2);
                    pstmt.setString(1, lev);
                    pstmt.setString(2, name);
                    pstmt.setString(3, nr);
                    pstmt.setString(4, prio);
                    pstmt.setString(5, user);
                    pstmt.setString(6, date);
                    pstmt.setString(7, kyl);
                    pstmt.executeUpdate(); //inlagd i fintable
                }catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
            case "Ta bort":
                sql = "DELETE FROM fintable WHERE lev=? AND name=? AND nr=? AND prio = ? AND user = ? AND date = ? AND kyl = ?";
                try {
                    Connection conn = connect();
                    PreparedStatement pstmt = conn.prepareStatement(sql);
                    pstmt.setString(1, lev);
                    pstmt.setString(2, name);
                    pstmt.setString(3, nr);
                    pstmt.setString(4, prio);
                    pstmt.setString(5, user);
                    pstmt.setString(6, date);
                    pstmt.setString(7, kyl);
                    pstmt.executeUpdate(); //uttagen ur fintable
                }catch (SQLException e) {
                    System.out.println(e.getMessage());
                }
                break;
            default:
                break;
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

