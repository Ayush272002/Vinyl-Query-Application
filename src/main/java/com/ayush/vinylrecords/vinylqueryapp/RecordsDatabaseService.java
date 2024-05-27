package com.ayush.vinylrecords.vinylqueryapp;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
//import java.io.OutputStreamWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;

import java.net.Socket;

import java.util.StringTokenizer;

import java.sql.*;
import javax.sql.rowset.*;
    //Direct import of the classes CachedRowSet and CachedRowSetImpl will fail becuase
    //these clasess are not exported by the module. Instead, one needs to impor
    //javax.sql.rowset.* as above.



public class RecordsDatabaseService extends Thread{

    private Socket serviceSocket = null;
    private String[] requestStr  = new String[2]; //One slot for artist's name and one for recordshop's name.
    private ResultSet outcome   = null;

	//JDBC connection
    private String USERNAME = Credentials.USERNAME;
    private String PASSWORD = Credentials.PASSWORD;
    private String URL      = Credentials.URL;



    //Class constructor
    public RecordsDatabaseService(Socket aSocket){
        
		//TO BE COMPLETED
        serviceSocket = aSocket;
        this.start();
		
    }


    //Retrieve the request from the socket
    public String[] retrieveRequest()
    {
        this.requestStr[0] = ""; //For artist
        this.requestStr[1] = ""; //For recordshop
		
		String tmp = "";
        try {

			//TO BE COMPLETED
            InputStream socketStream = this.serviceSocket.getInputStream();
            InputStreamReader socketReader = new InputStreamReader(socketStream);
            StringBuffer stringBuffer = new StringBuffer();
            char x;
            while(true)
            {
                x = (char) socketReader.read();
                if(x=='#') break;
                stringBuffer.append(x);
            }
            tmp = stringBuffer.toString();
            StringTokenizer tokenizer = new StringTokenizer(tmp,";");
            if(tokenizer.countTokens() == 2)
            {
                this.requestStr[0] = tokenizer.nextToken();
                this.requestStr[1] = tokenizer.nextToken();
            }
			
         }catch(IOException e){
            System.out.println("Service thread " + this.getId() + ": " + e);
        }
        return this.requestStr;
    }


    //Parse the request command and execute the query
    public boolean attendRequest() {
        boolean flagRequestAttended = true;

        this.outcome = null;

        /*String sql = "SELECT DISTINCT ON (r.title) r.title, r.label, r.genre, r.rrp, rc.copyid " +
                "FROM record r " +
                "INNER JOIN recordcopy rc ON r.recordid = rc.recordid " +
                "INNER JOIN recordshop rs ON rc.recordshopid = rs.recordshopid " +
                "WHERE r.artistid = (SELECT artistid FROM artist WHERE lastname = ?) " +
                "AND rs.city = ? " +
                "ORDER BY r.title, rc.copyid DESC";*/

        /*String sql = "SELECT r.title, r.label, r.genre, r.rrp, COUNT(rc.copyID) AS copies_available " +
                "FROM artist a " +
                "JOIN record r ON a.artistID = r.artistID " +
                "JOIN recordcopy rc ON r.recordID = rc.recordID " +
                "JOIN recordshop rs ON rc.recordshopID = rs.recordshopID " +
                "WHERE (a.lastname = ? OR a.firstname || ' ' || a.lastname = ?) " +
                "AND rs.city = ? " +
                "GROUP BY r.title, r.label, r.genre, r.rrp";*/

        String sql = "SELECT r.title, r.label, r.genre, r.rrp, COUNT(rc.copyID) AS copies_available " +
                "FROM artist a " +
                "JOIN record r ON a.artistID = r.artistID " +
                "JOIN recordcopy rc ON r.recordID = rc.recordID " +
                "JOIN recordshop rs ON rc.recordshopID = rs.recordshopID " +
                "WHERE a.lastname = ? " +
                "AND rs.city = ? " +
                "GROUP BY r.title, r.label, r.genre, r.rrp";

        /*
        SELECT r.title, r.label, r.genre, r.rrp, COUNT(rc.copyID) AS copies_available FROM artist a JOIN record r ON a.artistID = r.artistID JOIN recordcopy rc ON r.recordID = rc.recordID JOIN recordshop rs ON rc.recordshopID = rs.recordshopID GROUP BY r.title, r.label, r.genre, r.rrp;
         */


        try {
            // Connect to the database
            DriverManager.registerDriver(new org.postgresql.Driver());
            Connection conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // Prepare the query statement
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, this.requestStr[0]); // Artist's last name or band name
            //stmt.setString(2, this.requestStr[0]); // Artist's full name or band name
            stmt.setString(2, this.requestStr[1]); // Record shop's city
            /*stmt.setString(1, this.requestStr[0]); // Artist's last name
            stmt.setString(2, this.requestStr[1]); // Record shop's city*/

            // Execute the query
            ResultSet rs = stmt.executeQuery();

            // Process the query result
            RowSetFactory factory = RowSetProvider.newFactory();
            CachedRowSet crs = factory.createCachedRowSet();
            crs.populate(rs);
            this.outcome = crs;

            // Clean up
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            System.out.println(e);
            flagRequestAttended = false;
        }

        return flagRequestAttended;
    }




    //Wrap and return service outcome
    public void returnServiceOutcome(){
        try {
			//Return outcome
			//TO BE COMPLETED
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(serviceSocket.getOutputStream());
            // If the outcome is not null
            if (this.outcome != null) {
                // Move the cursor to the beginning of the ResultSet
                this.outcome.beforeFirst();

                while (this.outcome.next()) {
                    System.out.println(this.outcome.getString("title") + " | " +
                            this.outcome.getString("label") + " | " +
                            this.outcome.getString("genre") + " | " +
                            this.outcome.getFloat("rrp") + " | " +
                            this.outcome.getInt("copies_available"));
                }

            }

            objectOutputStream.writeObject(this.outcome);


            System.out.println("Service thread " + this.getId() + ": Service outcome returned; " + this.outcome);

			//Terminating connection of the service socket
			//TO BE COMPLETED
            serviceSocket.close();
            objectOutputStream.flush();

        }catch (IOException e){
            System.out.println("Service thread " + this.getId() + ": " + e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    //The service thread run() method
    public void run()
    {
		try {
			System.out.println("\n============================================\n");
            //Retrieve the service request from the socket
            this.retrieveRequest();
            System.out.println("Service thread " + this.getId() + ": Request retrieved: "
						+ "artist->" + this.requestStr[0] + "; recordshop->" + this.requestStr[1]);

            //Attend the request
            boolean tmp = this.attendRequest();

            //Send back the outcome of the request
            if (!tmp)
                System.out.println("Service thread " + this.getId() + ": Unable to provide service.");
            this.returnServiceOutcome();

        }catch (Exception e){
            System.out.println("Service thread " + this.getId() + ": " + e);
        }
        //Terminate service thread (by exiting run() method)
        System.out.println("Service thread " + this.getId() + ": Finished service.");
    }

}
