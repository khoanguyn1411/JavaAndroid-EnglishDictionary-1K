package com.duanthivien1k.tudientienganh;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.duanthivien1k.tudientienganh.databinding.ActivityMainBinding;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.navigation.NavigationView;

import java.nio.channels.spi.AbstractInterruptibleChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    ActivityMainBinding binding;
    DatabaseHelper myDatabaseHelper = new DatabaseHelper(this);
    ArrayList<String>newList = new ArrayList<>();
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        myDatabaseHelper.openDataBase();
        addControls();
    }
    private void addControls() {
        //region xử lý nút tìm trên keyboard
        binding.edtNhapTu.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId){
                    case EditorInfo.IME_ACTION_SEARCH:
                        String word=binding.edtNhapTu.getText().toString();
                        try {
                            getMeaning(word);
                            int idWord=myDatabaseHelper.getId(word);
                            Log.d("IDIDIDIDIDID", "onEditorAction: "+idWord);
                        }catch (Exception e){
                        }
                }
                return false;
            }
        });
        //endregion
        //region Xử lý nhập từ bàn phím
        if (binding.edtNhapTu.callOnClick()){
            timWordTuBanPhim();
        }
        //endregion
        //region Xử lý nhập từ giọng nói
        binding.btnGiongNoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { xuLyGiongNoiGoogleAI();
            }
        });
        //endregion
        //region Xử lý Sidebar
        drawerLayout= findViewById(R.id.drawer_layout);
        navigationView= findViewById(R.id.nav_view);
        toolbar = binding.toolbar;
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        navigationView.bringToFront();
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        //endregion
        //region Xử lý mở giao diện chèn hình
        binding.btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChenHinhActivity.class);
                startActivityForResult(intent,0);
            }
        });
        //endregion
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
    }
        else
        {
                super.onBackPressed();
    }
    }

    private void timWordTuBanPhim() {

        //region Dự đoán nội dung nhập cùng lúc nhập
        binding.edtNhapTu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
               @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                suggestWord(s);
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        //endregion
        //region Lấy từ user đã chọn và dịch
        binding.edtNhapTu.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String word= (String) parent.getItemAtPosition(0);
                    getMeaning(word);
                }catch (IndexOutOfBoundsException e){
                    Log.d("Exception", "duDoanNoiDungNhap: "+e.toString());
                }
            }
        });
        //endregion

    }



    private void xuLyGiongNoiGoogleAI() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please say something:");
        try {
            startActivityForResult(intent, 113);
        }
        catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "Điện thoại bạn không hỗ trợ Google AI Recognization",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==113 && resultCode==RESULT_OK && data!=null)
        {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            String lenhNgat = result.get(0).toLowerCase();
            String lenhNgat1 = lenhNgat.replaceAll(" ","");
            if(lenhNgat1.contains("closethisapp"))
                finish();
            else
            {
                binding.edtNhapTu.setText(result.get(0));
                getMeaning(result.get(0));
            }
        }
    }

    private void suggestWord(CharSequence s) {
        //còn bug ko gõ dc dấu '
           if(s.length()>0) {
               newList.clear();
               newList.addAll(myDatabaseHelper.getWord(s.toString()));
               binding.edtNhapTu.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newList));
           }
    }
    //region Dịch nghĩa từ user nhập
    public void getMeaning(String word){
        String answer=myDatabaseHelper.getMeaning(word);
        String noanswer="WORD NOT FOUND\nPLEASE SEARCH AGAIN";
        int tim = myDatabaseHelper.isTim(word);
        Intent intent=new Intent(MainActivity.this,DetailActivity.class);
        try {
            if(answer==null){
                intent.putExtra("word",word);
                intent.putExtra("nodetail",noanswer);
                binding.edtNhapTu.setText("");
                startActivity(intent);
            }else {
                intent.putExtra("word",word);
                intent.putExtra("detail",answer);
                binding.edtNhapTu.setText("");
                intent.putExtra("favorite",tim);
                startActivity(intent);
            }
        }catch (Exception e){
        }

    }
    //endregion
    @Override
    //region chuyển hướng intent khi chọn item trong sidebar
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case  R.id.nav_tucuaban:{
                Intent intent = new Intent(this, TuCuaBanActivity.class);
                startActivity(intent);
                break;
            }
            case  R.id.nav_caidat:{
                break;
            }
            case  R.id.nav_gioithieu:{
                break;
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
    //endregion


}
