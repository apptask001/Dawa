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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import tzdawa.app.mwakalonga.dawa.BuildConfig;
import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.adapters.posts_list_fragment_adp;
import tzdawa.app.mwakalonga.dawa.models.fragment_items_posts;
import tzdawa.app.mwakalonga.dawa.webview.MainActivity;

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
    private ArrayList<fragment_items_posts> arrylstpostlist;
    private tzdawa.app.mwakalonga.dawa.adapters.posts_list_fragment_adp posts_list_fragment_adp;
    private RecyclerView.LayoutManager rllayoutManager;
    private ArrayList<String> postSelected_tokens, postSelected_title;

    private final int broadcastcount = 0;
    private AdRequest adRequest;

    private String search_text = ("");
    private final String actsearch_text = ("0");
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
        fgview = inflater.inflate(R.layout.fragment_posts, container, false);
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
                    loadpostsapi_search(searchqueryText);

                }
            }
        };
        adRequest = new AdRequest.Builder().build();
        loadbannerads();
        loadviews();
        loadpostsapi("");
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

    private void loadbannerads() {
        MobileAds.initialize(Objects.requireNonNull(getActivity()), initializationStatus -> {

        });

        mAdView = fgview.findViewById(R.id.fgdw_adView1);
        mAdView.loadAd(adRequest);
    }

    private void loadviews() {
        fgpst_recyclerview = fgview.findViewById(R.id.fgpost_rcview);
        //fgpst_recyclerview.setVisibility(View.INVISIBLE);

        swipeRefreshLayout = fgview.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //fgpst_recyclerview.setVisibility(View.INVISIBLE);
                // progressBar.setVisibility(View.VISIBLE);
                loadpostsapi("");
            }
        });
    }

    private void loadpostsapi_search(String s) {
        swipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.post((BuildConfig.SERVER_URL))
                .addBodyParameter("VIEW_ALL_ADMIN_POST", "1")
                .addBodyParameter("SEARCH_POST", s.trim().toUpperCase())
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Log.e("demont_worked",  response.toString());
                        String responce_string = response.toString();
                        displayRecyclerView(responce_string);

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


    private void loadpostsapi(String s) {
        swipeRefreshLayout.setRefreshing(true);
        AndroidNetworking.post((BuildConfig.SERVER_URL))
                .addBodyParameter("VIEW_ALL_ADMIN_POST", "1")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Log.e("demont_worked",  response.toString());
                        String responce_string = response.toString();
                        displayRecyclerView(responce_string);

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

    private void displayRecyclerView(String response) {
        arrylstpostlist = new ArrayList<>();
        postSelected_tokens = new ArrayList<>();
        postSelected_title = new ArrayList<>();
        //progressBar.setVisibility(View.INVISIBLE);
        fgpst_recyclerview.setVisibility(View.VISIBLE);

        //    Log.e("dawa_list", response.toString());

        try {
            JSONArray mJsonArray = new JSONArray(response.trim());
            JSONObject mJsonObject = new JSONObject();


            fgpst_recyclerview.setHasFixedSize(true);
            fgpst_recyclerview.setLayoutManager(new LinearLayoutManager(Objects.requireNonNull(getActivity())));
            //fgpst_recyclerview.addItemDecoration(new DividerItemDecoration(Objects.requireNonNull(getActivity()), LinearLayout.VERTICAL));
            int post_list_length = mJsonArray.length();
            if ((post_list_length) < 1) {
                Toast.makeText(getActivity(), "No results found", Toast.LENGTH_SHORT).show();
            }

            for (int i = 0; i < post_list_length; i++) {
                mJsonObject = mJsonArray.getJSONObject(i);
                arrylstpostlist.add(
                        new fragment_items_posts(
                                (mJsonObject.getString("pname")),
                                (mJsonObject.getString("pthmbimg"))

                        ));
                postSelected_tokens.add((mJsonObject.getString("ptoken")));
                postSelected_title.add((mJsonObject.getString("pname")));
            }
            posts_list_fragment_adp = new posts_list_fragment_adp(
                    arrylstpostlist, position -> {
                //  Toast.makeText(getActivity(), (dawalist_tokens.get(position)), Toast.LENGTH_SHORT).show();
                //   String webviewurl = (String.format("%s?viewdawa_details&dawaid=%s",BuildConfig.SERVER_URL,(dawalist_tokens.get(position))));
                Intent detailspage = new Intent(getActivity(), MainActivity.class);
                detailspage.putExtra("toolbartitle", (postSelected_title.get(position).toUpperCase()));
                detailspage.putExtra("webviewurl", ("https://admkapp.000webhostapp.com/a-dawa/master-admin/detailspage.php?showpostcontent&id=") + (postSelected_tokens.get(position)));
                detailspage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(detailspage);


            }
            );
            fgpst_recyclerview.setAdapter(posts_list_fragment_adp);
        } catch (JSONException e) {
            //progressBar.setVisibility(View.INVISIBLE);
            swipeRefreshLayout.setRefreshing(false);
            e.printStackTrace();
        }
        swipeRefreshLayout.setRefreshing(false);
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