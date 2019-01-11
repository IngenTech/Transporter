package weatherrisk.com.wrms.transporter.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBAdapter {

    // Initial Configuration
    public static final String DB_NAME = "transporter";
    private static final int DATABASE_VER = 1;
    private static final String TAG = "DBAdapter";

    private final Context context;

    private DatabaseHelper DBHelper;
    public SQLiteDatabase db = null;

    public static final String CUSTOMER_ID = "customer_id";
    public static final String FROM_CITY_ID = "from_city_id";
    public static final String FROM_ADDRESS = "from_address";
    public static final String TO_CITY_ID = "to_city_id";
    public static final String TO_ADDRESS = "to_address";
    public static final String VEHICLE_QUANTITY = "vehicle_quantity";
    public static final String DOOR_STATUS = "door_status";
    public static final String INVOICE_AMOUNT = "invoice_amount";
    public static final String ORDER_DATE = "order_date";
    public static final String ORDER_STATUS = "order_status";

    public static final String TABLE_CREDENTIALS = "credential";
    public static final String TABLE_VEHICLE = "vehicle";
    public static final String TABLE_FIRM = "firm";
    public static final String TABLE_STATE = "state";

    public static final String TABLE_PHOTO = "photo";

    public static final String TABLE_CITY = "city";
    public static final String TABLE_MATERIAL = "material";

    public static final String TABLE_DOCUMENT_TITLE = "document_title";
    public static final String DOCUMENT_TITLE = "title";
    public static final String DOCUMENT_TITLE_ID = "title_id";

    public static final String TABLE_VEHICAL_MODEL = "vehical_model";
    public static final String TABLE_VEHICAL_TYPE = "vehical_type";

    public static final String TABLE_DRIVER = "driver_list";
    public static final String DRIVER_ID = "driver_id";
    public static final String DRIVER_NAME = "driver_name";
    public static final String DRIVER_DL = "driver_dl";
    public static final String DRIVER_AGE = "driver_age";
    public static final String DRIVER_SEX = "driver_sex";
    public static final String DRIVER_PHONE = "driver_phone";
    public static final String DRIVER_EMAIL = "driver_email";
    public static final String DRIVER_REMARK = "driver_remark";

    public static final String TABLE_EXPENSE_TYPE = "expense_type";
    public static final String EXPENSE_TYPE = "type";
    public static final String EXPENSE_TYPE_ID = "type_id";


    public static final String ID = "_id";
    public static final String GROUP_ID = "group_id";
    public static final String USER_ID = "user_id";
    public static final String PASSWORD = "password";
    public static final String TABLE_PROFILE = "getProfile";

    public static final String STATE_ID = "state_id";
    public static final String STATE_NAME = "state_name";
    public static final String ROAD_PERMIT = "road_permit";
    public static final String MIN_ROAD_PERMIT_AMOUNT = "min_road_permit_amount";
    public static final String CITY_ID = "city_id";
    public static final String CITY_NAME = "city_name";

    public static final String MATERIAL_ID = "material_id";
    public static final String MATERIAL_NAME = "material_name";

    public static final String VEHICAL_TYPE = "vehical_type";
    public static final String MODEL_NUMBER = "model_number";
    public static final String MODEL_NAME = "model_name";
    public static final String MODEL_YEAR = "model_year";


    public static final String SAVE = "Save";
    public static final String SENT = "Sent";
    public static final String DATE = "date";

    public static final String FIRM_ID = "id";
    public static final String FIRM_NAME = "firm_name";
    public static final String CONTACT_NAME = "contact_name";
    public static final String PERSON_CONTACT_NO = "person_contact_no";
    public static final String HOME_CONTACT_NO = "home_contact_no";
    public static final String OFFICE_CONTACT_NO = "office_contact_no";
    public static final String TIN_NO = "tin_no";
    public static final String PAN_NO = "pan_no";
    public static final String ADDRESS = "address";
    public static final String EMAIL = "email";
    public static final String DO_NO_CALL = "do_no_call";
    public static final String BANK_NAME = "bank_name";
    public static final String BANK_ACCOUNT_NO = "bank_account_no";
    public static final String BANK_BRANCH_NAME = "bank_branch_name";
    public static final String BANK_BRANCH_CODE = "bank_branch_code";
    public static final String REMARK = "remarks";

    public static final String VEHICLE_ID = "vehicle_id";
    public static final String VEHICLE_NO = "vehicle_no";
    public static final String IMEI = "imei";
    public static final String MODEL_NO = "model_no";
    public static final String REGISTRATION_NO = "registration_no";
    public static final String INSURANCE_NO = "insurance_no";
    public static final String VALIDITY_DATE = "validity_date";
    public static final String POLLUTION_NO = "pollution_no";
    public static final String YEAR_OF_PURCHASE = "year_of_purchase";
    public static final String CAPACITY = "capacity";
    public static final String REFRIGERATED = "refrigerated";
    public static final String CLOSED_DORE = "closed_door";

    public static final String VEHICLE_MODEL_ID = "model_id";
    public static final String VEHICLE_DATE_RC = "date_rc";
    public static final String VEHICLE_DATE_POLLUTION = "date_pollution";
    public static final String VEHICLE_ROAD_TAX = "road_tax_no";
    public static final String VEHICLE_DATE_ROAD_TAX = "date_road_tax";
    public static final String VEHICLE_PERMIT_TYPE = "permit_type";
    public static final String VEHICLE_PERMIT_REGION_ID = "permit_region_id";
    public static final String VEHICLE_DATE_PERMIT = "date_permit_type";
    public static final String VEHICLE_FITNESS = "fitness_no";
    public static final String VEHICLE_DATE_FITNESS = "date_fitness";
    public static final String VEHICLE_OTHER_DATE = "date_others";
    public static final String VEHICLE_OTHER_NO = "others_no";

    public static final String VEHICLE_CREATE_ID = "create_id";
    public static final String VEHICLE_CREATE_DATE = "create_date";
    public static final String VEHICLE_EDIT_ID = "edit_id";
    public static final String VEHICLE_STATUS = "status";
    public static final String VEHICLE_REMARKS = "remarks";
    public static final String VEHICLE_TYPE = "vehicle_type";
    public static final String VEHICLE_CATEGORY = "category";
    public static final String VEHICLE_MAX_SPEED = "max_speed";

    public static final String PHOTO = "picture";
    public static final String PHOTO_ID = "photo_id";


    private static final String CREATE_VEHICLE = "CREATE TABLE " + TABLE_VEHICLE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + VEHICLE_ID + " TEXT,"
            + VEHICLE_NO + " TEXT,"
            + IMEI + " TEXT,"
            + MODEL_NO + " TEXT,"
            + REGISTRATION_NO + " TEXT,"
            + INSURANCE_NO + " TEXT,"
            + VALIDITY_DATE + " TEXT,"
            + POLLUTION_NO + " TEXT,"
            + YEAR_OF_PURCHASE + " TEXT,"
            + CAPACITY + " TEXT,"

            + VEHICLE_MODEL_ID + " TEXT,"
            + VEHICLE_DATE_RC + " TEXT,"
            + VEHICLE_DATE_POLLUTION + " TEXT,"
            + VEHICLE_ROAD_TAX + " TEXT,"
            + VEHICLE_DATE_ROAD_TAX + " TEXT,"
            + VEHICLE_PERMIT_TYPE + " TEXT,"
            + VEHICLE_PERMIT_REGION_ID + " TEXT,"
            + VEHICLE_DATE_PERMIT + " TEXT,"
            + VEHICLE_FITNESS + " TEXT,"
            + VEHICLE_DATE_FITNESS + " TEXT,"

            + VEHICLE_OTHER_DATE + " TEXT,"
            + VEHICLE_OTHER_NO + " TEXT,"
            + VEHICLE_CREATE_ID + " TEXT,"
            + VEHICLE_CREATE_DATE + " TEXT,"
            + VEHICLE_EDIT_ID + " TEXT,"
            + VEHICLE_STATUS + " TEXT,"
            + VEHICLE_REMARKS + " TEXT,"
            + VEHICLE_TYPE + " TEXT,"
            + VEHICLE_CATEGORY + " TEXT,"
            + VEHICLE_MAX_SPEED + " TEXT,"

            + REFRIGERATED + " TEXT,"
            + CLOSED_DORE + " TEXT)";




    private static final String CREATE_CREDENTIAL = "CREATE TABLE " + TABLE_CREDENTIALS + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + GROUP_ID + " TEXT,"
            + USER_ID + " TEXT,"
            + PASSWORD + " TEXT,"
            + IMEI + " TEXT)";


    private static final String CREATE_STATE = "CREATE TABLE " + TABLE_STATE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + STATE_ID + " TEXT,"
            + STATE_NAME + " TEXT,"
            + ROAD_PERMIT + " TEXT,"
            + MIN_ROAD_PERMIT_AMOUNT + " TEXT)";

    private static final String CREATE_PHOTO = "CREATE TABLE " + TABLE_PHOTO + "("
            + PHOTO_ID + " TEXT ,"
            + USER_ID + " TEXT,"
            + PHOTO + " TEXT)";

    private static final String CREATE_TABLE_DRIVER = "CREATE TABLE " + TABLE_DRIVER + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + DRIVER_ID + " TEXT,"
            + DRIVER_NAME + " TEXT,"
            + DRIVER_DL + " TEXT,"
            + DRIVER_EMAIL + " TEXT,"
            + DRIVER_AGE + " TEXT,"
            + DRIVER_PHONE + " TEXT,"
            + DRIVER_SEX + " TEXT,"
            + DRIVER_REMARK + " TEXT)";

    private static final String CREATE_CITY = "CREATE TABLE " + TABLE_CITY + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + CITY_ID + " TEXT,"
            + CITY_NAME + " TEXT,"
            + STATE_ID + " TEXT)";
    private static final String CREATE_MATERIAL = "CREATE TABLE " + TABLE_MATERIAL + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + MATERIAL_ID + " TEXT,"
            + MATERIAL_NAME + " TEXT)";

    private static final String CREATE_DOCUMENT_TITLE = "CREATE TABLE " + TABLE_DOCUMENT_TITLE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + DOCUMENT_TITLE + " TEXT,"
            + DOCUMENT_TITLE_ID + " TEXT)";


    private static final String CREATE_VEHICAL_MODEL = "CREATE TABLE " + TABLE_VEHICAL_MODEL + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + VEHICAL_TYPE + " TEXT,"
            + MODEL_NAME + " TEXT,"
            + MODEL_NUMBER + " TEXT,"
            + MODEL_YEAR + " TEXT)";

    private static final String CREATE_VEHICAL_TYPE = "CREATE TABLE " + TABLE_VEHICAL_TYPE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + VEHICAL_TYPE + " TEXT)";

    private static final String CREATE_EXPENSE_TYPE = "CREATE TABLE " + TABLE_EXPENSE_TYPE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + EXPENSE_TYPE_ID + " TEXT,"
            + EXPENSE_TYPE + " TEXT)";

    private static final String CREATE_FIRM = "CREATE TABLE " + TABLE_FIRM + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + FIRM_ID + " TEXT,"
            + FIRM_NAME + " TEXT,"
            + CONTACT_NAME + " TEXT,"
            + PERSON_CONTACT_NO + " TEXT,"
            + HOME_CONTACT_NO + " TEXT,"
            + OFFICE_CONTACT_NO + " TEXT,"
            + TIN_NO + " TEXT,"
            + ADDRESS + " TEXT,"
            + EMAIL + " TEXT,"
            + STATE_ID + " TEXT,"
            + CITY_ID + " TEXT)";


    private static final String CREATE_PROFILE = "CREATE TABLE " + TABLE_PROFILE + "("
            + ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
            + FIRM_ID + " TEXT,"
            + FIRM_NAME + " TEXT,"
            + CONTACT_NAME + " TEXT,"
            + PERSON_CONTACT_NO + " TEXT,"
            + HOME_CONTACT_NO + " TEXT,"
            + OFFICE_CONTACT_NO + " TEXT,"
            + TIN_NO + " TEXT,"
            + PAN_NO + " TEXT,"
            + ADDRESS + " TEXT,"
            + EMAIL + " TEXT,"
            + DO_NO_CALL + " TEXT,"
            + BANK_NAME + " TEXT,"
            + BANK_ACCOUNT_NO + " TEXT,"
            + BANK_BRANCH_NAME + " TEXT,"
            + BANK_BRANCH_CODE + " TEXT,"
            + REMARK + " TEXT,"
            + STATE_ID + " TEXT,"
            + CITY_ID + " TEXT)";


    public DBAdapter(Context ctx) {
        this.context = ctx;
        DBHelper = new DatabaseHelper(context);
    }

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public DatabaseHelper(Context context) {
            super(context, DB_NAME, null, DATABASE_VER);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_CREDENTIAL);
            db.execSQL(CREATE_VEHICLE);
            db.execSQL(CREATE_FIRM);
            db.execSQL(CREATE_STATE);
            db.execSQL(CREATE_CITY);
            db.execSQL(CREATE_MATERIAL);
            db.execSQL(CREATE_DOCUMENT_TITLE);
            db.execSQL(CREATE_VEHICAL_MODEL);
            db.execSQL(CREATE_VEHICAL_TYPE);
            db.execSQL(CREATE_EXPENSE_TYPE);
            db.execSQL(CREATE_PROFILE);
            db.execSQL(CREATE_TABLE_DRIVER);
            db.execSQL(CREATE_PHOTO);


        }


        @Override

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");

            onCreate(db);

        }

    }

    public SQLiteDatabase getSQLiteDatabase() {
        SQLiteDatabase db = DBHelper.getWritableDatabase();
        return db;
    }


    public void deletefromtable(String tablename) {
        db.delete(tablename, null, null);
    }

    public DBAdapter open() throws SQLException {
        db = DBHelper.getWritableDatabase();
        return this;

    }


    public void resetDatabase() {
        SQLiteDatabase database = DBHelper.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_STATE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CITY);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_MATERIAL);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICAL_MODEL);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_VEHICAL_TYPE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCUMENT_TITLE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSE_TYPE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PROFILE);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_DRIVER);
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_PHOTO);
        database.execSQL(CREATE_STATE);
        database.execSQL(CREATE_CITY);
        database.execSQL(CREATE_MATERIAL);
        database.execSQL(CREATE_DOCUMENT_TITLE);
        database.execSQL(CREATE_VEHICAL_MODEL);
        database.execSQL(CREATE_VEHICAL_TYPE);
        database.execSQL(CREATE_TABLE_DRIVER);
        database.execSQL(CREATE_EXPENSE_TYPE);
        database.execSQL(CREATE_PROFILE);
        database.execSQL(CREATE_PHOTO);
        database.close();
    }


    public void close() {
        DBHelper.close();
    }

    public Cursor credentialList() throws SQLException {
        return db.query(TABLE_CREDENTIALS, new String[]{ID, GROUP_ID, USER_ID, PASSWORD, IMEI}, null,
                null, null, null, null, null);
    }

    public Cursor getVehicle() throws SQLException {
        return db.query(TABLE_VEHICLE, null, null, null, null, null, VEHICLE_NO, null);
    }

    public Cursor getVehicleById(String vehicleId) throws SQLException {
        return db.query(TABLE_VEHICLE, null, VEHICLE_ID + " = '" + vehicleId + "'",
                null, null, null, VEHICLE_NO, null);
    }

    public Cursor stateList() throws SQLException {
        return db.query(TABLE_STATE, null, null, null, null, null, null, null);
    }

    public Cursor photoList() throws SQLException {
        return db.query(TABLE_PHOTO, null, null, null, null, null, null, null);
    }

    public Cursor photoListByStateId(String userId) throws SQLException {
        return db.query(TABLE_PHOTO, null, USER_ID + " = '" + userId + "'",
                null, null, null, null, null);
    }

    public Cursor stateById(String stateId) throws SQLException {
        return db.query(TABLE_STATE, new String[]{ID, STATE_ID, STATE_NAME, ROAD_PERMIT, MIN_ROAD_PERMIT_AMOUNT}, STATE_ID + " = '" + stateId + "'",
                null, null, null, null, null);
    }

    public Cursor cityList() throws SQLException {
        return db.query(TABLE_CITY, null, null,
                null, null, null, null, null);
    }

    public Cursor cityListByStateId(String stateId) throws SQLException {
        return db.query(TABLE_CITY, null, STATE_ID + " = '" + stateId + "'",
                null, null, null, null, null);
    }

    public Cursor cityById(String cityId) throws SQLException {
        return db.query(TABLE_CITY, new String[]{ID, CITY_ID, CITY_NAME}, CITY_ID + " = '" + cityId + "'",
                null, null, null, null, null);
    }

    public Cursor documentTitleList() throws SQLException {
        return db.query(TABLE_DOCUMENT_TITLE, null, null,
                null, null, null, null, null);
    }

    public Cursor documentTitleListById(String docId) throws SQLException {
        return db.query(TABLE_DOCUMENT_TITLE, new String[]{ID, DOCUMENT_TITLE, DOCUMENT_TITLE_ID}, DOCUMENT_TITLE_ID + " = '" + docId + "'",
                null, null, null, null, null);
    }

    public Cursor materialList() throws SQLException {
        return db.query(TABLE_MATERIAL, new String[]{ID, MATERIAL_ID, MATERIAL_NAME}, null,
                null, null, null, null, null);
    }

    public Cursor materialById(String materialTypeId) throws SQLException {
        return db.query(TABLE_MATERIAL, new String[]{ID, MATERIAL_ID, MATERIAL_NAME}, MATERIAL_ID + " ='" + materialTypeId + "'",
                null, null, null, null, null);
    }

    public Cursor vehicalModelList() throws SQLException {
        return db.query(TABLE_VEHICAL_MODEL, new String[]{ID, VEHICAL_TYPE, MODEL_NAME, MODEL_NUMBER, MODEL_YEAR}, null,
                null, null, null, null, null);
    }

    public Cursor vehicalModelById(String vehicalModelTypeId) throws SQLException {
        return db.query(TABLE_VEHICAL_MODEL, new String[]{ID, MODEL_NO, MODEL_NAME}, MODEL_NUMBER + " ='" + vehicalModelTypeId + "'",
                null, null, null, null, null);
    }

    public Cursor vehicalTypeList() throws SQLException {
        return db.query(TABLE_VEHICAL_TYPE, new String[]{ID, VEHICAL_TYPE}, null,
                null, null, null, null, null);
    }

    public Cursor expenseTypeList() throws SQLException {
        return db.query(TABLE_EXPENSE_TYPE, new String[]{ID, EXPENSE_TYPE_ID,EXPENSE_TYPE}, null, null, null, null, null, null);
    }

    public Cursor expenseTypeByID(String expenseTypeId) throws SQLException {
        return db.query(TABLE_EXPENSE_TYPE, new String[]{ID, EXPENSE_TYPE_ID, EXPENSE_TYPE}, EXPENSE_TYPE_ID + " ='" + expenseTypeId + "'",
                null, null, null, null, null);
    }


    public Cursor firmList() throws SQLException {
        return db.query(TABLE_FIRM, null, null, null, null, null, null, null);
    }

    public Cursor getProfile() throws SQLException {
        return db.query(TABLE_PROFILE, null, null,
                null, null, null, null, null);
    }

    public Cursor getProfileByAccountId(String firmId) throws SQLException {
        return db.query(TABLE_PROFILE, null, FIRM_ID + " ='" + firmId + "'",
                null, null, null, null, null);
    }

    public Cursor driverList() throws SQLException {
        return db.query(TABLE_DRIVER, null, null,
                null, null, null, null, null);
    }

}
