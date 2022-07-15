package com.binaryss.fulcrum;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.amplifyframework.api.aws.GsonVariablesSerializer;
import com.amplifyframework.api.graphql.GraphQLRequest;
import com.amplifyframework.api.graphql.SimpleGraphQLRequest;
import com.amplifyframework.core.Amplify;
import com.binaryss.fulcrum.Auth.Session;
import com.binaryss.fulcrum.Utils.LoadingDialog;
import com.binaryss.fulcrum.Utils.MyCustomMethods;
import com.binaryss.fulcrum.databinding.ActivityLaptopBinding;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LaptopActivity extends AppCompatActivity {
    private static final String TAG = "LaptopActivity";
    private ActivityLaptopBinding binding;
    LoadingDialog loadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLaptopBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadingDialog = new LoadingDialog(this);
        fetchData();
        Session session = MyCustomMethods.getSessionsDetails(this);
        Log.d(TAG, "Session == " + session.getName());
        binding.textviewName.setText(session.getName());
        binding.imageBack.setOnClickListener(v -> onBackPressed());
        binding.buttonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });
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
                            if(items.length()>0) {
                                runOnUiThread(() -> {
                                    try {
                                        binding.txtData.setText(items.get(0).toString());
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                });
                            }else{
                                binding.txtData.setText("Result count == 0");
                            }

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