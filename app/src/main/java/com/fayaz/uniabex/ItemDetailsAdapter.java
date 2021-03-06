package com.fayaz.uniabex;

/**
 * Created by Fayaz on 21/12/2016.
 */

        import android.widget.BaseAdapter;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.support.v7.app.AlertDialog;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.TextView;

        import com.google.firebase.crash.FirebaseCrash;

        import java.util.ArrayList;

public class ItemDetailsAdapter extends BaseAdapter {

    private ArrayList<Item> arrayListItem;
    private Context context;
    private LayoutInflater inflater;

    public String curStatus;

    public ItemDetailsAdapter(Context context, ArrayList<Item> arrayListItem) {
        this.context = context;
        this.arrayListItem = arrayListItem;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return arrayListItem.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayListItem.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View v = convertView;
        Holder holder;
        if (v == null) {
            v = inflater.inflate(R.layout.list_item, null);
            holder = new Holder();
            holder.ItemName = (TextView) v.findViewById(R.id.ItemNametv);
            holder.qty = (TextView) v.findViewById(R.id.qtytv);
            holder.curStatus = (TextView) v.findViewById(R.id.stattv);
            holder.EditItem = (Button) v.findViewById(R.id.EditItem);
            holder.DeleteItem = (Button) v.findViewById(R.id.DeleteItem);

            v.setTag(holder);
        } else {
            holder = (Holder) v.getTag();
        }

        holder.ItemName.setText("Item: " + arrayListItem.get(position).getItem());
        holder.qty.setText("Qty: " + arrayListItem.get(position).getQty());
        holder.curStatus.setText("Status: " + arrayListItem.get(position).getStatus());
        holder.EditItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,AddOrUpdateItem.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Position", position);
                context.getApplicationContext().startActivity(intent);
            }
        });
        holder.DeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowConfirmDialog(context, position);
            }
        });
        return v;
    }

    class Holder {
        TextView ItemName,qty, curStatus;
        Button DeleteItem, EditItem;
    }

    public static void ShowConfirmDialog(Context context, final int position) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder
                .setMessage("Are you sure you want to delete this entry?")
                .setCancelable(true)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        MainActivity.getInstance().deleteItem(position);
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
}