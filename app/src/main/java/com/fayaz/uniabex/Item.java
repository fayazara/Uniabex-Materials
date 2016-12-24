package com.fayaz.uniabex;

/**
 * Created by Fayaz on 21/12/2016.
 */
import java.util.HashMap;
import java.util.Map;

public class Item {

    private String item;
    private String ponum;
    private String contact;
    private String qty;
    private String supplier;
    private String transporter;
    private String lrnum;
    private String remarks;


    public Item(){
        /*Blank default constructor essential for Firebase*/
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getPonum() {
        return ponum;
    }

    public void setPonum(String ponum) {
        this.ponum = ponum;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public String getLrnum() {
        return lrnum;
    }

    public void setLrnum(String lrnum) {
        this.lrnum = lrnum;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("item", item);
        result.put("ponum", ponum);
        result.put("qty", qty);
        result.put("supplier", supplier);
        result.put("contact", contact);
        result.put("transporter", transporter);
        result.put("lrnum", lrnum);
        result.put("remarks", remarks);
        return result;
    }
}
