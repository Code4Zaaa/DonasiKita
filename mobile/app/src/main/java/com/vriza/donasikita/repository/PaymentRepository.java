package com.vriza.donasikita.repository;

import android.util.Log;
import androidx.annotation.NonNull;
import com.vriza.donasikita.models.PaymentChannel;
import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.ApiService;
import com.vriza.donasikita.network.responses.PaymentChannelResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentRepository {
    private static final String TAG = "PaymentRepository";
    private final ApiService apiService;

    public PaymentRepository() {
        this.apiService = ApiClient.getApiService();
    }

    public interface PaymentChannelCallback {
        void onSuccess(List<PaymentChannel> paymentChannels);
        void onError(String errorMessage);
    }

    public void getPaymentChannel(PaymentChannelCallback callback) {
        Call<PaymentChannelResponse> call = apiService.getPaymentChannels();
        call.enqueue(new Callback<PaymentChannelResponse>() {
            @Override
            public void onResponse(Call<PaymentChannelResponse> call, Response<PaymentChannelResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    PaymentChannelResponse paymentResponse = response.body();
                    if (paymentResponse.isSuccess() && paymentResponse.getData() != null) {
                        callback.onSuccess(paymentResponse.getData());
                    } else {
                        String message = paymentResponse.getMessage() != null ? paymentResponse.getMessage() : "Data metode pembayaran kosong";
                        callback.onError(message);
                    }
                } else {
                    callback.onError("Gagal memuat data. Kode: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<PaymentChannelResponse> call, Throwable t) {
                Log.e(TAG, "onFailure: ", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}