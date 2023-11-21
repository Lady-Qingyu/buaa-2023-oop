enum OrderState {
    pending,
    canceled,
    finished
}
public class Order {
    private String id;
    private OrderState state;
    private User affilCustomer;
    private Shop affilShop;
    private Commodity affilCommodity;
    private int buyCount;
    private double totalCost;
    public Order(User customer, Shop shop, Commodity commodity, int count) {
        this.id = "O-" + Global.getCurrentOrderId();
        this.state = OrderState.pending;
        affilCustomer = customer;
        affilShop = shop;
        affilCommodity = commodity;
        buyCount = count;
        totalCost = buyCount * commodity.getCommodityUnit().getPrice();
    }

    public String getId() {
        return id;
    }

    public OrderState getState() {
        return state;
    }

    public User getAffilCustomer() { return affilCustomer; }
    public Shop getAffilShop() { return affilShop; }
    public int getBuyCount() { return buyCount;}
    public double getTotalCost() { return totalCost;}
    public Commodity getAffilCommodity() { return affilCommodity; }

    public void cancelOrder() {
        if (state == OrderState.pending) {
            affilCommodity.increaseCount(buyCount);
            state = OrderState.canceled;
        }
    }

    public void finishOrder() {
        if (state == OrderState.pending) {
            state = OrderState.finished;
        }
    }

    public void completeOrder() {
        if (state == OrderState.pending) {
            state = OrderState.finished;
            System.out.println("Order " + id + " has been completed.");
        } else {
            System.out.println("Cannot complete the order. Invalid status.");
        }
    }
}
