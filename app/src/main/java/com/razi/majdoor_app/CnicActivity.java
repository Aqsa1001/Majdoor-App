package com.razi.majdoor_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

public class CnicActivity extends AppCompatActivity {
    EditText cnicNo;
    Button doneBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cnic);

        cnicNo = findViewById(R.id.cnicNo);
        doneBtn = findViewById(R.id.doneBtn);

        //Did Only CNIC Verification Work
        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cnicNo.getText().toString().isEmpty()) {
                    cnicNo.setError("Can't Be Empty!");
                    cnicNo.requestFocus();
                    return;
                } else {
                    String temp = cnicNo.getText().toString();
                    String url = "https://cute-snaps-toad.cyclic.app/person/" + temp;
                    RequestQueue requestQueue;
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                            null, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                Boolean temp = response.getBoolean("criminalStatus");
                                if (temp == true) {
                                    Toast.makeText(CnicActivity.this, "Its Criminal!", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Toast.makeText(CnicActivity.this, "Not Criminal", Toast.LENGTH_SHORT).show();
                            } catch (JSONException e) {
                                Log.i("Error:", "In Response Catch");
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                                // URL not found
                                Toast.makeText(CnicActivity.this, "Please Check CNIC. Person Not Found Against this CNIC", Toast.LENGTH_SHORT).show();
                            } else {
                                // Other network errors
                                error.printStackTrace();
                            }
                        }
                    });
                    requestQueue.add(jsonObjectRequest);
                }
            }
        });
    }
}
