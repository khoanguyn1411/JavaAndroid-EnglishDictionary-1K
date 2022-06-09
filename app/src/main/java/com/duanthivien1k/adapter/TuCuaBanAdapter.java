package com.duanthivien1k.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.duanthivien1k.model.TuCuaBan;
import com.duanthivien1k.tudientienganh.AnhVietFragment;
import com.duanthivien1k.tudientienganh.DatabaseHelper;
import com.duanthivien1k.tudientienganh.DetailActivity;
import com.duanthivien1k.tudientienganh.MainActivity;
import com.duanthivien1k.tudientienganh.R;
import com.duanthivien1k.tudientienganh.TuCuaBanActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TuCuaBanAdapter extends RecyclerView.Adapter<TuCuaBanAdapter.TuCuaBanViewHolder> {
    Activity context;
    DatabaseReference reference;
    ArrayList<String> tuCuaBanArrayList;
    ArrayList<String> list;
    private LayoutInflater mLayoutInflater;
    DatabaseHelper myDatabaseHelper;


    public TuCuaBanAdapter (Activity contex, ArrayList<String> p ){
        this.context =  contex;
        myDatabaseHelper=new DatabaseHelper(context);
        tuCuaBanArrayList  =p;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public class TuCuaBanViewHolder extends RecyclerView.ViewHolder{
        public TextView tucuaban;
        public Button buttonViewOption;

        public TuCuaBanViewHolder(View view){
            super(view);
            tucuaban = view.findViewById(R.id.tucuaban);
            buttonViewOption = view.findViewById(R.id.buttonViewOption);

        }
    }
    @NonNull
    @Override
    public TuCuaBanViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.tucuaban_list_item,parent,false);
        return new TuCuaBanViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final TuCuaBanAdapter.TuCuaBanViewHolder holder, final int position) {
        final String tuCuaBan = tuCuaBanArrayList.get(position);
        holder.tucuaban.setText(tuCuaBan);
        tuCuaBanArrayList=myDatabaseHelper.getFavoriteWord();
        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             //   loaddata();
                final PopupMenu popupMenu = new PopupMenu(context, holder.buttonViewOption);
                popupMenu.inflate(R.menu.option_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.mnuDelete:
                                // lấy Id của từ cần xóa
                                int id=myDatabaseHelper.getId(tuCuaBan);
                                // update từ yêu thích từ 1 thành 0
                                int kq=myDatabaseHelper.removeFavoriteWord(id);
                                // Gọi hàm xóa
                                removeAt(position);
                                break;
                            case R.id.mnuCheck:
                                String answer=myDatabaseHelper.getMeaning(tuCuaBan);
                                int tim=myDatabaseHelper.isTim(tuCuaBan);
                             try {
                                        Intent intent=new Intent(context, DetailActivity.class);
                                        intent.putExtra("word",tuCuaBan);
                                        intent.putExtra("detail",answer);
                                        intent.putExtra("isTim",tim);
                                        context.startActivity(intent);

                                }catch (Exception e){
                                }
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();

            }
        });
    }
    // Hàm xóa vị trí từ muốn xóa
    private void removeAt(int position) {
        tuCuaBanArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, tuCuaBanArrayList.size());
    }

    @Override
    public int getItemCount() {
        return tuCuaBanArrayList.size();
    }


}
