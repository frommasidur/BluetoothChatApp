package com.mrs.btchat_masidur;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class RFComm extends AppCompatActivity {
    private static TextView textView;
    private static Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rfcomm);

        textView = findViewById(R.id.textView_Text);
        button = findViewById(R.id.btn);
        textView.setText("Masidur");
        textView.setVisibility(View.VISIBLE);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RFCommServer rfCommServer = new RFCommServer();
                rfCommServer.run();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume() {
        super.onResume();
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static class RFCommServer extends Thread{

        private static final String TAG = "MyTag";

        //based on java.util.UUID
        private UUID MY_UUID = UUID.fromString("446118f0-8b1e-11e2-9e96-0800200c9a66");

        // The local server socket
        private BluetoothServerSocket mmServerSocket;

        // based on android.bluetooth.BluetoothAdapter
        private BluetoothAdapter mAdapter;
        private BluetoothDevice remoteDevice;

        private Activity activity;

        public RFCommServer() {
        }

        @SuppressLint("SetTextI18n")
        public void run() {
            BluetoothSocket socket = null;
            mAdapter = BluetoothAdapter.getDefaultAdapter();
            Log.d(TAG,"In run");
            textView.setText("R");
            // Listen to the server socket if we're not connected
            while (true) {
                Log.d(TAG,"Loop");
                textView.setText("Initializing RFCOMM...");
                try {
                    // Create a new listening server socket
                    Log.d(TAG, ".....Initializing RFCOMM SERVER....");

                    // MY_UUID is the UUID you want to use for communication
                    mmServerSocket = mAdapter.listenUsingRfcommWithServiceRecord("MyService", MY_UUID);
                    //mmServerSocket = mAdapter.listenUsingInsecureRfcommWithServiceRecord(NAME, MY_UUID); // you can also try using In Secure connection...

                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();

                } catch (Exception e) {
                    Log.d(TAG,e.toString());
                }

                try {
                    Log.d(TAG, "Closing Server Socket.....");
                    mmServerSocket.close();

                    InputStream tmpIn = null;
                    OutputStream tmpOut = null;

                    // Get the BluetoothSocket input and output streams

                    assert socket != null;
                    tmpIn = socket.getInputStream();
                    tmpOut = socket.getOutputStream();

                    DataInputStream mmInStream = new DataInputStream(tmpIn);
                    DataOutputStream mmOutStream = new DataOutputStream(tmpOut);

                    // here you can use the Input Stream to take the string from the client whoever is connecting
                    //similarly use the output stream to send the data to the client

                    textView.setText(mmInStream.toString() + "\n"+mmOutStream.toString());

                } catch (Exception e) {
                    //catch your exception here
                    Log.d(TAG,e.toString());
                }
            }
        }

    }
}