/**
 * Copyright (c) 2016 Motorola Mobility, LLC.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.motorola.samples.mdkdisplay;

import android.content.Context;
import android.widget.Toast;

import com.motorola.mod.ModDevice;
import com.motorola.mod.ModDisplay;
import com.motorola.mod.ModProtocol;

/**
 * A class to represent ModDisplay interface.
 */
public class DisplayPersonality extends Personality implements Personality.Display {
    /**
     * Instance of ModDisplay
     */
    private ModDisplay modDisplay;

    public DisplayPersonality(Context context) {
        super(context);
    }

    /** Mod device attach/detach, update modDisplay */
    @Override
    public void onModDevice(ModDevice d) {
        super.onModDevice(d);

        if (d == null || modManager == null) {
            modDisplay = null;
        } else {
            modDisplay = (ModDisplay) modManager.getClassManager(ModProtocol.Protocol.MODS_DISPLAY);
        }
    }

    /** Provide instance of Personality.Display interface */
    @Override
    public Personality.Display getDisplay() {
        return this;
    }

    /** Set mod device display state */
    @Override
    public boolean setModDisplayState(boolean state) {
        boolean result = false;
        if (modDisplay != null) {
            try {
                if (state) {
                    result = modDisplay.setModDisplayState(ModDisplay.STATE_ON);
                } else {
                    result = modDisplay.setModDisplayState(ModDisplay.STATE_OFF);
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /** Get mod device display state */
    @Override
    public boolean getModDisplayState() {
        boolean result = false;
        if (modDisplay != null) {
            try {
                if (modDisplay.getModDisplayState() == ModDisplay.STATE_ON) {
                    result = true;
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /** Set mod device display follow mode */
    @Override
    public boolean setModDisplayFollowState(boolean state) {
        boolean result = false;
        if (modDisplay != null) {
            try {
                result = modDisplay.setModDisplayFollowState(
                        state ? ModDisplay.POWER_MODE_FOLLOW : ModDisplay.POWER_MODE_NOFOLLOW);
            } catch (UnsupportedOperationException e) {
                Toast.makeText(context, context.getString(R.string.follow_mode_not_support),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }

    /** Get mod device display follow mode */
    @Override
    public boolean getModDisplayFollowState() {
        boolean result = false;
        if (modDisplay != null) {
            try {
                if (modDisplay.getModDisplayFollowState() == ModDisplay.POWER_MODE_FOLLOW) {
                    result = true;
                }
            } catch (UnsupportedOperationException e) {
                Toast.makeText(context, context.getString(R.string.follow_mode_not_support),
                        Toast.LENGTH_SHORT).show();
                e.printStackTrace();
                result = false;
            }
        }
        return result;
    }
}
