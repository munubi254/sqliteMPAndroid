package com.munubi.sqlitetest

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteException

class DatabaseHandler(context: Context): SQLiteOpenHelper(context,DB_NAME,null,DB_VERSION) {
    companion object {
        private val DB_VERSION = 1
        private val DB_NAME = "wildDB"
        private val MAIN_TABLE = "mainTable"
        private val ID_FIELD = "_id"
        private val NAME_FIELD = "name"
        private val NUMBER_FIELD = "tot_number"
        private val AGE_FIELD = "avg_age"
        private val GROWTH_FIELD = "growth_rate"
    }
    override fun onCreate(ourDB: SQLiteDatabase?) {
        //creating our table with the respective fields
        val CREATE_MAIN_TABLE = ("CREATE TABLE " + MAIN_TABLE + "("
                + ID_FIELD + " INTEGER PRIMARY KEY,"
                + NAME_FIELD + " TEXT,"
                + NUMBER_FIELD + " INTEGER,"
                + AGE_FIELD + " INTEGER,"
                + GROWTH_FIELD + " INTEGER" + ")")
        //executing the create table query
        ourDB?.execSQL(CREATE_MAIN_TABLE)
    }

    //function to be invoked when upgrading your database
    override fun onUpgrade(ourDB: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

        ourDB!!.execSQL("DROP TABLE IF EXISTS " + MAIN_TABLE)
        onCreate(ourDB)
    }


    //a method to insert records
    fun addAnimalDetails(animal: AnimalModel):Long{
        //opening the database in a writable mode to be able to make changes in it
        val ourDB = this.writableDatabase
        val ourContentValues = ContentValues()
        ourContentValues.put(ID_FIELD, animal.animalId)
        ourContentValues.put(NAME_FIELD, animal.animalName)
        ourContentValues.put(NUMBER_FIELD, animal.totNumber)
        ourContentValues.put(AGE_FIELD, animal.avgAge)
        ourContentValues.put(GROWTH_FIELD, animal.avgGrowth)
        val success = ourDB.insert(MAIN_TABLE, null, ourContentValues)
        //closse the database
        ourDB.close()
        return success
    }

    //method to read the animal records
    @SuppressLint("Range")
    fun retreiveAnimals():List<AnimalModel>{
        //a list to be returned after fetching the records
        val animalList:ArrayList<AnimalModel> = ArrayList<AnimalModel>()
        //the SELECT query
        val selectQuery = "SELECT  * FROM $MAIN_TABLE"
        //we open the database in a readable mode for fetching the records
        val ourDB = this.readableDatabase
        //cursor for storing the retrieved records
        var ourCursor: Cursor? = null
        try{
            ourCursor = ourDB.rawQuery(selectQuery, null)
        }catch (e: SQLiteException) {
            ourDB.execSQL(selectQuery)
            return ArrayList()
        }

        var animalIDReturned: Int
        var animalNameReturned: String
        var animalNumberReturned: Int
        var animalAgeReturned:Int
        var animalGrowthReturned:Int

        //fetch all the records until all are finished
        if (ourCursor.moveToFirst()) {
            do {
                //assign the values gotten to the respective strings
                animalIDReturned = ourCursor.getInt(ourCursor.getColumnIndex("_id"))
                animalNameReturned = ourCursor.getString(ourCursor.getColumnIndex("name"))
                animalNumberReturned = ourCursor.getInt(ourCursor.getColumnIndex("tot_number"))
                animalAgeReturned = ourCursor.getInt(ourCursor.getColumnIndex("avg_age"))
                animalGrowthReturned = ourCursor.getInt(ourCursor.getColumnIndex("growth_rate"))

                //add the values to the Model class and later to the arraylist
                val animalRow= AnimalModel(animalId=animalIDReturned,animalName=animalNameReturned,totNumber=animalNumberReturned,avgAge=animalAgeReturned,avgGrowth=animalGrowthReturned)
                animalList.add(animalRow)
            } while (ourCursor.moveToNext())
        }
        return animalList
    }
}