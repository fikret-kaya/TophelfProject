package com.example.fkrt.tophelf;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by MSI on 26.3.2016.
 */
public class HashKey extends Application {

    @Override
    public void onCreate(){
        super.onCreate();
        printHash();
    }

    public void printHash(){
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.fkrt.tophelf",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
    }
}
