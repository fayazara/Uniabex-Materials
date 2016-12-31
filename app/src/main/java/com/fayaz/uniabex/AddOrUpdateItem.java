package com.fayaz.uniabex;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddOrUpdateItem extends AppCompatActivity {

    Button bOK,bCancel;
    Item item;
    int position;
    EditText iName,iPono,iQty,iSupplier,iContact,iTranport,iLrno,iRemarks;
    CoordinatorLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_update_item);

        position = getIntent().getIntExtra("Position", -1);

        cl = (CoordinatorLayout) findViewById(R.id.cdlayout);

         iName= (EditText) findViewById(R.id.iName);
        iPono = (EditText) findViewById(R.id.iPoNo);
        iQty = (EditText) findViewById(R.id.iQty);
        iSupplier = (EditText) findViewById(R.id.iSupplier);
        iContact = (EditText) findViewById(R.id.iContact);
        iTranport = (EditText) findViewById(R.id.iTransport);
        iLrno = (EditText) findViewById(R.id.iLrno);
        iRemarks = (EditText) findViewById(R.id.iRemarks);
        String test = iRemarks.getText().toString();

        bOK = (Button) findViewById(R.id.bOk);
        bCancel = (Button) findViewById(R.id.bCancel);

        if(position != -1) {
            getSupportActionBar().setTitle("Edit Entry");
            searchPerson(position);
            item = new Item();
        }
        else {
            getSupportActionBar().setTitle("Add Entry");
            item = null;
        }

        bOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    String test = iRemarks.getText().toString();
                    Item i = new Item();
                    i.setItem(iName.getText().toString());
                    i.setPonum(iPono.getText().toString());
                    i.setQty(iQty.getText().toString());
                    i.setSupplier(iSupplier.getText().toString());
                    i.setContact(iContact.getText().toString());
                    i.setTransporter(iTranport.getText().toString());
                    i.setLrnum(iQty.getText().toString());
                    if(test.equals("\n")){
                        i.setRemarks(iRemarks.getText().toString());
                    }
                    else{
                        i.setRemarks(iRemarks.getText().toString() + "\n");
                    }

                    if (item == null)
                        MainActivity.getInstance().addItem(i);
                    else
                        MainActivity.getInstance().updateItemDetails(i, position);
                    finish();
            }
        });

        bCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void searchPerson(int position) {
        String clickedKey = MainActivity.getInstance().getKeysArray().get(position);
        MainActivity.getInstance().getmDatabase().child(clickedKey).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        String test = iRemarks.getText().toString();
                        Item person = dataSnapshot.getValue(Item.class);
                        iName.setText(person.getItem());
                        iPono.setText(person.getPonum());
                        iSupplier.setText(person.getSupplier());
                        iQty.setText(person.getQty());
                        iTranport.setText(person.getTransporter());
                        iContact.setText(person.getContact());
                        iLrno.setText(person.getLrnum());
                        if(test.equals("\n")){
                            iRemarks.setText(person.getRemarks());
                        }
                        else{
                            iRemarks.setText(person.getRemarks() + "\n");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }
}
