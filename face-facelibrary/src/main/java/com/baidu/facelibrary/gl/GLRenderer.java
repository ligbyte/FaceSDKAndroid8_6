package com.baidu.facelibrary.gl;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.effect.Effect;
import android.media.effect.EffectContext;
import android.media.effect.EffectFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class GLRenderer implements GLSurfaceView.Renderer {

    private Bitmap photo;
    private static int photoWidth = 480;
    private static int photoHeight = 640;
    private int textures[] = new int[2];
    private Shape shape;
    private EffectContext effectContext;
    private Effect effect;
    public static int EFFECT_NUM = 0;

    public GLRenderer(Context context, int width, int height) {
        super();
        photo = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444);
        photoWidth = width;
        photoHeight = height;
    }

    public void setPhoto(Bitmap bitmap) {
        photo = bitmap;
    }

    private void refresh(GL10 gl) {
//        gl.glGenTextures(1, textures, 0);
        gl.glBindTexture(GL10.GL_TEXTURE_2D, textures[0]);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
                GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
                GL10.GL_LINEAR);

        // Use Android GLUtils to specify a two-dimensional texture image from
        // our bitmap
        if (photo != null && !photo.isRecycled()) {
            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, photo, 0);
            photo.recycle();
        } else {
            Log.e("huwwds", "============================== photo is null");
        }
    }

    private void generateSquare() {
        GLES20.glGenTextures(2, textures, 0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);

        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, photo, 0);
        shape = new Shape();
    }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glClearColor(0, 0, 0, 1);
        generateSquare();
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        if (effectContext == null) {
            effectContext = EffectContext.createWithCurrentGlContext();
        }
        refresh(gl10);
        drawEffect();
        shape.draw(textures[1]);
    }

    private void drawEffect() {
        if (this.effect == null) {
            EffectFactory factory = effectContext.getFactory();
            this.effect = factory.createEffect(EffectFactory.EFFECT_AUTOFIX);
        }
        this.effect.apply(textures[0], photoWidth, photoHeight, textures[1]);
    }
}
