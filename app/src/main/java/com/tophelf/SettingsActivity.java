package com.tophelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tophelf.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class SettingsActivity extends AppCompatActivity {

    Intent intent;

    private EditText  name, phone, oldPassword, oldRepassword, newPassword, newRepassword;
    private Button update, cancel;

    private String u_id;

    public SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        setTitle("Settings");

        u_id = sharedpreferences.getString("u_id", "N/A");

        name = (EditText) findViewById(R.id.name);
        name.setText(sharedpreferences.getString("name", "N/A"));
        phone = (EditText) findViewById(R.id.phonenumber);
        phone.setText(sharedpreferences.getString("phone", "N/A"));
        oldPassword = (EditText) findViewById(R.id.oldPassword);
        oldRepassword = (EditText) findViewById(R.id.oldRepassword);
        newPassword = (EditText) findViewById(R.id.newPassword);
        newRepassword = (EditText) findViewById(R.id.newRepassword);

        update = (Button) findViewById(R.id.update);
        cancel = (Button) findViewById(R.id.cancel);

    }

    // update now
    public void onClick(View v) throws ExecutionException, InterruptedException {

        if(name.getText().toString().equals("") || phone.getText().toString().equals("") ||
                oldPassword.getText().toString().equals("") || oldRepassword.getText().toString().equals("") ||
                newPassword.getText().toString().equals("") || newRepassword.getText().toString().equals("")) {

            AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
            alertDialog.setTitle("Warning!");
            alertDialog.setMessage("Please fill all boxes!");
            alertDialog.setIcon(R.drawable.tophelf);
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            alertDialog.show();

        } else if(!oldPassword.getText().toString().equals(oldRepassword.getText().toString()) ||
                  !newPassword.getText().toString().equals(newRepassword.getText().toString())) {

            AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
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
            boolean b = new UpdateUserconn().execute(name.getText().toString(),
                    phone.getText().toString(), oldPassword.getText().toString(),
                    newPassword.getText().toString(), u_id).get();
            if(!b) {
                AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                alertDialog.setTitle("Warning!");
                alertDialog.setMessage("Invalid Password!");
                alertDialog.setIcon(R.drawable.tophelf);
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
            } else {
                Toast.makeText(getApplicationContext(), "Successfully Updated!", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();

            }
        }
        //intent = new Intent(this, MainActivity.class);
        //startActivity(intent);
    }

    // cancel now
    public void onClick2(View v) {
        intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    // server connection
    class UpdateUserconn extends AsyncTask<Object, Void, Boolean>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Object... params) {
            String username = (String) params[0];
            String phone = (String) params[1];
            String oldPassword = (String) params[2];
            String newPassword = (String) params[3];
            String u_id = (String) params[4];

            try {
                URL url = new URL("http://"+getResources().getString(R.string.ip)+":3000"); // 192.168.1.24 --- 10.0.2.2
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestProperty("Content-Type", "application/json");
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("type", "UpdateUser");
                jsonParam.put("username", username);
                jsonParam.put("phone", phone);
                jsonParam.put("old_pass", oldPassword);
                jsonParam.put("new_pass", newPassword);
                jsonParam.put("u_id", u_id);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonParam.toString()); // URLEncoder.encode(jsonParam.toString(), "UTF-8")
                writer.flush();
                writer.close();
                os.close();

                int statusCode = conn.getResponseCode();

                if (statusCode >= 200 && statusCode < 400) {

                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("name", username);
                    editor.putString("phone", phone);
                    editor.commit();

                    conn.disconnect();

                    return true;
                }
                else {
                    conn.disconnect();
                    return false;
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
