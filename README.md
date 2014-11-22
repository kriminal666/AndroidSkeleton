Sergi Tur Badenas:

disabled StrictMode to work (avoid NetworkOnMainThreadException http://acacha.org/mediawiki/index.php/Programaci%C3%B3_xarxes_Android#Exemple_simple)

 if (android.os.Build.VERSION.SDK_INT > 9) {
  StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
  StrictMode.setThreadPolicy(policy);
 }

## Working: Android Twitter Connect.


License: [MIT](http://codehate.com/MIT)
