package com.example.ufaapp;

import org.junit.Test;
import org.junit.Assert;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnectionUnitTest {

    @Test
    public void testDatabaseConnection() {
        // Your test code here

        Connection con = connectionclass();

        // Assert that the connection is not null
        Assert.assertNotNull("Database connection is not null", con);
    }

    private Connection connectionclass() {
        Connection con = null;
        String ip = "192.168.40.108", port = "1433", username = "adminapp", password = "Andr01d!", databasename = "UFAMS";
        StrictMode.ThreadPolicy tp = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(tp);
        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            String connectionUrl = "jdbc:jtds:sqlserver://" + ip + ":" + port + ";databasename=" + databasename + ";User=" + username + ";password=" + password + ";";
            con = DriverManager.getConnection(connectionUrl);

            // Log a success message if the connection is established
            if (con != null) {
                Log.d("DatabaseConnection", "Connection to the database was successful");
            }
        } catch (Exception exception) {
            Log.e("Error", exception.getMessage());
        }
        return con;
    }
}
