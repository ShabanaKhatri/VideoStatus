package com.videoStatus.activity;

import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.videoStatus.R;
import com.videoStatus.adapter.ViewPagerAdapter;
import com.videoStatus.api.APIError;
import com.videoStatus.api.ApiClient;
import com.videoStatus.api.BaseUrl;
import com.videoStatus.api.ErrorUtils;
import com.videoStatus.databinding.ActivityMainBinding;
import com.videoStatus.fragment.CategoryFragment;
import com.videoStatus.fragment.HotFragment;
import com.videoStatus.fragment.OnlyMemeFragment;
import com.videoStatus.fragment.VideoListFragment;
import com.videoStatus.fragment.VideosFragment;
import com.videoStatus.retrofit.AndyUtils;
import com.videoStatus.retrofit.ConstantManager;
import com.videoStatus.retrofit.DataItem;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements CategoryFragment.OnFragmentInteractionListener, VideoListFragment.OnFragmentInteractionListener {

    private ActivityMainBinding binding;
    private ViewPagerAdapter adapter;
    //    private Fragment newFragment;
//    private MemesFragment memeFragment;
    private FragmentTransaction transaction;

    TabLayout tabLayoutMainActivity;
    ViewPager viewPagerMainActivity;

    // private VideosFragment trendingFragment = new VideosFragment();
    //  private VideosFragment hotFragment = new VideosFragment();
    private VideosFragment sadFragment = new VideosFragment();
    //private VideosFragment loveFragment = new VideosFragment();
    private HotFragment hotFragment = new HotFragment();
    private OnlyMemeFragment onlyMemeFragment = new OnlyMemeFragment();
    private CategoryFragment categoryFragment = new CategoryFragment();

    private boolean flagTapVideoTab = true;
    private boolean flagTapMemeTab = false;

    private static final String TAG = "MainActivity";
    public static String type = "Hot";
//    public static ArrayList<DataItem> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        addNewFragment();
        Bundle bundle;
        tabLayoutMainActivity = findViewById(R.id.tabLayoutMainActivity);
        viewPagerMainActivity = findViewById(R.id.viewPagerMainActivity);
//        callVideoListAPI();
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), MainActivity.this);
//        adapter.addFragment(trendingFragment, getResources().getString(R.string.trending));
        adapter.addFragment(hotFragment, getResources().getString(R.string.hot));
        adapter.addFragment(sadFragment, getResources().getString(R.string.sad));
//        adapter.addFragment(loveFragment, getResources().getString(R.string.love));
        adapter.addFragment(categoryFragment, getResources().getString(R.string.categories));
//        adapter.addFragment(onlyMemeFragment, "MEMES");
        viewPagerMainActivity.setAdapter(adapter);
        viewPagerMainActivity.setOffscreenPageLimit(1);
        tabLayoutMainActivity.setupWithViewPager(viewPagerMainActivity);

        tabLayoutMainActivity.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getText() != null) {
                    type = tab.getText().toString();
                    if (!type.equals(getString(R.string.categories))) {
//                        callVideoListAPI();
                    }
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
                    //trendingFragment.onResumeVideo();

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

                    //trendingFragment.onPauseVideo();
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
                        //trendingFragment.onPauseVideo();
                        //trendingFragment.mRecyclerView.smoothScrollToPosition(0);
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

    private void callVideoListAPI() {
        HashMap<String, String> requestParams = new HashMap<>();
        requestParams.put("ApiName", type);
        requestParams.put("ApiKey", "cGl0cmFkZXZlZ2h5b25hbWFo");
        AndyUtils.showSimpleProgressDialog(this);
        Call<JsonObject> apiResponse = ApiClient.getService().methodCallGet(BaseUrl.SUB_URL, requestParams);
        apiResponse.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                swipeRefreshLayout.setRefreshing(false);
                AndyUtils.removeSimpleProgressDialog();
                if (response.isSuccessful()) {
                    /*if (list != null) {
                        list.clear();
                    }*/
                    if (BaseUrl.isSuccess(response.body().get("Error").getAsString())) {
                        Type listType;
                        Gson gson = new Gson();
                        listType = new TypeToken<ArrayList<DataItem>>() {
                        }.getType();
                       /* list = gson.fromJson(response.body().get("result"), listType);
                        if (list != null && list.size() > 0) {
                            viewPagerMainActivity.setAdapter(adapter);
                            Log.e("size", list.size() + "");
                        }*/
                    }
                } else {
                    AndyUtils.removeSimpleProgressDialog();
                    APIError error = ErrorUtils.parseError(response);
                    Log.e("error message", error.message());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("fail", t.getMessage());
                AndyUtils.removeSimpleProgressDialog();
            }
        });

    }
}

