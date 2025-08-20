package com.vriza.donasikita.utils;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.responses.DonationDetailResponse;
import com.vriza.donasikita.network.responses.DonationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentStatusChecker {
    private static final String TAG = "PaymentStatusChecker";
    private static final long CHECK_INTERVAL = 10000;

    private Handler handler;
    private Runnable statusCheckRunnable;
    private String orderId;
    private PaymentStatusListener listener;
    private boolean isChecking = false;
    private String lastKnownStatus = "";

    public interface PaymentStatusListener {
        void onStatusChanged(String newStatus, DonationResponse.DonationDetails donationData);
        void onError(String error);
    }

    public PaymentStatusChecker(String orderId, PaymentStatusListener listener) {
        this.orderId = orderId;
        this.listener = listener;
        this.handler = new Handler(Looper.getMainLooper());
    }

    public void startChecking() {
        if (isChecking) {
            return;
        }

        isChecking = true;
        statusCheckRunnable = new Runnable() {
            @Override
            public void run() {
                if (isChecking) {
                    checkPaymentStatus();
                    handler.postDelayed(this, CHECK_INTERVAL);
                }
            }
        };

        handler.post(statusCheckRunnable);

        Log.d(TAG, "Started checking payment status for order: " + orderId);
    }

    public void stopChecking() {
        isChecking = false;
        if (handler != null && statusCheckRunnable != null) {
            handler.removeCallbacks(statusCheckRunnable);
        }
        Log.d(TAG, "Stopped checking payment status");
    }

    private void checkPaymentStatus() {
        if (orderId == null || orderId.isEmpty()) {
            if (listener != null) {
                listener.onError("Order ID tidak valid");
            }
            return;
        }

        String cleanOrderId = orderId.startsWith("#") ? orderId.substring(1) : orderId;

        Log.d(TAG, "Checking payment status for order ID: " + cleanOrderId);

        Call<DonationDetailResponse> call = ApiClient.getApiService().getDonationByOrderId(cleanOrderId);
        call.enqueue(new Callback<DonationDetailResponse>() {
            @Override
            public void onResponse(Call<DonationDetailResponse> call, Response<DonationDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DonationDetailResponse donationResponse = response.body();
                    if (donationResponse.isSuccess() && donationResponse.getData() != null) {
                        DonationResponse.DonationDetails donationData = donationResponse.getData();
                        String currentStatus = donationData.getStatus();

                        if (!currentStatus.equals(lastKnownStatus) || lastKnownStatus.isEmpty()) {
                            lastKnownStatus = currentStatus;
                            if (listener != null) {
                                listener.onStatusChanged(currentStatus, donationData);
                            }
                        }

                        if (!donationData.isPending()) {
                            stopChecking();
                        }

                    } else {
                        Log.e(TAG, "Invalid response structure or empty data");
                        if (listener != null) {
                            listener.onError("Data donasi tidak ditemukan dari response.");
                        }
                    }
                } else {
                    Log.e(TAG, "Failed to check payment status. Response code: " + response.code());
                    if (response.code() == 404) {
                        if (listener != null) {
                            listener.onError("Donasi tidak ditemukan. Periksa kembali ID donasi.");
                        }
                    } else {
                        if (listener != null) {
                            listener.onError("Gagal mengecek status pembayaran (HTTP " + response.code() + ")");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DonationDetailResponse> call, Throwable t) {
                Log.e(TAG, "Network error checking payment status", t);
                if (listener != null) {
                    String errorMessage = "Koneksi bermasalah";
                    if (t.getMessage() != null) {
                        errorMessage += ": " + t.getMessage();
                    }
                    listener.onError(errorMessage);
                }
            }
        });
    }

    public static void checkOnce(String orderId, PaymentStatusListener listener) {
        if (orderId == null || orderId.isEmpty()) {
            if (listener != null) {
                listener.onError("Order ID tidak valid");
            }
            return;
        }

        String cleanOrderId = orderId.startsWith("#") ? orderId.substring(1) : orderId;

        Call<DonationDetailResponse> call = ApiClient.getApiService().getDonationByOrderId(cleanOrderId);
        call.enqueue(new Callback<DonationDetailResponse>() {
            @Override
            public void onResponse(Call<DonationDetailResponse> call, Response<DonationDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    DonationDetailResponse donationResponse = response.body();
                    if (donationResponse.isSuccess() && donationResponse.getData() != null) {
                        DonationResponse.DonationDetails donationData = donationResponse.getData();
                        String currentStatus = donationData.getStatus();

                        if (listener != null) {
                            listener.onStatusChanged(currentStatus, donationData);
                        }
                    } else {
                        Log.e(TAG, "Single check - Invalid response structure");
                        if (listener != null) {
                            listener.onError("Data donasi tidak ditemukan");
                        }
                    }
                } else {
                    Log.e(TAG, "Single check failed. Response code: " + response.code());
                    if (response.code() == 404) {
                        if (listener != null) {
                            listener.onError("Donasi tidak ditemukan. Periksa kembali ID donasi.");
                        }
                    } else {
                        if (listener != null) {
                            listener.onError("Gagal mengecek status pembayaran");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<DonationDetailResponse> call, Throwable t) {
                Log.e(TAG, "Single check network error", t);
                if (listener != null) {
                    String errorMessage = "Koneksi bermasalah";
                    if (t.getMessage() != null) {
                        errorMessage += ": " + t.getMessage();
                    }
                    listener.onError(errorMessage);
                }
            }
        });
    }
}