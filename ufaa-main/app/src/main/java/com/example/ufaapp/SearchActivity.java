package com.example.ufaapp;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;


import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;


import com.bumptech.glide.Glide;
import com.example.ufaapp.model.Item;
import com.example.ufaapp.model.UserModel;
import com.example.ufaapp.utils.FirebaseUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;





import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.StrictMode;
import android.util.Log;


import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;


import android.widget.EditText;
import android.widget.TextView;


import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;


public class SearchActivity extends AppCompatActivity {
    private boolean isDatabaseConnected = false;
    private UserModel userModel;
    private final HashSet<Integer> selectedRows = new HashSet<>();
    private int currentPage = 0;
    private static final int CREATE_FILE = 1;
    private static final int STORAGE_PERMISSION_CODE = 100;
    ProgressBar diagProgress;
    ProgressBar diagProgress2;
    private TextView searchStatusTextView;
    private Button previousButton;
    private Button nextButton;
    private Button downloadButton;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private final List<Item> items = new ArrayList<>();
    private final List<Item> selectedItems = new ArrayList<>();
    private TextView selectedRowsTextView;
    private DrawerLayout drawerLayout;
    private ImageView saveIcon;
    private String username;
    private String idNumber;
    private ScrollView scrollView;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_search);



//
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            // User is not authenticated, redirect to login page
            getUserDetails();
             // Stop further execution of this method
        }else {
            Intent intent = new Intent(SearchActivity.this, LoginPhoneNumberActivity.class);
            startActivity(intent);
            finish(); // Close the current activity
            return;
        }
        // Initialize the buttons here
        previousButton = findViewById(R.id.previousButton);
        nextButton = findViewById(R.id.nextButton);
        downloadButton = findViewById(R.id.downloadButton);
        saveIcon = findViewById(R.id.save_icon);

        // Inside your SearchActivity.java
        ImageView imageView = findViewById(R.id.imageViewLogo4);
        //ImageView saveIcon = findViewById(R.id.save_icon);

        Glide.with(this)
                .load(R.drawable.newlogo)
                .into(imageView);
        EditText editTextSearch = findViewById(R.id.editTextSearch);
        Button searchByIdButton = findViewById(R.id.searchByIdButton);
        Button searchByNameButton = findViewById(R.id.searchByNameButton);

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty()) {
                    searchByIdButton.setVisibility(View.GONE);
                    searchByNameButton.setVisibility(View.GONE);
                } else if (TextUtils.isDigitsOnly(s)) {
                    searchByIdButton.setVisibility(View.VISIBLE);
                    searchByNameButton.setVisibility(View.GONE);
                } else {
                    searchByIdButton.setVisibility(View.GONE);
                    searchByNameButton.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

// Add onClickListeners for searchByIdButton and searchByNameButton here

        drawerLayout = findViewById(R.id.drawer_layout);
        TextView pageNumberTextView = findViewById(R.id.pageNumberTextView);
        recyclerView = findViewById(R.id.searchResultsRecyclerView);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setNestedScrollingEnabled(false);





        adapter = new ItemAdapter(items, currentPage, selectedRows, this::onItemSelectionChanged);
        recyclerView.setAdapter(adapter);

        selectedRowsTextView = findViewById(R.id.selectedRowsTextView);

        diagProgress = findViewById(R.id.progressBar1);
        diagProgress.bringToFront();

        diagProgress2 = findViewById(R.id.progressBar2);
        diagProgress2.bringToFront();


        Button profileButton = findViewById(R.id.profileButton);
        searchStatusTextView = findViewById(R.id.searchStatusTextView);
        ImageView logoutImageView = findViewById(R.id.logout_image_view);

        previousButton.setVisibility(View.GONE);
        nextButton.setVisibility(View.GONE);
        downloadButton.setVisibility(View.GONE);
        saveIcon.setVisibility(View.GONE);
        logoutImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle logout action here
                Intent intent = new Intent(SearchActivity.this, LoginPhoneNumberActivity.class);
                startActivity(intent);
                finish();
            }
        });


        nextButton.setOnClickListener(view -> {
            pageNumberTextView.setVisibility(View.VISIBLE);
            if(currentPage < 9) {
                currentPage++;
                pageNumberTextView.setText("Page " + (currentPage + 1));
                adapter = new ItemAdapter(items, currentPage, selectedRows, this::onItemSelectionChanged);
                recyclerView.setAdapter(adapter);
                // Fetch new data for this page and update adapter...
                new SearchByNameTask().execute(editTextSearch.getText().toString().trim());
            }else {
                Toast.makeText(this, "No more next pages", Toast.LENGTH_SHORT).show();
            }
        });

        previousButton.setOnClickListener(view -> {
            pageNumberTextView.setVisibility(View.VISIBLE);
            if (currentPage > 0) {
                currentPage--;
                pageNumberTextView.setText("Page " + (currentPage + 1));
                adapter = new ItemAdapter(items, currentPage, selectedRows, this::onItemSelectionChanged);
                recyclerView.setAdapter(adapter);
                // Fetch new data for this page and update adapter...
                new SearchByNameTask().execute(editTextSearch.getText().toString().trim());
            }else {
                Toast.makeText(this, "No more previous pages", Toast.LENGTH_SHORT).show();
            }
        });

        profileButton.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
                drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        // Add logic for logout button in drawer
        TextView logoutButton = drawerLayout.findViewById(R.id.logout_btn);
        logoutButton.setOnClickListener(v -> {
            // Handle logout logic here
            Intent intent = new Intent(SearchActivity.this, LoginPhoneNumberActivity.class);
            startActivity(intent);
            finish();
        });

        downloadButton.setOnClickListener(view -> {
            if (isNetworkConnected()) {
                createFile(); // User will choose the file location and name
            } else {
                Toast.makeText(SearchActivity.this, "Check your internet connectivity", Toast.LENGTH_SHORT).show();
            }
        });

        // Inside your click listener
        searchByNameButton.setOnClickListener(view -> {
            items.clear();
            selectedItems.clear();
            currentPage= 0;
            pageNumberTextView.setVisibility(View.GONE);

            String searchTerm = editTextSearch.getText().toString().trim();

            if (!searchTerm.isEmpty()) {

                new SearchByNameTask().execute(searchTerm);
            } else {
                Toast.makeText(SearchActivity.this, "Please enter a Name", Toast.LENGTH_SHORT).show();
            }
        });

        searchByIdButton.setOnClickListener(view -> {
            items.clear();
            selectedItems.clear();
            currentPage=0;
            pageNumberTextView.setVisibility(View.GONE);
            String id = editTextSearch.getText().toString().trim();
            if (id.isEmpty()) {
                Toast.makeText(SearchActivity.this, "Please enter an ID", Toast.LENGTH_SHORT).show();
                return;
            }
            new SearchByIDTask().execute(id);
        });





    }

    private void getUserDetails() {
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                userModel = task.getResult().toObject(UserModel.class);
                if (userModel != null) {
                    username = userModel.getUsername();
                    idNumber = userModel.getIdNumber();
                    // Now you can use 'username' and 'idNumber' in your PDF creation
                }
            } else {
                // Handle failure (e.g., user details not found)
                username = ""; // Set username to empty string
                idNumber = "";
            }
        });
    }

    private void createFile() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf");
        intent.putExtra(Intent.EXTRA_TITLE, "SelectedItems");
        startActivityForResult(intent, CREATE_FILE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri uri = data.getData();
                List<Item> selectedItems = getSelectedItems();
                if (!selectedItems.isEmpty()) {
                    createPdfWithContent(selectedItems, uri);
                } else {
                    Toast.makeText(this, "No rows selected", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                // Call the method to download the PDF here
            } else {
                Toast.makeText(this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private List<Item> getSelectedItems() {
        List<Item> selectedItems = new ArrayList<>();
        for (Integer globalPosition : selectedRows) {
            //int pageIndex = globalPosition / 10; // Change '10' to your page size
            int localPosition = globalPosition % 10; // Change '10' to your page size

            // Ensure the localPosition is within the current items list size
            if (localPosition < items.size()) {
                Item selectedItem = items.get(localPosition);
                selectedItems.add(selectedItem);
            }
        }
        return selectedItems;
    }

    private void updateSelectedRowsDisplay() {
        int selectedCount = selectedRows.size();
        String text = selectedCount + " row" + (selectedCount == 1 ? "" : "s") + " selected";
        selectedRowsTextView.setText(text);
    }
    private void onItemSelectionChanged(boolean isSelected, int globalPosition) {
        if (isSelected) {
            downloadButton.setVisibility(View.VISIBLE);
            selectedRows.add(globalPosition);
        } else {
            selectedRows.remove(globalPosition);
            downloadButton.setVisibility(View.INVISIBLE);
        }
        updateSelectedRowsDisplay();
    }



    private void createPdfWithContent(List<Item> selectedItems, Uri uri) {
        try {

            OutputStream outputStream = getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                throw new IOException("Failed to get output stream.");
            }

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, outputStream);
            document.open();


            // Add a logo
            // ... existing code for adding a logo ...
            try {
                Bitmap bitmap = BitmapFactory.decodeResource(SearchActivity.this.getResources(), R.raw.pdfpic);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image image = Image.getInstance(stream.toByteArray());
                image.scaleToFit(700, 500);
                image.setAlignment(Element.ALIGN_CENTER);
                document.add(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
            document.add(new Paragraph("Dear " + username, FontFactory.getFont(FontFactory.TIMES, 12)));
            //document.add(new Paragraph("ID Number: " + idNumber, FontFactory.getFont(FontFactory.TIMES, 12)));
            document.add(Chunk.NEWLINE);
            Paragraph additionalParagraph1 = new Paragraph("Register on our site to claim your assets.", FontFactory.getFont(FontFactory.TIMES, 12));
            document.add(additionalParagraph1);
            Font blueFont = FontFactory.getFont(FontFactory.TIMES, 12, BaseColor.BLUE);
            Anchor link = new Anchor("https://reunify.ufaa.go.ke/site/login", blueFont);
            link.setReference("https://reunify.ufaa.go.ke/site/login"); // Replace with your actual link
            document.add(link);

            document.add(Chunk.NEWLINE);

            Paragraph additionalParagraph2 = new Paragraph("Learn how to claim on your own!", FontFactory.getFont(FontFactory.TIMES, 12));
            document.add(additionalParagraph2);
            Anchor link2 = new Anchor("https://ufaa.go.ke/claiming-instructions/", blueFont);
            link2.setReference("https://ufaa.go.ke/claiming-instructions/"); // Replace with your actual link
            document.add(link2);


            document.add(Chunk.NEWLINE);


            // Create a table
            PdfPTable table = new PdfPTable(new float[]{1, 3, 3, 2,2}); // Adjusted column widths
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);

            // Table Header
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
            table.addCell(new PdfPCell(new Phrase("No.", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Name", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Holder", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Amount", headerFont)));
            table.addCell(new PdfPCell(new Phrase("Status", headerFont)));

            // Calculate the starting counter based on the minimum selected row number
            //int counter = 1;
            int counter = selectedRows.isEmpty() ? 1 : Collections.min(selectedRows) + 1;

            // Add selected rows
            for (Item item : selectedItems) {
                table.addCell(String.valueOf(counter++));
                table.addCell(item.getName());
                table.addCell(item.getHolder());
                table.addCell(item.getAmount());
                table.addCell(item.getStatus());
            }

            // Add the table to the document
            document.add(table);

            // Save the document
            document.close();
            writer.close();
            Toast.makeText(getApplicationContext(), "PDF created", Toast.LENGTH_SHORT).show();
            resetSelection();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void resetSelection() {
        // Iterate over all items and reset their selected status
        for (Item item : items) {
            item.setSelected(false);
        }

        // Clear the selected rows set
        selectedRows.clear();

        // Notify the adapter to refresh the UI
        adapter.notifyDataSetChanged();
    }


    private class SearchByIDTask extends AsyncTask<String, Void, List<Item>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            diagProgress.setVisibility(View.VISIBLE);
            //diagProgress2.setVisibility(View.VISIBLE);
            hideKeyboard(SearchActivity.this);
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            String id = strings[0];
            List<Item> resultList = new ArrayList<>();
            Connection connection = connectionclass();
            if (connection == null) {
                return resultList; // Empty list indicates failure.
            }
            try {
                String sqlget = "SELECT * FROM [Live-UFAA$Item] WHERE [ID Number] = '" + id + "'";
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(sqlget);
                while (rs.next()) {
                    // Assuming your ResultSet has columns named "Name", "Holder", "Amount"
                    String name = rs.getString(130);
                    String myidnumber = rs.getString(134);
                    String holder = rs.getString(169);
                    String amount = rs.getString(155);
                    String box = rs.getString(136);
                    String status = rs.getString(165);

                    double truncatedAmount = Double.parseDouble(amount);
                    String formattedAmount = String.format("%.1f", truncatedAmount);

                    resultList.add(new Item(name, myidnumber,holder, formattedAmount,box,status));
                }
                rs.close();
                st.close();
            } catch (Exception exception) {
                Log.e("Error", Objects.requireNonNull(exception.getMessage()));
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Log.e("Error", e.getMessage() != null ? e.getMessage() : "SQLException occurred with no error message.");
                }
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            diagProgress.setVisibility(View.GONE);
            //diagProgress2.setVisibility(View.GONE);
            if (result.isEmpty()) {
                Toast.makeText(SearchActivity.this, R.string.zero_results_found, Toast.LENGTH_SHORT).show();
                searchStatusTextView.setText(R.string.zero_results_foundd);
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                downloadButton.setVisibility(View.GONE);
                saveIcon.setVisibility(View.GONE);
            } else {
                Toast.makeText(SearchActivity.this, R.string.search_successful, Toast.LENGTH_SHORT).show();
                searchStatusTextView.setText(R.string.search_successful);

                for (Item item : result) {
                    int statusValue = Integer.parseInt(item.getStatus());
                    String statusString = getStatusString(statusValue);
                    item.setStatus(statusString);
                }
                items.clear();
                items.addAll(result);
                adapter.notifyDataSetChanged();
                previousButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                downloadButton.setVisibility(View.VISIBLE);
                saveIcon.setVisibility(View.VISIBLE);
            }
        }
    }
    private String getStatusString(int statusValue) {
        switch (statusValue) {
            case 1:
                return "Unclaimed";
            case 2:
                return "In Process";
            case 3:
                return "Paid";
            default:
                return "Unknown"; // Handle unexpected status values
        }
    }

    private class SearchByNameTask extends AsyncTask<String, Void, List<Item>> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            diagProgress.setVisibility(View.VISIBLE);
            //diagProgress2.setVisibility(View.VISIBLE);
            hideKeyboard(SearchActivity.this);
        }

        @Override
        protected List<Item> doInBackground(String... strings) {
            String searchTerm = strings[0];
            List<Item> resultList = new ArrayList<>();
            Connection connection = connectionclass();
            if (connection == null) {
                return resultList; // Empty list indicates failure.
            }

            String[] searchTerms = searchTerm.toLowerCase().split("\\s+");
            StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM [Live-UFAA$Item] WHERE ");

            String[] conditions = new String[searchTerms.length];
            for (int i = 0; i < searchTerms.length; i++) {
                conditions[i] = "LOWER([Name]) LIKE '%" + searchTerms[i] + "%'";
            }

            String conditionsString = String.join(" AND ", conditions);

            sqlBuilder.append(conditionsString).append(" ORDER BY [Name] OFFSET (10 * ").append(currentPage).append(") ROWS FETCH NEXT 10 ROWS ONLY");

            String sqlget = sqlBuilder.toString();

            try {
                Statement st = connection.createStatement();
                ResultSet rs = st.executeQuery(sqlget);
                while (rs.next()) {
                    String name = rs.getString(130);
                    String holder = rs.getString(169);
                    String amount = rs.getString(155);
                    String box = rs.getString(136);
                    if(box !=null && !box.isEmpty()){

                    }else{
                        box="empty";
                    }
                    String status = rs.getString(165);

                    String id = rs.getString(134);
                    String myidnumber;
                    if (id != null && !id.isEmpty()) {
                        // Extract the last 3 digits of the ID
                        int idLength = id.length();
                        String lastThreeDigits = id.substring(idLength - 3);

                        // Add two asterisks before the last 3 digits
                        myidnumber = "**" + lastThreeDigits;
                    } else {
                        myidnumber = "Empty";
                    }
//

                    double truncatedAmount = Double.parseDouble(amount);
                    String formattedAmount = String.format("%.1f", truncatedAmount);

                    resultList.add(new Item(name, myidnumber,holder, formattedAmount,box,status));
                }
                rs.close();
                st.close();
            } catch (Exception exception) {
                Log.e("Error", Objects.requireNonNull(exception.getMessage()));
            } finally {
                try {
                    connection.close();
                } catch (SQLException e) {
                    Log.e("Error", e.getMessage() != null ? e.getMessage() : "SQLException occurred with no error message.");
                }
            }
            return resultList;
        }

        @Override
        protected void onPostExecute(List<Item> result) {
            diagProgress.setVisibility(View.GONE);
            //diagProgress2.setVisibility(View.GONE);
            if (result.isEmpty()) {
                Toast.makeText(SearchActivity.this, R.string.zero_results_found, Toast.LENGTH_SHORT).show();
                searchStatusTextView.setText("Zero results found.");
                previousButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                downloadButton.setVisibility(View.GONE);
                saveIcon.setVisibility(View.GONE);

            } else {
                Toast.makeText(SearchActivity.this, R.string.search_successful, Toast.LENGTH_SHORT).show();
                searchStatusTextView.setText("Search successful.");
                for (Item item : result) {
                    int statusValue = Integer.parseInt(item.getStatus());
                    String statusString = getStatusString(statusValue);
                    item.setStatus(statusString);
                }

                items.clear();
                items.addAll(result);
                adapter.notifyDataSetChanged();


                previousButton.setVisibility(View.VISIBLE);
                nextButton.setVisibility(View.VISIBLE);
                downloadButton.setVisibility(View.VISIBLE);
                saveIcon.setVisibility(View.VISIBLE);
            }
        }
    }



    public static void hideKeyboard(Context context) {
        View view = ((SearchActivity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


private boolean isNetworkConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

    if (cm != null) {
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    return false;
}


    @SuppressLint("NewApi")
    public Connection connectionclass() {
        Connection con = null;
        String ip = getString(R.string.ip);
        String port = getString(R.string.prt);
        String username = getString(R.string.usrname);
        String password = getString(R.string.psswrd);
        String databasename = getString(R.string.dbname);
        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
        // ... existing code ...

        try {
            // ... existing code for establishing connection ...
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databaseName=" + databasename;
            DriverManager.setLoginTimeout(5);
            con = DriverManager.getConnection(connectionUrl, username, password);

            if (con != null) {
                Log.d("DatabaseConnection", "Connection to the database was successful");
                if (!isDatabaseConnected) {
                    runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Database connection successful", Toast.LENGTH_LONG).show());
                    isDatabaseConnected = true; // Update the flag
                }
            } else {
                Log.d("DatabaseConnection", "Connection to the database was unsuccessful");
                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Failed to connect to the database", Toast.LENGTH_LONG).show());
            }

            // ... rest of the existing code ...
        } catch (ClassNotFoundException e) {
            Log.e("DatabaseConnection", "Driver not found: " + e.getMessage());
            // Display an error message as a toast
            runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Database driver not found", Toast.LENGTH_LONG).show());
        } catch (SQLException e) {
            Log.e("DatabaseConnection", "SQL Exception: " + e.getMessage()); // Add this line for debugging
            if (e.getMessage() != null && e.getMessage().contains("Network error IOException: failed to connect")) {
                Log.e("DatabaseConnection", "Failed to connect: " + e.getMessage());
                // Customize the failed to connect error message
                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Failed to connect to the database. Database server might not be running.", Toast.LENGTH_LONG).show());
            } else if (e.getMessage() != null && e.getMessage().contains("Connection timed out")) {
                Log.e("DatabaseConnection", "Connection timed out: " + e.getMessage());
                // Customize the timeout error message
                runOnUiThread(() -> Toast.makeText(SearchActivity.this, "Connection timed out. Connection timed out.", Toast.LENGTH_LONG).show());
            }
        } catch (Exception e) {
            Log.e("DatabaseConnection", "Error: " + e.getMessage());
            // Display an error message as a toast
            runOnUiThread(() -> Toast.makeText(SearchActivity.this, "An error occurred while connecting to the database", Toast.LENGTH_LONG).show());
        }

        return con;
    }




}


