package com.duanthivien1k.tudientienganh;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.duanthivien1k.tudientienganh.databinding.FragmentAnhVietBinding;
import com.google.firebase.database.DatabaseReference;

import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AnhVietFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AnhVietFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DatabaseHelper mydb;
    Context context;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    TextToSpeech textToSpeech;
    public AnhVietFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AnhVietFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AnhVietFragment newInstance(String param1, String param2) {
        AnhVietFragment fragment = new AnhVietFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivity detailActivity = (DetailActivity) getActivity();
        int isTim = detailActivity.getIsTim();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            if (isTim==1){
                binding.imgTimDetailBoTim.setVisibility(View.VISIBLE);
                binding.imgTimDetail.setVisibility(View.GONE);
            }
            else {
                binding.imgTimDetail.setVisibility(View.VISIBLE);
                binding.imgTimDetailBoTim.setVisibility(View.GONE);
            }
        }
    }
    FragmentAnhVietBinding  binding;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAnhVietBinding.inflate(inflater, container, false);
        context = getActivity();
        mydb = new DatabaseHelper(context);
        DetailActivity detailActivity = (DetailActivity) getActivity();
        String word = detailActivity.getWord2();
        String answer = detailActivity.getAnswer2();
        String noaanswer = detailActivity.getNoanswer2();
        int isTim = detailActivity.getIsTim();
        int id=detailActivity.getId();
        addcontrol(word, answer, noaanswer,isTim,id);
        return binding.getRoot();

    }

    private void addcontrol(final String word, String answer, String noanswer, final int isTim, final int id) {
        binding.txtWord.setText(word);
        if(answer==null){
            binding.txtAnswer.setText(noanswer);
        }else {
            setStyleForMeaning(answer);
            binding.txtAnswer.setMovementMethod(new ScrollingMovementMethod());
        }

        // update từ yêu thích thành 1
        binding.imgTimDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int kq=mydb.addFavoriteWord(id);
                binding.imgTimDetailBoTim.setVisibility(View.VISIBLE);
                binding.imgTimDetail.setVisibility(View.GONE);
                Log.d("TAG", "timtimtimtim: "+isTim);
            }
        });
        // update từ yêu thích thành 0
        binding.imgTimDetailBoTim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int kq=mydb.removeFavoriteWord(id);
                binding.imgTimDetailBoTim.setVisibility(View.GONE);
                binding.imgTimDetail.setVisibility(View.VISIBLE);
            }
        });
        //kiểm tra từ yêu thích
        if(isTim==1){
            binding.imgTimDetailBoTim.setVisibility(View.VISIBLE);
            binding.imgTimDetail.setVisibility(View.GONE);
        }else {
            binding.imgTimDetailBoTim.setVisibility(View.GONE);
            binding.imgTimDetail.setVisibility(View.VISIBLE);
        }
        Text_To_Speech();
    }
    private void setStyleForMeaning(String answer) {
        answer = answer.substring(15,answer.length()-20);
        // Xử lý các lặp xuống hàng
        answer =answer.replaceAll("<br /><br />","<br />");
        //split theo dòng
        String []phanTich = answer.split("<br />");
        // tạo biến xài cho dễ ý mà
        String startheader="<h1"; String endheader="</h1>";
        String startpart ="<p"; String endpart="</p>";
        // đau khổ bắt đầu, phân tích mà thêm tiền tố hậu tố html :)
        for (int i=1;i<=phanTich.length-1;i++){
            // dùng 1 biến thế vì em quen làm thế =))
            String the=phanTich[i];
            // cắt đống khoảng trắng dư thừa
            the.trim();
            // lấy tiền tố của detail
            // *: loại từ
            // -: các định nghĩa
            // =: ví dụ cho nghĩa
            // !: cũng là ví dụ mà hong hiểu sao dùng tiền tố khác =))
            //
            String phanLoai = the.substring(0,1);
            switch (phanLoai)  {
                case "*":{
                    // xử lí loại từ
                    the = the.substring(1);
                    the = the.trim();
                    // tạo style
                    String style=" style = \"color:#114B5F\">";
                    //viết hoa chữ cái đầu tiên
                    the = Character.toUpperCase(the.charAt(0))+the.substring(1);
                    // tạo code html
                    the= startheader+style+the+endheader;
                    // gán vào mảng
                    phanTich[i]="";
                    phanTich[i]=phanTich[i].concat(the);
                    break;
                }
                case "-":{
                    the= the.substring(1);
                    String style=" style = \"font-family:nunito; color:#376879\">";
                    the = startpart+style+the+endpart;
                    phanTich[i]="";
                    phanTich[i]=phanTich[i].concat(the);
                    break;
                }
                case "=":{
                    the= the.substring(1);
                    String style=" style = \"font-family:nunito; color:#93989A\">";
                    the = startpart+style+the+endpart;
                    phanTich[i]="";
                    phanTich[i]=phanTich[i].concat(the);
                    break;
                }
                case "!":{
                    the = the.substring(1);
                    String style=" style = \"font-family:nunito; color:#93989A\">";
                    the = startpart+style+the+endpart;
                    phanTich[i]="";
                    phanTich[i]=phanTich[i].concat(the);
                    break;
                }
                case "@":{
                    String the2="lon";
                    phanTich[i]="";
                    the=startpart+">"+the2+endpart;
                    phanTich[i]=phanTich[i].concat(the);
                    break;
                }
            }
        }
        String xuat="";
        String thePhienAm = phanTich[0];
        thePhienAm.substring(0,thePhienAm.length()-1);
        String []arrPhienAm = thePhienAm.split("/");
        if(arrPhienAm.length>1)
            binding.txtPhatAm.setText("/"+arrPhienAm[1]+"/");
        for (int i=1;i<=phanTich.length-1;i++) {
            xuat = xuat + phanTich[i];
        }
        binding.txtAnswer.setText(Html.fromHtml(xuat)) ;
    }

    private void Text_To_Speech() {
        textToSpeech =new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.UK);
                }
            }
        });

     binding.imgPhienAm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // binding.txtWord.setText(binding.edtNhapTu.getText().toString());
                String toSpeak = binding.txtWord.getText().toString();
                textToSpeech.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    /*private void Speech_To_Text() {
        binding.btnGiongNoi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                xuLyGiongNoiGoogleAI();
            }
        });
    }*/
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
            Toast.makeText(context.getApplicationContext(),
                    "Điện thoại bạn không hỗ trợ Google AI Recognization",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
