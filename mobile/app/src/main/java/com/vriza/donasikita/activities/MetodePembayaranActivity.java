package com.vriza.donasikita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.models.PaymentChannel;
import com.vriza.donasikita.utils.DataCache;
import com.vriza.donasikita.utils.PaymentUtils;

import java.util.List;

public class MetodePembayaranActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ProgressBar progressBar;
    private LinearLayout paymentMethodsContainer;
    private LinearLayout instantPaymentContainer;
    private LinearLayout virtualAccountContainer;
    private LinearLayout bankTransferContainer;

    private DataCache dataCache;
    private PaymentChannel selectedPaymentChannel;
    private int donationAmount;
    private String currentPaymentCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_metode_pembayaran);

        initViews();
        setupToolbar();

        dataCache = DataCache.getInstance();

        getIntentData();

        if (dataCache.hasPaymentChannelsData()) {
            showPaymentMethods();
        } else {
            loadPaymentMethods();
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progressBar);
        paymentMethodsContainer = findViewById(R.id.paymentMethodsContainer);
        instantPaymentContainer = findViewById(R.id.instantPaymentContainer);
        virtualAccountContainer = findViewById(R.id.virtualAccountContainer);
        bankTransferContainer = findViewById(R.id.bankTransferContainer);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Pilih Metode Pembayaran");
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            donationAmount = intent.getIntExtra("donation_amount", 0);
            currentPaymentCode = intent.getStringExtra("current_payment_code");

            if (currentPaymentCode != null) {
                selectedPaymentChannel = dataCache.getPaymentChannelByCode(currentPaymentCode);
            }
        }
    }

    private void loadPaymentMethods() {
        showLoading(true);
        showPaymentMethods();
    }

    private void showPaymentMethods() {
        showLoading(false);

        instantPaymentContainer.removeAllViews();
        virtualAccountContainer.removeAllViews();
        bankTransferContainer.removeAllViews();

        List<PaymentChannel> instantMethods = dataCache.getInstantPaymentChannels();
        List<PaymentChannel> virtualAccounts = dataCache.getVirtualAccountChannels();
        List<PaymentChannel> bankTransfers = dataCache.getBankTransferChannels();

        for (PaymentChannel channel : instantMethods) {
            View itemView = createPaymentMethodItem(channel);
            instantPaymentContainer.addView(itemView);
        }

        for (PaymentChannel channel : virtualAccounts) {
            View itemView = createPaymentMethodItem(channel);
            virtualAccountContainer.addView(itemView);
        }

        for (PaymentChannel channel : bankTransfers) {
            View itemView = createPaymentMethodItem(channel);
            bankTransferContainer.addView(itemView);
        }

        paymentMethodsContainer.setVisibility(View.VISIBLE);
    }

    private View createPaymentMethodItem(PaymentChannel channel) {
        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.item_payment_method, null);

        ImageView ivPaymentIcon = itemView.findViewById(R.id.ivPaymentIcon);
        TextView tvPaymentName = itemView.findViewById(R.id.tvPaymentName);
        ImageView ivSelected = itemView.findViewById(R.id.ivSelected);

        boolean isValidAmount = PaymentUtils.isAmountValid(channel, donationAmount);
        boolean isSelected = selectedPaymentChannel != null &&
                selectedPaymentChannel.getCode().equals(channel.getCode());

        tvPaymentName.setText(channel.getName());


        Glide.with(this)
                .load(channel.getIconUrl())
                .placeholder(R.drawable.ic_bank_transfer)
                .error(R.drawable.ic_bank_transfer)
                .into(ivPaymentIcon);

        if (!isValidAmount) {
            itemView.setAlpha(0.5f);
            tvPaymentName.setTextColor(ContextCompat.getColor(this, android.R.color.darker_gray));
            ivPaymentIcon.setAlpha(0.5f);

            itemView.setOnClickListener(v -> {
                String minAmount = PaymentUtils.getFormattedMinimumAmount(channel);
            });
        } else {
            itemView.setAlpha(1.0f);
            tvPaymentName.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            ivPaymentIcon.setAlpha(1.0f);

            itemView.setOnClickListener(v -> selectPaymentMethod(channel));
        }

        ivSelected.setVisibility(isSelected ? View.VISIBLE : View.GONE);

        return itemView;
    }

    private void selectPaymentMethod(PaymentChannel channel) {
        if (!PaymentUtils.isAmountValid(channel, donationAmount)) {
            String minAmount = PaymentUtils.getFormattedMinimumAmount(channel);
            return;
        }

        selectedPaymentChannel = channel;

        showPaymentMethods();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("payment_channel_code", channel.getCode());
        resultIntent.putExtra("payment_channel_name", channel.getName());
        resultIntent.putExtra("payment_channel_icon", channel.getIconUrl());

        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        paymentMethodsContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    private boolean validatePaymentMethod(PaymentChannel channel, int donationAmount) {
        if (!PaymentUtils.isAmountValid(channel, donationAmount)) {
            String minAmount = PaymentUtils.getFormattedMinimumAmount(channel);
            String maxAmount = PaymentUtils.getFormattedMaximumAmount(channel);
            return false;
        }
        return true;
    }

    private void showFeeCalculation(PaymentChannel channel, int donationAmount) {
        int customerFee = PaymentUtils.calculateCustomerFee(channel, donationAmount);
        int totalFee = PaymentUtils.calculateTotalFee(channel, donationAmount);

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}