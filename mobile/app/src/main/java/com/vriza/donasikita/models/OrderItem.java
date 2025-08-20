package com.vriza.donasikita.models;

import com.google.gson.annotations.SerializedName;

public class OrderItem {
    @SerializedName("sku")
    private String sku;

    @SerializedName("name")
    private String name;

    @SerializedName("price")
    private int price;

    @SerializedName("quantity")
    private int quantity;

    @SerializedName("subtotal")
    private int subtotal;

    @SerializedName("product_url")
    private String product_url;

    @SerializedName("image_url")
    private String image_url;

    // Constructors
    public OrderItem() {}

    public OrderItem(String sku, String name, int price, int quantity,
                     int subtotal, String product_url, String image_url) {
        this.sku = sku;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.product_url = product_url;
        this.image_url = image_url;
    }

    // Getters and Setters
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getSubtotal() { return subtotal; }
    public void setSubtotal(int subtotal) { this.subtotal = subtotal; }

    public String getProduct_url() { return product_url; }
    public void setProduct_url(String product_url) { this.product_url = product_url; }

    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }
}