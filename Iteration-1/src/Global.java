import java.util.HashMap;
import java.util.Map;

public class Global {
    private static boolean isLogged = false;
    public static Map<String, User> registeredUsers = new HashMap<>();

    private static String mode = null;
    private static String currentLoggedCardNumber = null;


    public static void setIsLoggedTrue(User user) {
        isLogged = true;
        mode = user.getIdentityType();
        currentLoggedCardNumber = user.getCardNumber();
    }

    public static void setIsLoggedFalse() {
        isLogged = false;
        mode = null;
        currentLoggedCardNumber = null;
    }
    public static String getMode() {
        return mode;
    }
    public static String getCurrentLoggedCardNumber() {
        return currentLoggedCardNumber;
    }
    public static boolean getStatus() {
        return isLogged;
    }



    public static User getCurrentLoginUser() {
        if (currentLoggedCardNumber == null) {
            System.out.println("没有用户登录");
        }
        return registeredUsers.get(currentLoggedCardNumber);
    }
}
