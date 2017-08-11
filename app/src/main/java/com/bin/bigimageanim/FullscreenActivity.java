package com.bin.bigimageanim;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {

    TranBitImageView mImageView;
    Button mBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_fullscreen);
        mImageView = (TranBitImageView) findViewById(R.id.iv_bg);
        mBtn = (Button) findViewById(R.id.btn);

        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImageView.startAnim();
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        mImageView.pauseAnim();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mImageView.resumeAnim();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mImageView.stopAnim();
    }
}
