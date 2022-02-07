package com.drudotstech.teststickers;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class StickersActivity extends AppCompatActivity {

    private static final int RC_PERMISSIONS = 101;
    private final Context context = StickersActivity.this;
    private MyCanvas canvas;
    private RelativeLayout rlMain;
    private ImageView ivResult;


    private int screenWidth;
    private int screenHeight;
    private float centerX, centerY;
    private Bitmap background;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stickers);
        rlMain = findViewById(R.id.rl_main);
        ivResult = findViewById(R.id.iv_result);

        final int size = (int) CanvasUtils.toPx(context, 100f);
        Bitmap sticker1 = BitmapFactory.decodeResource(getResources(), R.drawable.love_1);
        sticker1 = CanvasUtils.getResizedBitmap(sticker1, size, size);
        Bitmap sticker2 = BitmapFactory.decodeResource(getResources(), R.drawable.love_2);
        sticker2 = CanvasUtils.getResizedBitmap(sticker2, size, size);

        screenWidth = CanvasUtils.getScreenWidth(context);
        screenHeight = CanvasUtils.getScreenHeight(context);
        centerX = (float) (screenWidth / 2.0);
        centerY = (float) (screenHeight / 2.0);

        int width = sticker1.getWidth();
        int height = sticker1.getHeight();

        int left = (int) (centerX - (width / 2));
        int top = (int) (centerY - (height / 2));

        Rect rect = new Rect(left - size, top - size, width, height);
        MyBitmap b1 = new MyBitmap(context, sticker1);
        MyBitmap b2 = new MyBitmap(context, sticker2);


        canvas = new MyCanvas(this);

        background = BitmapFactory.decodeResource(getResources(), R.drawable.lake);
        background = CanvasUtils.getResizedBitmap(context, background);
        canvas.addBackgroundBitmap(background);

        canvas.addBitmapView(b1);
//        canvas.addBitmapView(b2);
        rlMain.addView(canvas);


        findViewById(R.id.btn_save).setOnClickListener(view -> {
            checkStoragePermission();
        });
    }


    private void checkStoragePermission() {
        // check permission
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // request permission
            ActivityCompat.requestPermissions(StickersActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}
                    , RC_PERMISSIONS);
        } else {
            saveBitmap();
        }
    }

    private void saveBitmap() {
        final Bitmap finalBitmap = canvas.getFinalBitmap();
        FileUtils.insertImage(context, getContentResolver(), finalBitmap, "Sticker", "Sticker");
        Toast.makeText(context, "Image is Saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS
                && grantResults.length >= 2
                && grantResults[0] == PackageManager.PERMISSION_GRANTED
                && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
            saveBitmap();
        } else {
            Toast.makeText(context, "Permission is denied!", Toast.LENGTH_SHORT).show();
        }
    }

}