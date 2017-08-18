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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;


public class FilterCountry extends Activity {

    ArrayList<String> list;
    ListView l1;
    ArrayAdapter ada;
    SQLiteDatabase mydatabase;
    Cursor resultSet;
    Intent intent;
    final int REQUEST_CODE=10;
    String criteria;
    EditText filter;
    Button go;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_country);

        //Control definitions
        filter=(EditText)findViewById(R.id.txtFilter);
        go=(Button)findViewById(R.id.cmdGo);
        criteria=getIntent().getStringExtra("Filter");
        filter.setText(criteria);

        //List and ArrayAdapter creation and initialization
        list = new ArrayList<String>();
        l1 = (ListView)findViewById(R.id.lstFilterCountry);
        ada = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);

        //User selects a contact to view
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent=new Intent(getApplicationContext(),ContactDetails.class);
                intent.putExtra("SelectedName",l1.getItemAtPosition(i).toString());
                startActivityForResult(intent,REQUEST_CODE);
            }
        });

        //Function call to filter by country
        filterByCountry();

        //Filter button click listener
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filter.getText().length()==0)
                    filter.setError("Enter Country Filter Criteria!!");
                else {
                    criteria = filter.getText().toString();
                    filterByCountry();
                }
            }
        });
    }

    //Logic to filter by country
    protected  void filterByCountry() {
        list.clear();
        mydatabase = openOrCreateDatabase("zarphonebook",MODE_PRIVATE,null);
        resultSet = mydatabase.rawQuery("Select first_name || ' ' || last_name from contacts where lower(country)='"+criteria+"'order by lower(first_name)",null);
        resultSet.moveToFirst();

        if(resultSet.moveToFirst()) {
            while (!resultSet.isAfterLast()) {
                list.add(resultSet.getString(0));
                resultSet.moveToNext();
            }
        }
        else {
            list.add("Sorry, no one lives in \""+criteria+"\"");
        }

        l1.setAdapter(ada);
        mydatabase.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_country, menu);
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
