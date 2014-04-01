package com.MeadowEast.audiotest;

import com.MeadowEast.R;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.webkit.WebView;


public class DisplayDict extends Activity
{
	
	private WebView webView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_dict);
		
		webView = (WebView) findViewById(R.id.webView1);
		webView.getSettings().setSupportZoom(true);  
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setDisplayZoomControls(false);
		//webViewwv.getSettings().setDefaultZoom(WebSettings.ZoomDensity.FAR);
		//webView.addView(iv);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.loadUrl("http://www.mdbg.net/chindict/chindict.php?page=worddict&wdrst=1&wdqb="+ MainActivity.hanzi.get(MainActivity.passkey));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_dict, menu);
		return true;
	}


	
}
