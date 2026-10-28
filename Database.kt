package com.ink.suivichantier

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class Database(context: Context) : SQLiteOpenHelper(context, "suivi_chantier.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE records (id INTEGER PRIMARY KEY AUTOINCREMENT, category TEXT NOT NULL, date TEXT NOT NULL, chantier TEXT, designation TEXT NOT NULL, fournisseur TEXT, quantite REAL DEFAULT 0, tarif REAL DEFAULT 0, montant REAL DEFAULT 0, notes TEXT, created_at TEXT DEFAULT CURRENT_TIMESTAMP)")
        db.execSQL("CREATE TABLE chantiers (id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT UNIQUE, nom TEXT NOT NULL, lieu TEXT, responsable TEXT, date_debut TEXT, date_fin TEXT, statut TEXT DEFAULT 'En cours')")
        db.execSQL("CREATE INDEX idx_records_category ON records(category)")
        db.execSQL("CREATE INDEX idx_records_date ON records(date)")
        db.execSQL("CREATE INDEX idx_records_chantier ON records(chantier)")
    }
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}

    fun addRecord(values: ContentValues): Long = writableDatabase.insert("records", null, values)
    fun allRecords(): List<Record> {
        val out = mutableListOf<Record>()
        readableDatabase.rawQuery("SELECT id,category,date,chantier,designation,fournisseur,quantite,tarif,montant,notes FROM records ORDER BY date DESC,id DESC", null).use { c ->
            while (c.moveToNext()) out.add(Record(c.getLong(0), c.getString(1), c.getString(2), c.getString(3) ?: "", c.getString(4), c.getString(5) ?: "", c.getDouble(6), c.getDouble(7), c.getDouble(8), c.getString(9) ?: ""))
        }
        return out
    }
    fun deleteRecord(id: Long) { writableDatabase.delete("records", "id=?", arrayOf(id.toString())) }
    fun totalCost(): Double { readableDatabase.rawQuery("SELECT COALESCE(SUM(montant + CASE WHEN montant=0 THEN quantite*tarif ELSE 0 END),0) FROM records", null).use { if (it.moveToFirst()) return it.getDouble(0) }; return 0.0 }
    fun countCategory(category: String): Int { readableDatabase.rawQuery("SELECT COUNT(*) FROM records WHERE category=?", arrayOf(category)).use { if (it.moveToFirst()) return it.getInt(0) }; return 0 }
}

data class Record(val id: Long, val category: String, val date: String, val chantier: String, val designation: String, val fournisseur: String, val quantite: Double, val tarif: Double, val montant: Double, val notes: String)
