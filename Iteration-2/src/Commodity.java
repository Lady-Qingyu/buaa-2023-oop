public class Commodity {
    private CommodityUnit commodityUnit;
    private int count;
    public Commodity(CommodityUnit commodityUnit, int count) {
        this.count = count;
        this.commodityUnit = commodityUnit;
    }

    public CommodityUnit getCommodityUnit() {
        return commodityUnit;
    }

    public int getCount() {
        return count;
    }

    public void increaseCount(int count) {
        this.count += count;
    }

    public void decreaseCount(int count) { this.count -= count; }
}
