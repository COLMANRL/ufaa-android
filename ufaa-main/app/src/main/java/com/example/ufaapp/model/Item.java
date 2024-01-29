package com.example.ufaapp.model;

public class Item {
    private String name;
    private String myidnumber;
    private String holder;
    private String box;
    private String amount;
    private String status;
    private boolean isSelected = false; // Default to false

    // Constructor with parameters for name, holder, and amount
    public Item(String name,String myidnumber, String holder, String amount,String box,String status) {
        this.name = name;
        this.myidnumber = myidnumber;
        this.holder = holder;
        this.amount = amount;
        this.box = box;
        this.status = status;
        this.isSelected = false; // Initialize isSelected to false
    }

    // Getters and Setters for name, holder, amount, and isSelected
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getBox() {
        return box;
    }

    public void setBox(String box) {
        this.box = box;
    }
    public String getMyidnumber() {
        return myidnumber;
    }

    public void setMyidnumber(String status) {
        this.myidnumber = myidnumber;
    }

    // ... include other methods or fields if needed ...
}
