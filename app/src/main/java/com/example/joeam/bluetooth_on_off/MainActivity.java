package com.example.joeam.bluetooth_on_off;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
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
    TextView t1,t2;
    int temp = 0;
    String address = null , name=null;
    private static final String TAG = "MyActivity";

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
        try {bluetooth_connect_device();}
        catch (Exception e){}


    i1.setOnClickListener(new View.OnClickListener()

    {
        @Override
        public void onClick (View view)
        {
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
                t2.setText("data" + temp);
                Log.i(TAG, "current value of i: " + i.toString().getBytes());
                Log.i(TAG, "current value of temp: " + temp);
            }

        }
        catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();

        }

    }

    }


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





