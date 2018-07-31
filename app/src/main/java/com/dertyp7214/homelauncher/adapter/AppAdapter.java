package com.dertyp7214.homelauncher.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dertyp7214.homelauncher.MainActivity;
import com.dertyp7214.homelauncher.R;
import com.dertyp7214.homelauncher.component.App;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class AppAdapter extends RecyclerView.Adapter<AppAdapter.ViewHolder> {

    private Context context;
    private List<App> apps;

    public AppAdapter(Context context, List<App> apps) {
        this.context = context;
        this.apps = apps;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_home_item, parent, false);
        return new ViewHolder(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        App app = apps.get(position);

        holder.appName.setText(app.getName());
        holder.pkg.setText(app.getPackageName());
        holder.imageView.setImageDrawable(app.getIcon());

        holder.card.setOnLongClickListener(new MainActivity.MyTouchListener());
        holder.card.setOnClickListener(v -> {
            Intent launchIntent =
                    context.getPackageManager().getLaunchIntentForPackage(app.getPackageName());
            context.startActivity(launchIntent);
        });
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        CardView card;
        ImageView imageView;
        TextView appName, pkg;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            card = itemView.findViewById(R.id.card);
            imageView = itemView.findViewById(R.id.img_icon);
            appName = itemView.findViewById(R.id.text_appName);
            pkg = itemView.findViewById(R.id.text_pkg);
        }
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }
}
