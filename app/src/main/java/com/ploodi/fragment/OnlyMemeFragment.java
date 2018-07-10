package com.ploodi.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.ploodi.R;
import com.ploodi.parse.AsyncTaskCompleteListener;
import com.ploodi.parse.HttpRequester;
import com.ploodi.retrofit.APIClient;
import com.ploodi.retrofit.APIInterface;
import com.ploodi.retrofit.AndyUtils;
import com.ploodi.retrofit.ConstantManager;
import com.ploodi.retrofit.Response.Meme;
import com.ploodi.utills.AndyConstants;
import com.ploodi.widgets.OnLoadMoreListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

public class OnlyMemeFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, AsyncTaskCompleteListener {

    private static final String TAG = "OnlyMemeFragment";
    private View mrootView;
    public static RecyclerView mrecycler_view;
    private APIInterface apiInterface;
    //    private ArrayList<Meme> arrayList;
    MemeAdapter memeAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    ArrayList<Meme> arrayListMoreLoad = new ArrayList<>();
    private boolean isLoadMore = false;
    Bitmap myBitmap;
    String srcImage;

    public OnlyMemeFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_only_meme, container, false);
        mrootView = view;


        handler = new Handler();
        apiInterface = APIClient.getClient().create(APIInterface.class);
        mrecycler_view = (RecyclerView) mrootView.findViewById(R.id.mrecycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mrecycler_view.setLayoutManager(layoutManager);
        mrecycler_view.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) mrootView.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                swipeRefreshLayout.setRefreshing(false);
                arrayListMoreLoad.clear();
                getVideoList();
            }
        });

        Log.d(TAG, "onStart:MEME_VISIBLE_POSITION_FOR_RESUME->");
        if (ConstantManager.MEME_VISIBLE_POSITION_FOR_RESUME == 0) {
            Log.d(TAG, "onStart: meme");

        }
        getVideoList();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        getVideoList();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: meme");
        onResumeVideo();

    }

    public void onResumeVideo() {
        /*if (ConstantManager.IS_FIRST_FOR_RESUME_MEME) {
            ConstantManager.IS_FIRST_POSITION_MEME = false;
        }*/
//        mrecycler_view.scrollToPosition(ConstantManager.MEME_VISIBLE_POSITION_FOR_RESUME);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: meme");
        onPauseVideo();
    }

    public void onPauseVideo() {
        LinearLayoutManager layoutManager = ((LinearLayoutManager) mrecycler_view.getLayoutManager());
        int firstVisiblePosition = layoutManager.findFirstCompletelyVisibleItemPosition();
        ConstantManager.MEME_VISIBLE_POSITION_FOR_RESUME = firstVisiblePosition;
    }

    private void getVideoList() {
        if (AndyUtils.isNetworkAvailable(getContext())) {
            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, "http://ploodi.eu-west-1.elasticbeanstalk.com/apis/FetchMemes.php?offset=0");
            new HttpRequester(getActivity(), map, AndyConstants.ServiceCode.LOGIN, this);
            AndyUtils.showSimpleProgressDialog(getActivity());
        } else {
            AndyUtils.DialogForNoInternetConnection(getActivity());
        }
    }

    private void getVideoListMeme() {
        if (AndyUtils.isNetworkAvailable(getContext())) {

            HashMap<String, String> map = new HashMap<String, String>();
            map.put(AndyConstants.URL, "http://ploodi.eu-west-1.elasticbeanstalk.com/apis/FetchMemes.php?offset=" + AndyConstants.MEME_OFFSET);
            new HttpRequester(getActivity(), map, AndyConstants.ServiceCode.EDIT_PROFILE, this);
//            AndyUtils.showSimpleProgressDialog(getActivity());
        } else {
            AndyUtils.DialogForNoInternetConnection(getActivity());
        }
    }

    private void loadMoreMy(final ArrayList<Meme> arrayListMain, boolean firstTime) {
        final ArrayList<Meme> imagesItemArrayListToUseList;

        Log.d(TAG, "loadMoreMy: " + firstTime);
        if (firstTime) {
            memeAdapter = new MemeAdapter(mrecycler_view, getActivity(), arrayListMain, arrayListMoreLoad);
            mrecycler_view.setAdapter(memeAdapter);
            isLoadMore = true;
            Log.d(TAG, "loadMoreMy: in if");
        } else {
            Log.d(TAG, "loadMoreMy: else" + arrayListMoreLoad.size());
            memeAdapter.notifyDataSetChanged();
            memeAdapter.setLoaded();
        }

        memeAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore:::" + arrayListMain.size());
                if (isLoadMore) {
                    getVideoListMeme();
                }
            }
        });
    }


    private ArrayList<Meme> setNewArraylistWithAd(JSONArray arrayList) {
        int count = 0;
        ArrayList<Meme> arrayListMain = new ArrayList<>();
        for (int i = 1; i <= arrayList.length(); i++) {
            if (i != 0 && i % 3 == 0) {
                count++;
            }
        }
        int arrayCount = 0;
        for (int i = 0; i < count + arrayList.length(); i++) {
            if (i != 0 && i % 3 == 0) {
                Meme meme = new Meme();
                meme.setMeme_name("ad");
                meme.setMeme_id("");
                arrayListMain.add(meme);
            } else {
                Meme meme = new Meme();
                try {
                    meme.setMeme_id(arrayList.getJSONObject(arrayCount).optString("meme_id"));
                    meme.setMeme_link(arrayList.getJSONObject(arrayCount).optString("meme_link"));
                    meme.setMeme_name(arrayList.getJSONObject(arrayCount).optString("meme_name"));
                    arrayListMain.add(meme);
                    arrayCount++;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return arrayListMain;
    }

    @Override
    public void onTaskCompleted(String response, int serviceCode) {
        AndyUtils.removeSimpleProgressDialog();
        switch (serviceCode) {
            case AndyConstants.ServiceCode.LOGIN:
                if (response != null) {

                    try {
                        JSONArray jsonArray = new JSONArray(response);

                        JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                        AndyConstants.MEME_OFFSET = jsonArray1.length();

                      /*  Log.d(TAG, "onTaskCompleted: 1--->"+jsonArray1.length());

                        ArrayList<Meme> arrayListMain = setNewArraylistWithAd(jsonArray1);

                        arrayListMoreLoad = arrayListMain;
                        AndyConstants.MEME_LAST_SCROLL_POS = arrayListMoreLoad.size();

                        loadMoreMy(arrayListMoreLoad, true);*/

                        ArrayList<Meme> arrayListMain = new ArrayList<>();

                        for (int i = 0; i < jsonArray1.length(); i++) {

                            if (i != 0 && i % 3 == 0) {
                                Meme meme = new Meme();
                                meme.setMeme_name("ad");
                                meme.setMeme_id("");
                                arrayListMain.add(meme);
//                                arrayListMoreLoad.add(dataItem);
                            }
                            Meme meme = new Meme();
                            meme.setMeme_id(jsonArray1.getJSONObject(i).optString("meme_id"));
                            meme.setMeme_link(jsonArray1.getJSONObject(i).optString("meme_link"));
                            meme.setMeme_name(jsonArray1.getJSONObject(i).optString("meme_name"));
                            arrayListMain.add(meme);
                        }

                        arrayListMoreLoad.addAll(arrayListMain);
                        loadMoreMy(arrayListMoreLoad, true);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case AndyConstants.ServiceCode.EDIT_PROFILE:

                try {
                    AndyConstants.MEME_LAST_SCROLL_POS = arrayListMoreLoad.size();

                    JSONArray jsonArray = new JSONArray(response);
                    Log.d(TAG, "onTaskCompleted:JsonArray--->" + jsonArray.length());
                    JSONArray jsonArray1 = jsonArray.getJSONArray(1);
                    Log.d(TAG, "onTaskCompleted: 2--->"+jsonArray1.length());

                    AndyConstants.MEME_OFFSET = AndyConstants.MEME_OFFSET + jsonArray1.length();

//                    ArrayList<Meme> arrayListMain = setNewArraylistWithAd(jsonArray1);

                    ArrayList<Meme> arrayListMain = new ArrayList<>();

                    for (int i = 0; i < jsonArray1.length(); i++) {

                        if (i != 0 && i % 3 == 0) {
                            Meme meme = new Meme();
                            meme.setMeme_name("ad");
                            meme.setMeme_id("");
                            arrayListMain.add(meme);
//                                arrayListMoreLoad.add(dataItem);
                        }
                        Meme meme = new Meme();
                        meme.setMeme_id(jsonArray1.getJSONObject(i).optString("meme_id"));
                        meme.setMeme_link(jsonArray1.getJSONObject(i).optString("meme_link"));
                        meme.setMeme_name(jsonArray1.getJSONObject(i).optString("meme_name"));
                        arrayListMain.add(meme);
                    }

                    arrayListMoreLoad.addAll(arrayListMain);

                    loadMoreMy(arrayListMoreLoad, false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
    }

    public class MemeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final String TAG = "MemeAdapter";
        private final Activity activity;
        private final ArrayList<Meme> arrayList;

        private int visibleThreshold = 3;
        private int lastVisibleItem, totalItemCount;
        private boolean loading;
        private OnLoadMoreListener onLoadMoreListener;
        private final int VIEW_PROG = 0;


        // These matrices will be used to move and zoom image
        Matrix matrix = new Matrix();
        Matrix savedMatrix = new Matrix();

        // We can be in one of these 3 states
        static final int NONE = 0;
        static final int DRAG = 1;
        static final int ZOOM = 2;
        int mode = NONE;

        // Remember some things for zooming
        PointF start = new PointF();
        PointF mid = new PointF();
        float oldDist = 1f;
        String savedItemClicked;

        public MemeAdapter(RecyclerView recyclerView, Activity activity, final ArrayList<Meme> arrayList, final ArrayList<Meme> list) {
            this.activity = activity;
            this.arrayList = arrayList;
            this.notifyDataSetChanged();
            Log.d(TAG, "MemeAdapter: notifydatasetchange");
            if (recyclerView.getLayoutManager() instanceof LinearLayoutManager) {
                final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrolled(RecyclerView recyclerView,
                                           int dx, int dy) {
                        super.onScrolled(recyclerView, dx, dy);
                        totalItemCount = linearLayoutManager.getItemCount();
                        lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                        if (!loading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                            // End has been reached
                            // Do something
                            if (onLoadMoreListener != null) {
                                onLoadMoreListener.onLoadMore();
                            }
                            loading = true;
                        }
                    }
                });
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View view;
            if (viewType == 11) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meme, parent, false);
                return new ItemViewHolder(view);
            } else if (viewType == 12) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_layout, parent, false);
                return new ViewHolder2(view);
            } else if (viewType == VIEW_PROG) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(view);
            }
            return null;

        }

        @Override
        public int getItemCount() {
            return arrayList.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (position != 0) {

                if (arrayList.get(position) == null) {
                    Log.d(TAG, "getItemViewType: meme");
                    return VIEW_PROG;
                } else if (arrayList.get(position).getMeme_name().equalsIgnoreCase("ad")) {
                    return 12;
                } else {
                    return 11;
                }
            } else {
                return 11;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            if (holder.getItemViewType() == 11) {
                final ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
                itemViewHolder.tv_meme_name.setText(arrayList.get(position).getMeme_name());
                Picasso.with(activity)
                        .load(arrayList.get(position).getMeme_link())
                        .into(itemViewHolder.img_meme);

//                itemViewHolder.img_meme.setImageURI(Uri.parse(arrayList.get(position).getMeme_link()));

//                itemViewHolder.img_meme.setImage(ImageSource.bitmap(getBitmapFromURL(arrayList.get(position).getMeme_link())));

//                new DownloadImageTask(itemViewHolder.img_meme).execute(arrayList.get(position).getMeme_link());


                itemViewHolder.mImageViewShareMeme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent share = new Intent(android.content.Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                        share.putExtra(Intent.EXTRA_TEXT, "View meme from below link \n\n" + arrayList.get(position).getMeme_link());
                        share.putExtra(Intent.EXTRA_TEXT, "Check out this Meme  \n\napp.ploodi.com/viewMeme.php?img_name=" + arrayList.get(position).getMeme_name());

                        Log.d(TAG, "onClick: memelink->"+arrayList.get(position).getMeme_link());
                        Log.d(TAG, "onClick: memename->"+arrayList.get(position).getMeme_name());
                        activity.startActivity(Intent.createChooser(share, "Share this Meme using..."));

                    }
                });
            } else if (holder instanceof LoadingViewHolder) {
                LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
                loadingViewHolder.progressBar.setIndeterminate(true);
                loadingViewHolder.progressBar.getIndeterminateDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            } else if (holder instanceof ViewHolder2) {
                ViewHolder2 viewHolder2 = (ViewHolder2) holder;
                AdRequest adRequest = new AdRequest.Builder().build();
                viewHolder2.mAdView.loadAd(adRequest);
            }
        }

        public void setLoaded() {
            loading = false;
        }

        public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
            this.onLoadMoreListener = onLoadMoreListener;
        }

        public class ItemViewHolder extends RecyclerView.ViewHolder {
            //                                    private final ImageView img_meme;
            private final PhotoView img_meme;
            //            private final SubsamplingScaleImageView img_meme;
            private final TextView tv_meme_name;
            private final ImageView mImageViewShareMeme;
            private ScaleGestureDetector scaleGestureDetector;

            public ItemViewHolder(View itemView) {
                super(itemView);

                img_meme = itemView.findViewById(R.id.img_meme);
                tv_meme_name = itemView.findViewById(R.id.tv_meme_name);
                mImageViewShareMeme = itemView.findViewById(R.id.mImageViewShareMeme);

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

    }

}