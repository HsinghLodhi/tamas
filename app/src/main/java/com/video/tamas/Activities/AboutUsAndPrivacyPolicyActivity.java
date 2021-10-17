package com.video.tamas.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.video.tamas.R;
import com.video.tamas.Utils.Config;

public class AboutUsAndPrivacyPolicyActivity extends AppCompatActivity {
    private WebView webView;
    private String pageName;
    private ProgressBar pbWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us_and_privacy_policy);
        webView = findViewById(R.id.wVAboutUsandpp);
        pbWebView = findViewById(R.id.pbWebView);
        pageName = getIntent().getExtras().getString("PAGE_NAME");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(pageName);
        actionBar.setDisplayHomeAsUpEnabled(true);
        pbWebView.setVisibility(View.VISIBLE);
        webView.setWebViewClient(new MyWebViewClient());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        if (pageName.equals("Privacy Policy")) {
            webView.loadUrl(Config.MethodName.privacyPolicy);
        } else if (pageName.equals("Terms And Condition")) {
            webView.loadUrl(Config.MethodName.TermsAndCondition);
        } else if (pageName.equals("Check Terms And Condition")) {
            actionBar.setTitle("Terms And Condition");
            webView.loadUrl(Config.MethodName.TermsAndCondition);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) // Press Back Icon
        {
            switch (pageName) {
                case "Privacy Policy":
                case "Terms And Condition":
                    startActivity(new Intent(AboutUsAndPrivacyPolicyActivity.this, SettingActivity.class));
                    finish();
                    break;
                case "Check Terms And Condition":
                    startActivity(new Intent(AboutUsAndPrivacyPolicyActivity.this, RegistrationActivity.class));
                    finish();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        switch (pageName) {
            case "Privacy Policy":
            case "Terms And Condition":
                startActivity(new Intent(AboutUsAndPrivacyPolicyActivity.this, SettingActivity.class));
                finish();
                break;
            case "Check Terms And Condition":
                startActivity(new Intent(AboutUsAndPrivacyPolicyActivity.this, RegistrationActivity.class));
                finish();
                break;
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            pbWebView.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            pbWebView.setVisibility(View.GONE);
            loadErrorPage(view, description);
        }
    }

    private void loadErrorPage(WebView webview, String description) {
        if (webview != null) {
            String htmlData = "<html><body><div align=\"center\" ><h3>" + description + "</h3></div></body>";
            webview.loadUrl("about:blank");
            webview.loadDataWithBaseURL(null, htmlData, "text/html", "UTF-8", null);
            webview.invalidate();
        }
    }
}
