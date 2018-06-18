package com.example.kakses.bluetoothapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SearchBtDevice extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.myfirstapp.MESSAGE";


    private Button list;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ListView lv;
    private MyBluetoothService mBluetoothService;
    private List<BluetoothDevice> pairedDevicesList;
    List<String> pairedDevicesListAddress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bt_device);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        lv = (ListView) findViewById(R.id.lv);
        setSupportActionBar(toolbar);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        list();
    }


    public void list() {
        Toast.makeText(getApplicationContext(), "Showing Paired Devices", Toast.LENGTH_SHORT).show();
        //paired Devices (in many containers)
        Set<BluetoothDevice> pairedDevicesSet = mBluetoothAdapter.getBondedDevices();
        pairedDevicesList = new ArrayList<BluetoothDevice>();
        List<String> pairedDevicesListNames = new ArrayList<String>();
        pairedDevicesListAddress = new ArrayList<String>();
        pairedDevicesList.addAll(pairedDevicesSet);
        for (BluetoothDevice dev : pairedDevicesList) {
            pairedDevicesListNames.add(dev.getName());
            pairedDevicesListAddress.add(dev.getAddress());
        }

        //List of paired device, click to connect
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pairedDevicesListNames);
        lv.setAdapter(arrayAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ConnectedActivity.class);
                intent.putExtras(getIntent().getExtras()); //pass the file path
                String message = pairedDevicesListAddress.get(position);
                intent.putExtra(EXTRA_MESSAGE, message);
                startActivity(intent);
            }
        });
    }
}
