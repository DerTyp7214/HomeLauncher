package com.dertyp7214.homelauncher.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dertyp7214.homelauncher.R;
import com.dertyp7214.homelauncher.component.App;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class AppScreen extends RecyclerView.Adapter<AppScreen.ViewHolder> {

    private List<App> apps = new ArrayList<>();
    private Activity activity;

    public AppScreen(Activity activity) {
        this.activity = activity;

        RecyclerView recyclerView = activity.findViewById(R.id.app_screen);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(this);
    }

    public void loadApps(){
        notifyDataSetChanged();
    }

    public void addApp(App app) {
        apps.add(app);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.card, parent);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        App app = apps.get(position);

        holder.appName.setText(app.getName());
        holder.packageName.setText(app.getPackageName());
        holder.icon.setImageDrawable(app.getIcon());

        holder.cardView.setOnClickListener(v -> {
            Intent launch = new Intent(activity.getPackageManager().getLaunchIntentForPackage(app.getPackageName()));
            activity.startActivity(launch);
        });
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        CardView cardView;
        ImageView icon;
        TextView appName, packageName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            icon = itemView.findViewById(R.id.img_icon);
            appName = itemView.findViewById(R.id.text_appName);
            cardView = itemView.findViewById(R.id.card);
            packageName = itemView.findViewById(R.id.text_pkg);
        }
    }
}
