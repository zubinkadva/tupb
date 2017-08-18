package com.zar.theultimatephonebook;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class FilterContacts extends Activity {
    final int REQUEST_CODE=10;
    Intent intent;
    EditText filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_contacts);

        //Filter criteria text box
        filter=(EditText)findViewById(R.id.txtFilter);

        //Filter by city button click listener
        Button city=(Button)findViewById(R.id.cmdCity);
        city.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filter.getText().length()==0)
                    filter.setError("Enter Filter Criteria!!");
                else {
                    intent = new Intent(getApplicationContext(), FilterCity.class);
                    intent.putExtra("Filter", filter.getText().toString().toLowerCase());
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        //Filter by state button click listener
        Button state=(Button)findViewById(R.id.cmdState);
        state.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filter.getText().length()==0)
                    filter.setError("Enter Filter Criteria!!");
                else {
                    intent = new Intent(getApplicationContext(), FilterState.class);
                    intent.putExtra("Filter", filter.getText().toString().toLowerCase());
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });

        //Filter by country button click listener
        Button country=(Button)findViewById(R.id.cmdCountry);
        country.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(filter.getText().length()==0)
                    filter.setError("Enter Filter Criteria!!");
                else {
                    intent = new Intent(getApplicationContext(), FilterCountry.class);
                    intent.putExtra("Filter", filter.getText().toString().toLowerCase());
                    startActivityForResult(intent, REQUEST_CODE);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.filter_contacts, menu);
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
