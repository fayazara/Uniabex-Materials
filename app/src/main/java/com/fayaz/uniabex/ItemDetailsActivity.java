package com.fayaz.uniabex;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemDetailsActivity extends AppCompatActivity {
    private TextView tvItemDetailName,tvItemDetailPono,tvItemdetailQty,
            tvItemDetailSupplier,tvItemDetailContact,tvItemDetailTransporter,tvItemDetailLrno,tvItemDetailRemarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);



        tvItemDetailName= (TextView) findViewById(R.id.tvItemDetailsName);
        tvItemDetailPono= (TextView) findViewById(R.id.tvItemDetailsPono);
        tvItemdetailQty= (TextView) findViewById(R.id.tvItemDetailsQty);
        tvItemDetailSupplier= (TextView) findViewById(R.id.tvItemDetailsSupplier);
        tvItemDetailContact= (TextView) findViewById(R.id.tvItemDetailsContact);
        tvItemDetailTransporter= (TextView) findViewById(R.id.tvItemDetailsTransport);
        tvItemDetailLrno= (TextView) findViewById(R.id.tvItemDetailsLrno);
        tvItemDetailRemarks= (TextView) findViewById(R.id.tvItemDetailsRemarks);

        int position = getIntent().getIntExtra("Position", -1);
        searchPerson(position);
    }

    public void searchPerson(int position) {
        String clickedKey = MainActivity.getInstance().getKeysArray().get(position);
        MainActivity.getInstance().getmDatabase().child(clickedKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        Item itemDetailsModel = dataSnapshot.getValue(Item.class);
                        tvItemDetailName.setText(itemDetailsModel.getItem());
                        tvItemDetailPono.setText(itemDetailsModel.getPonum());
                        tvItemdetailQty.setText(String.valueOf(itemDetailsModel.getQty()));
                        tvItemDetailSupplier.setText(itemDetailsModel.getSupplier());
                        tvItemDetailContact.setText(itemDetailsModel.getContact());
                        tvItemDetailTransporter.setText(itemDetailsModel.getTransporter());
                        tvItemDetailLrno.setText(itemDetailsModel.getLrnum());
                        tvItemDetailRemarks.setText(itemDetailsModel.getRemarks());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}