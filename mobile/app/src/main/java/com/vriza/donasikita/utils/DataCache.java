package com.vriza.donasikita.utils;

import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.models.Category;
import com.vriza.donasikita.models.PaymentChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataCache {
    private static DataCache instance;

    private List<Category> categories = new ArrayList<>();
    private List<Campaign> popularCampaigns = new ArrayList<>();
    private List<Campaign> allCampaigns = new ArrayList<>();
    private List<PaymentChannel> paymentChannels = new ArrayList<>();

    private DataCache() {}

    public static synchronized DataCache getInstance() {
        if (instance == null) {
            instance = new DataCache();
        }
        return instance;
    }

    public List<Category> getCategories() {
        return new ArrayList<>(categories);
    }

    public void setCategories(List<Category> categories) {
        this.categories.clear();
        if (categories != null) {
            this.categories.addAll(categories);
        }
    }

    public boolean hasCategoriesData() {
        return !categories.isEmpty();
    }

    public List<Campaign> getPopularCampaigns() {
        return new ArrayList<>(popularCampaigns);
    }

    public void setPopularCampaigns(List<Campaign> campaigns) {
        this.popularCampaigns.clear();
        if (campaigns != null) {
            this.popularCampaigns.addAll(campaigns);
        }
    }

    public boolean hasPopularCampaignsData() {
        return !popularCampaigns.isEmpty();
    }

    public List<Campaign> getAllCampaigns() {
        return new ArrayList<>(allCampaigns);
    }

    public void setAllCampaigns(List<Campaign> campaigns) {
        this.allCampaigns.clear();
        if (campaigns != null) {
            this.allCampaigns.addAll(campaigns);
        }
    }

    public boolean hasAllCampaignsData() {
        return !allCampaigns.isEmpty();
    }

    public List<PaymentChannel> getPaymentChannels() {
        return new ArrayList<>(paymentChannels);
    }

    public void setPaymentChannels(List<PaymentChannel> paymentChannels) {
        this.paymentChannels.clear();
        if (paymentChannels != null) {
            this.paymentChannels.addAll(paymentChannels);
        }
    }

    public boolean hasPaymentChannelsData() {
        return !paymentChannels.isEmpty();
    }

    public List<PaymentChannel> getPaymentChannelsByGroup(String group) {
        List<PaymentChannel> filteredChannels = new ArrayList<>();
        for (PaymentChannel channel : paymentChannels) {
            if (channel.getGroup() != null && channel.getGroup().equalsIgnoreCase(group) && channel.isActive()) {
                filteredChannels.add(channel);
            }
        }
        return filteredChannels;
    }

    public List<PaymentChannel> getInstantPaymentChannels() {
        List<PaymentChannel> instantChannels = new ArrayList<>();
        for (PaymentChannel channel : paymentChannels) {
            if (channel.isActive() && "E-Wallet".equalsIgnoreCase(channel.getGroup())) {
                instantChannels.add(channel);
            }
        }
        return instantChannels;
    }

    public List<PaymentChannel> getVirtualAccountChannels() {
        List<PaymentChannel> vaChannels = new ArrayList<>();
        for (PaymentChannel channel : paymentChannels) {
            if (channel.isActive() && "Virtual Account".equalsIgnoreCase(channel.getGroup())) {
                vaChannels.add(channel);
            }
        }
        return vaChannels;
    }

    public List<PaymentChannel> getBankTransferChannels() {
        List<PaymentChannel> bankChannels = new ArrayList<>();
        for (PaymentChannel channel : paymentChannels) {
            if (channel.isActive() && "Bank Transfer".equalsIgnoreCase(channel.getGroup())) {
                bankChannels.add(channel);
            }
        }
        return bankChannels;
    }

    public PaymentChannel getPaymentChannelByCode(String code) {
        for (PaymentChannel channel : paymentChannels) {
            if (channel.getCode() != null && channel.getCode().equalsIgnoreCase(code)) {
                return channel;
            }
        }
        return null;
    }

    public List<PaymentChannel> getActivePaymentChannels() {
        List<PaymentChannel> activeChannels = new ArrayList<>();
        for (PaymentChannel channel : paymentChannels) {
            if (channel.isActive()) {
                activeChannels.add(channel);
            }
        }
        return activeChannels;
    }

    public Map<String, List<PaymentChannel>> getPaymentChannelsByType() {
        Map<String, List<PaymentChannel>> channelsByType = new HashMap<>();

        channelsByType.put("INSTANT", getInstantPaymentChannels());
        channelsByType.put("VIRTUAL_ACCOUNT", getVirtualAccountChannels());
        channelsByType.put("BANK_TRANSFER", getBankTransferChannels());

        return channelsByType;
    }

    private boolean isInstantPaymentGroup(String group) {
        if (group == null) return false;
        return "E-Wallet".equalsIgnoreCase(group);
    }

    public boolean hasAllData() {
        return hasCategoriesData() && hasPopularCampaignsData() && hasAllCampaignsData();
    }

    public boolean hasAllDataWithPayments() {
        return hasAllData() && hasPaymentChannelsData();
    }

    public void clearCache() {
        categories.clear();
        popularCampaigns.clear();
        allCampaigns.clear();
        paymentChannels.clear();
    }

    public void clearPaymentChannels() {
        paymentChannels.clear();
    }
}