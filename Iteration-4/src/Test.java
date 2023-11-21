import java.util.*;

public class Test {
    public static void main(String[] args) {
        try {
            Scanner scanner = new Scanner(System.in);
            Map<String, Command> commands = new HashMap<>();
            commands.put("quit", new Quit());
            commands.put("register", new Register());
            commands.put("login", new Login());
            commands.put("logout", new Logout());
            commands.put("printInfo", new PrintInfo());
            commands.put("registerShop", new registerShop());
            commands.put("putCommodity", new putCommodity());
            commands.put("listShop", new listShop());
            commands.put("listCommodity", new listCommodity());
            commands.put("searchCommodity", new searchCommodity());
            commands.put("buyCommodity", new buyCommodity());
            commands.put("removeCommodity", new removeCommodity());
            commands.put("cancelShop", new cancelShop());
            commands.put("putCommodityBatch", new putCommodityBatch());
            commands.put("cancelOrder", new cancelOrder());
            commands.put("finishOrder", new finishOrder());
            commands.put("listOrder", new listOrder());
            commands.put("exportMerchantOrder", new exportMerchantOrder());
            commands.put("openFile", new openFile());
            commands.put("favoriteCommodity", new favoriteCommodity());
            commands.put("cancelFavoriteCommodity", new cancelFavoriteCommodity());
            commands.put("listFavoriteCommodity", new listFavoriteCommodity());
            commands.put("uploadFavoriteCommodity", new uploadFavoriteCommodity());
            commands.put("readFavoriteCommodity", new readFavoriteCommodity());
            commands.put("buyFavoriteCommodity", new buyFavoriteCommodity());
            while (true) {
                String input = scanner.nextLine().trim(); // 去除输入前后的空格
                String[] parts = input.split("\\s+"); // 使用空格分割命令
                if (parts.length == 0) {
                    System.out.println("Command '' not found");
                }
                String commandName = parts[0];
                Command command = commands.get(commandName);
                if (command == null) {
                    System.out.println("Command '" + commandName + "' not found");
                } else {
                    String[] arguments = new String[parts.length - 1];
                    System.arraycopy(parts, 1, arguments, 0, arguments.length);
                    command.execute(arguments);
                }
            }
        } catch (Exception e) {
            System.out.println("评测机RE了！");
            e.printStackTrace();
        }
    }
}

