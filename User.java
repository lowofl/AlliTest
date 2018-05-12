public class User {

    private String name;
    private boolean costAdmin, orderAdmin;
    public User(String name, boolean costAdmin, boolean orderAdmin){
        this.name = name;
        this.costAdmin = costAdmin;
        this.orderAdmin = orderAdmin;
    }

    public boolean isCostAdmin() {
        return costAdmin;
    }

    public boolean isOrderAdmin() {
        return orderAdmin;
    }
    public String getName(){
        return name;
    }
}
