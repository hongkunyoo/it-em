package com.pinthecloud.item.util;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES10;

public class WindowUtil {

	public static int getMaxTextureSize(){
		int[] maxTextureSize = new int[1];
		GLES10.glGetIntegerv(GL10.GL_MAX_TEXTURE_SIZE, maxTextureSize, 0);
		return maxTextureSize[0];
	}
}
