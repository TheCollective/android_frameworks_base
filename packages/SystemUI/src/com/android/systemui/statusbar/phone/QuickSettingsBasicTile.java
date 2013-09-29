/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.systemui.statusbar.phone;

import com.android.systemui.R;
import android.util.TypedValue;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.provider.Settings;
import android.content.res.Resources;
class QuickSettingsBasicTile extends QuickSettingsTileView {
    private final TextView mTextView;
    private final ImageView mImageView;
    private int mNumColumns;
	private int mTileTextSize;
    private int mTileTextPadding;
    private int mTileTextColor;
	Context mContext;
    public QuickSettingsBasicTile(Context context) {
        this(context, null);
    }

    public QuickSettingsBasicTile(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        setLayoutParams(new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            context.getResources().getDimensionPixelSize(R.dimen.quick_settings_cell_height)
        ));
        setBackgroundResource(R.drawable.qs_tile_background);
		getTiles();
        addView(LayoutInflater.from(context).inflate(
                R.layout.quick_settings_tile_basic, null),
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT));
        mTextView = (TextView) findViewById(R.id.text);
        mImageView = (ImageView) findViewById(R.id.image);
		TextView tv = (TextView) findViewById(R.id.text);
            tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTileTextSize);
            tv.setPadding(0, mTileTextPadding, 0, 0);
            if (mTileTextColor != -2) {
                tv.setTextColor(mTileTextColor);
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
    public ImageView getImageView() {
        return mImageView;
    }

    public TextView getTextView() {
        return mTextView;
    }

    public void setImageDrawable(Drawable drawable) {
        mImageView.setImageDrawable(drawable);
    }

    public void setImageResource(int resId) {
        mImageView.setImageResource(resId);
    }

    public void setText(CharSequence text) {
        mTextView.setText(text);
    }

    public void setTextResource(int resId) {
        mTextView.setText(resId);
    }
}
