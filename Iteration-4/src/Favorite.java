import java.io.Serializable;

public class Favorite implements Serializable {
    private int count;
    private String id;
    private int serial;
    private String affiliCommodityId;
    private String affilShopId;
    public Favorite (String commodityId, int count, int serial, String shopId) {
        id = "F-" + Global.getCurrentFavoriteId();
        this.count = count;
        affiliCommodityId = commodityId;
        this.serial = serial;
        affilShopId = shopId;
    }
    public void changeCount(int count) {
        this.count += count;
    }
    public String getId() { return id; }
    public String getAffilShopId() { return affilShopId; }
    public int getSerial() { return serial; }
    public int getCount() { return count; }
    public String getAffiliCommodityId() { return affiliCommodityId; }
    public void changeSerial(int serial) {
        this.serial = serial;
    }
}
