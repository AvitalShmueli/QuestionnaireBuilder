package com.example.questionnairebuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.questionnairebuilder.databinding.ActivityMainBinding;
import com.example.questionnairebuilder.utilities.AuthenticationManager;
import com.example.questionnairebuilder.utilities.CircleWithBorderTransformation;
import com.example.questionnairebuilder.utilities.FirestoreManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MaterialToolbar myToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_explore, R.id.navigation_my_surveys)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        NavigationUI.setupWithNavController(binding.navView, navController);

        String destination = getIntent().getStringExtra("navigateTo");
        if ("navigation_my_surveys".equals(destination)) {
            binding.navView.setSelectedItemId(R.id.navigation_my_surveys);
        } else if (getIntent().getBooleanExtra("navigate_to_explore", false)) {
            binding.navView.setSelectedItemId(R.id.navigation_explore);
        }

        myToolbar = binding.topAppBar;
        setSupportActionBar(myToolbar);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller,
                                             @NonNull NavDestination destination, @Nullable Bundle arguments) {
                myToolbar.setNavigationIcon(null);
            }
        });
        myToolbar.setNavigationIcon(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_profile, menu);
        MenuItem profileItem = menu.findItem(R.id.action_profile);
        if (profileItem != null) {
            ImageView imageView = new ImageView(this);

            int size = getResources().getDimensionPixelSize(R.dimen.toolbar_icon_size); // define in dimens.xml, e.g. 40dp
            imageView.setLayoutParams(new ViewGroup.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            FirestoreManager firebaseManager = FirestoreManager.getInstance();
            AuthenticationManager authenticationManager = AuthenticationManager.getInstance();
            if (authenticationManager.getCurrentUser() != null) {
                String currentUserId = authenticationManager.getCurrentUser().getUid();
                firebaseManager.getUserData(currentUserId, user -> {
                    if (user != null) {
                        // Load the image from URL using Glide
                        String imageUrl = user.getProfileImageUrl();
                        Glide.with(this)
                                .load(imageUrl)
                                .placeholder(R.drawable.account_circle_24px)
                                //.circleCrop()
                                .transform(new CircleWithBorderTransformation(this, 1f, getColor(R.color.blue))) // 2dp white border
                                .into(imageView);
                    }
                    else imageView.setImageResource(R.drawable.account_circle_24px);
                });
            }
            profileItem.setActionView(imageView);
        }

        return true;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (intent.getBooleanExtra("navigate_to_explore", false))
            binding.navView.setSelectedItemId(R.id.navigation_explore);
    }
}