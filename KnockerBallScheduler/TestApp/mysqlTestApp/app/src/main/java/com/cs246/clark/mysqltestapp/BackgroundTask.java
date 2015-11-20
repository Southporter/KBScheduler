package com.cs246.clark.mysqltestapp;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Observable;
import java.util.Observer;

/***********************************************************************
 *
 * 10/26/2015
 *
 * @author Weston Clark, Shem Sedrick, Jared Mefford
 * @version 1.0
 **********************************************************************/
public class BackgroundTask extends AsyncTask<String, String, String> implements Observer {

    User user;
    String method;

    BackgroundTask(User _user, String _method){
        user   = _user;
        method = _method;
    }

    @Override
    public void update(Observable observable, Object data){

    }

    @Override
    protected void onPreExecute(){
        //we don't need to do anything here...
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String...params) {

        //directs to the register php file
        String login = "";
        String data  = "";

        if(method.equals("register")) {
            login = "http://96.18.168.42:80/register_user.php";
        } else if(method.equals("login")){
            login = "http://96.18.168.42:80/verify_login.php";
        }

        //try opening the connection to the server
        try {
            //set up the connection
            URL url = new URL(login);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(10000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("POST");
            connection.setDoInput (true);
            connection.setDoOutput(true);

            if(method.equals("register")) {
                //write & encode the data to be sent via the "POST" method
                 data = URLEncoder.encode("first_name", "UTF-8")+ "=" + URLEncoder.encode(user.getFirstName(), "UTF-8")+ "&" +
                        URLEncoder.encode("last_name", "UTF-8") + "=" + URLEncoder.encode(user.getLastName(), "UTF-8") + "&" +
                        URLEncoder.encode("password", "UTF-8")  + "=" + URLEncoder.encode(user.getPassword(), "UTF-8") + "&" +
                        URLEncoder.encode("email", "UTF-8")     + "=" + URLEncoder.encode(user.getEmail(), "UTF-8")    + "&" +
                        URLEncoder.encode("phone", "UTF-8")     + "=" + URLEncoder.encode(user.getPhone(), "UTF-8");
            } else if(method.equals("login")){
                 data = URLEncoder.encode("email","UTF-8")      + "=" + URLEncoder.encode(user.getEmail(), "UTF-8")    + "&" +
                        URLEncoder.encode("password", "UTF-8")  + "=" + URLEncoder.encode(user.getPassword(), "UTF-8");
            }

            //write the data to the stream and close up shop
            OutputStream out = connection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, "UTF-8"));
            writer.write(data);
            writer.flush();
            writer.close();
            out.close();

            connection.connect();

            //used to verify we got the right response back from the server
            int responseCode = connection.getResponseCode();
            System.out.println(responseCode);
            if(responseCode != 200){
                return "Failed to connect to the server...";
            }

            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "iso-8859-1"));

            String line;
            String response = "";
            while((line = reader.readLine()) != null){
                response+= line;
            }
            reader.close();
            in.close();
            connection.disconnect();

            System.out.println(response);

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        //this should never ever happen, so if it does..please alert the local authorities
        return null;
    }


    @Override
    protected void onPostExecute(String result){
        //display a confirmation message as Toast when we're done

    }
}