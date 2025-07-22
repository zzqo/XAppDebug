package tw.idv.palatis.xappdebug.ui;

import static tw.idv.palatis.xappdebug.Constants.LOG_TAG;
import static tw.idv.palatis.xappdebug.Constants.PREF_KEY_APP_LANGUAGE;
import static tw.idv.palatis.xappdebug.Constants.PREF_KEY_SHOW_DEBUG;
import static tw.idv.palatis.xappdebug.Constants.PREF_KEY_SHOW_DEBUGGABLE_FIRST;
import static tw.idv.palatis.xappdebug.Constants.PREF_KEY_SHOW_SYSTEM;
import static tw.idv.palatis.xappdebug.Constants.PREF_KEY_SORT_ORDER;
import static tw.idv.palatis.xappdebug.Constants.SORT_ORDER_INSTALL_TIME;
import static tw.idv.palatis.xappdebug.Constants.SORT_ORDER_LABEL;
import static tw.idv.palatis.xappdebug.Constants.SORT_ORDER_PACKAGE_NAME;
import static tw.idv.palatis.xappdebug.Constants.SORT_ORDER_UPDATE_TIME;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.Locale;

import tw.idv.palatis.xappdebug.R;
import tw.idv.palatis.xappdebug.adapters.InstalledPackageAdapter;
import tw.idv.palatis.xappdebug.viewmodels.AppsViewModel;

public class AppsFragment extends Fragment {

    private AppsViewModel mAppsViewModel;
    private SharedPreferences mSharedPreferences;
    private InstalledPackageAdapter mAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        mAppsViewModel = new ViewModelProvider(requireActivity()).get(AppsViewModel.class);
        final View root = inflater.inflate(R.layout.fragment_apps, container, false);
        swipeRefreshLayout = root.findViewById(R.id.swipe_refresh_layout);
        final RecyclerView packages = root.findViewById(R.id.packages);
        mAdapter = new InstalledPackageAdapter(requireContext().getPackageManager(), mSharedPreferences);
        packages.setAdapter(mAdapter);

        // 设置下拉刷新监听
        swipeRefreshLayout.setOnRefreshListener(() -> {
            // 执行刷新操作，例如重新加载数据
            mAppsViewModel.updatePackageList(requireContext());

            // 停止刷新动画（你可以在数据加载完成后调用）
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                swipeRefreshLayout.setRefreshing(false);
            }, 200); // 模拟延迟
        });

        mAppsViewModel.getInstalledPackages().observe(
                getViewLifecycleOwner(),
                mAdapter::updateInstalledPackages
        );
        mAppsViewModel.updatePackageList(requireContext());
        return root;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.apps, menu);
        final SearchView search = (SearchView) menu.findItem(R.id.action_search).getActionView();
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.setFilterQuery(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.setFilterQuery(newText);
                return true;
            }
        });
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        switch (mSharedPreferences.getInt(PREF_KEY_SORT_ORDER, SORT_ORDER_LABEL)) {
            case SORT_ORDER_LABEL:
                menu.findItem(R.id.action_sort_label).setChecked(true);
                break;
            case SORT_ORDER_PACKAGE_NAME:
                menu.findItem(R.id.action_sort_package_name).setChecked(true);
                break;
            case SORT_ORDER_INSTALL_TIME:
                menu.findItem(R.id.action_sort_install_time).setChecked(true);
                break;
            case SORT_ORDER_UPDATE_TIME:
                menu.findItem(R.id.action_sort_update_time).setChecked(true);
                break;
        }
        menu.findItem(R.id.action_debuggable_first)
                .setChecked(mSharedPreferences.getBoolean(PREF_KEY_SHOW_DEBUGGABLE_FIRST, false));
        menu.findItem(R.id.action_show_system)
                .setChecked(mSharedPreferences.getBoolean(PREF_KEY_SHOW_SYSTEM, false));
        menu.findItem(R.id.action_show_debug)
                .setChecked(mSharedPreferences.getBoolean(PREF_KEY_SHOW_DEBUG, false));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort_label:
                mSharedPreferences.edit().putInt(PREF_KEY_SORT_ORDER, SORT_ORDER_LABEL).apply();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_sort_package_name:
                mSharedPreferences.edit().putInt(PREF_KEY_SORT_ORDER, SORT_ORDER_PACKAGE_NAME).apply();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_sort_install_time:
                mSharedPreferences.edit().putInt(PREF_KEY_SORT_ORDER, SORT_ORDER_INSTALL_TIME).apply();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_sort_update_time:
                mSharedPreferences.edit().putInt(PREF_KEY_SORT_ORDER, SORT_ORDER_UPDATE_TIME).apply();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_debuggable_first:
                mSharedPreferences.edit()
                        .putBoolean(PREF_KEY_SHOW_DEBUGGABLE_FIRST, !mSharedPreferences.getBoolean(PREF_KEY_SHOW_DEBUGGABLE_FIRST, false))
                        .apply();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_show_system:
                mSharedPreferences.edit()
                        .putBoolean(PREF_KEY_SHOW_SYSTEM, !mSharedPreferences.getBoolean(PREF_KEY_SHOW_SYSTEM, false))
                        .apply();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_show_debug:
                mSharedPreferences.edit()
                        .putBoolean(PREF_KEY_SHOW_DEBUG, !mSharedPreferences.getBoolean(PREF_KEY_SHOW_DEBUG, false))
                        .apply();
                requireActivity().invalidateOptionsMenu();
                return true;
            case R.id.action_language_en:
                showLanguage("en");
                return true;
            case R.id.action_language_zh:
                showLanguage("zh");
                return true;
            case R.id.action_language_tw:
                Log.e(LOG_TAG, "----------------------------"+ Locale.TAIWAN);
                showLanguage("tw");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLanguage(String languageCode){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(PREF_KEY_APP_LANGUAGE, languageCode);
        editor.apply();

        // 重启 Activity 以生效语言设置
        requireActivity().recreate();
    }
}
