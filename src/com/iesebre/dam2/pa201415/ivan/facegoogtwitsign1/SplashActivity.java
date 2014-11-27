package com.iesebre.dam2.pa201415.ivan.facegoogtwitsign1;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import com.iesebre.dam2.pa201415.ivan.facegoogtwitsign1.R;
public class SplashActivity extends Activity {
	 // Duración de la pantalla splash
    private static final long SPLASH_SCREEN_DELAY = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Orientación de la pantalla
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Escondemos la barra del título
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.splash);
   
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
            	
                // llamar a la pantalla de login
                Intent mainIntent = new Intent().setClass(
                        SplashActivity.this, LoginActivity.class);
                startActivity(mainIntent);
            	
                //Cerramos la actividad de manera que no se pueda volver atrás
                finish();
            	
            }
        };
    	
        // Simulador
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
    
}
