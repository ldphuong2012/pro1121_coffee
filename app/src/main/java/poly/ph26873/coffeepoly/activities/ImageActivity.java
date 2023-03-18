package poly.ph26873.coffeepoly.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import poly.ph26873.coffeepoly.R;

public class ImageActivity extends AppCompatActivity {

    private ImageView imageView;
    private ScaleGestureDetector scaleGestureDetector;
    private Float FACTOR = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = findViewById(R.id.imv_avatar_zoom);
        Intent intent = getIntent();
        String image = intent.getStringExtra("image");
        if (image != null) {
            Glide.with(this).load(Uri.parse(image)).error(R.drawable.image_guest).into(imageView);
        }
        scaleGestureDetector = new ScaleGestureDetector(this, new Scale());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    class Scale extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(@NonNull ScaleGestureDetector detector) {
            FACTOR *= detector.getScaleFactor();
            FACTOR = Math.max(0.1f, Math.min(FACTOR, 10.f));
            imageView.setScaleX(FACTOR);
            imageView.setScaleY(FACTOR);
            return true;
        }
    }
}