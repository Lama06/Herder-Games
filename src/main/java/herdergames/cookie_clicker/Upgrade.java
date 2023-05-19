package herdergames.cookie_clicker;

final class Upgrade {
    final String name;
    float price;
    int amount;
    final int cookiesPerSecond;
    final float priceIncrease = 1.15f;

    Upgrade(String name, int price, int cookiesPerSecond) {
        this.name = name;
        this.price = price;
        this.cookiesPerSecond = cookiesPerSecond;
    }

    String getDescription() {
        return name + " kostet " + (int) price + " Kekse und gibt " + cookiesPerSecond + " Kekse pro Sekunde und du hast " + amount + " davon";
    }
}
