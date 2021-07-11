package tzdawa.app.mwakalonga.dawa.webview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Objects;

import tzdawa.app.mwakalonga.dawa.R;

public class MainActivity extends AppCompatActivity {
    private AdRequest adRequest;
    private SwipeRefreshLayout swipeRefreshLayout;
    private WebView mawebview;
    private AdView mAdView1,mAdView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_dawa);

        loadviews();
        loadbannerads();
        initializeapp();
    }


    private void initializeapp() {
        swipeRefreshLayout.setRefreshing(true);
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
        MobileAds.initialize((MainActivity.this), initializationStatus -> {

        });

        mAdView1 = findViewById(R.id.webv_adView1);
        mAdView1.loadAd(adRequest);

        mAdView2 = findViewById(R.id.webv_adView2);
        mAdView2.loadAd(adRequest);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadviews() {
        mawebview = (WebView) findViewById(R.id.webv_webview);
        WebSettings webSettings = mawebview.getSettings();
        webSettings.setJavaScriptEnabled(true);

        adRequest = new AdRequest.Builder().build();
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this::initializeapp);
    }
}