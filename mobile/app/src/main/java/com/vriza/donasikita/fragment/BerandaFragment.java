package com.vriza.donasikita.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vriza.donasikita.R;
import com.vriza.donasikita.activities.CampaignDetailActivity;
import com.vriza.donasikita.activities.ProfileActivity;
import com.vriza.donasikita.adapters.CampaignAdapter;
import com.vriza.donasikita.adapters.CategoryAdapter;
import com.vriza.donasikita.models.Campaign;
import com.vriza.donasikita.models.Category;
import com.vriza.donasikita.repository.CampaignRepository;
import com.vriza.donasikita.repository.CategoryRepository;
import com.vriza.donasikita.utils.DataCache;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BerandaFragment extends Fragment implements
        CampaignAdapter.OnCampaignClickListener,
        CategoryAdapter.OnCategoryClickListener {

    private static final String TAG = "BerandaFragment";

    private Toolbar toolbar;
    private SearchView searchView;
    private RecyclerView recyclerViewCategories, recyclerViewCampaigns;
    private ProgressBar progressBar;
    private ImageView ivProfile;
    private NestedScrollView nestedScrollView;
    private LinearLayout layoutError;
    private MaterialButton btnRetry;

    private CategoryAdapter categoryAdapter;
    private CampaignAdapter campaignAdapter;
    private final List<Category> categoryList = new ArrayList<>();
    private final List<Category> displayedCategoryList = new ArrayList<>();
    private final List<Campaign> allCampaignList = new ArrayList<>();
    private final List<Campaign> filteredCampaignList = new ArrayList<>();

    private CategoryRepository categoryRepository;
    private CampaignRepository campaignRepository;
    private FirebaseAuth mAuth;

    private String currentQuery = "";
    private int selectedCategoryId = 1;
    private boolean isCategoriesLoaded = false;
    private boolean isCampaignsLoaded = false;

    public BerandaFragment() {

    }

    public static BerandaFragment newInstance() {
        return new BerandaFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_beranda, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        mAuth = FirebaseAuth.getInstance();
        initRepositories();
        setupToolbar();
        loadUserProfile();
        setupProfileClickListener();
        setupRetryButton();
        setupRecyclerViews();
        setupSearchView();
        loadDataFromCacheOrServer();
    }

    private void initViews(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        recyclerViewCategories = view.findViewById(R.id.recyclerViewCategories);
        recyclerViewCampaigns = view.findViewById(R.id.recyclerViewCampaigns);
        progressBar = view.findViewById(R.id.progressBar);
        ivProfile = view.findViewById(R.id.ivProfile);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        layoutError = view.findViewById(R.id.layoutError);
        btnRetry = view.findViewById(R.id.btnRetry);
    }

    private void initRepositories() {
        categoryRepository = new CategoryRepository();
        campaignRepository = new CampaignRepository();
    }

    private void setupToolbar() {
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null) {
                ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.app_name);
            }
        }
    }

    private void setupProfileClickListener() {
        ivProfile.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            startActivity(intent);
        });
    }

    private void setupRetryButton() {
        btnRetry.setOnClickListener(v -> {
            isCategoriesLoaded = false;
            isCampaignsLoaded = false;
            showError(false);
            showLoading(true);
            DataCache.getInstance().clearCache();
            loadDataFromServer();
        });
    }

    private void setupRecyclerViews() {
        categoryAdapter = new CategoryAdapter(getContext(), displayedCategoryList, this);
        recyclerViewCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCategories.setAdapter(categoryAdapter);

        campaignAdapter = new CampaignAdapter(filteredCampaignList, this, true);
        recyclerViewCampaigns.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerViewCampaigns.setAdapter(campaignAdapter);
    }

    private void setupSearchView() {
        if (searchView != null) {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    currentQuery = newText;
                    applyFilters();
                    return true;
                }
            });
        }
    }

    private void loadDataFromCacheOrServer() {
        DataCache dataCache = DataCache.getInstance();
        if (dataCache.hasAllData()) {
            loadDataFromCache();
        } else {
            showLoading(true);
            showError(false);
            loadDataFromServer();
        }
    }

    private void loadDataFromCache() {
        DataCache dataCache = DataCache.getInstance();

        categoryList.clear();
        categoryList.addAll(dataCache.getCategories());
        updateDisplayedCategories();

        allCampaignList.clear();
        allCampaignList.addAll(dataCache.getAllCampaigns());
        applyFilters();

        showLoading(false);
        showError(false);
        showContent(true);
    }

    private void loadDataFromServer() {
        isCategoriesLoaded = false;
        isCampaignsLoaded = false;
        loadCategories();
        loadAllCampaigns();
    }

    private void loadCategories() {
        categoryRepository.getCategories(new CategoryRepository.CategoryCallback() {
            @Override
            public void onSuccess(List<Category> categories) {
                DataCache.getInstance().setCategories(categories);
                categoryList.clear();
                categoryList.addAll(categories);
                updateDisplayedCategories();
                isCategoriesLoaded = true;
                checkLoadingComplete();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading categories: " + errorMessage);
                isCategoriesLoaded = true; 
                checkLoadingComplete();
            }
        });
    }

    private void updateDisplayedCategories() {
        displayedCategoryList.clear();
        int limit = 3;
        if (categoryList.size() > limit) {
            for (int i = 0; i < limit; i++) {
                displayedCategoryList.add(categoryList.get(i));
            }
            displayedCategoryList.add(new Category(-1, "categories/other.png", "Lainnya", "lainnya"));
        } else {
            displayedCategoryList.addAll(categoryList);
        }
        categoryAdapter.notifyDataSetChanged();
    }

    private void loadAllCampaigns() {
        campaignRepository.getCampaigns(null, null, null, new CampaignRepository.CampaignCallback() {
            @Override
            public void onSuccess(List<Campaign> campaigns) {
                DataCache.getInstance().setAllCampaigns(campaigns);
                allCampaignList.clear();
                allCampaignList.addAll(campaigns);
                applyFilters();
                isCampaignsLoaded = true;
                checkLoadingComplete();
            }

            @Override
            public void onError(String errorMessage) {
                Log.e(TAG, "Error loading campaigns: " + errorMessage);
                isCampaignsLoaded = true;
                checkLoadingComplete();
            }
        });
    }

    private void checkLoadingComplete() {
        if (isCategoriesLoaded && isCampaignsLoaded) {
            if (allCampaignList.isEmpty() || categoryList.isEmpty()) {
                showLoading(false);
                showError(true);
                showContent(false);
            } else {
                showLoading(false);
                showError(false);
                showContent(true);
            }
        }
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

    private void applyFilters() {
        filteredCampaignList.clear();
        List<Campaign> tempCampaigns = new ArrayList<>(allCampaignList);

        if (selectedCategoryId != 0) {
            tempCampaigns = tempCampaigns.stream()
                    .filter(c -> c.getCategoryId() == selectedCategoryId)
                    .collect(Collectors.toList());
        }

        if (!TextUtils.isEmpty(currentQuery)) {
            String lowerCaseQuery = currentQuery.toLowerCase();
            tempCampaigns = tempCampaigns.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(lowerCaseQuery))
                    .collect(Collectors.toList());
        }

        filteredCampaignList.addAll(tempCampaigns);

        if (campaignAdapter != null) {
            campaignAdapter.notifyDataSetChanged();
        }
    }

    private void showLoading(boolean isLoading) {
        if (progressBar != null) {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void showError(boolean showError) {
        if (layoutError != null) {
            layoutError.setVisibility(showError ? View.VISIBLE : View.GONE);
        }
    }

    private void showContent(boolean showContent) {
        if (nestedScrollView != null) {
            nestedScrollView.setVisibility(showContent ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void onCampaignClick(Campaign campaign) {
        Intent intent = new Intent(getActivity(), CampaignDetailActivity.class);
        intent.putExtra("campaign", campaign);
        startActivity(intent);
    }

    @Override
    public void onCategoryClick(Category category) {
        if (getContext() == null) return;

        if (category.getId() == -1) {
            Toast.makeText(getContext(), "Membuka semua kategori...", Toast.LENGTH_SHORT).show();
            // Implementasi untuk membuka halaman semua kategori
        } else {
            selectCategory(category.getId());
        }
    }

    public void selectCategory(int categoryId) {
        selectedCategoryId = categoryId;
        if (categoryAdapter != null) {
            categoryAdapter.setSelectedCategory(categoryId);
        }
        applyFilters();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserProfile();
    }
}