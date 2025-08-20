package com.vriza.donasikita.network.responses;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DonationResponse {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private DonationData data;

    @SerializedName("message")
    private String message;

    // Constructors
    public DonationResponse() {}

    // Getters and Setters
    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public DonationData getData() {
        return data;
    }

    public void setData(DonationData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public static class DonationData implements Parcelable {
        @SerializedName("reference")
        private String reference;

        @SerializedName("merchant_ref")
        private String merchantRef;

        @SerializedName("payment_selection_type")
        private String paymentSelectionType;

        @SerializedName("payment_method")
        private String paymentMethod;

        @SerializedName("payment_name")
        private String paymentName;

        @SerializedName("customer_name")
        private String customerName;

        @SerializedName("customer_email")
        private String customerEmail;

        @SerializedName("customer_phone")
        private String customerPhone;

        @SerializedName("callback_url")
        private String callbackUrl;

        @SerializedName("return_url")
        private String returnUrl;

        @SerializedName("amount")
        private int amount;

        @SerializedName("fee_merchant")
        private int feeMerchant;

        @SerializedName("fee_customer")
        private int feeCustomer;

        @SerializedName("total_fee")
        private int totalFee;

        @SerializedName("amount_received")
        private int amountReceived;

        @SerializedName("pay_code")
        private String payCode;

        @SerializedName("pay_url")
        private String payUrl;

        @SerializedName("qr_url")
        private String qrUrl;

        @SerializedName("checkout_url")
        private String checkoutUrl;

        @SerializedName("status")
        private String status;

        @SerializedName("expired_time")
        private long expiredTime;

        @SerializedName("order_items")
        private List<OrderItem> orderItems;

        @SerializedName("instructions")
        private List<Instruction> instructions;

        // Constructors
        public DonationData() {}

        protected DonationData(Parcel in) {
            reference = in.readString();
            merchantRef = in.readString();
            paymentSelectionType = in.readString();
            paymentMethod = in.readString();
            paymentName = in.readString();
            customerName = in.readString();
            customerEmail = in.readString();
            customerPhone = in.readString();
            callbackUrl = in.readString();
            returnUrl = in.readString();
            amount = in.readInt();
            feeMerchant = in.readInt();
            feeCustomer = in.readInt();
            totalFee = in.readInt();
            amountReceived = in.readInt();
            payCode = in.readString();
            payUrl = in.readString();
            qrUrl = in.readString();
            checkoutUrl = in.readString();
            status = in.readString();
            expiredTime = in.readLong();
            orderItems = in.createTypedArrayList(OrderItem.CREATOR);
            instructions = in.createTypedArrayList(Instruction.CREATOR);
        }

        public static final Creator<DonationData> CREATOR = new Creator<DonationData>() {
            @Override
            public DonationData createFromParcel(Parcel in) {
                return new DonationData(in);
            }

            @Override
            public DonationData[] newArray(int size) {
                return new DonationData[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(reference);
            dest.writeString(merchantRef);
            dest.writeString(paymentSelectionType);
            dest.writeString(paymentMethod);
            dest.writeString(paymentName);
            dest.writeString(customerName);
            dest.writeString(customerEmail);
            dest.writeString(customerPhone);
            dest.writeString(callbackUrl);
            dest.writeString(returnUrl);
            dest.writeInt(amount);
            dest.writeInt(feeMerchant);
            dest.writeInt(feeCustomer);
            dest.writeInt(totalFee);
            dest.writeInt(amountReceived);
            dest.writeString(payCode);
            dest.writeString(payUrl);
            dest.writeString(qrUrl);
            dest.writeString(checkoutUrl);
            dest.writeString(status);
            dest.writeLong(expiredTime);
            dest.writeTypedList(orderItems);
            dest.writeTypedList(instructions);
        }

        // Getters and Setters
        public String getReference() { return reference; }
        public void setReference(String reference) { this.reference = reference; }

        public String getMerchantRef() { return merchantRef; }
        public void setMerchantRef(String merchantRef) { this.merchantRef = merchantRef; }

        public String getPaymentSelectionType() { return paymentSelectionType; }
        public void setPaymentSelectionType(String paymentSelectionType) { this.paymentSelectionType = paymentSelectionType; }

        public String getPaymentMethod() { return paymentMethod; }
        public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

        public String getPaymentName() { return paymentName; }
        public void setPaymentName(String paymentName) { this.paymentName = paymentName; }

        public String getCustomerName() { return customerName; }
        public void setCustomerName(String customerName) { this.customerName = customerName; }

        public String getCustomerEmail() { return customerEmail; }
        public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

        public String getCustomerPhone() { return customerPhone; }
        public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

        public String getCallbackUrl() { return callbackUrl; }
        public void setCallbackUrl(String callbackUrl) { this.callbackUrl = callbackUrl; }

        public String getReturnUrl() { return returnUrl; }
        public void setReturnUrl(String returnUrl) { this.returnUrl = returnUrl; }

        public int getAmount() { return amount; }
        public void setAmount(int amount) { this.amount = amount; }

        public int getFeeMerchant() { return feeMerchant; }
        public void setFeeMerchant(int feeMerchant) { this.feeMerchant = feeMerchant; }

        public int getFeeCustomer() { return feeCustomer; }
        public void setFeeCustomer(int feeCustomer) { this.feeCustomer = feeCustomer; }

        public int getTotalFee() { return totalFee; }
        public void setTotalFee(int totalFee) { this.totalFee = totalFee; }

        public int getAmountReceived() { return amountReceived; }
        public void setAmountReceived(int amountReceived) { this.amountReceived = amountReceived; }

        public String getPayCode() { return payCode; }
        public void setPayCode(String payCode) { this.payCode = payCode; }

        public String getPayUrl() { return payUrl; }
        public void setPayUrl(String payUrl) { this.payUrl = payUrl; }

        public String getQrUrl() { return qrUrl; }
        public void setQrUrl(String qrUrl) { this.qrUrl = qrUrl; }

        public String getCheckoutUrl() { return checkoutUrl; }
        public void setCheckoutUrl(String checkoutUrl) { this.checkoutUrl = checkoutUrl; }

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public long getExpiredTime() { return expiredTime; }
        public void setExpiredTime(long expiredTime) { this.expiredTime = expiredTime; }

        public List<OrderItem> getOrderItems() { return orderItems; }
        public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }

        public List<Instruction> getInstructions() { return instructions; }
        public void setInstructions(List<Instruction> instructions) { this.instructions = instructions; }
    }

    public static class OrderItem implements Parcelable {
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
        private String productUrl;

        @SerializedName("image_url")
        private String imageUrl;

        public OrderItem() {}

        protected OrderItem(Parcel in) {
            sku = in.readString();
            name = in.readString();
            price = in.readInt();
            quantity = in.readInt();
            subtotal = in.readInt();
            productUrl = in.readString();
            imageUrl = in.readString();
        }

        public static final Creator<OrderItem> CREATOR = new Creator<OrderItem>() {
            @Override
            public OrderItem createFromParcel(Parcel in) {
                return new OrderItem(in);
            }

            @Override
            public OrderItem[] newArray(int size) {
                return new OrderItem[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(sku);
            dest.writeString(name);
            dest.writeInt(price);
            dest.writeInt(quantity);
            dest.writeInt(subtotal);
            dest.writeString(productUrl);
            dest.writeString(imageUrl);
        }

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

        public String getProductUrl() { return productUrl; }
        public void setProductUrl(String productUrl) { this.productUrl = productUrl; }

        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    }

    public static class Instruction implements Parcelable {
        @SerializedName("title")
        private String title;

        @SerializedName("steps")
        private List<String> steps;

        public Instruction() {}

        protected Instruction(Parcel in) {
            title = in.readString();
            steps = in.createStringArrayList();
        }

        public static final Creator<Instruction> CREATOR = new Creator<Instruction>() {
            @Override
            public Instruction createFromParcel(Parcel in) {
                return new Instruction(in);
            }

            @Override
            public Instruction[] newArray(int size) {
                return new Instruction[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(title);
            dest.writeStringList(steps);
        }

        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public List<String> getSteps() { return steps; }
        public void setSteps(List<String> steps) { this.steps = steps; }
    }

    public static class DonationDetails implements Parcelable {

        @SerializedName("id")
        private int id;

        @SerializedName("order_id")
        private String orderId;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("campaign_id")
        private int campaignId;

        @SerializedName("amount")
        private String amount;

        @SerializedName("is_anonymous")
        private boolean isAnonymous;

        @SerializedName("doa")
        private String doa;

        @SerializedName("status")
        private String status;

        @SerializedName("payment_method")
        private String paymentMethod;

        @SerializedName("tripay_reference")
        private String tripayReference;

        // Can be null
        @SerializedName("payment_url")
        private String paymentUrl;

        // Can be null
        @SerializedName("va_number")
        private String vaNumber;

        @SerializedName("qr_code_url")
        private String qrCodeUrl;

        @SerializedName("created_at")
        private String createdAt;

        @SerializedName("updated_at")
        private String updatedAt;

        @SerializedName("expired_time")
        private String expiredAt;

        @SerializedName("campaign")
        private CampaignDetails campaign;

        public DonationDetails() {}

        public int getId() { return id; }
        public String getOrderId() { return orderId; }
        public int getUserId() { return userId; }
        public int getCampaignId() { return campaignId; }
        public String getAmount() { return amount; }
        public boolean isAnonymous() { return isAnonymous; }
        public String getDoa() { return doa; }
        public String getStatus() { return status; }
        public String getPaymentMethod() { return paymentMethod; }
        public String getTripayReference() { return tripayReference; }
        public String getPaymentUrl() { return paymentUrl; }
        public String getVaNumber() { return vaNumber; }
        public String getQrCodeUrl() { return qrCodeUrl; }
        public String getCreatedAt() { return createdAt; }
        public String getUpdatedAt() { return updatedAt; }
        public String getExpiredAt() { return expiredAt; }
        public void setExpiredAt(String expiredAt) {
            this.expiredAt = expiredAt;
        }
        public void setAmount(String amount) {
            this.amount = amount;
        }

        public void setPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public CampaignDetails getCampaign() { return campaign; }

        public boolean isPending() {
            return "pending".equalsIgnoreCase(status);
        }

        public boolean isSuccessful() {
            return "success".equalsIgnoreCase(status) ||
                    "berhasil".equalsIgnoreCase(status) ||
                    "paid".equalsIgnoreCase(status);
        }

        public boolean isFailed() {
            return "failed".equalsIgnoreCase(status);
        }

        public boolean isExpired() {
            return "expired".equalsIgnoreCase(status);
        }



        public String getFormattedAmount() {
            if (amount == null) return "Rp 0";

            try {
                long amountValue = Long.parseLong(amount.replace(".00", ""));
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                formatter.setMaximumFractionDigits(0);
                formatter.setMinimumFractionDigits(0);
                return formatter.format(amountValue).replace("IDR", "Rp");
            } catch (Exception e) {
                return "Rp " + amount;
            }
        }

        public String getFormattedCreatedDate() {
            if (createdAt == null) return "";

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy - HH:mm", new Locale("id", "ID"));
                Date date = inputFormat.parse(createdAt);
                return date != null ? outputFormat.format(date) : createdAt;
            } catch (Exception e) {
                return createdAt;
            }
        }

        public String getStatusDisplayText() {
            if (status == null) return "Unknown";

            switch (status.toLowerCase()) {
                case "pending":
                    return "Pending";
                case "success":
                case "berhasil":
                case "paid":
                    return "Berhasil";
                case "failed":
                    return "Gagal";
                case "cancelled":
                    return "Dibatalkan";
                case "expired":
                    return "Kadaluarsa";
                default:
                    return status;
            }
        }

        public String getTimeRemainingDisplayText() {
            if (expiredAt == null || !isPending()) {
                return getStatusDisplayText();
            }

            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date expiredDate = format.parse(expiredAt);

                if (expiredDate == null) return "Invalid Date";

                long diff = expiredDate.getTime() - System.currentTimeMillis();

                if (diff <= 0) {
                    return "Telah kedaluwarsa";
                }

                long minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(diff) - TimeUnit.MINUTES.toSeconds(minutes);

                return String.format(Locale.getDefault(), "Sisa waktu %dm %ds", minutes, seconds);

            } catch (Exception e) {
                e.printStackTrace();
                return "Error parsing date";
            }
        }


        protected DonationDetails(Parcel in) {
            id = in.readInt();
            orderId = in.readString();
            userId = in.readInt();
            campaignId = in.readInt();
            amount = in.readString();
            isAnonymous = in.readByte() != 0;
            doa = in.readString();
            status = in.readString();
            paymentMethod = in.readString();
            tripayReference = in.readString();
            paymentUrl = in.readString();
            vaNumber = in.readString();
            qrCodeUrl = in.readString();
            createdAt = in.readString();
            updatedAt = in.readString();
            campaign = in.readParcelable(CampaignDetails.class.getClassLoader());
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeString(orderId);
            dest.writeInt(userId);
            dest.writeInt(campaignId);
            dest.writeString(amount);
            dest.writeByte((byte) (isAnonymous ? 1 : 0));
            dest.writeString(doa);
            dest.writeString(status);
            dest.writeString(paymentMethod);
            dest.writeString(tripayReference);
            dest.writeString(paymentUrl);
            dest.writeString(vaNumber);
            dest.writeString(qrCodeUrl);
            dest.writeString(createdAt);
            dest.writeString(updatedAt);
            dest.writeParcelable(campaign, flags);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<DonationDetails> CREATOR = new Creator<DonationDetails>() {
            @Override
            public DonationDetails createFromParcel(Parcel in) {
                return new DonationDetails(in);
            }

            @Override
            public DonationDetails[] newArray(int size) {
                return new DonationDetails[size];
            }
        };
    }


    public static class CampaignDetails implements Parcelable {

        @SerializedName("id")
        private int id;

        @SerializedName("user_id")
        private int userId;

        @SerializedName("category_id")
        private int categoryId;

        @SerializedName("title")
        private String title;

        @SerializedName("slug")
        private String slug;

        @SerializedName("description")
        private String description;

        @SerializedName("thumbnail")
        private String thumbnail;

        @SerializedName("target_donation")
        private String targetDonation;

        @SerializedName("current_donation")
        private String currentDonation;

        @SerializedName("deadline")
        private String deadline;

        @SerializedName("status")
        private String status;

        @SerializedName("is_recommendation")
        private int isRecommendation;

        // Getters for all fields...
        public int getId() { return id; }
        public int getUserId() { return userId; }
        public int getCategoryId() { return categoryId; }
        public String getTitle() { return title; }
        public String getSlug() { return slug; }
        public String getDescription() { return description; }
        public String getThumbnail() { return thumbnail; }
        public String getTargetDonation() { return targetDonation; }
        public String getCurrentDonation() { return currentDonation; }
        public String getDeadline() { return deadline; }
        public String getStatus() { return status; }
        public int getIsRecommendation() { return isRecommendation; }


        protected CampaignDetails(Parcel in) {
            id = in.readInt();
            userId = in.readInt();
            categoryId = in.readInt();
            title = in.readString();
            slug = in.readString();
            description = in.readString();
            thumbnail = in.readString();
            targetDonation = in.readString();
            currentDonation = in.readString();
            deadline = in.readString();
            status = in.readString();
            isRecommendation = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(id);
            dest.writeInt(userId);
            dest.writeInt(categoryId);
            dest.writeString(title);
            dest.writeString(slug);
            dest.writeString(description);
            dest.writeString(thumbnail);
            dest.writeString(targetDonation);
            dest.writeString(currentDonation);
            dest.writeString(deadline);
            dest.writeString(status);
            dest.writeInt(isRecommendation);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        public static final Creator<CampaignDetails> CREATOR = new Creator<CampaignDetails>() {
            @Override
            public CampaignDetails createFromParcel(Parcel in) {
                return new CampaignDetails(in);
            }

            @Override
            public CampaignDetails[] newArray(int size) {
                return new CampaignDetails[size];
            }
        };
    }
}