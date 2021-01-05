package com.levi.nfcapplication;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class ReceiverManager {

    private static List<BroadcastReceiver> receivers = new ArrayList<BroadcastReceiver>();
    private static ReceiverManager ref;
    private Context context;

    private ReceiverManager(Context context){
        this.context = context;
    }

    public static synchronized ReceiverManager init(Context context) {
        if (ref == null) ref = new ReceiverManager(context);
        return ref;
    }

    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter intentFilter){
        receivers.add(receiver);
        Intent intent = context.registerReceiver(receiver, intentFilter);
        Log.i(getClass().getSimpleName(), "registered receiver: "+receiver+"  with filter: "+intentFilter);
        Log.i(getClass().getSimpleName(), "receiver Intent: "+intent);
        return intent;
    }

    public boolean isReceiverRegistered(BroadcastReceiver receiver){
        boolean registered = receivers.contains(receiver);
        Log.i(getClass().getSimpleName(), "is receiver "+receiver+" registered? "+registered);
        return registered;
    }

    public void unregisterReceiver(BroadcastReceiver receiver){
        if (isReceiverRegistered(receiver)){
            receivers.remove(receiver);
            context.unregisterReceiver(receiver);
            Log.i(getClass().getSimpleName(), "unregistered receiver: "+receiver);
        }
    }
}