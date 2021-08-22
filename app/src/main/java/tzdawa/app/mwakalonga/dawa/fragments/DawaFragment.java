package tzdawa.app.mwakalonga.dawa.fragments;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;

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
    private AdView mAdView1,mAdView2;
    private View fgview;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private RecyclerView fgdawa_recyclerview;

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
                    Toast.makeText(getActivity(), (String.format("Searching for \"%s\"..", searchqueryText)), Toast.LENGTH_SHORT).show();
                    loaddawaapi(searchqueryText);

                }
            }
        };

        loadviews();
        loadbannerads();
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
        swipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.post((BuildConfig.SERVER_URL))
                .addBodyParameter("VIEW_ALL_DAWA_LIST", "1")
                .addBodyParameter("RANDOM_ORDER_FORMAT", "1")
                .addBodyParameter("SEARCH_DAWA", "1")
                .addBodyParameter("DAWA_NAME", (searchQuery))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Log.e("demont_worked",  response.toString());
                        //progressBar.setVisibility(View.INVISIBLE);
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
                                arrylstdawalist.add(new fragment_items_dawa((mJsonObject.getString("dname")), (mJsonObject.getString("dothname"))));
                                dawalist_tokens.add((mJsonObject.getString("dtoken")));
                                dawalist_dawaname.add((mJsonObject.getString("dname")));
                            }
                            dawa_list_fragment_adp = new dawa_list_fragment_adp(
                                    arrylstdawalist, position -> {
                                //  Toast.makeText(getActivity(), (dawalist_tokens.get(position)), Toast.LENGTH_SHORT).show();
                                //   String webviewurl = (String.format("%s?viewdawa_details&dawaid=%s",BuildConfig.SERVER_URL,(dawalist_tokens.get(position))));
                                Intent detailspage = new Intent(getActivity(), MainActivity.class);
                                detailspage.putExtra("toolbartitle", (dawalist_dawaname.get(position).toUpperCase()));
                                detailspage.putExtra("webviewurl", ("https://admkapp.000webhostapp.com/a-dawa/master-admin/detailspage.php?showdawacontent&id=")+(dawalist_tokens.get(position)));
                                detailspage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(detailspage);

                            }
                            );
                            fgdawa_recyclerview.setAdapter(dawa_list_fragment_adp);
                        } catch (JSONException e) {
                            //progressBar.setVisibility(View.INVISIBLE);
                            swipeRefreshLayout.setRefreshing(false);
                            e.printStackTrace();
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }

                    @Override
                    public void onError(ANError anError) {
                       // Log.e("demont_failed", anError.getMessage());
                       // Log.e("DAWA_ERROR", anError.toString() );
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getActivity(), R.string.networkfailure_error_message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadviews() {
        swipeRefreshLayout = fgview.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fgdawa_recyclerview.setVisibility(View.INVISIBLE);
            loaddawaapi("");
        });

        fgdawa_recyclerview = fgview.findViewById(R.id.fgdawa_rcview);
        fgdawa_recyclerview.setVisibility(View.INVISIBLE);
        adRequest = new AdRequest.Builder().build();
    }

    private void loadbannerads() {
        MobileAds.initialize(Objects.requireNonNull(getActivity()), initializationStatus -> {
        });

        mAdView1 = fgview.findViewById(R.id.fgdw_adView1);
        mAdView1.loadAd(adRequest);

        mAdView2 = fgview.findViewById(R.id.fgdw_adView2);
        mAdView2.loadAd(adRequest);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            //Toast.makeText(getActivity(), "Brands fragment is visible to user", Toast.LENGTH_SHORT).show();
            fragvisibility = 1;
           // fgdawa_recyclerview.setVisibility(View.VISIBLE);
        } else {
            fragvisibility = 0;
          //  fgdawa_recyclerview.setVisibility(View.INVISIBLE);
        }
    }
}