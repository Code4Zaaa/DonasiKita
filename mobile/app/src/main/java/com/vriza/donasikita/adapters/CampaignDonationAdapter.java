package com.vriza.donasikita.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Campaign;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CampaignDonationAdapter extends RecyclerView.Adapter<CampaignDonationAdapter.CampaignDonationViewHolder> {

    private final List<Campaign> campaignList;
    private final OnCampaignClickListener listener;
    private Context context;
    private final NumberFormat currencyFormatter;

    public interface OnCampaignClickListener {
        void onCampaignClick(Campaign campaign);
    }

    public CampaignDonationAdapter(List<Campaign> campaignList, OnCampaignClickListener listener) {
        this.campaignList = campaignList;
        this.listener = listener;
        this.currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        this.currencyFormatter.setMaximumFractionDigits(0); // Menghilangkan ,00
    }

    @NonNull
    @Override
    public CampaignDonationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_campaign_donation, parent, false);
        return new CampaignDonationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CampaignDonationViewHolder holder, int position) {
        Campaign campaign = campaignList.get(position);
        holder.bind(campaign);
    }

    @Override
    public int getItemCount() {
        return campaignList != null ? campaignList.size() : 0;
    }

    public class CampaignDonationViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivCampaignImage;
        private TextView tvRecommendationBadge;
        private TextView tvCampaignTitle;
        private ProgressBar progressBarCampaign;
        private TextView tvStatus;
        private LinearLayout layoutDaysRemaining;
        private TextView tvDaysRemaining;
        private TextView tvAmountRaised;

        public CampaignDonationViewHolder(@NonNull View itemView) {
            super(itemView);

            ivCampaignImage = itemView.findViewById(R.id.iv_campaign_image);
            tvRecommendationBadge = itemView.findViewById(R.id.tv_recommendation_badge);
            tvCampaignTitle = itemView.findViewById(R.id.tv_campaign_title);
            progressBarCampaign = itemView.findViewById(R.id.progress_bar_campaign);
            tvStatus = itemView.findViewById(R.id.tv_status);
            layoutDaysRemaining = itemView.findViewById(R.id.layout_days_remaining);
            tvDaysRemaining = itemView.findViewById(R.id.tv_days_remaining);
            tvAmountRaised = itemView.findViewById(R.id.tv_amount_raised);
        }

        void bind(Campaign campaign) {
            if (tvCampaignTitle != null) {
                tvCampaignTitle.setText(campaign.getTitle());
            }

            if (ivCampaignImage != null) {
                Glide.with(context)
                        .load(campaign.getImageUrl())
                        .placeholder(R.drawable.placeholder_campaign)
                        .error(R.drawable.placeholder_campaign)
                        .centerCrop()
                        .into(ivCampaignImage);
            }

            if (tvRecommendationBadge != null) {
                tvRecommendationBadge.setVisibility(campaign.isRecommendation() ? View.VISIBLE : View.GONE);
            }

            int progress = 0;
            if (campaign.getTargetAmount() > 0) {
                progress = (int) ((campaign.getCollectedAmount() * 100) / campaign.getTargetAmount());
            }

            if (progressBarCampaign != null) {
                progressBarCampaign.setProgress(progress);
            }

            if (tvStatus != null) {
                if (campaign.getDaysLeft() > 0) {
                    tvStatus.setText("Terkumpul");
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                }
            }

            if (layoutDaysRemaining != null && tvDaysRemaining != null) {
                if (campaign.getDaysLeft() > 0) {
                    layoutDaysRemaining.setVisibility(View.VISIBLE);
                    tvDaysRemaining.setText(String.valueOf(campaign.getDaysLeft()));
                } else {
                    layoutDaysRemaining.setVisibility(View.GONE);
                }
            }

            if (tvAmountRaised != null) {
                tvAmountRaised.setText(currencyFormatter.format(campaign.getCollectedAmount()));
            }

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCampaignClick(campaign);
                }
            });
        }
    }

    public void updateCampaigns(List<Campaign> newCampaigns) {
        this.campaignList.clear();
        this.campaignList.addAll(newCampaigns);
        notifyDataSetChanged();
    }

    public void addCampaigns(List<Campaign> newCampaigns) {
        int startPosition = this.campaignList.size();
        this.campaignList.addAll(newCampaigns);
        notifyItemRangeInserted(startPosition, newCampaigns.size());
    }
}