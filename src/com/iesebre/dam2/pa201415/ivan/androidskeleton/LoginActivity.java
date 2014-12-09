package com.iesebre.dam2.pa201415.ivan.androidskeleton;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import android.app.Activity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.android.AsyncFacebookRunner;
import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.Facebook.DialogListener;
import com.facebook.android.FacebookError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;



public class LoginActivity extends Activity implements OnClickListener,ConnectionCallbacks,OnConnectionFailedListener {
	//BEGIN FACEBOOK
	// Your Facebook APP ID
		private static String APP_ID = "714569478638464"; // Replace with your App ID

		// Instance of Facebook Class
		@SuppressWarnings("deprecation")
		private Facebook facebook = new Facebook(APP_ID);
		private AsyncFacebookRunner mAsyncRunner;
		String FILENAME = "AndroidSSO_data";
		private static SharedPreferences mPrefs;
		private String FACEBOOK_LOGIN = "Facebook_Login";
		private static final int FACE_REQUEST = 78452301;
		//FACEBOOK BUTTON
		Button btnFbLogin;
		//END FACEBOOK CAMPS
	
		//BEGIN TWITTER
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
    //TWITTER REQUEST
	private static final int TWITTER_REQUEST = 789012;
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
	//END TWITTER CAMPS
	//BEGIN GOOGLE CAMPS
	private static final int RC_SIGN_IN = 0;
	// Logcat tag
	private static final String TAG = "MainActivity";
    

	// Google client to interact with Google API
	protected static   GoogleApiClient mGoogleApiClient;
	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;

	private boolean mSignInClicked;

	private ConnectionResult mConnectionResult;
    //we need just one button
	//private SignInButton btnSignIn;
	private Button btnSignIn;
	//GOOGLE REQUEST
	private static final int GOOGLE_REQUEST = 98702341;

	private static final int DIALOG_PLAY_SERVICES_ERROR = 91;
	//END GOOGLE CAMPS
    //PERSONAL CAMP FOR TOAST
	Context context;
	//PERSONAL PROGRESS DIALOG
	private ProgressDialog progressDialog;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//For toasts
		context = getApplicationContext();
	   
		progressDialog =  new ProgressDialog(LoginActivity.this);
		progressDialog.setMessage("Loading..");
		
		//FACEBOOK CONTROLS
		btnFbLogin = (Button) findViewById(R.id.btnFb);
		mAsyncRunner = new AsyncFacebookRunner(facebook);
		//End facebook controls
		//BEGIN FACEBOOK CODE
		/**
		 * Login button Click event
		 * */
		btnFbLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.d("Image Button", "button Clicked");
				 loginToFacebook();
			}
		});
		//END FACEBOOK CLICK
		//GOOGLE CONTROLS
		btnSignIn = (Button) findViewById(R.id.btnGplus);
		btnSignIn.setOnClickListener(this);
		mGoogleApiClient = new GoogleApiClient.Builder(this)
		.addConnectionCallbacks(this)
		.addOnConnectionFailedListener(this).addApi(Plus.API)
		.addScope(Plus.SCOPE_PLUS_LOGIN).build();
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
			alert.showAlertDialog(LoginActivity.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
			// stop executing code by return
			return;
		}
		
		// Check if twitter keys are set
		if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
			// Internet Connection is not present
			alert.showAlertDialog(LoginActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
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
				//INIT PROGRESS DIALOG
				
				progressDialog.show();
				loginToTwitter();
			}
		});
		
		
		
        /** This if conditions is tested once is
		 * redirected from twitter page. Parse the uri to get oAuth
		 * Verifier
		 * */
		
		Log.d("Logout", "onCreate valor twitter"+isTwitterLoggedInAlready());
		if (!isTwitterLoggedInAlready()) {
			
			//GET THE FUCKING URI FROM SPLASH IF WE ARE REDIRECTED FROM TWITTER
			Uri uri = getIntent().getData();
			Log.d("Logout","la uri ="+uri);
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
					  progressDialog.dismiss();
					Toast.makeText(context,"User connected to Twitter", Toast.LENGTH_LONG).show();
                    Intent loginTwitter = new Intent(LoginActivity.this,MainActivityDrawer.class);
					startActivityForResult(loginTwitter,TWITTER_REQUEST);	
				} catch (Exception e) {
					// Check log for login errors
					Log.d("Logout", "> " + e.getMessage());
				}
			}
			//IF WE ARE CONNECTED TO TWITTER GO TO DRAWER ACTIVITY
		}else{
			Log.d("Logout", "onCreate valor twitter en el else"+isTwitterLoggedInAlready());
			//STOP PROGRESS DIALOG ACTIVATED ONCLICK LOGIN
			progressDialog.dismiss();
            Intent loginTwitter = new Intent(LoginActivity.this,MainActivityDrawer.class);
			startActivityForResult(loginTwitter,TWITTER_REQUEST);
		}
        
	}//End of method onCreate
	
	//GOOGLE METHODS
	protected void onStart() {
		super.onStart();
		mGoogleApiClient.connect();
		
	}
    /*
     * No need to use this 
     * @see android.app.Activity#onStop()
     
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
		}
	}*/
	
	/**
	 * Method to resolve any sign in errors
	 * */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				mConnectionResult.startResolutionForResult(this, RC_SIGN_IN);
			} catch (SendIntentException e) {
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!result.hasResolution()) {
			GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this,
					0).show();
			return;
		}

		if (!mIntentInProgress) {
			// Store the ConnectionResult for later usage
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	@Override
	public void onConnected(Bundle arg0) {
		mSignInClicked = false;
		Toast.makeText(context, "User connected to Google+", Toast.LENGTH_LONG).show();
        
		// Get user's information
		//wE DON NEED THIS RIGHT NOW
		//getProfileInformation();
		
		// Update the UI after signin
		//STOP PROGRESSDIALOG
		Log.d("Logout","paramos progress google");
		   progressDialog.dismiss();
		Log.d("Logout","Tras parar el progress dialog");
		updateUI(true);		
		

	}
	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 *aCTIONS TO DO ONCE WE ARE SIGN IN
	 * */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			
			//LET'S GO TO DE NEXT ACTIVITY
			Intent googleLogin = new Intent(LoginActivity.this,MainActivityDrawer.class);
			startActivityForResult(googleLogin, GOOGLE_REQUEST);
			
		} else {
			//if false we don't need to do nothing
			Toast.makeText(context, "User Disconnected from Google+", Toast.LENGTH_LONG).show();
			return;
	
		}
	}
	@Override
	public void onConnectionSuspended(int arg0) {
		
		mGoogleApiClient.connect();
		updateUI(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	/**
	 * Button on click listener
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGplus:
			// Sign in button clicked
			Log.d("Logout","se activa el progress de google");
		   progressDialog.show();
			signInWithGplus();
			break;
		}
	}
	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		Log.d("Logout","signgoogle");
		if (!mGoogleApiClient.isConnecting()) {
			Log.d("Logout","pasa el if");
			mSignInClicked = true;
			resolveSignInError();
		}
	}
	/**
	 * Sign-out from google
	 */
	public void signOutFromGplus() {
		
		// shared es true o false sharedPrefsGoogle.getBoolean(GOOGLE_LOGIN,false)
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			mGoogleApiClient.disconnect();
			mGoogleApiClient.connect();
			updateUI(false);
			
		}
		
	}
	/**
	 * Revoking access from google
	 * */
	private void revokeGplusAccess() {
		Log.d("Logout","revokeGPlus");
		 Log.d("Logout","revoke google");
		if (mGoogleApiClient.isConnected()) {
			Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
			Plus.AccountApi.revokeAccessAndDisconnect(mGoogleApiClient)
					.setResultCallback(new ResultCallback<Status>() {
						@Override
						public void onResult(Status arg0) {
							Log.e(TAG, "User access revoked!");
							mGoogleApiClient.disconnect();
							mGoogleApiClient.connect();
							updateUI(false);
						}

					});
		}
	}
	//LOGIN FACEBOOK
	/**
	 * Function to login into facebook
	 * */
	@SuppressWarnings("deprecation")
	public void loginToFacebook() {
        Log.d("Logout","LLegamos al login facebook");
		mPrefs = getPreferences(MODE_PRIVATE);
		String access_token = mPrefs.getString("access_token", null);
		long expires = mPrefs.getLong("access_expires", 0);
		//If we have been logged before set the access token and expires
        
		if (access_token != null) {
			Log.d("Logout","Pasa el if del token"+access_token);
			facebook.setAccessToken(access_token);
		//Actions when login
			Log.d("loginface","login facebook 1");

			Log.d("FB Sessions", "" + facebook.isSessionValid());
		}

		if (expires != 0) {
			Log.d("Logout","Pasa el if de expires "+expires);
			facebook.setAccessExpires(expires);
		}
        //Check if session is really valid
		if (!facebook.isSessionValid()) {
			Log.d("Logout","La sesión ha dado que no es valida");
			facebook.authorize(this,
					new String[] { "email", "publish_stream" },
					new DialogListener() {

						@Override
						public void onCancel() {
							// Function to handle cancel event
						}

						@Override
						public void onComplete(Bundle values) {
							// Function to handle complete event
							// Edit Preferences and update facebook acess_token
							SharedPreferences.Editor editor = mPrefs.edit();
							editor.putString("access_token",
									facebook.getAccessToken());
							editor.putLong("access_expires",
									facebook.getAccessExpires());
							editor.commit();
							//Actions when login finish on facebook
							Toast.makeText(context,"User connected to Facebook", Toast.LENGTH_LONG).show();
							//LET'S GO TO ANOTHER ACTIVITY
							Intent loginFace = new Intent (LoginActivity.this,MainActivityDrawer.class);
							startActivityForResult(loginFace,FACE_REQUEST);
						}

						@Override
						public void onError(DialogError error) {
							// Function to handle error

						}

						@Override
						public void onFacebookError(FacebookError fberror) {
							// Function to handle Facebook errors

						}

					});
		}else{
			Log.d("Logout","llega al else, la sesión es válida hace el intent a la drawer");
			//If we have been logged before and session is valid
			Toast.makeText(context,"User connected to Facebook", Toast.LENGTH_LONG).show();
			//LET'S GO TO ANOTHER ACTIVITY
			Intent loginFace = new Intent (LoginActivity.this,MainActivityDrawer.class);
			startActivityForResult(loginFace,FACE_REQUEST);
		}
		
	}
	@SuppressWarnings("deprecation")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		      Log.d("Logout","llegamos a onactivityresult ");
		     //Default value not revoke
		     boolean revoke = false;
		   //IF NULL APP GOES DOWN SO WE NEED TO TEST IT
		     if(data!=null){
		        Bundle extras=data.getExtras();
		          if(extras!=null){
		        	//GET INTENT DATA EXTRAS  
		    	  revoke = data.getBooleanExtra(BaseUtils.REVOKE,false);
		          }
		     }
		//For google
		if (requestCode == RC_SIGN_IN) {
			if (resultCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
		//logout 
		if(resultCode==9999){
		  switch (requestCode) {
		    case FACE_REQUEST :
		     //IF FACEBOOK CALL LOGOUT
			 logoutFromFacebook(this,revoke);
			 break;
		    case TWITTER_REQUEST :
		    	//IF TWITTER CALL LOGOUT
		    	 Log.d("Logout","antes de llamar al logout de twitter "+isTwitterLoggedInAlready()+","+revoke); 
		          logoutFromTwitter(revoke);
		    		break;
		    case GOOGLE_REQUEST :
		    	//IF GOOGLE+ CALL LOGOUT OR REVOKE
		    	if(revoke){
		    	 revokeGplusAccess();
		    	}else{
		    	signOutFromGplus();
		    	
		    	}
		  }
		}
		//for facebook
		super.onActivityResult(requestCode, resultCode, data);
		facebook.authorizeCallback(requestCode, resultCode, data);
	}
	//END LOGIN FACEBOOK

	/**
	 * Function to login twitter
	 * */
	private void loginToTwitter() {
		// Check if already logged in
		
		String oauth_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, null);
		String oauth_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET,null);
		Log.d("Logout","loginTwitter valor conected"+isTwitterLoggedInAlready());
		if (!isTwitterLoggedInAlready()) {
			if ((oauth_token==null)&&(oauth_secret==null)){
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
		 //IF istwitterLoggedInAlready is false but we have the tokens go to drawer
		   }
		}else {
			   Log.d("Logout","Login twitter llegamos al else");
			      //HIDE PROGRESS DIALOG
			      progressDialog.dismiss();
			// user logged into twitter if 
			   Toast.makeText(context,"User connected to Twitter", Toast.LENGTH_LONG).show();
			 //INTENT TO GO TO DRAWER ACTIVITY IF WE HAVE LOGGED IN ONCE BEFORE
	            Intent stillLogged = new Intent(LoginActivity.this,MainActivityDrawer.class);
		        startActivityForResult(stillLogged,TWITTER_REQUEST);
		        this.finish(); 
	    }
	        
	}

		/**
	 * Function to logout from twitter
	 * It will just clear the application shared preferences
	 *  Changed to static for calling from LoginSuccess
	 * */
	 private void logoutFromTwitter(boolean revoke) {
		// Clear the shared preferences
		 Log.d("Logout","logout twiitter"+revoke);
		
		//IF WE WANT TO REVOKE REMOVE TOKENS
		 if(revoke){
			 Log.d("Logout","Pasa el if de revoke");
		  Editor e = mSharedPreferences.edit();
		  Log.d("Logout","logout twitter limpiamos token"); 
		  e.remove(PREF_KEY_OAUTH_TOKEN);
		  e.remove(PREF_KEY_OAUTH_SECRET);
		  e.remove(PREF_KEY_TWITTER_LOGIN);
		  e.commit();
		  
		 }
		//If we don't want to revoke nothing to do
		 
		Toast.makeText(context,"User disconnected from Twitter", Toast.LENGTH_LONG).show();
		}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	protected  void onResume() {
		super.onResume();
		
		
	}
	/**
	 * 
	 * NEW LOGOUT FROM FACEBOOK
	 */
	public void logoutFromFacebook(Context context,boolean revoke) {
	    Session session = Session.getActiveSession();
	   Log.d("Logout","logout facebook");
	    //Normally Dont pass through this if
	    if (session != null) {
	    	Log.d("Logout","Pasa el if de session : "+session);
	    	//We change if  if(!session.isclosed)
	        if (session.isClosed()) {
	        	Log.d("Logout","Pasa el if de session is closed"+session.isClosed());
	        	session.closeAndClearTokenInformation();
	        	//IF WE WANT TO REVOKE
	        	if(revoke){
	        		Log.d("Logout","Pasa el if de revoke");
	            //CLEAR FACEBOOK PREFERENCES
	             SharedPreferences.Editor editor = mPrefs.edit();
	    		 editor.remove("access_token");
	    		 editor.remove("access_expires");
	    		 editor.commit();
	        	}
	      }
	    } else {
	    	Log.d("Logout","llega al else");
	    	//Normally it comes here..
	        session = new Session(context);
	        Session.setActiveSession(session);

	        session.closeAndClearTokenInformation();
	        //IF WE WANT TO REVOKE
	        if(revoke){
	        	Log.d("Logout","pasa el if de revoke y limpiamos los token");
	         //CLEAR FACEBOOK PREFERENCES
	          SharedPreferences.Editor editor = mPrefs.edit();
    		  editor.remove("access_token");
    		  editor.remove("access_expires");
    		  editor.commit();
	        }
	        
	    }
	    Log.d("Logout","Refrescamos la pantalla despues de logout facebook");
		 Toast.makeText(context,"User disconnected from Facebook", Toast.LENGTH_LONG).show();
		//REFRESH THIS ACTIVITY TO CLEAN DATA
		Intent refresh = new Intent(context,LoginActivity.class);
		context.startActivity(refresh);
	}//END LOGOUT FACEBOOK
	

	
}//END CLASS
