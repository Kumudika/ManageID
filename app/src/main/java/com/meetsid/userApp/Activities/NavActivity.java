package com.meetsid.userApp.Activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.meetsid.userApp.Components.DashboardFrag;
import com.meetsid.userApp.Components.ProfileFrag;
import com.meetsid.userApp.Components.SettingsFrag;
import com.meetsid.userApp.R;
import com.meetsid.userApp.Utils.Common;

public class NavActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav);
        Common.isRegCompleted = true;
        BottomNavigationView navView = findViewById(R.id.nav_view);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragContainer, new DashboardFrag())
                .commit();
        navView.setOnNavigationItemSelectedListener(this);
        navView.setItemHorizontalTranslationEnabled(true);
        navView.setFocusableInTouchMode(true);
    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragContainer, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;
        switch (menuItem.getItemId()) {
            case R.id.dashboard:
                fragment = new DashboardFrag();
                break;
            case R.id.profile:
                fragment = new ProfileFrag();
                break;
            case R.id.settings:
                fragment = new SettingsFrag();
                break;
        }
        return loadFragment(fragment);
    }

    @Override
    public void onBackPressed() {
        //
    }
}
