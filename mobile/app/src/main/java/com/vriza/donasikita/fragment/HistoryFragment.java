package com.vriza.donasikita.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vriza.donasikita.R;
import com.vriza.donasikita.activities.IntruksiPembayaranActivity;
import com.vriza.donasikita.activities.ProfileActivity;
import com.vriza.donasikita.activities.StatusPembayaranActivity;
import com.vriza.donasikita.adapters.DonationHistoryAdapter;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.responses.DonationDetailResponse;
import com.vriza.donasikita.network.responses.DonationHistoryResponse;
import com.vriza.donasikita.network.responses.DonationResponse;
import com.vriza.donasikita.utils.CustomLoadingDialog;
import com.vriza.donasikita.utils.SessionManager;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment {
    private static final String TAG = "HistoryFragment";

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private DonationHistoryAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutEmptyState;
    private EditText etSearch;
    private ImageView ivProfile, ivNotification;
    private TextView tvSummary, tvTotalDonations;

    private List<DonationResponse.DonationDetails> donationList = new ArrayList<>();
    private List<DonationResponse.DonationDetails> filteredList = new ArrayList<>();
    private SessionManager sessionManager;
    private FirebaseAuth mAuth;
    private CustomLoadingDialog loadingDialog;

    public HistoryFragment() {
        // Required empty public constructor
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(requireContext());
        loadingDialog = new CustomLoadingDialog(requireContext());
        setupToolbar();
        setupRecyclerView();
        setupSearchFunctionality();
        loadUserProfile();
        setupProfileClickListener();
        loadDonationHistory();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        recyclerView = view.findViewById(R.id.recyclerViewHistory);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        etSearch = view.findViewById(R.id.etSearch);
        ivProfile = view.findViewById(R.id.ivProfile);
        ivNotification = view.findViewById(R.id.ivNotification);
        tvSummary = view.findViewById(R.id.tvSummary);
        tvTotalDonations = view.findViewById(R.id.tvTotalDonations);

        swipeRefreshLayout.setOnRefreshListener(this::loadDonationHistory);
        swipeRefreshLayout.setColorSchemeResources(R.color.cerulean_50);
    }

    private void setupToolbar() {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Riwayat Donasi");
            }
        }
    }

    private void setupRecyclerView() {
        adapter = new DonationHistoryAdapter(filteredList, requireContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(donation -> {
            fetchDonationDetails(donation.getOrderId());
        });
    }

    private void setupSearchFunctionality() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterDonations(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && getContext() != null) {
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(getContext())
                        .load(currentUser.getPhotoUrl())
                        .placeholder(R.drawable.ic_person)
                        .error(R.drawable.ic_person)
                        .circleCrop()
                        .into(ivProfile);
            } else {
                ivProfile.setImageResource(R.drawable.ic_person);
            }
        }
    }

    private void setupProfileClickListener() {
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void filterDonations(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(donationList);
        } else {
            for (DonationResponse.DonationDetails donation : donationList) {
                if (donation.getCampaign() != null &&
                        donation.getCampaign().getTitle().toLowerCase().contains(query.toLowerCase())) {
                    filteredList.add(donation);
                }
            }
        }
        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void loadDonationHistory() {
        swipeRefreshLayout.setRefreshing(true);

        String token = sessionManager.getApiToken();
        if (token == null || token.isEmpty()) {
            Toast.makeText(getContext(), "Token tidak ditemukan, silakan login kembali", Toast.LENGTH_SHORT).show();
            swipeRefreshLayout.setRefreshing(false);
            return;
        }

        Call<DonationHistoryResponse> call = ApiClient.getApiService().getDonationHistory("Bearer " + token);
        call.enqueue(new Callback<DonationHistoryResponse>() {
            @Override
            public void onResponse(Call<DonationHistoryResponse> call, Response<DonationHistoryResponse> response) {
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    DonationHistoryResponse historyResponse = response.body();
                    if (historyResponse.isSuccess()) {
                        donationList.clear();
                        donationList.addAll(historyResponse.getData());
                        filteredList.clear();
                        filteredList.addAll(donationList);
                        adapter.notifyDataSetChanged();

                        updateSummary();
                        updateEmptyState();

                        Log.d(TAG, "Loaded " + donationList.size() + " donations");
                    } else {
                        Toast.makeText(getContext(), historyResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                    }
                } else {
                    if (response.code() == 401) {
                        Toast.makeText(getContext(), "Sesi telah berakhir, silakan login kembali", Toast.LENGTH_SHORT).show();
                        sessionManager.logoutUser();
                    } else {
                        Toast.makeText(getContext(), "Gagal memuat riwayat donasi", Toast.LENGTH_SHORT).show();
                    }
                    Log.e(TAG, "Response not successful: " + response.code());
                    updateEmptyState();
                }
            }

            @Override
            public void onFailure(Call<DonationHistoryResponse> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "Gagal memuat data: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "API call failed", t);
                updateEmptyState();
            }
        });
    }

    private void fetchDonationDetails(String orderId) {
        String cleanOrderId = orderId.startsWith("#") ? orderId.substring(1) : orderId;

        Log.d(TAG, "Fetching donation details for order ID: " + cleanOrderId);

        loadingDialog.show("Memuat detail donasi...");

        Call<DonationDetailResponse> call = ApiClient.getApiService().getDonationByOrderId(cleanOrderId);
        call.enqueue(new Callback<DonationDetailResponse>() {
            @Override
            public void onResponse(Call<DonationDetailResponse> call, Response<DonationDetailResponse> response) {
                loadingDialog.hide();

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

                        Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "API Error: " + errorMsg);
                    }
                } else {
                    String errorMsg = "HTTP Error " + response.code();
                    if (response.code() == 404) {
                        errorMsg = "Donasi tidak ditemukan. Periksa kembali ID donasi.";
                    }

                    Toast.makeText(getContext(), errorMsg, Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "HTTP Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DonationDetailResponse> call, Throwable t) {
                loadingDialog.hide();

                Log.e(TAG, "Network error fetching donation details", t);

                String errorMessage = "Koneksi bermasalah";
                if (t.getMessage() != null) {
                    errorMessage += ": " + t.getMessage();
                }

                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToStatusActivity(DonationResponse.DonationDetails donationDetails) {
        Intent intent = new Intent(getActivity(), StatusPembayaranActivity.class);

        intent.putExtra("ORDER_ID", donationDetails.getOrderId());
        intent.putExtra("DONATION_DETAILS", donationDetails);

        if (donationDetails.getCampaign() != null) {
            Campaign campaign = Campaign.fromCampaignDetails(donationDetails.getCampaign());
            intent.putExtra("CAMPAIGN", campaign);
            Log.d(TAG, "Converted and passed Campaign object");
        } else {
            Log.w(TAG, "No campaign details available in donation");
        }

        try {
            String amount = donationDetails.getAmount().replace(".00", "");
            long donationAmount = Long.parseLong(amount);
            intent.putExtra("DONATION_AMOUNT", donationAmount);
        } catch (Exception e) {
            Log.e(TAG, "Error parsing donation amount: " + donationDetails.getAmount());
            intent.putExtra("DONATION_AMOUNT", 0L);
        }

        if (donationDetails.getPaymentMethod() != null) {
            intent.putExtra("PAYMENT_METHOD_NAME", donationDetails.getPaymentMethod());
        }

        if (donationDetails.getExpiredAt() != null) {
            intent.putExtra("EXPIRED_AT", donationDetails.getExpiredAt());

            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                java.util.Date expiredDate = sdf.parse(donationDetails.getExpiredAt());
                if (expiredDate != null) {
                    long expiredTime = expiredDate.getTime() / 1000; 
                    intent.putExtra("EXPIRED_TIME", expiredTime);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing expired date: " + donationDetails.getExpiredAt());
                intent.putExtra("EXPIRED_TIME", 0L);
            }
        }

        startActivity(intent);
    }

    private void updateSummary() {
        if (donationList.isEmpty()) {
            tvSummary.setText("Tidak ada donasi");
            tvTotalDonations.setText("0 Donasi");
            return;
        }

        long totalAmount = 0;
        for (DonationResponse.DonationDetails donation : donationList) {
            try {
                String amount = donation.getAmount().replace(".00", "");
                totalAmount += Long.parseLong(amount);
            } catch (Exception e) {
                Log.e(TAG, "Error parsing amount: " + donation.getAmount());
            }
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formattedAmount = formatter.format(totalAmount).replace("IDR", "Rp");

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMM yyyy", new Locale("id", "ID"));
        String monthYear = monthYearFormat.format(calendar.getTime());

        tvSummary.setText(monthYear + " • " + formattedAmount);
        long totalDonasi = 0;
        for (DonationResponse.DonationDetails donation : donationList) {
            try {
                if (donation.getStatus().equals("success")) {
                    totalDonasi++;
                }
            } catch (Exception e) {
                Log.e(TAG, "Error parsing amount: " + donation.getAmount());
            }
        }
        tvTotalDonations.setText(totalDonasi + " Donasi");
    }

    private void updateEmptyState() {
        if (filteredList.isEmpty()) {
            layoutEmptyState.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            layoutEmptyState.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
        loadDonationHistory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.hide();
        }
    }
}