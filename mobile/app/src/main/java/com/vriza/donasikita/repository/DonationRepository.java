package com.vriza.donasikita.repository;

import android.util.Log;

import com.vriza.donasikita.models.DonationRequest;
import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.ApiService;
import com.vriza.donasikita.network.responses.DonationResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DonationRepository {
    private static final String TAG = "DonationRepository";

    private ApiService apiService;

    public DonationRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public interface DonationCallback {
        void onSuccess(DonationResponse donationResponse);
        void onError(String errorMessage);
    }

    public void createDonation(DonationRequest donationRequest, DonationCallback callback) {
        Call<DonationResponse> call = apiService.createDonation(donationRequest);

        call.enqueue(new Callback<DonationResponse>() {
            @Override
            public void onResponse(Call<DonationResponse> call, Response<DonationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to create donation";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            errorMessage = "Error: " + response.code();
                        }
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<DonationResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    public void createDonationWithAuth(String authToken, DonationRequest donationRequest, DonationCallback callback) {
        Call<DonationResponse> call = apiService.createDonationWithAuth("Bearer " + authToken, donationRequest);

        call.enqueue(new Callback<DonationResponse>() {
            @Override
            public void onResponse(Call<DonationResponse> call, Response<DonationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    String errorMessage = "Failed to create donation";
                    if (response.errorBody() != null) {
                        try {
                            errorMessage = response.errorBody().string();
                        } catch (Exception e) {
                            errorMessage = "Error: " + response.code();
                        }
                    }
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(Call<DonationResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}