package com.fayaz.uniabex;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.MailTo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ItemDetailsActivity extends AppCompatActivity {
    private TextView tvItemDetailName, tvItemDetailPono, tvItemdetailQty,
            tvItemDetailSupplier, tvItemDetailContact, tvItemDetailTransporter, tvItemDetailLrno, tvItemDetailRemarks;
    private ImageView callSupplier;
    public String phone;
    private Context context;
    private static  int position;
    public int MY_PERMISSIONS_REQUEST_CALL_PHONE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_details);

        callSupplier = (ImageView) findViewById(R.id.call);
        context = getApplicationContext();

        tvItemDetailName = (TextView) findViewById(R.id.tvItemDetailsName);
        tvItemDetailPono = (TextView) findViewById(R.id.tvItemDetailsPono);
        tvItemdetailQty = (TextView) findViewById(R.id.tvItemDetailsQty);
        tvItemDetailSupplier = (TextView) findViewById(R.id.tvItemDetailsSupplier);
        tvItemDetailContact = (TextView) findViewById(R.id.tvItemDetailsContact);
        tvItemDetailTransporter = (TextView) findViewById(R.id.tvItemDetailsTransport);
        tvItemDetailLrno = (TextView) findViewById(R.id.tvItemDetailsLrno);
        tvItemDetailRemarks = (TextView) findViewById(R.id.tvItemDetailsRemarks);

        position = getIntent().getIntExtra("Position", -1);
        searchPerson(position);
    }

    public static void ShowConfirmDialog(Context context, final int position) {

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private boolean checkPermission() {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= Build.VERSION_CODES.M) ;
        {
            if (ActivityCompat.checkSelfPermission(ItemDetailsActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale(ItemDetailsActivity.this, android.Manifest.permission.CALL_PHONE)) {
                    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission Necessary");
                    alertBuilder.setMessage("Call Phone Permission is necessary to Call the Supplier");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ItemDetailsActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                            alertBuilder.show();
                        }
                    });
                } else {
                    ActivityCompat.requestPermissions(ItemDetailsActivity.this, new String[]{android.Manifest.permission.CALL_PHONE}, MY_PERMISSIONS_REQUEST_CALL_PHONE);
                }
                return false;
            } else {
                return true;
            }
        }
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
                        phone = tvItemDetailContact.getText().toString();
                        tvItemDetailTransporter.setText(itemDetailsModel.getTransporter());
                        tvItemDetailLrno.setText(itemDetailsModel.getLrnum());
                        tvItemDetailRemarks.setText(itemDetailsModel.getRemarks());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.call) {
            boolean result = checkPermission();

            if (result) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + phone));
                startActivity(callIntent);
            }
        }

        if (id == R.id.editmenu){
            Intent intent = new Intent(context,AddOrUpdateItem.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Position", position);
            context.getApplicationContext().startActivity(intent);
        }

        if(id == R.id.deletemenu){

            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder
                    .setMessage("Are you sure you want to delete this entry?")
                    .setCancelable(true)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MainActivity.getInstance().deleteItem(position);
                            finish();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
        return super.onOptionsItemSelected(item);
    }

}