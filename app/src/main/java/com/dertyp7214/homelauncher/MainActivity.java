package com.dertyp7214.homelauncher;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.dertyp7214.homelauncher.adapter.AppAdapter;
import com.dertyp7214.homelauncher.adapter.AppScreen;
import com.dertyp7214.homelauncher.component.App;
import com.dertyp7214.homelauncher.component.CardItem;
import com.dertyp7214.homelauncher.component.TextDrawable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static com.dertyp7214.homelauncher.Utils.hideKeyboard;

public class MainActivity extends AppCompatActivity {

    private static BottomSheetBehavior bottomSheetBehavior;
    private TextInputEditText textInputEditTextSearch;
    private RecyclerView recyclerViewApps;
    private AppAdapter appAdapter;
    private List<App> apps = new ArrayList<>();
    private List<App> appsList = new ArrayList<>();
    private ImageView icClear;
    private AppScreen appScreen;

    @SuppressLint({"InlinedApi", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.content).setOnDragListener(new MyDragListener());

        RelativeLayout bottomSheet = findViewById(R.id.bottom_sheet);

        textInputEditTextSearch = findViewById(R.id.input_search);
        recyclerViewApps = findViewById(R.id.rv);
        icClear = findViewById(R.id.ic_clear);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        bottomSheetBehavior
                .setPeekHeight(bottomSheetBehavior.getPeekHeight() + navigationBarHeight());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
        textInputEditTextSearch.setEnabled(false);
        setMargins(recyclerViewApps, - 1, - 1, - 1, navigationBarHeight());

        appScreen = new AppScreen(this);

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_EXPANDED) {
                    if (Objects.requireNonNull(textInputEditTextSearch.getText()).toString()
                            .length() > 0)
                        icClear.setVisibility(View.VISIBLE);
                    else
                        icClear.setVisibility(View.INVISIBLE);
                } else
                    icClear.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });

        textInputEditTextSearch.setOnFocusChangeListener((view, b) -> {
            if (b && bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        textInputEditTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (event != null) {
                if (! event.isShiftPressed()) {
                    hideKeyboard(this);
                    textInputEditTextSearch.clearFocus();
                    onTextChange(
                            Objects.requireNonNull(textInputEditTextSearch.getText()).toString());
                    return true;
                }
                return false;
            }
            hideKeyboard(this);
            textInputEditTextSearch.clearFocus();
            onTextChange(Objects.requireNonNull(textInputEditTextSearch.getText()).toString());
            return true;
        });

        textInputEditTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                onTextChange(editable.toString());
                if (editable.toString().length() > 0)
                    icClear.setVisibility(View.VISIBLE);
                else
                    icClear.setVisibility(View.INVISIBLE);
            }
        });

        icClear.setOnClickListener(view -> {
            hideKeyboard(this);
            textInputEditTextSearch.clearFocus();
            textInputEditTextSearch.setText("");
            onTextChange("");
        });

        new Thread(() -> setUpApps(recyclerViewApps)).start();
    }

    private void onTextChange(String text) {
        if (! text.equals("")) {
            List<App> remove = new ArrayList<>();

            for (App app : appsList)
                if (! app.getName().toLowerCase().contains(text.toLowerCase()))
                    remove.add(app);

            apps.clear();
            apps.addAll(appsList);
            apps.removeAll(remove);
        } else {
            apps.clear();
            apps.addAll(appsList);
        }
        appAdapter.notifyDataSetChanged();
    }

    public int convertDpToPixel(float dp) {
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return (int) px;
    }

    private void setUpApps(RecyclerView appList) {
        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        List<PackageInfo> withoutStart = new ArrayList<>();
        for (PackageInfo packageInfo : packages)
            if (! isStartApp(packageInfo))
                withoutStart.add(packageInfo);
        packages.removeAll(withoutStart);
        Collections.sort(packages, (packageInfo, t1) -> getLabel(packageInfo.packageName)
                .compareToIgnoreCase(getLabel(t1.packageName)));
        appsList.clear();

        for (PackageInfo info : packages) {
            PackageManager packageManager = getPackageManager();
            String label = packageManager
                    .getApplicationLabel(info.applicationInfo)
                    .toString();
            Drawable drawable = new TextDrawable(getResources(), String.valueOf(label.charAt(0)));
            try {
                drawable = packageManager.getApplicationIcon(info.packageName);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            appsList.add(new App(drawable, label, info.packageName));
        }
        apps.clear();
        apps.addAll(appsList);

        runOnUiThread(() -> {
            appAdapter = new AppAdapter(this, apps);
            appList.setAdapter(appAdapter);
            appList.setLayoutManager(new LinearLayoutManager(this));
            textInputEditTextSearch.setEnabled(true);
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HALF_EXPANDED)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        });
    }

    private String getLabel(String packageName) {
        try {
            return getPackageManager()
                    .getApplicationLabel(getPackageManager().getApplicationInfo(packageName, 0))
                    .toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    private boolean isStartApp(PackageInfo pkgInfo) {
        return getPackageManager().getLaunchIntentForPackage(pkgInfo.packageName) != null;
    }

    private void animateMargin(View view, int percent, int percentBottom, int top, int bottom) {
        try {
            setMargins(view, - 1, top != - 1 ? (int) (((float) top / 100) * percent) : - 1, - 1,
                    bottom != - 1 ? (int) (((float) bottom / 100) * percentBottom) : - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }

    private int statusBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    private int navigationBarHeight() {
        Resources resources = getResources();
        int resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return resources.getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    @Override
    public void onBackPressed() {
        if (textInputEditTextSearch.hasFocus())
            textInputEditTextSearch.clearFocus();
        else if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        onBackPressed();
    }

    public static class MyTouchListener implements View.OnLongClickListener {

        @Override
        public boolean onLongClick(View view) {
            ClipData data = ClipData.newPlainText("", "");
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            view.startDrag(data, shadowBuilder, view, 0);
            view.setVisibility(View.INVISIBLE);
            if (bottomSheetBehavior != null)
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
    }

    class MyDragListener implements View.OnDragListener {
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    break;
                case DragEvent.ACTION_DROP:
                    View view = (View) event.getLocalState();
                    Drawable icon = ((ImageView) view.findViewById(R.id.img_icon)).getDrawable();
                    String name =
                            ((TextView) view.findViewById(R.id.text_appName)).getText().toString();
                    String packageName = ((TextView) view.findViewById(R.id.text_pkg)).getText()
                            .toString();
                    appScreen.addApp(new App(icon, name, packageName));
                    appAdapter.notifyDataSetChanged();
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                default:
                    break;
            }
            return true;
        }
    }
}
