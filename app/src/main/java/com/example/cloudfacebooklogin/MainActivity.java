package com.example.cloudfacebooklogin;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
//import com.facebook.FacebookSdk;
//import com.facebook.appevents.AppEventsLogger;

public class MainActivity extends AppCompatActivity {
    private LoginButton btnLogin;
    private TextView txtName;
    private ImageView imgHinh;
    private CallbackManager callbackManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin=findViewById(R.id.login_button);
        txtName=findViewById(R.id.txtName);
        imgHinh=findViewById(R.id.imgHinh);

        callbackManager = CallbackManager.Factory.create();
        btnLogin.setReadPermissions(Arrays.asList("user_gender","user_friends"));

        btnLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Demo","Login Successful");
                Toast.makeText(MainActivity.this,"Login Successful",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onCancel() {
                Log.d("Demo","Login Cancel");
                Toast.makeText(MainActivity.this,"Login Cancel",Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Log.d("Demo","Login Error");
                Toast.makeText(MainActivity.this,"Login Error",Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);

        GraphRequest graphRequest= GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.d("Demo",object.toString());
                        try {
                            String name= object.getString("name");
                            String id= object.getString("id");
                            txtName.setText(name);
                            Picasso.get().load("https://graph.facebook.com/" + id + "/picture?type=large")
                            .into(imgHinh);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle bundle= new Bundle();
        bundle.putString("fields","gender, name, id, first_name, last_name");
        graphRequest.setParameters(bundle);
        graphRequest.executeAsync();
    }

    AccessTokenTracker accessTokenTracker= new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if(currentAccessToken==null){
                LoginManager.getInstance().logOut();
                txtName.setText("");
                imgHinh.setImageResource(0);
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        accessTokenTracker.startTracking();
    }

    //    AccessToken accessToken = AccessToken.getCurrentAccessToken();
//    boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

}