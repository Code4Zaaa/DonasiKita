package com.vriza.donasikita.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.models.DonationRequest;
import com.vriza.donasikita.models.PaymentChannel;
import com.vriza.donasikita.network.responses.DonationResponse;
import com.vriza.donasikita.repository.DonationRepository;
import com.vriza.donasikita.utils.DataCache;
import com.vriza.donasikita.utils.PaymentUtils;
import com.vriza.donasikita.utils.SessionManager;

import java.text.NumberFormat;
import java.util.Locale;

public class PembayaranActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText etCustomAmount;
    private EditText etPrayerMessage;
    private TextView tvCharacterCount;
    private TextView tvTotalAmount;
    private TextView tvSelectedPaymentMethod;
    private ImageView ivPaymentIcon;
    private TextView tvChangePayment;
    private LinearLayout layoutSelectedPayment;
    private MaterialButton btnSelectPayment;
    private MaterialButton btnContinuePayment;
    private Switch switchAnonymous;

    private ProgressDialog progressDialog;

    private Campaign campaign;
    private long donationAmount;
    private String paymentMethodCode;
    private String paymentMethodName;
    private String paymentMethodIcon;

    private DonationRepository donationRepository;
    private SessionManager sessionManager;

    private static final int REQUEST_PAYMENT_METHOD = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pembayaran);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        initRepository();
        setupToolbar();
        getIntentData();
        setupListeners();
        updateUI();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        etCustomAmount = findViewById(R.id.etCustomAmount);
        etPrayerMessage = findViewById(R.id.etPrayerMessage);
        tvCharacterCount = findViewById(R.id.tvCharacterCount);
        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvSelectedPaymentMethod = findViewById(R.id.tvSelectedPaymentMethod);
        ivPaymentIcon = findViewById(R.id.ivPaymentIcon);
        tvChangePayment = findViewById(R.id.tvChangePayment);
        layoutSelectedPayment = findViewById(R.id.layoutSelectedPayment);
        btnSelectPayment = findViewById(R.id.btnSelectPayment);
        btnContinuePayment = findViewById(R.id.btnContinuePayment);
        switchAnonymous = findViewById(R.id.switchAnonymous);
    }

    private void initRepository() {
        donationRepository = new DonationRepository();
        sessionManager = new SessionManager(this);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getIntentData() {
        Intent intent = getIntent();
        if (intent != null) {
            campaign = intent.getParcelableExtra("campaign");
            donationAmount = intent.getLongExtra("donation_amount", 0);
            paymentMethodCode = intent.getStringExtra("payment_channel_code");
            paymentMethodName = intent.getStringExtra("payment_channel_name");
            paymentMethodIcon = intent.getStringExtra("payment_channel_icon");
        }
    }

    private void setupListeners() {
        etCustomAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cleanString = s.toString().replaceAll("[.,]", "");
                if (!cleanString.isEmpty()) {
                    try {
                        donationAmount = Long.parseLong(cleanString);
                        updateTotalAmount();
                        validateSelectedPaymentMethod();
                    } catch (NumberFormatException e) {
                        donationAmount = 0;
                        updateTotalAmount();
                        validateSelectedPaymentMethod();
                    }
                } else {
                    donationAmount = 0;
                    updateTotalAmount();
                    validateSelectedPaymentMethod();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");
                    if (!cleanString.isEmpty()) {
                        try {
                            long parsed = Long.parseLong(cleanString);
                            NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            String formatted = formatter.format(parsed);

                            etCustomAmount.removeTextChangedListener(this);
                            etCustomAmount.setText(formatted);
                            etCustomAmount.setSelection(formatted.length());
                            etCustomAmount.addTextChangedListener(this);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

        etPrayerMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int length = s.length();
                tvCharacterCount.setText(length + "/240");
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSelectPayment.setOnClickListener(v -> openPaymentMethodSelection());
        tvChangePayment.setOnClickListener(v -> openPaymentMethodSelection());

        btnContinuePayment.setOnClickListener(v -> processPayment());
    }

    private void updateUI() {
        if (donationAmount > 0) {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
            etCustomAmount.setText(formatter.format(donationAmount));
            updateTotalAmount();
        }

        if (paymentMethodCode != null && paymentMethodName != null) {
            showSelectedPaymentMethod();
        } else {
            hideSelectedPaymentMethod();
        }

        if (campaign != null && campaign.getTitle() != null) {
            toolbar.setTitle(campaign.getTitle());
        }

        btnContinuePayment.setEnabled(false);
    }

    private void showSelectedPaymentMethod() {
        layoutSelectedPayment.setVisibility(View.VISIBLE);
        btnSelectPayment.setVisibility(View.GONE);

        tvSelectedPaymentMethod.setText(paymentMethodName);

        if (paymentMethodIcon != null) {
            Glide.with(this)
                    .load(paymentMethodIcon)
                    .placeholder(R.drawable.ic_bank_transfer)
                    .error(R.drawable.ic_bank_transfer)
                    .into(ivPaymentIcon);
        } else {
            ivPaymentIcon.setImageResource(getPaymentMethodIcon(paymentMethodCode));
        }

        validateSelectedPaymentMethod();
    }

    private void hideSelectedPaymentMethod() {
        layoutSelectedPayment.setVisibility(View.GONE);
        btnSelectPayment.setVisibility(View.VISIBLE);
    }

    private void validateSelectedPaymentMethod() {
        boolean isValid = false;

        if (paymentMethodCode != null && donationAmount > 0) {
            try {
                DataCache dataCache = DataCache.getInstance();
                if (dataCache != null) {
                    PaymentChannel selectedChannel = dataCache.getPaymentChannelByCode(paymentMethodCode);

                    if (selectedChannel != null) {
                        isValid = PaymentUtils.isAmountValid(selectedChannel, (int) donationAmount);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        btnContinuePayment.setEnabled(isValid);
    }

    private void clearSelectedPaymentMethod() {
        paymentMethodCode = null;
        paymentMethodName = null;
        paymentMethodIcon = null;
        hideSelectedPaymentMethod();
    }

    private int getPaymentMethodIcon(String code) {
        switch (code) {
            default: return R.drawable.ic_bank_transfer;
        }
    }

    private void openPaymentMethodSelection() {
        if (donationAmount <= 0) {
            return;
        }

        Intent paymentMethodIntent = new Intent(this, MetodePembayaranActivity.class);
        paymentMethodIntent.putExtra("campaign", campaign);
        paymentMethodIntent.putExtra("donation_amount", (int) donationAmount);
        paymentMethodIntent.putExtra("current_payment_code", paymentMethodCode);
        startActivityForResult(paymentMethodIntent, REQUEST_PAYMENT_METHOD);
    }

    private void updateTotalAmount() {
        tvTotalAmount.setText(formatCurrency(donationAmount));
    }

    private void processPayment() {
        if (donationAmount < 1000) {
            return;
        }

        if (paymentMethodCode == null || paymentMethodCode.isEmpty()) {
            return;
        }

        DataCache dataCache = DataCache.getInstance();
        PaymentChannel selectedChannel = dataCache.getPaymentChannelByCode(paymentMethodCode);

        if (selectedChannel != null && !PaymentUtils.isAmountValid(selectedChannel, (int) donationAmount)) {
            return;
        }

        DonationRequest donationRequest = createDonationRequest();

        showLoadingDialog();

        if (sessionManager.isLoggedIn()) {
            String authToken = sessionManager.getApiToken();
            donationRepository.createDonationWithAuth(authToken, donationRequest, donationCallback);
        } else {
            donationRepository.createDonation(donationRequest, donationCallback);
        }
    }

    private DonationRequest createDonationRequest() {
        String prayerMessage = etPrayerMessage.getText().toString().trim();
        boolean isAnonymous = switchAnonymous.isChecked();

        DonationRequest request = new DonationRequest();
        request.setCampaignId(String.valueOf(Integer.parseInt(campaign.getId())));
        request.setAmount(String.valueOf((int) donationAmount));
        request.setPayment_method(paymentMethodCode);
        request.setDoa(prayerMessage.isEmpty() ? null : prayerMessage);
        request.setIs_anonymous(isAnonymous);

        return request;
    }

    private final DonationRepository.DonationCallback donationCallback = new DonationRepository.DonationCallback() {
        @Override
        public void onSuccess(DonationResponse donationResponse) {
            hideLoadingDialog();

            if (donationResponse.isSuccess() && donationResponse.getData() != null) {
                DonationResponse.DonationData data = donationResponse.getData();

                handlePaymentResponse(data);
            } else {
                Toast.makeText(PembayaranActivity.this,
                        "Gagal membuat donasi. Silakan coba lagi.",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onError(String errorMessage) {
            hideLoadingDialog();
            Toast.makeText(PembayaranActivity.this,
                    "Error: " + errorMessage,
                    Toast.LENGTH_LONG).show();
        }
    };

    private void handlePaymentResponse(DonationResponse.DonationData data) {
        String paymentMethod = data.getPaymentMethod().toLowerCase();

        if (isEWallet(paymentMethod) && !isQRISPayment(data)) {
            if (data.getPayUrl() != null && !data.getPayUrl().isEmpty()) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(data.getPayUrl()));
                startActivity(browserIntent);
            } else {
                Toast.makeText(this, "URL pembayaran tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent instruksiIntent = new Intent(this, IntruksiPembayaranActivity.class);

            instruksiIntent.putExtra("payment_data", data);
            instruksiIntent.putExtra("campaign", campaign);
            instruksiIntent.putExtra("donation_amount", donationAmount);
            instruksiIntent.putExtra("payment_method_name", paymentMethodName);
            instruksiIntent.putExtra("payment_method_icon", paymentMethodIcon);

            startActivity(instruksiIntent);
        }
    }

    private boolean isEWallet(String paymentMethod) {
        String[] eWalletMethods = {
                "ovo", "dana", "gopay", "linkaja", "shopeepay",
                "astrapay", "doku", "sakuku", "blu"
        };

        for (String method : eWalletMethods) {
            if (paymentMethod.contains(method)) {
                return true;
            }
        }
        return false;
    }

    private boolean isQRISPayment(DonationResponse.DonationData data) {
        return data.getQrUrl() != null && !data.getQrUrl().isEmpty();
    }

    private void showLoadingDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Memproses pembayaran...");
            progressDialog.setCancelable(false);
        }

        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private String formatCurrency(long amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        formatter.setMaximumFractionDigits(0);
        formatter.setMinimumFractionDigits(0);

        String formatted = formatter.format(amount);
        return formatted.replace("Rp", "Rp");
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_PAYMENT_METHOD && resultCode == RESULT_OK && data != null) {
            paymentMethodCode = data.getStringExtra("payment_channel_code");
            paymentMethodName = data.getStringExtra("payment_channel_name");
            paymentMethodIcon = data.getStringExtra("payment_channel_icon");

            showSelectedPaymentMethod();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hideLoadingDialog();
    }
}