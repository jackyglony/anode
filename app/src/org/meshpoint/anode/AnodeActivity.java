package org.meshpoint.anode;

import org.meshpoint.anode.Runtime;
import org.meshpoint.anode.Runtime.IllegalStateException;
import org.meshpoint.anode.Runtime.InitialisationException;
import org.meshpoint.anode.Runtime.NodeException;
import org.meshpoint.anode.Runtime.StateListener;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class AnodeActivity extends Activity implements StateListener {

	private static String TAG = "anode::AnodeActivity";
	private Context ctx;
	private Button startButton;
	private Button stopButton;
	private EditText argsText;
	private TextView stateText;
	private Handler viewHandler = new Handler();

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ctx = getApplicationContext();
        initUI();
        initRuntime();
    }
    
    private Runtime runtime;
    
    private void initUI() {
    	startButton = (Button)findViewById(R.id.start_button);
    	startButton.setOnClickListener(new StartClickListener());
    	stopButton = (Button)findViewById(R.id.stop_button);
    	stopButton.setOnClickListener(new StopClickListener());
    	argsText = (EditText)findViewById(R.id.args_editText);
    	stateText = (TextView)findViewById(R.id.args_stateText);
    }
    
    private void initRuntime() {
    	try {
    		runtime = Runtime.getRuntime(ctx);
    		if(runtime != null)
    			runtime.addStateListener(this);
		} catch (InitialisationException e) {
			Log.v(TAG, "initRuntime: exception: " + e + "cause: " + e.getCause());
		}
    }
    
    class StartClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			String args = argsText.getText().toString();
			try {
				runtime.start(args.split("\\s"));
			} catch (IllegalStateException e) {
				Log.v(TAG, "runtime start: exception: " + e + "cause: " + e.getCause());
			} catch (NodeException e) {
				Log.v(TAG, "runtime start: exception: " + e);
			}
		}
    }

    class StopClickListener implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			try {
				runtime.stop();
			} catch (IllegalStateException e) {
				Log.v(TAG, "runtime stop: exception: " + e + "cause: " + e.getCause());
			} catch (NodeException e) {
				Log.v(TAG, "runtime stop: exception: " + e);
			}
		}
    }

	@Override
	public void stateChanged(final int state) {
		viewHandler.post(new Runnable() {
			public void run() {
				stateText.setText(getStateString(state));
			}});
	}
	
	private String getStateString(int state) {
		Resources res = ctx.getResources();
		String result = null;
		switch(state) {
		case Runtime.STATE_CREATED:
			result = res.getString(R.string.created);
			break;
		case Runtime.STATE_STARTED:
			result = res.getString(R.string.started);
			break;
		case Runtime.STATE_STOPPING:
			result = res.getString(R.string.stopping);
			break;
		case Runtime.STATE_STOPPED:
			result = res.getString(R.string.stopped);
			break;
		}
		return result;
	}
}