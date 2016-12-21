package com.fayaz.uniabex;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

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
    private ListView lvItem;
    private static MainActivity mainActivity;
    private static ArrayList<Item> arrayListItem = new ArrayList<>();
    private ItemDetailsAdapter itemDetailsAdapter;
    private ArrayList<String> keysArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.addChildEventListener(childEventListener);
        mainActivity = this;

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
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
                Intent intent = new Intent(MainActivity.this, AddOrUpdateItem.class);
                intent.putExtra("Position", -1);
                startActivity(intent);
            }
        });

        itemDetailsAdapter = new ItemDetailsAdapter(MainActivity.this, arrayListItem);
        lvItem.setAdapter(itemDetailsAdapter);

        new Wait().execute();
    }

    private class Wait extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            lvItem.setVisibility(View.GONE);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                Thread.sleep(5000);
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

        String key = mDatabase.child("Items").push().getKey();
        Map<String, Object> postValues = item.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, postValues);
        mDatabase.updateChildren(childUpdates);
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
        mDatabase.child(clickedKey).setValue(item);
    }

    ChildEventListener childEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            lvItem.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
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
        itemDetailsAdapter.notifyDataSetChanged();
        lvItem.invalidate();
        progressBar.setVisibility(View.GONE);
        lvItem.setVisibility(View.VISIBLE);
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
}
