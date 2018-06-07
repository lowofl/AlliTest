import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Article {
    private SimpleStringProperty lev = new SimpleStringProperty();
    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty nr = new SimpleStringProperty();
    private SimpleStringProperty proj = new SimpleStringProperty();
    private SimpleStringProperty prio = new SimpleStringProperty();
    private SimpleStringProperty chemText = new SimpleStringProperty();
    private SimpleStringProperty date = new SimpleStringProperty();
    private SimpleStringProperty user = new SimpleStringProperty();

    public int getID() {
        return ID.get();
    }

    public SimpleIntegerProperty IDProperty() {
        return ID;
    }

    public void setID(int ID) {
        this.ID.set(ID);
    }

    private SimpleIntegerProperty ID = new SimpleIntegerProperty();


    public String getTable() {
        return table.get();
    }

    public SimpleStringProperty tableProperty() {
        return table;
    }

    public void setTable(String table) {
        this.table.set(table);
    }

    private SimpleStringProperty table = new SimpleStringProperty();


    public String getReceived() {
        return received.get();
    }

    public SimpleStringProperty receivedProperty() {
        return received;
    }

    public void setReceived(String received) {
        this.received.set(received);
    }

    private SimpleStringProperty received = new SimpleStringProperty();

    public String getOrdered() {
        return ordered.get();
    }

    public SimpleStringProperty orderedProperty() {
        return ordered;
    }

    public void setOrdered(String ordered) {
        this.ordered.set(ordered);
    }

    private SimpleStringProperty ordered = new SimpleStringProperty("ord");

    public String getPris() {
        return pris.get();
    }

    public SimpleStringProperty prisProperty() {
        return pris;
    }

    public void setPris(String pris) {
        this.pris.set(pris);
    }

    private SimpleStringProperty pris = new SimpleStringProperty();

    public String getKyl() {
        return kyl.get();
    }

    public SimpleStringProperty kylProperty() {
        return kyl;
    }

    public void setKyl(String kyl) {
        this.kyl.set(kyl);
    }

    private SimpleStringProperty kyl = new SimpleStringProperty("RT");


    public void setLev(String lev) {
        this.lev.set(lev);
    }

    public String getLev() {
        return lev.get();
    }

    public void setProj(String proj) {
        this.proj.set(proj);
    }

    public String getProj() {
        return proj.get();
    }

    public void setPrio(String prio) {
        this.prio.set(prio);
    }

    public String getPrio() {
        return prio.get();
    }

    public void setChemText(String chemText) {
        this.chemText.set(chemText);
    }

    public String getChemText() {
        return chemText.get();
    }

    public void setDate(String date) {
        this.date.set(date);
    }

    public String getDate() {
        return date.get();
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public String getUser() {
        return user.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public String getName() {
        return name.get();
    }

    public void setNr(String nr) {
        this.nr.set(nr);
    }

    public String getNr() {
        return nr.get();
    }
}
