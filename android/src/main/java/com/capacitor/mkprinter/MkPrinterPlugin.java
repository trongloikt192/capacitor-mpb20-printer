package com.capacitor.mkprinter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import com.zebra.sdk.comm.BluetoothConnectionInsecure;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.graphics.internal.ZebraImageAndroid;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.SGD;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.ZebraPrinterLinkOs;
import com.zebra.sdk.printer.discovery.BluetoothDiscoverer;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;

import com.capacitor.mkprinter.goojprt.util.PrintUtils;
import com.android.print.sdk.PrinterInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Set;

@CapacitorPlugin(
        name = "MkPrinter",
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
public class MkPrinterPlugin extends Plugin implements DiscoveryHandler {
    private static final String LOG_TAG = "MkPrinterPlugin";
    private PluginCall call;
    private boolean printerFound;
    private Connection thePrinterConn;
    private PrinterStatus printerStatus;
    private ZebraPrinter printer;
    private final int MAX_PRINT_RETRIES = 1;
    private BluetoothAdapter bluetoothAdapter;

    public MkPrinterPlugin() { }

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
        String filePath = call.getString("filePath");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Looper.prepare();

                    PrinterInstance mPrinter = PrintUtils.getCurrentPrinter(getContext());
                    PrintUtils.printImage(mPrinter, filePath);
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
            PrintUtils.connectPrinter(getContext(), MACAddress);
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
        json.put("address", device.getAddress());
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
