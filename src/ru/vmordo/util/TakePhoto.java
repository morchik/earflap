package ru.vmordo.util;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import kz.alfa.util.Cnt;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class TakePhoto {
	public static Camera cam;
	private static SurfaceView sv;

	public static void getOne(SurfaceView pSV) {
		SurfaceHolder holder;
		HolderCallback holderCallback;

		if (cam != null)
			cam.release();
		Log.e("FlashLiteActivity", "onClickHide start");
		cam = Camera.open();
		if (pSV != null)
			sv = pSV;
		if (sv == null)
			sv = new SurfaceView(Cnt.get());
		//(new FlashLiteActivity()).startActivity(new Intent(Cnt.get(), FlashLiteActivity.class));
		//sv = (SurfaceView) findViewById(R.id.surfaceView);
		holder = sv.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		holderCallback = new HolderCallback();
		holder.addCallback(holderCallback);
		try {
			cam.setPreviewDisplay(holder);
		} catch (Exception e) {
			//Toast.makeText(Cnt.get(),"setPreviewDisplay error " + e.getMessage(),Toast.LENGTH_LONG).show();
			e.printStackTrace();
			Log.e("FlashLiteActivity", "error  cam.setPreviewDisplay(holder); "
					+ e.getMessage());
		}
		setPicMax();
		cam.startPreview();
		Log.e("FlashLiteActivity", "onClickHide takePicture ");
		try {
			cam.takePicture(null, null, new SilentPictureCallback());
		} catch (Exception e) {
			//Toast.makeText(Cnt.get(), "takePicture error " + e.getMessage(),Toast.LENGTH_LONG).show();
			e.printStackTrace();
			Log.e("FlashLiteActivity",
					"error  cam.takePicture(null, null, this); "
							+ e.getMessage());
		}
	}

	public static void switchFlash() {
		if (Cnt.get().getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA_FLASH)) {
			if (cam == null) {
				Toast.makeText(Cnt.get(), "start", Toast.LENGTH_SHORT).show();
				cam = Camera.open();
				Parameters p = cam.getParameters();
				p.setFlashMode(Parameters.FLASH_MODE_TORCH);
				cam.setParameters(p);
				cam.startPreview();
			} else {
				Toast.makeText(Cnt.get(), "stop", Toast.LENGTH_SHORT).show();
				cam.stopPreview();
				cam.release();
				cam = null;
			}

		} else
			Toast.makeText(Cnt.get(), "no flash", Toast.LENGTH_SHORT).show();
	}
	public static void setPicMax() {
		Camera.Parameters param;
		param = cam.getParameters();

		Camera.Size bestSize = null;
		List<Camera.Size> sizeList = cam.getParameters()
				.getSupportedPictureSizes();
		bestSize = sizeList.get(0);
		for (int i = 1; i < sizeList.size(); i++) {
			if ((sizeList.get(i).width * sizeList.get(i).height) > (bestSize.width * bestSize.height)) {
				bestSize = sizeList.get(i);
			}
		}
		param.setPictureSize(bestSize.width, bestSize.height);
		cam.setParameters(param);
	}

	void someTask(long everySec) { 
		Timer myTimer = new Timer(); // Создаем таймер
		Log.e("someTask", " someTask ");
		myTimer.schedule(new TimerTask() { // Определяем задачу
					@Override
					public void run() {
						TakePhoto.getOne(sv);
					}
				}, everySec * 1000L, everySec * 1000L); // интервал
	}
}
