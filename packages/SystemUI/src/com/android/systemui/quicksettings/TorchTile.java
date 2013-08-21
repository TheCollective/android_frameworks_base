package com.android.systemui.quicksettings;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.hardware.Camera;
import android.util.Log;
import java.util.List;

import com.android.internal.util.cm.TorchConstants;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;

public class TorchTile extends QuickSettingsTile {
    private Camera mCamera;
	private Camera.Parameters mParams;
	private boolean useScreen;
	private boolean useLed = false;
	private static final String TAG="Camera";
    private boolean mActive = false;

    public TorchTile(Context context, 
            QuickSettingsController qsc, Handler handler) {
        super(context, qsc);

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
			mCamera = null;
	    	checkCamera();	
        if (useScreen == true) {
		if (mCamera != null) {
		    mCamera.release();
			}
	    	Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.setClassName("android.flashlight", "android.flashlight.FlashlightActivity");
            startSettingsActivity(intent);
   
		
		} else {
	        	useLed = true;
                Intent i = new Intent(TorchConstants.ACTION_TOGGLE_STATE);
                mContext.sendBroadcast(i);
				}
            }
        };

        mOnLongClick = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startSettingsActivity(TorchConstants.INTENT_LAUNCH_APP);
                return true;
            }
        };

        qsc.registerAction(TorchConstants.ACTION_STATE_CHANGED, this);
    }

    @Override
    void onPostCreate() {
        updateTile();
        super.onPostCreate();
    }

    @Override
    public void updateResources() {
        updateTile();
        super.updateResources();
    }

    private synchronized void updateTile() {
        if (mActive) {
            mDrawable = R.drawable.ic_qs_torch_on;
            mLabel = mContext.getString(R.string.quick_settings_torch);
        } else {
            mDrawable = R.drawable.ic_qs_torch_off;
            mLabel = mContext.getString(R.string.quick_settings_torch_off);
        }
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
    public void onReceive(Context context, Intent intent) {
        mActive = intent.getIntExtra(TorchConstants.EXTRA_CURRENT_STATE, 0) != 0;
        updateResources();
    }
}
