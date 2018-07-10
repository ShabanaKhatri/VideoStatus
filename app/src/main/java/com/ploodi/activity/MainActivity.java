package com.ploodi.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.ploodi.R;
import com.ploodi.adapter.ViewPagerAdapter;
import com.ploodi.databinding.ActivityMainBinding;
import com.ploodi.fragment.CategoryFragment;
import com.ploodi.fragment.OnlyMemeFragment;
import com.ploodi.fragment.VideoListFragment;
import com.ploodi.fragment.VideosFragment;
import com.ploodi.retrofit.ConstantManager;

public class MainActivity extends AppCompatActivity implements CategoryFragment.OnFragmentInteractionListener, VideoListFragment.OnFragmentInteractionListener {

    private ActivityMainBinding binding;
    private ViewPagerAdapter adapter;
    //    private Fragment newFragment;
//    private MemesFragment memeFragment;
    private FragmentTransaction transaction;

    TabLayout tabLayoutMainActivity;
    ViewPager viewPagerMainActivity;

    private VideosFragment trendingFragment = new VideosFragment();
    private VideosFragment hotFragment = new VideosFragment();
    private VideosFragment sadFragment = new VideosFragment();
    private VideosFragment loveFragment = new VideosFragment();
    private OnlyMemeFragment onlyMemeFragment = new OnlyMemeFragment();
    private CategoryFragment categoryFragment = new CategoryFragment();

    private boolean flagTapVideoTab = true;
    private boolean flagTapMemeTab = false;

    private static final String TAG = "MainActivity";
    public static String type = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        addNewFragment();
        Bundle bundle;
        tabLayoutMainActivity = findViewById(R.id.tabLayoutMainActivity);
        viewPagerMainActivity = findViewById(R.id.viewPagerMainActivity);
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
//        adapter.addFragment(trendingFragment, getResources().getString(R.string.trending));
        adapter.addFragment(hotFragment, getResources().getString(R.string.hot));
        adapter.addFragment(sadFragment, getResources().getString(R.string.sad));
        adapter.addFragment(loveFragment, getResources().getString(R.string.love));
        adapter.addFragment(categoryFragment, getResources().getString(R.string.categories));
//        adapter.addFragment(onlyMemeFragment, "MEMES");

        viewPagerMainActivity.setOffscreenPageLimit(0);
        viewPagerMainActivity.setAdapter(adapter);

        tabLayoutMainActivity.setupWithViewPager(viewPagerMainActivity);
//        tabLayoutMainActivity.getTabAt(0).setIcon(R.mipmap.play_pause);
//        tabLayoutMainActivity.getTabAt(1).setIcon(R.mipmap.icon_two);

        tabLayoutMainActivity.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    type = tab.getText().toString();
                }
                int tabIndex = tab.getPosition();
                if (tabIndex == 0) {

                    Log.d(TAG, "onTabSelected: onTabSelected:");
                    flagTapVideoTab = true;
                    flagTapMemeTab = false;
                    ConstantManager.mEndTimeTextView = null;
                    ConstantManager.mCurrentTimeTextView = null;
                    ConstantManager.happyPosition = 0;
                    ConstantManager.lastPlayingPosition = 0;
                    ConstantManager.IS_FIRST_POSITION = true;
                    ConstantManager.IS_FIRST_FOR_RESUME = false;
                    ConstantManager.IS_WIFI = false;
                    ConstantManager.isVideoFragment = true;
                    trendingFragment.onResumeVideo();

                } else if (tabIndex == 1) {
                    flagTapVideoTab = false;
                    flagTapMemeTab = true;
                    ConstantManager.mEndTimeTextView = null;
                    ConstantManager.mCurrentTimeTextView = null;
                    ConstantManager.happyPosition = 0;
                    ConstantManager.lastPlayingPosition = 0;
                    ConstantManager.IS_FIRST_POSITION = true;
                    ConstantManager.IS_FIRST_FOR_RESUME = false;
                    ConstantManager.IS_WIFI = false;
                    ConstantManager.isVideoFragment = false;

                    trendingFragment.onPauseVideo();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                int tabIndex = tab.getPosition();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int tabIndex = tab.getPosition();
                if (tabIndex == 0) {
                    if (flagTapVideoTab) {
                        Log.d(TAG, "onTabSelected: onTabReselected:");
                        trendingFragment.onPauseVideo();
                        trendingFragment.mRecyclerView.smoothScrollToPosition(0);
                    }
                } else if (tabIndex == 1) {
                    if (flagTapMemeTab) {
                        Log.d(TAG, "onTabSelected: onTabReselected: meme");
//                        categoryFragment.mRecyclerView.smoothScrollToPosition(0);
                    }
                }
            }
        });

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

