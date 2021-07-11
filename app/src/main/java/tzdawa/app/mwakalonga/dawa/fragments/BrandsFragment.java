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
import tzdawa.app.mwakalonga.dawa.adapters.brands_list_fragment_adp;
import tzdawa.app.mwakalonga.dawa.models.fragment_items_brands;
import tzdawa.app.mwakalonga.dawa.webview.MainActivity;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BrandsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BrandsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private BroadcastReceiver broadcastReceiver;
    private String searchqueryText;
    private char fragvisibility;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView fgbrands_recyclerview;
    private ArrayList<fragment_items_brands> arrylstbrandlist;
    private brands_list_fragment_adp brands_list_fragment_adp;
    private ArrayList<String> brandslist_tokens;
    private ArrayList<String> brandslist_name;
    private AdRequest adRequest;

    private String search_text = ("");
    private View fgview;
    private AdView mAdView;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BrandsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BrandsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BrandsFragment newInstance(String param1, String param2) {
        BrandsFragment fragment = new BrandsFragment();
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
        //Toast.makeText(getActivity(), "on create view", Toast.LENGTH_SHORT).show();

        fgview = inflater.inflate(R.layout.fragment_brands, container, false);
        AndroidNetworking.initialize(Objects.requireNonNull(getContext()));
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                searchqueryText = intent.getStringExtra("tzdawa.app.mwakalonga.dawa_SEARCH_QUERY_TEXT");
                //Toast.makeText(context, searchqueryText, Toast.LENGTH_SHORT).show();
                // Log.e("BROADCAST RECEIVED", searchqueryText);

                if (fragvisibility == 1) {
                    //  Toast.makeText(context, "Begin search", Toast.LENGTH_SHORT).show();
                    // Toast.makeText(context, "Begin search on dawa fragment", Toast.LENGTH_SHORT).show();
                    // Log.e("BROADCAST_RECEIVED_FRA", searchqueryText);
                    search_text = (searchqueryText);
                    Toast.makeText(getActivity(), (String.format("Searching for \"%s\"..", searchqueryText)), Toast.LENGTH_SHORT).show();
                    loadbrandsapi(searchqueryText);

                }
            }
        };
        loadviews();
        loadbannerads();
        //loadinterstacialads();
        loadbrandsapi("");
        return fgview;
    }

    private void loadviews() {
        swipeRefreshLayout = fgview.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fgbrands_recyclerview.setVisibility(View.INVISIBLE);
            loadbrandsapi("");
        });


        fgbrands_recyclerview = fgview.findViewById(R.id.fgbrand_rcview);
        fgbrands_recyclerview.setVisibility(View.INVISIBLE);

        adRequest = new AdRequest.Builder().build();
    }

    private void loadbannerads() {
        MobileAds.initialize(getActivity(), initializationStatus -> {

        });

        mAdView = fgview.findViewById(R.id.fgdw_adView1);
        mAdView.loadAd(adRequest);
    }


    private void loadbrandsapi(String searchQuery) {
        swipeRefreshLayout.setRefreshing(true);
        fgbrands_recyclerview.setVisibility(View.INVISIBLE);

        AndroidNetworking.post((BuildConfig.SERVER_URL))
                .addBodyParameter("VIEW_ALL_BRAND_LIST", "1")
                .addBodyParameter("RANDOM_ORDER_FORMAT", "1")
                .addBodyParameter("BRAND_DAWA_COMBINATION", "1")
                .addBodyParameter("SEARCH_BRANDS_RELATE_DAWA", (searchQuery))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        fgbrands_recyclerview.setVisibility(View.VISIBLE);
                       // Log.e("BRAND_JSON", response.toString());

                        try {
                            JSONArray mJsonArray = new JSONArray(response.toString().trim());
                            JSONObject mJsonObject = new JSONObject();
                            brandslist_tokens = new ArrayList<>();
                            arrylstbrandlist = new ArrayList<>();
                            brandslist_name = new ArrayList<>();

                            fgbrands_recyclerview.setHasFixedSize(true);
                            fgbrands_recyclerview.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity())));
                            fgbrands_recyclerview.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), LinearLayout.VERTICAL));
                            int brands_list_length = mJsonArray.length();
                            if ((brands_list_length) < 1) {
                                Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
                            }

                            for (int i = 0; i < (brands_list_length); i++) {
                                mJsonObject = mJsonArray.getJSONObject(i);
                                arrylstbrandlist.add(new fragment_items_brands(
                                        (mJsonObject.getString("btoken")), (mJsonObject.getString("bname")),
                                        (mJsonObject.getString("bprice")), (mJsonObject.getString("bimage"))
                                ));
                                brandslist_tokens.add((mJsonObject.getString("btoken")));
                                brandslist_name.add((mJsonObject.getString("bname")));
                            }
                            brands_list_fragment_adp = new brands_list_fragment_adp(
                                    arrylstbrandlist, position -> {
                                //  Toast.makeText(getActivity(), (dawalist_tokens.get(position)), Toast.LENGTH_SHORT).show();
                                //   String webviewurl = (String.format("%s?viewdawa_details&dawaid=%s",BuildConfig.SERVER_URL,(dawalist_tokens.get(position))));
                                Intent detailspage = new Intent(getActivity(), MainActivity.class);
                                detailspage.putExtra("toolbartitle", (brandslist_name.get(position).toUpperCase()));
                                detailspage.putExtra("webviewurl", ("https://admkapp.000webhostapp.com/a-dawa/master-admin/detailspage.php?showbrandcontent&id=") + (brandslist_tokens.get(position)));
                                detailspage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(detailspage);

                            }
                            );
                            fgbrands_recyclerview.setAdapter(brands_list_fragment_adp);
                        } catch (JSONException e) {
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.networkfailure_error_message, Toast.LENGTH_SHORT).show();
                    }
                });
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

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
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