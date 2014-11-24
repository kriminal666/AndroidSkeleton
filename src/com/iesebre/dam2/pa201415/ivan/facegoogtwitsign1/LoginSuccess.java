package com.iesebre.dam2.pa201415.ivan.facegoogtwitsign1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.iesebre.dam2.pa201415.ivan.facegoogtwitsign1.MainActivity;
public class LoginSuccess extends Activity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loginsuccess);
		 //GET LOGOUT BUTTON
        Button btnLogout = (Button) findViewById(R.id.btnLogout);
        //Create listener
        /*btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //VERIFY WHERE ARE CONNECTED (need import MainActivity class)
                if (MainActivity.isTwitterLoggedInAlready()) {
                    // user already logged into twitter
                    //Call logout.
                    MainActivity.logoutFromTwitter();
                    //return to login page
                    Intent logoutTwitter = new Intent(LoginSuccess.this,MainActivity.class);
                    startActivity(logoutTwitter);

                }

            }
        });*/
	}
	
	
	
	
}
