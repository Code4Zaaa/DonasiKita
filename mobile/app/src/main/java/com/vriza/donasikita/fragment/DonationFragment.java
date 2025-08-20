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
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vriza.donasikita.R;
import com.vriza.donasikita.activities.CampaignDetailActivity;
import com.vriza.donasikita.activities.ProfileActivity;
import com.vriza.donasikita.adapters.CampaignDonationAdapter;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.repository.CampaignRepository;
import com.vriza.donasikita.utils.DataCache;
import com.vriza.donasikita.utils.SessionManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DonationFragment extends Fragment implements CampaignDonationAdapter.OnCampaignClickListener {

    private static final String TAG = "DonationFragment";

    private LinearLayout btnSort;
    private RecyclerView rvCampaigns;
    private ProgressBar progressBar;
    private LinearLayout layoutError;
    private MaterialButton btnRetry;
    private EditText etSearch;
    private ImageView ivProfile, ivNotification;

    private List<Campaign> allCampaigns = new ArrayList<>();
    private List<Campaign> filteredCampaigns = new ArrayList<>();
    private CampaignDonationAdapter adapter;

    private CampaignRepository campaignRepository;

    private String currentQuery = "";
    private int currentSortType = 0; // 0: Default, 1: Newest, 2: Oldest, 3: Highest Amount, 4: Lowest Amount

    private SessionManager sessionManager;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_donation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(requireContext());
        setupRecyclerView();
        setupListeners();
        setupSearch();

        campaignRepository = new CampaignRepository();
        loadUserProfile();
        setupProfileClickListener();
        loadCampaigns();
    }

    private void initViews(View view) {
        btnSort = view.findViewById(R.id.btn_sort);
        rvCampaigns = view.findViewById(R.id.rv_campaigns);
        progressBar = view.findViewById(R.id.progress_bar);
        layoutError = view.findViewById(R.id.layout_error);
        btnRetry = view.findViewById(R.id.btn_retry);
        etSearch = view.findViewById(R.id.etSearch);
        ivProfile = view.findViewById(R.id.ivProfile);
        ivNotification = view.findViewById(R.id.ivNotification);
    }

    private void setupRecyclerView() {
        adapter = new CampaignDonationAdapter(filteredCampaigns, this);
        rvCampaigns.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCampaigns.setAdapter(adapter);
    }

    private void setupListeners() {
        btnSort.setOnClickListener(v -> showSortDialog());
        btnRetry.setOnClickListener(v -> loadCampaigns());

        ivProfile.setOnClickListener(v -> {
            // TODO
        });

        ivNotification.setOnClickListener(v -> {
            // TODO
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currentQuery = s.toString().trim();
                applyFilters();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO
            }
        });
    }

    private void loadCampaigns() {
        showLoading(true);
        showError(false);

        DataCache dataCache = DataCache.getInstance();
        if (dataCache.hasAllData()) {
            allCampaigns.clear();
            allCampaigns.addAll(dataCache.getAllCampaigns());
            applyFilters();
            showLoading(false);
            return;
        }

        campaignRepository.getCampaigns(null, null, null, new CampaignRepository.CampaignCallback() {
            @Override
            public void onSuccess(List<Campaign> campaigns) {
                if (getContext() == null) return;

                allCampaigns.clear();
                allCampaigns.addAll(campaigns);

                DataCache.getInstance().setAllCampaigns(campaigns);

                applyFilters();
                showLoading(false);
                showError(false);
            }

            @Override
            public void onError(String errorMessage) {
                if (getContext() == null) return; 

                Log.e(TAG, "Error loading campaigns: " + errorMessage);
                showLoading(false);
                showError(true);
            }
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

    private void applyFilters() {
        filteredCampaigns.clear();

        List<Campaign> tempList = new ArrayList<>(allCampaigns);

        if (!currentQuery.isEmpty()) {
            tempList = tempList.stream()
                    .filter(campaign -> campaign.getTitle().toLowerCase().contains(currentQuery.toLowerCase()) ||
                            campaign.getDescription().toLowerCase().contains(currentQuery.toLowerCase()))
                    .collect(Collectors.toList());
        }

        applySorting(tempList);

        filteredCampaigns.addAll(tempList);

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void applySorting(List<Campaign> campaigns) {
        switch (currentSortType) {
            case 1: // Newest - Sort by ID descending
                Collections.sort(campaigns, (c1, c2) -> c2.getId().compareTo(c1.getId()));
                break;
            case 2: // Oldest - Sort by ID ascending
                Collections.sort(campaigns, (c1, c2) -> c1.getId().compareTo(c2.getId()));
                break;
            case 3: // Highest Amount
                Collections.sort(campaigns, (c1, c2) -> Long.compare(c2.getCollectedAmount(), c1.getCollectedAmount()));
                break;
            case 4: // Lowest Amount
                Collections.sort(campaigns, (c1, c2) -> Long.compare(c1.getCollectedAmount(), c2.getCollectedAmount()));
                break;
            default: // Default sorting (by ID or original order)
                Collections.sort(campaigns, (c1, c2) -> c1.getId().compareTo(c2.getId()));
                break;
        }
    }

    private void showSortDialog() {
        if (getContext() == null) return;

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_sort, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        LinearLayout sortDefault = bottomSheetView.findViewById(R.id.sort_default);
        LinearLayout sortNewest = bottomSheetView.findViewById(R.id.sort_newest);
        LinearLayout sortOldest = bottomSheetView.findViewById(R.id.sort_oldest);
        LinearLayout sortHighestAmount = bottomSheetView.findViewById(R.id.sort_highest_amount);
        LinearLayout sortLowestAmount = bottomSheetView.findViewById(R.id.sort_lowest_amount);

        sortDefault.setOnClickListener(v -> {
            currentSortType = 0;
            applyFilters();
            bottomSheetDialog.dismiss();
        });

        sortNewest.setOnClickListener(v -> {
            currentSortType = 1;
            applyFilters();
            bottomSheetDialog.dismiss();
        });

        sortOldest.setOnClickListener(v -> {
            currentSortType = 2;
            applyFilters();
            bottomSheetDialog.dismiss();
        });

        sortHighestAmount.setOnClickListener(v -> {
            currentSortType = 3;
            applyFilters();
            bottomSheetDialog.dismiss();
        });

        sortLowestAmount.setOnClickListener(v -> {
            currentSortType = 4;
            applyFilters();
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.show();
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
        if (rvCampaigns != null) {
            rvCampaigns.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        }
    }

    private void showError(boolean showError) {
        if (layoutError != null) {
            layoutError.setVisibility(showError ? View.VISIBLE : View.GONE);
        }
        if (rvCampaigns != null) {
            rvCampaigns.setVisibility(showError ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onCampaignClick(Campaign campaign) {
        Intent intent = new Intent(getContext(), CampaignDetailActivity.class);
        intent.putExtra("campaign", campaign);
        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();

        if (allCampaigns.isEmpty()) {
            loadCampaigns();
        }
    }
}