package tw.idv.palatis.xappdebug.ui;

import static tw.idv.palatis.xappdebug.Constants.PREF_KEY_APP_LANGUAGE;
import static tw.idv.palatis.xappdebug.Constants.PREF_KEY_SHOW_DEBUGGABLE_FIRST;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

import tw.idv.palatis.xappdebug.R;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        final FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show());
        final DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_apps, R.id.nav_about)
                .setDrawerLayout(drawer)
                .build();
        final NavHostFragment navFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        final NavController navController = navFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        final NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(newBase);
        String languageCode = mSharedPreferences.getString(PREF_KEY_APP_LANGUAGE, Locale.getDefault().getLanguage());
        Locale locale;
        if ("tw".equals(languageCode)) {
            locale = Locale.TAIWAN;
        } else {
            locale = new Locale(languageCode);
        }

        super.attachBaseContext(setLanguage(newBase, locale));
    }

    private Context setLanguage(Context context, Locale locale) {
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        Locale.setDefault(locale);
        return context.createConfigurationContext(configuration);
    }

}
