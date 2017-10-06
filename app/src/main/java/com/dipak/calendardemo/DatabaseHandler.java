package com.dipak.calendardemo;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.util.Iterator;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    static String DATABASE_NAME="OwnerDatabase";


    private static final String MESS_TABLE_NAME="MessInfo";
    private static final String MESS_TABLE_Column_ID="Day" ;
    private static final String MESS_TABLE_Column_1_week_details="Flag";
    private static final String MESS_TABLE_Column_2_menu_details="Menu";
    private static final String CREATE_TABLE_MESS
            ="CREATE TABLE IF NOT EXISTS "+MESS_TABLE_NAME+
            " ("+MESS_TABLE_Column_ID+" VARCHAR PRIMARY KEY, "
            +MESS_TABLE_Column_1_week_details+" VARCHAR, "+
            MESS_TABLE_Column_2_menu_details+" VARCHAR) ";


    private static final String VEG_INFO_TABLE_NAME="Menu_Veg";
    private static final String VEG_INFO_Column="Vegie";
    private static final String CREATE_TABLE_VEG
            ="CREATE TABLE IF NOT EXISTS "+VEG_INFO_TABLE_NAME+" " +
            "("+VEG_INFO_Column+" VARCHAR) ";

    private static final String SPE_INFO_TABLE_NAME="Menu_Special";
    private static final String SPE_INFO_Column="Special";
    private static final String CREATE_TABLE_SPE
            ="CREATE TABLE IF NOT EXISTS "+SPE_INFO_TABLE_NAME+" " +
            "("+SPE_INFO_Column+" VARCHAR) ";


    /*private static final String COLLEGE_TABLE_NAME="CollegeList";
    private static final String COLLEGE_TABLE_Column_1="Name";
    private static final String CREATE_TABLE_COLLEGE
            ="CREATE TABLE IF NOT EXISTS "+COLLEGE_TABLE_NAME+" " +
            "("+COLLEGE_TABLE_NAME+" VARCHAR PRIMARY KEY) ";
*/

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_VEG);
        database.execSQL(CREATE_TABLE_SPE);
        database.execSQL(CREATE_TABLE_MESS);
        //database.execSQL(CREATE_TABLE_COLLEGE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+MESS_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+VEG_INFO_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+SPE_INFO_TABLE_NAME);

        //db.execSQL("DROP TABLE IF EXISTS "+COLLEGE_TABLE_NAME);
        onCreate(db);
    }


    public void addFirst()
    {
        SQLiteDatabase database = this.getWritableDatabase();

        String insertsql2 = "INSERT INTO '"+VEG_INFO_TABLE_NAME+"' ('"+VEG_INFO_Column+"') VALUES" +
                "    ('Aaloo')," +
                "    ('Paneer')," +
                "    ('Chana Masala')," +
                "    ('Cholle')," +
                "    ('Matar')," +
                "    ('Karela')";

        String insertsql3 = "INSERT INTO '"+SPE_INFO_TABLE_NAME+"' ('"+SPE_INFO_Column+"') VALUES" +
                " ('Gajar Halwa')," +
                " ('Badam Halwa')," +
                " ('Moong Dal Halwa')," +
                " ('Aamras')," +
                " ('Dahi Puri')," +
                " ('Buttermilk')," +
                " ('Dahi Vada')," +
                " ('Dhokla')," +
                " ('Gulab Jamun')," +
                " ('Kachori')," +
                " ('Puran Poli')," +
                " ('Rabri')," +
                " ('Kheer')," +
                " ('Ras Malai')," +
                " ('Boondi Raita')," +
                " ('Jalebi')";

        //database.insert(MENU_INFO_TABLE_NAME, null, values);
        database.execSQL(insertsql2);
        database.execSQL(insertsql3);

        database.close();

    }


    public void addVegie(String vegie)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        String insertsql2 = "INSERT INTO '"+VEG_INFO_TABLE_NAME+"' ('"+VEG_INFO_Column+"') VALUES" +
                "    ('"+vegie+"')";


        database.execSQL(insertsql2);

        database.close();

    }

    public void addSpecial(String spec)
    {
        SQLiteDatabase database = this.getWritableDatabase();

        String insertsql3 = "INSERT INTO '"+SPE_INFO_TABLE_NAME+"' ('"+SPE_INFO_Column+"') VALUES" +
                " ('"+spec+"')";

        database.execSQL(insertsql3);

        database.close();

    }


    public ArrayList<String> getAllVegies() {
        ArrayList<String> contactList = new ArrayList<>();
        contactList.clear();
        String selectQuery = "SELECT "+VEG_INFO_Column+" FROM " + VEG_INFO_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contactList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();

        return contactList;
    }

    public List<String> getAllSpecials() {
        List<String> contactList = new ArrayList<>();
        contactList.clear();
        String selectQuery = "SELECT "+SPE_INFO_Column+" FROM " + SPE_INFO_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                contactList.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return contactList;
    }


    public void setWeekMenu(String jsonStr)
    {

        SQLiteDatabase database = this.getWritableDatabase();


        try {
            if (jsonStr != null) {

                JSONArray details = new JSONArray(jsonStr);

                for (int i = 0; i < details.length(); i++) {
                    JSONObject m = details.getJSONObject(i);

                    String day = m.getString("day");

                    JSONArray menu = m.getJSONArray("menu");

                    Log.i("Day ",day);
                    for (int j = 0; j < menu.length(); j++) {
                        JSONObject m1 = menu.getJSONObject(j);

                        Log.i("jsonobj",m1.toString());

                        String meal = m1.getString("Meal");

                        /*String insertsql2 = "REPLACE INTO '"+MESS_TABLE_NAME+
                                "' ('"+MESS_TABLE_Column_ID+"','"
                                +MESS_TABLE_Column_2_menu_details+"') VALUES"
                                +
                                " ('"+day+meal+"','"
                                +m1.toString()+"');";

                        database.execSQL(insertsql2);*/


                        ContentValues initialValues = new ContentValues();
                        initialValues.put(MESS_TABLE_Column_ID, day+meal); // the execution is different if _id is 2
                        initialValues.put(MESS_TABLE_Column_2_menu_details, m1.toString());

                        int id = (int) database.insertWithOnConflict(MESS_TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                        if (id == -1) {
                            database.update(MESS_TABLE_NAME, initialValues, MESS_TABLE_Column_ID+"=?", new String[] {day+meal});  // number 1 is the _id here, update to variable for your code
                        }
                    }

                }
            } else {
                Log.e("in data base handler","could not get menu");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        database.close();

        getFlagWeekMenu();
    }


    public void setFlagWeekMenu(JSONObject json)
    {

        Iterator<String> iter = json.keys();
        SQLiteDatabase database = this.getWritableDatabase();
        while (iter.hasNext()) {
            String key = iter.next();
            try {
                String value = json.getString(key);
                String meal = key.substring(0,1);
                if(meal.equals("l"))
                    meal="Lunch";
                else if(meal.equals("d"))
                    meal="Dinner";
                else
                    continue;

                String day = key.substring(3,6).toUpperCase();


                /*String insertsql2 = "REPLACE INTO '"+MESS_TABLE_NAME+
                        "' ('"+MESS_TABLE_Column_ID+"','"
                        +MESS_TABLE_Column_1_week_details+"') VALUES" +
                        "    ('"+day+meal+"','"+value+"');";

                database.execSQL(insertsql2);
                Log.i("query",insertsql2);*/

                ContentValues initialValues = new ContentValues();
                initialValues.put(MESS_TABLE_Column_ID, day+meal); // the execution is different if _id is 2
                initialValues.put(MESS_TABLE_Column_1_week_details, value);

                int id = (int) database.insertWithOnConflict(MESS_TABLE_NAME, null, initialValues, SQLiteDatabase.CONFLICT_IGNORE);
                if (id == -1) {
                    database.update(MESS_TABLE_NAME, initialValues, MESS_TABLE_Column_ID+"=?", new String[] {day+meal});  // number 1 is the _id here, update to variable for your code
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public Menu getMenu(String day, String meal)
    {
        Menu menud = null;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(MESS_TABLE_NAME, new String[] { MESS_TABLE_Column_ID,
                        MESS_TABLE_Column_2_menu_details}, MESS_TABLE_Column_ID + "=?",
                new String[] { day+meal }, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        try {
            JSONObject m1 = new JSONObject(cursor.getString(1));

            //Log.e("json");
            menud = new Menu(
                    m1.getString("Rice"),
                    m1.getString("Roti"),
                    m1.getString("VegieOne"),
                    m1.getString("VegieTwo"),
                    m1.getString("VegieThree"),
                    m1.getString("Special"),
                    m1.getString("SpecialExtra"),
                    m1.getString("Other")
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("in DBH",menud.toString());
        return menud;
    }

    public boolean[][] getFlagWeekMenu() {
        boolean status[][] = new boolean[8][2];

        String selectQuery = "SELECT * FROM " + MESS_TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                //Contact contact = new Contact();
                String id = cursor.getString(0);

                Log.i("cursor",cursor.getString(0)+cursor.getString(1)+cursor.getString(2));

                int dayOfWeek;
                if(id.substring(0,3).equals("SUN"))
                    dayOfWeek=0;
                else if(id.substring(0,3).equals("MON"))
                    dayOfWeek=1;
                else if(id.substring(0,3).equals("TUE"))
                    dayOfWeek=2;
                else if(id.substring(0,3).equals("WED"))
                    dayOfWeek=3;
                else if(id.substring(0,3).equals("THU"))
                    dayOfWeek=4;
                else if(id.substring(0,3).equals("FRI"))
                    dayOfWeek=5;
                else
                    dayOfWeek=6;


                if(id.substring(3,4).equals("L"))
                {
                    Log.i("getstring",cursor.getString(1));
                    if(cursor.getString(1).equals("1"))
                        status[dayOfWeek][0]=true;
                    else
                        status[dayOfWeek][0]=false;
                }
                else
                {
                    if(cursor.getString(1).equals("1"))
                        status[dayOfWeek][1]=true;
                    else
                        status[dayOfWeek][1]=false;
                }
            } while (cursor.moveToNext());
        }

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 2; j++) {
                Log.i("status",String.valueOf(status[i][j]));
            }
        }

        return status;
    }

   /* public void setNBCollege(JSONObject jsonObject)
    {

    }
    public ArrayList<String> getAllNBCollege()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<String> nbcoll = new ArrayList<>();
        Cursor cursor = db.query(COLLEGE_TABLE_NAME, new String[] { COLLEGE_TABLE_Column_1}, null,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                nbcoll.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }
        return nbcoll;
    }
*/
}