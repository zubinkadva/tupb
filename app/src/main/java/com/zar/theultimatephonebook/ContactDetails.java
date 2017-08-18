package com.zar.theultimatephonebook;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;


public class ContactDetails extends Activity {

    ArrayList<String> list;
    ListView l1;
    ArrayAdapter ada;

    SQLiteDatabase mydatabase;
    Cursor resultSet;
    String gotName,temp,tel1,tel2,eadd;
    int gotId;
    Intent intent;

    private int REQUEST_CODE=10;
    ImageButton call1,call2,mail;
    ImageView avatar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);

        //List and ArrayAdapter creation and initialization
        list = new ArrayList<String>();
        l1 = (ListView)findViewById(R.id.lstDetails);
        ada = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);

        //ImageView definition
        avatar=(ImageView)findViewById(R.id.avatar);

        //Get name of contact passed as an intent
        gotName=getIntent().getStringExtra("SelectedName");
        setTitle(gotName);

        //Retrieve contact id depending on parameter passed
        mydatabase = openOrCreateDatabase("zarphonebook",MODE_PRIVATE,null);
        resultSet = mydatabase.rawQuery("select id from contacts where first_name || ' ' || last_name='"+gotName+"' ",null);
        resultSet.moveToFirst();

        while(!resultSet.isAfterLast()) {
            gotId=resultSet.getInt(0);
            resultSet.moveToNext();
        }

        //Now retrieve full contact details
        resultSet = mydatabase.rawQuery("select * from contacts where id="+gotId+" ",null);
        resultSet.moveToFirst();

        while(!resultSet.isAfterLast()) {
            //Index 1 - Full name (First / Middle / Last name)
            list.add(resultSet.getString(resultSet.getColumnIndex("first_name")) + " " +
                    resultSet.getString(resultSet.getColumnIndex("middle_name")) +" " +
                    resultSet.getString(resultSet.getColumnIndex("last_name")));

            //Index 2 - Phone 1
            tel1=resultSet.getString(resultSet.getColumnIndex("phone1"));
            list.add(tel1);

            //Index 3 - Phone 2, but first check if null or what
            temp=resultSet.getString(resultSet.getColumnIndex("phone2"));
                if(temp.equals("")) {
                    list.add("<Phone 2 Absent>");
//                    call2.setVisibility(View.GONE);
                }
                else {
                    tel2=temp;
                    list.add(tel2);
                }

            //Index 4 - Email, but first check if null or what
            temp=resultSet.getString(resultSet.getColumnIndex("email"));
                if(temp.equals("")) {
                    list.add("<Email Address Absent>");
                    //mail.setVisibility(View.GONE);
                }
                else {
                    eadd=temp;
                    list.add(eadd);
                }

            //Index 5 - DOB, age, gender
            list.add(resultSet.getString(resultSet.getColumnIndex("dob"))+" "+
                    resultSet.getInt(resultSet.getColumnIndex("age")) + " years"+ " "+
                    resultSet.getString(resultSet.getColumnIndex("gender")));

            //Index 6 - Full address
            list.add(resultSet.getString(resultSet.getColumnIndex("address")));

            //Index 7 - City, state, country
            list.add(resultSet.getString(resultSet.getColumnIndex("city"))+" "+
                    resultSet.getString(resultSet.getColumnIndex("state"))+" "+
                    resultSet.getString(resultSet.getColumnIndex("country")));

            //Index 8 - Number of family members
            list.add(resultSet.getString(resultSet.getColumnIndex("family_members")) + " family members");

            //Time for the avatar
            String found=resultSet.getString(resultSet.getColumnIndex("avatar"));

                //Set the default avatar based on whether the contact is M / F
                if(found==null || found.equals("null") || found.equals("")) {
                    if(resultSet.getString(resultSet.getColumnIndex("gender")).equals("M"))
                        avatar.setBackgroundResource(R.drawable.default_male);
                    else
                        avatar.setBackgroundResource(R.drawable.default_female);
                }

                //Value found!!. Set the user defined avatar
                else {
                    Bitmap bitmap = BitmapFactory.decodeFile(found);
                    avatar.setImageBitmap(bitmap);
                }

            resultSet.moveToNext();
        }
        //Retrievals complete

        l1.setAdapter(ada);
        mydatabase.close();
/*
        call1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+tel1));
                startActivity(intent);
            }
        });

        call2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:"+tel2));
                startActivity(intent);
            }
        });

        mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{eadd});

                try {
                    startActivity(intent);
                }
                catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(ContactDetails.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
*/
        //User selects an item from the list, hence add a listener
        l1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                //If user has selected Phone 1 call
                if(l1.getItemIdAtPosition(i)==1) {
                    intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+tel1));
                    startActivity(intent);
                }

                //If user has selected Phone 2 call, but check if number is present first!!
                else if(l1.getItemIdAtPosition(i)==2 && !l1.getItemAtPosition(i).equals("<Phone 2 Absent>")) {
                    intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:"+tel2));
                    startActivity(intent);
                }

                //If user has selected email, but check if address is present first!!
                else if(l1.getItemIdAtPosition(i)==3 && !l1.getItemAtPosition(i).equals("<Email Address Absent>")) {
                    intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{eadd});

                    try {
                        startActivity(intent);
                    }
                    catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(ContactDetails.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.contact_details, menu);
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
