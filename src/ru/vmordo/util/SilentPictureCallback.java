package ru.vmordo.util;

import java.io.File;
import java.io.FileOutputStream;

import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

  // @SuppressWarnings("deprecation")
public class SilentPictureCallback implements Camera.PictureCallback {

	File directory2;

	@Override
	public void onPictureTaken(byte[] paramArrayOfByte, Camera paramCamera) {
		Log.e("onPictureTaken", "start");
		createDirectory2();
		try {
			String fln = String.format(directory2.getPath() + "/h%d.jpg",
					System.currentTimeMillis());
			FileOutputStream os = new FileOutputStream(fln);
			os.write(paramArrayOfByte);
			os.close();
			Log.e("onPictureTaken", paramArrayOfByte.length + " saved in " + fln);
			//Toast.makeText(Cnt.get(), paramArrayOfByte.length + " hide " + fln,	Toast.LENGTH_LONG).show();
		} catch (Exception e) {
			Log.e("onPictureTaken", e.getMessage());
			//Toast.makeText(Cnt.get(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
		}
		//paramCamera.startPreview();
	}


	private void createDirectory2() {
		directory2 = new File(
				Environment
						.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"HideFolder");
		if (!directory2.exists())
			directory2.mkdirs();
	}


}
