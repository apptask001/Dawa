package tzdawa.app.mwakalonga.dawa.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.ArrayList;
import java.util.Objects;

import tzdawa.app.mwakalonga.dawa.BuildConfig;
import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.models.fragment_items_brands;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PostsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PostsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BroadcastReceiver broadcastReceiver;
    private String searchqueryText;
    private Context context;
    private char fragvisibility;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private RecyclerView fgpst_recyclerview;
    private ProgressBar progressBar;
    private ArrayList<fragment_items_brands> arrylstbrandlist;
    private tzdawa.app.mwakalonga.dawa.adapters.brands_list_fragment_adp brands_list_fragment_adp;
    private RecyclerView.LayoutManager rllayoutManager;
    private ArrayList<String> brandslist_tokens;
    private ArrayList<String> brandslist_name;
    private int broadcastcount = 0;
    private AdRequest adRequest;

    private String search_text = ("");
    private String actsearch_text = ("0");
    private View fgview;
    private AdView mAdView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PostsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PostsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PostsFragment newInstance(String param1, String param2) {
        PostsFragment fragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fgview = inflater.inflate(R.layout.fragment_posts, container, false);
        return fgview;
    }

    private void loadbannerads() {
        MobileAds.initialize(getActivity(), initializationStatus -> {

        });

        mAdView = fgview.findViewById(R.id.fgpst_adView);
        mAdView.loadAd(adRequest);
    }

    private void loadviews() {
        swipeRefreshLayout = fgview.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                fgpst_recyclerview.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                loadpostsapi("");
            }
        });




        fgpst_recyclerview = fgview.findViewById(R.id.fgpst_rcview);
        fgpst_recyclerview.setVisibility(View.INVISIBLE);

        adRequest = new AdRequest.Builder().build();
    }

    private void loadpostsapi(String s) {

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            //Toast.makeText(getActivity(), "Brands fragment is visible to user", Toast.LENGTH_SHORT).show();
            fragvisibility = 1;
        } else {
            fragvisibility = 0;
        }
    }

}