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

/**
 * A class to represent constant values.
 */
public class Constants {
    public static final String TAG = "MDKDisplay";

    public static String URL_PRIVACY_POLICY = "https://motorola.com/device-privacy";
    public static String URL_DEV_PORTAL = "http://developer.motorola.com/build/examples/display";
    public static String URL_SOURCE_CODE = "https://github.com/MotorolaMobilityLLC/mdkdisplay";

    public static final int INVALID_ID = -1;

    public static final byte BACKLIGHT_OFF = 0x00;
    public static final byte BACKLIGHT_ON = 0x1F;

    public static final int VID_MDK = 0x00000312;
    public static final int VID_DEVELOPER = 0x00000042;
    public static final int PID_DEVELOPER = 0x00000001;
    public static final int PID_MDK_DISPLAY = 0x00010903;
    public static final int VID_PROJECTOR = 0x00000128;
    public static final int PID_PROJECTOR = 0x00050005;
}
