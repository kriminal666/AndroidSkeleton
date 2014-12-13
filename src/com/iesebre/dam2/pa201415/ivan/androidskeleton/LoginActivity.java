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
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.AppEventsLogger;
import com.facebook.HttpMethod;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.Plus;
import com.iesebre.dam2.pa201415.ivan.androidskeleton.LoginActivityFragment;



//Cambiar esto por extends fragmentactivity
public class LoginActivity extends FragmentActivity implements
                     OnClickListener,ConnectionCallbacks,OnConnectionFailedListener,LoginActivityFragment.OnFragmentInteractionListener{
	//BEGIN FACEBOOK
	// Your Facebook APP ID
		private static String APP_ID = "714569478638464"; // Replace with your App ID
		private UiLifecycleHelper uiHelper;
	    private Session.StatusCallback callback = new Session.StatusCallback() {
	        @Override
	        public void call(Session session, SessionState state, Exception exception) {
	            onSessionStateChange(session, state, exception);
	        }
	    };
	    
	    private boolean isResumed = false;
	

		private static final int FACE_REQUEST = 78452301;
		
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
	
	//GOOGLE REQUEST
	private static final int GOOGLE_REQUEST = 98702341;

	private static final int DIALOG_PLAY_SERVICES_ERROR = 91;
	//END GOOGLE CAMPS
    //PERSONAL CAMP FOR TOAST
	Context context;
	//PERSONAL PROGRESS DIALOG
	private ProgressDialog progressDialog;
	
	//necesario cambiarlo para fragments
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//Fragment code
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.login_activitycontainer, new LoginActivityFragment()).commit();
		}
		
		
		
		//Aquí añade un if para los fragments hay que pasarle un framelayout y
		//cargar el fragmento nuevo donde van los botones
		//For toasts
		context = getApplicationContext();
	   
		progressDialog =  new ProgressDialog(LoginActivity.this);
		progressDialog.setMessage("Loading..");
		
		
		
		//BEGIN FACEBOOK CODE
		//FACEBOOK
				uiHelper = new UiLifecycleHelper(this, callback);
		        uiHelper.onCreate(savedInstanceState);
		
		//END FACEBOOK 
		        
		//GOOGLE CONTROLS
		
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
	
		// Shared Preferences twitter
		mSharedPreferences = getApplicationContext().getSharedPreferences(
				"MyPref", 0);
		
		
		
        /** This if conditions is tested once is
		 * redirected from twitter page. Parse the uri to get oAuth
		 * Verifier
		 * */
		
		
		if (!isTwitterLoggedInAlready()) {
			
			//GET THE FUCKING URI FROM SPLASH IF WE ARE REDIRECTED FROM TWITTER
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
					  progressDialog.dismiss();
					Toast.makeText(context,"User connected to Twitter", Toast.LENGTH_LONG).show();
                    Intent loginTwitter = new Intent(LoginActivity.this,MainActivityDrawer.class);
					startActivityForResult(loginTwitter,TWITTER_REQUEST);	
				} catch (Exception e) {
					// Check log for login errors
					Log.d("Logout", "> " + e.getMessage());
				}
			}
			
		}
	
        
	}//End of method onCreate
	
	private void onSessionStateChange(Session session, SessionState state, Exception exception) {
		Log.d(TAG,"onSessionStateChange!");
        if (isResumed) {
        	
            FragmentManager manager = getSupportFragmentManager();
            int backStackSize = manager.getBackStackEntryCount();
            for (int i = 0; i < backStackSize; i++) {
                manager.popBackStack();
            }
            // check for the OPENED state instead of session.isOpened() since for the
            // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
            if (state.equals(SessionState.OPENED)) {
            	
            	Intent i = new Intent(LoginActivity.this, MainActivityDrawer.class);
        		startActivityForResult(i, FACE_REQUEST);
            } else if (state.isClosed()) {
                //NO logged to facebook
            }
        }
    }
	
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
		
		   progressDialog.dismiss();
		
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
		getMenuInflater().inflate(R.menu.main_activity_blank_fragment_borrar, menu);
		return true;
	}
	/**
	 * Button on click listener
	 * */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnGplus:
			// Signin with Google Plus button clicked
			progressDialog.show();
			signInWithGplus();
			break;
		/* NOTE: Facebook have a custom button!
		case R.id.btn_facebook_sign_in:
			// Signin with Twitter button clicked
			loginToFacebook();
			break;*/	
		case R.id.btnTwitter:
			// Signin with Twitter button clicked
			progressDialog.show();
			loginToTwitter();
			break;
		}
	}
	/**
	 * Sign-in into google
	 * */
	private void signInWithGplus() {
		
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


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		     super.onActivityResult(requestCode, resultCode, data);
		   //Facebook:
			uiHelper.onActivityResult(requestCode, resultCode, data);
		      Log.d("Logout","llegamos a onactivityresult ");
		     //Default value not revoke
		     boolean revoke = false;
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
		
		  switch (requestCode) {
		    case FACE_REQUEST :
		    	if(resultCode==RESULT_OK){
		    		Bundle extras = data.getExtras();
		    		if(extras!=null){
		    			revoke =extras.getBoolean(BaseUtils.REVOKE);
		    		}
		    	
		    	Session session = Session.getActiveSession();
		    	if (revoke == true) {
		    		progressDialog = ProgressDialog.show(
				            LoginActivity.this, "", "Revoke permission...", true);
		    		new Request(
							   session,
							    "/me/permissions",
							    null,
							    HttpMethod.DELETE,
							    new Request.Callback() {
							        public void onCompleted(Response response) {
							            /* handle the result */
							        	progressDialog.dismiss(); 
							        }
							    }
							).executeAsync();
					session.closeAndClearTokenInformation();
				//If we don want to revoke	
		    	}else {
									
					session.closeAndClearTokenInformation();
				}
		   }	
			 break;
		    case TWITTER_REQUEST :
		    	//IF TWITTER CALL LOGOUT
		    	 Log.d("Logout","antes de llamar al logout de twitter "+isTwitterLoggedInAlready()+","+revoke);
		    		if(resultCode==RESULT_OK){
			    		Bundle extras = data.getExtras();
			    		if(extras!=null){
			    			revoke =extras.getBoolean(BaseUtils.REVOKE);
			    		}
			    	}
		          logoutFromTwitter(revoke);
		    		break;
		    case GOOGLE_REQUEST:
		    	if(resultCode==RESULT_OK){
		    		Bundle extras = data.getExtras();
		    		if(extras!=null){
		    			revoke =extras.getBoolean(BaseUtils.REVOKE);
		    		}
		    	}
		    	//IF GOOGLE+ CALL LOGOUT OR REVOKE
		    	if(revoke){
		    	 revokeGplusAccess();
		    	}else{
		    	signOutFromGplus();
		    	
		    	}
		  
		}
	}

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
		   }else {
			   Log.d("Logout","Login twitter llegamos al else");
			      //HIDE PROGRESS DIALOG
			      progressDialog.dismiss();
			// user logged into twitter if we have tokens
			      //Edit shared preferences
			      Editor e = mSharedPreferences.edit();
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes
			   Toast.makeText(context,"User connected to Twitter", Toast.LENGTH_LONG).show();
			 //INTENT TO GO TO DRAWER ACTIVITY IF WE HAVE LOGGED IN ONCE BEFORE
	            Intent stillLogged = new Intent(LoginActivity.this,MainActivityDrawer.class);
		        startActivityForResult(stillLogged,TWITTER_REQUEST);
		  
	    }
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
		  Toast.makeText(context,"User disconnected from Twitter", Toast.LENGTH_LONG).show();
		 }else{
		//If we don't want to revoke remove login status
		 Editor e = mSharedPreferences.edit();
		 e.remove(PREF_KEY_TWITTER_LOGIN); 
		 e.commit();
		 Toast.makeText(context,"User disconnected from Twitter", Toast.LENGTH_LONG).show();
		 }
		}

	/**
	 * Check user already logged in your application using twitter Login flag is
	 * fetched from Shared Preferences
	 * */
	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	@Override
    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        isResumed = true;

        // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
        // the onResume methods of the primary Activities that an app may be launched into.
        AppEventsLogger.activateApp(this);
    }
	@Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
        isResumed = false;

        // Call the 'deactivateApp' method to log an app event for use in analytics and advertising
        // reporting.  Do so in the onPause methods of the primary Activities that an app may be launched into.
        AppEventsLogger.deactivateApp(this);
    }
	@Override
    protected void onResumeFragments() {
		Log.d(TAG,"onResumeFragments!");
        super.onResumeFragments();
        Session session = Session.getActiveSession();

        if (session != null && session.isOpened()) {
            // if the session is already open, try to show the selection fragment
            
        	//Login ok
        	Log.d(TAG,"Login to facebook Ok!");
        	Intent i = new Intent(LoginActivity.this, MainActivityDrawer.class);
    		startActivityForResult(i, FACE_REQUEST);
        	
            //userSkippedLogin = false;
        } /*else if (userSkippedLogin) {
            showFragment(SELECTION, false);
        }*/ else {
            // otherwise present the splash screen and ask the user to login, unless the user explicitly skipped.
        	//showFragment(SPLASH, false);
        	Log.d(TAG,"Login to facebook not Ok!");
        }
    }
	@Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }
	
	@Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);

        //outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
    }
	@Override
	public void onFragmentInteraction(Uri uri) {
		// TODO Auto-generated method stub
		// Nothing to do!?
	}

	

	
}//END CLASS
