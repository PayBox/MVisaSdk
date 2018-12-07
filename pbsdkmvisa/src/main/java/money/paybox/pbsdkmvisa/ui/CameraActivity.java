package money.paybox.pbsdkmvisa.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;
import java.util.HashMap;

import money.paybox.pbsdkmvisa.R;
import money.paybox.pbsdkmvisa.models.MVisa;
import money.paybox.pbsdkmvisa.utils.BarcodeGraphic;
import money.paybox.pbsdkmvisa.utils.BarcodeGraphicTracker;
import money.paybox.pbsdkmvisa.utils.BarcodeTrackerFactory;
import money.paybox.pbsdkmvisa.utils.CameraSource;
import money.paybox.pbsdkmvisa.utils.CameraSourcePreview;
import money.paybox.pbsdkmvisa.utils.GraphicOverlay;
import money.paybox.pbsdkmvisa.utils.MParser;
import money.paybox.payboxsdk.Utils.Constants;

public class CameraActivity extends AppCompatActivity implements BarcodeGraphicTracker.BarcodeUpdateListener {

    private TextView cameraError;
    private ImageButton flashView;
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;
    private boolean autoFocus = true;
    private boolean useFlash = false;
    private boolean flashIsChecked = false;
    private static final int RC_HANDLE_GMS = 9001;
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public static final String MVISA_EXTRA = "merchant";
    public static final String QR_ACTION = "money.paybox.DETECTED_ACTION";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_camera);
        cameraError = (TextView)findViewById(R.id.cameraError);
        flashView = (ImageButton)findViewById(R.id.flashView);
        mPreview = (CameraSourcePreview)findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>)findViewById(R.id.graphicOverlay);

        int rc = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }
        flashView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flashIsChecked){
                    flashIsChecked = false;
                    flashView.setActivated(false);
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                } else {
                    flashIsChecked = true;
                    flashView.setActivated(true);
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }
    private void requestCameraPermission() {
        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }
        ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);

    }


    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }
    }


    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                mPreview.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != 2) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.camera_not_permited))
                .setMessage(getString(R.string.camera_not_permited_message))
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .show();

    }



    @Override
    public synchronized void onBarcodeDetected(final Barcode barcode) {
        if(barcode.rawValue==null){
            return;
        }
        HashMap<String, String> hm = MParser.decode(barcode.rawValue);

        if(hm!=null && hm.containsKey(MParser.MVISA_AMOUNT_TAG) && hm.containsKey(MParser.MVISA_MERCHANT_ID_TAG) && hm.containsKey(MParser.MVISA_MERCHANT_NAME_TAG) && hm.containsKey(MParser.MVISA_CURRENCY_CODE_TAG)){
            cameraError.setTextColor(ColorStateList.valueOf(Color.TRANSPARENT));
            String amount = hm.get(MParser.MVISA_AMOUNT_TAG);
            String merchantId = hm.get(MParser.MVISA_MERCHANT_ID_TAG);
            String merchantName = hm.get(MParser.MVISA_MERCHANT_NAME_TAG);
            String currencyCode = MParser.getCurrencyByCodeISO4217(hm.get(MParser.MVISA_CURRENCY_CODE_TAG));
            Intent intent = new Intent();
            intent.putExtra(MVISA_EXTRA, new MVisa(merchantId, merchantName, currencyCode, Float.parseFloat(amount)));
            intent.setAction(QR_ACTION);
            sendBroadcast(intent);
            finish();
        } else {
            cameraError.setTextColor(ColorStateList.valueOf(Color.RED));
        }
    }
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(getApplicationContext()).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {

            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = this.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {

            }
        }
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        CameraSource.Builder builder = new CameraSource.Builder(getApplicationContext(), barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(size.y, size.x)
                .setRequestedFps(15.0f);
        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_OK);
        finish();
    }

}
