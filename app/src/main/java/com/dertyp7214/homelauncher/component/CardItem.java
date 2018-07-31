package com.dertyp7214.homelauncher.component;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dertyp7214.homelauncher.R;

public class CardItem extends ViewGroup {

    private Context context;
    private Drawable icon;
    private String name, packageName;
    private View view;

    public CardItem(Context context) {
        super(context);
    }

    public CardItem(Context context, Drawable icon, String name, String packageName) {
        super(context);
        setUp(context, icon, name, packageName);
    }

    private void setUp(Context context, Drawable icon, String name, String packageName){
        this.context = context;
        this.icon = icon;
        this.name = name;
        this.packageName = packageName;

        view = LayoutInflater.from(context).inflate(R.layout.card, this, true);

        ImageView imageView = view.findViewById(R.id.img_icon);
        TextView textView1 = view.findViewById(R.id.text_appName);
        TextView textView2 = view.findViewById(R.id.text_pkg);

        imageView.setImageDrawable(icon);
        textView1.setText(name);
        textView2.setText(packageName);

        view.setOnClickListener(v -> {
            Intent launchIntent =
                    context.getPackageManager().getLaunchIntentForPackage(packageName);
            context.startActivity(launchIntent);
        });
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
    }

    public String getName() {
        return this.name;
    }
}
