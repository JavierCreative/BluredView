package com.creative.blured;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.creative.bluredview.BitmapUtils;
import com.creative.bluredview.BluredView;
import com.google.android.material.button.MaterialButton;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.first_button)
    MaterialButton mFirstButton;
    @BindView(R.id.blured_bluredbiew)
    BluredView mBluredBluredbiew;
    @BindView(R.id.main_view)
    ConstraintLayout mainView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.first_button)
    public void onViewClicked()
    {
        Bitmap bitmap = BitmapUtils.getBitmapFromView(mainView);
        mBluredBluredbiew.applyBlurToView(bitmap,20,3);
    }
}
