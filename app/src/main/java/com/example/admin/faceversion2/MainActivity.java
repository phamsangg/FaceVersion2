package com.example.admin.faceversion2;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

public class MainActivity extends AppCompatActivity {
    Button btDetection;
    ImageView imageFace;
    ImageButton imbCamera;
    int REQUEST_CODE = 1234;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK){

            Bitmap defaultBitmap = (Bitmap) data.getExtras().get("data");
            imageFace.setImageBitmap(defaultBitmap);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Init();

        BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
        bitmapOption.inMutable = true;

        imbCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);









                startActivityForResult(intent,REQUEST_CODE);


            }

        });







        btDetection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //tạo một đối tượng BitmapFactory.Options mới và thiết lập inmutable thành true.
                // Điều này đảm bảo rằng bitmap có thể thay đổi để chúng tôi có thể
                // để áp dụng các hiệu ứng theo chương trình cho nó

                BitmapFactory.Options bitmapOption = new BitmapFactory.Options();
                bitmapOption.inMutable = true;

                //Bitmao option????????????????????


                Bitmap defaultBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cogai, bitmapOption);


                //
                //
                //
                Paint rectPain = new Paint();
                rectPain.setStrokeWidth(3);
                rectPain.setStyle(Paint.Style.STROKE);
                rectPain.setColor(Color.WHITE);
                // canvas to display our bitmap
                Bitmap temporaryBitmap= Bitmap.createBitmap(defaultBitmap.getWidth(),defaultBitmap.getHeight()
                        ,Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(temporaryBitmap);
                canvas.drawBitmap(defaultBitmap,0,0,null);
                // get to point where we use the FaceDectectory API
                FaceDetector faceDetector = new FaceDetector.Builder(MainActivity.this)
                        .setTrackingEnabled(false)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setMode(FaceDetector.FAST_MODE)
                        .build();
                //Check hoat dong
                if (!faceDetector.isOperational()){
                    Toast.makeText(MainActivity.this, "Khong the nhan dien guong mat", Toast.LENGTH_SHORT).show();
                    return;
                }
                //create a frame using the defauit bitmap and call on the face detector to get the face OBJ
                Frame frame = new Frame.Builder().setBitmap(defaultBitmap).build();
                SparseArray<Face> sparseArray= faceDetector.detect(frame);

                //draw rectangle over the faces
                for (int i=0;i < sparseArray.size();i++){
                    Face face = sparseArray.get(i);
                    float left = face.getPosition().x;
                    float top  = face.getPosition().y;
                    float right = face.getWidth() + left;
                    float bottom = face.getHeight() + top;
                    float cornerRadius = 2.0f;

                    RectF rectF = new RectF(left,top,right,bottom);
                    canvas.drawRoundRect(rectF,cornerRadius,cornerRadius,rectPain);

                    for (Landmark landmark : face.getLandmarks()){
                        int x = (int) (landmark.getPosition().x);
                        int y = (int) (landmark.getPosition().y);
                        float radius = 10.0f;
                        canvas.drawCircle(x,y,radius,rectPain);
                    }
                }
                //set Imageview from our layout after which we release the face detector
                imageFace.setImageDrawable(new BitmapDrawable(getResources(),temporaryBitmap));
                faceDetector.release();

            }
        });
    }

    private void Init() {
        btDetection= (Button) findViewById(R.id.detection);
        imageFace= (ImageView) findViewById(R.id.face);
        imbCamera= (ImageButton) findViewById(R.id.imbCamera);
    }

}
