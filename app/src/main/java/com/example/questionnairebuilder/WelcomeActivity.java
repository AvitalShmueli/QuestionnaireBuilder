package com.example.questionnairebuilder;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;

import java.util.Locale;

public class WelcomeActivity extends AppCompatActivity {

    private ShapeableImageView welcome_IMG_language;
    private MaterialButton welcome_BTN_login;
    private MaterialButton welcome_BTN_signUp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViews();

        animateEntrance(welcome_IMG_language, 0);
        animateEntrance(welcome_BTN_login, 0);
        animateEntrance(welcome_BTN_signUp, 0);

        initViews();
    }

    private void initViews() {
        welcome_IMG_language.setOnClickListener(view -> showLanguageDialog());
        welcome_BTN_login.setOnClickListener(view -> moveToLogin());
        welcome_BTN_signUp.setOnClickListener(view -> moveToSignUp());
    }

    private void moveToLogin() {
        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void moveToSignUp() {
        Intent intent = new Intent(WelcomeActivity.this, SignUpActivity.class);
        startActivity(intent);
        finish();
    }

    private void findViews() {
        welcome_IMG_language = findViewById(R.id.welcome_IMG_language);
        welcome_BTN_login = findViewById(R.id.welcome_BTN_login);
        welcome_BTN_signUp = findViewById(R.id.welcome_BTN_signUp);
    }

    private void animateEntrance(View view, long delay) {
        view.setAlpha(0f);
        view.setTranslationY(100f); // slight downward offset
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(600)
                .setInterpolator(new android.view.animation.DecelerateInterpolator())
                .start();
    }

    private void showLanguageDialog() {
        final String[] languages = {"English", "עברית"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                languages
        ) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = view.findViewById(android.R.id.text1);

                if (Locale.getDefault().getLanguage().equals("he")) { // Force alignment based on current locale direction
                    textView.setTextDirection(View.TEXT_DIRECTION_RTL);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // aligns right in RTL
                }
                else {
                    textView.setTextDirection(View.TEXT_DIRECTION_LTR);
                    textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START); // aligns left in LTR
                }

                return view;
            }
        };

        new AlertDialog.Builder(this)
                .setTitle(R.string.select_language)
                .setAdapter(adapter, (dialog, which) -> {
                    if (which == 0)
                        setLocale("en");
                    else if (which == 1)
                        setLocale("he");
                })
                .show();
    }

    private void setLocale(String langCode) {
        getSharedPreferences("settings", MODE_PRIVATE)  // Save selected language
                .edit()
                .putString("app_lang", langCode)
                .apply();

        recreate(); // Restart to apply locale (it will be picked in attachBaseContext)
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        SharedPreferences prefs = newBase.getSharedPreferences("settings", MODE_PRIVATE);
        String lang = prefs.getString("app_lang", "en");

        Configuration config = new Configuration(newBase.getResources().getConfiguration());
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        config.setLocale(locale);
        config.setLayoutDirection(locale);

        Context context = newBase.createConfigurationContext(config);
        super.attachBaseContext(context);
    }
}