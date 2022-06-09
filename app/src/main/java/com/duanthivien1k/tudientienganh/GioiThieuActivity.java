package com.duanthivien1k.tudientienganh;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;

import com.duanthivien1k.tudientienganh.databinding.ActivityGioiThieuBinding;

public class GioiThieuActivity extends AppCompatActivity {

    ActivityGioiThieuBinding binding;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGioiThieuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ConstraintLayout constraintLayout = binding.layout;
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }
}
