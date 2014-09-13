package uk.me.fantastic.battery;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.millennialmedia.android.MMAdView;
import com.millennialmedia.android.MMInterstitial;
import com.millennialmedia.android.MMRequest;
import com.millennialmedia.android.MMSDK;

public class AlarmManagerActivity extends Activity {

    private AlarmManagerBroadcastReceiver alarm;
    private SharedPreferences data;
    private Button buttonStart, buttonStop;

    public static Activity activity;

    private MMInterstitial interstitial;

    private boolean free;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        free=true;

        if (!free) {

            setContentView(R.layout.activity_alarm_manager_noads);
        } else {



           // MMSDK.setLogLevel(2);
            MMSDK.initialize(this);

            setContentView(R.layout.activity_alarm_manager);
            MMAdView adViewFromXml = (MMAdView) findViewById(R.id.adView);


            MMRequest request = new MMRequest();

            adViewFromXml.setMMRequest(request);

            adViewFromXml.getAd();

            interstitial = new MMInterstitial(this);
            interstitial.setMMRequest(request);
            interstitial.setApid("135112");
            interstitial.fetch();
        }
        activity = this;
        alarm = new AlarmManagerBroadcastReceiver();
        data = getSharedPreferences("AlarmManagerActivity",
                Context.MODE_PRIVATE);
        buttonStart = (Button) findViewById(R.id.btStart);
        buttonStop = (Button) findViewById(R.id.btCancel);
        if (data.getBoolean("enable", false)) {

            buttonStart.setEnabled(false);

        } else {


            buttonStop.setEnabled(false);
        }

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


        // interstitial.setApid("135067");

        // Map all = data.getAll();

        // StrictMode.ThreadPolicy policy = new
        // StrictMode.ThreadPolicy.Builder().permitAll().build();
        // StrictMode.setThreadPolicy(policy);
        // Log.v("poo",""+all.size());
    }

    @Override
    protected void onStart() {

        super.onStart();

    }

    public void startRepeatingTimer(View view) {
        Context context = this.getApplicationContext();

        buttonStart.setEnabled(false);
        buttonStop.setEnabled(true);

        if (alarm != null) {

            alarm.SetAlarm(context);

            SharedPreferences.Editor editor = data.edit();
            editor.putBoolean("enable", true);
            editor.commit();

            Toast.makeText(context, "Enabled", Toast.LENGTH_SHORT).show();
            if(free){
                try{
                    interstitial.display();
                }catch (Exception e){
                    Log.e("Error","Error",e);
                }
            }


          /*  if (free) {
                interstitial.fetch();
                interstitial.setListener(new RequestListener.RequestListenerImpl() {

                    @Override

                    public void requestCompleted(MMAd mmAd) {
                        interstitial.display();
                    }
                });
            }*/
        } else {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelRepeatingTimer(View view) {
        Context context = this.getApplicationContext();
        buttonStart.setEnabled(true);
        buttonStop.setEnabled(false);
        if (alarm != null) {
            alarm.CancelAlarm(context);
            SharedPreferences.Editor editor = data.edit();
            editor.putBoolean("enable", false);
            editor.commit();
            Toast.makeText(context, "Cancelled", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void sendTestEmail(View view) {
        final Context context = this.getApplicationContext();

        new AsyncTask<Void, Void, String>() {

            @Override
            protected String doInBackground(Void... params) {
                try {

                    SharedPreferences sharedPrefs = PreferenceManager
                            .getDefaultSharedPreferences(context);
                    boolean sent = Mail.sendMail(sharedPrefs, 100);
                    if (sent) {
                        return "Email was sent successfully.";

                    } else {
                        return "Error sending email!";

                    }
                } catch (Exception e) {

                    // display("Error sending email: " + e);
                    Log.e("MailApp", "Could not send email", e);
                    return "Error sending email: " + e.toString();
                }

            }

            @Override
            protected void onPostExecute(String result) {
                Toast.makeText(context, result,
                        Toast.LENGTH_LONG).show();
            }
        }.execute();

    }

    public void onetimeTimer(View view) {

        startActivity(new Intent(this, QuickPrefsActivity.class));

		/*
         * Context context = this.getApplicationContext(); if(alarm != null){
		 * alarm.setOnetimeTimer(context); }else{ Toast.makeText(context,
		 * "Alarm is null", Toast.LENGTH_SHORT).show(); }
		 */
    }
    /*
     * @Override public boolean onCreateOptionsMenu(Menu menu) {
	 * getMenuInflater().inflate(R.menu.activity_widget_alarm_manager, menu);
	 * return true; }
	 */

}
