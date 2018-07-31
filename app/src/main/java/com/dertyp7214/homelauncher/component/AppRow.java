package com.dertyp7214.homelauncher.component;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dertyp7214.homelauncher.R;

@SuppressLint("NewApi")
public class AppRow extends RelativeLayout {

    private Context context;
    private View rootView;
    private App app1, app2, app3, app4, app5;
    private LayoutListener listener;

    public AppRow(Context context) {
        super(context);
        setUp(context);
    }

    public AppRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context);
    }

    public AppRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setUp(context);
    }

    public AppRow(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setUp(context);
    }

    private void setUp(Context context) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        rootView = inflater.inflate(R.layout.app_row, this, true);

        app1 = new App(rootView.findViewById(R.id.app1), context);
        app2 = new App(rootView.findViewById(R.id.app2), context);
        app3 = new App(rootView.findViewById(R.id.app3), context);
        app4 = new App(rootView.findViewById(R.id.app4), context);
        app5 = new App(rootView.findViewById(R.id.app5), context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (listener != null)
            listener.onChange(this);
    }

    public void setOnLayoutListener(LayoutListener listener) {
        this.listener = listener;
    }

    public void setAppRow(AppRow appRow) {
        app1.setAppTitle(appRow.app1.label);
        app1.setImage(appRow.app1.icon);
        app1.setPackageName(appRow.app1.packageName);
        app2.setAppTitle(appRow.app2.label);
        app2.setImage(appRow.app2.icon);
        app2.setPackageName(appRow.app2.packageName);
        app3.setAppTitle(appRow.app3.label);
        app3.setImage(appRow.app3.icon);
        app3.setPackageName(appRow.app3.packageName);
        app4.setAppTitle(appRow.app4.label);
        app4.setImage(appRow.app4.icon);
        app4.setPackageName(appRow.app4.packageName);
        app5.setAppTitle(appRow.app5.label);
        app5.setImage(appRow.app5.icon);
        app5.setPackageName(appRow.app5.packageName);
    }

    public void setApp(AppId appId, Drawable appIcon, String appName, String packageName) {
        setApp(appId.value, appIcon, appName, packageName);
    }

    public void setApp(int appId, Drawable appIcon, String appName, String packageName) {
        App app;
        switch (appId) {
            case 0:
                app = app1;
                break;
            case 1:
                app = app2;
                break;
            case 2:
                app = app3;
                break;
            case 3:
                app = app4;
                break;
            default:
                app = app5;
        }
        app.setPackageName(packageName);
        app.setImage(appIcon);
        app.setAppTitle(appName);
        if (listener != null)
            listener.onChange(this);
    }

    public void setApp(AppId appId, App app) {
        setApp(appId.value, app);
    }

    public void setApp(int appId, App app) {
        if (app == null)
            return;
        switch (appId) {
            case 0:
                app1 = app;
                break;
            case 1:
                app2 = app;
                break;
            case 2:
                app3 = app;
                break;
            case 3:
                app4 = app;
                break;
            default:
                app5 = app;
        }
    }

    public interface LayoutListener {
        void onChange(View view);
    }

    public enum AppId {
        ONE(0),
        TWO(1),
        THREE(2),
        FOUR(3),
        FIVE(4);
        private int value;

        AppId(int value) {
            this.value = value;
        }
    }

    public static class App {
        private ImageView imageView;
        private TextView textView;
        private View view;
        private Context context;
        private String label, packageName;
        private Drawable icon;

        App(View view, Context context) {
            this.context = context;
            this.view = view;
            imageView = view.findViewById(R.id.img_app_icon);
            textView = view.findViewById(R.id.text_appName);
        }

        void setPackageName(String packageName) {
            this.packageName = packageName;
            view.setOnClickListener(view -> {
                try {
                    Intent launchIntent = context.getPackageManager()
                            .getLaunchIntentForPackage(packageName);
                    context.startActivity(launchIntent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        void setImage(Drawable drawable) {
            this.icon = drawable;
            imageView.setImageDrawable(drawable);
        }

        void setAppTitle(String string) {
            this.label = string;
            textView.setText(string);
        }
    }
}
