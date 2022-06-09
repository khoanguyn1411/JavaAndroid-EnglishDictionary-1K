package com.duanthivien1k.tudientienganh;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.SearchRecentSuggestions;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import com.duanthivien1k.adapter.ViewPageAdapter;
import com.duanthivien1k.model.TuCuaBan;
import com.duanthivien1k.tudientienganh.databinding.ActivityDetailBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

public class DetailActivity extends AppCompatActivity {
    private  MaterialSearchView materialSearchView;
    ActivityDetailBinding binding;
    TextView textView;
    private TabLayout tabLayout;
    String word2, answer2, noanswer2;
    int tim2,id2;
    private ViewPager viewPager;
    TextToSpeech textToSpeech;
    Integer key = new Random().nextInt();
    //region của phần thêm từ vào danh sách từ của bạn

    DatabaseHelper mydb=new DatabaseHelper(this);
    ArrayList<String> hisList=new ArrayList<>();
    ArrayList<String> newList=new ArrayList<>();
    //endregion

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ConstraintLayout constraintLayout = binding.layout;
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
        viewPager = binding.viewPageDetail;
        tabLayout = binding.tabDetail;
        final String word=getIntent().getStringExtra("word");
        final String answer=getIntent().getStringExtra("detail");
        final String noanswer=getIntent().getStringExtra("nodetail");
        final int tim = getIntent().getIntExtra("favorite",1);
       // final int id=getIntent().getIntExtra("id",mydb.getId(word));
        int idWord=mydb.getId(word);
        id2=getIntent().getIntExtra("id",idWord);
        word2 = word; answer2 = answer; noanswer2 = noanswer; tim2=tim;
        setupViewPager();
        tabLayout.setupWithViewPager(viewPager);
        //region Toolbar

        Toolbar toolbar = findViewById(R.id.toolbar_detail);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));
        //endregion
        addControls(idWord);

    }

    private void addControls(final int idWord) {
        materialSearchView = findViewById(R.id.searchView);
        materialSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String word) {
                    getMeaning(word);
                Intent intent  = getIntent();
                if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
                    String query = intent.getStringExtra(SearchManager.QUERY);
                    SearchRecentSuggestions suggestions = new SearchRecentSuggestions(DetailActivity.this,
                            MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
                    suggestions.saveRecentQuery(query, null);
                }
                  //  mydb.saveHistoryWord(idWord);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                suggestWord(newText);
                return false;
            }
        });
        materialSearchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String word= (String) parent.getItemAtPosition(position);
                    getMeaning(word);
                   // mydb.saveHistoryWord(idWord);
                }catch (IndexOutOfBoundsException e){
                    Log.d("Exception", "duDoanNoiDungNhap: "+e.toString());
                }

            }
        });
        materialSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {

            }
        });

    }

    private void getMeaning(String word) {
        String answer=mydb.getMeaning(word);
        String noanswer="WORD NOT FOUND\nPLEASE SEARCH AGAIN";
        int tim = mydb.isTim(word);
        Intent intent=new Intent(DetailActivity.this,DetailActivity.class);
        try {
            if(answer==null){
                intent.putExtra("word",word);
                intent.putExtra("nodetail",noanswer);
              //  binding.searchView.setText("");
                startActivity(intent);
            }else {
                intent.putExtra("word",word);
                intent.putExtra("detail",answer);
              //  binding.searchView.setText("");
                intent.putExtra("favorite",tim);
                startActivity(intent);
            }
        }catch (Exception e){
        }
    }

    private void suggestWord(String word) {
        if(word.length()>0) {
            newList.clear();
            newList.addAll(mydb.getWord(word));
            binding.searchView.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, newList));
        }

    }

    private void showHisList() {
       hisList.clear();
        hisList.addAll(mydb.getHistoryWord());
        binding.searchView.setAdapter(new ArrayAdapter<String>(DetailActivity.this,android.R.layout.simple_list_item_1,hisList));
    }

    private void setupViewPager() {
        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new AnhVietFragment(), "Anh Việt");
        adapter.addFragment(new GhiChuFragment(), "Ghi Chú");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search,menu);
        MenuItem menuItem = menu.findItem(R.id.searchMenu);
        materialSearchView.setMenuItem(menuItem);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.searchMenu:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (materialSearchView.isSearchOpen()){
            materialSearchView.closeSearch();
        }
        else {
            super.onBackPressed();
        }

    }

    public String getWord2() {
        return word2;
    }
    public String getAnswer2() {
        return answer2;
    }
    public String getNoanswer2() {
        return noanswer2;
    }
    public int getIsTim(){return tim2;}
    public int getId(){return id2;}

}
