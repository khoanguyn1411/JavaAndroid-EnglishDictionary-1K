package com.duanthivien1k.tudientienganh;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.duanthivien1k.tudientienganh.databinding.FragmentAnhVietBinding;
import com.duanthivien1k.tudientienganh.databinding.FragmentGhiChuBinding;

import java.security.cert.Extension;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link GhiChuFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GhiChuFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    DatabaseHelper mydb;
    Context context;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public GhiChuFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment GhiChuFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GhiChuFragment newInstance(String param1, String param2) {
        GhiChuFragment fragment = new GhiChuFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentGhiChuBinding binding;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGhiChuBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        context=getActivity();
        mydb = new DatabaseHelper(context);
        DetailActivity detailActivity = (DetailActivity) getActivity();
        int id=detailActivity.getId();
        addcontrol(id);
        return binding.getRoot();
    }

    private void addcontrol(final int id) {
        String note=mydb.getNote(id);
        binding.edtNote.setText(note);
        binding.edtNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                saveNoteToDatabase(id,s);
                Log.d("TAG", "onTextChanged: "+id);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void saveNoteToDatabase(int id, CharSequence s) {
        mydb.saveNote(id,s.toString());
    }
}
