package com.mrs.btchat_masidur;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    Button listen,buttonBt, send, listDevices;
    ListView listView;
    TextView msg_box,status,msgSendBox;
    EditText writeMsg;

    BluetoothAdapter bluetoothAdapter;
    BluetoothDevice[] btArray;

    SendReceive sendReceive;
    static String TAG="MyBT_tag";

    AffineCipher affineCipher;
    Sha sha;
    NsdManager.DiscoveryListener discoveryListener;
    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING=2;
    static final int STATE_CONNECTED=3;
    static final int STATE_CONNECTION_FAILED=4;
    static final int STATE_MESSAGE_RECEIVED=5;

    int REQUEST_ENABLE_BLUETOOTH=1;

    private static final String APP_NAME = "BTChat";
    private static final UUID MY_UUID=UUID.fromString("8ce255c0-223a-11e0-ac64-0803450c9a66");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listen= findViewById(R.id.listen);
        buttonBt = findViewById(R.id.buttonBT);
        send= findViewById(R.id.send);
        listView= findViewById(R.id.listview);
        msg_box = findViewById(R.id.msg);
        msgSendBox = findViewById(R.id.textViewMsgSend);
        status= findViewById(R.id.status);
        writeMsg= findViewById(R.id.writemsg);
        listDevices= findViewById(R.id.listDevices);

        msg_box.setVisibility(View.GONE);
        send.setVisibility(View.GONE);
        writeMsg.setVisibility(View.GONE);

        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();

//        String myStr = "Masidur";
//        String mystrH = sha.getSha1Hash(myStr) + myStr; // Merge data with hash
//        Log.d(TAG,mystrH);
//        //send mystrH -> received
//        String rh = mystrH.substring(0,40);     // Getting hash
//        Log.d(TAG,rh);
//        String rt = mystrH.substring(40);       // Getting data
//        Log.d(TAG,rt);
//        String h2 = sha.getSha1Hash(rt);
//        Log.d(TAG,h2);
//        if (rh.equals(h2))
//            Log.d(TAG,"Accepted "+rh.length());
//        else
//            Log.d(TAG,"Not accepted");

//        String str = "Hello Test";
//        String plainText = new String(str);
//        char[] cipherText = affineCipher.encryption(str.toCharArray(),41,163);
//        String toSend = new String(cipherText);
//        Log.d(TAG, "Cipher text:"+cipherText.toString()+" "+toSend);
//        char[] plainText1 = affineCipher.decryption(toSend.toCharArray(),25,93);
//        String dt = new String(plainText1);
//        Log.d(TAG, "Plain text:"+dt);
//        String data = new String(cipherText);
//        System.out.println("Plain text: "+plainText+"\nCipher Text: "+data);

        updateUIforBT(bluetoothAdapter.isEnabled());
        implementListeners();
    }

    private void updateUIforBT(boolean enabled) {
        if (enabled){
            Log.d("BTmasidur", "In updateUIforBT");
            listen.setEnabled(true);
            buttonBt.setBackgroundResource(R.drawable.ic_baseline_bluetooth_on);
            listDevices.setEnabled(true);
            status.setText("Bluetooth is on");
            listView.setEnabled(true);
        }
        else {
            listen.setEnabled(false);
            buttonBt.setBackgroundResource(R.drawable.ic_baseline_bluetooth_disabled_24);
            listDevices.setEnabled(false);
            status.setText("Click on BT icon to enable/disable");
            listView.setEnabled(false);

        }
    }

    private void implementListeners() {

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ServerClass serverClass=new ServerClass();
                serverClass.start();
            }
        });

        buttonBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!bluetoothAdapter.isEnabled()){
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn, REQUEST_ENABLE_BLUETOOTH);
                }
                else {
                    bluetoothAdapter.disable();
                    updateUIforBT(false);
                    Toast.makeText(getApplicationContext(), "Bluetooth disabled ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        listDevices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateUIforConnected(false);
                Set<BluetoothDevice> bt=bluetoothAdapter.getBondedDevices();
                String[] strings=new String[bt.size()];
                btArray=new BluetoothDevice[bt.size()];
                int index=0;

                List<HashMap<String, String>> listItems = new ArrayList<>();

                if( bt.size()>0)
                {
                    for(BluetoothDevice device : bt)
                    {
                        btArray[index]= device;
                        strings[index]=device.getName();
                        index++;

                        HashMap<String, String> resultsMap = new HashMap<>();
                        resultsMap.put("First Line", device.getName());
                        resultsMap.put("Second Line", device.getAddress());
                        listItems.add(resultsMap);
                    }
                    SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), listItems, R.layout.list_item, new String[]{"First Line", "Second Line"}, new int[]{R.id.text1, R.id.text2});
                    listView.setAdapter(adapter);
                }
                listView.setVisibility(View.VISIBLE);
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClientClass clientClass=new ClientClass(btArray[i]);
                clientClass.start();

                status.setText("Connecting");
            }
        });

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String string= String.valueOf(writeMsg.getText());
                msgSendBox.setText(writeMsg.getText().toString());
                //Encryption
                char[] cipherText = affineCipher.encryption(string.toCharArray(),41,163);
                String stringCipher = new String(cipherText);
                //Hashing
                String data = sha.getSha1Hash(stringCipher) + stringCipher; //Combine the Hash and Data
                sendReceive.write(data.getBytes());
                writeMsg.setText(null);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == REQUEST_ENABLE_BLUETOOTH)) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth enabled success ", Toast.LENGTH_SHORT).show();
                updateUIforBT(true);
            } else {
                Toast.makeText(getApplicationContext(), "Canceled ! ", Toast.LENGTH_SHORT).show();
            }
        }
    }

    Handler handler=new Handler(new Handler.Callback() {
        @SuppressLint("ResourceAsColor")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what)
            {
                case STATE_LISTENING:
                    status.setText("Listening..");
                    status.setTextColor(R.color.design_default_color_primary);
                    break;
                case STATE_CONNECTING:
                    status.setText("Connecting..");
                    status.setTextColor(R.color.teal_700);
                    buttonBt.setBackgroundResource(R.drawable.ic_baseline_bluetooth_audio_connectiong);
                    break;
                case STATE_CONNECTED:
                    status.setText("Connected");
                    status.setTextColor(R.color.green);
                    buttonBt.setBackgroundResource(R.drawable.ic_baseline_bluetooth_audio_connected);
                    updateUIforConnected(true);
                    break;
                case STATE_CONNECTION_FAILED:
                    status.setText("Connection Failed");
                    status.setTextColor(R.color.red);
                    updateUIforConnected(false);
                    break;
                case STATE_MESSAGE_RECEIVED:
                    byte[] readBuff= (byte[]) msg.obj;
                    String tempMsg=new String(readBuff,0,msg.arg1);
                    //hashing here
                    String h = tempMsg.substring(0,40);     //Extract the hash part
                    String data = tempMsg.substring(40);    //Extract the data part
                    if(h.equals(sha.getSha1Hash(data))){    //Comparing
                        char[] plainText = affineCipher.decryption(data.toCharArray(),25,93);
                        String plainString = new String(plainText);
                        msg_box.setText(plainString);
                    }else {
                        msg_box.setText("Data broken !");
                    }
                    //Decryption
                    break;
            }
            return true;
        }
    });

    public void updateUIforConnected(boolean connected){
        if (connected){
            listView.setVisibility(View.GONE);
            msg_box.setVisibility(View.VISIBLE);
            msgSendBox.setVisibility(View.VISIBLE);
            writeMsg.setVisibility(View.VISIBLE);
            send.setVisibility(View.VISIBLE);
        }
        else {
            listView.setVisibility(View.VISIBLE);
            msg_box.setVisibility(View.GONE);
            msgSendBox.setVisibility(View.GONE);
            writeMsg.setVisibility(View.GONE);
            send.setVisibility(View.GONE);
        }
    }
    private class ServerClass extends Thread
    {
        private BluetoothServerSocket serverSocket;

        public ServerClass(){
            try {
                serverSocket=bluetoothAdapter.listenUsingRfcommWithServiceRecord(APP_NAME,MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            BluetoothSocket socket=null;

            while (socket==null)
            {
                try {
                    Message message=Message.obtain();
                    message.what=STATE_LISTENING;
                    handler.sendMessage(message);

                    socket=serverSocket.accept();
                } catch (IOException e) {
                    e.printStackTrace();
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTION_FAILED;
                    handler.sendMessage(message);
                }

                if(socket!=null)
                {
                    Message message=Message.obtain();
                    message.what=STATE_CONNECTED;
                    handler.sendMessage(message);

                    sendReceive=new SendReceive(socket);
                    sendReceive.start();

                    break;
                }
            }
        }
    }

    private class ClientClass extends Thread
    {
        private BluetoothDevice device;
        private BluetoothSocket socket;

        public ClientClass (BluetoothDevice device1)
        {
            device=device1;

            try {
                socket=device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run()
        {
            try {
                socket.connect();
                Message message=Message.obtain();
                message.what=STATE_CONNECTED;
                handler.sendMessage(message);

                sendReceive=new SendReceive(socket);
                sendReceive.start();

            } catch (IOException e) {
                e.printStackTrace();
                Message message=Message.obtain();
                message.what=STATE_CONNECTION_FAILED;
                handler.sendMessage(message);
            }
        }
    }

    private class SendReceive extends Thread
    {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public SendReceive (BluetoothSocket socket)
        {
            bluetoothSocket=socket;
            InputStream tempIn=null;
            OutputStream tempOut=null;

            try {
                tempIn=bluetoothSocket.getInputStream();
                tempOut=bluetoothSocket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

            inputStream=tempIn;
            outputStream=tempOut;
        }

        public void run()
        {
            byte[] buffer=new byte[1024];
            int bytes;

            while (true)
            {
                try {
                    bytes=inputStream.read(buffer);
                    handler.obtainMessage(STATE_MESSAGE_RECEIVED,bytes,-1,buffer).sendToTarget();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void write(byte[] bytes)
        {
            try {
                outputStream.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
