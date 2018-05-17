package com.absyz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import static android.support.constraint.Constraints.TAG;

public class ConnectWebViewActivity extends SalesforceActivity {
    private WebView ConnectWebView;
    private boolean savedInstance=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_web_view);
        ConnectWebView=findViewById(R.id.ConnectWebView);
        if(savedInstanceState==null)
        {
            savedInstance=true;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        ConnectWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        ConnectWebView.restoreState(savedInstanceState);
    }

    @Override
    public void onResume(RestClient client) {
        Intent url = getIntent();
        String AccessURL=url.getStringExtra("URL");
        ConnectWebView.setWebViewClient(new WebViewClient());
        ConnectWebView.getSettings().setJavaScriptEnabled(true);
        if(savedInstance) {
            Log.d(TAG, "onResume: first time loading");
            ConnectWebView.loadUrl(AccessURL);
        }
    }
}
