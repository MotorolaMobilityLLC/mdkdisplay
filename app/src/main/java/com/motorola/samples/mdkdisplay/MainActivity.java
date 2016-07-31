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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Presentation;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.motorola.mod.ModBacklight;
import com.motorola.mod.ModDevice;

/**
 * A class to represent main activity.
 */
public class MainActivity extends Activity implements View.OnClickListener {
    public static final String MOD_UID = "mod_uid";

    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int SELECT_PHOTO = 101;

    /**
     * Remote view to show on mod device display. For future details,
     * refer to https://developer.android.com/reference/android/app/Presentation.html
     */
    private Presentation presentation;

    /**
     * The on top view to show selfie tips
     */
    private RelativeLayout tipsView;

    /**
     * Instance of MDK Personality Card interface
     */
    private Personality personality;

    /**
     * Handler for events from mod device
     */
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Personality.MSG_MOD_DEVICE:
                    /** Mod attach/detach */
                    ModDevice device = personality.getModDevice();
                    onModDevice(device);
                    break;
                default:
                    Log.i(Constants.TAG, "MainActivity - Un-handle events: " + msg.what);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setActionBar(toolbar);

        /** Switch button for toggle mod device display */
        Switch switchDisplay = (Switch) findViewById(R.id.display_switch);
        if (switchDisplay != null) {
            switchDisplay.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (null == personality || personality.getDisplay() == null
                            || personality.getModDevice() == null) {
                        Toast.makeText(MainActivity.this, getString(R.string.display_not_available),
                                Toast.LENGTH_SHORT).show();
                        buttonView.setChecked(false);
                        updateUIWidgets(false);
                        return;
                    }

                    final CompoundButton button = buttonView;
                    final boolean checked = isChecked;
                    button.setEnabled(false);

                    /**
                     * Toggle mod device display taking time, execute it in thread to
                     * avoid block UI
                     */
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            /** Toggle Mod Display when the state is not expected */
                            if (personality.getDisplay().getModDisplayState() != checked) {
                                personality.getDisplay().setModDisplayState(checked);
                            }

                            /** Toggle Mod Display back light */
                            ModBacklight backlight = personality.getModManager().getClassManager(
                                    personality.getModDevice(), ModBacklight.class);
                            if (backlight != null) {
                                backlight.setModBacklightBrightness(
                                        checked ? Constants.BACKLIGHT_ON : Constants.BACKLIGHT_OFF);
                            }

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    button.setEnabled(true);
                                    updateUIWidgets(checked);
                                }
                            });
                        }
                    }).start();
                }
            });
        }

        TextView textView = (TextView)findViewById(R.id.mod_external_dev_portal);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        textView = (TextView)findViewById(R.id.mod_external_buy_mdk);
        if (textView != null) {
            textView.setOnClickListener(this);
        }

        Button button = (Button)findViewById(R.id.status_choose_image);
        if (button != null) {
            button.setOnClickListener(this);
        }

        button = (Button)findViewById(R.id.status_camera);
        if (button != null) {
            button.setOnClickListener(this);
        }

        button = (Button)findViewById(R.id.status_clock);
        if (button != null) {
            button.setOnClickListener(this);
        }

        button = (Button)findViewById(R.id.status_clear);
        if (button != null) {
            button.setOnClickListener(this);
        }
    }

    /** Update UI button widgets status */
    private void updateUIWidgets(boolean enable) {
        Button btCamera = (Button) findViewById(R.id.status_camera);
        if (btCamera != null) {
            btCamera.setEnabled(enable);
        }

        Button btClock = (Button) findViewById(R.id.status_clock);
        if (btClock != null) {
            btClock.setEnabled(enable);
        }

        Button btImage = (Button) findViewById(R.id.status_choose_image);
        if (btImage != null) {
            btImage.setEnabled(enable);
        }

        Button btClear = (Button) findViewById(R.id.status_clear);
        if (btClear != null) {
            btClear.setEnabled(enable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            /** Get the UUID from attached mod device */
            String uid = getString(R.string.na);
            if (personality != null
                    && personality.getModDevice() != null
                    && personality.getModDevice().getUniqueId() != null) {
                uid = personality.getModDevice().getUniqueId().toString();
            }

            startActivity(new Intent(this, AboutActivity.class).putExtra(MOD_UID, uid));
            return true;
        }

        if (id == R.id.action_policy) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_PRIVACY_POLICY)));
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        /** Clean up presentation view */
        if (presentation != null) {
            presentation.dismiss();
            presentation = null;
        }

        /** Clean up selfie tips view */
        if (tipsView != null) {
            tipsView.setVisibility(View.GONE);

            WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(
                    Context.WINDOW_SERVICE);
            windowManager.removeView(tipsView);

            tipsView = null;
        }

        /** Clean up mod interface */
        releasePersonality();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        /** Hide selfie tips view */
        if (tipsView != null) {
            tipsView.setVisibility(View.GONE);
        }

        /** Initial mod personality interface */
        initPersonality();

        /** Update Display switch button base on currently mod device status */
        Switch switcher = (Switch) findViewById(R.id.display_switch);
        if (switcher != null) {
            if (personality.getDisplay() != null) {
                boolean state = personality.getDisplay().getModDisplayState();
                switcher.setChecked(state);
            }
        }
    }

    /** Initial mod personality interface */
    private void initPersonality() {
        if (null == personality) {
            personality = new DisplayPersonality(this);
            personality.registerListener(handler);
        }
    }

    /** Clean up mod personality interface */
    private void releasePersonality() {
        if (null != personality) {
            personality.onDestroy();
            personality = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * Handler the permission requesting result
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION
                && grantResults != null && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                /** Got permission granted, go to select pictures */
                selectPhoto();
            } else {
                // TODO: User declined for storage accessing permission.
                // You may need pop up a description dialog or other prompts to explain
                // the app cannot work without the permission granted.
            }
        }
    }

    /** Mod device attach/detach */
    public void onModDevice(ModDevice device) {
        /** Clean up the remote presentation view */
        if (presentation != null) {
            presentation.dismiss();
            presentation = null;
        }

        /** Moto Mods Status */
        /**
         * Get mod device's Product String, which should correspond to
         * the product name or the vendor internal's name.
         */
        TextView tvName = (TextView) findViewById(R.id.mod_name);
        if (null != tvName) {
            if (null != device) {
                tvName.setText(device.getProductString());
            } else {
                tvName.setText(getString(R.string.na));
            }
        }

        /**
         * Get mod device's Vendor ID. This is assigned by the Motorola
         * and unique for each vendor.
         */
        TextView tvVid = (TextView) findViewById(R.id.mod_status_vid);
        if (null != tvVid) {
            if (device == null
                    || device.getVendorId() == Constants.INVALID_ID) {
                tvVid.setText(getString(R.string.na));
            } else {
                tvVid.setText(String.format(getString(R.string.mod_pid_vid_format),
                        device.getVendorId()));
            }
        }

        /** Get mod device's Product ID. This is assigned by the vendor */
        TextView tvPid = (TextView) findViewById(R.id.mod_status_pid);
        if (null != tvPid) {
            if (device == null
                    || device.getProductId() == Constants.INVALID_ID) {
                tvPid.setText(getString(R.string.na));
            } else {
                tvPid.setText(String.format(getString(R.string.mod_pid_vid_format),
                        device.getProductId()));
            }
        }

        /** Get mod device's version of the firmware */
        TextView tvFirmware = (TextView) findViewById(R.id.mod_status_firmware);
        if (null != tvFirmware) {
            if (null != device && null != device.getFirmwareVersion()
                    && !device.getFirmwareVersion().isEmpty()) {
                tvFirmware.setText(device.getFirmwareVersion());
            } else {
                tvFirmware.setText(getString(R.string.na));
            }
        }

        /**
         * Get the default Android application associated with the currently attached mod,
         * as read from the mod hardware manifest.
         */
        TextView tvPackage = (TextView) findViewById(R.id.mod_status_package_name);
        if (null != tvPackage) {
            if (device == null
                    || personality.getModManager() == null) {
                tvPackage.setText(getString(R.string.na));
            } else {
                if (personality.getModManager() != null) {
                    String modPackage = personality.getModManager().getDefaultModPackage(device);
                    if (null == modPackage || modPackage.isEmpty()) {
                        modPackage = getString(R.string.name_default);
                    }
                    tvPackage.setText(modPackage);
                }
            }
        }

        /** Moto Mods Display */
        /** Set Display switch button based on currently mod device display status */
        Switch switcher = (Switch) findViewById(R.id.display_switch);
        TextView tvReason = (TextView) findViewById(R.id.switch_reason);
        if (switcher != null) {
            if ((device != null) && (personality.getDisplay() != null) &&
                    ((device.getVendorId() == Constants.VID_DEVELOPER) ||
                            ((device.getVendorId() == Constants.VID_MDK) &&
                            (device.getProductId() == Constants.PID_MDK_DISPLAY)))) {
                if (tvReason != null) {
                    tvReason.setText(R.string.display_description);
                }
                boolean state = personality.getDisplay().getModDisplayState();
                switcher.setChecked(state);
                switcher.setEnabled(true);
            } else {
                if (tvReason != null) {
                    if ((device == null) || (device.getVendorId() != Constants.VID_MDK)) {
                        tvReason.setText(R.string.attach_pcard);
                    } else {
                        tvReason.setText(R.string.mdk_switch);
                    }
                }
                switcher.setEnabled(false);
                switcher.setChecked(false);
            }
        }
    }

    /** Check current mod whether in developer mode */
    private boolean isMDKMod(ModDevice device) {
        if (device == null) {
            /** Mod is not available */
            return false;
        } else if (device.getVendorId() == Constants.VID_DEVELOPER
                && device.getProductId() == Constants.PID_DEVELOPER) {
            // MDK in developer mode
            return true;
        } else {
            // Check MDK
            return device.getVendorId() == Constants.VID_MDK;
        }
    }

    /** UI button widgets click events */
    @Override
    public void onClick(View v) {
        if (v == null) {
            return;
        }

        switch (v.getId()) {
            case R.id.mod_external_dev_portal:
                /** The Developer Portal link is clicked */
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_DEV_PORTAL)));
                break;
            case R.id.mod_external_buy_mdk:
                /** The Buy Mods link is clicked */
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.URL_MOD_STORE)));
                break;
            case R.id.status_choose_image:
                /** The Presentation Photo button is clicked */
                selectPhoto();
                break;
            case R.id.status_camera:
                /** The Selfie button is clicked */
                onCamera();
                break;
            case R.id.status_clock:
                /** The Clock button is clicked */
                onClock();
                break;
            case R.id.status_clear:
                /** The Reset button is clicked */
                if (presentation != null) {
                    presentation.dismiss();
                    presentation = null;
                }
                Toast.makeText(this, getString(R.string.mirror_mode), Toast.LENGTH_SHORT).show();
                break;
            default:
                Log.i(Constants.TAG, "Alert: Main action not handle.");
                break;
        }
    }

    /** Start Camera in mirror mode, view finder will show on mod device display screen */
    private void onCamera() {
        Intent intent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);

            /** Create a on top tip view for prompt text */
            createTipView();
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** Create a on top tip view for prompt text */
    private void createTipView() {
        if (tipsView != null) {
            tipsView.setVisibility(View.VISIBLE);
        } else {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            tipsView = (RelativeLayout) inflater.inflate(R.layout.view_topview, null, false);

            WindowManager windowManager = (WindowManager) getApplicationContext().getSystemService(
                    Context.WINDOW_SERVICE);
            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
            params.format = PixelFormat.RGBA_8888;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            params.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION;

            /** Set view position */
            int position = 256;
            params.x = 10;
            params.y = position * 2;
            params.width = position * 5;
            params.height = position;

            windowManager.addView(tipsView, params);
        }
    }

    /** Show a clock on remote presentation view */
    private void onClock() {
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if (presentationDisplays.length > 0) {
            /**
             * If there is more than one suitable presentation display, then we could consider
             * giving the user a choice.  For this example, we simply choose the first display
             * which is the one the system recommends as the preferred presentation display.
             */
            Display display = presentationDisplays[0];

            try {
                /** Clean up previously presentation view */
                if (presentation != null) {
                    presentation.dismiss();
                    presentation = null;
                }
                presentation = new ClockActivity(this, display);
                presentation.show();
                Toast.makeText(this, getString(R.string.presentation_mode), Toast.LENGTH_SHORT).show();
            } catch (WindowManager.InvalidDisplayException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.external_display_not_available));
            alert.setPositiveButton(android.R.string.ok, null);
            alert.show();
        }
    }

    /** Show a picture on remote presentation view */
    private void onPresentation(Uri image) {
        DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
        if (presentationDisplays.length > 0) {
            /**
             * If there is more than one suitable presentation display, then we could consider
             * giving the user a choice.  For this example, we simply choose the first display
             * which is the one the system recommends as the preferred presentation display.
             */
            Display display = presentationDisplays[0];
            try {
                /** Clean up previously presentation view */
                if (presentation != null) {
                    presentation.dismiss();
                    presentation = null;
                }
                presentation = new PresentationActivity(this, display, image);
                presentation.show();

                Toast.makeText(this, getString(R.string.presentation_mode), Toast.LENGTH_SHORT).show();
            } catch (WindowManager.InvalidDisplayException e) {
                e.printStackTrace();
            }
        } else {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setMessage(getString(R.string.external_display_not_available));
            alert.setPositiveButton(android.R.string.ok, null);
            alert.show();
        }
    }

    /**
     * Select a picture to show on remote presentation view
     */
    private void selectPhoto() {
        if (checkSelfPermission("android.permission.READ_EXTERNAL_STORAGE")
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{"android.permission.READ_EXTERNAL_STORAGE"},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            DisplayManager displayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
            Display[] presentationDisplays = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION);
            if (presentationDisplays.length > 0) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i,
                        getString(R.string.select_picture)), SELECT_PHOTO);
            } else {
                AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setMessage(getString(R.string.external_display_not_available));
                alert.setPositiveButton(android.R.string.ok, null);
                alert.show();
            }
        }
    }

    /** The picture is selected */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        switch (requestCode) {
            case SELECT_PHOTO:
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = imageReturnedIntent.getData();
                    onPresentation(selectedImage);
                }
                break;
            default:
                break;
        }
    }
}
