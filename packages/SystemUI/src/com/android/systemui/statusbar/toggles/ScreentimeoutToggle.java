 
package com.android.systemui.statusbar.toggles;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.provider.Settings.System;
import android.view.View;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.android.systemui.R;
import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;
import android.app.Activity;

public class ScreentimeoutToggle extends Toggle {

    private int sc;
    int ScreenTimeout = 0;
    private boolean tripped = false;
	private int timeout;
	private int value;
	
     public ScreentimeoutToggle(Context context) {
        super(context);
		
		
        setLabel(R.string.toggle_screentimeout);
	    tripped = false;
        loadToggles();
        }
 
     private void setScreenTimeout(int sc) {
	 
        ContentResolver resolver = mContext.getContentResolver();
        Settings.System.putInt(resolver, Settings.System.SCREEN_OFF_TIMEOUT, sc);
        }

     public int getScreenTimeout() {
		ContentResolver cr = mContext.getContentResolver();
        return timeout = android.provider.Settings.System.getInt(cr, SCREEN_OFF_TIMEOUT, 0);
        }
        
     @Override
     protected boolean updateInternalToggleState() {
        
	if (tripped == true) { 	 
	int ScreenTimeout = getScreenTimeout();
         switch (ScreenTimeout) {
                 case -1: 
				 setIcon(R.drawable.toggle_30s);
				 value = 30000; 
                     
                    break;
					
                 case 30000: 
			     setIcon(R.drawable.toggle_1m);
			     value = 60000;
				
                    break;
					
                 case 60000: 
				 setIcon(R.drawable.toggle_2m);
				 value = 120000;
				
                    break;
					
				 case 120000: 
				 setIcon(R.drawable.toggle_10m);
				 value = 600000;
				
                    break;
				 case 600000: 
				 setIcon(R.drawable.toggle_on);
				 value = -1;
				
                    break;		
                 
				 default: 
				 setIcon(R.drawable.toggle_on);
				 value = -1;
				 }
				 	int sc = value;
                 setScreenTimeout(sc);
				 
       } else {
	        	tripped = true;
	}
    
//	mToggle.setChecked(true);
  
    return tripped;
			
   }
	
  
  
  private void loadToggles() {
  tripped = false;
  int timeoutToggle = getScreenTimeout();
  switch (timeoutToggle) {
                 case -1: 
				 setIcon(R.drawable.toggle_on);
	//			 value = -1;
				 
                     
                    break;
					
                 case 30000: 
				 setIcon(R.drawable.toggle_30s);
	//			 value = 30000; 
			     
				
                    break;
					
                 case 60000: 
				 setIcon(R.drawable.toggle_1m);
	//		     value = 60000;
				 
				
                    break;
					
				 case 120000: 
				 setIcon(R.drawable.toggle_2m);
		//		 value = 120000;
				 
				
                    break;
					
				 case 600000: 
				 setIcon(R.drawable.toggle_10m);
		//		 value = 600000;
				
                    break;		
                 
				 default: 
				 setIcon(R.drawable.toggle_settings);
			//	 value = timeoutToggle;
            }
  mToggle.setChecked(true);
  }
  
  @Override
  protected void onCheckChanged(boolean isChecked) {
      

   //     mToggle.setChecked(true);
        updateState();
    }
	
    @Override
    protected boolean onLongPress() {
        if (mContext != null) {
            Intent intent = new Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
        return true;
    }
}