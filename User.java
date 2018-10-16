import javafx.beans.property.SimpleStringProperty;
import sun.java2d.pipe.SpanShapeRenderer;

public class User {

    private SimpleStringProperty name = new SimpleStringProperty();
    private SimpleStringProperty admin = new SimpleStringProperty();
    private boolean fullAdmin, orderAdmin;

    public User(){
        setName("");
        fullAdmin = false;
        orderAdmin = false;
    }
    public User(String name, boolean fullAdmin, boolean orderAdmin){
        setName(name);
        setAdmin(fullAdmin, orderAdmin);
        this.fullAdmin = fullAdmin;
        this.orderAdmin = orderAdmin;
    }

    public boolean isFullAdmin() {
        return fullAdmin;
    }
    public boolean isOrderAdmin() {
        return orderAdmin;
    }
    public String getName(){
        return name.get();
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty adminProperty() {
        return admin;
    }
    public String getAdmin(){
        return admin.get();
    }
    public void setAdmin(boolean a, boolean o){
        this.admin.set("Grundrätt");
        if(o)
            this.admin.set("Beställrätt");
        if(a)
            this.admin.set("Adminrätt");
    }
}
