package com.android.systemui.statusbar.powerwidget;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.UserHandle;
import android.provider.Settings;
import android.hardware.Camera;
import com.android.internal.util.cm.TorchConstants;
import com.android.systemui.R;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class FlashlightButton extends PowerButton {
    private Camera mCamera = null;
	private Camera.Parameters mParams;
	private boolean useScreen;
	private boolean useLed = false;
    private static final IntentFilter STATE_FILTER =
            new IntentFilter(TorchConstants.ACTION_STATE_CHANGED);
    private boolean mActive = false;

    public FlashlightButton() { mType = BUTTON_FLASHLIGHT; }

    @Override
    protected void updateState(Context context) {
        if (mActive) {
            mIcon = R.drawable.stat_flashlight_on;
            mState = STATE_ENABLED;
        } else {
            mIcon = R.drawable.stat_flashlight_off;
            mState = STATE_DISABLED;
        }
    }

    @Override
    protected void toggleState(Context context) {
	    mCamera = null;
	    checkCamera();
		
	    if (useScreen == true) {
		if (mCamera != null) {
		    mCamera.release();
			}
		Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("android.flashlight", "android.flashlight.FlashlightActivity");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
   
		
		} else {
		useLed = true;
		useScreen = false;
        boolean bright = Settings.System.getIntForUser(context.getContentResolver(),
                Settings.System.EXPANDED_FLASH_MODE, 0, UserHandle.USER_CURRENT) == 1;
        Intent i = new Intent(TorchConstants.ACTION_TOGGLE_STATE);
        i.putExtra(TorchConstants.EXTRA_BRIGHT_MODE, bright);
        context.sendBroadcast(i);
		}
    }

    @Override
    protected boolean handleLongClick(Context context) {
        Intent intent = new Intent(TorchConstants.INTENT_LAUNCH_APP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        return true;
    }
	private void getCamera() {
        if (mCamera == null) {
            try {
                mCamera = Camera.open();
            } catch (RuntimeException e) {
                Log.e(TAG, "Camera.open() failed: " + e.getMessage());
            }
        }
    }
    private void checkCamera() {
	if (useLed == false) {
        getCamera();
        if (mCamera == null) {
            Log.d(TAG, "Camera not Found!");
            useScreen = true;
            return;
        }
       
        mParams = mCamera.getParameters();
        if (mParams == null) {
            Log.d(TAG, "Camera Params not Found!");
			useScreen = true;
			mCamera.release();
            return;
        }
        List<String> flashModes = mParams.getSupportedFlashModes();
        // Check if camera flash exists
        if (flashModes == null) {
            Log.d(TAG, "Camera Flash not Found!");
			useScreen = true;
			mCamera.release();
            return;
        }
        
		mCamera.release();
	  }	
	}

    @Override
    protected IntentFilter getBroadcastIntentFilter() {
        return STATE_FILTER;
    }

    @Override
    protected void onReceive(Context context, Intent intent) {
        mActive = intent.getIntExtra(TorchConstants.EXTRA_CURRENT_STATE, 0) != 0;
    }
}
