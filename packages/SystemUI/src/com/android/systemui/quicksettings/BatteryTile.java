package com.android.systemui.quicksettings;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.BatteryManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.Settings;
import android.content.res.Resources;
import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.policy.BatteryController;
import com.android.systemui.statusbar.policy.BatteryController.BatteryStateChangeCallback;

public class BatteryTile extends QuickSettingsTile implements BatteryStateChangeCallback{
    private BatteryController mController;

    private int mBatteryLevel = 0;
    private int mBatteryStatus;
    private Drawable mBatteryIcon;
    private int mNumColumns;
	private int mTileTextSize;
    private int mTileTextPadding;
    private int mTileTextColor;
	
    public BatteryTile(Context context, QuickSettingsController qsc, BatteryController controller) {
        super(context, qsc, R.layout.quick_settings_tile_battery);

        mController = controller;

        mOnClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSettingsActivity(Intent.ACTION_POWER_USAGE_SUMMARY);
            }
        };
    }

    @Override
    void onPostCreate() {
        updateTile();
        mController.addStateChangedCallback(this);
        super.onPostCreate();
    }

    @Override
    public void onDestroy() {
        mController.removeStateChangedCallback(this);
        super.onDestroy();
    }

    @Override
    public void onBatteryLevelChanged(int level, int status) {
        mBatteryLevel = level;
        mBatteryStatus = status;
        updateResources();
    }

    @Override
    public void updateResources() {
        updateTile();
        super.updateResources();
    }

    private synchronized void updateTile() {
        final int drawableResId = mBatteryStatus == BatteryManager.BATTERY_STATUS_CHARGING
                ? R.drawable.qs_sys_battery_charging : R.drawable.qs_sys_battery;

        mBatteryIcon = mContext.getResources().getDrawable(drawableResId);

        if (mBatteryStatus == BatteryManager.BATTERY_STATUS_FULL) {
            mLabel = mContext.getString(R.string.quick_settings_battery_charged_label);
        } else if (mBatteryStatus == BatteryManager.BATTERY_STATUS_CHARGING) {
            mLabel = mContext.getString(R.string.quick_settings_battery_charging_label,
                    mBatteryLevel);
        } else {
            mLabel = mContext.getString(R.string.status_bar_settings_battery_meter_format,
                    mBatteryLevel);
        }
    }
	
   private void getTiles() {
	mNumColumns = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QUICK_TILES_PER_ROW, 3);
	mTileTextPadding = getTileTextPadding();
	mTileTextColor = getTileTextColor();
	mTileTextSize = getTileTextSize();
	}
	
    private int getTileTextPadding() {
        // get tile text padding based on column count
		final Resources res = mContext.getResources();
        switch (mNumColumns) {
            case 5:
                return res.getDimensionPixelSize(R.dimen.qs_5_column_text_padding);
            case 4:
                return res.getDimensionPixelSize(R.dimen.qs_4_column_text_padding);
            case 3:
            default:
                return res.getDimensionPixelSize(R.dimen.qs_tile_margin_below_icon);
        }
    }

    private int getTileTextColor() {
        int tileTextColor = Settings.System.getInt(mContext.getContentResolver(),
                Settings.System.QUICK_TILES_TEXT_COLOR, -2);

        return tileTextColor;
    }
     private int getTileTextSize() {
	 final Resources res = mContext.getResources();
        // get tile text size based on column count
        switch (mNumColumns) {
            case 5:
                return res.getDimensionPixelSize(R.dimen.qs_5_column_text_size);
            case 4:
                return res.getDimensionPixelSize(R.dimen.qs_4_column_text_size);
            case 3:
            default:
                return res.getDimensionPixelSize(R.dimen.qs_3_column_text_size);
        }
    }
    @Override
    void updateQuickSettings() {
	    getTiles();
        TextView tv = (TextView) mTile.findViewById(R.id.text);
        ImageView iv = (ImageView) mTile.findViewById(R.id.image);

        tv.setText(mLabel);
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTileTextSize);
        tv.setPadding(0, mTileTextPadding, 0, 0);
        if (mTileTextColor != -2) {
            tv.setTextColor(mTileTextColor);
			}
        iv.setImageDrawable(mBatteryIcon);
        iv.setImageLevel(mBatteryLevel);
    }

}
