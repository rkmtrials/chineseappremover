package com.textsdev.chineseappremover;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<String> aapplist = new ArrayList<>();
    LinearLayout linearLayout;
    List<String> stringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        InputStream inputStream = getResources().openRawResource(R.raw.list);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String eachline = null;
        linearLayout = findViewById(R.id.list_ll);
        try {
            eachline = bufferedReader.readLine();
            while (eachline != null) {
                // `the words in the file are separated by space`, so to get each words
                eachline = bufferedReader.readLine();
                if (eachline != null) {
                    aapplist.add(eachline);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        listPackages(linearLayout);
    }

    private void listPackages(LinearLayout linearLayout) {
        List<ApplicationInfo> packages = getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        TextView no_app = findViewById(R.id.no_app_tv);
        no_app.setVisibility(View.GONE);
        if (linearLayout != null && linearLayout.getChildCount() > 0) {
            linearLayout.removeAllViews();
        }
        int i = 0;
        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        llp.setMargins(10, 10, 10, 10);
        for (ApplicationInfo a : packages) {
            boolean system = (a.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM;
            if (!system) {
                String packageName = a.packageName;
                CharSequence applicationLabel = getPackageManager().getApplicationLabel(a);
                if (packageName != null) {
                    String raw_app_name = applicationLabel.toString();
                    String o = raw_app_name.toLowerCase().replaceAll(" ", "");
                    for (String list : aapplist) {
                        String[] split = list.split(",");
                        String s = split[0].toLowerCase().replaceAll(" ", "");
                        if (split.length == 1) {
                            String app_name = o.toLowerCase().replaceAll(" ", "");
                            Log.d("texts", "listPackages: "+s+" "+app_name);
                            if (s.contains(app_name)|| app_name.contains(s)) {
                                View view = getLayoutInflater().inflate(R.layout.row_layout, null);
                                view.setLayoutParams(llp);
                                setLable_only_uninstall(linearLayout, view, raw_app_name, getPackageManager().getApplicationIcon(a), a.packageName);
                                i++;
                            }


                        } else {


                            String packageSplit = split[1];
                            boolean equals = packageName.matches(packageSplit.toLowerCase().replaceAll(" ", ""));
                            String app_name = o.toLowerCase().replaceAll(" ", "");
                            if (split.length == 2) {
                                if (s.equals(app_name) || equals) {
                                    View view = getLayoutInflater().inflate(R.layout.row_layout, null);
                                    view.setLayoutParams(llp);
                                    setLable_only_uninstall(linearLayout, view, raw_app_name, getPackageManager().getApplicationIcon(a), a.packageName);
                                    i++;
                                }
                            } else if (split.length == 4) {

                                if (s.equals(app_name) || equals) {
                                    View view = getLayoutInflater().inflate(R.layout.row_layout, null);
                                    view.setLayoutParams(llp);
                                    set_label_uninstall_download(linearLayout, raw_app_name, split[3], view, getPackageManager().getApplicationIcon(a), packageName);
                                    i++;
                                }

                            }

                        }

                    }
                }
            }
        }
        if(i == 0)
        {
            no_app.setVisibility(View.VISIBLE);
        }
    }

    private void set_label_uninstall_download(LinearLayout linearLayout, String app_name, String alt_name, View view, Drawable icon, final String packageName) {
        if(!stringList.contains(packageName))
        {

            Button removebtn = view.findViewById(R.id.uninstall_btn);
            Button alt_download_button = view.findViewById(R.id.download_btn);
            ImageView icon_image = view.findViewById(R.id.icon_view);
            TextView app_name_tv = view.findViewById(R.id.app_name);
            TextView alt_name_tv = view.findViewById(R.id.alt_name);
            icon_image.setImageDrawable(icon);
            app_name_tv.setText(app_name);
            alt_name_tv.setText(alt_name);
            removebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivityForResult(intent, 1);
                }
            });
            alt_download_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=" + packageName));
                    startActivity(intent);
                }
            });
            linearLayout.addView(view);
            stringList.add(packageName);
        }
    }

    private void setLable_only_uninstall(LinearLayout linearLayout, View view, String app_name, Drawable icon, final String packageName) {
        if(!stringList.contains(packageName))
        {

            Button removebtn = view.findViewById(R.id.uninstall_btn);
            ImageView icon_image = view.findViewById(R.id.icon_view);
            TextView app_name_tv = view.findViewById(R.id.app_name);
            icon_image.setImageDrawable(icon);
            app_name_tv.setText(app_name);
            removebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DELETE);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivityForResult(intent, 1);
                }
            });
            linearLayout.addView(view);
            stringList.add(packageName);
            view.findViewById(R.id.alt_ll).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (linearLayout != null) {
                listPackages(linearLayout);
            }
        }
    }
}