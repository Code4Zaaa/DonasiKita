package com.vriza.donasikita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.models.PaymentChannel;
import com.vriza.donasikita.network.responses.DonationResponse;
import com.vriza.donasikita.utils.DataCache;
import com.vriza.donasikita.utils.PaymentStatusChecker;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StatusPembayaranActivity extends AppCompatActivity implements PaymentStatusChecker.PaymentStatusListener {

    private Toolbar toolbar;

    private TextView tvStatusTitle;
    private TextView tvStatusSubtitile;
    private TextView tvPaymentDeadline;
    private TextView tvPaymentInstruction;
    private TextView tvDate;
    private TextView tvPaymentMethod;
    private TextView tvDonationId;
    private TextView tvPaymentStatus;
    private TextView tvTotalDonation;
    private TextView tvNominalDonation;

    private String orderId;
    private DonationResponse.DonationDetails donationData;
    private PaymentChannel paymentData;
    private Campaign campaign;
    private String paymentStatus;
    private PaymentStatusChecker paymentStatusChecker;

    private long expiredTime;
    private String expiredAt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_pembayaran);

        initViews();
        setupToolbar();
        getIntentData();
        DataCache dataCache = DataCache.getInstance();

        loadInitialData();

        if (donationData != null && donationData.getPaymentMethod() != null) {
            paymentData = dataCache.getPaymentChannelByCode(donationData.getPaymentMethod());
            android.util.Log.d("StatusPembayaran", "PaymentChannel found: " + (paymentData != null ? paymentData.getName() : "null"));
        } else {
            android.util.Log.w("StatusPembayaran", "Cannot get PaymentChannel - donationData or paymentMethod is null");
        }

        tvPaymentInstruction.setOnClickListener(v -> {
            Intent intent = new Intent(this, IntruksiPembayaranActivity.class);
            intent.putExtra("payment_data", donationData);

            Campaign campaignToPass = null;

            if (campaign != null) {
                campaignToPass = campaign;
                Log.d("StatusPembayaran", "Using campaign from intent");
            }
            else if (donationData != null && donationData.getCampaign() != null) {
                campaignToPass = Campaign.fromCampaignDetails(donationData.getCampaign());
                Log.d("StatusPembayaran", "Converted campaign from donationData");
            }

            if (campaignToPass != null) {
                intent.putExtra("campaign", campaignToPass);
            } else {
                Log.w("StatusPembayaran", "No campaign data available to pass");
            }

            long donationAmountLong = 0;
            try {
                String amountStr = donationData.getAmount().replace(".00", "");
                donationAmountLong = Long.parseLong(amountStr);
            } catch (Exception e) {
                Log.e("StatusPembayaran", "Error parsing donation amount: " + donationData.getAmount(), e);
                donationAmountLong = 0;
            }
            intent.putExtra("donation_amount", donationAmountLong);

            intent.putExtra("payment_method_name", donationData.getPaymentMethod());

            if (paymentData != null) {
                intent.putExtra("payment_method_icon", paymentData.getIconUrl());
            } else {
                Log.w("StatusPembayaran", "PaymentData is null, not adding payment_method_icon");
            }

            if (expiredTime > 0) {
                intent.putExtra("expired_time", expiredTime);
                Log.d("StatusPembayaran", "Passing expired_time to InstruksiPembayaran: " + expiredTime);
            }

            if (expiredAt != null && !expiredAt.isEmpty()) {
                intent.putExtra("expired_at", expiredAt);
                Log.d("StatusPembayaran", "Passing expired_at to InstruksiPembayaran: " + expiredAt);
            }

            if (donationData != null && donationData.getExpiredAt() != null && !donationData.getExpiredAt().isEmpty()) {
                intent.putExtra("expired_at", donationData.getExpiredAt());
                Log.d("StatusPembayaran", "Passing expired_at from donationData to InstruksiPembayaran: " + donationData.getExpiredAt());
            }

            Log.d("StatusPembayaran", "Starting IntruksiPembayaranActivity with expiration data");
            startActivity(intent);
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvStatusTitle = findViewById(R.id.tv_status_title);
        tvStatusSubtitile = findViewById(R.id.tv_status_subtitle);
        tvPaymentDeadline = findViewById(R.id.tv_payment_deadline);
        tvPaymentInstruction = findViewById(R.id.tv_payment_instruction);
        tvDate = findViewById(R.id.tv_date);
        tvPaymentMethod = findViewById(R.id.tv_payment_method);
        tvDonationId = findViewById(R.id.tv_donation_id);
        tvPaymentStatus = findViewById(R.id.tv_status);
        tvTotalDonation = findViewById(R.id.tv_total_donation);
        tvNominalDonation = findViewById(R.id.tv_nominal_donation);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }


    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            orderId = intent.getStringExtra("ORDER_ID");

            donationData = intent.getParcelableExtra("DONATION_DETAILS");

            campaign = intent.getParcelableExtra("CAMPAIGN");

            if (campaign != null) {
                android.util.Log.d("StatusPembayaran", "Campaign data received from intent");
            } else if (donationData != null && donationData.getCampaign() != null) {
                campaign = Campaign.fromCampaignDetails(donationData.getCampaign());
                android.util.Log.d("StatusPembayaran", "Campaign converted from donationData as fallback");
            } else {
                android.util.Log.w("StatusPembayaran", "No campaign data available");
            }

            expiredTime = intent.getLongExtra("EXPIRED_TIME", 0);
            expiredAt = intent.getStringExtra("EXPIRED_AT");

            long donationAmount = intent.getLongExtra("DONATION_AMOUNT", 0);
            String paymentMethodName = intent.getStringExtra("PAYMENT_METHOD_NAME");

            android.util.Log.d("StatusPembayaran", "Intent data received:");
            android.util.Log.d("StatusPembayaran", "Order ID: " + orderId);
            android.util.Log.d("StatusPembayaran", "Donation Details: " + (donationData != null ? "Available" : "Null"));
            android.util.Log.d("StatusPembayaran", "Campaign: " + (campaign != null ? "Available" : "Null"));

            if (orderId == null || orderId.isEmpty()) {
                Toast.makeText(this, "ID Donasi tidak valid.", Toast.LENGTH_LONG).show();
                finish();
                return;
            }

            if (orderId.startsWith("#")) {
                orderId = orderId.substring(1);
            }
        }
    }

    private void loadInitialData() {
        if (donationData != null) {
            paymentStatus = donationData.getStatus();
            setupUI();
            startPaymentStatusChecking();
        } else {
            PaymentStatusChecker.checkOnce(orderId, new PaymentStatusChecker.PaymentStatusListener() {
                @Override
                public void onStatusChanged(String newStatus, DonationResponse.DonationDetails updatedDonationData) {
                    donationData = updatedDonationData;
                    paymentStatus = newStatus;

                    runOnUiThread(() -> {
                        setupUI();
                        startPaymentStatusChecking();
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        Toast.makeText(StatusPembayaranActivity.this, "Gagal memuat data: " + error, Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }
            });
        }
    }

    private void setupUI() {
        if (donationData == null) {
            return;
        }

        setupStatusDisplay();
        setupPaymentDetails();
        setupDonationSummary();
    }

    private void setupStatusDisplay() {
        if (donationData.isSuccessful()) {
            tvStatusTitle.setText("Terima kasih!");
            tvStatusSubtitile.setText("Donasimu telah diterima dan akan segera disalurkan");
            tvPaymentDeadline.setVisibility(View.GONE);
            tvPaymentInstruction.setVisibility(View.GONE);
        } else if (donationData.isFailed()) {
            tvStatusTitle.setText("Donasi dibatalkan");
            tvStatusSubtitile.setText("Batas waktu pembayaran telah berakhir atau donasi gagal tercatat di sistem");
            tvPaymentDeadline.setVisibility(View.GONE);
            tvPaymentInstruction.setVisibility(View.GONE);
        }else if (donationData.isExpired()) {
            tvStatusTitle.setText("Donasi dibatalkan");
            tvStatusSubtitile.setText("Batas waktu pembayaran telah berakhir atau donasi gagal tercatat di sistem");
            tvPaymentDeadline.setVisibility(View.GONE);
            tvPaymentInstruction.setVisibility(View.GONE);
        } else {
            tvStatusTitle.setText("Menunggu pembayaran");
            tvStatusSubtitile.setText("Segera lakukan pembayaran sebelum");

            setupPaymentDeadline();

            tvPaymentInstruction.setVisibility(View.VISIBLE);
        }
    }

    private void setupPaymentDeadline() {
        String formattedDeadline = getFormattedExpiredTime();

        if (formattedDeadline != null && !formattedDeadline.isEmpty()) {
            tvPaymentDeadline.setText(formattedDeadline);
            tvPaymentDeadline.setVisibility(View.VISIBLE);

            android.util.Log.d("StatusPembayaran", "Payment deadline set: " + formattedDeadline);
        } else {
            tvPaymentDeadline.setVisibility(View.GONE);
            android.util.Log.d("StatusPembayaran", "No valid expiration time found");
        }
    }

    private void setupPaymentDetails() {
        tvDate.setText(donationData.getFormattedCreatedDate());

        if (donationData.getPaymentMethod() != null) {
            tvPaymentMethod.setText(donationData.getPaymentMethod());
        }

        if (donationData.getOrderId() != null) {
            tvDonationId.setText(donationData.getOrderId());
        }

        setupPaymentStatusDisplay();
    }

    private void setupPaymentStatusDisplay() {
        String statusText = donationData.getStatusDisplayText();
        tvPaymentStatus.setText(statusText);
    }

    private void setupDonationSummary() {
        String formattedAmount = donationData.getFormattedAmount();
        tvTotalDonation.setText(formattedAmount);
        tvNominalDonation.setText(formattedAmount);
    }

    private String getFormattedExpiredTime() {
        if (expiredTime > 0) {
            try {
                Date expiredDate = new Date(expiredTime * 1000L);
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy - HH:mm", new Locale("id", "ID"));
                return outputFormat.format(expiredDate);
            } catch (Exception e) {
                android.util.Log.e("StatusPembayaran", "Error formatting expiredTime", e);
            }
        }

        if (expiredAt != null && !expiredAt.isEmpty()) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy - HH:mm", new Locale("id", "ID"));

                Date expiredDate = inputFormat.parse(expiredAt);
                if (expiredDate != null) {
                    return outputFormat.format(expiredDate);
                }
            } catch (Exception e) {
                android.util.Log.e("StatusPembayaran", "Error formatting expiredAt", e);
            }
        }

        if (donationData != null && donationData.getExpiredAt() != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMMM yyyy - HH:mm", new Locale("id", "ID"));

                Date expiredDate = inputFormat.parse(donationData.getExpiredAt());
                if (expiredDate != null) {
                    return outputFormat.format(expiredDate);
                }
            } catch (Exception e) {
                android.util.Log.e("StatusPembayaran", "Error formatting donationData expiredAt", e);
            }
        }

        return null;
    }

    private String formatCurrency(long amount) {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            return formatter.format(amount).replace("IDR", "Rp");
        } catch (Exception e) {
            return "Rp " + String.format(Locale.getDefault(), "%,d", amount);
        }
    }

    public void updatePaymentStatus(String newStatus) {
        this.paymentStatus = newStatus;
        setupStatusDisplay();
        setupPaymentStatusDisplay();
    }

    private void startPaymentStatusChecking() {
        if (donationData != null && donationData.isPending()) {
            if (paymentStatusChecker != null) {
                paymentStatusChecker.stopChecking();
            }
            paymentStatusChecker = new PaymentStatusChecker(orderId, this);
            paymentStatusChecker.startChecking();
        }
    }

    @Override
    public void onStatusChanged(String newStatus, DonationResponse.DonationDetails updatedDonationData) {
        this.donationData = updatedDonationData;
        this.paymentStatus = newStatus;

        runOnUiThread(() -> {
            setupStatusDisplay();
            setupPaymentStatusDisplay();

            if (!updatedDonationData.isPending()) {
                if (paymentStatusChecker != null) {
                    paymentStatusChecker.stopChecking();
                }

                if (updatedDonationData.isSuccessful()) {
                    Toast.makeText(this, "Pembayaran berhasil! Terima kasih atas donasi Anda.", Toast.LENGTH_LONG).show();
                } else if (updatedDonationData.isFailed()) {
                    Toast.makeText(this, "Pembayaran gagal. Silakan coba lagi.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onError(String error) {
        if (error.contains("Order ID tidak valid")) {
            runOnUiThread(() -> {
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (paymentStatusChecker != null) {
            paymentStatusChecker.stopChecking();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (paymentStatusChecker != null) {
            paymentStatusChecker.stopChecking();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (donationData != null && donationData.isPending()) {
            startPaymentStatusChecking();
        }
    }
}