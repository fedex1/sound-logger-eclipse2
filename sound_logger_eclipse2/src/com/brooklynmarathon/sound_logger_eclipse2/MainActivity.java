package com.brooklynmarathon.sound_logger_eclipse2;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mStatusView = (TextView) findViewById(R.id.status);
        Log.d(TAG, "QQQ: create: statusview: " + mStatusView);
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
        

        if (runner == null)
        { 
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }

        
       
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    TextView mStatusView;
    MediaRecorder mRecorder;
    Thread runner;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;
	private static final String TAG = MainActivity.class.getName();

    final Runnable updater = new Runnable(){

        public void run(){          
            updateTv();
        };
    };
    final Handler mHandler = new Handler();

    public void XXXonCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.noiselevel);
        //mStatusView = (TextView) findViewById(R.id.status);


        if (runner == null)
        { 
            runner = new Thread(){
                public void run()
                {
                    while (runner != null)
                    {
                        try
                        {
                            Thread.sleep(1000);
                            Log.i("Noise", "Tock");
                        } catch (InterruptedException e) { };
                        mHandler.post(updater);
                    }
                }
            };
            runner.start();
            Log.d("Noise", "start runner()");
        }
    }

    public void onResume()
    {
        super.onResume();

    	Log.d(TAG, "QQQ: onresume.");

        try{
        	if (mRecorder == null){
        		startRecorder();
        	}
        }catch(Exception e){
        	mStatusView.setText(e.getMessage());
                	
        }
    }

    public void onPause()
    {    
    	super.onPause();

    	Log.d(TAG, "QQQ: onpause.");
 
    	//stopRecorder();
    }

    public void startRecorder(){
        if (mRecorder == null)
        {
            mRecorder = new MediaRecorder();
            Log.d(TAG, "QQQ initial mrecorder: " + mRecorder);
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null"); 
            try
            {           
                mRecorder.prepare();
            }catch (java.io.IOException ioe) {
                Log.e(TAG, "QQQ: IOException: " + android.util.Log.getStackTraceString(ioe));

            }catch (java.lang.SecurityException e) {
                Log.e(TAG, "QQQ: SecurityException: " + android.util.Log.getStackTraceString(e));
            }
            try
            {           
                mRecorder.start();
            }catch (java.lang.SecurityException e) {
                Log.e(TAG, "QQQ: SecurityException: " + android.util.Log.getStackTraceString(e));
            }

            //mEMA = 0.0;
        }

    }
    public void stopRecorder() {
        if (mRecorder != null) {
            mRecorder.stop();       
            mRecorder.release();
            mRecorder = null;
        }
    }

    public void updateTv(){
    	Log.d(TAG,"QQQ: " + mStatusView  + " " + Double.toString((getAmplitudeEMA())) + " dB" );
    	
    	mStatusView.setText(Double.toString((getAmplitudeEMA())) + " dB");
    }
    public double soundDb(double ampl){
        return  20 * Math.log10(getAmplitudeEMA() / ampl);
    }
    public double getAmplitude() {
    	Log.d(TAG, "QQQ: mrecorder: " + mRecorder);
    	
        if (mRecorder != null)
            return  (mRecorder.getMaxAmplitude());
        else
            return 0;

    }
    public double getAmplitudeEMA() {
        double amp =  getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    
    
}
