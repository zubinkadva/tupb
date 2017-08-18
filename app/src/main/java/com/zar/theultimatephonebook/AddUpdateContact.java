package com.zar.theultimatephonebook;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class AddUpdateContact extends Activity {

    private static final int SELECT_PICTURE = 1;
    private static final int REQUEST_IMAGE = 100;
    private static final String DEFAULT_MALE="/data/data/com.zar.theultimatephonebook/images/default_male.png";
    private static final String DEFAULT_FEMALE="/data/data/com.zar.theultimatephonebook/images/default_female.png";
    File destination;
    ImageButton gallery,camera;
    ImageView display;
    File src,dest;
    final String LOCATION="/data/data/com.zar.theultimatephonebook/images/";
    String imagePath=null,final_location;

    ImageButton ib;
    Calendar cal;
    int day;
    int month;
    int year;
    String selected_dob[];
    int maxid;
    EditText first,middle,last,phone1,phone2,email,dob,address,city,state,country,no;
    RadioButton male,female;
    Intent intent;
    SQLiteDatabase mydatabase;
    Cursor resultSet;
    String gotName=null;
    int gotId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_update_contact);

        //Control definitions
        ib = (ImageButton) findViewById(R.id.imageButton);
        cal = Calendar.getInstance();
        day = cal.get(Calendar.DAY_OF_MONTH);
        month = cal.get(Calendar.MONTH);
        year = cal.get(Calendar.YEAR);
        dob = (EditText) findViewById(R.id.txtDOB);
        gallery=(ImageButton)findViewById(R.id.cmdGallery);
        camera=(ImageButton)findViewById(R.id.cmdCamera);
        display=(ImageView)findViewById(R.id.imgContact);

        //Setting the file name for a newly captured image
        String name = dateToString(new Date(),"yyyy-MM-dd-hh-mm-ss");
        destination = new File(Environment.getExternalStorageDirectory(), name + ".jpg");

        //If user wants to select an image from the gallery
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
            }
        });

        //If user wants to capture an image
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(destination));
                startActivityForResult(intent, REQUEST_IMAGE);

            }
        });

        //Show the date picker
        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(0);
            }
        });

        //More control definitions
        first=(EditText)findViewById(R.id.txtFirst);
        middle=(EditText)findViewById(R.id.txtMiddle);
        last=(EditText)findViewById(R.id.txtLast);
        phone1=(EditText)findViewById(R.id.txtP1);
        phone2=(EditText)findViewById(R.id.txtP2);
        email=(EditText)findViewById(R.id.txtEmail);

        male=(RadioButton)findViewById(R.id.rdMale);
        female=(RadioButton)findViewById(R.id.rdFemale);
        address=(EditText)findViewById(R.id.txtAddress);
        city=(EditText)findViewById(R.id.txtCity);
        state=(EditText)findViewById(R.id.txtState);
        country=(EditText)findViewById(R.id.txtCountry);
        no=(EditText)findViewById(R.id.txtNo);

        //Display default male image when page is first loaded (Male is by default checked)
        display.setBackgroundResource(R.drawable.default_male);

        //If user explicitly clicks on Male
        male.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(imagePath==null || imagePath.equals("null"))
                    display.setBackgroundResource(R.drawable.default_female);
            }
        });

        //If user explicitly clicks on Female
        female.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(imagePath==null || imagePath.equals("null"))
                    display.setBackgroundResource(R.drawable.default_male);
            }
        });

        //If the request has come from Update activity
        if(getIntent().getStringExtra("SelectedName")!=null) {
            gotName=getIntent().getStringExtra("SelectedName");
            setTitle("Update "+gotName);

            mydatabase = openOrCreateDatabase("zarphonebook",MODE_PRIVATE,null);

            //First get the ID
            resultSet = mydatabase.rawQuery("select id from contacts where first_name || ' ' || last_name='"+gotName+"' ",null);
            resultSet.moveToFirst();

            while(!resultSet.isAfterLast()) {
                gotId=resultSet.getInt(0);
                resultSet.moveToNext();
            }

            //Then retrieve the contact details based on the above ID
            resultSet = mydatabase.rawQuery("select * from contacts where id="+gotId+" ",null);
            resultSet.moveToFirst();

            while(!resultSet.isAfterLast()) {
                //Part 1 - Full name (First / Middle / Last name)
                first.setText(resultSet.getString(resultSet.getColumnIndex("first_name")));
                middle.setText(resultSet.getString(resultSet.getColumnIndex("middle_name")));
                last.setText(resultSet.getString(resultSet.getColumnIndex("last_name")));

                //Part 2 - Phones and email
                phone1.setText(resultSet.getString(resultSet.getColumnIndex("phone1")));
                phone2.setText(resultSet.getString(resultSet.getColumnIndex("phone2")));
                email.setText(resultSet.getString(resultSet.getColumnIndex("email")));

                //Part 3 - DOB
                dob.setText(resultSet.getString(resultSet.getColumnIndex("dob")));
                selected_dob=dob.getText().toString().split("/");
                day=Integer.parseInt(selected_dob[0]);
                month=Integer.parseInt(selected_dob[1])+1;
                year=Integer.parseInt(selected_dob[2]);

                //Part 4 - Gender
                if(resultSet.getString(resultSet.getColumnIndex("gender")).equals("M"))
                    male.setChecked(true);
                else
                    female.setChecked(true);

                //Part 5 - Full address
                address.setText(resultSet.getString(resultSet.getColumnIndex("address")));

                //Part 6 - City, state, country
                city.setText(resultSet.getString(resultSet.getColumnIndex("city")));
                state.setText(resultSet.getString(resultSet.getColumnIndex("state")));
                country.setText(resultSet.getString(resultSet.getColumnIndex("country")));

                //Part 7 - Number of family members
                no.setText(resultSet.getString(resultSet.getColumnIndex("family_members")));

                //Part 8 - Avatar [The most sickening part!!]
                imagePath=resultSet.getString(resultSet.getColumnIndex("avatar"));
                System.out.println("PATH: "+imagePath);

                //If the avatar has no value, then display default (Of course, based on gender!!)
                if(imagePath==null || imagePath.equals("null")) {
                    if (resultSet.getString(resultSet.getColumnIndex("gender")).equals("M"))
                        display.setBackgroundResource(R.drawable.default_male);
                    else
                        display.setBackgroundResource(R.drawable.default_female);
                }

                //Avatar has a value
                else {
                    Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
                    display.setBackgroundResource(0);
                    display.setImageBitmap(bitmap);
                }

                resultSet.moveToNext();
            }
            //Retrieval done!!

            mydatabase.close();
        }
    }

    //Overridden?? (For image selector / capture)
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            display.setBackgroundResource(0);

            //If user has selected an image from the gallery
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                imagePath = getPath(selectedImageUri);
                System.out.println("Old Image Path : " + imagePath);

                display.setImageURI(selectedImageUri);
            }
        }

        //If user has captured an image
        if(requestCode == REQUEST_IMAGE ) {
            try {
                FileInputStream in = new FileInputStream(destination);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 10;
                imagePath = destination.getAbsolutePath();
                //tvPath.setText(imagePath);
                Bitmap bmp = BitmapFactory.decodeStream(in, null, options);
                display.setImageBitmap(bmp);
            }
            catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }

    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            dob.setText(selectedDay + "/" + (selectedMonth + 1) + "/"
                    + selectedYear);
            day=selectedDay;
            month=selectedMonth;
            year=selectedYear;
        }
    };

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.cmdInsert:
                insertData();
                return true;
            case R.id.cmdCancel:
                gotoHomeScreen();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Function to insert / update the data
    protected void insertData() {
        //First get the gender
        char sex;
        if(male.isChecked()) sex='M';
        else sex='F';

        //Then calculate the age
        int age=getAge(year,month,day);
        //Log.d("CALCULATED AGE IS ",""+age);

        //Only after successful validation
        if(validation()) {
            //User selected an image (captured / gallery)
            if((imagePath!=null)) {
                src = new File(imagePath);
                final_location = LOCATION + first.getText() + "_" + middle.getText() + "_" + last.getText() + ".jpg";
                dest = new File(final_location);

                //System.out.println("SRC: " + imagePath + " DEST: " + final_location);

                try {
                    //IMPORTANT - Check if source is the same as destination and source is a valid file, THEN ONLY COPY!!
                    if (!imagePath.equals(final_location) && !imagePath.equals("null")) {
                        System.out.println("SRC: " + imagePath + " DEST: " + final_location);
                        copyFile(src, dest);
                    }

                    //IMPORTANT - Check if source is fake and not valid
                    else if(imagePath.equals("null"))
                        final_location=null;
                }

                catch (IOException e) {
                    System.out.print(e.getMessage());
                }
            }
            //Avatar stuff ends

            //Request for update, do it!!
            if (gotName != null) {
                mydatabase = openOrCreateDatabase("zarphonebook", MODE_PRIVATE, null);

                //Update begins
                mydatabase.execSQL("update contacts set " +
                        "id=" + gotId + "," +
                        "first_name='" + first.getText() + "'," +
                        "middle_name='" + middle.getText() + "'," +
                        "last_name='" + last.getText() + "'," +
                        "phone1='" + phone1.getText() + "'," +
                        "phone2='" + phone2.getText() + "'," +
                        "email='" + email.getText() + "'," +
                        "dob='" + dob.getText() + "'," +
                        "age=" + age + "," +
                        "gender='" + sex + "'," +
                        "address='" + address.getText() + "'," +
                        "city='" + city.getText() + "'," +
                        "state='" + state.getText() + "'," +
                        "country='" + country.getText() + "'," +
                        "family_members=" + no.getText() + ", " +
                        "avatar='"+final_location+"'" +
                        "where id=" + gotId);
                //Update ends

                mydatabase.close();

                //Redirect to Home with Toast message
                intent = new Intent(getApplicationContext(), Home.class);
                Toast.makeText(getApplicationContext(),"Contact Updated",Toast.LENGTH_LONG).show();
                finish();
            }

            //Request for insert, do it!!
            else {
                mydatabase = openOrCreateDatabase("zarphonebook", MODE_PRIVATE, null);

                //Get maximum ID from database
                resultSet = mydatabase.rawQuery("select max(id) from contacts", null);
                resultSet.moveToFirst();
                while (!resultSet.isAfterLast()) {
                    maxid = resultSet.getInt(0);
                    resultSet.moveToNext();
                }
                //Result stored in maxid

                //Insert begins
                mydatabase.execSQL("insert into contacts values(" +
                        (maxid + 1) + "," +
                        "'" + first.getText() + "'," +
                        "'" + middle.getText() + "'," +
                        "'" + last.getText() + "'," +
                        "'" + phone1.getText() + "'," +
                        "'" + phone2.getText() + "'," +
                        "'" + email.getText() + "'," +
                        "'" + dob.getText() + "'," +
                        age + "," +
                        "'" + sex + "'," +
                        "'" + address.getText() + "'," +
                        "'" + city.getText() + "'," +
                        "'" + state.getText() + "'," +
                        "'" + country.getText() + "'," +
                        no.getText() + "," +
                        "'"+final_location+"')");

                mydatabase.close();
                //Insert ends

                //Redirect to Home with Toast message
                intent = new Intent(getApplicationContext(), Home.class);
                Toast.makeText(getApplicationContext(), "Contact Inserted", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    protected void gotoHomeScreen() {
        //Just redirect to Home
        intent=new Intent(getApplicationContext(),Home.class);
        finish();
    }

    //My age calculator function
    public int getAge (int _year, int _month, int _day) {
        GregorianCalendar cal = new GregorianCalendar();
        int y, m, d, a;

        y = cal.get(Calendar.YEAR);
        m = cal.get(Calendar.MONTH);
        d = cal.get(Calendar.DAY_OF_MONTH);
        cal.set(_year, _month, _day);
        a = y - cal.get(Calendar.YEAR);
        if ((m < cal.get(Calendar.MONTH))
                || ((m == cal.get(Calendar.MONTH)) && (d < cal
                .get(Calendar.DAY_OF_MONTH)))) {
            --a;
        }
//        if(a < 0)
//            throw new IllegalArgumentException("Age < 0");
        return a;
    }

    //My validations
    protected boolean validation() {
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(first.getText().length()==0) {
            first.setError("Enter First Name!!");
            return false;
        }
        else if(middle.getText().length()==0) {
            middle.setError("Enter Middle Name!!");
            return false;
        }
        else if(last.getText().length()==0) {
            last.setError("Enter Last Name!!");
            return false;
        }
        else if(phone1.getText().length()==0) {
            phone1.setError("Enter Phone 1!!");
            return false;
        }
        else if(email.getText().length()>0 && !email.getText().toString().trim().matches(emailPattern)) {
            email.setError("Invalid Email Address!!");
            return false;
        }
        else if(dob.getText().length()==0) {
            dob.setError("Enter Date Of Birth!!");
            return false;
        }
        else if(address.getText().length()==0) {
            address.setError("Enter Address!!");
            return false;
        }
        else if(city.getText().length()==0) {
            city.setError("Enter City!!");
            return false;
        }
        else if(state.getText().length()==0) {
            state.setError("Enter State!!");
            return false;
        }
        else if(country.getText().length()==0) {
            country.setError("Enter Country!!");
            return false;
        }
        else if(no.getText().length()==0) {
            no.setError("Enter Number Of Family Members!!");
            return false;
        }
        else
            return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(getIntent().getStringExtra("SelectedName")!=null)
            getMenuInflater().inflate(R.menu.update_contact, menu);
        else
            getMenuInflater().inflate(R.menu.add_contact, menu);
        return true;
    }

    //My Path returning function
    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    //My copying function
    private void copyFile(File sourceFile, File destFile) throws IOException {
        FileChannel source = null;
        FileChannel destination = null;
        source = new FileInputStream(sourceFile).getChannel();
        destination = new FileOutputStream(destFile).getChannel();
        if (destination != null && source != null) {
            destination.transferFrom(source, 0, source.size());
        }
        if (source != null) {
            source.close();
        }
        if (destination != null) {
            destination.close();
        }
    }

    //Convert date to string for providing a "string" filename
    public String dateToString(Date date, String format) {
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(date);
    }

}
