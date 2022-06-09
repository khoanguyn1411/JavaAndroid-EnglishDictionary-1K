package com.duanthivien1k.tudientienganh;

import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.duanthivien1k.adapter.TuCuaBanAdapter;
import com.duanthivien1k.model.TuCuaBan;
import com.duanthivien1k.tudientienganh.databinding.ActivityTuCuaBanBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TuCuaBanActivity extends AppCompatActivity {
    ActivityTuCuaBanBinding binding;
    RecyclerView rvTuCuaBan;
    ArrayList<String> tuCuaBanArrayList;
    TuCuaBanAdapter tuCuaBanAdapter;
    DatabaseHelper mydb=new DatabaseHelper(this);
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTuCuaBanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
       rvTuCuaBan = findViewById(R.id.rvTuCuaBan);
        rvTuCuaBan.setLayoutManager(new LinearLayoutManager(this));
        tuCuaBanArrayList=(mydb.getFavoriteWord());
         tuCuaBanAdapter=new TuCuaBanAdapter(TuCuaBanActivity.this,tuCuaBanArrayList);
        rvTuCuaBan.setAdapter(tuCuaBanAdapter);
        ConstraintLayout constraintLayout = binding.layout;
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }


}
