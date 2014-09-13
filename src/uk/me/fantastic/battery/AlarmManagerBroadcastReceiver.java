package uk.me.fantastic.battery;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.BatteryManager;

import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

	final public static String ONE_TIME = "onetime";

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager pm = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wl = pm.newWakeLock(
				PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
		// Acquire the lock
		try {
			wl.acquire();

			Log.v("TAG", "tick");

			final Context contextF = context.getApplicationContext();
			// context;

			new AsyncTask<Void, Void, Void>() {

				@Override
				protected Void doInBackground(Void... params) {

					Log.v("Battery", "poop");

					// if(true)return null;

					SharedPreferences sharedPrefs = PreferenceManager
							.getDefaultSharedPreferences(contextF);

					SharedPreferences data = contextF.getSharedPreferences(
							"AlarmManagerActivity", Context.MODE_PRIVATE);

					if (System.currentTimeMillis()
							- data.getLong("last_sent", 0) < 1000 * 60 * Integer
							.parseInt(sharedPrefs.getString("email_interval",
									"0"))) {
						Log.v("Mail",
								"Not sending mail because we sent one recently");
						return null;
					}

					IntentFilter ifilter = new IntentFilter(
							Intent.ACTION_BATTERY_CHANGED);
					Intent batteryStatus = contextF.registerReceiver(null,
							ifilter);
					int level = batteryStatus.getIntExtra(
							BatteryManager.EXTRA_LEVEL, -1);
					int scale = batteryStatus.getIntExtra(
							BatteryManager.EXTRA_SCALE, -1);
					float batteryPct = level / (float) scale;
					int battery = (int) (100.0 * batteryPct);

					Log.v("Battery", "" + battery);

					if (battery >= Integer.parseInt(sharedPrefs.getString(
							"battery_level", "50"))) {
						Log.v("Battery", "fine");
						return null;
					}

					Log.v("Battery", "too low!");

					try {
						boolean sent = Mail.sendMail(sharedPrefs, battery);
						if (sent) {
							// Toast.makeText(context,
							// "Email was sent successfully.",
							// Toast.LENGTH_LONG).show();

							SharedPreferences.Editor editor = data.edit();
							editor.putLong("last_sent",
									System.currentTimeMillis());
							editor.commit();
							Log.v("Email", "Sent");

						} else {
							display("Error sending email");
						}
					} catch (Exception e) {
						// Toast.makeText(MailApp.this,
						// "There was a problem sending the email.",
						// Toast.LENGTH_LONG).show();
						display("Error sending email: " + e);
						Log.e("MailApp", "Could not send email", e);
					}

					return null;
				}

				private void display(final String string) {
					try {
						if (AlarmManagerActivity.activity != null) {
							AlarmManagerActivity.activity
									.runOnUiThread(new Runnable() {
										public void run() {
											Toast.makeText(
													AlarmManagerActivity.activity,
													string, Toast.LENGTH_LONG)
													.show();
										}
									});
						}
					} catch (Exception e) {
					}
					;
					Log.v("Email", string);

				}

			}.execute();

			// You can do the processing here update the widget/remote views.
			/*
			 * Bundle extras = intent.getExtras(); StringBuilder msgStr = new
			 * StringBuilder();
			 * 
			 * if(extras != null && extras.getBoolean(ONE_TIME, Boolean.FALSE)){
			 * msgStr.append("One time Timer : "); } Format formatter = new
			 * SimpleDateFormat("hh:mm:ss a");
			 * msgStr.append(formatter.format(new Date()));
			 * 
			 * Toast.makeText(context, msgStr, Toast.LENGTH_LONG).show();
			 */
		} finally {
			// Release the lock
			wl.release();

		}

	}

	public void SetAlarm(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.FALSE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);

		SharedPreferences data = context.getSharedPreferences(
				"AlarmManagerActivity", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = data.edit();
		editor.putLong("last_sent", 0);
		editor.commit();

		SharedPreferences sharedPrefs = PreferenceManager
				.getDefaultSharedPreferences(context);
		int minutes = Integer.parseInt(sharedPrefs.getString("poll_interval",
				"50"));
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),
				1000 * 60 * minutes, pi);

	}

	public void CancelAlarm(Context context) {
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		PendingIntent sender = PendingIntent
				.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(sender);
	}

	public void setOnetimeTimer(Context context) {
		AlarmManager am = (AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.TRUE);
		PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
		am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
	}
}
