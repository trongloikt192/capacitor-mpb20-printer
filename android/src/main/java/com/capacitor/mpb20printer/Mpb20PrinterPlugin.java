package com.capacitor.mpb20printer;


import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import com.capacitor.mpb20printer.seiko.Function;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import com.seikoinstruments.sdk.thermalprinter.PrinterManager;
import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

import com.capacitor.mpb20printer.goojprt.util.PrintUtils;
import com.android.print.sdk.PrinterInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;




@CapacitorPlugin(
        name = "Mpb20Printer",
        permissions = {
                @Permission(
                        alias = "bluetooth",
                        strings = {Manifest.permission.BLUETOOTH}
                ),
                @Permission(
                        alias = "bluetooth_admin",
                        strings = {Manifest.permission.BLUETOOTH_ADMIN}
                )
        }
)
public class Mpb20PrinterPlugin extends Plugin implements DiscoveryHandler {
    private static final String LOG_TAG = "Mpb20PrinterPlugin";
    private PluginCall call;
    private boolean printerFound;
    private Connection thePrinterConn;
    private BluetoothAdapter bluetoothAdapter;



    private Function mSdkFunction = null;

    protected static final int SEND_TIMEOUT_DEFAULT = 10000;
    protected static final int RECEIVE_TIMEOUT_DEFAULT = 10000;
    protected static final int SOCKET_KEEPING_TIME_DEFAULT = 300000;
    protected static final int SECURE_CONNECTION_OFF = 0;

    public Mpb20PrinterPlugin() {
        if(mSdkFunction == null) {
            mSdkFunction = new Function(getContext());
            mSdkFunction.applySettings(
                SOCKET_KEEPING_TIME_DEFAULT,
                SEND_TIMEOUT_DEFAULT,
                RECEIVE_TIMEOUT_DEFAULT,
                PrinterManager.CODE_PAGE_1252,
                PrinterManager.COUNTRY_USA,
                SECURE_CONNECTION_OFF
            );
        }
    }

    @PluginMethod
    public void printText(PluginCall call){
        try {
            String printText = call.getString("rows");

            PrinterInstance mPrinter = PrintUtils.getCurrentPrinter(getContext());
            PrintUtils.printText(mPrinter, printText);
            call.resolve();

        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void printImage(PluginCall call)  {
        String base64Data = call.getString("base64Data");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();

                    PrinterInstance mPrinter = PrintUtils.getCurrentPrinter(getContext());
//                     PrintUtils.printImage(mPrinter, base64Data);

                    String imagePath = PrintUtils.convertBase64ToFile(base64Data);
                    mSdkFunction.sendDataFileSample(imagePath);
                    PrintUtils.removeTempFile(imagePath);

                    call.resolve();

                    Looper.myLooper().quit();
                } catch (Exception e) {
                    call.reject(e.getMessage());
                }
            }
        }).start();
    }

    @PluginMethod
    public void listenPrinters(PluginCall call) throws JSONException{
        discoverPrinters(call);
    }

    @PluginMethod
    public void connectPrinter(PluginCall call){
        try {
            String MACAddress = call.getString("macAddress");
//            PrintUtils.connectPrinter(getContext(), MACAddress);

            // Perform connection
            mSdkFunction.connectSample(
                    PrinterManager.PRINTER_TYPE_BLUETOOTH,
                    PrinterManager.PRINTER_MODEL_MP_B20,
                    MACAddress
            );

            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void disconnectPrinter(PluginCall call) {
        try {
            PrintUtils.disconnectPrinter(getContext());
            call.resolve();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void getCurrentPrinter(PluginCall call) {
        try {
            JSObject res = new JSObject();
            res.put("name", PrintUtils.bluetoothDevice.getName());
            res.put("macAddress", PrintUtils.bluetoothDevice.getAddress());
            call.resolve(res);
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage());
            e.printStackTrace();
            call.reject(e.getMessage());
        }
    }

    @PluginMethod
    public void openBluetoothSettings(PluginCall call){
        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
        getActivity().startActivity(intent);
        call.resolve();
    }

    @SuppressLint("MissingPermission")
    private boolean openBluetoothConnection(String MACAddress) throws ConnectionException {

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter.isEnabled()) {
            Log.d(LOG_TAG, "Creating a bluetooth-connection for mac-address " + MACAddress);

            thePrinterConn = new BluetoothConnectionInsecure(MACAddress);

            Log.d(LOG_TAG, "Opening connection...");
            thePrinterConn.open();
            Log.d(LOG_TAG, "connection successfully opened...");

            return true;
        } else {
            Log.d(LOG_TAG, "Bluetooth is disabled...");
            call.reject("Bluetooth is not on.");
        }

        return false;
    }

    @PluginMethod
    public void enableBluetooth(PluginCall call){
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(call, intent, "enableBluetoothCallback");
    }

    @SuppressLint("MissingPermission")
    private void discoverPrinters(PluginCall call) throws JSONException  {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        JSONArray deviceList = new JSONArray();
        JSObject res = new JSObject();
        Set< BluetoothDevice > bondedDevices = bluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device: bondedDevices){
            deviceList.put(deviceToJSON(device));
        }

        Log.d(LOG_TAG, deviceList.toString());
        res.put("devices", deviceList);
        call.resolve(res);
    }

    @SuppressLint("MissingPermission")
    private JSONObject deviceToJSON(BluetoothDevice device) throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", device.getName());
        json.put("macAddress", device.getAddress());
        json.put("id", device.getAddress());
        if (device.getBluetoothClass() != null) {
            json.put("class", device.getBluetoothClass().getDeviceClass());
        }
        return json;
    }

    @Override
    public void foundPrinter(DiscoveredPrinter discoveredPrinter) {
        Log.d(LOG_TAG, "Printer found: " + discoveredPrinter.address);
        if (!printerFound) {
            printerFound = true;
            JSObject res = new JSObject();
            res.put("value", discoveredPrinter.address);
            call.resolve(res);
        }
    }

    @Override
    public void discoveryFinished() {
        Log.d(LOG_TAG, "Finished searching for printers...");
        if (!printerFound) {
            call.reject("No printer found. If this problem persists, restart the printer.");
        }
    }

    @Override
    public void discoveryError(String s) {
        Log.e(LOG_TAG, "An error occurred while searching for printers. Message: " + s);
        call.reject(s);
    }

}
