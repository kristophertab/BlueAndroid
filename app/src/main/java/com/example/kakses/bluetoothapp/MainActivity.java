package com.example.kakses.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "FragmentActivity";
    public BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1; //must be greater than 0
    private Intent BtServiceIntent;
    private MyBluetoothService mBluetoothService;
    private EditText editText;
    private TextView textView;
    private ListView listView;
    private List<BluetoothDevice> pairedDevicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Activity UI part
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        textView = (TextView) findViewById(R.id.textOutput);
        editText = (EditText) findViewById(R.id.textInput);
        listView = (ListView) findViewById(R.id.devicesList);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                byte[] message = editText.getText().toString().getBytes(Charset.defaultCharset());
                mBluetoothService.write(message);
            }
        });

        //Bluetooth part
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        //null if Bluetooth is not supported on this hardware platform
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Your Device does't support bluetooth, sorry", Toast.LENGTH_LONG).show();
        }
        //Enable bluetooth if necessary
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        //paired Devices (in many containers)
        Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
        pairedDevicesList = new ArrayList<BluetoothDevice>();
        List<String> pairedDevicesListNames = new ArrayList<String>();
        pairedDevicesList.addAll(pairedDevicesSet);
        for(BluetoothDevice dev : pairedDevicesList){
            pairedDevicesListNames.add(dev.getName());
        }

        //List of paired device, click to connect
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String> (this, android.R.layout.simple_list_item_1, pairedDevicesListNames);
        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mBluetoothService.start_client(pairedDevicesList.get(position));
                textView.setText(getString(R.string.app_txtOutput2));
            }
        });


        //bluetooth Service init and start
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // process incoming messages here
                // this will run in the thread, which instantiates it
                String message = (String)msg.obj;
                textView.setText(message);
            }
        };

        mBluetoothService = new MyBluetoothService(this, handler);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the a        Log.d(TAG, "connected, Socket Type:" + socketType);ction bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBluetoothService.start_server();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBluetoothService.onDestroy();
    }
}
