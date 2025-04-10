# Capacitor MK Printer

A Capacitor plugin for MP-B20 printer integration.

## Installation

```bash
npm install capacitor-mpb20-printer
npx cap sync
```

## API

<docgen-index>

* [`listenPrinters()`](#listenprinters)
* [`connectPrinter(...)`](#connectprinter)
* [`getCurrentPrinter()`](#getcurrentprinter)
* [`printImage(...)`](#printimage)
* [Interfaces](#interfaces)

</docgen-index>

<docgen-api>

### listenPrinters()

```typescript
listenPrinters() => Promise<{ devices: BluetoothDevice[] }>
```

List available printers that can be connected.

**Returns:** <code>Promise&lt;{ devices: BluetoothDevice[] }&gt;</code>

--------------------

### connectPrinter(...)

```typescript
connectPrinter(options: { macAddress: string }) => Promise<any>
```

Connect to a printer.

| Param         | Type                                 | Description                    |
| ------------- | ------------------------------------ | ------------------------------ |
| **`options`** | <code>{ macAddress: string }</code> | MAC address of the printer to connect |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------

### getCurrentPrinter()

```typescript
getCurrentPrinter() => Promise<{ name: string; macAddress: string }>
```

Get information about the currently connected printer.

**Returns:** <code>Promise&lt;{ name: string; macAddress: string }&gt;</code>

--------------------

### printImage(...)

```typescript
printImage(options: PrintImageOptions) => Promise<any>
```

Print an image.

| Param         | Type                                                          | Description                |
| ------------- | ------------------------------------------------------------- | -------------------------- |
| **`options`** | <code><a href="#printimageoptions">PrintImageOptions</a></code> | Image printing options     |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------

### Interfaces

#### BluetoothDevice

| Property          | Type                | Description                        |
| ----------------- | ------------------- | ---------------------------------- |
| **`name`**        | <code>string</code> | Device name                        |
| **`macAddress`**  | <code>string</code> | Device MAC address                 |
| **`type`**        | <code>string</code> | Device type (optional)             |

#### PrintImageOptions

| Property         | Type                | Description  |
|------------------| ------------------- |--------------|
| **`base64Data`** | <code>string</code> | image base64 |

</docgen-api>

## Permissions

### Android

Add these permissions to your `AndroidManifest.xml` file:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### iOS

Add these permission descriptions to your `Info.plist` file:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>This app needs Bluetooth access to connect to printers</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>This app needs Bluetooth access to connect to printers</string>
```

## Usage Example

```typescript
import { Mpb20Printer } from 'capacitor-mpb20-printer';

// List available printers
async function scanPrinters() {
  try {
    const result = await Mpb20Printer.listenPrinters();
    console.log('Available printers:', result.devices);
  } catch (error) {
    console.error('Error scanning printers:', error);
  }
}

// Connect to a printer
async function connect(macAddress: string) {
  try {
    await Mpb20Printer.connectPrinter({ macAddress });
    console.log('Connected successfully');
  } catch (error) {
    console.error('Connection failed:', error);
  }
}

// Get current printer information
async function getCurrentPrinter() {
  try {
    const printer = await Mpb20Printer.getCurrentPrinter();
    console.log('Current printer:', printer);
  } catch (error) {
    console.error('Error getting current printer:', error);
  }
}

// Print an image
async function printImage(base64Data: string) {
  try {
    await Mpb20Printer.printImage({ base64Data });
    console.log('Image printed successfully');
  } catch (error) {
    console.error('Error printing image:', error);
  }
}
```

## License

MIT