import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Global {
    private static boolean loggingState = false;
    /**
     * 键为Kakafee卡号
     */
    public static Map<String, User> registeredUsers = new HashMap<>();
    /**
     * 全局商品单元
     */
    private static HashMap<String, CommodityUnit>
            commodityUnits = new HashMap<>();
    private static HashMap<String, Shop>
            globalShops = new HashMap<>();

    private static HashMap<String, Order>
            globalOrders = new HashMap<>();
    /**
     * mode仅有三种模式：顾客模式，商家模式和管理员模式
     */
    private static String mode = null;
    private static String currentLoggingCardNumber = null;

    private static int currentShopId = 0;
    private static int currentCommodityId = 0;
    private static int currentOrderId = 0;
    public static int getCurrentCommodityId() {
        return currentCommodityId;
    }

    public static int getCurrentShopId() {
        return currentShopId;
    }
    public static int getCurrentOrderId() { return currentOrderId;}

    public static void currentShopIdPlusOne() {
        currentShopId += 1;
    }
    public static void currentOrderIdPlusOne() {
        currentOrderId += 1;
    }
    public static void setLoggingStateTrue(User user) {
        loggingState = true;
        mode = user.getIdentityType();
        currentLoggingCardNumber = user.getCardNumber();
    }

    public static void setLoggingStateFalse() {
        loggingState = false;
        mode = null;
        currentLoggingCardNumber = null;
    }

    /**
     * 返回当前系统的模式
     * @return 顾客模式或商家模式或管理员模式
     */
    public static String getMode() {
        return mode;
    }
    public static String getCurrentLoggingCardNumber() {
        return currentLoggingCardNumber;
    }
    public static boolean getLoggingState() {
        return loggingState;
    }

    public static HashMap<String, Shop> getGlobalShops() {
        return globalShops;
    }
    public static HashMap<String, Order> getGlobalOrders() { return globalOrders; }

    public static User getCurrentLoginUser() {
        if (currentLoggingCardNumber == null) {
            System.out.println("没有用户登录");
        }
        return registeredUsers.get(currentLoggingCardNumber);
    }
    private static String getCommodityId() {
        currentCommodityId++;
        return "C-" + currentCommodityId;
    }

    public static Map<String, User> getRegisteredUsers() {
        return registeredUsers;
    }

    public static HashMap<String, CommodityUnit> getCommodityUnits() {
        return commodityUnits;
    }

    /**
     *创建一个Unit商品的实例并加入到全局变量commodityUnit中
     * @param commodityName
     * @param commodityPrice
     * @return
     */
    public static CommodityUnit createCommodity(String commodityName, double commodityPrice, User merchant) {
        CommodityUnit commodityUnit =
                new CommodityUnit(getCommodityId(), commodityName, commodityPrice, merchant);
        commodityUnits.put(commodityUnit.getId(), commodityUnit);
        return commodityUnit;
    }

    public static int getNumberId(String id) {
        String regex = "\\d+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(id);
        if (matcher.find()) {
            String numberString = matcher.group();
            return Integer.parseInt(numberString);
        } else {
            return -1;
        }
    }
}
