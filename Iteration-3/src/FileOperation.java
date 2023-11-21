import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileOperation {
    public static boolean isPathValid(String path) {
        // 定义合法路径的正则表达式
        String regex = "(\\./)?(((?![\\\\*\\\\?\\\\\"\\\\<\\\\>\\\\|\\\\/]).)*/)*((?![\\\\*\\\\?\\\\\"\\\\<\\\\>\\\\|\\\\/]).)+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }
    public static boolean validateFileExistence(String filePath) {
        // 创建File对象
        File file = new File(filePath);

        // 判断文件是否存在且为文件格式
        return file.exists() && file.isFile();
    }

    public static void open(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            // 逐行读取文件内容并输出到控制台
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("File operation failed");
        }
    }
    private static String getDirectoryFromPath(String filePath) {
        // 使用lastIndexOf()方法找到路径中的最后一个斜杠（或反斜杠）的位置
        int lastSlashIndex = filePath.lastIndexOf("/");
        int lastBackslashIndex = filePath.lastIndexOf("\\");

        // 取最后一个斜杠（或反斜杠）之前的部分作为目录
        if (lastSlashIndex > lastBackslashIndex) {
            return filePath.substring(0, lastSlashIndex);
        } else if (lastBackslashIndex > -1) {
            return filePath.substring(0, lastBackslashIndex);
        } else {
            // 如果没有斜杠（或反斜杠），则返回空字符串或其他默认值，视需求而定
            return "";
        }
    }
    public static void outputOrders(HashMap<String, Order> orders, String path, String redirectMark) {
        boolean append;
        String filePath;
        String directoryPath;
        if (redirectMark.equals(">>")) {
            append = true;
        } else {
            append = false;
        }
        filePath = path;
        directoryPath = getDirectoryFromPath(path);
        try {
            // 创建目录
            Path directory = Paths.get(directoryPath);
            Files.createDirectories(directory);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
                List<Map.Entry<String, Order>> keys = new ArrayList<>(orders.entrySet());
                keys.sort(new Comparator<Map.Entry<String, Order>>() {
                    @Override
                    public int compare(Map.Entry<String, Order> o1,
                                       Map.Entry<String, Order> o2) {
                        // 顺序排序，如果想要逆序，o2 - o1 即可
                        return Global.getNumberId(o1.getKey()) -
                                Global.getNumberId(o2.getKey());
                    }
                });
                for (Map.Entry<String, Order> entry : keys) {
                    String key = entry.getKey();
                    Order currentOrder = orders.get(key);
                    String formattedCost = String.format("%.2f", currentOrder.getTotalCost());
                    writer.write(currentOrder.getId() + ": " + currentOrder.getAffilShop().getId() +
                            " " + currentOrder.getAffilCommodity().getCommodityUnit().getId() +
                            " " + currentOrder.getBuyCount()+
                            " " + formattedCost + "yuan" +
                            " " + currentOrder.getState());
                    writer.newLine();
                }

            }
        } catch (FileAlreadyExistsException e) {
            System.out.println("File operation failed 文件已存在，无法创建目录:");
            //System.err.println("文件已存在，无法创建目录: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("File operation failed 导出订单时出现错误:");
            //System.err.println("导出订单时出现错误: " + e.getMessage());
        }
    }
    public static void outputErrors(String error, String path, String redirectMark) {
        boolean append;
        if (redirectMark.equals(">>")) {
            append = true;
        } else {
            append = false;
        }
        String filePath;
        String directoryPath;
        filePath = path;
        directoryPath = getDirectoryFromPath(path);
        try {
            // 创建目录
            Path directory = Paths.get(directoryPath);
            Files.createDirectories(directory);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
                writer.write(error);
                writer.newLine();
            }
        } catch (FileAlreadyExistsException e) {
            System.out.println("File operation failed");
        } catch (IOException e) {
            System.out.println("File operation failed");
        }
    }

    public static void deleteDirectory(File file){
        File[] list = file.listFiles();
        Integer i = 0;
        for (File f:list){
            if (f.isDirectory()){
                //删除子文件夹
                deleteDirectory(new File(f.getPath()));
            }else{
                //删除文件
                f.delete();
                i ++;
            }
        }
        //重新遍历一下文件夹内文件是否已删除干净，删除干净后则删除文件夹。
        if (file.listFiles().length <=0 ){
            file.delete();
            return;
        }
    };

    public static boolean arePathsEqual(String path1String, String path2String) {
        Path path1 = Paths.get(path1String).normalize(); // 对路径进行标准化处理
        Path path2 = Paths.get(path2String).normalize(); // 对路径进行标准化处理

        // 使用equals方法比较两个路径是否相同
        return path1.equals(path2);
    }
}
