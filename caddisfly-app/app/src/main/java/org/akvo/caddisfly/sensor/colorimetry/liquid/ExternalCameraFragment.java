/*
 * Copyright (C) Stichting Akvo (Akvo Foundation)
 *
 * This file is part of Akvo Caddisfly.
 *
 * Akvo Caddisfly is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Akvo Caddisfly is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Akvo Caddisfly. If not, see <http://www.gnu.org/licenses/>.
 */

package org.akvo.caddisfly.sensor.colorimetry.liquid;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.SurfaceTexture;
import android.graphics.drawable.ColorDrawable;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;

import org.akvo.caddisfly.AppConfig;
import org.akvo.caddisfly.R;
import org.akvo.caddisfly.helper.SoundPoolPlayer;
import org.akvo.caddisfly.sensor.CameraDialog;
import org.akvo.caddisfly.usb.DeviceFilter;
import org.akvo.caddisfly.usb.USBMonitor;
import org.akvo.caddisfly.usb.USBMonitor.OnDeviceConnectListener;
import org.akvo.caddisfly.usb.USBMonitor.UsbControlBlock;
import org.akvo.caddisfly.usb.UVCCamera;
import org.akvo.caddisfly.widget.CameraViewInterface;

import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.List;

public final class ExternalCameraFragment extends CameraDialog {

    private static final int INITIAL_DELAY_MILLIS = 500;
    /**
     * preview resolution(width)
     * if your camera does not support specific resolution and mode,
     * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
     */
    private static final int PREVIEW_WIDTH = 1280;
    /**
     * preview resolution(height)
     * if your camera does not support specific resolution and mode,
     * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
     */
    private static final int PREVIEW_HEIGHT = 720;
    /**
     * preview mode
     * if your camera does not support specific resolution and mode,
     * {@link UVCCamera#setPreviewSize(int, int, int)} throw exception
     * 0:YUYV, other:MJPEG
     */
    private static final int PREVIEW_MODE = 1;
    private final Handler delayHandler = new Handler();
    /**
     * for accessing USB
     */
    private USBMonitor mUSBMonitor;
    /**
     * Handler to execute camera related methods sequentially on private thread
     */
    private CameraHandler mHandler;
    /**
     * for camera preview display
     */
    private CameraViewInterface mUVCCameraView;
    private final OnDeviceConnectListener mOnDeviceConnectListener = new OnDeviceConnectListener() {
        @Override
        public void onAttach(final UsbDevice device) {
            Toast.makeText(getActivity(), "USB_DEVICE_ATTACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onConnect(final UsbDevice device, final UsbControlBlock ctrlBlock, final boolean createNew) {
            mHandler.openCamera(ctrlBlock);
            startPreview();
        }

        @Override
        public void onDisconnect(final UsbDevice device, final UsbControlBlock ctrlBlock) {
            if (mHandler != null) {
                mHandler.closeCamera();
            }
        }

        @Override
        public void onDetach(final UsbDevice device) {
            //Toast.makeText(ExternalCameraFragment.this, "USB_DEVICE_DETACHED", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel() {
        }
    };
    private int mNumberOfPhotosToTake;
    private int mPhotoCurrentCount = 0;
    private SoundPoolPlayer sound;
    private long mSamplingDelay;

    public static ExternalCameraFragment newInstance() {
        return new ExternalCameraFragment();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sound = new SoundPoolPlayer(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        sound.release();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_external_camera, container, false);
        final View cameraView = view.findViewById(R.id.uvcCameraView);
        mUVCCameraView = (CameraViewInterface) cameraView;
        mUVCCameraView.setAspectRatio(PREVIEW_WIDTH / (float) PREVIEW_HEIGHT);
        mUSBMonitor = new USBMonitor(getActivity(), mOnDeviceConnectListener);
        mHandler = CameraHandler.createHandler(this, mUVCCameraView);

        return view;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                if (getActivity() instanceof Cancelled) {
                    ((Cancelled) getActivity()).dialogCancelled();
                }
                dismiss();
            }
        };

        if (dialog.getWindow() != null) {
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }

        return dialog;
    }


    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = this.getDialog();
        Window window = dialog.getWindow();
        try {
            window.setLayout(PREVIEW_HEIGHT, PREVIEW_WIDTH);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        if (getActivity() instanceof Cancelled) {
            ((Cancelled) getActivity()).dialogCancelled();
        }
        super.onCancel(dialog);
    }

    private UsbDevice getCameraDevice(List<UsbDevice> usbDeviceList) {

        for (int i = 0; i < usbDeviceList.size(); i++) {
            if (usbDeviceList.get(i).getVendorId() == AppConfig.CAMERA_VENDOR_ID) {
                return usbDeviceList.get(i);
            }
        }

        return null;
    }

//    private List<UsbDevice> getUsbDevices() {
//        final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity(), R.xml.camera_device_filter);
//        return mUSBMonitor.getDeviceList(filter.get(0));
//    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Runnable delayRunnable = new Runnable() {
            @Override
            public void run() {
                if (mHandler != null && !mHandler.isCameraOpened()) {
                    final List<DeviceFilter> filter = DeviceFilter.getDeviceFilters(getActivity(), R.xml.camera_device_filter);
                    List<UsbDevice> usbDeviceList = mUSBMonitor.getDeviceList(filter.get(0));
                    if (getCameraDevice(usbDeviceList) != null) {
                        final Object item = getCameraDevice(usbDeviceList);
                        mUSBMonitor.requestPermission((UsbDevice) item);
                    }
                }
            }
        };

        delayHandler.postDelayed(delayRunnable, INITIAL_DELAY_MILLIS);
    }

    @Override
    public void onResume() {
        super.onResume();
        mUSBMonitor.register();
    }

    @Override
    public void onPause() {
        mHandler.closeCamera();
        if (mUVCCameraView != null) {
            mUVCCameraView.onPause();
        }
        mUSBMonitor.unregister();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (mHandler != null) {
//            mHandler.release();
            mHandler = null;
        }
        try {
            if (mUSBMonitor != null) {
                mUSBMonitor.destroy();
                mUSBMonitor = null;
            }
        } catch (Exception e) {
            // do nothing
        }
        mUVCCameraView = null;
        super.onDestroy();
    }

    private void startPreview() {
        final SurfaceTexture st = mUVCCameraView.getSurfaceTexture();
        mHandler.startPreview(new Surface(st));
    }

    private void takePicture() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                if (getActivity() != null && mHandler != null && mHandler.isCameraOpened()) {
                    mHandler.captureStill();
                }
            }
        }, mSamplingDelay);
    }

    private boolean hasTestCompleted() {
        return mPhotoCurrentCount >= mNumberOfPhotosToTake;
    }

    @Override
    public void takePictureSingle() {
        if (mHandler.isCameraOpened()) {
            mNumberOfPhotosToTake = 1;
            mPhotoCurrentCount = 0;
            takePicture();
        }
    }

    @Override
    public void takePictures(int count, long delay) {
        mNumberOfPhotosToTake = count;
        mPhotoCurrentCount = 0;
        mSamplingDelay = delay;

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                takePicture();
            }
        }, delay);
    }

    @Override
    public void stopCamera() {

    }

    /**
     * Handler class to execute camera related methods sequentially on private thread
     */
    private static final class CameraHandler extends Handler {
        private static final int MSG_OPEN = 0;
        private static final int MSG_CLOSE = 1;
        private static final int MSG_PREVIEW_START = 2;
        private static final int MSG_PREVIEW_STOP = 3;
        private static final int MSG_CAPTURE_STILL = 4;
        private static final int MSG_RELEASE = 9;

        private final WeakReference<CameraThread> mWeakThread;

        private CameraHandler(final CameraThread thread) {
            mWeakThread = new WeakReference<>(thread);
        }

        static CameraHandler createHandler(final ExternalCameraFragment parent, final CameraViewInterface cameraView) {
            final CameraThread thread = new CameraThread(parent, cameraView);
            thread.start();
            return thread.getHandler();
        }

        boolean isCameraOpened() {
            final CameraThread thread = mWeakThread.get();
            return thread != null && thread.isCameraOpened();
        }

        void openCamera(final UsbControlBlock ctrlBlock) {
            sendMessage(obtainMessage(MSG_OPEN, ctrlBlock));
        }

        void closeCamera() {
            stopPreview();
            sendEmptyMessage(MSG_CLOSE);
        }

        void startPreview(final Surface surface) {
            if (surface != null) {
                sendMessage(obtainMessage(MSG_PREVIEW_START, surface));
            }
        }

        void stopPreview() {
            final CameraThread thread = mWeakThread.get();
            if (thread == null) {
                return;
            }
            synchronized (thread.mSync) {
                sendEmptyMessage(MSG_PREVIEW_STOP);
                // wait for actually preview stopped to avoid releasing Surface/SurfaceTexture
                // while preview is still running.
                // therefore this method will take a time to execute
                try {
                    thread.mSync.wait();
                } catch (final InterruptedException ignored) {
                }
            }
        }

        void captureStill() {
            sendEmptyMessage(MSG_CAPTURE_STILL);
        }

        @Override
        public void handleMessage(final Message msg) {
            final CameraThread thread = mWeakThread.get();
            if (thread == null) {
                return;
            }
            switch (msg.what) {
                case MSG_OPEN:
                    thread.handleOpen((UsbControlBlock) msg.obj);
                    break;
                case MSG_CLOSE:
                    thread.handleClose();
                    break;
                case MSG_PREVIEW_START:
                    thread.handleStartPreview((Surface) msg.obj);
                    break;
                case MSG_PREVIEW_STOP:
                    thread.handleStopPreview();
                    break;
                case MSG_CAPTURE_STILL:
                    thread.handleCaptureStill();
                    break;
                case MSG_RELEASE:
                    thread.handleRelease();
                    break;
                default:
                    throw new IllegalArgumentException("unsupported message:what=" + msg.what);
            }
        }

        private static final class CameraThread extends Thread {
            private final Object mSync = new Object();
            private final WeakReference<ExternalCameraFragment> mWeakParent;
            private final WeakReference<CameraViewInterface> mWeakCameraView;
            /**
             * shutter sound
             */
            private CameraHandler mHandler;
            /**
             * for accessing UVC camera
             */
            private UVCCamera mUVCCamera;

            private CameraThread(final ExternalCameraFragment parent, final CameraViewInterface cameraView) {
                super("CameraThread");
                mWeakParent = new WeakReference<>(parent);
                mWeakCameraView = new WeakReference<>(cameraView);
            }

            public CameraHandler getHandler() {
                synchronized (mSync) {
                    while (mHandler == null) {
                        try {
                            mSync.wait();
                        } catch (final InterruptedException ignored) {
                        }
                    }
                }
                return mHandler;
            }

            boolean isCameraOpened() {
                return mUVCCamera != null;
            }

            void handleOpen(final UsbControlBlock ctrlBlock) {
                handleClose();
                mUVCCamera = new UVCCamera();
                mUVCCamera.open(ctrlBlock);
            }

            void handleClose() {
                if (mUVCCamera != null) {
                    mUVCCamera.stopPreview();
                    mUVCCamera.destroy();
                    mUVCCamera = null;
                }
            }

            void handleStartPreview(final Surface surface) {
                if (mUVCCamera == null) {
                    return;
                }
                try {
                    mUVCCamera.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT, PREVIEW_MODE);
                } catch (final IllegalArgumentException e) {
                    try {
                        // fallback to YUV mode
                        mUVCCamera.setPreviewSize(PREVIEW_WIDTH, PREVIEW_HEIGHT, UVCCamera.DEFAULT_PREVIEW_MODE);
                    } catch (final IllegalArgumentException e1) {
                        handleClose();
                    }
                }
                if (mUVCCamera != null) {
//                    mUVCCamera.setFrameCallback(mIFrameCallback, UVCCamera.PIXEL_FORMAT_YUV);
                    mUVCCamera.setPreviewDisplay(surface);
                    mUVCCamera.startPreview();
                }
            }

            void handleStopPreview() {
                if (mUVCCamera != null) {
                    mUVCCamera.stopPreview();
                }
                synchronized (mSync) {
                    mSync.notifyAll();
                }
            }

            void handleCaptureStill() {
                final ExternalCameraFragment parent = mWeakParent.get();
                if (parent == null) {
                    return;
                }

                CameraViewInterface cameraViewInterface = mWeakCameraView.get();
                if (cameraViewInterface != null) {
                    final Bitmap bitmap = cameraViewInterface.captureStillImage();

                    parent.mPhotoCurrentCount++;

                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    for (PictureTaken pictureTakenObserver : parent.pictureTakenObservers) {
                        pictureTakenObserver.onPictureTaken(byteArray, parent.hasTestCompleted());
                    }
                }
                //if (!mCancelled) {
                if (!parent.hasTestCompleted()) {
                    //parent.pictureCallback.onPictureTaken(bitmap);
                    parent.takePicture();
                }
                //}
            }

            void handleRelease() {
                handleClose();
            }


            @Override
            public void run() {
                Looper.prepare();
                synchronized (mSync) {
                    mHandler = new CameraHandler(this);
                    mSync.notifyAll();
                }
                Looper.loop();
                synchronized (mSync) {
                    mHandler = null;
                    mSync.notifyAll();
                }
            }
        }
    }
}
