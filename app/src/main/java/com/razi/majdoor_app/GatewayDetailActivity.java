package com.razi.majdoor_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

public class GatewayDetailActivity extends AppCompatActivity {
    EditText accountNumText, amountText, codeText;
    Button submitBtn;
    String actionStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway_detail_actitvity);
        accountNumText = findViewById(R.id.accountNumText);
        amountText = findViewById(R.id.amountText);
        codeText = findViewById(R.id.codeText);
        submitBtn = findViewById(R.id.submitBtn);

        if (getIntent().hasExtra("action")) {
            actionStr = getIntent().getStringExtra("action");
        }

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checksOnFields();
                retrieveAmountFromGateway();
            }
        });
    }

    private void retrieveAmountFromGateway() {
        String temp = accountNumText.getText().toString();
        String url = "https://odd-blue-starfish-robe.cyclic.app/user/" + temp;
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Double inputAmount = Double.parseDouble(amountText.getText().toString());
                    Double amount = response.getDouble("ammount");
                    Integer code = response.getInt("code");
                    if (code == Integer.parseInt(codeText.getText().toString())) {
                        if (actionStr.equals("deposit")) {
                            if (amount < inputAmount) {
                                Toast.makeText(GatewayDetailActivity.this, "Don't Have Enough Balance In Gateway Account!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(GatewayDetailActivity.this, WallActivity.class));
                                finish();
                            } else {
                                putRequestOnServerAndUpdateWallet(amount, inputAmount, temp, "deposit");
                            }
                        } else {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                String userId = user.getUid();
                                DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Wallet").child("ammount");
                                walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        Double firebaseAmount = snapshot.getValue(Double.class);
                                        if (inputAmount > firebaseAmount) {
                                            Toast.makeText(GatewayDetailActivity.this, "Don't Have Enough Balance In App Wallet!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(GatewayDetailActivity.this, WallActivity.class));
                                            finish();
                                        } else {
                                            putRequestOnServerAndUpdateWallet(amount, inputAmount, temp, "withdrawal");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("Firebase", "Failed to retrieve amount from Firebase", error.toException());
                                    }
                                });
                            }
                        }
                    } else {
                        Toast.makeText(GatewayDetailActivity.this, "Wrong Password", Toast.LENGTH_SHORT).show();
                        return;
                    }

                } catch (JSONException e) {
                    Log.i("Error:", "In Response Catch");
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error.networkResponse != null && error.networkResponse.statusCode == 404) {
                    Log.i("Error", "Server Error");
                } else {
                    // Other network errors
                    error.printStackTrace();
                }
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private void putRequestOnServerAndUpdateWallet(Double getAmount, Double inputAmount, String accountNumber, String transactionType) {
        RequestQueue requestQueue;
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        Double remainingAmount;
        if (transactionType.equals("deposit")) {
            remainingAmount = getAmount - inputAmount;
        } else {
            remainingAmount = getAmount + inputAmount;
        }

        // Update the amount in the API server using PUT request
        String updateUrl = "https://odd-blue-starfish-robe.cyclic.app/update-user/" + accountNumber;
        JSONObject updateData = new JSONObject();
        try {
            updateData.put("ammount", remainingAmount);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest updateRequest = new JsonObjectRequest(Request.Method.PUT, updateUrl, updateData,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Response", response.toString());

                        // Update the user's wallet in Firebase Realtime Database
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            DatabaseReference walletRef = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("Wallet").child("ammount");
                            walletRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Double previousAmount = snapshot.getValue(Double.class);
                                    Double newAmount;
                                    if (transactionType.equals("deposit")) {
                                        newAmount = previousAmount + inputAmount;
                                    } else {
                                        newAmount = previousAmount - inputAmount;
                                    }
                                    walletRef.setValue(newAmount);
                                    Log.i("Firebase", "Amount successfully updated in Firebase");
                                    Toast.makeText(GatewayDetailActivity.this, "Transaction successful!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(GatewayDetailActivity.this, WallActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Failed to retrieve amount from Firebase", error.toException());
                                }
                            });
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        requestQueue.add(updateRequest);
    }

    private void checksOnFields() {
        // Check if any field is empty
        if (accountNumText.getText().toString().isEmpty() || amountText.getText().toString().isEmpty() || codeText.getText().toString().isEmpty()) {
            // Show error message or toast as per your requirement
            Toast.makeText(GatewayDetailActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check the account number length and format
        if (accountNumText.getText().toString().length() != 12 || !accountNumText.getText().toString().startsWith("92") || !TextUtils.isDigitsOnly(accountNumText.getText().toString())) {
            // Show error message or toast as per your requirement
            Toast.makeText(GatewayDetailActivity.this, "Please enter a valid account number", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check the amount field for numeric value
        if (!TextUtils.isDigitsOnly(amountText.getText().toString())) {
            // Show error message or toast as per your requirement
            Toast.makeText(GatewayDetailActivity.this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check the code field for numeric value and length
        if (!TextUtils.isDigitsOnly(codeText.getText().toString()) || codeText.getText().toString().length() != 4) {
            // Show error message or toast as per your requirement
            Toast.makeText(GatewayDetailActivity.this, "Please enter a valid code", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if amount is numeric and greater than 10
        try {
            float amountVal = Float.parseFloat(amountText.getText().toString());
            if (amountVal < 10) {
                Toast.makeText(GatewayDetailActivity.this, "Amount should be greater than 10", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(GatewayDetailActivity.this, "Amount should be a number", Toast.LENGTH_SHORT).show();
            return;
        }
    }
}