public class CommodityUnit {
    private boolean availability;
    private String id;
    private String name;
    private double price;
    private User affilMerchant;
    public CommodityUnit(String id, String name, double price, User merchant) {
        availability = true;
        this.id = id;
        this.name = name;
        this.price = price;
        affilMerchant = merchant;
    }

    public String getId() {
        return id;
    }

    public double getPrice() {
        return price;
    }

    public String getName() {
        return name;
    }

    public User getAffilMerchant() {
        return affilMerchant;
    }

    public void terminate() {
        availability = false;
    }

    public boolean getAvailability() {
        return availability;
    }
}
