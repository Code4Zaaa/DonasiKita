package com.vriza.donasikita.activities;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.button.MaterialButton;
import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.responses.DonationDetailResponse;
import com.vriza.donasikita.network.responses.DonationResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IntruksiPembayaranActivity extends AppCompatActivity {

    private static final String TAG = "IntruksiPembayaran";
    private static final int PERMISSION_REQUEST_WRITE_STORAGE = 1001;

    private Toolbar toolbar;

    private TextView tvPaymentDeadline;
    private TextView tvCountdown;
    private TextView saveQrButton;
    private LinearLayout layoutQrPayment;
    private LinearLayout layoutBankPayment;
    private ImageView ivQrCode;
    private TextView tvTotalDonationQr;
    private TextView tvTotalDonationBank;
    private TextView tvVirtualAccountNumber;
    private TextView btnCopyVirtualAccount;
    private ImageView tvPaymentIconIntruksi;
    private MaterialButton btnCheckPaymentStatusQr;
    private MaterialButton btnCheckPaymentStatusBank;
    private LinearLayout layoutPaymentGuide;
    private ImageView ivExpandGuide;
    private ProgressBar progressBarStatus;

    private DonationResponse.DonationData paymentData;
    private DonationResponse.DonationDetails donationDetails;
    private Campaign campaign;
    private long donationAmount;
    private String paymentMethodName;
    private String paymentMethodIcon;
    private CountDownTimer countDownTimer;
    private boolean isGuideExpanded = true;
    private String qrCodeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intruksi_pembayaran);

        initViews();
        setupToolbar();
        getIntentData();
        setupListeners();
        setupUI();
        startCountdownTimer();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        tvPaymentDeadline = findViewById(R.id.tvPaymentDeadline);
        saveQrButton = findViewById(R.id.saveQrButton);
        tvCountdown = findViewById(R.id.tvCountdown);
        layoutQrPayment = findViewById(R.id.layoutQrPayment);
        layoutBankPayment = findViewById(R.id.layoutBankPayment);
        ivQrCode = findViewById(R.id.ivQrCode);
        tvTotalDonationQr = findViewById(R.id.tvTotalDonationQr);
        tvTotalDonationBank = findViewById(R.id.tvTotalDonationBank);
        tvVirtualAccountNumber = findViewById(R.id.tvVirtualAccountNumber);
        btnCopyVirtualAccount = findViewById(R.id.btnCopyVirtualAccount);
        tvPaymentIconIntruksi = findViewById(R.id.tvPaymentIconIntruksi);
        btnCheckPaymentStatusQr = findViewById(R.id.btnCheckPaymentStatusQr);
        btnCheckPaymentStatusBank = findViewById(R.id.btnCheckPaymentStatusBank);
        layoutPaymentGuide = findViewById(R.id.layoutPaymentGuide);
        ivExpandGuide = findViewById(R.id.ivExpandGuide);
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
            if (intent.hasExtra("payment_data")) {
                try {
                    Object paymentObject = intent.getParcelableExtra("payment_data");

                    if (paymentObject instanceof DonationResponse.DonationData) {
                        paymentData = (DonationResponse.DonationData) paymentObject;
                        Log.d(TAG, "Received DonationData from intent");
                    } else if (paymentObject instanceof DonationResponse.DonationDetails) {
                        donationDetails = (DonationResponse.DonationDetails) paymentObject;
                        Log.d(TAG, "Received DonationDetails from intent");
                    } else {
                        Log.e(TAG, "Unknown payment data type: " + (paymentObject != null ? paymentObject.getClass().getSimpleName() : "null"));
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error getting payment data from intent", e);
                }
            }

            campaign = intent.getParcelableExtra("campaign");

            if (intent.hasExtra("donation_amount")) {
                Object amountObj = intent.getExtras().get("donation_amount");
                if (amountObj instanceof Long) {
                    donationAmount = (Long) amountObj;
                } else if (amountObj instanceof String) {
                    try {
                        String amountStr = ((String) amountObj).replace(".00", "");
                        donationAmount = Long.parseLong(amountStr);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing donation amount string: " + amountObj, e);
                        donationAmount = 0;
                    }
                } else {
                    donationAmount = intent.getLongExtra("donation_amount", 0);
                }
            } else {
                donationAmount = 0;
            }

            paymentMethodName = intent.getStringExtra("payment_method_name");
            paymentMethodIcon = intent.getStringExtra("payment_method_icon");

            if (intent.hasExtra("expired_time")) {
                long expiredTimeFromIntent = intent.getLongExtra("expired_time", 0);
                if (expiredTimeFromIntent > 0) {
                    if (paymentData == null) {
                        paymentData = new DonationResponse.DonationData();
                    }
                    paymentData.setExpiredTime(expiredTimeFromIntent);
                }
            }

            if (intent.hasExtra("expired_at")) {
                String expiredAtFromIntent = intent.getStringExtra("expired_at");
                if (expiredAtFromIntent != null && !expiredAtFromIntent.isEmpty()) {
                    if (donationDetails == null) {
                        donationDetails = new DonationResponse.DonationDetails();
                    }
                    donationDetails.setExpiredAt(expiredAtFromIntent);
                    Log.d(TAG, "Set expired_at from intent: " + expiredAtFromIntent);
                }
            }

            Log.d(TAG, "Intent data received:");
            Log.d(TAG, "PaymentData (DonationData): " + (paymentData != null ? "Available" : "Null"));
            Log.d(TAG, "DonationDetails: " + (donationDetails != null ? "Available" : "Null"));
            Log.d(TAG, "Campaign: " + (campaign != null ? "Available" : "Null"));
            Log.d(TAG, "Donation Amount: " + donationAmount);
            Log.d(TAG, "Payment Method: " + paymentMethodName);
            Log.d(TAG, "Payment Method Icon: " + paymentMethodIcon);
            Log.d(TAG, "Expired Time: " + (paymentData != null ? paymentData.getExpiredTime() : "Not available"));
            Log.d(TAG, "Expired At: " + (donationDetails != null ? donationDetails.getExpiredAt() : "Not available"));

            if (paymentData == null && donationDetails != null) {
                Log.d(TAG, "Using DonationDetails as payment data source");
            }
        }
    }

    private void setupUI() {
        if (paymentData == null && donationDetails == null) {
            Toast.makeText(this, "Data pembayaran tidak tersedia", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        setupPaymentDeadline();
        setupPaymentMethod();
        setupTotalAmount();
    }

    private void setupPaymentDeadline() {
        long expiredTime = 0;
        String expiredAt = null;

        if (paymentData != null && paymentData.getExpiredTime() > 0) {
            expiredTime = paymentData.getExpiredTime();
        } else if (donationDetails != null && donationDetails.getExpiredAt() != null) {
            expiredAt = donationDetails.getExpiredAt();
        }

        if (expiredTime > 0) {
            Date expiredDate = new Date(expiredTime * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm", new Locale("id", "ID"));
            tvPaymentDeadline.setText(sdf.format(expiredDate));
        } else if (expiredAt != null) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                SimpleDateFormat outputFormat = new SimpleDateFormat("EEEE, dd MMM yyyy HH:mm", new Locale("id", "ID"));
                Date expiredDate = inputFormat.parse(expiredAt);
                if (expiredDate != null) {
                    tvPaymentDeadline.setText(outputFormat.format(expiredDate));
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing expired date: " + expiredAt, e);
            }
        }
    }

    private void setupPaymentMethod() {
        String qrUrl = null;
        String payCode = null;

        if (donationDetails != null) {
            qrUrl = donationDetails.getQrCodeUrl();
            payCode = donationDetails.getVaNumber();
        }

        if (paymentData != null) {
            if (qrUrl == null || qrUrl.isEmpty()) {
                qrUrl = paymentData.getQrUrl();
            }
            if (payCode == null || payCode.isEmpty()) {
                payCode = paymentData.getPayCode();
            }
        }

        qrCodeUrl = qrUrl;

        boolean hasQrUrl = qrUrl != null && !qrUrl.isEmpty();
        boolean hasPayCode = payCode != null && !payCode.isEmpty();

        if (hasQrUrl) {
            layoutQrPayment.setVisibility(View.VISIBLE);
            layoutBankPayment.setVisibility(View.GONE);

            Glide.with(this)
                    .load(qrUrl)
                    .placeholder(R.drawable.ic_qr_placeholder)
                    .error(R.drawable.ic_qr_placeholder)
                    .into(ivQrCode);

        } else if (hasPayCode) {
            layoutQrPayment.setVisibility(View.GONE);
            layoutBankPayment.setVisibility(View.VISIBLE);

            tvVirtualAccountNumber.setText(payCode);

            if (paymentMethodIcon != null && !paymentMethodIcon.isEmpty()) {
                Glide.with(this)
                        .load(paymentMethodIcon)
                        .placeholder(R.drawable.ic_bank_transfer)
                        .error(R.drawable.ic_bank_transfer)
                        .into(tvPaymentIconIntruksi);
            } else {
                tvPaymentIconIntruksi.setImageResource(R.drawable.ic_bank_transfer);
                Log.d(TAG, "No payment method icon provided, using default");
            }

        } else {
            layoutQrPayment.setVisibility(View.VISIBLE);
            layoutBankPayment.setVisibility(View.GONE);
            ivQrCode.setImageResource(R.drawable.ic_qr_placeholder);
            Log.d(TAG, "No QR URL or Pay Code available, showing placeholder");
        }
    }

    private void setupTotalAmount() {
        long amount = 0;

        if (donationDetails != null && donationDetails.getAmount() != null) {
            try {
                String amountStr = donationDetails.getAmount().replace(".00", "");
                amount = Long.parseLong(amountStr);
            } catch (NumberFormatException e) {
                Log.e(TAG, "Error parsing amount from donationDetails", e);
            }
        }

        if (amount == 0 && paymentData != null) {
            amount = paymentData.getAmount();
        }

        if (amount == 0 && donationAmount > 0) {
            amount = donationAmount;
        }

        String formattedAmount = formatCurrency(amount);

        if (layoutQrPayment.getVisibility() == View.VISIBLE) {
            tvTotalDonationQr.setText(formattedAmount);
        }

        if (layoutBankPayment.getVisibility() == View.VISIBLE) {
            tvTotalDonationBank.setText(formattedAmount);
        }
    }

    private void setupListeners() {
        btnCopyVirtualAccount.setOnClickListener(v -> copyVirtualAccountNumber());

        btnCheckPaymentStatusQr.setOnClickListener(v -> checkPaymentStatus());
        btnCheckPaymentStatusBank.setOnClickListener(v -> checkPaymentStatus());

        ivExpandGuide.setOnClickListener(v -> togglePaymentGuide());

        saveQrButton.setOnClickListener(v -> saveQrCodeToGallery());
    }

    private void saveQrCodeToGallery() {
        if (qrCodeUrl == null || qrCodeUrl.isEmpty()) {
            Toast.makeText(this, "QR Code tidak tersedia untuk disimpan", Toast.LENGTH_SHORT).show();
            return;
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_WRITE_STORAGE);
                return;
            }
        }

        saveQrButton.setEnabled(false);
        saveQrButton.setText("Menyimpan...");

        Glide.with(this)
                .asBitmap()
                .load(qrCodeUrl)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, Transition<? super Bitmap> transition) {
                        saveBitmapToGallery(resource);
                    }

                    @Override
                    public void onLoadCleared(Drawable placeholder) {
                        resetSaveButtonState();
                    }

                    @Override
                    public void onLoadFailed(Drawable errorDrawable) {
                        Toast.makeText(IntruksiPembayaranActivity.this,
                                "Gagal memuat QR Code", Toast.LENGTH_SHORT).show();
                        resetSaveButtonState();
                    }
                });
    }

    private void saveBitmapToGallery(Bitmap bitmap) {
        String fileName = "QR_Code_Donasi_" + System.currentTimeMillis() + ".jpg";
        boolean success = false;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            success = saveBitmapToGalleryAPI29(bitmap, fileName);
        } else {
            success = saveBitmapToGalleryLegacy(bitmap, fileName);
        }

        if (success) {
            Toast.makeText(this, "QR Code berhasil disimpan ke galeri", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Gagal menyimpan QR Code", Toast.LENGTH_SHORT).show();
        }

        resetSaveButtonState();
    }

    private boolean saveBitmapToGalleryAPI29(Bitmap bitmap, String fileName) {
        ContentResolver resolver = getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/DonasiKita");

        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                if (outputStream != null) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);
                    return true;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error saving bitmap to gallery (API 29+)", e);
            }
        }
        return false;
    }

    private boolean saveBitmapToGalleryLegacy(Bitmap bitmap, String fileName) {
        String imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES).toString() + File.separator + "DonasiKita";

        File file = new File(imagesDir);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e(TAG, "Failed to create directory: " + imagesDir);
                return false;
            }
        }

        File imageFile = new File(imagesDir, fileName);
        try (FileOutputStream outputStream = new FileOutputStream(imageFile)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(imageFile));
            sendBroadcast(mediaScanIntent);

            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error saving bitmap to gallery (Legacy)", e);
            return false;
        }
    }

    private void resetSaveButtonState() {
        saveQrButton.setEnabled(true);
        saveQrButton.setText("Simpan QR");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveQrCodeToGallery();
            } else {
                Toast.makeText(this, "Izin penyimpanan diperlukan untuk menyimpan QR Code",
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void copyVirtualAccountNumber() {
        String payCode = null;

        if (paymentData != null) {
            payCode = paymentData.getPayCode();
        } else if (donationDetails != null) {
            payCode = donationDetails.getVaNumber();
        }

        if (payCode != null && !payCode.isEmpty()) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Virtual Account", payCode);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Nomor Virtual Account berhasil disalin", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nomor Virtual Account tidak tersedia", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkPaymentStatus() {
        String orderId = getOrderIdFromPaymentData();
        if (orderId == null || orderId.isEmpty()) {
            Toast.makeText(this, "ID Donasi tidak ditemukan", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState(true);
        fetchDonationDetails(orderId);
    }

    private String getOrderIdFromPaymentData() {
        if (donationDetails != null && donationDetails.getOrderId() != null) {
            Log.d(TAG, "Using order ID from donationDetails: " + donationDetails.getOrderId());
            return donationDetails.getOrderId();
        }

        if (paymentData != null) {
            if (paymentData.getMerchantRef() != null && !paymentData.getMerchantRef().isEmpty()) {
                Log.d(TAG, "Using merchant_ref as order ID: " + paymentData.getMerchantRef());
                return paymentData.getMerchantRef();
            }

            if (paymentData.getReference() != null && !paymentData.getReference().isEmpty()) {
                Log.d(TAG, "Using reference as order ID: " + paymentData.getReference());
                return paymentData.getReference();
            }
        }

        Log.e(TAG, "No valid order ID found in payment data");
        return null;
    }

    private void fetchDonationDetails(String orderId) {
        String cleanOrderId = orderId.startsWith("#") ? orderId.substring(1) : orderId;

        Log.d(TAG, "Fetching donation details for order ID: " + cleanOrderId);

        Call<DonationDetailResponse> call = ApiClient.getApiService().getDonationByOrderId(cleanOrderId);
        call.enqueue(new Callback<DonationDetailResponse>() {
            @Override
            public void onResponse(Call<DonationDetailResponse> call, Response<DonationDetailResponse> response) {
                setLoadingState(false);

                if (response.isSuccessful() && response.body() != null) {
                    DonationDetailResponse donationResponse = response.body();

                    if (donationResponse.isSuccess() && donationResponse.getData() != null) {
                        DonationResponse.DonationDetails donationDetails = donationResponse.getData();

                        Log.d(TAG, "Successfully fetched donation details:");
                        Log.d(TAG, "Order ID: " + donationDetails.getOrderId());
                        Log.d(TAG, "Status: " + donationDetails.getStatus());
                        Log.d(TAG, "Amount: " + donationDetails.getAmount());
                        Log.d(TAG, "Payment Method: " + donationDetails.getPaymentMethod());
                        Log.d(TAG, "Expired at: " + donationDetails.getExpiredAt());

                        navigateToStatusActivity(donationDetails);

                    } else {
                        String errorMsg = donationResponse.getMessage() != null ?
                                donationResponse.getMessage() : "Data donasi tidak ditemukan";

                        Log.e(TAG, "API Error: " + errorMsg);
                        Toast.makeText(IntruksiPembayaranActivity.this,
                                "Gagal memuat data: " + errorMsg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    String errorMsg = "HTTP Error " + response.code();
                    if (response.code() == 404) {
                        errorMsg = "Donasi tidak ditemukan. Periksa kembali ID donasi.";
                    }

                    Log.e(TAG, "HTTP Error: " + response.code());
                    Toast.makeText(IntruksiPembayaranActivity.this,
                            errorMsg, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<DonationDetailResponse> call, Throwable t) {
                setLoadingState(false);

                Log.e(TAG, "Network error fetching donation details", t);

                String errorMessage = "Koneksi bermasalah";
                if (t.getMessage() != null) {
                    errorMessage += ": " + t.getMessage();
                }

                Toast.makeText(IntruksiPembayaranActivity.this,
                        errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToStatusActivity(DonationResponse.DonationDetails donationDetails) {
        Intent intent = new Intent(this, StatusPembayaranActivity.class);

        intent.putExtra("ORDER_ID", donationDetails.getOrderId());
        intent.putExtra("DONATION_DETAILS", donationDetails);

        Campaign campaignToPass = null;

        if (campaign != null) {
            campaignToPass = campaign;
            Log.d(TAG, "Using existing Campaign object");
        }
        else if (donationDetails.getCampaign() != null) {
            campaignToPass = Campaign.fromCampaignDetails(donationDetails.getCampaign());
            Log.d(TAG, "Converted Campaign from donationDetails");
        }

        if (campaignToPass != null) {
            intent.putExtra("CAMPAIGN", campaignToPass);
        } else {
            Log.w(TAG, "No campaign data available to pass");
        }

        intent.putExtra("DONATION_AMOUNT", donationAmount);
        intent.putExtra("PAYMENT_METHOD_NAME", paymentMethodName);

        if (paymentData != null && paymentData.getExpiredTime() > 0) {
            intent.putExtra("EXPIRED_TIME", paymentData.getExpiredTime());
            Log.d(TAG, "Passing expired time: " + paymentData.getExpiredTime());
        }

        if (donationDetails.getExpiredAt() != null && !donationDetails.getExpiredAt().isEmpty()) {
            intent.putExtra("EXPIRED_AT", donationDetails.getExpiredAt());
            Log.d(TAG, "Passing expired_at: " + donationDetails.getExpiredAt());
        }

        Log.d(TAG, "Navigating to StatusPembayaranActivity with:");
        Log.d(TAG, "ORDER_ID: " + donationDetails.getOrderId());
        Log.d(TAG, "Status: " + donationDetails.getStatus());

        startActivity(intent);
    }

    private void setLoadingState(boolean isLoading) {
        btnCheckPaymentStatusQr.setEnabled(!isLoading);
        btnCheckPaymentStatusBank.setEnabled(!isLoading);

        if (isLoading) {
            btnCheckPaymentStatusQr.setText("Memuat...");
            btnCheckPaymentStatusBank.setText("Memuat...");
        } else {
            btnCheckPaymentStatusQr.setText("Cek Status Pembayaran");
            btnCheckPaymentStatusBank.setText("Cek Status Pembayaran");
        }
    }

    private void togglePaymentGuide() {
        if (isGuideExpanded) {
            layoutPaymentGuide.setVisibility(View.GONE);
            ivExpandGuide.setRotation(0);
            isGuideExpanded = false;
        } else {
            layoutPaymentGuide.setVisibility(View.VISIBLE);
            ivExpandGuide.setRotation(180);
            isGuideExpanded = true;
        }
    }

    private void startCountdownTimer() {
        long expiredTime = 0;
        String expiredAt = null;

        if (paymentData != null && paymentData.getExpiredTime() > 0) {
            expiredTime = paymentData.getExpiredTime();
            Log.d(TAG, "Using expiredTime for countdown: " + expiredTime);
        }
        else if (donationDetails != null && donationDetails.getExpiredAt() != null) {
            expiredAt = donationDetails.getExpiredAt();
            Log.d(TAG, "Using expiredAt for countdown: " + expiredAt);

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date expiredDate = sdf.parse(expiredAt);
                if (expiredDate != null) {
                    expiredTime = expiredDate.getTime() / 1000L;
                    Log.d(TAG, "Converted expiredAt to expiredTime for countdown: " + expiredTime);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing expired date for countdown: " + expiredAt, e);
            }
        }

        if (expiredTime > 0) {
            long currentTime = System.currentTimeMillis() / 1000L;
            long timeLeft = (expiredTime - currentTime) * 1000L;

            Log.d(TAG, "Current time: " + currentTime + ", Expired time: " + expiredTime + ", Time left: " + timeLeft + "ms");

            if (timeLeft > 0) {
                countDownTimer = new CountDownTimer(timeLeft, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        updateCountdownDisplay(millisUntilFinished);
                    }

                    @Override
                    public void onFinish() {
                        tvCountdown.setText("00:00:00");
                        Toast.makeText(IntruksiPembayaranActivity.this,
                                "Waktu pembayaran telah habis", Toast.LENGTH_LONG).show();
                    }
                };
                countDownTimer.start();
                Log.d(TAG, "Countdown timer started with " + timeLeft + "ms remaining");
            } else {
                tvCountdown.setText("00:00:00");
                Log.d(TAG, "Payment already expired, setting countdown to 00:00:00");
            }
        } else {
            Log.w(TAG, "No valid expiration time found for countdown");
            tvCountdown.setText("--:--:--");
        }
    }

    private void updateCountdownDisplay(long millisUntilFinished) {
        long hours = millisUntilFinished / (1000 * 60 * 60);
        long minutes = (millisUntilFinished % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (millisUntilFinished % (1000 * 60)) / 1000;

        String timeString = String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds);
        tvCountdown.setText(timeString);
    }

    private String formatCurrency(long amount) {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
            return formatter.format(amount).replace("IDR", "Rp");
        } catch (Exception e) {
            return "Rp " + String.format(Locale.getDefault(), "%,d", amount);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}