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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;


public class SearchContact extends Activity {
    ImageButton b1;
    EditText search;
    String query;
    ArrayList<String> list;
    ListView l1;
    ArrayAdapter ada;
    SQLiteDatabase mydatabase;
    Cursor resultSet;
    Intent intent;
    final int REQUEST_CODE=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_contact);

        //List and ArrayAdapter creation and initialization
        list = new ArrayList<String>();
        l1 = (ListView)findViewById(R.id.lstResults);
        ada = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);

        //Control definitions
        b1 = (ImageButton)findViewById(R.id.cmdGo);
        search = (EditText)findViewById(R.id.txtQuery);

        //User clicks on Search button
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(search.getText().length()==0) {
                    search.setError("Enter Search Criteria!!");
                }
                else {
                    list.clear();
                    mydatabase = openOrCreateDatabase("zarphonebook", MODE_PRIVATE, null);
                    query = search.getText().toString().toLowerCase();
                    resultSet = mydatabase.rawQuery("select first_name || ' ' || last_name from contacts where lower(first_name) like '" + query + "%'", null);
                    resultSet.moveToFirst();

                    if (resultSet.moveToFirst()) {
                        while (!resultSet.isAfterLast()) {
                            list.add(resultSet.getString(0));
                            resultSet.moveToNext();
                        }
                    } else
                        list.add("Sorry, no one named \"" + search.getText() + "\"");

                    mydatabase.close();
                    l1.setAdapter(ada);
                }
            }
        });
        //l1.setAdapter(ada);

        //User selects a contact to view
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent=new Intent(getApplicationContext(),ContactDetails.class);
                intent.putExtra("SelectedName",l1.getItemAtPosition(i).toString());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.search_contact, menu);
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
