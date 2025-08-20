package com.vriza.donasikita.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.vriza.donasikita.R;
import com.vriza.donasikita.network.ApiClient;
import com.vriza.donasikita.network.responses.DonationResponse;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class DonationHistoryAdapter extends RecyclerView.Adapter<DonationHistoryAdapter.ViewHolder> {

    private List<DonationResponse.DonationDetails> donationList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void onItemClick(DonationResponse.DonationDetails donation);
    }

    public DonationHistoryAdapter(List<DonationResponse.DonationDetails> donationList, Context context) {
        this.donationList = donationList;
        this.context = context;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_donation_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DonationResponse.DonationDetails donation = donationList.get(position);
        holder.bind(donation);
    }

    @Override
    public int getItemCount() {
        return donationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCampaignThumbnail;
        private TextView tvCampaignTitle;
        private TextView tvDonationDate;
        private TextView tvDonationAmount;
        private TextView tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCampaignThumbnail = itemView.findViewById(R.id.ivCampaignThumbnail);
            tvCampaignTitle = itemView.findViewById(R.id.tvCampaignTitle);
            tvDonationDate = itemView.findViewById(R.id.tvDonationDate);
            tvDonationAmount = itemView.findViewById(R.id.tvDonationAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);

            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        onItemClickListener.onItemClick(donationList.get(position));
                    }
                }
            });
        }

        public void bind(DonationResponse.DonationDetails donation) {
            if (donation.getCampaign() != null) {
                tvCampaignTitle.setText(donation.getCampaign().getTitle());

                String imageUrl = ApiClient.getImageUrl() + donation.getCampaign().getThumbnail();
                Glide.with(context)
                        .load(imageUrl)
                        .transform(new CenterCrop(), new RoundedCorners(16))
                        .placeholder(R.drawable.placeholder_campaign)
                        .error(R.drawable.placeholder_campaign)
                        .into(ivCampaignThumbnail);
            }

            tvDonationAmount.setText(formatAmount(donation.getAmount()));

            tvDonationDate.setText(formatDate(donation.getCreatedAt()));

            setupStatusBadge(donation.getStatus());
        }

        private String formatAmount(String amount) {
            if (amount == null) return "Rp 0";

            try {
                String cleanAmount = amount.replace(".00", "");
                long amountValue = Long.parseLong(cleanAmount);

                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
                return formatter.format(amountValue).replace("IDR", "Rp");
            } catch (Exception e) {
                return "Rp " + amount;
            }
        }

        private String formatDate(String createdAt) {
            if (createdAt == null) return "";

            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", Locale.getDefault());
                inputFormat.setTimeZone(java.util.TimeZone.getTimeZone("UTC"));
                Date date = inputFormat.parse(createdAt);

                if (date != null) {
                    long diffInMillis = System.currentTimeMillis() - date.getTime();
                    long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                    if (diffInDays == 0) {
                        return "Hari ini";
                    } else if (diffInDays == 1) {
                        return "Kemarin";
                    } else if (diffInDays < 7) {
                        return diffInDays + " hari lalu";
                    } else if (diffInDays < 30) {
                        long weeks = diffInDays / 7;
                        return weeks + " minggu lalu";
                    } else {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy", new Locale("id", "ID"));
                        return outputFormat.format(date);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            return createdAt;
        }

        private void setupStatusBadge(String status) {
            if (status == null) {
                tvStatus.setVisibility(View.GONE);
                return;
            }

            tvStatus.setVisibility(View.VISIBLE);

            switch (status.toLowerCase()) {
                case "success":
                case "berhasil":
                case "paid":
                    tvStatus.setText("Berhasil");
                    tvStatus.setBackgroundResource(R.drawable.status_success_background);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cerulean_70));
                    break;

                case "pending":
                    tvStatus.setText("Pending");
                    tvStatus.setBackgroundResource(R.drawable.status_success_background);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.cerulean_70));
                    break;

                case "failed":
                case "expired":
                case "cancelled":
                    tvStatus.setText("Dibatalkan");
                    tvStatus.setBackgroundResource(R.drawable.status_cancelled_background);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.text_secondary));
                    break;

                default:
                    tvStatus.setText(status);
                    tvStatus.setBackgroundResource(R.drawable.status_default_background);
                    tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black));
                    break;
            }
        }
    }
}