package com.vriza.donasikita.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.vriza.donasikita.R;
import com.vriza.donasikita.models.Prayer;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class PrayerAdapter extends RecyclerView.Adapter<PrayerAdapter.PrayerViewHolder> {

    private Context context;
    private List<Prayer> prayerList;
    private SimpleDateFormat inputFormat;

    public PrayerAdapter(Context context) {
        this.context = context;
        this.prayerList = new ArrayList<>();
        this.inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.forLanguageTag("id-ID"));
        this.inputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    @NonNull
    @Override
    public PrayerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_prayer, parent, false);
        return new PrayerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrayerViewHolder holder, int position) {
        Prayer prayer = prayerList.get(position);

        holder.txtUserName.setText(prayer.getDisplayName());

        holder.txtTimeAgo.setText(getTimeAgo(prayer.getCreatedAt()));

        if (prayer.getDoa() != null && !prayer.getDoa().isEmpty()) {
            holder.txtPrayerContent.setText(prayer.getDoa());
            holder.txtPrayerContent.setVisibility(View.VISIBLE);
        } else {
            holder.txtPrayerContent.setText("Mengirimkan doa dengan tulus ❤️");
        }

        if (prayer.isAnonymous()) {
            Glide.with(context).load(R.drawable.ic_person).circleCrop()
                    .into(holder.imgUserProfile);
        } else {
            Glide.with(context)
                    .load(prayer.getUserPhotoUrl())
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_person)
                    .circleCrop()
                    .into(holder.imgUserProfile);
        }

    }

    @Override
    public int getItemCount() {
        return prayerList.size();
    }

    public void updatePrayers(List<Prayer> prayers) {
        this.prayerList.clear();
        if (prayers != null) {
            this.prayerList.addAll(prayers);
        }
        notifyDataSetChanged();
    }

    private String getTimeAgo(String dateString) {
        if (dateString == null || dateString.isEmpty()) {
            return "";
        }

        try {
            String cleanDateString = dateString;
            if (dateString.length() > 23) {
                cleanDateString = dateString.substring(0, 23) + "Z";
            }

            Date date = inputFormat.parse(cleanDateString);
            if (date != null) {
                long diffMillis = System.currentTimeMillis() - date.getTime();
                long diffSeconds = diffMillis / 1000;
                long diffMinutes = diffSeconds / 60;
                long diffHours = diffMinutes / 60;
                long diffDays = diffHours / 24;

                if (diffMinutes < 1) {
                    return "Baru saja";
                } else if (diffMinutes < 60) {
                    return diffMinutes + " menit yang lalu";
                } else if (diffHours < 24) {
                    return diffHours + " jam yang lalu";
                } else {
                    return diffDays + " hari yang lalu";
                }
            }
        } catch (ParseException e) {
            Log.e("PrayerAdapter", "Error parsing date: " + dateString, e);
            return "Beberapa waktu lalu";
        }

        return "Beberapa waktu lalu";
    }

    // ViewHolder class
    static class PrayerViewHolder extends RecyclerView.ViewHolder {
        ImageView imgUserProfile;
        TextView txtUserName;
        TextView txtTimeAgo;
        TextView txtPrayerContent;

        PrayerViewHolder(@NonNull View itemView) {
            super(itemView);
            imgUserProfile = itemView.findViewById(R.id.imgUserProfile);
            txtUserName = itemView.findViewById(R.id.txtUserName);
            txtTimeAgo = itemView.findViewById(R.id.txtTimeAgo);
            txtPrayerContent = itemView.findViewById(R.id.txtPrayerContent);
        }
    }
}