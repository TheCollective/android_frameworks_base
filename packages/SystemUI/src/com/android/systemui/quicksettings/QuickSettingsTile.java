package com.android.systemui.quicksettings;

import android.app.ActivityManagerNative;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.RemoteException;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.systemui.R;
import com.android.systemui.statusbar.phone.QuickSettingsController;
import com.android.systemui.statusbar.phone.PhoneStatusBar;
import com.android.systemui.statusbar.phone.QuickSettingsContainerView;
import com.android.systemui.statusbar.phone.QuickSettingsTileView;

public class QuickSettingsTile implements OnClickListener {

    protected final Context mContext;
    protected QuickSettingsContainerView mContainer;
    protected QuickSettingsTileView mTile;
    protected OnClickListener mOnClick;
    protected OnLongClickListener mOnLongClick;
    protected int mTileLayout;
    protected int mDrawable;
    protected int mTileTextSize;
    protected int mTileTextPadding;
    protected int mTileTextColor;
    public static int mTileSize = 141;

    protected String mLabel;
    protected String name = "";
    protected String tileID = "0";

    protected PhoneStatusBar mStatusbarService;
    protected QuickSettingsController mQsc;


    public QuickSettingsTile(Context context, QuickSettingsController qsc) {
        this(context, qsc, R.layout.quick_settings_tile_basic);
    }

    public QuickSettingsTile(Context context, QuickSettingsController qsc, int layout) {
        mContext = context;
        mDrawable = R.drawable.ic_notifications;
        mLabel = mContext.getString(R.string.quick_settings_label_enabled);
        mStatusbarService = qsc.mStatusBarService;
        mQsc = qsc;
        mTileLayout = layout;
    }

    public void setupQuickSettingsTile(LayoutInflater inflater, QuickSettingsContainerView container) {
        mTile = (QuickSettingsTileView) inflater.inflate(R.layout.quick_settings_tile, container, false);
        mTile.setContent(mTileLayout, inflater);
        mContainer = container;
        mContainer.addView(mTile);
		mTile.addOnLayoutChangeListener(new OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom,
                    int oldLeft, int oldTop, int oldRight, int oldBottom)
            {
                if (left == 0 && top == 0 && right == 0 && bottom == 0)
                    return;
                int newSize = v.getHeight();
                if (newSize > 0 && newSize != mTileSize) {
                    mTileSize = newSize;
                }
            }
        });
		container.updateResources();
        mTileTextSize = container.getTileTextSize();
        mTileTextPadding = container.getTileTextPadding();
        mTileTextColor = container.getTileTextColor();
        mTileSize = mContext.getResources().getDimensionPixelSize(R.dimen.quick_settings_cell_height);
        onPostCreate();
        updateQuickSettings();
        mTile.setOnClickListener(this);
        mTile.setOnLongClickListener(mOnLongClick);
    }

    void onPostCreate(){}

    public void onDestroy() {}

    public void onReceive(Context context, Intent intent) {}

    public void onChangeUri(ContentResolver resolver, Uri uri) {}

    public void updateResources() {
        if(mTile != null) {
            updateQuickSettings();
        }
    }

    void updateQuickSettings(){
        TextView tv = (TextView) mTile.findViewById(R.id.text);
        if (tv != null) {
            tv.setText(mLabel);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTileTextSize);
            tv.setPadding(0, mTileTextPadding, 0, 0);
            if (mTileTextColor != -2) {
                tv.setTextColor(mTileTextColor);
            }
        }
        ImageView image = (ImageView) mTile.findViewById(R.id.image);
        if (image != null) {
            image.setImageResource(mDrawable);
        }
    }

    void startSettingsActivity(String action) {
        Intent intent = new Intent(action);
        startSettingsActivity(intent);
    }

    void startSettingsActivity(Intent intent) {
        startSettingsActivity(intent, true);
    }

    private void startSettingsActivity(Intent intent, boolean onlyProvisioned) {
        if (onlyProvisioned && !mStatusbarService.isDeviceProvisioned()) return;
        try {
            // Dismiss the lock screen when Settings starts.
            ActivityManagerNative.getDefault().dismissKeyguardOnNextActivity();
        } catch (RemoteException e) {
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivityAsUser(intent, new UserHandle(UserHandle.USER_CURRENT));
        mStatusbarService.animateCollapsePanels();
    }

    @Override
    public void onClick(View v) {
        if (mOnClick != null) {
            mOnClick.onClick(v);
        }

        ContentResolver resolver = mContext.getContentResolver();
        boolean shouldCollapse = Settings.System.getIntForUser(resolver,
                Settings.System.QS_COLLAPSE_PANEL, 0, UserHandle.USER_CURRENT) == 1;
        if (shouldCollapse) {
            mQsc.mBar.collapseAllPanels(true);
        }
    }

}
