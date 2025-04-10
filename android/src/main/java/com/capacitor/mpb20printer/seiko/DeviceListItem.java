package com.capacitor.mpb20printer.seiko;

/**
 * Represents a device item containing information such as name, MAC address, and IP address.
 */
public class DeviceListItem {

    /**
     * Constructor for creating a DeviceListItem.
     * @param deviceName The name of the device.
     * @param deviceAddress The MAC address of the device.
     * @param ipAddress The IP address of the device.
     */
    public DeviceListItem(String deviceName, String deviceAddress, String ipAddress){
        mDeviceName = deviceName;
        mDeviceMacAddress = deviceAddress;
        mDeviceIpAddress = ipAddress;
    }

    /** The name of the device. */
    private String mDeviceName = "";

    /** The MAC address of the device. */
    private String mDeviceMacAddress = "";

    /** The IP address of the device. */
    private String mDeviceIpAddress = "";

    public String getName() {
        return mDeviceName;
    }

    public String getMacAddress() {
        return mDeviceMacAddress;
    }

    public String getIpAddress() {
        return mDeviceIpAddress;
    }
}
