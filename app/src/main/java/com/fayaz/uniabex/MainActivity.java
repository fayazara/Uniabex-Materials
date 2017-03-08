package com.fayaz.uniabex;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;



public class MainActivity extends AppCompatActivity {

    static final String TAG = "Main Activity";
    private DatabaseReference mDatabase;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private ImageView noCon;
    private TextView txtView;
    private ListView lvItem;
    private CoordinatorLayout coordinatorLayout;
    private static MainActivity mainActivity;
    private ArrayList<Item> arrayListItem = new ArrayList<>();
    private ItemDetailsAdapter itemDetailsAdapter;
    private ArrayList<String> keysArray;
    public String recEmail = "uniabexappupdates@gmail.com";
    public String mailSubject ="";
    public String mailBody="";



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try{
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar.setTitle(R.string.app_name);
            setSupportActionBar(toolbar);




            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.addChildEventListener(childEventListener);
            mainActivity = this;

            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            txtView = (TextView) findViewById(R.id.txtLoading);
            noCon = (ImageView) findViewById(R.id.noCon);
            coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            fab = (FloatingActionButton) findViewById(R.id.fab);
            lvItem = (ListView) findViewById(R.id.ItemList);



            keysArray = new ArrayList<>();

            lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, ItemDetailsActivity.class);
                    intent.putExtra("Position", position);
                    startActivity(intent);
                }
            });



            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FirebaseCrash.log("FAB Button clicked!");
                    Intent intent = new Intent(MainActivity.this, AddOrUpdateItem.class);
                    intent.putExtra("Position", -1);
                    startActivity(intent);
                }
            });

            itemDetailsAdapter = new ItemDetailsAdapter(MainActivity.this, arrayListItem);
            lvItem.setAdapter(itemDetailsAdapter);

            new Wait().execute();




        }

        catch (Exception e){
            FirebaseCrash.report(e);
        }
    }


    private class Wait extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            txtView.setVisibility(View.VISIBLE);
            lvItem.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(3000);
            }
            catch (InterruptedException ie) {
                Log.d(TAG,ie.toString());
            }
            return(arrayListItem.size()==0);
        }

        @Override
        protected void onPostExecute(Boolean bool) {
            if(bool)
                updateView();
        }
    }

    public static MainActivity getInstance() {
        return mainActivity;
    }

    public ArrayList<String> getKeysArray() {
        return keysArray;
    }

    public DatabaseReference getmDatabase() {
        return mDatabase;
    }

    public void addItem(Item model) {
        Item item = new Item();
        item.setItem(model.getItem());
        item.setPonum(model.getPonum());
        item.setQty(model.getQty());
        item.setSupplier(model.getSupplier());
        item.setContact(model.getContact());
        item.setTransporter(model.getTransporter());
        item.setLrnum(model.getLrnum());
        item.setRemarks(model.getRemarks());
        item.setStatus(model.getStatus());

        String key = mDatabase.child("Items").push().getKey();
        Map<String, Object> postValues = item.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        mDatabase.updateChildren(childUpdates);

        mailSubject ="New Item " + model.getItem().toString() + " is added to Materials app.";
        mailBody = "Item : " + model.getItem().toString() + "\n" +
                "PO # : " + model.getPonum().toString() + "\n" +
                "Qty : " + model.getQty().toString() + "\n" +
                "Supplier : " + model.getSupplier().toString() + "\n" +
                "Contact : " + model.getContact().toString() + "\n" +
                "Transport : " + model.getTransporter().toString() + "\n" +
                "LR # : " + model.getLrnum().toString() + "\n" +
                "Remarks : " + model.getRemarks().toString();

        //Creating SendMail object
        SendMail sm = new SendMail(this, recEmail, mailSubject, mailBody);

        //Executing sendmail to send email
        sm.execute();

    }

    public void deleteItem(int position) {
        String clickedKey = keysArray.get(position);
        mDatabase.child(clickedKey).removeValue();
    }

    public void updateItemDetails(final Item model, int position) {
        String clickedKey = keysArray.get(position);
        Item item = new Item();
        item.setItem(model.getItem());
        item.setPonum(model.getPonum());
        item.setQty(model.getQty());
        item.setSupplier(model.getSupplier());
        item.setContact(model.getContact());
        item.setTransporter(model.getTransporter());
        item.setLrnum(model.getLrnum());
        item.setRemarks(model.getRemarks());
        item.setStatus(model.getStatus());
        mDatabase.child(clickedKey).setValue(item);

        mailSubject = "Item " + model.getItem().toString() + " is Updated to Materials app.";
        mailBody = "Item : " + model.getItem().toString() + "\n" +
                "PO # : " + model.getPonum().toString() + "\n" +
                "Qty : " + model.getQty().toString() + "\n" +
                "Supplier : " + model.getSupplier().toString() + "\n" +
                "Contact : " + model.getContact().toString() + "\n" +
                "Transport : " + model.getTransporter().toString() + "\n" +
                "LR # : " + model.getLrnum().toString() + "\n" +
                "Remarks : " + model.getRemarks().toString();

        //Creating SendMail object
        SendMail sm = new SendMail(this, recEmail, mailSubject, mailBody);

        //Executing sendmail to send email
        sm.execute();
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            lvItem.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            txtView.setVisibility(View.VISIBLE);
            Log.d(TAG, dataSnapshot.getKey() + ":" + dataSnapshot.getValue().toString());
            Item item = dataSnapshot.getValue(Item.class);
            arrayListItem.add(item);
            keysArray.add(dataSnapshot.getKey());
            updateView();
        }
        @Override
        public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            String changedKey = dataSnapshot.getKey();
            int changedIndex = keysArray.indexOf(changedKey);
            Item item = dataSnapshot.getValue(Item.class);
            arrayListItem.set(changedIndex,item);
            updateView();
        }
        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {
            String deletedKey = dataSnapshot.getKey();
            int removedIndex = keysArray.indexOf(deletedKey);
            keysArray.remove(removedIndex);
            arrayListItem.remove(removedIndex);
            updateView();
        }
        @Override
        public void onChildMoved(DataSnapshot dataSnapshot, String s) { }
        @Override
        public void onCancelled(DatabaseError databaseError) {
            Toast.makeText(getApplicationContext(),"Could not update.",Toast.LENGTH_SHORT).show();
            updateView();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arrayListItem.clear();
        mDatabase.removeEventListener(childEventListener);
    }


    public void updateView() {
        if(isNetworkStatusAvialable (getApplicationContext())) {
            itemDetailsAdapter.notifyDataSetChanged();
            lvItem.invalidate();
            progressBar.setVisibility(View.GONE);
            txtView.setVisibility(View.GONE);
            noCon.setVisibility(View.GONE);
            lvItem.setVisibility(View.VISIBLE);

        } else {
            noCon.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
            txtView.setVisibility(View.GONE);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "No Internet Connection", Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            updateView();
                        }
                    });

            snackbar.show();
        }

    }

    public static boolean isNetworkStatusAvialable (Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null)
        {
            NetworkInfo netInfos = connectivityManager.getActiveNetworkInfo();
            if(netInfos != null)
                if(netInfos.isConnected())
                    return true;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.settings) {
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        this.finish();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
