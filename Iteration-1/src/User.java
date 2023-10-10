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
}

class Administrator extends User {

    public Administrator(String cardNumber, String name, String password, String identityType) {
        super(cardNumber, name, password, identityType);
    }
}

class Merchant extends User {

    public Merchant(String cardNumber, String name, String password, String identityType) {
        super(cardNumber, name, password, identityType);
    }
}

class Customer extends User {

    public Customer(String cardNumber, String name, String password, String identityType) {
        super(cardNumber, name, password, identityType);
    }
}


