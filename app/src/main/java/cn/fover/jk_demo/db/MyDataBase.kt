package cn.fover.jk_demo.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import org.jetbrains.anko.db.*

/**
 * Created by apple on 2017/6/11.
 */
class MyDataBase(ctx: Context) : ManagedSQLiteOpenHelper(ctx, "MyDatabase", null, 1) {
    companion object {
        private var instance: MyDataBase? = null

        @Synchronized
        fun getInstance(ctx: Context): MyDataBase {
            if (instance == null) {
                instance = MyDataBase(ctx.applicationContext)
            }
            return instance!!
        }
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Here you create tables
        db.createTable("Like", true,
                "id" to INTEGER + PRIMARY_KEY ,
                "type" to TEXT,
                "url" to TEXT,
                "is_like" to BLOB)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Here you can upgrade tables, as usual
        db.dropTable("User", true)
    }
}

// Access property for Context
val Context.database: MyDataBase
    get() = MyDataBase.getInstance(getApplicationContext())