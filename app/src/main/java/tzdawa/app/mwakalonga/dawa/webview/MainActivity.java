package tzdawa.app.mwakalonga.dawa.webview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Objects;

import tzdawa.app.mwakalonga.dawa.BuildConfig;
import tzdawa.app.mwakalonga.dawa.R;

public class MainActivity extends AppCompatActivity {
    private AdRequest adRequest;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView mawebview;
    private AdView mAdView;
    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_dawa);

        loadviews();
        loadbannerads();
        initializeapp();
        loadinterstacialads();

    }

    private void loadinterstacialads() {
        InterstitialAd.load(MainActivity.this, BuildConfig.ADMOB_INTERSTACIAL, adRequest, new InterstitialAdLoadCallback() {
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

    private void initializeapp() {
        swipeRefreshLayout.setRefreshing(true);
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
            loadinterstacialads();
        }
        Bundle extras = (getIntent().getExtras());
        if ((extras != null)) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(extras.getString("toolbartitle"));
            mawebview.loadUrl(extras.getString("webviewurl"));
            mawebview.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    super.onPageFinished(view, url);
                    swipeRefreshLayout.setRefreshing(false);
                }
            });
        }

    }

    private void loadbannerads() {
        MobileAds.initialize(MainActivity.this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {

            }
        });

        mAdView = findViewById(R.id.webv_adView);
        mAdView.loadAd(adRequest);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadviews() {
        mawebview = (WebView) findViewById(R.id.webv_webview);
        WebSettings webSettings = mawebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        adRequest = new AdRequest.Builder().build();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    loadinterstacialads();
                }
                initializeapp();
            }
        });
    }
}