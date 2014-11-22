package com.iesebre.dam2.pa201415.ivan.facegoogtwitsign1;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;




public class MainActivity extends Activity {
	// Constants
	/**
	 * Register your here app https://dev.twitter.com/apps/new and get your
	 * consumer key and secret
	 * */
	static String TWITTER_CONSUMER_KEY = "jhWYET4K8vfngTAs6xameIjDT"; // place your cosumer key here
	static String TWITTER_CONSUMER_SECRET = "UYDlUoXHHY5MaIN1jjgBp0V6dVJpkoHaGrLPzHV82Pk7LQ3RUQ"; // place your consumer secret here

	// Preference Constants
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";

	// Twitter oauth urls
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";

	// Login button
	Button btnTwitter;


	// Progress dialog
	ProgressDialog pDialog;

	// Twitter
	private static Twitter twitter;
	private static RequestToken requestToken;
	
	// Shared Preferences
	private static SharedPreferences mSharedPreferences;
	
	// Internet Connection detector
	private ConnectionDetector cd;
	
	// Alert Dialog Manager
	AlertDialogManager alert = new AlertDialogManager();
	
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//BEGIN TWITER CODE
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy =
			new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		// Check if twitter keys are set
		if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
			// Internet Connection is not present
			alert.showAlertDialog(MainActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
			// stop executing code by return
			return;
		}

		// All UI elements
		btnTwitter = (Button) findViewById(R.id.btnTwitter);
	

		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);

		/**
		 * Twitter login button click event will call loginToTwitter() function
		 * */
		btnTwitter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Call login twitter function
				//new LoginToTwitter().execute();
				loginToTwitter();
			}
		});

	

	

		/** This if conditions is tested once is
		 * redirected from twitter page. Parse the uri to get oAuth
		 * Verifier
		 * */
		if (!isTwitterLoggedInAlready()) {
			Uri uri = getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					AccessToken accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET,
							accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
					 //AQUI ACCIONES PARA HACER SI HAY UN LOGIN
                    Intent login = new Intent(MainActivity.this,LoginSuccess.class);
                    this.startActivity(login);
						
				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}

	}

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		// Check if already logged in
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
			builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
			Configuration configuration = builder.build();
			
			TwitterFactory factory = new TwitterFactory(configuration);
			twitter = factory.getInstance();

			try {
				requestToken = twitter
						.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				this.startActivity(new Intent(Intent.ACTION_VIEW, Uri
						.parse(requestToken.getAuthenticationURL())));
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		} else {
			// user already logged into twitter
			Toast.makeText(getApplicationContext(),
					"Already Logged into twitter", Toast.LENGTH_LONG).show();
			// Hide login button
			//ACCIONES SI YA ESTÁ LOGGEADO EN TWITTER
            //Creamos un intent para mandarlo a la actividad correspondiente
            Intent stillLogged = new Intent(MainActivity.this,LoginSuccess.class);
            this.startActivity(stillLogged);


	
		}
	}

		/**
	 * Function to logout from twitter
	 * It will just clear the application shared preferences
	 *  Changed to static for calling from LoginSuccess
	 * */
	 protected static void logoutFromTwitter() {
		// Clear the shared preferences
		Editor e = mSharedPreferences.edit();
		e.remove(PREF_KEY_OAUTH_TOKEN);
		e.remove(PREF_KEY_OAUTH_SECRET);
		e.remove(PREF_KEY_TWITTER_LOGIN);
		e.commit();
	}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	//Change private to static just to call from LoginSuccess
	protected static boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	protected void onResume() {
		super.onResume();
	}

}
