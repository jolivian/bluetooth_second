package com.example.joeam.bluetooth_on_off;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.icu.util.Output;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    Button i1,i2;
    TextView t1,t2,txtString, txtStringLength;
    int temp = 0;
    byte temp2;
    OutputStream temp3;
    String address = null , name=null;
    private static final String TAG = "MyActivity";
    Handler bluetoothIn;
    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();

    BluetoothAdapter myBluetooth = null;
    BluetoothSocket btSocket = null;
    Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //try {setw();} catch (Exception e) {}
        t1 = (TextView) findViewById(R.id.textView1);
        t2 = (TextView) findViewById(R.id.textView2);
        i1 = (Button) findViewById(R.id.button1);
        i2 = (Button) findViewById(R.id.button2);
        txtString = (TextView) findViewById(R.id.textView3);
        txtStringLength = (TextView) findViewById(R.id.textView4);
        try {bluetooth_connect_device();}
        catch (Exception e){}

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {										//if message is what we want
                    String readMessage = (String) msg.obj;                                                                // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);      								//keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        txtString.setText("Data Received = " + dataInPrint);
                        int dataLength = dataInPrint.length();							//get length of data received
                        txtStringLength.setText("String Length = " + String.valueOf(dataLength));

                        if (recDataString.charAt(0) == '#')								//if it starts with # we know it is what we are looking for
                        {
//                            String sensor0 = recDataString.substring(1, 5);             //get sensor value from string between indices 1-5
//                            String sensor1 = recDataString.substring(6, 10);            //same again...
//                            String sensor2 = recDataString.substring(11, 15);
//                            String sensor3 = recDataString.substring(16, 20);
//
//                            sensorView0.setText(" Sensor 0 Voltage = " + sensor0 + "V");	//update the textviews with sensor values
//                            sensorView1.setText(" Sensor 1 Voltage = " + sensor1 + "V");
//                            sensorView2.setText(" Sensor 2 Voltage = " + sensor2 + "V");
//                            sensorView3.setText(" Sensor 3 Voltage = " + sensor3 + "V");
                            String sensor = recDataString.toString();
                            txtString.setText("Values received: " +   sensor);
                            Log.i(TAG, "value received " + sensor);
                        }
                        recDataString.delete(0, recDataString.length()); 					//clear all string data

                        // strIncom =" ";
                        dataInPrint = " ";
                    }
                }
            }
        };

         myBluetooth = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();


        i1.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view)
        {
            checkBTState();
            try {
                //bluetooth_connect_device();
                led_on_off("b");
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }

        }
    });

        i2.setOnClickListener(new View.OnClickListener()

        {

            @Override
            public void onClick (View view)
            {
                checkBTState();
                try {
                    //bluetooth_connect_device();
                    led_on_off("f");
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            }
        });
    }
    private void bluetooth_connect_device() throws IOException
    {
        try
        {
            myBluetooth = BluetoothAdapter.getDefaultAdapter();
            address = myBluetooth.getAddress();
            pairedDevices = myBluetooth.getBondedDevices();
            if (pairedDevices.size()>0)
            {
                for(BluetoothDevice bt : pairedDevices)
                {
                    address=bt.getAddress().toString();name = bt.getName().toString();
                    Toast.makeText(getApplicationContext(),"Connected", Toast.LENGTH_SHORT).show();

                }
            }

        }
        catch(Exception we){}
        myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
        BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
        btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
        btSocket.connect();
        try { t1.setText("BT Name: "+name+"\nBT Address: "+address ); }
        catch(Exception e){}

    }

    public void run()
    {
        for (; ; ) {
            try {
                int bytesAvailable = btSocket.getInputStream().available();

                byte[] packetBytes = new byte[bytesAvailable];
                if (bytesAvailable > 0) {
                    txtString.setText(bytesAvailable + "ok");
                    btSocket.getInputStream().read(packetBytes);

                    for (int i = 0; i < bytesAvailable; i++) {
                        if (packetBytes[i] == 65)
                            txtString.setText("ON");
                        else if (packetBytes[i] == 90)
                            txtString.setText("off");
                    }
                }
                Log.i(TAG, "current value of string: " + packetBytes.toString().getBytes());
            }catch (Exception e )
            {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onPause()
    {
        super.onPause();
        try
        {
            //Don't leave Bluetooth sockets open when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //insert code to deal with this
        }
    }



    private void led_on_off(String i)
    {
        try
        {
            if (btSocket!=null)
            {
                if (i == "f")
                {
                    temp = 0;
                } else if (i == "b")
                {
                    temp = 1;
                }
                btSocket.getOutputStream().write(temp);

                //btSocket.getInputStream().read(temp2);
                t2.setText("data " + temp);
                Log.i(TAG, "current value of i: " + i.toString().getBytes());
                Log.i(TAG, "current value of temp: " + temp);
            }

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }
    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(myUUID);
        //creates secure outgoing connecetion with BT device using UUID
    }

    private void checkBTState() {

        if(myBluetooth==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (myBluetooth.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }
}


    /*

    NOTE: CONNECT Bluetooth module before starting app. then it will work by sending 1 and 0.
    */

   /* @SuppressLint("ClickableViewAccessibility")
    private void setw() throws IOException
    {
        t1=(TextView)findViewById(R.id.textView1);
        bluetooth_connect_device();



        i1=(Button)findViewById(R.id.button1);

        i1.setOnClickListener(new View.OnClickListener()
        {   @Override
        public boolean onClick(View v, MotionEvent event){
            if(event.getAction() == MotionEvent.ACTION_DOWN) {led_on_off("f");}
            if(event.getAction() == MotionEvent.ACTION_UP){led_on_off("b");}
            return true;}
        });

    }
*/





