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
    default boolean validateName(String name) {
        String regex = "[A-Za-z][A-Za-z_]{3,15}";
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
        } else if (Global.getStatus()) {
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
        } else if (!validateName(name)) {
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
        } else if (Global.getStatus()) {
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
                Global.setIsLoggedTrue(user);
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
        } else if (!Global.getStatus()) {
            System.out.println("Please log in first");
        } else {
            Global.setIsLoggedFalse();
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
        } else if (!Global.getStatus()) {
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