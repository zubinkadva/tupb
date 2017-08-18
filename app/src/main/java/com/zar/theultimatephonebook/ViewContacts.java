package com.zar.theultimatephonebook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;


public class ViewContacts extends Activity {
    ArrayList<String> list;
    ListView l1;
    ArrayAdapter ada;
    SQLiteDatabase mydatabase;
    Cursor resultSet;
    Intent intent;
    private int REQUEST_CODE=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);

        //Check if user has come from the Update contact activity
        if(getIntent().getBooleanExtra("ToUpdateContact", false))
            setTitle("Select a contact");

        //List and ArrayAdapter creation and initialization
        list = new ArrayList<String>();
        l1 = (ListView)findViewById(R.id.lstContacts);
        ada = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);

        mydatabase = openOrCreateDatabase("zarphonebook",MODE_PRIVATE,null);
        resultSet = mydatabase.rawQuery("Select first_name || ' ' || last_name from contacts order by lower(first_name)",null);
        resultSet.moveToFirst();

        if(resultSet.moveToFirst()) {
            while (!resultSet.isAfterLast()) {
                list.add(resultSet.getString(0));
                resultSet.moveToNext();
            }
        }
        else
            list.add("No Contacts Exist Yet!!");

        l1.setAdapter(ada);
        mydatabase.close();

        //User selects a contact to view / update
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //If user has selected for updating a contact
                if(getIntent().getBooleanExtra("ToUpdateContact", false))
                    intent=new Intent(getApplicationContext(),AddUpdateContact.class);

                //If user has selected for viewing a contact
                else
                    intent=new Intent(getApplicationContext(),ContactDetails.class);

                intent.putExtra("SelectedName",l1.getItemAtPosition(i).toString());
                startActivityForResult(intent,REQUEST_CODE);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.view_contacts, menu);
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
