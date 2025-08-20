package com.vriza.donasikita.utils;

import com.vriza.donasikita.models.Fee;
import com.vriza.donasikita.models.PaymentChannel;

import java.text.NumberFormat;
import java.util.Locale;


public class PaymentUtils {

    public static int calculateTotalFee(PaymentChannel channel, int amount) {
        if (channel == null || channel.getTotalFee() == null) {
            return 0;
        }

        Fee totalFee = channel.getTotalFee();
        int calculatedFee = totalFee.getFlat() + (int) (amount * totalFee.getPercent() / 100);

        if (channel.getMinimumFee() != null && calculatedFee < channel.getMinimumFee()) {
            calculatedFee = channel.getMinimumFee();
        }
        if (channel.getMaximumFee() != null && calculatedFee > channel.getMaximumFee()) {
            calculatedFee = channel.getMaximumFee();
        }

        return calculatedFee;
    }

    public static int calculateCustomerFee(PaymentChannel channel, int amount) {
        if (channel == null || channel.getFeeCustomer() == null) {
            return 0;
        }

        Fee customerFee = channel.getFeeCustomer();
        int calculatedFee = customerFee.getFlat() + (int) (amount * customerFee.getPercent() / 100);

        if (channel.getMinimumFee() != null && calculatedFee < channel.getMinimumFee()) {
            calculatedFee = channel.getMinimumFee();
        }
        if (channel.getMaximumFee() != null && calculatedFee > channel.getMaximumFee()) {
            calculatedFee = channel.getMaximumFee();
        }

        return calculatedFee;
    }


    public static boolean isAmountValid(PaymentChannel channel, int amount) {
        if (channel == null) return false;

        boolean validMin = channel.getMinimumAmount() == null || amount >= channel.getMinimumAmount();
        boolean validMax = channel.getMaximumAmount() == null || amount <= channel.getMaximumAmount();

        return validMin && validMax;
    }


    public static String getFormattedMinimumAmount(PaymentChannel channel) {
        if (channel == null || channel.getMinimumAmount() == null) {
            return "Rp0";
        }
        return formatCurrency(channel.getMinimumAmount());
    }


    public static String getFormattedMaximumAmount(PaymentChannel channel) {
        if (channel == null || channel.getMaximumAmount() == null) {
            return "Tidak terbatas";
        }
        return formatCurrency(channel.getMaximumAmount());
    }


    public static String formatCurrency(int amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        return formatter.format(amount);
    }

    public static String getPaymentTypeDescription(String group) {
        if (group == null) return "Tidak diketahui";

        switch (group) {
            case "E-Wallet":
                return "Pembayaran instan (verifikasi otomatis, minimal nominal donasi Rp1.000)";
            case "Virtual Account":
                return "Virtual account (verifikasi otomatis, minimal nominal donasi Rp10.000)";
            case "Bank Transfer":
                return "Transfer bank (verifikasi manual 1x24 jam, minimal nominal donasi Rp10.000)";
            default:
                return "Metode pembayaran";
        }
    }


    public static String getPaymentCategory(String group) {
        if (group == null) return "OTHER";

        switch (group) {
            case "E-Wallet":
                return "INSTANT";
            case "Virtual Account":
                return "VIRTUAL_ACCOUNT";
            case "Bank Transfer":
                return "BANK_TRANSFER";
            default:
                return "OTHER";
        }
    }

    public static boolean isInstantVerification(PaymentChannel channel) {
        if (channel == null || channel.getGroup() == null) return false;

        String group = channel.getGroup();
        return "E-Wallet".equals(group) || "Virtual Account".equals(group);
    }


    public static String getProcessingTime(PaymentChannel channel) {
        if (isInstantVerification(channel)) {
            return "Otomatis (real-time)";
        } else {
            return "Manual (1x24 jam)";
        }
    }


    public static String getFeeDescription(PaymentChannel channel) {
        if (channel == null) return "";

        Fee totalFee = channel.getTotalFee();
        if (totalFee == null) return "Gratis";

        StringBuilder description = new StringBuilder();

        if (totalFee.getFlat() > 0) {
            description.append(formatCurrency(totalFee.getFlat()));
        }

        if (totalFee.getPercent() > 0) {
            if (description.length() > 0) {
                description.append(" + ");
            }
            description.append(String.format("%.1f%%", totalFee.getPercent()));
        }

        if (description.length() == 0) {
            return "Gratis";
        }

        return description.toString();
    }
}