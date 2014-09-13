package uk.me.fantastic.battery;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoStart extends BroadcastReceiver
{   
	AlarmManagerBroadcastReceiver alarm = new AlarmManagerBroadcastReceiver();
    @Override
    public void onReceive(Context context, Intent intent)
    {   
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED"))
        {
        	if (context.getSharedPreferences("AlarmManagerActivity",Context.MODE_PRIVATE).getBoolean("enable", false)) {
            alarm.SetAlarm(context);
        	}
        }
    }
}