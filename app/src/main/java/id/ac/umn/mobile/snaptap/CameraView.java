package id.ac.umn.mobile.snaptap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by Rico on 04/12/2016.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "Camera";
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    int cameraId = 0;
    Paint paint;
    int left = 0;
    int top = 0;
    int right = 0;
    int bottom = 0;
    int width;
    int height;
    boolean faceInRect = false;



    private Camera.FaceDetectionListener faceDetectionListener = new Camera.FaceDetectionListener() {
        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            Rect r = new Rect( 100, 100, 400, 400);
            if (faces.length > 0){
                /*Log.d("FaceDetection", "face detected: "+ faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        "Y: " + faces[0].rect.centerY() ); */


                float batasKiri = (float) 2000 / width * left - 1000;
                float batasAtas =  (float) 2000 / height * top - 1000;
                float batasKanan =  (float) 2000 / width * right - 1000;
                float batasBawah = (float) 2000 / height * bottom - 1000;

                if(faces[0].rect.left > batasKiri && faces[0].rect.top > batasAtas
                        && faces[0].rect.right < batasKanan && faces[0].rect.bottom < batasBawah) {
                    faceInRect = true;
                }
                else {
                    faceInRect = false;
                }

                //face = faces[0];
                //test = faces[0].rect;
            }
        }
    };

    public CameraView(Context context, Camera camera) {
        super(context);
        setWillNotDraw(false);
        this.camera = camera;
        camera.setFaceDetectionListener(faceDetectionListener);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.release();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            left =  Math.round(getLeft()+(getRight()-getLeft())/5);
            top =  Math.round(getTop()+(getBottom()-getTop())/4);
            right =  Math.round(getRight()-(getRight()-getLeft())/5);
            bottom =  Math.round(getBottom()-(getBottom()-getTop())/4);

            width = getWidth();
            height = getHeight();

            Camera.Parameters params = camera.getParameters();
            params.set("orientation", "portrait");
            camera.setDisplayOrientation(90);
            params.setRotation(90);
            camera.setParameters(params);

            camera.setPreviewDisplay(holder);

            camera.startPreview();

            startFaceDetection(); // start face detection feature

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (surfaceHolder.getSurface() == null){
            // preview surface does not exist
            Log.d(TAG, "mHolder.getSurface() == null");
            return;
        }

        try {
            camera.stopPreview();

        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error stopping camera preview: " + e.getMessage());
        }

        try {
            Camera.Parameters params = camera.getParameters();
            //params.set("orientation", "portrait");
            //camera.setDisplayOrientation(90);
            //params.setRotation(90);


            camera.setPreviewDisplay(surfaceHolder);
            camera.startPreview();

            startFaceDetection(); // re-start face detection feature

        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void startFaceDetection(){
        // Try starting Face Detection
        Camera.Parameters params = camera.getParameters();

        // start face detection only *after* preview has started
        if (params.getMaxNumDetectedFaces() > 0){
            // camera supports face detection, so can start it:
            camera.startFaceDetection();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        double scale = 1;
        drawFaceBox(canvas, scale);
        invalidate();
    }

    private void drawFaceBox(Canvas canvas, double scale) {
        //paint should be defined as a member variable rather than
        //being created on each onDraw request, but left here for
        //emphasis.
        paint = new Paint();

        if(faceInRect)
            paint.setColor(Color.GREEN);
        else
            paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);

        //left = (float) ( face.getPosition().x * scale );
        //top = (float) ( face.getPosition().y * scale );
        //right = (float) scale * ( face.getPosition().x + face.getWidth() );
        //bottom = (float) scale * ( face.getPosition().y + face.getHeight() );

        //canvas.drawRect( left, top, right, bottom, paint );

        canvas.drawRect(left, top, right, bottom, paint);

        /* if (test != null)
            canvas.drawRect(Math.abs(test.left), Math.abs(test.top), Math.abs(test.right), Math.abs(test.bottom), paint); */
    }
}
