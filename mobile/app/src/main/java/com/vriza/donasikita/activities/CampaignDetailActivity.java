package com.vriza.donasikita.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.models.Donation;
import com.vriza.donasikita.models.DonationRequest;
import com.vriza.donasikita.models.Prayer;
import com.vriza.donasikita.adapters.PrayerAdapter;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CampaignDetailActivity extends AppCompatActivity {

    private Campaign campaign;

    private CollapsingToolbarLayout collapsingToolbar;
    private ImageView imgCampaignDetail;
    private TextView txtCampaignTitle;
    private TextView txtVerifiedBadge;
    private ProgressBar progressBarDetail;
    private TextView txtCollectedAmount;
    private TextView txtCollectedLabel;
    private TextView txtTargetAmount;
    private TextView txtDonorsCount;
    private TextView txtDescriptionDetail;
    private TextView txtReadMore;
    private MaterialButton btnDonate;
    private Toolbar toolbar;

    private TextView txtPrayersCount;
    private RecyclerView recyclerViewPrayers;
    private TextView txtViewMorePrayers;
    private PrayerAdapter prayerAdapter;

    private BottomSheetDialog donationDialog;
    private LinearLayout donationOption5000, donationOption15000, donationOption25000, donationOption50000;
    private LinearLayout customAmountSection;
    private EditText etCustomAmount;
    private MaterialButton btnContinuePayment;
    private ImageView btnCloseDialog;

    private boolean isDescriptionExpanded = false;
    private String fullDescriptionText = "";
    private static final int DESCRIPTION_COLLAPSED_LENGTH = 200;
    private long selectedDonationAmount = 0;
    private static final int MAX_PRAYERS_SHOWN = 10; 


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_campaign_details);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.category_details), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupToolbar();
        getCampaignData();
        populateViews();
        setupClickListeners();
        setupDonationDialog();
        setupPrayerSection();
    }

    private void initViews() {
        collapsingToolbar = findViewById(R.id.collapsingToolbar);
        imgCampaignDetail = findViewById(R.id.imgCampaignDetail);
        txtCampaignTitle = findViewById(R.id.txtCampaignTitle);
        txtVerifiedBadge = findViewById(R.id.txtVerifiedBadge);
        progressBarDetail = findViewById(R.id.progressBarDetail);
        txtCollectedAmount = findViewById(R.id.txtCollectedAmount);
        txtCollectedLabel = findViewById(R.id.txtCollectedLabel);
        txtTargetAmount = findViewById(R.id.txtTargetAmount);
        txtDonorsCount = findViewById(R.id.txtDonorsCount);
        txtDescriptionDetail = findViewById(R.id.txtDescriptionDetail);
        txtReadMore = findViewById(R.id.txtReadMore);
        btnDonate = findViewById(R.id.btnDonate);
        toolbar = findViewById(R.id.toolbar);

        txtPrayersCount = findViewById(R.id.txtPrayersCount);
        recyclerViewPrayers = findViewById(R.id.recyclerViewPrayers);
        txtViewMorePrayers = findViewById(R.id.txtViewMorePrayers);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void getCampaignData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("campaign")) {
            campaign = intent.getParcelableExtra("campaign");
        }

        if (campaign == null) {
            finish();
        }
    }

    private void setupPrayerSection() {
        prayerAdapter = new PrayerAdapter(this);
        recyclerViewPrayers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewPrayers.setAdapter(prayerAdapter);
        recyclerViewPrayers.setNestedScrollingEnabled(false);

        loadPrayersFromDonations();

        txtViewMorePrayers.setOnClickListener(v -> {
            // TODO: Implementasi untuk menampilkan semua doa
            showAllPrayers();
        });
    }

    private void loadPrayersFromDonations() {
        if (campaign == null || campaign.getDonations() == null) {
            txtPrayersCount.setText("0");
            return;
        }

        List<Prayer> prayers = new ArrayList<>();
        int totalPrayersCount = 0;
        int donationIndex = 0;

        for (DonationRequest donation : campaign.getDonations()) {
            if (donation.getDoa() != null && !donation.getDoa().trim().isEmpty()) {
                Log.d("CampaignDetailActivity", "Prayer FOUND: " + donation.getDoa());
                String donorName = "Orang Baik";
                String donorPhotoUrl = null;
                if (donation.getUser() != null) {
                    donorName = donation.getUser().getName();
                    donorPhotoUrl = donation.getUser().getPhotoUrl();
                }

                Prayer prayer = new Prayer(
                        donation.getDoa(),
                        donation.getUserId(),
                        donation.isAnonymous(),
                        donation.getCreatedAt(),
                        donation.getAmount(),
                        donorName,
                        donorPhotoUrl
                );
                prayers.add(prayer);
                totalPrayersCount++;
            } else {
                Log.d("CampaignDetailActivity", "Donation #" + donationIndex + ": Prayer is NULL or EMPTY.");
            }
            donationIndex++;
        }

        txtPrayersCount.setText(String.valueOf(totalPrayersCount));
        Log.i("CampaignDetailActivity", "Total prayers found: " + totalPrayersCount); 

        List<Prayer> prayersToShow = prayers.size() > MAX_PRAYERS_SHOWN ?
                prayers.subList(0, MAX_PRAYERS_SHOWN) : prayers;

        prayerAdapter.updatePrayers(prayersToShow);

        if (prayers.size() > MAX_PRAYERS_SHOWN) {
            txtViewMorePrayers.setVisibility(View.VISIBLE);
        } else {
            txtViewMorePrayers.setVisibility(View.GONE);
        }
    }

    private void showAllPrayers() {
        if (campaign == null || campaign.getDonations() == null) {
            return;
        }

        List<Prayer> allPrayers = new ArrayList<>();

        for (DonationRequest donation : campaign.getDonations()) {
            if (donation.getDoa() != null && !donation.getDoa().trim().isEmpty()) {
                String donorName = "Orang Baik";
                String donorPhotoUrl = null;
                if (donation.getUser() != null) {
                    donorName = donation.getUser().getName();
                    donorPhotoUrl = donation.getUser().getPhotoUrl();
                }

                Prayer prayer = new Prayer(
                        donation.getDoa(),
                        donation.getUserId(),
                        donation.isAnonymous(),
                        donation.getCreatedAt(),
                        donation.getAmount(),
                        donorName,
                        donorPhotoUrl
                );
                allPrayers.add(prayer);
            }
        }

        prayerAdapter.updatePrayers(allPrayers);

        txtViewMorePrayers.setVisibility(View.GONE);

        Toast.makeText(this, "Menampilkan semua " + allPrayers.size() + " doa", Toast.LENGTH_SHORT).show();
    }

    private void populateViews() {
        if (campaign == null) return;

        try {
            if (txtCampaignTitle != null) {
                txtCampaignTitle.setText(campaign.getTitle());
            }

            loadCampaignImage();

            long collected = campaign.getCollectedAmount();
            long target = campaign.getTargetAmount();
            int progressPercentage = target > 0 ? (int) ((collected * 100) / target) : 0;
            progressBarDetail.setProgress(Math.min(progressPercentage, 100));

            String collectedText = formatCurrency(collected);
            txtCollectedAmount.setText(collectedText);

            String targetText = formatCurrency(collected) + " dari target " + formatCurrency(target);
            txtTargetAmount.setText(targetText);

            txtDonorsCount.setText(formatNumber(campaign.getDonorCount()) + " Donasi");

            if (campaign.getDescription() != null && !campaign.getDescription().isEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fullDescriptionText = Html.fromHtml(campaign.getDescription(), Html.FROM_HTML_MODE_COMPACT).toString();
                } else {
                    fullDescriptionText = Html.fromHtml(campaign.getDescription()).toString();
                }
            } else {
                fullDescriptionText = "Deskripsi kampanye tidak tersedia.";
            }

            updateDescriptionView();

        } catch (Exception e) {
            Toast.makeText(this, "Error menampilkan data kampanye", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void loadCampaignImage() {
        try {
            String imageUrl = campaign.getImageUrl();
            if (imageUrl != null && !imageUrl.isEmpty() && !imageUrl.equals("null")) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.placeholder_campaign)
                        .error(R.drawable.placeholder_campaign)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(imgCampaignDetail);
            } else {
                imgCampaignDetail.setImageResource(R.drawable.placeholder_campaign);
            }
        } catch (Exception e) {
            imgCampaignDetail.setImageResource(R.drawable.placeholder_campaign);
            e.printStackTrace();
        }
    }

    private void setupDonationDialog() {
        donationDialog = new BottomSheetDialog(this, R.style.BottomSheetDialogTheme);
        View dialogView = getLayoutInflater().inflate(R.layout.donation_dialog_layout, null);
        donationDialog.setContentView(dialogView);
        if (donationDialog != null && donationDialog.getWindow() != null) {
            donationDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        }

        donationOption5000 = dialogView.findViewById(R.id.donationOption5000);
        donationOption15000 = dialogView.findViewById(R.id.donationOption15000);
        donationOption25000 = dialogView.findViewById(R.id.donationOption25000);
        donationOption50000 = dialogView.findViewById(R.id.donationOption50000);
        customAmountSection = dialogView.findViewById(R.id.customAmountSection);
        etCustomAmount = dialogView.findViewById(R.id.etCustomAmount);
        btnContinuePayment = dialogView.findViewById(R.id.btnContinuePayment);

        setupDonationDialogListeners();
    }

    private void setupDonationDialogListeners() {
        donationOption5000.setOnClickListener(v -> selectDonationAmount(5000, true));
        donationOption15000.setOnClickListener(v -> selectDonationAmount(15000, true));
        donationOption25000.setOnClickListener(v -> selectDonationAmount(25000, true));
        donationOption50000.setOnClickListener(v -> selectDonationAmount(50000, true));

        etCustomAmount.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String cleanString = s.toString().replaceAll("[.,]", "");

                if (!TextUtils.isEmpty(cleanString)) {
                    try {
                        long amount = Long.parseLong(cleanString);
                        selectDonationAmount(amount, false);
                        clearPresetSelections();
                    } catch (NumberFormatException e) {
                        selectedDonationAmount = 0;
                        updateContinueButton();
                    }
                } else {
                    selectedDonationAmount = 0;
                    updateContinueButton();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    etCustomAmount.removeTextChangedListener(this);

                    String cleanString = s.toString().replaceAll("[Rp,.\\s]", "");

                    if (!cleanString.isEmpty()) {
                        try {
                            long parsed = Long.parseLong(cleanString);
                            NumberFormat formatter = NumberFormat.getInstance(new Locale("id", "ID"));
                            String formatted = formatter.format(parsed);

                            current = formatted;
                            etCustomAmount.setText(formatted);
                            etCustomAmount.setSelection(formatted.length());
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }

                    etCustomAmount.addTextChangedListener(this);
                }
            }
        });

        btnContinuePayment.setOnClickListener(v -> {
            if (selectedDonationAmount >= 1000) {
                proceedToPayment();
            } else {
                Toast.makeText(this, "Minimal donasi sebesar Rp1.000", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectDonationAmount(long amount, boolean go) {
        selectedDonationAmount = amount;
        if (go) {
            proceedToPayment();
            return;
        }

        updateContinueButton();
    }

    private void clearPresetSelections() {
        //TODO
    }

    private void updateContinueButton() {
        if (selectedDonationAmount >= 1000) {
            btnContinuePayment.setText("Lanjut pembayaran");
            btnContinuePayment.setEnabled(true);
            btnContinuePayment.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.primary_color)
            );
        } else {
            btnContinuePayment.setText("Lanjut pembayaran");
            btnContinuePayment.setEnabled(false);
            btnContinuePayment.setBackgroundTintList(
                    ContextCompat.getColorStateList(this, R.color.text_hint)
            );
        }
    }

    private void proceedToPayment() {
        donationDialog.dismiss();

        Intent paymentIntent = new Intent(this, PembayaranActivity.class);
        paymentIntent.putExtra("campaign", campaign);
        paymentIntent.putExtra("donation_amount", selectedDonationAmount);
        startActivity(paymentIntent);
    }

    private String formatCurrency(long amount) {
        try {
            if (amount >= 1_000_000_000) {
                return String.format(Locale.getDefault(), "Rp %.1f M", amount / 1_000_000_000.0);
            } else if (amount >= 1_000_000) {
                return String.format(Locale.getDefault(), "Rp %.1f Jt", amount / 1_000_000.0);
            } else if (amount >= 1_000) {
                return String.format(Locale.getDefault(), "Rp %.1f K", amount / 1_000.0);
            } else {
                return String.format(Locale.getDefault(), "Rp %,d", amount);
            }
        } catch (Exception e) {
            return "Rp 0";
        }
    }

    private void setupClickListeners() {
        btnDonate.setOnClickListener(v -> {
            if (campaign != null) {
                selectedDonationAmount = 0;
                etCustomAmount.setText("");
                updateContinueButton();
                donationDialog.show();
            } else {
                Toast.makeText(this, "Error: Data kampanye tidak tersedia", Toast.LENGTH_SHORT).show();
            }
        });

        if (txtReadMore != null) {
            txtReadMore.setOnClickListener(v -> {
                isDescriptionExpanded = !isDescriptionExpanded;
                updateDescriptionView();
            });
        }
    }

    private void updateDescriptionView() {
        if (fullDescriptionText.length() <= DESCRIPTION_COLLAPSED_LENGTH) {
            txtDescriptionDetail.setText(fullDescriptionText);
            txtReadMore.setVisibility(View.GONE);
        } else {
            txtReadMore.setVisibility(View.VISIBLE);
            if (isDescriptionExpanded) {
                txtDescriptionDetail.setText(fullDescriptionText);
                txtReadMore.setText("Baca Lebih Sedikit");
            } else {
                String truncatedText = fullDescriptionText.substring(0, DESCRIPTION_COLLAPSED_LENGTH) + "...";
                txtDescriptionDetail.setText(truncatedText);
                txtReadMore.setText("Baca Selengkapnya");
            }
        }
    }

    private String formatNumber(int number) {
        try {
            if (number >= 1_000_000) {
                return String.format(Locale.getDefault(), "%.1fM", number / 1_000_000.0);
            } else if (number >= 1_000) {
                return String.format(Locale.getDefault(), "%.1fK", number / 1_000.0);
            } else {
                return String.valueOf(number);
            }
        } catch (Exception e) {
            return "0";
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
        try {
            Glide.with(this).clear(imgCampaignDetail);
            if (donationDialog != null && donationDialog.isShowing()) {
                donationDialog.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}