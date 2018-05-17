/*
 * Copyright (c) 2012-present, salesforce.com, inc.
 * All rights reserved.
 * Redistribution and use of this software in source and binary forms, with or
 * without modification, are permitted provided that the following conditions
 * are met:
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * - Neither the name of salesforce.com, inc. nor the names of its contributors
 * may be used to endorse or promote products derived from this software without
 * specific prior written permission of salesforce.com, inc.
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.absyz;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.salesforce.androidsdk.app.SalesforceSDKManager;
import com.salesforce.androidsdk.rest.ApiVersionStrings;
import com.salesforce.androidsdk.rest.RestClient;
import com.salesforce.androidsdk.rest.RestClient.AsyncRequestCallback;
import com.salesforce.androidsdk.rest.RestRequest;
import com.salesforce.androidsdk.rest.RestResponse;
import com.salesforce.androidsdk.ui.SalesforceActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

import static android.support.constraint.Constraints.TAG;

/**
 * Main activity
 */
public class MainActivity extends SalesforceActivity {

    private RestClient client;
	private TextView errorTextView;
	private String AskToLogout="Try To Login Out And Login Again";
	//change this URL if we are moving to production
	private String URL="https://test.salesforce.com/services/oauth2/token";
	private String Client_ID ="3MVG99S6MzYiT5k9Zi_aoc.dquXJmkC3UIOksfnVzO5qn9KqIJ3KfkOk0WMZr9uqDjeCEEx4bd43jhObsZPkn";
	private String Client_Secret ="8240518122990865023";
	private String TAG = "CLIMA";
	private WebView ConnectWebView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Setup view
		setContentView(R.layout.main);

	}
	
	@Override 
	public void onResume() {
		// Hide everything until we are logged in
		findViewById(R.id.root).setVisibility(View.INVISIBLE);

		super.onResume();
	}		
	
	@Override
	public void onResume(RestClient client) {
        // Keeping reference to rest client
        this.client = client;
        checkConn();
		// Show everything
        findViewById(R.id.ConnectWebViews).setVisibility(View.INVISIBLE);
		findViewById(R.id.root).setVisibility(View.INVISIBLE);
	}

	/**
	 * Called when "Logout" button is clicked. 
	 * 
	 * @param v
	 */
	public void onLogoutClick(View v) {
		SalesforceSDKManager.getInstance().logout(this);
	}
	public void Logout()
    {
        SalesforceSDKManager.getInstance().logout(this);
    }
	
public void checkConn()
{
    Log.d(TAG, "\n CheckForConnection: Old Access token"+client.getAuthToken());
    AsyncHttpClient Aclient = new AsyncHttpClient();
    RequestParams params = new RequestParams();
    params.add("grant_type","refresh_token");
    params.add("refresh_token",client.getRefreshToken());
    params.add("client_id",Client_ID);
    params.add("client_secret",Client_Secret);

    Aclient.post(URL,params,new JsonHttpResponseHandler(){
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

            Log.d("HTTP", "OnSuccess: "+statusCode+"Header"+headers+"Response"+response.toString());
            try {
                Toast.makeText(getApplicationContext(),"Logging in",Toast.LENGTH_SHORT).show();
                String communityUrl= String.valueOf(client.getClientInfo().communityUrl);
                System.out.println("community URL"+client.getClientInfo().communityUrl);
                Log.d(TAG, "\nonSuccess: New Access Token"+response.get("access_token"));
                String url = ""+ communityUrl +"/one/one.app?sid="+ response.get("access_token")+"";
                findViewById(R.id.root).setVisibility(View.VISIBLE);
                ConnectWebView=findViewById(R.id.ConnectWebViews);
                ConnectWebView.setWebViewClient(new WebViewClient());
                ConnectWebView.getSettings().setJavaScriptEnabled(true);
//
                Log.d(TAG, "onResume: first time loading");
                ConnectWebView.loadUrl(url);
                findViewById(R.id.ConnectWebViews).setVisibility(View.VISIBLE);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        @Override
        public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            Log.d("HTTP", "OnFailure: "+statusCode+"Header"+headers+"Response"+errorResponse.toString()+"Throwbable"+throwable.getMessage());
            Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
           // LoginError();
            Logout();

        }

    });

}

	public void CheckForConnection(View view) {
        Log.d(TAG, "\n CheckForConnection: Old Access token"+client.getAuthToken());
		AsyncHttpClient Aclient = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.add("grant_type","refresh_token");
		params.add("refresh_token",client.getRefreshToken());
		params.add("client_id",Client_ID);
		params.add("client_secret",Client_Secret);

		Aclient.post(URL,params,new JsonHttpResponseHandler(){
			@Override
			public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

				Log.d("HTTP", "OnSuccess: "+statusCode+"Header"+headers+"Response"+response.toString());
                try {
                    Toast.makeText(getApplicationContext(),"Logging in",Toast.LENGTH_SHORT).show();
                    String communityUrl= String.valueOf(client.getClientInfo().communityUrl);
                    System.out.println("community URL"+client.getClientInfo().communityUrl);
                    Log.d(TAG, "\nonSuccess: New Access Token"+response.get("access_token"));
                    String url = ""+ communityUrl +"/one/one.app?sid="+ response.get("access_token")+"";
//                    OpenWebView(url);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
			@Override
			public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

				Log.d("HTTP", "OnFailure: "+statusCode+"Header"+headers+"Response"+errorResponse.toString()+"Throwbable"+throwable.getMessage());
			    Toast.makeText(getApplicationContext(),throwable.getMessage(),Toast.LENGTH_SHORT).show();
                //LoginError();
			}

		});

	}

//    private void LoginError() {
//        errorTextView=findViewById(R.id.Error);
//        errorTextView.setText(AskToLogout);
//    }

//    public void OpenWebView(String url) {
//        Intent OpenWebView = new Intent(this,ConnectWebViewActivity.class);
//        OpenWebView.putExtra("URL",url);
//        Toast.makeText(getApplicationContext(),"Logging in",Toast.LENGTH_SHORT).show();
//        startActivity(OpenWebView);
//
//	}

}
