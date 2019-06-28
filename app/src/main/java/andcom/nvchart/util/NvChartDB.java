package andcom.nvchart.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class NvChartDB extends SQLiteOpenHelper {
    private Context context;
    public NvChartDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        // String 보다 StringBuffer가 Query 만들기 편하다.
        StringBuffer sb = new StringBuffer();
        sb.append(" CREATE TABLE NVCHART ( ");
        sb.append(" DB_NO INTEGER NOT NULL, ");
        sb.append(" NODEKEY TEXT NOT NULL, ");
        sb.append(" NVDATA BLOB ,");
        sb.append(" PRIMARY KEY(DB_NO,NODEKEY)) ");

        // SQLite Database로 쿼리 실행
        db.execSQL(sb.toString());
        }
    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Toast.makeText(context, "DB의 버전이 올라갔습니다.", Toast.LENGTH_SHORT).show();
    }
    public void testDB() {
        SQLiteDatabase db = getReadableDatabase();
    }

}
