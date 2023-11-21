import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Shop implements Serializable {
    private String id;
    private String registerState;
    private String name;
    private User affilMerchant;
    /**
     *表示当前店铺中的商品，商品编码”C-X“为唯一编号
     */
    private HashMap<String, Commodity> commodityOfShop;

    private HashMap<String, Order> orderOfShop;

    public HashMap<String, Commodity> getCommodityOfShop() {
        return commodityOfShop;
    }

    public Shop(String name, User merchant) {
        id = "S-" + Global.getCurrentShopId();
        registerState = "open";
        this.name = name;
        commodityOfShop = new HashMap<String, Commodity>();
        orderOfShop = new HashMap<String, Order>();
        affilMerchant = merchant;
    }

    public String getName() {
        return name;
    }
    public HashMap<String, Order> getOrderOfShop() { return orderOfShop;}

    /**
     * 返回当前店铺的状态，已注册或已注销
     * @return 当前店铺状态
     */
    public String getRegisterState(){
        return registerState;
    }

    public String getId() {
        return id;
    }


    public User getAffilMerchant() {
        return affilMerchant;
    }

    public void closeShop() {
        registerState = "closed";
        if (commodityOfShop == null) return;
        Iterator<Map.Entry<String, Commodity>> iterator = commodityOfShop.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Commodity> entry = iterator.next();
            Commodity commodity = entry.getValue();
            commodity.decreaseCount(commodity.getCount());
            // 满足删除条件时，使用迭代器的remove()方法安全删除元素
            iterator.remove();
        }
    }

    public void offShelfCommodity(String commodityId) {
        if (commodityOfShop.containsKey(commodityId) == false) {
            System.out.println("Error!! Commodity not exists");
        }
        commodityOfShop.get(commodityId).decreaseCount(commodityOfShop.get(commodityId).getCount());
        commodityOfShop.remove(commodityId);
    }
}
