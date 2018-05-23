public class User {

    private String name;
    private boolean fullAdmin, orderAdmin;
    public User(String name, boolean fullAdmin, boolean orderAdmin){
        this.name = name;
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
        return name;
    }
}
