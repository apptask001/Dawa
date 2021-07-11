package tzdawa.app.mwakalonga.dawa.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.viewpager.widget.ViewPager;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import tzdawa.app.mwakalonga.dawa.BuildConfig;
import tzdawa.app.mwakalonga.dawa.R;
import tzdawa.app.mwakalonga.dawa.home.ui.main.SectionsPagerAdapter;

import com.google.android.gms.ads.AdRequest;

import org.json.JSONArray;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ReviewManager reviewManager;
    private InterstitialAd mInterstitialAd;
    private AdRequest adRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        tabs.setSelectedTabIndicatorColor(Color.parseColor("#ffffff"));
        tabs.setSelectedTabIndicatorHeight((int) (3 * getResources().getDisplayMetrics().density));
        tabs.setTabTextColors(Color.parseColor("#DADADA"), Color.parseColor("#ffffff"));

        FloatingActionButton fab = findViewById(R.id.fab);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //Toast.makeText(MainActivity.this, String.valueOf(position), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        fab.setOnClickListener(view -> {
            /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();*/

            String[] myphonenumbers = {"WhatsApp", "Sms", "Live chat"};

            AlertDialog.Builder callbuilder = new AlertDialog.Builder(MainActivity.this);
            callbuilder.setTitle("Message using");
            callbuilder.setItems(myphonenumbers, (dialog, which) -> {
                // the user clicked on colors[which]

                switch (which) {
                    case 0:
                        openChromeCustomTabs("https://wa.me/255688445680");
                        break;

                    case 1:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", "255688445680", null)));
                        break;

                    case 2:
                        openChromeCustomTabs("https://tawk.to/chat/5e2805968e78b86ed8aa7ea6/default");
                        break;

                    default:
                        Toast.makeText(MainActivity.this, "UNKNOWN_SELECTION", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
            callbuilder.show();
        });

        bindxmlviews();
        loadCloudMessagingInit();
        displayads();

    }

    private void displayads() {
        adRequest = new AdRequest.Builder().build();
        MobileAds.initialize((MainActivity.this), initializationStatus -> {

        });

        InterstitialAd.load((MainActivity.this), (BuildConfig.ADMOB_INTERSTACIAL), (adRequest),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = (interstitialAd);
                        //Log.i("interstitialAd", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        // Log.i("interstitialAd", loadAdError.getMessage());
                        mInterstitialAd = (null);
                    }
                });
    }

    private void loadCloudMessagingInit() {

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(
                task -> {
                    SharedPreferences tkpref = getApplicationContext().getSharedPreferences("TOKEN_MANAGER", MODE_PRIVATE);
                   // Log.e("FBM_TOKEN", Objects.requireNonNull(task.getResult()));
//task.getResult()
                    if (((tkpref.getString("DEVICE_TOKEN_REGISTER", null)) == null) || (!(tkpref.getString("DEVICE_TOKEN_REGISTER", null).equals(task.getResult())))) {
                        AndroidNetworking.post((BuildConfig.SERVER_URL))
                                .addBodyParameter("ADD_NEW_FCM_TOKEN", "1")
                                .addBodyParameter("O_FCM_TOKEN", (Objects.requireNonNull(task.getResult()).trim()))
                                .setPriority(Priority.HIGH)
                                .build()
                                .getAsJSONArray(new JSONArrayRequestListener() {
                                    @Override
                                    public void onResponse(JSONArray response) {
                                        SharedPreferences.Editor tkeditor = tkpref.edit();
                                        tkeditor.putString("DEVICE_TOKEN_REGISTER", (task.getResult()));
                                        tkeditor.apply();
                                    }

                                    @Override
                                    public void onError(ANError anError) {
                                       // Log.e("FBM_TOKEN_ERROR", anError.getMessage());
                                    }
                                });
                    }

             /*
                    SharedPreferences.Editor tkeditor = tkpref.edit();
                    tkeditor.putString("device_token_register", (task.getResult()));
                    tkeditor.apply();
                        Toast.makeText(this, (tkpref.getString("device_token_register", null)), Toast.LENGTH_SHORT).show();
*/


                }

        );
    }

    private void sendasearchbroadcast(String queryText) {
        Intent intent = new Intent("tzdawa.app.mwakalonga.dawa");
        intent.putExtra("tzdawa.app.mwakalonga.dawa_SEARCH_QUERY_TEXT", queryText.trim());
        sendBroadcast(intent);
    }


    private void openChromeCustomTabs(String customTabsUrl) {
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, Uri.parse(customTabsUrl));
    }

    private void bindxmlviews() {
        AndroidNetworking.initialize(MainActivity.this);
        reviewManager = ReviewManagerFactory.create(MainActivity.this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //findViewById(R.id.btn_rate_app).setOnClickListener(view -> showRateApp());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.hometop, menu);

        MenuItem menuItem = menu.findItem(R.id.htsearch);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        searchView.setQueryHint("Tafuta ugonjwa, dawa..");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.isEmpty()) {
                    if (mInterstitialAd != null) {
                        mInterstitialAd.show(MainActivity.this);
                        displayads();
                    }
                    // Toast.makeText(MainActivity.this, "Searching for \"" + query.trim() + "\" ..", Toast.LENGTH_SHORT).show();
                    sendasearchbroadcast(query);
                } else {
                    Toast.makeText(MainActivity.this, "EMPTY_SEARCH_TEXT", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.htrateapp:
                showRateApp();
                break;

            case R.id.htshare:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share " + getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Download " + getString(R.string.app_name).toLowerCase() + " app now from https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
                break;

            case R.id.htprivacypolicy:
                String privacypolicyurl = "https://dawa-1.flycricket.io/privacy.html";
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                //  builder.setToolbarColor(Color.parseColor(String.valueOf(R.color.teal_200)));
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl(this, Uri.parse(privacypolicyurl));

           /*     Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://maktaba.flycricket.io/privacy.html"));
                startActivity(browserIntent);*/
                break;

            default:
                String options_menu_failed_error = "UNKNOWN_SELECTION";
                Toast.makeText(MainActivity.this, options_menu_failed_error, Toast.LENGTH_SHORT).show();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows rate app bottom sheet using In-App review API
     * The bottom sheet might or might not shown depending on the Quotas and limitations
     * https://developer.android.com/guide/playcore/in-app-review#quotas
     * We show fallback dialog if there is any error
     */
    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            } else {
                // There was some problem, continue regardless of the result.
                // show native rate app dialog on error
                showRateAppFallbackDialog();
            }
        });
    }

    /**
     * Showing native dialog with three buttons to review the app
     * Redirect user to playstore to review the app
     */
    private void showRateAppFallbackDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle(R.string.rate_app_title)
                .setMessage(R.string.rate_app_message)
                .setPositiveButton(R.string.rate_btn_pos, (dialog, which) -> {
                    Uri uri = Uri.parse("market://details?id=" + getApplicationContext().getPackageName());
                    Intent appmarketlink = new Intent(Intent.ACTION_VIEW, uri);
                    try {
                        startActivity(appmarketlink);
                    } catch (ActivityNotFoundException e) {
                        String notfounderror = "MARKET_APP_NOT_FOUND";
                        //  Toasty.error(getApplicationContext(), notfounderror, Toast.LENGTH_SHORT, true).show();
                        Toast.makeText(MainActivity.this, notfounderror, Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton(R.string.rate_btn_neg,
                        (dialog, which) -> {
                        })
                .setNeutralButton(R.string.rate_btn_nut,
                        (dialog, which) -> {
                        })
                .setOnDismissListener(dialog -> {
                })
                .show();
    }
}