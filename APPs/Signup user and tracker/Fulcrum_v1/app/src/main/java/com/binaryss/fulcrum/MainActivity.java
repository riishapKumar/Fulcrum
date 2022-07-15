package com.binaryss.fulcrum;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.amplifyframework.api.aws.GsonVariablesSerializer;
import com.amplifyframework.api.graphql.GraphQLRequest;
import com.amplifyframework.api.graphql.SimpleGraphQLRequest;
import com.amplifyframework.auth.cognito.AWSCognitoAuthSession;
import com.amplifyframework.core.Amplify;
import com.binaryss.fulcrum.Auth.Login;
import com.binaryss.fulcrum.Auth.Session;
import com.binaryss.fulcrum.Auth.UserSessionManager;
import com.binaryss.fulcrum.Utils.GlobalVariables;
import com.binaryss.fulcrum.Utils.LoadingDialog;
import com.binaryss.fulcrum.Utils.MyCustomMethods;
import com.binaryss.fulcrum.Utils.SharedHelper;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.binaryss.fulcrum.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    Context context = MainActivity.this;
    SharedHelper sharedHelper;
    UserSessionManager userSessionManager;
    Session session;
    LoadingDialog loadingDialog;
    TextView txtName;
    Button btnOne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        InitViews();
        txtName = findViewById(R.id.textview_name);
        btnOne = findViewById(R.id.button_first);
        btnOne.setOnClickListener(view -> {
            startActivity(new Intent(context, LaptopActivity.class));
            //fetchData();
        });
        binding.txtLogout.setOnClickListener(view -> {
            SignOut();
        });

    }
    private void SignOut(){
        Amplify.Auth.signOut(
                () -> {
                    loadingDialog.dismiss();
                    sharedHelper.clearStoreInfoData();
                    userSessionManager.setLoggedIn(false);
                    userSessionManager.clearSessionData();
                    Intent intent = new Intent(this, Login.class);
                    this.startActivity(intent);
                    this.finishAffinity();

                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, error.toString());
                    MyCustomMethods.showSimpleToast(this, error.toString());
                }
        );
    }

    private void InitViews() {
        loadingDialog = new LoadingDialog(context);
        userSessionManager = new UserSessionManager(context);
        sharedHelper = new SharedHelper(context);
        if (userSessionManager.isLoggedIn()) {
            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_LONG).show();
            fetchUserSession();
        } else {
            startActivity(new Intent(context, Login.class));
            finish();
        }
    }

    private void fetchUserSession() {
        loadingDialog.show();
        Amplify.Auth.fetchAuthSession(
                result -> {
                    AWSCognitoAuthSession cognitoAuthSession = (AWSCognitoAuthSession) result;
                    switch (cognitoAuthSession.getIdentityId().getType()) {
                        case SUCCESS:
                            Log.i(TAG, "IdentityId: " + cognitoAuthSession.getIdentityId().getValue() + "UserName: " + cognitoAuthSession.getUserSub());
                            Amplify.Auth.fetchUserAttributes(
                                    attributes -> {
                                        Log.d(TAG, "fetchSession: " + attributes.toString());
                                        String email = "", id = "", name = "", type = "";

                                        for (int i = 0; i < attributes.size(); i++) {
                                            switch (attributes.get(i).getKey().getKeyString()) {
                                                case "email":
                                                    email = attributes.get(i).getValue();
                                                    break;
                                                case "name":
                                                    name = attributes.get(i).getValue();
                                                    break;
                                                case "profile":
                                                    type = attributes.get(i).getValue();
                                                    break;
                                                case "sub":
                                                    id = attributes.get(i).getValue();
                                                    break;
                                            }
                                        }
                                        Log.i(TAG, "User attributes id= " + id);
                                        Log.i(TAG, "User attributes name= " + name);
                                        Log.i(TAG, "User attributes email= " + email);
                                        Log.i(TAG, "User attributes type= " + type);
                                        session = new Session(id, email, name, type);
                                        userSessionManager.setSessionDetails(session);
                                        userSessionManager.setLoggedIn(true);
                                        runOnUiThread(() -> txtName.setText(session.getName()));
                                        loadingDialog.dismiss();
                                    },
                                    error -> {
                                        loadingDialog.dismiss();
                                        Log.e(TAG, "Failed to fetch user attributes.", error);
                                        loginActivity();
                                    }
                            );
                            break;
                        case FAILURE:
                            loadingDialog.dismiss();
                            Log.i(TAG, "IdentityId not present because: " + cognitoAuthSession.getIdentityId().getError().toString());
                            loginActivity();
                    }
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.e(TAG, "fetchUserSession error: ", error);
                    loginActivity();
                }
        );
    }

    private void loginActivity() {
        loadingDialog.dismiss();
        sharedHelper.clearStoreInfoData();
        userSessionManager.setLoggedIn(false);
        userSessionManager.clearSessionData();
        Intent intent = new Intent(context, Login.class);
        startActivity(intent);
    }

    private void fetchData(){
        loadingDialog.show();
        Amplify.API.mutate(fetchDataGraphQL(), response -> {
            loadingDialog.dismiss();
                    Log.d(TAG, "fetchData: " + response);
                    Log.d(TAG, "fetchData > Data: " + response.getData());
                    try {
                        if (response.getData() != null) {
                            JSONObject dataObject = new JSONObject(response.getData());
                            Log.d(TAG, "fetchData > dataObject: " + dataObject.getString("fetchData"));
                            JSONObject dataFetchDataObject = new JSONObject(dataObject.getString("fetchData"));
                            JSONObject dataFetchDataBodyObject = new JSONObject(dataFetchDataObject.getString("body"));
                            JSONArray items = new JSONArray(dataFetchDataBodyObject.getString("Items"));
                            Log.d(TAG, items.length() + " ");

                        }else{
                            Log.d(TAG, "fetchData > jsonObject: Unable to get data!");
                        }
                    }catch (Exception ex){
                        Log.d(TAG, "fetchData > Exception: " + ex.getMessage());
                    }
                },
                error -> {
                    loadingDialog.dismiss();
                    Log.d(TAG, "fetchData: " + error.toString());
                });
    }

    public static GraphQLRequest<String> fetchDataGraphQL() {

        String document = "query fetchData { "
                + "fetchData"
                + "}";
        Log.d(TAG, "fetchData Request = " + document.toString());
        return new SimpleGraphQLRequest<>(
                document,
                String.class,
                new GsonVariablesSerializer());
    }
}