package com.android.systemui.statusbar.powerwidget;

import com.android.systemui.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.hardware.Camera;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class FlashlightButton extends PowerButton {
    private Camera mCamera = null;
	private Camera.Parameters mParams;
	private boolean useScreen;
	private boolean useLed = false;
    private static final List<Uri> OBSERVED_URIS = new ArrayList<Uri>();
    static {
        OBSERVED_URIS.add(Settings.System.getUriFor(Settings.System.TORCH_STATE));
    }

    public FlashlightButton() { mType = BUTTON_FLASHLIGHT; }

    @Override
    protected void updateState(Context context) {
        boolean enabled = Settings.System.getInt(context.getContentResolver(), Settings.System.TORCH_STATE, 0) == 1;
        if(enabled) {
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
        boolean bright = Settings.System.getInt(context.getContentResolver(),
                Settings.System.EXPANDED_FLASH_MODE, 0) == 1;
        Intent i = new Intent("net.cactii.flash2.TOGGLE_FLASHLIGHT");
        i.putExtra("bright", bright);
        context.sendBroadcast(i);
		}
    }

    @Override
    protected boolean handleLongClick(Context context) {
        // it may be better to make an Intent action for the Torch
        // we may want to look at that option later
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("net.cactii.flash2", "net.cactii.flash2.MainActivity");
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
    protected List<Uri> getObservedUris() {
        return OBSERVED_URIS;
    }
}
