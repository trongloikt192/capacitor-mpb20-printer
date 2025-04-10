package com.capacitor.mpb20printer.seiko;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Custom ArrayAdapter for displaying a list of DeviceListItem objects in a ListView.
 * Extends ArrayAdapter to adapt the data items to the ListView.
 */
public class DeviceListArrayAdapter extends ArrayAdapter<DeviceListItem> {

    /** LayoutInflater to create view from XML */
    private LayoutInflater mLayoutInflater = null;
    private Context mContext;

    public DeviceListArrayAdapter(Context context, int resource){
        super(context, resource);
        // Initialize LayoutInflater
        mLayoutInflater =
                (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
    }

    @SuppressLint("InflateParams")// Suppressing warning for inflating without root view (convertView)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null && mLayoutInflater != null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }
        // Get the DeviceListItem at the given position
        final DeviceListItem item = this.getItem(position);
        if(item != null){
            // Find TextView for device name
            final TextView deviceNameTxt = (TextView) Objects.requireNonNull(convertView).findViewById(android.R.id.text1);
            // Set device name
            deviceNameTxt.setText(item.getName());
            // Find TextView for device address
            final TextView deviceAddressTxt = (TextView)convertView.findViewById(android.R.id.text2);
            // Set device address
            if(item.getIpAddress() == null || item.getIpAddress().isEmpty()) {
                deviceAddressTxt.setText(item.getMacAddress());
            } else {
                // Set device address with both MAC and IP address
                deviceAddressTxt.setText(String.format("%1$s(%2$s)", item.getMacAddress(), item.getIpAddress()));
            }

            if (isDarkModeEnabled()) {
                deviceNameTxt.setTextColor(Color.WHITE);
                deviceAddressTxt.setTextColor(Color.WHITE);
            }
        }
        // Return the converted view
        return Objects.requireNonNull(convertView);
    }

    private boolean isDarkModeEnabled() {
        int nightModeFlags = mContext.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}
