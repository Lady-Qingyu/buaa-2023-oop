import java.util.ArrayList;
import java.util.HashMap;

public class User {
    private String cardNumber;
    private String name;
    private String password;
    private String identityType;

    public User(String cardNumber, String name, String password, String identityType) {
        this.cardNumber = cardNumber;
        this.name = name;
        this.password = password;
        this.identityType = identityType;
    }

    public String getCardNumber() {
        return cardNumber;
    }
    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }

    public String getIdentityType() {
        return identityType;
    }

    public void printInfo() {
        System.out.println("Name: " + name);
        System.out.println("Kakafee number: " + cardNumber);
        System.out.println("Type: " + identityType);
    }


    public int getNumOfMyShop() {return 0;}

    public HashMap<String, Shop> getMyShop() {return null;}

    public ArrayList<String> getNameOfMyShop() {return null;}

    public void createMyShop(String name) {}

    public void createOwnedCommodities(String key) {}

    public HashMap<String, CommodityUnit> getOwnedCommodityUnit() {return null;}
}

class Administrator extends User {

    public Administrator(String cardNumber, String name, String password, String identityType) {
        super(cardNumber, name, password, identityType);
    }
}

class Merchant extends User {
    private int numOfMyShop;
    private HashMap<String, Shop> myShop;
    private ArrayList<String> nameOfMyShop;
    /**
     * 商家所拥有的商品单元
     */
    private HashMap<String, CommodityUnit> ownedCommodityUnit;
    public Merchant(String cardNumber, String name, String password, String identityType) {
        super(cardNumber, name, password, identityType);
        numOfMyShop = 0;
        nameOfMyShop = new ArrayList<String>();
        myShop = new HashMap<String, Shop>();
        ownedCommodityUnit = new HashMap<String, CommodityUnit>();
    }

    public HashMap<String, CommodityUnit> getOwnedCommodityUnit() {
        return ownedCommodityUnit;
    }

    public int getNumOfMyShop() {
        return numOfMyShop;
    }
    public ArrayList<String> getNameOfMyShop() {
        return nameOfMyShop;
    }

    public HashMap<String, Shop> getMyShop() {
        return myShop;
    }

    public void createMyShop(String name) {
        Global.currentShopIdPlusOne();
        Shop shop = new Shop(name, this);
        numOfMyShop += 1;
        myShop.put("S-" + Global.getCurrentShopId(), shop);
        Global.getGlobalShops().put("S-" + Global.getCurrentShopId(), shop);
        nameOfMyShop.add(shop.getName());
    }

    public void createOwnedCommodities(String key) {
        ownedCommodityUnit.put(key ,Global.getCommodityUnits().get(key));
    }

}

class Customer extends User {

    public Customer(String cardNumber, String name, String password, String identityType) {
        super(cardNumber, name, password, identityType);
    }
}

