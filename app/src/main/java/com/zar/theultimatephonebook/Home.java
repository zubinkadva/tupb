package com.zar.theultimatephonebook;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import java.io.File;


public class Home extends Activity {
    private static final int REQUEST_CODE = 10;
    Intent intent;
    SQLiteDatabase mydatabase;

    //Function for initial database creation and population
    protected void createDB() {
        mydatabase = openOrCreateDatabase("zarphonebook",MODE_PRIVATE,null);
        mydatabase.execSQL("create table if not exists contacts (" +
                "id numeric," +
                "first_name varchar," +
                "middle_name varchar," +
                "last_name varchar," +
                "phone1 varchar," +
                "phone2 varchar," +
                "email varchar," +
                "dob date," +
                "age numeric, " +
                "gender char," +
                "address varchar," +
                "city varchar," +
                "state varchar," +
                "country varchar," +
                "family_members numeric," +
                "avatar varchar" +
                ");");
//        mydatabase.execSQL("insert into contacts values(1,'Zubin','Kerman','Kadva','123456','789456','aaaaaaa@a.com','17-02-1991',23,'M','Mumbai, MH, India','Mumbai','MH','India',4,'/data/data/com.zar.theultimatephonebook/images/Zubin_Kerman_Kadva.jpg');");
//        mydatabase.execSQL("insert into contacts values(2,'Benaz','Kerman','Kadva','123456','789456','b@b.com','08-02-1995',19,'M','Mumbai, MH, India','Mumbai','MH','India',4,'/data/data/com.zar.theultimatephonebook/images/Benaz_Kerman_Kadva.jpg');");
//        mydatabase.execSQL("insert into contacts values(3,'Diana','Kerman','Kadva','123456','789456','d@d.com','17-01-1957',57,'M','Mumbai, MH, India','Mumbai','MH','India',4,'/data/data/com.zar.theultimatephonebook/images/Diana_Kerman_Kadva.jpg');");
//        mydatabase.execSQL("insert into contacts values(4,'Kerman','Erach','Kadva','123456','789456','k@k.com','17-11-1958',56,'M','Mumbai, MH, India','Mumbai','MH','India',4,'/data/data/com.zar.theultimatephonebook/images/Kerman_Erach_Kadva.jpg');");
        mydatabase.close();
        }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        createDB();

        //Menu button listeners begin here

        ImageButton add=(ImageButton)findViewById(R.id.imageButton1);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getApplicationContext(),AddUpdateContact.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        ImageButton view=(ImageButton)findViewById(R.id.imageButton2);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getApplicationContext(),ViewContacts.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        ImageButton search=(ImageButton)findViewById(R.id.imageButton3);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getApplicationContext(),SearchContact.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        ImageButton update=(ImageButton)findViewById(R.id.imageButton5);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getApplicationContext(),ViewContacts.class);
                intent.putExtra("ToUpdateContact",true);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        ImageButton filter=(ImageButton)findViewById(R.id.imageButton6);
        filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent=new Intent(getApplicationContext(),FilterContacts.class);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        ImageButton exit=(ImageButton)findViewById(R.id.imageButton7);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //END here

        //Check if SD card mounted
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d("MyApp", "No SDCARD");
        }
        //SD card mounted, now create images directory
        else {
            File directory = new File("/data/data/com.zar.theultimatephonebook/"+"images");
            directory.mkdirs();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

}
