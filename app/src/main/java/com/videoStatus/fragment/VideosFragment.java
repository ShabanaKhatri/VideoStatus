package com.videoStatus.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.videoStatus.R;
import com.videoStatus.activity.MainActivity;
import com.videoStatus.api.APIError;
import com.videoStatus.api.ApiClient;
import com.videoStatus.api.BaseUrl;
import com.videoStatus.api.ErrorUtils;
import com.videoStatus.databinding.FragmentVideosBinding;
import com.videoStatus.retrofit.APIClient;
import com.videoStatus.retrofit.APIInterface;
import com.videoStatus.retrofit.AndyUtils;
import com.videoStatus.retrofit.ConstantManager;
import com.videoStatus.retrofit.DataItem;
import com.videoStatus.videomanage.controller.VideoControllerView;
import com.videoStatus.videomanage.controller.ViewAnimator;
import com.videoStatus.videomanage.manager.SingleVideoPlayerManager;
import com.videoStatus.videomanage.manager.VideoPlayerManager;
import com.videoStatus.videomanage.meta.CurrentItemMetaData;
import com.videoStatus.videomanage.meta.MetaData;
import com.videoStatus.videomanage.ui.MediaPlayerWrapper;
import com.videoStatus.videomanage.ui.VideoPlayerView;
import com.videoStatus.widgets.DisableRecyclerView;
import com.videoStatus.widgets.OnLoadMoreListener;
import com.videoStatus.widgets.Utils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class VideosFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener/*, AsyncTaskCompleteListener*/ {

    private APIInterface apiInterface;
//    public static ArrayList<DataItem> arrayList;

    // from Fragment
    private static final String TAG = "MainActivity";
    public static DisableRecyclerView mRecyclerView;
    private LinearLayoutManager mLayoutManager;

    private FrameLayout mVideoFloatContainer;
    private View mVideoPlayerBg;
    private ImageView mVideoCoverMask;
    private VideoPlayerView mVideoPlayerView;
    private View mVideoLoadingView;
    private ProgressBar mVideoProgressBar;

    private View mCurrentPlayArea;
    private VideoControllerView mCurrentVideoControllerView;
    private int mCurrentActiveVideoItem = -1;

    private Handler mHandler = new Handler(Looper.getMainLooper());
    private int mCurrentBuffer;
    private MediaPlayerWrapper mediaPlayer;

    private ArrayList<DataItem> list = new ArrayList<>();

    /**
     * Prevent {@link #stopPlaybackImmediately} be called too many times
     */
    private boolean mCanTriggerStop = true;

    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(null);

    /**
     * Stop video have two scenes
     * 1.click to stop current video and start a new video
     * 2.when video item is dismiss or ViewPager changed ? tab changed ? ...
     */
    private boolean mIsClickToStop;

    private float mOriginalHeight;

    private float mMoveDeltaY;
    private RecyclerAdapter photoListAdapter;


    private FragmentVideosBinding binding;
    private View mrootView;
    //    private SwipeRefreshLayout swipeRefreshLayout;
    private static RecyclerAdapter recylerAdapter;
    private boolean isLoadMoreVideo = false;
    ArrayList<DataItem> arrayListMoreLoad = new ArrayList<>();

    private String type = "";


    private void startMoveFloatContainer(boolean click) {

        if (mVideoFloatContainer.getVisibility() != View.VISIBLE) return;
        final float moveDelta;

        if (click) {
            ViewAnimator.putOn(mVideoFloatContainer).translationY(0);
            int[] playAreaPos = new int[2];
            int[] floatContainerPos = new int[2];
            mCurrentPlayArea.getLocationOnScreen(playAreaPos);
            mVideoFloatContainer.getLocationOnScreen(floatContainerPos);
            mOriginalHeight = moveDelta = playAreaPos[1] - floatContainerPos[1];

        } else {
            moveDelta = mMoveDeltaY;
        }

        float translationY = moveDelta + (!click ? mOriginalHeight : 0);
        ViewAnimator.putOn(mVideoFloatContainer).translationY(translationY);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiInterface = APIClient.getClient().create(APIInterface.class);
//        arrayList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_videos, container, false);
        View rootView = binding.getRoot();
        mrootView = rootView;

        if (MainActivity.type.equals(getResources().getString(R.string.hot))) {
            type = BaseUrl.HOT;
        } else if (MainActivity.type.equals(getResources().getString(R.string.sad))) {
            type = BaseUrl.SAD;
        } else if (MainActivity.type.equals(getResources().getString(R.string.love))) {
            type = BaseUrl.LOVE;
        }

        onStartVideo();

        /*if (getArguments().containsKey("type")) {
            type = getArguments().getString("type");
            if (type != null) {
                Log.e("type", type);
            }
        }*/
        Log.e("type", MainActivity.type);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
//        onStartVideo();
    }

    public void onStartVideo() {
        Log.d(TAG, "Ploodi onStartVideo: ");
        checkWifi();
        initializeWidgets(mrootView);
        //setAdapter(list, true);
//        getVideoList();
        callVideoListAPI();
    }

    private void initializeWidgets(View rootView) {
        mVideoFloatContainer = (FrameLayout) rootView.findViewById(R.id.layout_float_video_container);
        mVideoPlayerBg = rootView.findViewById(R.id.video_player_bg);
        mVideoCoverMask = (ImageView) rootView.findViewById(R.id.video_player_mask);
        mVideoPlayerView = (VideoPlayerView) rootView.findViewById(R.id.video_player_view);
        mVideoLoadingView = rootView.findViewById(R.id.video_progress_loading);
        mVideoProgressBar = (ProgressBar) rootView.findViewById(R.id.video_progress_bar);

        mRecyclerView = (DisableRecyclerView) rootView.findViewById(R.id.recycler_view);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
//        mRecyclerView.addOnScrollListener(mOnScrollListener);

        /*swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh:new ");
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                arrayListMoreLoad.clear();
                // getVideoList();
            }
        });*/

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
    }


    private void loadMore(final ArrayList<DataItem> arrayListMain, boolean firstTime) {
        if (firstTime) {
            recylerAdapter = new VideosFragment.RecyclerAdapter(mRecyclerView, getActivity(), arrayListMain);

            LinearLayoutManager llm = new LinearLayoutManager(getActivity());
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(llm);

            mRecyclerView.setAdapter(recylerAdapter);
            isLoadMoreVideo = true;
            if (ConstantManager.IS_WIFI) {
                if (ConstantManager.IS_FIRST_POSITION) {
                    postAndNotifyAdapter(new Handler(), mRecyclerView, 0);
                    ConstantManager.IS_FIRST_POSITION = false;
                }
            }

        } else {
            recylerAdapter.notifyDataSetChanged();
            recylerAdapter.setLoaded();
        }

        /*recylerAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (isLoadMoreVideo) {
                    getVideoListForLoding();
                }
            }
        });*/
    }


    protected void postAndNotifyAdapter(final Handler handler, final RecyclerView recyclerView, final int i) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (!recyclerView.isComputingLayout()) {
                    // This will call first item by calling "performClick()" of view.
                    try {
                        Thread.sleep(10);
                        if (ConstantManager.IS_WIFI) {
                            ((RecyclerAdapter.ItemViewHolder) recyclerView.findViewHolderForLayoutPosition(i)).itemView.performClick();
                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                }
            }
        });
    }

    private void createVideoControllerView() {
        if (mCurrentVideoControllerView != null) {
            mCurrentVideoControllerView.hide();
            mCurrentVideoControllerView = null;
        }
        mCurrentVideoControllerView = new VideoControllerView.Builder(getActivity(), mPlayerControlListener)
                .withVideoView(mVideoPlayerView)//to enable toggle display controller view
                .canControlBrightness(true)
                .canControlVolume(true)
                .resumePosition(mCurrentActiveVideoItem)
                .canSeekVideo(false)
                .exitIcon(R.drawable.video_top_back)
                .pauseIcon(R.drawable.ic_media_pause)
                .playIcon(R.drawable.ic_media_play)
                .shrinkIcon(R.drawable.ic_media_fullscreen_shrink)
                .stretchIcon(R.drawable.ic_media_fullscreen_stretch)
                .build(mVideoFloatContainer);//layout container that hold video play view
    }

    private VideoControllerView.MediaPlayerControlListener mPlayerControlListener = new VideoControllerView.MediaPlayerControlListener() {
        @Override
        public void start() {
            if (checkMediaPlayerInvalid())
                mVideoPlayerView.getMediaPlayer().start();
        }

        @Override
        public void pause() {
            try {
                mVideoPlayerView.getMediaPlayer().pause();
            } catch (Exception e) {
            }
        }

        @Override
        public int getDuration() {
            if (checkMediaPlayerInvalid()) {
                return mVideoPlayerView.getMediaPlayer().getDuration();
            }
            return 0;
        }

        @Override
        public int getCurrentPosition() {
            if (checkMediaPlayerInvalid()) {
                return mVideoPlayerView.getMediaPlayer().getCurrentPosition();
            }
            return 0;
        }

        @Override
        public void seekTo(int position) {
            if (checkMediaPlayerInvalid()) {
                mVideoPlayerView.getMediaPlayer().seekToPosition(position);
            }
        }

        @Override
        public boolean isPlaying() {
            if (checkMediaPlayerInvalid()) {
                return mVideoPlayerView.getMediaPlayer().isPlaying();
            }
            return false;
        }

        @Override
        public boolean isComplete() {
            return false;
        }

        @Override
        public int getBufferPercentage() {
            return mCurrentBuffer;
        }

        @Override
        public boolean isFullScreen() {
            return getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                    || getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        }

        @Override
        public void toggleFullScreen() {
            if (isFullScreen()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            } else {
                getActivity().setRequestedOrientation(Build.VERSION.SDK_INT < 9 ?
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE :
                        ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
            }
        }

        @Override
        public void exit() {
            //TODO to handle exit status
            if (isFullScreen()) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    };

    private boolean checkMediaPlayerInvalid() {
        return mVideoPlayerView != null && mVideoPlayerView.getMediaPlayer() != null;
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Ploodi onResume:");
        onResumeVideo();

    }

    public void onResumeVideo() {
        /*if (ConstantManager.IS_WIFI) {
            if (ConstantManager.lastPlayingPosition % 3 != 0 && ConstantManager.IS_FIRST_FOR_RESUME) {
                ConstantManager.IS_FIRST_POSITION = false;
            }
        }*/
        checkWifi();
        if (ConstantManager.IS_WIFI) {
            if (ConstantManager.IS_FIRST_FOR_RESUME) {
                ConstantManager.IS_FIRST_POSITION = false;
            } else {

                postAndNotifyAdapter(new Handler(), mRecyclerView, ConstantManager.lastPlayingPosition);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        onPauseVideo();
    }

    public void onPauseVideo() {
        ConstantManager.IS_FIRST_FOR_RESUME = true;
        mPlayerControlListener.pause();
    }


    public void stopPlaybackImmediately() {

        mIsClickToStop = false;
        if (mCurrentPlayArea != null) {
            mCurrentPlayArea.setClickable(true);
            Log.d(TAG, "stopPlaybackImmediately: 1");
        }
        if (mVideoPlayerManager != null) {
            Log.d(TAG, "stopPlaybackImmediately: 2");
            Log.e(TAG, "check play stopPlaybackImmediately");

            mVideoFloatContainer.setVisibility(View.INVISIBLE);
            mVideoPlayerManager.stopAnyPlayback();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (mVideoFloatContainer == null) return;

        ViewGroup.LayoutParams layoutParams = mVideoFloatContainer.getLayoutParams();

        mCurrentVideoControllerView.hide();

        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {

            //200 indicate the height of video play area
            layoutParams.height = (int) getResources().getDimension(R.dimen.video_item_portrait_height);
            layoutParams.width = Utils.getDeviceWidth(getActivity());
            ViewAnimator.putOn(mVideoFloatContainer).translationY(mOriginalHeight);
            // Show status bar
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            mRecyclerView.setEnableScroll(true);
        } else {
            layoutParams.height = Utils.getDeviceHeight(getActivity());
            layoutParams.width = Utils.getDeviceWidth(getActivity());
            Log.e(TAG, "onConfigurationChanged translationY:" + mVideoFloatContainer.getTranslationY());
            ViewAnimator.putOn(mVideoFloatContainer).translationY(0);
            // Hide status
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

            mRecyclerView.setEnableScroll(false);
        }
        mVideoFloatContainer.setLayoutParams(layoutParams);

    }

    @Override
    public void onRefresh() {
        Log.d(TAG, "Ploodi onRefresh:");
//        getVideoList();

    }


    private AudioManager.OnAudioFocusChangeListener focusChangeListener =
            new AudioManager.OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    switch (focusChange) {
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK):
                            // Lower the volume while ducking.
                            if (mediaPlayer != null) {
                                mediaPlayer.setVolume(0.2f, 0.2f);
                            }
                            break;
                        case (AudioManager.AUDIOFOCUS_LOSS_TRANSIENT):
                            if (mediaPlayer != null) {
                                mediaPlayer.pause();
                            }
                            break;

                        case (AudioManager.AUDIOFOCUS_LOSS):
                            if (mediaPlayer != null) {
                                mediaPlayer.stop();
                            }
                            break;
                        case (AudioManager.AUDIOFOCUS_GAIN):
                            // Return the volume to normal and resume if paused.
                            if (mediaPlayer != null) {
                                mediaPlayer.setVolume(1f, 1f);
                                mediaPlayer.start();
                            }
                            break;
                        default:
                            break;
                    }
                }
            };

    /**
     * Runnable for update current video progress
     * 1.start this runnable in {@link MediaPlayerWrapper.MainThreadMediaPlayerListener#onInfoMainThread(int)}
     * 2.stop(remove) this runnable in {@link MediaPlayerWrapper.MainThreadMediaPlayerListener#onVideoStoppedMainThread()}
     * {@link MediaPlayerWrapper.MainThreadMediaPlayerListener#onVideoCompletionMainThread()}
     * {@link MediaPlayerWrapper.MainThreadMediaPlayerListener#onErrorMainThread(int, int)} ()}
     */
    private Runnable mProgressRunnable = new Runnable() {
        @Override
        public void run() {
            if (mPlayerControlListener != null) {

                if (mCurrentVideoControllerView.isShowing()) {
                    mVideoProgressBar.setVisibility(View.GONE);
                } else {
                    mVideoProgressBar.setVisibility(View.GONE);
                }

                int position = mPlayerControlListener.getCurrentPosition();
                int duration = mPlayerControlListener.getDuration();
                if (duration != 0) {
                    long pos = 1000L * position / duration;
                    int percent = mPlayerControlListener.getBufferPercentage() * 10;
                    mVideoProgressBar.setProgress((int) pos);
                    mVideoProgressBar.setSecondaryProgress(percent);
                    mHandler.postDelayed(this, 1000);
                }
            }
        }
    };

    private void checkWifi() {
        try {
            ConnectivityManager conMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED) {
                ConstantManager.IS_WIFI = false;
            } else if (conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                ConstantManager.IS_WIFI = true;
            }
        } catch (Exception e) {

        }
    }

    /*private void getVideoList() {
        swipeRefreshLayout.setRefreshing(false);
        if (AndyUtils.isNetworkAvailable(getContext())) {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, "http://ploodi.eu-west-1.elasticbeanstalk.com/apis/FetchVideos.php?offset=0");
            new HttpRequester(getActivity(), map, AndyConstants.ServiceCode.LOGIN, this);
            AndyUtils.showSimpleProgressDialog(getActivity());
        } else {
            AndyUtils.DialogForNoInternetConnection(getActivity());
        }
    }*/

    /*private void getVideoListForLoding() {
        swipeRefreshLayout.setRefreshing(false);
        if (AndyUtils.isNetworkAvailable(getContext())) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, "http://jgcoe.org/App/LoadData.php?ApiName=Hot&ApiKey=cGl0cmFkZXZlZ2h5b25hbWFo");
            new HttpRequester(getActivity(), map, AndyConstants.ServiceCode.GET_VIDEOS, this);
//            AndyUtils.showSimpleProgressDialog(getActivity());
        } else {
            AndyUtils.DialogForNoInternetConnection(getActivity());
        }
    }*/

    private void setAdapter(ArrayList<DataItem> arrayListLoad, boolean firstTime) {
/*
        ArrayList<DataItem> newArrayList = setNewArraylistWithAd(arrayListMoreLoad);
        arrayListMoreLoad = newArrayList;*/

        loadMore(arrayListLoad, firstTime);
        mVideoPlayerView.addMediaPlayerListener(new MediaPlayerWrapper.MainThreadMediaPlayerListener() {
            @Override
            public void onVideoSizeChangedMainThread(int width, int height) {

            }

            @Override
            public void onVideoPreparedMainThread() {

                Log.e(MediaPlayerWrapper.VIDEO_TAG, "check play onVideoPreparedMainThread");
                mVideoFloatContainer.setVisibility(View.VISIBLE);
                mVideoPlayerView.setVisibility(View.VISIBLE);
                mVideoLoadingView.setVisibility(View.VISIBLE);
                //for cover the pre Video frame
                mVideoCoverMask.setVisibility(View.VISIBLE);
            }

            @Override
            public void onVideoCompletionMainThread() {

                Log.e(MediaPlayerWrapper.VIDEO_TAG, "check play onVideoCompletionMainThread");

                if (mCurrentPlayArea != null) {
                    mCurrentPlayArea.setClickable(true);
                }

                mVideoFloatContainer.setVisibility(View.INVISIBLE);
                mCurrentPlayArea.setVisibility(View.VISIBLE);
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

                ViewAnimator.putOn(mVideoFloatContainer).translationY(0);

                //stop update progress
                mVideoProgressBar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mProgressRunnable);
            }

            @Override
            public void onErrorMainThread(int what, int extra) {
                Log.e(MediaPlayerWrapper.VIDEO_TAG, "check play onErrorMainThread");
                if (mCurrentPlayArea != null) {
                    mCurrentPlayArea.setClickable(true);
                    mCurrentPlayArea.setVisibility(View.VISIBLE);
                }
                mVideoFloatContainer.setVisibility(View.INVISIBLE);

                //stop update progress
                mVideoProgressBar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mProgressRunnable);
            }

            @Override
            public void onBufferingUpdateMainThread(int percent) {
                Log.e(MediaPlayerWrapper.VIDEO_TAG, "check play onBufferingUpdateMainThread");
                mCurrentBuffer = percent;
            }

            @Override
            public void onVideoStoppedMainThread() {
                Log.e(MediaPlayerWrapper.VIDEO_TAG, "check play onVideoStoppedMainThread");
                if (!mIsClickToStop) {
                    mCurrentPlayArea.setClickable(true);
                    mCurrentPlayArea.setVisibility(View.VISIBLE);
                }

                //stop update progress
                mVideoProgressBar.setVisibility(View.GONE);
                mHandler.removeCallbacks(mProgressRunnable);
            }

            @Override
            public void onInfoMainThread(int what) {
                Log.e(MediaPlayerWrapper.VIDEO_TAG, "check play onInfoMainThread what:" + what);
                if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {

                    //start update progress
                    mVideoProgressBar.setVisibility(View.VISIBLE);
                    mHandler.post(mProgressRunnable);

                    mVideoPlayerView.setVisibility(View.VISIBLE);
                    mVideoLoadingView.setVisibility(View.GONE);
                    mVideoCoverMask.setVisibility(View.GONE);
                    mVideoPlayerBg.setVisibility(View.VISIBLE);
                    if (ConstantManager.arrayList == null) {
                        ConstantManager.arrayList = arrayListMoreLoad;
                    }
                    createVideoControllerView();

                    //mCurrentVideoControllerView.showWithTitle("VIDEO TEST - " + mCurrentActiveVideoItem);

                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
                    mVideoLoadingView.setVisibility(View.VISIBLE);
                } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
                    mVideoLoadingView.setVisibility(View.GONE);
                }
            }
        });
    }

    private ArrayList<DataItem> setNewArraylistWithAd(JSONArray arrayList) {
        int count = 0;
        ArrayList<DataItem> arrayListMain = new ArrayList<>();
        for (int i = 1; i <= arrayList.length(); i++) {
            if (i != 0 && i % 3 == 0) {
                count++;
            }
        }
        int arrayCount = 0;
        for (int i = 0; i < count + arrayList.length(); i++) {
            if (i != 0 && i % 3 == 0) {
                DataItem dataItem = new DataItem();
                dataItem.setTitle("ad");
                arrayListMain.add(dataItem);
            } else {
                DataItem dataItem = new DataItem();
                try {
                    dataItem.setVideoId(arrayList.getJSONObject(arrayCount).optString("video_id"));
                    dataItem.setTitle(arrayList.getJSONObject(arrayCount).optString("video_name"));
                    dataItem.setThumbnail(arrayList.getJSONObject(arrayCount).optString("video_thumbnail_link"));
                    dataItem.setVideo(arrayList.getJSONObject(arrayCount).optString("video_link"));
                    arrayListMain.add(dataItem);

                    arrayCount++;

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

      /*  if (arrayList.length() > 0) {
            for (int i = 0; i < arrayList.length(); i++) {

                Log.d(TAG, "onTaskCompleted:Pos-> "+i);

                DataItem dataItem = new DataItem();
                try {
                    dataItem.setVideoId(arrayList.getJSONObject(i).optString("video_id"));
                    dataItem.setTitle(arrayList.getJSONObject(i).optString("video_name"));
                    dataItem.setThumbnail(arrayList.getJSONObject(i).optString("video_thumbnail_link"));
                    dataItem.setVideo(arrayList.getJSONObject(i).optString("video_link"));
                    arrayListMoreLoad.add(dataItem);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }*/
        return arrayListMain;
    }

  /*  @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeSimpleProgressDialog();
        Log.d(TAG, "onTaskCompleted: response->" + response);
        switch (serviceCode) {
            case AndyConstants.ServiceCode.GET_VIDEOS:
                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.optString("Error").equals("0")) {

                        }
                        JSONArray jsonArray = new JSONArray(response);
                        JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                        AndyConstants.VIDEO_OFFSET = jsonArray1.length();
                        for (int i = 0; i < jsonArray1.length(); i++) {

                            if (i != 0 && i % 3 == 0) {
                                DataItem dataItem = new DataItem();
                                //dataItem.setVideoId(jsonArray1.getJSONObject(i).optString("ad"));
                                dataItem.setTitle("ad");
                                //dataItem.setThumbnail(jsonArray1.getJSONObject(i).optString("ad"));
                                dataItem.setVideo("ad");
                                arrayListMoreLoad.add(dataItem);
                            }
                            DataItem dataItem = new DataItem();
                            dataItem.setVideoId(jsonArray1.getJSONObject(i).optString("video_id"));
                            dataItem.setTitle(jsonArray1.getJSONObject(i).optString("video_name"));
                            dataItem.setThumbnail(jsonArray1.getJSONObject(i).optString("video_thumbnail_link"));
                            dataItem.setVideo(jsonArray1.getJSONObject(i).optString("video_link"));
                            arrayListMoreLoad.add(dataItem);
                        }
                        AndyConstants.VIDEO_LAST_SCROLL_POS = arrayListMoreLoad.size();
                        setAdapter(arrayListMoreLoad, true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;


            case AndyConstants.ServiceCode.EDIT_PROFILE:

                try {
                    AndyConstants.VIDEO_LAST_SCROLL_POS = arrayListMoreLoad.size();

                    JSONArray jsonArray = new JSONArray(response);
                    Log.d(TAG, "onTaskCompleted:JsonArray--->" + jsonArray.length());
                    JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray1.length(); i++) {
                            if (i != 0 && i % 3 == 0) {
                                DataItem dataItem = new DataItem();
                                //dataItem.setVideoId(jsonArray1.getJSONObject(i).optString("ad"));
                                dataItem.setTitle("ad");
                                //dataItem.setThumbnail(jsonArray1.getJSONObject(i).optString("ad"));
                                dataItem.setVideo("ad");
                                arrayListMoreLoad.add(dataItem);
                            }
                            DataItem dataItem = new DataItem();
                            dataItem.setVideoId(jsonArray1.getJSONObject(i).optString("video_id"));
                            dataItem.setTitle(jsonArray1.getJSONObject(i).optString("video_name"));
                            dataItem.setThumbnail(jsonArray1.getJSONObject(i).optString("video_thumbnail_link"));
                            dataItem.setVideo(jsonArray1.getJSONObject(i).optString("video_link"));
                            arrayListMoreLoad.add(dataItem);
                        }
                    }
                    AndyConstants.VIDEO_OFFSET = AndyConstants.VIDEO_OFFSET + jsonArray1.length();

                    setAdapter(arrayListMoreLoad, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }*/

    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        int totalDy;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                mOriginalHeight = mVideoFloatContainer.getTranslationY();
                totalDy = 0;

                if (RecyclerView.SCROLL_STATE_IDLE == newState) {
                    mOriginalHeight = mVideoFloatContainer.getTranslationY();
                    totalDy = 0;

                    LinearLayoutManager layoutManager = ((LinearLayoutManager) mRecyclerView.getLayoutManager());
                    int firstCompleteVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    int lastVisible = layoutManager.findLastVisibleItemPosition();
                    int lastCompleteVisible = layoutManager.findLastCompletelyVisibleItemPosition();

                    if (mCurrentActiveVideoItem <= mLayoutManager.findFirstVisibleItemPosition() ||
                            mCurrentActiveVideoItem >= mLayoutManager.findLastVisibleItemPosition()) {

                        if (ConstantManager.lastPlayingPosition != firstCompleteVisiblePosition &&
                                ConstantManager.lastPlayingPosition != lastCompleteVisible &&
                                firstCompleteVisiblePosition != -1 &&
                                lastCompleteVisible != -1 &&
                                mCurrentActiveVideoItem != firstCompleteVisiblePosition &&
                                mCurrentActiveVideoItem != lastCompleteVisible /*&&
                                !arrayListMoreLoad.get(firstCompleteVisiblePosition).getVideo().equalsIgnoreCase("ad")*/) {

                            if (mCanTriggerStop) {
                                mCanTriggerStop = false;
                                stopPlaybackImmediately();
                            }

                            if (firstCompleteVisiblePosition != -1 && firstCompleteVisiblePosition < list.size() /*&&
                                    !arrayListMoreLoad.get(firstCompleteVisiblePosition).getVideo().equalsIgnoreCase("ad")*/) {
                                Log.d(TAG, "onScrollStateChanged: 1->");
                                if (mCanTriggerStop) {
                                    mCanTriggerStop = false;
                                    stopPlaybackImmediately();
                                }
                                postAndNotifyAdapter(new Handler(), mRecyclerView, firstCompleteVisiblePosition);
                            } else if (lastCompleteVisible != -1 && lastCompleteVisible < list.size()/* &&
                                    !arrayListMoreLoad.get(lastCompleteVisible).getVideo().equalsIgnoreCase("ad")*/) {
                                Log.d(TAG, "onScrollStateChanged: 2->");
                                postAndNotifyAdapter(new Handler(), mRecyclerView, lastCompleteVisible);
                            } else if (firstCompleteVisiblePosition != -1 && firstCompleteVisiblePosition < list.size() /*&&
                                    arrayListMoreLoad.get(firstCompleteVisiblePosition).getVideo().equalsIgnoreCase("ad")*/) {

                                if (firstCompleteVisiblePosition > mCurrentActiveVideoItem) {
                                    Log.d(TAG, "onScrollStateChanged: 3->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, firstCompleteVisiblePosition + 1);
                                } else if (firstCompleteVisiblePosition < mCurrentActiveVideoItem) {
                                    Log.d(TAG, "onScrollStateChanged: 4->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, firstCompleteVisiblePosition - 1);
                                }
                            } else if (lastCompleteVisible != -1 && lastCompleteVisible < list.size() /*&&
                                    arrayListMoreLoad.get(lastCompleteVisible).getVideo().equalsIgnoreCase("ad")*/) {
                                if (lastCompleteVisible > mCurrentActiveVideoItem) {
                                    Log.d(TAG, "onScrollStateChanged: 5->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, lastCompleteVisible + 1);
                                } else if (lastCompleteVisible < mCurrentActiveVideoItem) {
                                    Log.d(TAG, "onScrollStateChanged: 6->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, lastCompleteVisible - 1);
                                }
                            } else if (firstCompleteVisiblePosition == -1 && lastCompleteVisible == -1) {
                                if (firstVisiblePosition != -1 && firstVisiblePosition < list.size()) {
                                    Log.d(TAG, "onScrollStateChanged: 7->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, firstVisiblePosition);
                                }
                                if (lastVisible != -1 && lastVisible < list.size()) {
                                    Log.d(TAG, "onScrollStateChanged: 8->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, lastVisible);
                                }
                            } else if (firstCompleteVisiblePosition == -1 || lastCompleteVisible == -1) {
                                if (firstCompleteVisiblePosition == -1 && lastCompleteVisible != -1) {
                                    Log.d(TAG, "onScrollStateChanged: 9->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, lastCompleteVisible);
                                } else if (lastCompleteVisible == -1 && firstCompleteVisiblePosition != -1) {
                                    Log.d(TAG, "onScrollStateChanged: 10->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, firstCompleteVisiblePosition);
                                }
                            }
                        } else if (firstCompleteVisiblePosition == -1 && lastCompleteVisible == -1) {
                            if (firstVisiblePosition != -1 && firstVisiblePosition < list.size()) {
                                if (ConstantManager.lastPlayingPosition != firstVisiblePosition) {
                                    if (mCanTriggerStop) {
                                        mCanTriggerStop = false;
                                        stopPlaybackImmediately();
                                    }
                                    Log.d(TAG, "onScrollStateChanged: 11->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, firstVisiblePosition);
                                }
                            } else if (lastVisible != -1 && lastVisible < list.size()) {
                                Log.d(TAG, "onScrollStateChanged: 12->");
                                postAndNotifyAdapter(new Handler(), mRecyclerView, lastVisible);
                            }
                        } else if (firstCompleteVisiblePosition == -1 || lastCompleteVisible == -1) {
                            if (mCanTriggerStop) {
                                mCanTriggerStop = false;
                                stopPlaybackImmediately();
                            }
                            if (firstCompleteVisiblePosition == -1 && lastCompleteVisible != -1) {
                                Log.d(TAG, "onScrollStateChanged: 13->");
                                postAndNotifyAdapter(new Handler(), mRecyclerView, lastCompleteVisible);
                            } else if (lastCompleteVisible == -1 && firstCompleteVisiblePosition != -1) {
                                Log.d(TAG, "onScrollStateChanged: 14->");
                                postAndNotifyAdapter(new Handler(), mRecyclerView, firstCompleteVisiblePosition);
                            }
                        } else if (mCurrentActiveVideoItem == firstCompleteVisiblePosition
                                && mCurrentActiveVideoItem == lastCompleteVisible) {
                            if (ConstantManager.lastPlayingPosition != list.size() - 1) {
                                if (mCanTriggerStop) {
                                    mCanTriggerStop = false;
                                    stopPlaybackImmediately();
                                }
                                if (firstVisiblePosition != -1 && firstVisiblePosition < list.size()) {
                                    Log.d(TAG, "onScrollStateChanged: 15->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, firstVisiblePosition);
                                }
                                if (lastVisible != -1 && lastVisible < list.size()) {
                                    Log.d(TAG, "onScrollStateChanged: 16->");
                                    postAndNotifyAdapter(new Handler(), mRecyclerView, lastVisible);
                                }
                            }
                        } else {
                            if (firstCompleteVisiblePosition != -1 &&
                                    firstCompleteVisiblePosition < list.size()/* &&
                                    !arrayListMoreLoad.get(firstCompleteVisiblePosition).getVideo().equalsIgnoreCase("ad")*/) {
                                if (mCanTriggerStop) {
                                    mCanTriggerStop = false;
                                    stopPlaybackImmediately();
                                }
                                Log.d(TAG, "onScrollStateChanged: 17->");
                                postAndNotifyAdapter(new Handler(), mRecyclerView, firstCompleteVisiblePosition);
                            } else if (lastCompleteVisible != -1 &&
                                    lastCompleteVisible < list.size() /*&&
                                    !arrayListMoreLoad.get(lastCompleteVisible).getVideo().equalsIgnoreCase("ad")*/) {
                                if (mCanTriggerStop) {
                                    mCanTriggerStop = false;
                                    stopPlaybackImmediately();
                                    Log.d(TAG, "onScrollStateChanged: 18.0->" + lastCompleteVisible);
                                }
                                Log.d(TAG, "onScrollStateChanged: 18->" + lastCompleteVisible);
                                postAndNotifyAdapter(new Handler(), mRecyclerView, lastCompleteVisible);
                            } else if ((firstCompleteVisiblePosition - 1) != -1 &&
                                    (firstCompleteVisiblePosition - 1) < list.size() /*&&
                                    !arrayListMoreLoad.get(firstCompleteVisiblePosition - 1).getVideo().equalsIgnoreCase("ad")*/) {
                                if (mCanTriggerStop) {
                                    mCanTriggerStop = false;
                                    stopPlaybackImmediately();
                                }
                                Log.d(TAG, "onScrollStateChanged: 19->");
                                postAndNotifyAdapter(new Handler(), mRecyclerView, firstCompleteVisiblePosition - 1);
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            if (mPlayerControlListener.isFullScreen()) return;

            //Calculate the total scroll distance of RecyclerView
            totalDy += dy;
            mMoveDeltaY = -totalDy;
            Log.e(TAG, "onScrolled scrollY:" + -totalDy);
            startMoveFloatContainer(false);

            if (mCurrentActiveVideoItem < mLayoutManager.findFirstVisibleItemPosition()
                    || mCurrentActiveVideoItem > mLayoutManager.findLastVisibleItemPosition()) {

                if (mCanTriggerStop) {
                    Log.d(TAG, "onScrollStateChanged onScrolled: 2");
                    mCanTriggerStop = false;
                    //stopPlaybackImmediately();
                }
            }
        }
    };

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private final ArrayList<DataItem> imageArrayList;

        private final int VIEW_TYPE_LOADING = 1;
        private OnLoadMoreListener onLoadMoreListener;
        private boolean isLoading;
        private Activity activity;
        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;

        public RecyclerAdapter(RecyclerView recyclerView, Activity activity, ArrayList<DataItem> imageArrayList) {
            this.imageArrayList = imageArrayList;
            this.activity = activity;
            this.notifyDataSetChanged();


            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView,
                                           int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            isLoading = true;
                        }
                    }
                });
            }

        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        public void setLoaded() {
            isLoading = false;
        }

        @Override
        public int getItemViewType(int position) {
            if (position != 0) {
                if (imageArrayList.get(position) == null) {
                    Log.d(TAG, "getItemViewType: adapter loading->" + position);
                    return VIEW_TYPE_LOADING;
                } /*else if (imageArrayList.get(position).getVideo().equalsIgnoreCase("ad")) {
                    Log.d(TAG, "getItemViewType: adapter ad->" + position);
                    return 12;
                }*/ else {
                    Log.d(TAG, "getItemViewType: adapter ->" + position);
                    return 11;
                }
            } else {
                return 11;
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view;
            if (viewType == 11) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_view, parent, false);
                return new RecyclerAdapter.ItemViewHolder(view);
            } else if (viewType == 12) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_layout, parent, false);
                return new RecyclerAdapter.ViewHolder2(view);
            } else if (viewType == VIEW_TYPE_LOADING) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                return new RecyclerAdapter.LoadingViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            final DataItem model = imageArrayList.get(position);

            if (holder.getItemViewType() == 11) {
                final RecyclerAdapter.ItemViewHolder itemViewHolder = (RecyclerAdapter.ItemViewHolder) holder;
                itemViewHolder.mVideoTitle.setText(model.getTitle());
                itemViewHolder.name.setText(model.getTitle());
                Picasso.with(activity).load(BaseUrl.MEDIA_SUFFIX + model.getThumb()).placeholder(R.drawable.shape_place_holder).into(itemViewHolder.cover);
                model.position = position;
                itemViewHolder.playArea.setTag(model);

                itemViewHolder.mImageViewShare.setTag(R.integer.view, itemViewHolder.mImageViewShare);
                itemViewHolder.mImageViewShare.setTag(R.integer.pos, position);
                Log.e("VideoUrl", BaseUrl.MEDIA_SUFFIX + model.getLink());
                Log.e("ImageUrl", BaseUrl.MEDIA_SUFFIX + model.getThumb());
                itemViewHolder.mImageViewShare.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View tempView = (View) v.getTag(R.integer.view);
                        Integer pos = (Integer) v.getTag(R.integer.pos);
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, "View video from below link \n\n" + BaseUrl.MEDIA_SUFFIX + model.getLink());
                        startActivity(Intent.createChooser(share, "Share this Video using..."));
                    }
                });

                itemViewHolder.itemView.setTag(R.integer.click, itemViewHolder.playArea);
                itemViewHolder.itemView.setTag(R.integer.clickPosition, position);
                itemViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View tempView = (View) v.getTag(R.integer.click);
                        Integer pos = (Integer) v.getTag(R.integer.clickPosition);
                        ConstantManager.arrayList = imageArrayList;
                        ConstantManager.happyPosition = pos;
                        playVideo(tempView, pos);
                    }
                });
                itemViewHolder.playArea.setTag(R.integer.click, itemViewHolder.playArea);
                itemViewHolder.playArea.setTag(R.integer.clickPosition, position);
                itemViewHolder.playArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        View tempView = (View) v.getTag(R.integer.click);
                        Integer pos = (Integer) v.getTag(R.integer.clickPosition);
                        ConstantManager.arrayList = imageArrayList;
                        ConstantManager.happyPosition = pos;
                        playVideo(tempView, pos);
                    }
                });

            } else if (holder instanceof RecyclerAdapter.LoadingViewHolder) {

                RecyclerAdapter.LoadingViewHolder loadingViewHolder = (RecyclerAdapter.LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
                loadingViewHolder.progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

            } else if (holder.getItemViewType() == 12) {

                RecyclerAdapter.ViewHolder2 viewHolder2 = (RecyclerAdapter.ViewHolder2) holder;
                AdRequest adRequest = new AdRequest.Builder().build();
                viewHolder2.mAdView.loadAd(adRequest);

            }
        }

        @Override
        public int getItemCount() {
            return imageArrayList.size();
        }


        public class ItemViewHolder extends RecyclerView.ViewHolder {

            public TextView name, mVideoTitle;
            public ImageView cover, mImageViewShare;
            public View playArea;

            public ItemViewHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.tv_video_name);
                mVideoTitle = (TextView) itemView.findViewById(R.id.mVideoTitle);
                cover = (ImageView) itemView.findViewById(R.id.img_cover);
                playArea = itemView.findViewById(R.id.layout_play_area);
                mImageViewShare = (ImageView) itemView.findViewById(R.id.mImageViewShare);
            }
        }


        public class ViewHolder2 extends RecyclerView.ViewHolder {
            private final AdView mAdView;

            public ViewHolder2(View itemView) {
                super(itemView);
                mAdView = (AdView) itemView.findViewById(R.id.adView);


            }
        }

        private class LoadingViewHolder extends RecyclerView.ViewHolder {
            public ProgressBar progressBar;

            public LoadingViewHolder(View view) {
                super(view);
                progressBar = (ProgressBar) view.findViewById(R.id.progressBar1);
            }
        }

        private void playVideo(View v, int position) {

            mIsClickToStop = true;
            v.setClickable(false);
            if (mCurrentPlayArea != null) {
                if (mCurrentPlayArea != v) {
                    mCurrentPlayArea.setClickable(true);
                    mCurrentPlayArea.setVisibility(View.VISIBLE);
                    mCurrentPlayArea = v;
                } else {//click same area
                    if (mVideoFloatContainer.getVisibility() == View.VISIBLE) return;
                }
            } else {
                mCurrentPlayArea = v;
            }

            //invisible self ,and make visible when video play completely
            v.setVisibility(View.INVISIBLE);
            if (mCurrentVideoControllerView != null)
                mCurrentVideoControllerView.hide();

            mVideoFloatContainer.setVisibility(View.VISIBLE);
            mVideoCoverMask.setVisibility(View.GONE);
            mVideoPlayerBg.setVisibility(View.GONE);

            DataItem model = (DataItem) v.getTag();
            mCurrentActiveVideoItem = model.position;
            mCanTriggerStop = true;
            Log.d(TAG, "playVideo: mCanTriggerStop:" + mCanTriggerStop);

            //move container view
            startMoveFloatContainer(true);
            mVideoLoadingView.setVisibility(View.VISIBLE);
            mVideoPlayerView.setVisibility(View.INVISIBLE);

            //play video

            AudioManager am = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
            if (am.isMusicActive()) {
                // Request audio focus for playback
                try {
                    int result = am.requestAudioFocus(focusChangeListener, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
                    if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                        // other app had stopped playing song now , so u can do u stuff now .
                    }
                } catch (Exception e) {
                }
            }
            ConstantManager.lastPlayingPosition = position;
            mVideoPlayerManager.playNewVideo(new CurrentItemMetaData(model.position, v), mVideoPlayerView, BaseUrl.MEDIA_SUFFIX + model.getLink());
        }
    }

    private void callVideoListAPI() {
        HashMap<String, String> requestParams = new HashMap<>();
        requestParams.put("ApiName", "Sad");
        requestParams.put("ApiKey", "cGl0cmFkZXZlZ2h5b25hbWFo");
        AndyUtils.showSimpleProgressDialog(getActivity());
        Call<JsonObject> apiResponse = ApiClient.getService().methodCallGet(BaseUrl.SUB_URL, requestParams);
        apiResponse.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                swipeRefreshLayout.setRefreshing(false);
                AndyUtils.removeSimpleProgressDialog();
                if (response.isSuccessful()) {
                    if (list != null) {
                        list.clear();
                    }
                    if (BaseUrl.isSuccess(response.body().get("Error").getAsString())) {
                        Type listType;
                        Gson gson = new Gson();
                        listType = new TypeToken<ArrayList<DataItem>>() {
                        }.getType();
                        list = gson.fromJson(response.body().get("result"), listType);
                        if (list != null && list.size() > 0) {
                            setAdapter(list, true);
                            Log.e("size", list.size() + "");
                        }
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
