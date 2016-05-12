package com.example.kjs.myapplication;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.logging.Handler;

/**
 * Created by kjs on 1/6/2016.
 */
public class TService extends Service {
    public static String call_Flag;
    MediaRecorder recorder;
    File audiofile;
    String name, phonenumber;
    String audio_format;
    public String AUDIO_TYPE;
    int audioSource;
    Context context;
    private Handler handler;
    Timer timer;
    Boolean offHook=false, ringing=false;
    Toast toast;
    Boolean isOffHook=false;
    private boolean recordstarted=false;

    private static final String ACTION_IN="android.intent.action.PHONE_STATE";
    private static final String ACTION_OUT="android.intent.action.NEW_OUTGOING_CALL";
    private CallBr br_call;

    @Override
    public IBinder onBind(Intent arg0){
        return null;
    }

    @Override
    public void onDestroy(){
        Log.d("service", "destroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        //  Toast.makeText(this, "TService",Toast.LENGTH_LONG).show();
        final IntentFilter filter=new IntentFilter();

        filter.addAction(ACTION_OUT);
        filter.addAction(ACTION_IN);

        this.br_call=new CallBr();
        this.registerReceiver(this.br_call,filter);

        return START_NOT_STICKY;
    }

    int flag=0;

    public class CallBr extends BroadcastReceiver {
        Bundle bundle;
        String state;
        String inCall, outCall;
        public boolean wasRinging = false;

        @Override
        public void onReceive(Context context, Intent intent) {

            if (call_Flag.equals("On")) {
                if (intent.getAction().equals(ACTION_IN)) {
                    if ((bundle = intent.getExtras()) != null) {
                        state = bundle.getString(TelephonyManager.EXTRA_STATE);
                        if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                            inCall = bundle.getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                            wasRinging = true;
                            Toast.makeText(context, "IN : " + inCall, Toast.LENGTH_LONG).show();
                        } else if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                            if (wasRinging == true && recordstarted == false) {

                                Toast.makeText(context, "ANSWERED", Toast.LENGTH_LONG).show();

                                String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                                File sampleDir = new File(Environment.getExternalStorageDirectory(), "/TestData");
                                if (!sampleDir.exists()) {
                                    sampleDir.mkdirs();
                                }
                                String file_name = "Record_In";
                                try {
                                    audiofile = File.createTempFile(file_name + out, ".amr", sampleDir);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                                recorder = new MediaRecorder();
//                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

                                recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                                recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                recorder.setOutputFile(audiofile.getAbsolutePath());
                                try {
                                    recorder.prepare();
                                } catch (IllegalStateException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                recorder.start();
                                recordstarted = true;
                            }
                        } else if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                            wasRinging = false;
                            Toast.makeText(context, "REJECT || DISCO", Toast.LENGTH_LONG).show();
                            if (recordstarted) {
                                recorder.stop();
                                recordstarted = false;
                            }
                        }
                    }
                } else if (intent.getAction().equals(ACTION_OUT)) {

                    outCall = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                    Toast.makeText(context, "OUT : " + outCall, Toast.LENGTH_LONG).show();
                    String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
                    File sampleDir = new File(Environment.getExternalStorageDirectory(), "/TestData");
                    if (!sampleDir.exists()) {
                        sampleDir.mkdirs();
                    }
                    String file_name = "Record_Out";
                    try {
                        audiofile = File.createTempFile(file_name + out, ".amr", sampleDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath();

                    recorder = new MediaRecorder();
//                          recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_CALL);

                    recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(audiofile.getAbsolutePath());
                    try {
                        recorder.prepare();
                    } catch (IllegalStateException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    recorder.start();
                    recordstarted = true;
                }
            }
        }
    }
}
