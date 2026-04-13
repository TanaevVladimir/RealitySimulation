package com;

public class Item {
    private String name;
    private int count;
    private double price;
    Item (String name, int count)
    {
        this.name = name;
        this.count = count;
    }
    public void add (int delta) throws Exception {
        if (this.count+delta<0)
            throw new Exception("Not enough items to sell");
        this.count+=delta;
    }

    public String getName () {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getCount() {
        return count;
    }
}
