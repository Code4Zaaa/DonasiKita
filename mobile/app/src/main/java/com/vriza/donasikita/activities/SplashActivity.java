package com.vriza.donasikita.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;

import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.models.Category;
import com.vriza.donasikita.models.PaymentChannel;
import com.vriza.donasikita.network.responses.PaymentChannelResponse;
import com.vriza.donasikita.repository.CampaignRepository;
import com.vriza.donasikita.repository.CategoryRepository;
import com.vriza.donasikita.repository.PaymentRepository;
import com.vriza.donasikita.utils.DataCache;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SplashActivity extends AppCompatActivity {
    private MotionLayout motionLayout;

    private CategoryRepository categoryRepository;
    private CampaignRepository campaignRepository;
    private PaymentRepository paymentRepository;
    private final AtomicInteger loadingTasksCompleted = new AtomicInteger(0);
    private final int TOTAL_LOADING_TASKS = 3;
    private boolean hasError = false;

    private boolean isDataLoadingComplete = false;
    private boolean isAnimationComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initViews();
        initRepositories();
        setupAnimationListener();
        startDataLoading();
    }

    private void initViews() {
        motionLayout = findViewById(R.id.motionLayout);
    }

    private void initRepositories() {
        categoryRepository = new CategoryRepository();
        campaignRepository = new CampaignRepository();
        paymentRepository = new PaymentRepository();
    }

    private void setupAnimationListener() {
        motionLayout.addTransitionListener(new MotionLayout.TransitionListener() {
            @Override
            public void onTransitionStarted(MotionLayout motionLayout, int startId, int endId) {}

            @Override
            public void onTransitionChange(MotionLayout motionLayout, int startId, int endId, float progress) {}

            @Override
            public void onTransitionCompleted(MotionLayout motionLayout, int currentId) {
                isAnimationComplete = true;
                checkAndNavigate();
            }

            @Override
            public void onTransitionTrigger(MotionLayout motionLayout, int triggerId, boolean positive, float progress) {}
        });
    }

    private void startDataLoading() {
        loadCategories();
        loadPopularCampaigns();
        loadAllCampaigns();
        loadAllPaymentMethod();
    }

    private void onTaskCompleted() {
        if (hasError) return;

        int completed = loadingTasksCompleted.incrementAndGet();

        if (completed == TOTAL_LOADING_TASKS) {
            isDataLoadingComplete = true;
            checkAndNavigate();
        }
    }

    private synchronized void checkAndNavigate() {
        if (isDataLoadingComplete && isAnimationComplete) {
            navigateToMainActivity();
        }
    }

    private void handleLoadingError(String errorMessage) {
        if (!hasError) {
            hasError = true;

            new Handler(Looper.getMainLooper()).postDelayed(this::navigateToMainActivity, 2000);
        }
    }

    private void navigateToMainActivity() {
        if (isFinishing()) return;

        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }


    private void loadCategories() {
        categoryRepository.getCategories(new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                DataCache.getInstance().setCategories(categories);
                runOnUiThread(() -> onTaskCompleted());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> handleLoadingError("Gagal memuat kategori: " + errorMessage));
            }
        });
    }

    private void loadPopularCampaigns() {
        campaignRepository.getCampaigns(true, null, null, new CampaignRepository.CampaignCallback() {
            @Override
            public void onSuccess(List<Campaign> campaigns) {
                DataCache.getInstance().setPopularCampaigns(campaigns);
                runOnUiThread(() -> onTaskCompleted());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> handleLoadingError("Gagal memuat kampanye populer: " + errorMessage));
            }
        });
    }

    private void loadAllCampaigns() {
        campaignRepository.getCampaigns(null, null, null, new CampaignRepository.CampaignCallback() {
            @Override
            public void onSuccess(List<Campaign> campaigns) {
                DataCache.getInstance().setAllCampaigns(campaigns);
                runOnUiThread(() -> onTaskCompleted());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> handleLoadingError("Gagal memuat semua kampanye: " + errorMessage));
            }
        });
    }

    private void loadAllPaymentMethod() {
        paymentRepository.getPaymentChannel(new PaymentRepository.PaymentChannelCallback() {
            @Override
            public void onSuccess(List<PaymentChannel> paymentChannels) {
                DataCache.getInstance().setPaymentChannels(paymentChannels);
                runOnUiThread(() -> onTaskCompleted());
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> handleLoadingError("Gagal memuat semua metode pembayaran: " + errorMessage));
            }
        });
    }
}