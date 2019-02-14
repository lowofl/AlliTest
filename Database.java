import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Separator;
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
                + " CONSTRAINT unik_anv UNIQUE(anv)"
                + ");";

        //deklarerar levs
        String levs = "CREATE TABLE IF NOT EXISTS levs(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + " arts integer DEFAULT 0, \n"
                + " CONSTRAINT unik_lev UNIQUE(lev)"
                + ");";


        //deklarerar articles
        String articles = "CREATE TABLE IF NOT EXISTS articles(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + " name text NOT NULL, \n"
                + " nr text NOT NULL, \n"
                + " uses integer DEFAULT 0, \n"
                + " CONSTRAINT unik_lev_och_nr UNIQUE(lev,nr)"
                + ");";

        //alla orders

        String orders = "CREATE TABLE IF NOT EXISTS orders(\n"
                + "	id integer PRIMARY KEY,\n"
                + "	lev text NOT NULL,\n"
                + "	name text NOT NULL,\n"
                + "	nr text NOT NULL,\n"
                + " pris text,\n"
                + " antal integer DEFAULT 1,\n"
                + "	proj text,\n"
                + " prio text, \n"
                + " chemText text, \n"
                + " ordered real, \n"
                + " added real, \n"
                + " user text NOT NULL, \n"
                + " recuser text, \n"
                + " attuser text, \n"
                + " kyl text DEFAULT none, \n"
                + " mottagen real, \n"
                + " tabell text NOT NULL \n"
                + ");";
        try (
                Connection conn = DriverManager.getConnection(name);
                Statement stmt = conn.createStatement()) {
            stmt.execute(articles);
            stmt.execute(levs);
            stmt.execute(orders);
            stmt.execute(users);
            stmt.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        createUser("Olof","temp",true,true);
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
    public ObservableList<User> getUsers(){
        ObservableList<User> users = FXCollections.observableArrayList();
        String sql = "SELECT * FROM users";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();
            while(rs.next()){
                User user = new User();
                user.setName(rs.getString("anv"));
                int admin = rs.getInt("admin"), cost = rs.getInt("cost");
                user.setAdmin(admin==1,cost==1);
                users.add(user);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return users;
    }
    public boolean addBest(String lev, String name, String nr, int antal, String pris, String proj, String prio, String chem, String user) {
        String sql = "INSERT INTO orders(lev,name,nr,antal,pris,proj,prio,chemText,added,user,tabell) VALUES(?,?,?,?,?,?,?,?,julianday('now')+0.5,?,?)";
        String currTable = "bests";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lev);
            pstmt.setString(2, name);
            pstmt.setString(3, nr);
            pstmt.setInt(4,antal);
            pstmt.setString(5, pris);
            pstmt.setString(6, proj);
            pstmt.setString(7, prio);
            pstmt.setString(8, chem);
            pstmt.setString(9, user);
            pstmt.setString(10, currTable);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        sql = "UPDATE articles SET uses = uses + 1 WHERE lev = ? AND name = ? AND nr = ?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, lev);
            pstmt.setString(2, name);
            pstmt.setString(3, nr);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;



    }
    /* Returnerar data över en tabell i bests */
    public ObservableList <Article> getTable(int i){
        String sql = "SELECT id,lev,name,nr,antal,pris,proj,prio,chemText,date(ordered),date(added),user,kyl,date(mottagen),tabell, recuser, attuser,comment FROM orders WHERE tabell = ";
        //String sql = "SELECT * FROM orders WHERE tabell = ";
        if(i==0){
            sql += "'bests'";
        }else if(i==1){
            sql += "'orders'";
        }else if(i==2){
            sql += "'deliveries'";
        }else if(i==3){
            sql += "'fintable'";
        }else if(i==4){
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
                art.setAntal(rs.getInt("antal"));
                art.setUser(rs.getString("user"));
                art.setProj(rs.getString("proj"));
                art.setNr(rs.getString("nr"));
                art.setPrio(rs.getString("prio"));
                art.setChemText(rs.getString("chemText"));
                art.setOrdered(rs.getString("date(ordered)")); //är de getStrings?
                art.setDate(rs.getString("date(added)"));
                art.setKyl(rs.getString("kyl"));
                art.setTable(rs.getString("tabell"));
                art.setReceived(rs.getString("date(mottagen)"));
                art.setComment(rs.getString("comment"));
                art.setRecuser(rs.getString("recuser"));
                art.setAttuser(rs.getString("attuser"));


                data.add(art);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
    public ObservableList<Object> getLevOnly(){
        ObservableList <Object> levs = FXCollections.observableArrayList();
        String sql = "SELECT lev FROM levs WHERE arts>0 ORDER BY lev ASC";
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
    public ObservableList<Object> getLevOptions(){
        ObservableList<Object> top5 = FXCollections.observableArrayList();
        ObservableList<Object> levs = getLevOnly();
        String sql = "SELECT lev FROM levs WHERE arts>0 ORDER BY arts DESC";
        try {
            Connection conn = connect();

            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                top5.add(rs.getString("lev"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        top5.remove(4,top5.size()-1);
        top5.add(new Separator());
        top5.add(new Separator());
        top5.add(new Separator());
        top5.add(new Separator());
        top5.addAll(levs);
        return top5;
    }
    public ObservableList<Object> getProdOnly(String lev){
        //onödigt? Men liknande lösning som getLevOnly
        ObservableList <Object> prods = FXCollections.observableArrayList();
        String sql = "SELECT name FROM articles WHERE lev = ? ORDER BY name ASC";
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
    public ObservableList<Object> getProdOptions(String lev){
        String sql = "SELECT name FROM articles WHERE lev = ? ORDER BY uses DESC";
        ObservableList<Object> top5 = FXCollections.observableArrayList();
        ObservableList<Object> prods = getProdOnly(lev);
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, lev);

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                top5.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        top5.remove(4,top5.size()-1);
        top5.add(new Separator());
        top5.add(new Separator());
        top5.add(new Separator());
        top5.add(new Separator());
        top5.addAll(prods);
        return top5;
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
    public Article getArtByNr(String nr){
        Article art = new Article();
        String sql = "SELECT * FROM articles WHERE nr=?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nr);

            ResultSet rs = pstmt.executeQuery();
            art.setID(rs.getInt("id"));
            art.setLev(rs.getString("lev"));
            art.setName(rs.getString("name"));
            art.setNr(rs.getString("nr"));
            rs.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return art;
    }
    public boolean createArticle(String lev, String name, String nr){
        if(lev.length()<1 || name.length()<1 || nr.length()<1){
            return false;
        }
        lev = lev.toUpperCase();
        name = name.toUpperCase();
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

        sql = "UPDATE levs SET arts = arts + 1 WHERE lev=?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, lev);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return true;
    }
    public void orderAccepted(String table, int id, String user){
        String sql = "UPDATE orders SET tabell=? WHERE id=?";
        if(table.equals("bests")){
            table = "orders";
            setAttUser(id,user);
        }else if(table.equals("orders")){
            table = "deliveries";
            setOrderedTime(id);
        }else if(table.equals("deliveries")){
            setMottagen(id,user);
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
    private void setMottagen(int id, String user){
        System.out.println(user);
        String sql = "UPDATE orders SET mottagen=julianday('now')+0.5, recuser = ? WHERE id=?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,user);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void setOrderedTime(int id){
        String sql = "UPDATE orders SET ordered=julianday('now')+0.5 WHERE id=?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void setAttUser(int id, String user){
        String sql = "UPDATE orders SET attuser = ? WHERE id=?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public User logIn(String anv, String pw){

        User user = new User("",false,false);
        String pwDb = "";
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
            System.out.println(e.getMessage()); //ingen sådan användare
        }
        if(pw.equals(pwDb)){ //lyckad login
            user = new User(anv,admin==1,cost==1);
        }

        return user;

    }
    public boolean removeArticle(String lev,String name,String nr){
        String sql = "DELETE FROM articles WHERE lev=? AND name=? AND nr=?";

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

        sql = "UPDATE levs SET arts = arts-1 WHERE lev=?";

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
    public boolean createUser(String anv, String pw, boolean adminB, boolean costB){
        int admin = 0, cost = 0;
        if(adminB){
            admin = 1;
            cost = 1;
        }
        if(costB){
            cost = 1;
        }


        String sql = "INSERT INTO users(anv,pw,admin,cost) VALUES(?,?,?,?)";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anv);
            pstmt.setString(2, pw);
            pstmt.setInt(3, admin);
            pstmt.setInt(4, cost);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public boolean deleteUser(String anv, String pw){
        String sql = "DELETE FROM users WHERE anv=? AND pw=?";

        try (

            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anv);
            pstmt.setString(2, pw);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
        return true;
    }
    public void adminDeleteUser(String anv){
        String sql = "DELETE FROM users WHERE anv=?";

        try (

                Connection conn = this.connect();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, anv);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public Article getOrderByID(int id){
        Article art = new Article();
        String sql = "SELECT id,lev,name,nr,antal,pris,proj,prio,chemText,user,date(added),date(ordered),tabell,kyl,date(mottagen),comment,recuser,attuser FROM orders WHERE id=?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1,id);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                art.setID(rs.getInt("id"));
                art.setLev(rs.getString("lev"));
                art.setName(rs.getString("name"));
                art.setPris(rs.getString("pris"));
                art.setAntal(rs.getInt("antal"));
                art.setUser(rs.getString("user"));
                art.setProj(rs.getString("proj"));
                art.setNr(rs.getString("nr"));
                art.setPrio(rs.getString("prio"));
                art.setChemText(rs.getString("chemText"));
                art.setOrdered(rs.getString("date(ordered)")); //är de getStrings?
                art.setDate(rs.getString("date(added)"));
                art.setKyl(rs.getString("kyl"));
                art.setTable(rs.getString("tabell"));
                art.setReceived(rs.getString("date(mottagen)"));
                art.setRecuser(rs.getString("recuser"));
                art.setAttuser(rs.getString("attuser"));
                art.setComment(rs.getString("comment"));


            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return art;
    }
    public void clearLev(){
        String sql = "UPDATE orders SET tabell='reports' WHERE tabell='fintable' AND mottagen+30<julianday('now')+0.5"; //Hårdkodad 30 dagar remove

        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public ObservableList<Article> getSearchData(String lev, String name, String nr, int from, int to){
        ObservableList<Article> data = FXCollections.observableArrayList();
        String sql = "SELECT id,lev,name,nr,antal,pris,proj,prio,chemText,user,date(added),date(ordered),tabell,kyl,date(mottagen),comment,recuser,attuser FROM orders WHERE tabell = 'reports'"
                + " AND lev LIKE ? AND name LIKE ? AND nr LIKE ? AND added-2458261+17673 > ? AND added-2458261+17673 < ?;"; //2458261+17673
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,"%" +lev+"%");
            pstmt.setString(2, "%" +name+"%");
            pstmt.setString(3, "%" +nr+"%");
            pstmt.setInt(4, from);
            pstmt.setInt(5, to);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Article art = new Article();
                art.setID(rs.getInt("id"));
                art.setLev(rs.getString("lev"));
                art.setName(rs.getString("name"));
                art.setPris(rs.getString("pris"));
                art.setAntal(rs.getInt("antal"));
                art.setUser(rs.getString("user"));
                art.setProj(rs.getString("proj"));
                art.setNr(rs.getString("nr"));
                art.setPrio(rs.getString("prio"));
                art.setChemText(rs.getString("chemText"));
                art.setOrdered(rs.getString("date(ordered)")); //är de getStrings?
                art.setDate(rs.getString("date(added)"));
                art.setKyl(rs.getString("kyl"));
                art.setTable(rs.getString("tabell"));
                art.setReceived(rs.getString("date(mottagen)"));
                art.setRecuser(rs.getString("recuser"));
                art.setAttuser(rs.getString("attuser"));
                art.setComment(rs.getString("comment"));

                data.add(art);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return data;
    }
    public void deleteOrder(int id){
        String sql = "DELETE FROM orders WHERE id=?";
        try (
            Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void setComment(int id, String comment){
        String sql = "UPDATE orders SET comment=? WHERE id=?";
        try {
            Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1,comment);
            pstmt.setInt(2, id);
            pstmt.executeUpdate();
        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}

