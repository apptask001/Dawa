package tzdawa.app.mwakalonga.dawa.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import tzdawa.app.mwakalonga.dawa.BuildConfig;
import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.adapters.dawa_list_fragment_adp;
import tzdawa.app.mwakalonga.dawa.models.fragment_items_dawa;
import tzdawa.app.mwakalonga.dawa.webview.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DawaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DawaFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private AdView mAdView;
    private View fgview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private RecyclerView fgdawa_recyclerview;
    private ProgressBar progressBar;
    private ArrayList<fragment_items_dawa> arrylstdawalist;
    private dawa_list_fragment_adp dawa_list_fragment_adp;
    private RecyclerView.LayoutManager rllayoutManager;
    private ArrayList<String> dawalist_tokens;
    private ArrayList<String> dawalist_dawaname;
    private char fragvisibility;
    private BroadcastReceiver broadcastReceiver;
    private String searchqueryText;
    private int broadcastcount = 0;
    private AdRequest adRequest;
    private InterstitialAd mInterstitialAd;
    private String search_text = ("");
    private String actsearch_text = ("0");

    public DawaFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DawaFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DawaFragment newInstance(String param1, String param2) {
        DawaFragment fragment = new DawaFragment();
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
        fgview = inflater.inflate(R.layout.fragment_dawa, container, false);
        AndroidNetworking.initialize(Objects.requireNonNull(getContext()));

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchqueryText = intent.getStringExtra("tzdawa.app.mwakalonga.dawa_SEARCH_QUERY_TEXT");
                //Toast.makeText(context, searchqueryText, Toast.LENGTH_SHORT).show();
                // Log.e("BROADCAST_RECEIVED_FRA", searchqueryText);

                if (fragvisibility == 1) {
                    // Toast.makeText(context, "Begin search on dawa fragment", Toast.LENGTH_SHORT).show();
                    // Log.e("BROADCAST_RECEIVED_FRA", searchqueryText);
                    search_text = (searchqueryText);
                    actsearch_text = ("1");
                    Toast.makeText(getActivity(), (String.format("Searching for \"%s\"..", searchqueryText)), Toast.LENGTH_SHORT).show();
                    loaddawaapi(searchqueryText);
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(Objects.requireNonNull(getActivity()));
                        loadinterstacialads();
                    }
                }
            }
        };

        loadviews();
        loadbannerads();
        loadinterstacialads();
        loaddawaapi("");
        return fgview;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter("tzdawa.app.mwakalonga.dawa");
        Objects.requireNonNull(getActivity()).registerReceiver(broadcastReceiver, filter);
    }


    @Override
    public void onStop() {
        super.onStop();
        Objects.requireNonNull(getActivity()).unregisterReceiver(broadcastReceiver);
    }

    private void loaddawaapi(String searchQuery) {
        progressBar.setVisibility(View.VISIBLE);
        fgdawa_recyclerview.setVisibility(View.INVISIBLE);

        AndroidNetworking.get((BuildConfig.SERVER_URL))
                .addQueryParameter("viewall_dawa", "true")
                .addQueryParameter("advsearch", searchQuery)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        fgdawa_recyclerview.setVisibility(View.VISIBLE);

                    //    Log.e("dawa_list", response.toString());

                        try {
                            JSONArray mJsonArray = new JSONArray(response.toString().trim());
                            JSONObject mJsonObject = new JSONObject();
                            dawalist_tokens = new ArrayList<>();
                            arrylstdawalist = new ArrayList<>();
                            dawalist_dawaname = new ArrayList<>();

                            fgdawa_recyclerview.setHasFixedSize(true);
                            fgdawa_recyclerview.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity())));
                            fgdawa_recyclerview.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), LinearLayout.VERTICAL));
                            int dawa_list_length = mJsonArray.length();
                            if ((dawa_list_length) < 1) {
                                Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < dawa_list_length; i++) {
                                mJsonObject = mJsonArray.getJSONObject(i);
                                arrylstdawalist.add(new fragment_items_dawa((mJsonObject.getString("dn")), (mJsonObject.getString("don"))));
                                dawalist_tokens.add((mJsonObject.getString("dt")));
                                dawalist_dawaname.add((mJsonObject.getString("dn")));
                            }
                            dawa_list_fragment_adp = new dawa_list_fragment_adp(
                                    arrylstdawalist, new dawa_list_fragment_adp.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    //  Toast.makeText(getActivity(), (dawalist_tokens.get(position)), Toast.LENGTH_SHORT).show();
                                    //   String webviewurl = (String.format("%s?viewdawa_details&dawaid=%s",BuildConfig.SERVER_URL,(dawalist_tokens.get(position))));
                                    Intent detailspage = new Intent(getActivity(), MainActivity.class);
                                    detailspage.putExtra("toolbartitle", (dawalist_dawaname.get(position).toUpperCase()));
                                    detailspage.putExtra("webviewurl", ((String.format("%s?viewdawa_details&dawaid=%s&searchact=%s&searchtxt=%s", BuildConfig.SERVER_URL, (dawalist_tokens.get(position)),
                                            (actsearch_text), (search_text)
                                    ))));
                                    detailspage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                    startActivity(detailspage);

                                }

                            }
                            );
                            fgdawa_recyclerview.setAdapter(dawa_list_fragment_adp);
                        } catch (JSONException e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        progressBar.setVisibility(View.INVISIBLE);
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.networkfailure_error_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadviews() {
        swipeRefreshLayout = fgview.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Objects.requireNonNull(getActivity()));
                    loadinterstacialads();
                }
                fgdawa_recyclerview.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                loaddawaapi("");
            }
        });


        progressBar = fgview.findViewById(R.id.fgdawa_progressbar);
        progressBar.setVisibility(View.VISIBLE);

        fgdawa_recyclerview = fgview.findViewById(R.id.fgdawa_rcview);
        fgdawa_recyclerview.setVisibility(View.INVISIBLE);

        adRequest = new AdRequest.Builder().build();
    }

    private void loadbannerads() {
        MobileAds.initialize(getActivity(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        mAdView = fgview.findViewById(R.id.fgdw_adView);
        mAdView.loadAd(adRequest);
    }

    private void loadinterstacialads() {
        InterstitialAd.load(Objects.requireNonNull(getActivity()), BuildConfig.ADMOB_INTERSTACIAL, adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                // The mInterstitialAd reference will be null until
                // an ad is loaded.
                mInterstitialAd = interstitialAd;
                //Log.i(TAG, "onAdLoaded");
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                // Log.i(TAG, loadAdError.getMessage());
                mInterstitialAd = null;
            }
        });


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