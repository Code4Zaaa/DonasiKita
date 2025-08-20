package com.vriza.donasikita.models;

import com.google.gson.annotations.SerializedName;

public class Donation {
    @SerializedName("reference")
    private String reference;

    @SerializedName("merchant_ref")
    private String merchant_ref;

    @SerializedName("payment_selection_type")
    private String payment_selection_type;

    @SerializedName("payment_method")
    private String payment_method;

    @SerializedName("payment_name")
    private String payment_name;

    @SerializedName("customer_name")
    private String customer_name;

    @SerializedName("customer_email")
    private String customer_email;

    @SerializedName("customer_phone")
    private String customer_phone;

    @SerializedName("callback_url")
    private String callback_url;

    @SerializedName("return_url")
    private String return_url;

    @SerializedName("amount")
    private int amount;

    @SerializedName("fee_merchant")
    private int fee_merchant;

    @SerializedName("fee_customer")
    private int fee_customer;

    @SerializedName("total_fee")
    private int total_fee;

    @SerializedName("amount_received")
    private int amount_received;

    @SerializedName("pay_code")
    private String pay_code;

    @SerializedName("pay_url")
    private String pay_url;

    @SerializedName("checkout_url")
    private String checkout_url;

    @SerializedName("status")
    private String status;

    @SerializedName("expired_time")
    private long expired_time;

    @SerializedName("order_items")
    private OrderItem[] order_items;

    @SerializedName("instructions")
    private Instruction[] instructions;

    @SerializedName("qr_string")
    private String qr_string;

    @SerializedName("qr_url")
    private String qr_url;

    // Constructors
    public Donation() {}

    public Donation(String reference, String merchant_ref, String payment_selection_type,
                    String payment_method, String payment_name, String customer_name,
                    String customer_email, String customer_phone, String callback_url,
                    String return_url, int amount, int fee_merchant, int fee_customer,
                    int total_fee, int amount_received, String pay_code, String pay_url,
                    String checkout_url, String status, long expired_time,
                    OrderItem[] order_items, Instruction[] instructions,
                    String qr_string, String qr_url) {
        this.reference = reference;
        this.merchant_ref = merchant_ref;
        this.payment_selection_type = payment_selection_type;
        this.payment_method = payment_method;
        this.payment_name = payment_name;
        this.customer_name = customer_name;
        this.customer_email = customer_email;
        this.customer_phone = customer_phone;
        this.callback_url = callback_url;
        this.return_url = return_url;
        this.amount = amount;
        this.fee_merchant = fee_merchant;
        this.fee_customer = fee_customer;
        this.total_fee = total_fee;
        this.amount_received = amount_received;
        this.pay_code = pay_code;
        this.pay_url = pay_url;
        this.checkout_url = checkout_url;
        this.status = status;
        this.expired_time = expired_time;
        this.order_items = order_items;
        this.instructions = instructions;
        this.qr_string = qr_string;
        this.qr_url = qr_url;
    }

    // Getters and Setters
    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getMerchant_ref() { return merchant_ref; }
    public void setMerchant_ref(String merchant_ref) { this.merchant_ref = merchant_ref; }

    public String getPayment_selection_type() { return payment_selection_type; }
    public void setPayment_selection_type(String payment_selection_type) { this.payment_selection_type = payment_selection_type; }

    public String getPayment_method() { return payment_method; }
    public void setPayment_method(String payment_method) { this.payment_method = payment_method; }

    public String getPayment_name() { return payment_name; }
    public void setPayment_name(String payment_name) { this.payment_name = payment_name; }

    public String getCustomer_name() { return customer_name; }
    public void setCustomer_name(String customer_name) { this.customer_name = customer_name; }

    public String getCustomer_email() { return customer_email; }
    public void setCustomer_email(String customer_email) { this.customer_email = customer_email; }

    public String getCustomer_phone() { return customer_phone; }
    public void setCustomer_phone(String customer_phone) { this.customer_phone = customer_phone; }

    public String getCallback_url() { return callback_url; }
    public void setCallback_url(String callback_url) { this.callback_url = callback_url; }

    public String getReturn_url() { return return_url; }
    public void setReturn_url(String return_url) { this.return_url = return_url; }

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

    public int getFee_merchant() { return fee_merchant; }
    public void setFee_merchant(int fee_merchant) { this.fee_merchant = fee_merchant; }

    public int getFee_customer() { return fee_customer; }
    public void setFee_customer(int fee_customer) { this.fee_customer = fee_customer; }

    public int getTotal_fee() { return total_fee; }
    public void setTotal_fee(int total_fee) { this.total_fee = total_fee; }

    public int getAmount_received() { return amount_received; }
    public void setAmount_received(int amount_received) { this.amount_received = amount_received; }

    public String getPay_code() { return pay_code; }
    public void setPay_code(String pay_code) { this.pay_code = pay_code; }

    public String getPay_url() { return pay_url; }
    public void setPay_url(String pay_url) { this.pay_url = pay_url; }

    public String getCheckout_url() { return checkout_url; }
    public void setCheckout_url(String checkout_url) { this.checkout_url = checkout_url; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getExpired_time() { return expired_time; }
    public void setExpired_time(long expired_time) { this.expired_time = expired_time; }

    public OrderItem[] getOrder_items() { return order_items; }
    public void setOrder_items(OrderItem[] order_items) { this.order_items = order_items; }

    public Instruction[] getInstructions() { return instructions; }
    public void setInstructions(Instruction[] instructions) { this.instructions = instructions; }

    public String getQr_string() { return qr_string; }
    public void setQr_string(String qr_string) { this.qr_string = qr_string; }

    public String getQr_url() { return qr_url; }
    public void setQr_url(String qr_url) { this.qr_url = qr_url; }
}