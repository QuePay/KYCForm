package np.com.qpay.kycformapp.merchant_databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.security.interfaces.DSAKey;
import java.util.ArrayList;
import java.util.List;

import np.com.qpay.kycformapp.kyc.dto.AddressInfo;


/**
 * Created by dinesh on 5/15/16.
 */
public class QpayMerchantDatabase extends SQLiteOpenHelper {
    Cursor cursor;
    private static final String DATABASE_NAME = "qpay_merchant";
    private static final int DATABASE_VERSION = 2;
    private static final String TAG="dinesh";

    public static final String ZONE_TABLE_NAME = "zone";
    private static final String ZONE_PKey = "_id";
    private static final String ZONE_ID = "zone_id";
    private static final String ZONE_NAME = "zone_name";

    public static final String DISTRICT_TABLE_NAME = "district";
    private static final String DISTRICT_PKey = "_id";
    private static final String DISTRICT_ID = "district_id";
    private static final String DISTRICT_NAME = "district_name";

    public static final String VDC_TABLE_NAME = "vdc";
    private static final String VDC_PKey = "_id";
    private static final String VDC_ID = "vdc_id";
    private static final String VDC_NAME = "vdc_name";

    public static final String ALL_DISTRICT_TABLE_NAME = "all_district";
    private static final String ALL_DISTRICT_PKey = "_id";
    private static final String ALL_DISTRICT_ID = "all_district_id";
    private static final String ALL_DISTRICT_NAME = "all_district_name";

    private static final String CREATE_TABLE_ZONE = "CREATE TABLE " + ZONE_TABLE_NAME + " ( " +
            ZONE_PKey + " integer primary key autoincrement, " +
            ZONE_ID + " integer, " +
            ZONE_NAME + " varchar" +
            " )" ;

    private static final String CREATE_TABLE_DISTRICT = "CREATE TABLE " + DISTRICT_TABLE_NAME + " ( " +
            DISTRICT_PKey + " integer primary key autoincrement, " +
            DISTRICT_ID + " integer, " +
            DISTRICT_NAME + " varchar" +
            " )" ;
    private static final String CREATE_TABLE_VDC = "CREATE TABLE " + VDC_TABLE_NAME + " ( " +
            VDC_PKey + " integer primary key autoincrement, " +
            VDC_ID + " integer, " +
            VDC_NAME + " varchar" +
            " )" ;

    private static final String CREATE_TABLE_ALL_DISTRICT = "CREATE TABLE " + ALL_DISTRICT_TABLE_NAME + " ( " +
            ALL_DISTRICT_PKey + " integer primary key autoincrement, " +
            ALL_DISTRICT_ID + " integer, " +
            ALL_DISTRICT_NAME + " varchar" +
            " )" ;

    private String[] allZoneColumns = {ZONE_PKey, ZONE_ID, ZONE_NAME};
    private String[] allDistrictColumns = {DISTRICT_PKey, DISTRICT_ID, DISTRICT_NAME};
    private String[] allVDCColumns = {VDC_PKey, VDC_ID, VDC_NAME};
    private String[] allAllDistrictColumns = {ALL_DISTRICT_PKey, ALL_DISTRICT_ID, ALL_DISTRICT_NAME};


    public QpayMerchantDatabase(Context context) {
        super(context, DATABASE_NAME/*"/mnt/sdcard/qpay_merchant.db"*/, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void createZoneTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(CREATE_TABLE_ZONE);
    }

    public void populateZoneTable(AddressInfo addressInfo){
        ContentValues values = new ContentValues();
        values.put(ZONE_ID, addressInfo.getId());
        values.put(ZONE_NAME, addressInfo.getName());
        SQLiteDatabase db = this.getReadableDatabase();
        long insertId = db.insert(ZONE_TABLE_NAME, null,
                values);
    }

    public List<AddressInfo> selectAllZone(){
        List<AddressInfo> addressInfoArrayList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(ZONE_TABLE_NAME,
                allZoneColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AddressInfo addressInfo = new AddressInfo();
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(ZONE_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(ZONE_NAME)));
            addressInfoArrayList.add(addressInfo);
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfoArrayList;
    }

    public AddressInfo selectZoneById(int id){

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(ZONE_TABLE_NAME,
                allZoneColumns, ZONE_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        AddressInfo addressInfo = new AddressInfo();
        while (!cursor.isAfterLast()) {
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(ZONE_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(ZONE_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfo;
    }

    public void createDistrictTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(CREATE_TABLE_DISTRICT);
    }

    public void populateDistrictTable(AddressInfo addressInfo){
        ContentValues values = new ContentValues();
        values.put(DISTRICT_ID, addressInfo.getId());
        values.put(DISTRICT_NAME, addressInfo.getName());
        SQLiteDatabase db = this.getReadableDatabase();
        long insertId = db.insert(DISTRICT_TABLE_NAME, null,
                values);
    }

    public List<AddressInfo> selectAllDistrict(){
        List<AddressInfo> addressInfoArrayList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(DISTRICT_TABLE_NAME,
                allDistrictColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AddressInfo addressInfo = new AddressInfo();
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(DISTRICT_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(DISTRICT_NAME)));
            addressInfoArrayList.add(addressInfo);
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfoArrayList;
    }

    public AddressInfo selectDistrictById(int id){

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(DISTRICT_TABLE_NAME,
                allDistrictColumns, DISTRICT_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        AddressInfo addressInfo = new AddressInfo();
        while (!cursor.isAfterLast()) {
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(DISTRICT_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(DISTRICT_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfo;
    }

    public void createVDCTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(CREATE_TABLE_VDC);
    }

    public void populateVDCTable(AddressInfo addressInfo){
        ContentValues values = new ContentValues();
        values.put(VDC_ID, addressInfo.getId());
        values.put(VDC_NAME, addressInfo.getName());
        SQLiteDatabase db = this.getReadableDatabase();
        long insertId = db.insert(VDC_TABLE_NAME, null,
                values);
    }

    public List<AddressInfo> selectAllVDC(){
        List<AddressInfo> addressInfoArrayList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(VDC_TABLE_NAME,
                allVDCColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AddressInfo addressInfo = new AddressInfo();
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(VDC_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(VDC_NAME)));
            addressInfoArrayList.add(addressInfo);
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfoArrayList;
    }

    public AddressInfo selectVDCById(int id){

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(VDC_TABLE_NAME,
                allVDCColumns, VDC_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        AddressInfo addressInfo = new AddressInfo();
        while (!cursor.isAfterLast()) {
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(VDC_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(VDC_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfo;
    }

    public void createAllDistrictTable(){
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL(CREATE_TABLE_ALL_DISTRICT);
    }

    public void populateAllDistrictTable(AddressInfo addressInfo){
        ContentValues values = new ContentValues();
        values.put(ALL_DISTRICT_ID, addressInfo.getId());
        values.put(ALL_DISTRICT_NAME, addressInfo.getName());
        SQLiteDatabase db = this.getReadableDatabase();
        long insertId = db.insert(ALL_DISTRICT_TABLE_NAME, null,
                values);
    }

    public List<AddressInfo> selectAllAllDistrict(){
        List<AddressInfo> addressInfoArrayList = new ArrayList<>();

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(ALL_DISTRICT_TABLE_NAME,
                allAllDistrictColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AddressInfo addressInfo = new AddressInfo();
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(ALL_DISTRICT_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(ALL_DISTRICT_NAME)));
            addressInfoArrayList.add(addressInfo);
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfoArrayList;
    }

    public AddressInfo selectAllDistrictById(int id){

        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.query(ALL_DISTRICT_TABLE_NAME,
                allAllDistrictColumns, ALL_DISTRICT_ID + " = " + id, null, null, null, null);

        cursor.moveToFirst();
        AddressInfo addressInfo = new AddressInfo();
        while (!cursor.isAfterLast()) {
            addressInfo.setId(cursor.getInt(cursor.getColumnIndex(ALL_DISTRICT_ID)));
            addressInfo.setName(cursor.getString(cursor.getColumnIndex(ALL_DISTRICT_NAME)));
            cursor.moveToNext();
        }
        cursor.close();
        return addressInfo;
    }

    public boolean isTableExists(String tableName, boolean openDb) {
        SQLiteDatabase mDatabase = this.getReadableDatabase();
        if(openDb) {
            if(mDatabase == null || !mDatabase.isOpen()) {
                mDatabase = getReadableDatabase();
            }

            if(!mDatabase.isReadOnly()) {
                mDatabase.close();
                mDatabase = getReadableDatabase();
            }
        }

        Cursor cursor = mDatabase.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"+tableName+"'", null);
        if(cursor!=null) {
            if(cursor.getCount()>0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }

    public void dropTable(String TableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.execSQL("DROP TABLE " + TableName);
    }
}
