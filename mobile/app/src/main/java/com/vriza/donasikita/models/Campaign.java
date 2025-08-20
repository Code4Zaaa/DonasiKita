package com.vriza.donasikita.models;

import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.annotations.SerializedName;
import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.responses.DonationResponse;

import java.util.ArrayList;
import java.util.List;

public class Campaign implements Parcelable {
    @SerializedName("id")
    private String id;

    @SerializedName("title")
    private String title;

    @SerializedName("slug")
    private String slug;

    @SerializedName("thumbnail")
    private String thumbnail;

    @SerializedName("target_donation")
    private String targetDonation;

    @SerializedName("current_donation")
    private String currentDonation;

    @SerializedName("category_id")
    private int categoryId;

    @SerializedName("category")
    private Category category;

    @SerializedName("description")
    private String description;

    @SerializedName("days_left")
    private int daysLeft;

    @SerializedName("donor_count")
    private int donorCount;

    @SerializedName("is_recommendation")
    private int isRecommendation;

    @SerializedName("donations")
    private List<DonationRequest> donations;


    public Campaign() {}

    public Campaign(String id , int categoryId, String title, String slug, String description, String thumbnail,
                    long targetAmount, long collectedAmount, String categoryName, int daysLeft, int donorCount, int isRecommendation) {
        this.id = id;
        this.categoryId = categoryId;
        this.title = title;
        this.slug = slug;
        this.description = description;
        this.thumbnail = thumbnail;
        this.targetDonation = String.valueOf(targetAmount);
        this.currentDonation = String.valueOf(collectedAmount);
        this.daysLeft = daysLeft;
        this.donorCount = donorCount;
        this.isRecommendation = isRecommendation;
        this.donations = new ArrayList<>();

        this.category = new Category();
        this.category.setName(categoryName);
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }

    public String getThumbnail() { return thumbnail; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }

    public String getTargetDonation() { return targetDonation; }
    public void setTargetDonation(String targetDonation) { this.targetDonation = targetDonation; }

    public String getCurrentDonation() { return currentDonation; }
    public void setCurrentDonation(String currentDonation) { this.currentDonation = currentDonation; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getDaysLeft() { return daysLeft; }
    public void setDaysLeft(int daysLeft) { this.daysLeft = daysLeft; }

    public int getDonorCount() { return donorCount; }
    public void setDonorCount(int donorCount) { this.donorCount = donorCount; }

    public List<DonationRequest> getDonations() {
        return donations != null ? donations : new ArrayList<>();
    }

    public void setDonations(List<DonationRequest> donations) {
        this.donations = donations != null ? donations : new ArrayList<>();
    }
    public long getTargetAmount() {
        try {
            return Long.parseLong(targetDonation.replace(".00", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public long getCollectedAmount() {
        try {
            return Long.parseLong(currentDonation.replace(".00", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getCategoryName() {
        return category != null ? category.getName() : "";
    }

    public String getImageUrl() {
        return thumbnail != null ? ApiClient.getImageUrl() + thumbnail : "";
    }

    private void setImageUrl(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public boolean isRecommendation() {
        return isRecommendation == 1;
    }

    public void setRecommendation(int recommendation) {
        isRecommendation = recommendation;
    }

    public static Campaign fromCampaignDetails(DonationResponse.CampaignDetails campaignDetails) {
        if (campaignDetails == null) return null;

        Campaign campaign = new Campaign();
        campaign.setId(String.valueOf(campaignDetails.getId()));
        campaign.setTitle(campaignDetails.getTitle());
        campaign.setDescription(campaignDetails.getDescription());
        campaign.setThumbnail(campaignDetails.getThumbnail());
        campaign.setSlug(campaignDetails.getSlug());
        campaign.setCategoryId(campaignDetails.getCategoryId());

        try {
            if (campaignDetails.getTargetDonation() != null) {
                campaign.setTargetDonation(campaignDetails.getTargetDonation());
            }
            if (campaignDetails.getCurrentDonation() != null) {
                campaign.setCurrentDonation(campaignDetails.getCurrentDonation());
            }
        } catch (Exception e) {
            Log.e("CampaignConverter", "Error parsing amounts", e);
            campaign.setTargetDonation("0");
            campaign.setCurrentDonation("0");
        }

        try {
            if (campaignDetails.getDeadline() != null) {
                campaign.setDaysLeft(0);
            }
        } catch (Exception e) {
            Log.e("CampaignConverter", "Error parsing deadline", e);
            campaign.setDaysLeft(0);
        }

        campaign.setDonorCount(0);

        campaign.setRecommendation(campaignDetails.getIsRecommendation());

        return campaign;
    }



    protected Campaign(Parcel in) {
        id = in.readString();
        title = in.readString();
        slug = in.readString();
        thumbnail = in.readString();
        targetDonation = in.readString();
        currentDonation = in.readString();
        categoryId = in.readInt();
        category = in.readParcelable(Category.class.getClassLoader());
        description = in.readString();
        daysLeft = in.readInt();
        donorCount = in.readInt();

        isRecommendation = in.readInt();
        donations = in.createTypedArrayList(DonationRequest.CREATOR);

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(slug);
        dest.writeString(thumbnail);
        dest.writeString(targetDonation);
        dest.writeString(currentDonation);
        dest.writeInt(categoryId);
        dest.writeParcelable(category, flags);
        dest.writeString(description);
        dest.writeInt(daysLeft);
        dest.writeInt(donorCount);

        dest.writeInt(isRecommendation);
        dest.writeTypedList(donations);

    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Campaign> CREATOR = new Creator<Campaign>() {
        @Override
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }

        @Override
        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };
}