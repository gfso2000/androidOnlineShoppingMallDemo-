package com.gfso.client.oauthclientapplication.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gfso.client.oauthclientapplication.CaptureActivityPortrait;
import com.gfso.client.oauthclientapplication.MyApplication;
import com.gfso.client.oauthclientapplication.R;
import com.gfso.client.oauthclientapplication.bean.Movie;
import com.gfso.client.oauthclientapplication.fragment.recycleview.MoviesAdapter;
import com.gfso.client.oauthclientapplication.fragment.recycleview.RecyclerItemTouchHelper;
import com.gfso.client.oauthclientapplication.fragment.recycleview.RecyclerTouchListener;
import com.gfso.client.oauthclientapplication.fragment.task.ScanLoginTask;
import com.gfso.client.oauthclientapplication.okhttp.OkhttpHelper;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeFragment extends Fragment implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener{
    @BindView(R.id.head_scan_search_notification_layout_entire)
    View headView;
    @BindView(R.id.head_scan_search_notification_btn_search)
    Button searchButton;
    @BindView(R.id.scanbtn)
    TextView scanButton;
    @BindView(R.id.notificationbtn)
    TextView notificationButton;
    @BindView(R.id.head_scan_search_notification_layout_scan)
    LinearLayout scanLayout;

    @BindView(R.id.fragment_home_recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_home_layout_entire)
    RelativeLayout entireHomeLayout;
    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;

    AppCompatActivity activity = null;
    private int mDistanceY;
    private List<Movie> movieList = new ArrayList<>();
    private MoviesAdapter mAdapter;
    OkhttpHelper okhttpHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        activity = (AppCompatActivity)this.getActivity();
        okhttpHelper = OkhttpHelper.getOkhttpHelper() ;
        ButterKnife.bind(this, view);
        InitUI(inflater, view);

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        activity.getSupportActionBar().setTitle("toolbar title");
        initCollapsingToolbar(inflater, view);

        whiteScanSearchNotificationButton();
        return view;
    }

    private void InitUI(LayoutInflater inflater, View view) {
        final Fragment me = this;
        initHeader();

        /**
         * 滑动标题栏渐变, I use collapsible toolbar to control include_head_scan_search_notification bgcolor
         */
//        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                //滑动的距离
//                mDistanceY += dy;
//                //toolbar的高度
//                int toolbarHeight = headView.getBottom();
//
//                //当滑动的距离 <= toolbar高度的时候，改变Toolbar背景色的透明度，达到渐变的效果
//                if (mDistanceY <= toolbarHeight) {
//                    float scale = (float) mDistanceY / toolbarHeight;
//                    float alpha = scale * 255;
//                    headView.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
//                    Log.e("alpha", alpha+"");
//                    if(Math.abs(scale)>=0.8){
//                        blackScanSearchNotificationButton();
//                    } else {
//                        whiteScanSearchNotificationButton();
//                    }
//                } else {
//                    //将标题栏的颜色设置为完全不透明状态
//                    headView.setBackgroundResource(R.color.white);
//                    blackScanSearchNotificationButton();
//                }
//            }
//        });

        initRecyclerView(view, me);
    }

    private void initHeader(){
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, "Hello", Toast.LENGTH_SHORT).show();
            }
        });

        final Fragment me = this;
        scanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator intentIntegrator = IntentIntegrator.forSupportFragment(me);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setCaptureActivity(CaptureActivityPortrait.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    private void initCollapsingToolbar(LayoutInflater inflater, View view) {
        final CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(" aaa");
        appBarLayout.setExpanded(true);

        // hiding & showing the title when toolbar expanded & collapsed
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbar.setTitle(" show title");
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbar.setTitle(" aaa");
                    isShow = false;
                }
                float scale = 1 - (float) appBarLayout.getBottom() / appBarLayout.getHeight();
                float alpha = scale * 255;
                headView.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                if(appBarLayout.getBottom()<=300){
                    blackScanSearchNotificationButton();
                } else {
                    whiteScanSearchNotificationButton();
                }
            }
        });

    }

    private void whiteScanSearchNotificationButton() {
        searchButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        scanButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        notificationButton.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
    }

    private void blackScanSearchNotificationButton() {
        searchButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        scanButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        notificationButton.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
    }

    private void initRecyclerView(View view, final Fragment me) {
        mAdapter = new MoviesAdapter(activity, movieList);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(activity, 2);//new LinearLayoutManager(getApplicationContext());
        //RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(activity, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(me.getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Movie movie = movieList.get(position);
                Toast.makeText(me.getContext(), movie.getTitle() + " is selected!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);
        prepareMovieData();

        try {
            Glide.with(this).load(R.drawable.cover).into((ImageView) view.findViewById(R.id.backdrop));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(activity, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                String urlStr = result.getContents();
                try {
                    //have to call http in another thread, otherwise it will throw exception(networkOnMainThread)
                    ScanLoginTask task = new ScanLoginTask(activity);
                    String userId = MyApplication.getInstance().getUser().getUsername();
                    String token = MyApplication.getInstance().getToken();
                    task.execute(urlStr, userId, token);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof MoviesAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar
            String name = movieList.get(viewHolder.getAdapterPosition()).getTitle();

            // backup of removed item for undo purpose
            final Movie deletedItem = movieList.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            mAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(entireHomeLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    // undo is selected, restore the deleted item
                    mAdapter.restoreItem(deletedItem, deletedIndex);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.album_filter, menu);
//
//        // Associate searchable configuration with the SearchView
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        headView = (SearchView) menu.findItem(R.id.action_search).getActionView();
//        headView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//        headView.setMaxWidth(Integer.MAX_VALUE);
//
//        // listening to search query text change
//        headView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                // filter recycler view when query submitted
//                mAdapter.getFilter().filter(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//                // filter recycler view when text is changed
//                mAdapter.getFilter().filter(query);
//                return false;
//            }
//        });
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        } else if(id == android.R.id.home) {
//            onBackPressed();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

//    @Override
//    public void onBackPressed() {
//        // close search view on back button pressed
//        if (!headView.isIconified()) {
//            headView.setIconified(true);
//            return;
//        }
//        super.onBackPressed();
//    }

    public interface ClickListener {
        void onClick(View view, int position);
        void onLongClick(View view, int position);
    }

    private void prepareMovieData() {
        Movie movie = new Movie("Mad Max: Fury Road", "Action & Adventure", "2015");
        movieList.add(movie);

        movie = new Movie("Inside Out", "Animation, Kids & Family", "2015");
        movieList.add(movie);

        movie = new Movie("Star Wars: Episode VII - The Force Awakens", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Shaun the Sheep", "Animation", "2015");
        movieList.add(movie);

        movie = new Movie("The Martian", "Science Fiction & Fantasy", "2015");
        movieList.add(movie);

        movie = new Movie("Mission: Impossible Rogue Nation", "Action", "2015");
        movieList.add(movie);

        movie = new Movie("Up", "Animation", "2009");
        movieList.add(movie);

        movie = new Movie("Star Trek", "Science Fiction", "2009");
        movieList.add(movie);

        movie = new Movie("The LEGO Movie", "Animation", "2014");
        movieList.add(movie);

        movie = new Movie("Iron Man", "Action & Adventure", "2008");
        movieList.add(movie);

        movie = new Movie("Aliens", "Science Fiction", "1986");
        movieList.add(movie);

        movie = new Movie("Chicken Run", "Animation", "2000");
        movieList.add(movie);

        movie = new Movie("Back to the Future", "Science Fiction", "1985");
        movieList.add(movie);

        movie = new Movie("Raiders of the Lost Ark", "Action & Adventure", "1981");
        movieList.add(movie);

        movie = new Movie("Goldfinger", "Action & Adventure", "1965");
        movieList.add(movie);

        movie = new Movie("Guardians of the Galaxy", "Science Fiction & Fantasy", "2014");
        movieList.add(movie);

        mAdapter.notifyDataSetChanged();
    }
}
