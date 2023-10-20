import java.util.*;
import java.util.Objects;
import java.util.regex.Pattern;

interface Command {
    default boolean validateCardNumber(String cardNumber) {
        if (cardNumber.length() != 12) {
            return false;
        } else if (!cardNumber.matches("\\d+")) {
            return false;
        }

        String regionCode = cardNumber.substring(0, 4);
        String birthYearCode = cardNumber.substring(4, 8);
        String identificationCode = cardNumber.substring(8, 12);

        // 检查区域代码、出生年份代码和识别码的范围
        int region = Integer.parseInt(regionCode);
        int birthYear = Integer.parseInt(birthYearCode);
        int identification = Integer.parseInt(identificationCode);

        if (region < 1 || region > 4500 || birthYear < 1785 || birthYear > 1886 || identification < 1000 || identification > 3000) {
            return false;
        }

        return true;
    }
    default boolean validateUserName(String name) {
        String regex = "[A-Za-z][A-Za-z_]{3,15}";
        return name.matches(regex);
    }
    default boolean validateShopOrCommodityName(String name) {
        String regex = "^[a-zA-Z][a-zA-Z\\-_]{0,49}$";
        return name.matches(regex);
    }
    default boolean validatePassword(String password) {
        // 使用正则表达式验证密码格式
        String regex = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@_%$])[A-Za-z\\d@_%$]{8,16}$";
        return Pattern.matches(regex, password);
    }
    default boolean validateConfirmPassword(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
    default boolean validateIdentityType(String identityType) {
        return identityType.equals("Administrator") || identityType.equals("Merchant") || identityType.equals("Customer");
    }
    default boolean isCardNumberRegistered(String cardNumber) {
        return Global.registeredUsers.containsKey(cardNumber);
    }
    default boolean validateShopId(String shopId) {
        String regex = "^S-[1-9]\\d*$";
        return shopId.matches(regex);
    }

    default boolean validateCommodityId(String commodityId) {
        String regex = "^C-[1-9]\\d*$";
        return commodityId.matches(regex);
    }

    default boolean validatePrice(String price) {
        String regex = "^(?!0+(?:\\.0+)?$)(?!99999999\\.00)([1-9]\\d{0,7}|0)(\\.\\d{1,2})?$";
        return price.matches(regex);
    }
    default boolean validateCount(String count) {
        String regex = "^[1-9]\\d*$";
        return count.matches(regex);
    }
    default boolean validateGlobalShopIdExistence(String shopId) {
        if (Global.getGlobalShops().containsKey(shopId)) {
            return Global.getGlobalShops().get(shopId).getRegisterState().equals("open");
        } else {
            return false;
        }
    }
    void execute(String[] args);
}

class Quit implements Command {
    @Override
    public void execute(String[] args) {
        if (args.length != 0) {
            System.out.println("Illegal argument count");
            return;
        }
        System.out.println("----- Good Bye! -----");
        System.exit(0);
    }
}

class Register implements Command {


    @Override
    public void execute(String[] args) {
        if (args.length != 5) {
            System.out.println("Illegal argument count");
            return;
        } else if (Global.getLoggingState()) {
            System.out.println("Already logged in");
            return;
        }
        String cardNumber = args[0];
        String name = args[1];
        String password = args[2];
        String confirmPassword = args[3];
        String identityType = args[4];
        if (!validateCardNumber(cardNumber)) {
            System.out.println("Illegal Kakafee number");
        } else if (isCardNumberRegistered(cardNumber)) {
            System.out.println("Kakafee number exists");
        } else if (!validateUserName(name)) {
            System.out.println("Illegal name");
        } else if (!validatePassword(password)) {
            System.out.println("Illegal password");
        } else if (!validateConfirmPassword(password, confirmPassword)) {
            System.out.println("Passwords do not match");
        } else if (!validateIdentityType(identityType)) {
            System.out.println("Illegal identity");
        } else {
            switch (identityType) {
                case "Administrator":
                    Global.registeredUsers.put(cardNumber, new Administrator(cardNumber, name, password, identityType));
                    break;
                case "Merchant":
                    Global.registeredUsers.put(cardNumber, new Merchant(cardNumber, name, password, identityType));
                    break;
                case "Customer":
                    Global.registeredUsers.put(cardNumber, new Customer(cardNumber, name, password, identityType));
                    break;
            }
            System.out.println("Register success");
        }
    }
}

class Login implements Command {

    @Override
    public void execute(String[] args) {
        if (args.length != 2) {
            System.out.println("Illegal argument count");
            return;
        } else if (Global.getLoggingState()) {
            System.out.println("Already logged in");
            return;
        }
        String cardNumber = args[0];
        String password = args[1];
        if (!validateCardNumber(cardNumber)) {
            System.out.println("Illegal Kakafee number ");
        } else if (!isCardNumberRegistered(cardNumber)) {
            System.out.println("Kakafee number not exists");
        } else {
            String userPassword = Global.registeredUsers.get(cardNumber).getPassword();
            if (!password.equals(userPassword)) {
                System.out.println("Wrong password");
            } else {
                User user = Global.registeredUsers.get(cardNumber);
                Global.setLoggingStateTrue(user);
                System.out.println("Welcome to TMS");
            }
        }
    }
}

class Logout implements Command {
    @Override
    public void execute(String[] args) {
        if (args.length != 0) {
            System.out.println("Illegal argument count");
        } else if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
        } else {
            Global.setLoggingStateFalse();
            System.out.println("Bye~");
        }
    }
}

class PrintInfo implements Command {
    @Override
    public void execute(String[] args) {

        if (args.length != 0 && args.length != 1) {
            System.out.println("Illegal argument count");
            return;
        } else if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }

        User currentUser = Global.getCurrentLoginUser();
        if (args.length == 0) {
            currentUser.printInfo();
        } else if (args.length == 1) {
            if (Global.getMode().equals("Administrator")) {
                String cardNumber = args[0];
                if (!validateCardNumber(cardNumber)) {
                    System.out.println("Illegal Kakafee number");
                } else if (!Global.registeredUsers.containsKey(cardNumber)) {
                    System.out.println("Kakafee number not exist");
                } else {
                    User targetOperationUser = Global.registeredUsers.get(cardNumber);
                    targetOperationUser.printInfo();
                }
            } else {
                System.out.println("Permission denied");
            }
        }
    }
}

class registerShop implements Command {


    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            System.out.println("Illegal argument count");
            return;
        }
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (!Global.getMode().equals("Merchant")) {
            System.out.println("Permission denied");
            return;
        }
        if (Global.getCurrentLoginUser().getNumOfMyShop() == 5) {
            System.out.println("Shop count reached limit");
            return;
        }
        if (!validateShopOrCommodityName(args[0])) {
            System.out.println("Illegal shop name");
            return;
        }
        if (Global.getCurrentLoginUser().getNameOfMyShop().contains(args[0])) {
            System.out.println("Shop name already exists");
            return;
        }
        Global.getCurrentLoginUser().createMyShop(args[0]);
        System.out.println("Register shop success (shopId: S-" + Global.getCurrentShopId() + ")");
    }
}

class putCommodity implements Command {
    private boolean validateShopIdExistence(String shopId) {
        if (Global.getCurrentLoginUser().getMyShop().containsKey(shopId)) {
            return Global.getCurrentLoginUser().getMyShop().get(shopId).getRegisterState().equals("open");
        }
        return false;
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 3 && args.length != 4) {
            System.out.println("Illegal argument count");
            return;
        }
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (!Global.getMode().equals("Merchant")) {
            System.out.println("Permission denied");
            return;
        }
        if (!validateShopId(args[0])) {
            System.out.println("Illegal shop id");
            return;
        }
        if (!validateShopIdExistence(args[0])) {
                System.out.println("Shop id not exists");
                return;
        }
//        if (Global.getCurrentLoginUser().getMyShop().containsKey(args[0])){
//            if (!Global.getCurrentLoginUser().getMyShop().get(args[0]).getRegisterState().equals("open")) {
//                System.out.println("Shop id not exists");
//                return;
//            }
//        } else {
//            System.out.println("Shop id not exists");
//            return;
//        }
        if (args.length == 3) {
            String shopId = args[0];
            String commodityId = args[1];
            if (!validateCommodityId(commodityId)) {
                System.out.println("Illegal commodity id");
                return;
            }
            /**
             * 获得当前商家所持有的商品
             */

            if (Global.getCurrentLoginUser().getOwnedCommodityUnit().containsKey(commodityId)) {
                if (Global.getCurrentLoginUser().getOwnedCommodityUnit().get(commodityId).getAvailability() == false) {
                    System.out.println("Commodity id not exists");
                    return;
                }
            } else {
                System.out.println("Commodity id not exists");
                return;
            }
            if (!validateCount(args[2])) {
                System.out.println("Illegal commodity quantity");
                return;
            }
            Shop operatedShop = Global.getCurrentLoginUser().getMyShop().get(shopId);
            if (operatedShop.getCommodityOfShop().containsKey(commodityId) == false) {
                int count = Integer.parseInt(args[2]);
                Commodity freshCommodity = new Commodity(Global.getCommodityUnits().get(commodityId), count);
                operatedShop.getCommodityOfShop().put(commodityId, freshCommodity);
                System.out.println("Put commodity success (commodityId: " + commodityId + ")");
            } else {
                int count = Integer.parseInt(args[2]);
                HashMap<String, Commodity> currentCommodity = operatedShop.getCommodityOfShop();
                currentCommodity.get(commodityId).increaseCount(count);
                System.out.println("Put commodity success (commodityId: " + commodityId + ")");
            }
        }

        if (args.length == 4) {
            String shopId = args[0];
            String commodityName = args[1];
            if (!validateShopOrCommodityName(commodityName)) {
                System.out.println("Illegal commodity name");
                return;
            }
            if (!validatePrice(args[2])) {
                System.out.println("Illegal commodity price");
                return;
            }
            if (!validateCount(args[3])) {
                System.out.println("Illegal commodity quantity");
                return;
            }
            double price = Double.parseDouble(args[2]);
            int count = Integer.parseInt(args[3]);
            HashMap<String, Shop> myShop = Global.getCurrentLoginUser().getMyShop();
            CommodityUnit freshCommodityUnit = Global.createCommodity(commodityName, price, Global.getCurrentLoginUser());
            String commodityId = "C-" + Global.getCurrentCommodityId();
            Global.getCurrentLoginUser().createOwnedCommodities("C-" + Global.getCurrentCommodityId());
            //仅向指定商店的commodityOfShop属性中添加该商品，并用if语句更新商品数量，其中shopId为唯一标识码
            Shop operatedShop = myShop.get(shopId);
            Commodity freshCommodity = new Commodity(freshCommodityUnit, count);
            operatedShop.getCommodityOfShop().put(commodityId, freshCommodity);
            System.out.println("Put commodity success (commodityId: C-" + Global.getCurrentCommodityId() +")");
        }
    }
}

class listShop implements Command {
    private boolean validateShopExistence() {
        if (Global.getMode().equals("Customer") || Global.getMode().equals("Administrator")) {
            if (Global.getGlobalShops() == null) return false;
            for (Shop shop: Global.getGlobalShops().values()) {
                if (shop.getRegisterState().equals("open"))
                    return true;
            }
            return false;
        } else if (Global.getMode().equals("Merchant")) {
            if (Global.getCurrentLoginUser().getMyShop() == null) return false;
            for (Shop shop: Global.getCurrentLoginUser().getMyShop().values()) {
                if (shop.getRegisterState().equals("open"))
                    return true;
            }
            return false;
        }
        return false;
    }
    private boolean validateShopExistence(User queryUser) {
        if (queryUser.getIdentityType().equals("Merchant")) {
            if (queryUser.getMyShop() == null) return false;
            for (Shop shop: queryUser.getMyShop().values()) {
                if (shop.getRegisterState().equals("open"))
                    return true;
            }
            return false;
        }
        return false;
    }
    private void printShopList(HashMap<String, Shop> shops) {
        List<Map.Entry<String, Shop>> keys = new ArrayList<>(shops.entrySet());
        keys.sort(new Comparator<Map.Entry<String, Shop>>() {
            @Override
            public int compare(Map.Entry<String, Shop> o1,
                               Map.Entry<String, Shop> o2) {
                // 顺序排序，如果想要逆序，o2 - o1 即可
                return Global.getNumberId(o1.getKey()) -
                        Global.getNumberId(o2.getKey());
            }
        });
        for (Map.Entry<String, Shop> entry : keys) {
            String key = entry.getKey();
            if (Objects.equals(Global.getGlobalShops().get(key).getRegisterState(), "open")) {
                if (Global.getMode().equals("Administrator")) {
                    System.out.print(Global.getGlobalShops().get(key).getAffilMerchant().getCardNumber() + " ");
                }
                System.out.println(key + " " + Global.getGlobalShops().get(key).getName());
            }
        }
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.out.println("Illegal argument count");
            return;
        }
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (args.length == 0) {
            if (!validateShopExistence()) {
                System.out.println("Shop not exists");
                return;
            }
            if (Global.getMode().equals("Customer") || Global.getMode().equals("Administrator")) {
                printShopList(Global.getGlobalShops());
//                List<String> keys = new ArrayList<>(Global.getGlobalShops().keySet());
//                //Collections.sort(keys);
//                if (Global.getMode().equals("Customer")) {
//                    for (String key : keys) {
//                        if (Objects.equals(Global.getGlobalShops().get(key).getRegisterState(), "open")) {
//                            System.out.println(key + " " + Global.getGlobalShops().get(key).getName());
//                        }
//                    }
//                }
//                else if (Global.getMode().equals("Administrator")){
//                    for (String key : keys) {
//                        if (Objects.equals(Global.getGlobalShops().get(key).getRegisterState(), "open")) {
//                            System.out.println(Global.getGlobalShops().get(key).getAffilMerchant().getCardNumber() + " " + key + " " + Global.getGlobalShops().get(key).getName());
//                        }
//                    }
//                }
            }
            else if (Global.getMode().equals("Merchant")) {
                printShopList(Global.getCurrentLoginUser().getMyShop());
//                List<String> keys = new ArrayList<>(Global.getCurrentLoginUser().getMyShop().keySet());
//                //Collections.sort(keys);
//                for (String key: keys) {
//                    if (Objects.equals(Global.getCurrentLoginUser().getMyShop().get(key).getRegisterState(), "open")) {
//                        System.out.println(key + " " + Global.getCurrentLoginUser().getMyShop().get(key).getName());
//                    }
//                }
            }
        }
        else if (args.length == 1) {
            if (!Global.getMode().equals("Administrator")) {
                System.out.println("Permission denied");
                return;
            }
            if (!validateCardNumber(args[0])) {
                System.out.println("Illegal Kakafee number");
                return;
            }
            if (!isCardNumberRegistered(args[0])) {
                System.out.println("Kakafee number not exists");
                return;
            }
            User queryUser = Global.registeredUsers.get(args[0]);
            if (!queryUser.getIdentityType().equals("Merchant")) {
                System.out.println("Kakafee number does not belong to a Merchant");
                return;
            }
            if (!validateShopExistence(queryUser)) {
                System.out.println("Shop not exists");
                return;
            }
            printShopList(queryUser.getMyShop());
//            List<String> keys = new ArrayList<>(queryUser.getMyShop().keySet());
//            //Collections.sort(keys);
//            for (String key: keys) {
//                if (Objects.equals(queryUser.getMyShop().get(key).getRegisterState(), "open")) {
//                    System.out.println(args[0] + " " + key + " " + queryUser.getMyShop().get(key).getName());
//                }
//            }
        }
    }
}

class listCommodity implements Command {
    private boolean validateCommodityExistence() {
        if (Global.getMode().equals("Customer") || Global.getMode().equals("Administrator")) {
            if (Global.getCommodityUnits() == null) return false;
            for (CommodityUnit unit: Global.getCommodityUnits().values()) {
                if (unit.getAvailability())
                    return true;
            }
            return false;
        } else if (Global.getMode().equals("Merchant")) {
            if (Global.getCurrentLoginUser().getOwnedCommodityUnit() == null) return false;
            for (CommodityUnit unit: Global.getCurrentLoginUser().getOwnedCommodityUnit().values()) {
                if (unit.getAvailability())
                    return true;
            }
            return false;
        }
        return false;
    }
    private boolean validateCommodityExistence(String shopId) {
        Shop queryShop = Global.getGlobalShops().get(shopId);
        if (queryShop.getCommodityOfShop() == null) return false;
        for (Commodity commodity: queryShop.getCommodityOfShop().values()) {
            if (commodity.getCommodityUnit().getAvailability())
                return true;
        }
        return false;
    }
    private boolean validateShopExistence(String shopId) {
        if (Global.getMode().equals("Customer") || Global.getMode().equals("Administrator")) {
            if (Global.getGlobalShops().containsKey(shopId)) {
                if (Global.getGlobalShops().get(shopId).getRegisterState().equals("open")) {
                    return true;
                }
            }
        }
        else {
            if(Global.getCurrentLoginUser().getMyShop().containsKey(shopId)) {
                if (Global.getCurrentLoginUser().getMyShop().get(shopId).getRegisterState().equals("open")) {
                    return true;
                }
            }
        }
        return false;
    }
    private void printCommodityInfo(HashMap<String, Shop> shops) {
        boolean isAllCountZero = true;
        List<Map.Entry<String, Shop>> shopKeys = new ArrayList<>(shops.entrySet());
        shopKeys.sort(new Comparator<Map.Entry<String, Shop>>() {
            @Override
            public int compare(Map.Entry<String, Shop> o1,
                               Map.Entry<String, Shop> o2) {
                // 顺序排序，如果想要逆序，o2 - o1 即可
                return Global.getNumberId(o1.getKey()) -
                        Global.getNumberId(o2.getKey());
            }
        });
        for (Map.Entry<String, Shop> shopEntry : shopKeys) {
            String shopId = shopEntry.getKey();
            List<Map.Entry<String, Commodity>> commodityKeys
                    = new ArrayList<>(shops.get(shopId).getCommodityOfShop().entrySet());

            Collections.sort(commodityKeys, new Comparator<Map.Entry<String, Commodity>>() {
                @Override
                public int compare(Map.Entry<String, Commodity> o1,
                                   Map.Entry<String, Commodity> o2)
                {
                    // 顺序排序，如果想要逆序，o2 - o1 即可
                    return Global.getNumberId(o1.getKey()) -
                            Global.getNumberId(o2.getKey());
                }
            });
            for (Map.Entry<String, Commodity> commodityEntry : commodityKeys) {
                String commodityId = commodityEntry.getKey();
                Commodity commodity =shops.get((shopId)).getCommodityOfShop().get(commodityId);
                String formattedPrice = String.format("%.2f", commodity.getCommodityUnit().getPrice());
                if (commodity.getCommodityUnit().getAvailability()) {
                    System.out.println(shopId
                            +": "+commodityId
                            +" "+commodity.getCommodityUnit().getName()
                            +" "+formattedPrice+"yuan"
                            +" "+commodity.getCount());
                    isAllCountZero = false;
                }
            }
        }
        if (isAllCountZero)
            System.out.println("Commodity not exists");
    }
    private void printCommodityInfo(HashMap<String, Shop> shops, String shopId) {
        boolean isAllCountZero = true;
        List<Map.Entry<String, Commodity>> commodityKeys
                = new ArrayList<>(shops.get(shopId).getCommodityOfShop().entrySet());

        Collections.sort(commodityKeys, new Comparator<Map.Entry<String, Commodity>>() {
            @Override
            public int compare(Map.Entry<String, Commodity> o1,
                               Map.Entry<String, Commodity> o2)
            {
                // 顺序排序，如果想要逆序，o2 - o1 即可
                return Global.getNumberId(o1.getKey()) -
                        Global.getNumberId(o2.getKey());
            }
        });
        for (Map.Entry<String, Commodity> commodityEntry : commodityKeys) {
            String commodityId = commodityEntry.getKey();
            Commodity commodity =shops.get((shopId)).getCommodityOfShop().get(commodityId);
            String formattedPrice = String.format("%.2f", commodity.getCommodityUnit().getPrice());
            if (commodity.getCommodityUnit().getAvailability()) {
                System.out.println(shopId
                        +": "+commodityId
                        +" "+commodity.getCommodityUnit().getName()
                        +" "+formattedPrice+"yuan"
                        +" "+commodity.getCount());
                isAllCountZero = false;
            }
        }
        if (isAllCountZero)
            System.out.println("Commodity not exists");
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 0 && args.length != 1) {
            System.out.println("Illegal argument count");
            return;
        }
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (args.length == 0) {
            if (!validateCommodityExistence()) {
                System.out.println("Commodity not exists");
                return;
            }
            if (Global.getMode().equals("Customer") || Global.getMode().equals("Administrator")) {
                printCommodityInfo(Global.getGlobalShops());
            }
            else if (Global.getMode().equals("Merchant")) {
                printCommodityInfo(Global.getCurrentLoginUser().getMyShop());
            }
        }
        else if (args.length == 1) {
            if (!validateShopId(args[0])) {
                System.out.println("Illegal shop id");
                return;
            }
            if (!validateShopExistence(args[0])) {
                System.out.println("Shop id not exists");
                return;
            }
            if (!validateCommodityExistence(args[0])) {
                System.out.println("Commodity not exists");
                return;
            }
            if (Global.getMode().equals("Customer") || Global.getMode().equals("Administrator")) {
                printCommodityInfo(Global.getGlobalShops(), args[0]);
            }
            else if (Global.getMode().equals("Merchant")) {
                printCommodityInfo(Global.getCurrentLoginUser().getMyShop(), args[0]);
            }
        }
    }
}

class searchCommodity implements Command {

    private boolean validateDirectedCommodityExistence(String name) {
        if (Global.getMode().equals("Administrator") || Global.getMode().equals("Customer")) {
            for (Shop shop: Global.getGlobalShops().values())
                for (Commodity commodity: shop.getCommodityOfShop().values()) {
                    if (commodity.getCommodityUnit().getName().equals(name) && commodity.getCommodityUnit().getAvailability() && commodity.getCount() != 0)
                        return true;
                }
            return false;
        } else if (Global.getMode().equals("Merchant")) {
            for (Shop shop: Global.getCurrentLoginUser().getMyShop().values())
                for (Commodity commodity: shop.getCommodityOfShop().values()) {
                    if (commodity.getCommodityUnit().getName().equals(name) && commodity.getCommodityUnit().getAvailability() && commodity.getCount() != 0)
                        return true;
                }
            return false;
        }
        return false;
    }
    private void printSearchResult(String name) {
        HashMap<String, Shop> shops = null;
        if (Global.getMode().equals("Administrator") || Global.getMode().equals("Customer")) {
            shops = Global.getGlobalShops();
        } else if (Global.getMode().equals("Merchant")) {
            shops = Global.getCurrentLoginUser().getMyShop();
//            for (Shop shop: Global.getCurrentLoginUser().getMyShop().values())
//                for (Commodity commodity: shop.getCommodityOfShop().values()) {
//                    String formattedPrice = String.format("%.2f", commodity.getCommodityUnit().getPrice());
//                    if (commodity.getCommodityUnit().getName().equals(name) && commodity.getCount() != 0) {
//                        System.out.println(shop.getId()
//                                + ": " + commodity.getCommodityUnit().getId()
//                                + " " + name
//                                + " " + formattedPrice + "yuan"
//                                + " " + commodity.getCount());
//                    }
//                }
        }
        assert shops != null;
        List<Map.Entry<String, Shop>> shopKeys = new ArrayList<>(shops.entrySet());
        shopKeys.sort(new Comparator<Map.Entry<String, Shop>>() {
            @Override
            public int compare(Map.Entry<String, Shop> o1,
                               Map.Entry<String, Shop> o2) {
                // 顺序排序，如果想要逆序，o2 - o1 即可
                return Global.getNumberId(o1.getKey()) -
                        Global.getNumberId(o2.getKey());
            }
        });
        for (Map.Entry<String, Shop> shopEntry : shopKeys) {
            String shopId = shopEntry.getKey();
            List<Map.Entry<String, Commodity>> commodityKeys
                    = new ArrayList<>(shops.get(shopId).getCommodityOfShop().entrySet());

            Collections.sort(commodityKeys, new Comparator<Map.Entry<String, Commodity>>() {
                @Override
                public int compare(Map.Entry<String, Commodity> o1,
                                   Map.Entry<String, Commodity> o2)
                {
                    // 顺序排序，如果想要逆序，o2 - o1 即可
                    return Global.getNumberId(o1.getKey()) -
                            Global.getNumberId(o2.getKey());
                }
            });
            for (Map.Entry<String, Commodity> commodityEntry : commodityKeys) {
                String commodityId = commodityEntry.getKey();
                Commodity commodity = shops.get(shopId).getCommodityOfShop().get(commodityId);
                String formattedPrice = String.format("%.2f", commodity.getCommodityUnit().getPrice());
                if (commodity.getCommodityUnit().getName().equals(name) && commodity.getCount() != 0) {
                    System.out.println(shops.get(shopId).getId()
                            + ": " + commodity.getCommodityUnit().getId()
                            + " " + name
                            + " " + formattedPrice + "yuan"
                            + " " + commodity.getCount());
                }
            }
        }
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            System.out.println("Illegal argument count");
            return;
        }
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (!validateShopOrCommodityName(args[0])) {
            System.out.println("Illegal commodity name");
            return;
        }
        if (!validateDirectedCommodityExistence(args[0])) {
            System.out.println("Commodity not exists");
            return;
        }
        printSearchResult(args[0]);
    }
}

class buyCommodity implements Command {
    private boolean validateDirectedCommodityExistence(String shopId, String commodityId) {
        Shop shop = Global.getGlobalShops().get(shopId);
        if (shop.getCommodityOfShop().containsKey(commodityId)) {
            return shop.getCommodityOfShop().get(commodityId).getCommodityUnit().getAvailability();
        } else {
            return false;
        }
    }
    private boolean validateCount(String shopId, String commodityId, int count) {
        if (count <= 0) return false;
        Shop shop = Global.getGlobalShops().get(shopId);
        Commodity commodity = shop.getCommodityOfShop().get(commodityId);
        return count <= commodity.getCount();
    }
    private void buyCommodity(String shopId, String commodityId, int count) {
        Shop shop = Global.getGlobalShops().get(shopId);
        Commodity commodity = shop.getCommodityOfShop().get(commodityId);
        commodity.decreaseCount(count);
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 3) {
            System.out.println("Illegal argument count");
            return;
        }
        String shopId = args[0];
        String commodityId = args[1];
        int count = Integer.parseInt(args[2]);
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (!Global.getMode().equals("Customer")) {
            System.out.println("Permission denied");
            return;
        }
        if (!validateShopId(shopId)) {
            System.out.println("Illegal shop id");
            return;
        }
        if (!validateGlobalShopIdExistence(shopId)) {
            System.out.println("Shop id not exists");
            return;
        }
        if (!validateCommodityId(commodityId)) {
            System.out.println("Illegal commodity id");
            return;
        }
        if (!validateDirectedCommodityExistence(shopId, commodityId)) {
            System.out.println("Commodity id not exists");
            return;
        }
        if (!validateCount(shopId, commodityId, count)) {
            System.out.println("Illegal buy quantity");
            return;
        }
        buyCommodity(shopId, commodityId, count);
        System.out.println("Buy commodity success");
    }
}

class removeCommodity implements Command {
    private boolean validateShopIdExistence(String shopId) {
        if (Global.getMode().equals("Administrator")) {
            if (Global.getGlobalShops().containsKey(shopId)) {
                return Global.getGlobalShops().get(shopId).getRegisterState().equals("open");
            }
        } else if (Global.getMode().equals("Merchant")) {
            if (Global.getCurrentLoginUser().getMyShop().containsKey(shopId)) {
                return Global.getCurrentLoginUser().getMyShop().get(shopId).getRegisterState().equals("open");
            }
        }
        return false;
    }
    private boolean validateCommodityExistence(String commodityId) {
        if (Global.getMode().equals("Administrator")) {
            if (Global.getCommodityUnits().containsKey(commodityId)) {
                return Global.getCommodityUnits().get(commodityId).getAvailability();
            }
        } else if (Global.getMode().equals("Merchant")) {
            if (Global.getCurrentLoginUser().getOwnedCommodityUnit().containsKey(commodityId)) {
                return Global.getCurrentLoginUser().getOwnedCommodityUnit().get(commodityId).getAvailability();
            }
        }
        return false;
    }
    private boolean validateCommodityExistence(String commodityId, String shopId) {
        if (Global.getMode().equals("Administrator")) {
            Shop shop = Global.getGlobalShops().get(shopId);
            if (shop.getCommodityOfShop().containsKey(commodityId)) {
                return shop.getCommodityOfShop().get(commodityId).getCommodityUnit().getAvailability();
            }
        } else if (Global.getMode().equals("Merchant")) {
            Shop shop = Global.getCurrentLoginUser().getMyShop().get(shopId);
            if (shop.getCommodityOfShop().containsKey(commodityId)) {
                return shop.getCommodityOfShop().get(commodityId).getCommodityUnit().getAvailability();
            }
        }
        return false;
    }
    private void discountCommodity(String commodityId) {
        CommodityUnit commodityUnit;
        if (Global.getMode().equals("Administrator")) {
            commodityUnit = Global.getCommodityUnits().get(commodityId);

        } else if (Global.getMode().equals("Merchant")) {
            commodityUnit = Global.getCurrentLoginUser().getOwnedCommodityUnit().get(commodityId);
        } else {
            System.out.println("Error!! Bugs!!");
            return;
        }
        commodityUnit.getAffilMerchant().getMyShop().forEach((shopId, shop) -> {
            // 修改值
            if (shop.getCommodityOfShop().containsKey(commodityId)) {
                Commodity commodity = shop.getCommodityOfShop().get(commodityId);
                // 清除库存
                commodity.decreaseCount(commodity.getCount());
            }
        });
        // 注销商品，将availability设定为false
        commodityUnit.terminate();
    }
    private void discountCommodity(String shopId, String commodityId) {
        Shop shop = Global.getGlobalShops().get(shopId);
        shop.offShelfCommodity(commodityId);
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 1 && args.length != 2) {
            System.out.println("Illegal argument count");
            return;
        }
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (!Global.getMode().equals("Merchant") && !Global.getMode().equals("Administrator")) {
            System.out.println("Permission denied");
            return;
        }
        String commodityId = args[0];
        if (!validateCommodityId(commodityId)) {
            System.out.println("Illegal commodity id");
            return;
        }
        if (args.length == 1) {
            if (!validateCommodityExistence(commodityId)) {
                System.out.println("Commodity id not exists");
                return;
            }
            discountCommodity(commodityId);
            System.out.println("Remove commodity success");
        } else if (args.length == 2) {
            String shopId = args[1];
            if (!validateShopId(shopId)) {
                System.out.println("Illegal shop id");
                return;
            }
            if (!validateShopIdExistence(shopId)) {
                System.out.println("Shop id not exists");
                return;
            }
            if (!validateCommodityExistence(commodityId, shopId)) {
                System.out.println("Commodity id not exists");
                return;
            }
            discountCommodity(shopId, commodityId);
            System.out.println("Remove commodity success");
        }
    }
}

class cancelShop implements Command {
    private boolean validateShopIdExistence(String shopId) {
        if (Global.getMode().equals("Administrator")) {
            if (Global.getGlobalShops().containsKey(shopId)) {
                return Global.getGlobalShops().get(shopId).getRegisterState().equals("open");
            }
        } else if (Global.getMode().equals("Merchant")) {
            if (Global.getCurrentLoginUser().getMyShop().containsKey(shopId)) {
                return Global.getCurrentLoginUser().getMyShop().get(shopId).getRegisterState().equals("open");
            }
        }
        return false;
    }
    @Override
    public void execute(String[] args) {
        if (args.length != 1) {
            System.out.println("Illegal argument count");
            return;
        }
        String shopId = args[0];
        if (!Global.getLoggingState()) {
            System.out.println("Please log in first");
            return;
        }
        if (!Global.getMode().equals("Merchant") && !Global.getMode().equals("Administrator")) {
            System.out.println("Permission denied");
            return;
        }
        if (!validateShopId(shopId)) {
            System.out.println("Illegal shop id");
            return;
        }
        if (!validateShopIdExistence(shopId)) {
            System.out.println("Shop id not exists");
            return;
        }
        Shop shop = Global.getGlobalShops().get(shopId);
        shop.closeShop();
        System.out.println("Cancel shop success");
    }
}