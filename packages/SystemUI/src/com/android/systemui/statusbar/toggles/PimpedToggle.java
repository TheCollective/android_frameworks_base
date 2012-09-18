/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.systemui.statusbar.toggles;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.systemui.R;

public class PimpedToggle extends Toggle {

    private static final String TAG = "Pimped";
    private boolean pimpedOn;

    public PimpedToggle(Context c) {
        super(c);

        setLabel(R.string.toggle_pimped);
        updateState();
    }

    @Override
    protected void onCheckChanged(boolean isChecked) {
        if (isChecked) {
            pimpedOn = true;
            mContext.sendBroadcast(new Intent("LEAK_BUTT3R"));
            Log.e("Pimped", "CAUTION: You have been pimped.");
        }
        if (pimpedOn)
            mToggle.setChecked(true);
        updateState();

    }

    @Override
    protected boolean onLongPress() {
        Toast.makeText(mContext, "Pimp Juice!", Toast.LENGTH_LONG).show();
        return true;
    }

    @Override
    protected boolean updateInternalToggleState() {
        if (mToggle.isChecked()) {
            setIcon(R.drawable.toggle_pimped);
        } else {
            setIcon(R.drawable.toggle_pimped_off);
        }
        return mToggle.isChecked();
    }
}