package com.dertyp7214.homelauncher.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dertyp7214.homelauncher.JSONSharedPreferences;
import com.dertyp7214.homelauncher.R;
import com.dertyp7214.homelauncher.component.App;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.dertyp7214.homelauncher.Utils.drawableToBitmap;

public class AppScreen extends RecyclerView.Adapter<AppScreen.ViewHolder> {

    private List<App> apps = new ArrayList<>();
    private Activity activity;

    public AppScreen(Activity activity) {
        this.activity = activity;

        RecyclerView recyclerView = activity.findViewById(R.id.app_screen);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(this);
        loadApps();
    }

    private void loadApps() {
        try {
            JSONArray array = JSONSharedPreferences.loadJSONArray(activity, "appScreen", "appList");
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String packageName = object.getString("packageName");
                File imgFile = new File(activity.getFilesDir(), packageName + ".png");
                Drawable pic;
                if (imgFile.exists()) {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                    pic = new BitmapDrawable(activity.getResources(),
                            BitmapFactory.decodeFile(imgFile.getAbsolutePath(), options));
                } else {
                    try {
                        pic = activity.getPackageManager().getApplicationLogo(
                                activity.getPackageManager()
                                        .getApplicationInfo(packageName, 0));
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        pic = activity.getResources().getDrawable(R.drawable.ic_no_icon);
                    }
                }
                apps.add(new App(pic, object.getString("name"), packageName));

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        notifyDataSetChanged();
    }

    public void addApp(App app) {
        apps.add(app);
        notifyDataSetChanged();
        saveData();
    }

    public void removeApp(int position) {
        apps.remove(position);
        notifyDataSetChanged();
        saveData();
    }

    private void saveData() {
        JSONArray array = new JSONArray();
        for (App app : apps) {
            try {
                File imgFile = new File(activity.getFilesDir(), app.getPackageName() + ".png");
                if (! imgFile.exists()) {
                    FileOutputStream fileOutputStream = new FileOutputStream(imgFile);
                    drawableToBitmap(app.getIcon())
                            .compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                }
                JSONObject object = new JSONObject();
                object.put("packageName", app.getPackageName());
                object.put("name", app.getName());
                array.put(object);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        JSONSharedPreferences.saveJSONArray(activity, "appScreen", "appList", array);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.card, parent, false);
        return new ViewHolder(view);
    }

    private void showMenu(View v, int position) {
        PopupMenu popup = new PopupMenu(activity, v);
        popup.setOnMenuItemClickListener(item -> {
            removeApp(position);
            return false;
        });
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.app_menu, popup.getMenu());
        popup.show();
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        App app = apps.get(position);

        holder.appName.setText(app.getName());
        holder.packageName.setText(app.getPackageName());
        holder.icon.setImageDrawable(app.getIcon());

        holder.cardView.setOnClickListener(v -> {
            Intent launch = new Intent(
                    activity.getPackageManager().getLaunchIntentForPackage(app.getPackageName()));
            activity.startActivity(launch);
        });

        holder.cardView.setOnLongClickListener(view -> {
            showMenu(view, position);
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return apps.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

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
