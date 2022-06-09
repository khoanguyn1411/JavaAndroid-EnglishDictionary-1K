package com.duanthivien1k.tudientienganh;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class   DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_PATH_SUFFIX = "/databases/";

    public static final String DATABASE_NAME = "DictionaryEnglish.sqlite";
    private final Context context;
    public static SQLiteDatabase database = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);

        this.context = context;
    }

    private void CopyDataBaseFromAsset() {
        try {
            InputStream myInput = context.getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File file = new File(context.getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!file.exists()) {
                // tạo mới thư mục tên DATABASE
                file.mkdir();
            }
            OutputStream outputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.flush();
            outputStream.close();
            myInput.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private String getDatabasePath() {
        return context.getApplicationInfo().dataDir + DB_PATH_SUFFIX + DATABASE_NAME;
    }

    // Mở Database
    public SQLiteDatabase openDataBase() throws SQLException {
        File dbFile = context.getDatabasePath(DATABASE_NAME);
        if (!dbFile.exists()) {
            CopyDataBaseFromAsset();
            Toast.makeText(context, "Copying sucess from Assets folder", Toast.LENGTH_LONG).show();
        }
        return SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }

    // Đóng database
    public void closeDatabase() {
        if (database != null) {
            database.close();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    //truy vấn xuống data để lấy từ gợi ý
    public ArrayList<String> getWord(String word) {
        ArrayList<String> wordlist = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select word from tbl_edict where word like ? limit 40",new String[]{word+"%"});
        int index = cursor.getColumnIndex("word");
        while (cursor.moveToNext()) {
            wordlist.add(cursor.getString(index));
        }
        database.close();
        cursor.close();
        return wordlist;
    }

    public int isTim(String word) {
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from tbl_edict where word like ?", new String[]{word});
        int ischeck=0;
        while (cursor.moveToNext()) {
            ischeck = cursor.getInt(cursor.getColumnIndex("favorite"));
        }
        return ischeck;
    }

    //truy vấn lấy nghĩa của word từ database
    public String getMeaning(String word) {
        database = this.getReadableDatabase();
        String answer = null;
        Cursor cursor = database.rawQuery("select * from tbl_edict where word like ?", new String[]{word});
        while (cursor.moveToNext()) {
            answer = cursor.getString(cursor.getColumnIndex("detail"));
        }
        return answer;
    }

    // lấy Id của từ khi search
    public int getId(String word) {
        database = this.getReadableDatabase();
        int id = 0;
        Cursor cursor = database.rawQuery("select * from tbl_edict where word like ?", new String[]{word});
        while (cursor.moveToNext()) {
            id = cursor.getInt(cursor.getColumnIndex("idx"));
        }
        return id;
    }

    // update cột Yêu Thích thành 1
    public int addFavoriteWord(int id) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("favorite", 1);
        return database.update("tbl_edict", values, "idx=?", new String[]{String.valueOf(id)});
    }

    // update cột Yêu Thích thành 0
    public int removeFavoriteWord(int id) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("favorite", 0);
        return database.update("tbl_edict", values, "idx=?", new String[]{String.valueOf(id)});
    }

    // Lấy toàn bộ từ Yêu Thích
    public ArrayList<String> getFavoriteWord() {
        ArrayList<String> listFav = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select word from tbl_edict where favorite =1 ", null);
        int index = cursor.getColumnIndex("word");
        while (cursor.moveToNext()) {
            listFav.add(cursor.getString(index));
        }
        database.close();
        cursor.close();
        return listFav;
    }
    public void saveNote(int id, String note){
        database=this.getReadableDatabase();
        ContentValues values=new ContentValues();
        values.put("note",note);
          database.update("tbl_edict",values,"idx=?",new String[]{String.valueOf(id)});
    }
    public String getNote(int id){
        database=this.getReadableDatabase();
        String note=null;
        Cursor cursor=database.rawQuery("select note from tbl_edict where idx='"+id+"'",null);
        while (cursor.moveToNext()){
            note=cursor.getString(cursor.getColumnIndex("note"));
        }
        return note;
    }
    public void saveHistoryWord(int id) {
        database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("history", "history");
         database.update("tbl_edict", values, "idx=?", new String[]{String.valueOf(id)});
    }
    public ArrayList<String> getHistoryWord() {
        database = getReadableDatabase();
        ArrayList<String> listHis = new ArrayList<>();
        database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select word from tbl_edict where history ='history' ", null);
        int index = cursor.getColumnIndex("word");
        while (cursor.moveToNext()) {
            listHis.add(cursor.getString(index));
        }
        return listHis;
    }

}
