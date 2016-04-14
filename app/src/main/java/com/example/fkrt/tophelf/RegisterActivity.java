package com.example.fkrt.tophelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class RegisterActivity extends AppCompatActivity {

    private Intent intent;

    private EditText name, surname, phone, email, password, repassword;
    private Button register, selectImage;
    private Bitmap image;
    private static final int SELECTED_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        name = (EditText) findViewById(R.id.name);
        surname = (EditText) findViewById(R.id.surname);
        phone = (EditText) findViewById(R.id.phonenumber);
        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        repassword = (EditText) findViewById(R.id.repassword);

        register = (Button) findViewById(R.id.register);
        selectImage = (Button) findViewById(R.id.selectImage);
    }

    // select an profile image
    public void onClick(View v) {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, SELECTED_IMAGE);
    }

    // register now
    public void onClick2(View v) throws ExecutionException, InterruptedException {

        if(name.getText().toString().equals("") || surname.getText().toString().equals("") ||
                phone.getText().toString().equals("") || email.getText().toString().equals("") ||
                password.getText().toString().equals("") || repassword.getText().toString().equals("")
                || image == null) {

            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Please fill all boxes and select an profile image!");
            alertDialog.setIcon(R.drawable.tophelf);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

        } else if(!password.getText().toString().equals(repassword.getText().toString())) {
            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Passwords not matching!");
            alertDialog.setIcon(R.drawable.tophelf);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

        } else {
            boolean b = new Registerconn().execute(name.getText().toString() + " " + surname.getText().toString(),
                    phone.getText().toString(), email.getText().toString(),
                    password.getText().toString(), image).get();
            if(!b) {
                AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                alertDialog.setTitle("Warning!");
                alertDialog.setMessage("Entered email is a already registered");
                alertDialog.setIcon(R.drawable.tophelf);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();

            }
        }
        //intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    // check privacy policy
    public void onClick3(View v) {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SELECTED_IMAGE:
                if(resultCode == RESULT_OK && requestCode == SELECTED_IMAGE && data != null) {
                    Uri uri = data.getData();
                    ImageView profileImage = (ImageView) findViewById(R.id.profileImage);
                    profileImage.setImageURI(uri);
                    image = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
 
                    /*String[] projection = {MediaStore.Images.Media.DATA};
 
                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();
 
                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String imagePath = cursor.getString(columnIndex);
                    cursor.close();
 
                    Bitmap bitmapImage = BitmapFactory.decodeFile(imagePath);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                    byte[] b = byteArrayOutputStream.toByteArray();
                    encodedImage = Base64.encodeToString(b, Base64.DEFAULT);*/
                }
                break;
        }

    }

    class Registerconn extends AsyncTask<Object, Void, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            String username = (String) params[0];
            String phone = (String) params[1];
            String email = (String) params[2];
            String password = (String) params[3];
            Bitmap image = (Bitmap) params[4];

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 10, byteArrayOutputStream);
            String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            try {
                URL url = new URL("http://139.179.211.124:3000"); // 192.168.1.24 --- 10.0.2.2
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "Register");
                jsonParam.put("username", username);
                jsonParam.put("phone", phone);
                jsonParam.put("email", email);
                jsonParam.put("pass", password);
                jsonParam.put("image", encodedImage);
                jsonParam.put("rating", 0);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                writer.flush();
                writer.close();
                os.close();

                int statusCode = conn.getResponseCode();
                InputStream is = null;

                if (statusCode >= 200 && statusCode < 400) {
                    // zzzzzzzz
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    return true;
                }
                else {
                    is = conn.getErrorStream();
                }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}