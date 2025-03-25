# Capacitor MK Printer

A Capacitor plugin for MK printer integration.

## Installation

```bash
npm install capacitor-mk-printer
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

Liệt kê danh sách máy in có thể kết nối.

**Returns:** <code>Promise&lt;{ devices: BluetoothDevice[] }&gt;</code>

--------------------

### connectPrinter(...)

```typescript
connectPrinter(options: { macAddress: string }) => Promise<any>
```

Kết nối tới máy in.

| Param         | Type                                 | Description                    |
| ------------- | ------------------------------------ | ------------------------------ |
| **`options`** | <code>{ macAddress: string }</code> | Địa chỉ MAC của máy in cần kết nối |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------

### getCurrentPrinter()

```typescript
getCurrentPrinter() => Promise<{ name: string; macAddress: string }>
```

Lấy thông tin máy in hiện đang kết nối.

**Returns:** <code>Promise&lt;{ name: string; macAddress: string }&gt;</code>

--------------------

### printImage(...)

```typescript
printImage(options: PrintImageOptions) => Promise<any>
```

Thực hiện in hình ảnh.

| Param         | Type                                                          | Description                |
| ------------- | ------------------------------------------------------------- | -------------------------- |
| **`options`** | <code><a href="#printimageoptions">PrintImageOptions</a></code> | Tùy chọn in hình ảnh       |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------

### Interfaces

#### BluetoothDevice

| Property          | Type                | Description                        |
| ----------------- | ------------------- | ---------------------------------- |
| **`name`**        | <code>string</code> | Tên thiết bị                       |
| **`macAddress`**  | <code>string</code> | Địa chỉ MAC của thiết bị           |
| **`type`**        | <code>string</code> | Loại thiết bị (tùy chọn)           |

#### PrintImageOptions

| Property        | Type                | Description                        |
| --------------- | ------------------- | ---------------------------------- |
| **`filePath`**  | <code>string</code> | Đường dẫn đến file hình ảnh cần in |

</docgen-api>

## Permissions

### Android

Thêm các quyền sau vào file `AndroidManifest.xml` của bạn:

```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### iOS

Thêm các mô tả quyền sau vào file `Info.plist` của bạn:

```xml
<key>NSBluetoothAlwaysUsageDescription</key>
<string>Ứng dụng cần quyền truy cập Bluetooth để kết nối với máy in</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>Ứng dụng cần quyền truy cập Bluetooth để kết nối với máy in</string>
```

## Usage Example

```typescript
import { MkPrinter } from 'capacitor-mk-printer';

// Liệt kê các máy in có sẵn
async function scanPrinters() {
  try {
    const result = await MkPrinter.listenPrinters();
    console.log('Available printers:', result.devices);
  } catch (error) {
    console.error('Error scanning printers:', error);
  }
}

// Kết nối với máy in
async function connect(macAddress: string) {
  try {
    await MkPrinter.connectPrinter({ macAddress });
    console.log('Connected successfully');
  } catch (error) {
    console.error('Connection failed:', error);
  }
}

// Lấy thông tin máy in hiện tại
async function getCurrentPrinter() {
  try {
    const printer = await MkPrinter.getCurrentPrinter();
    console.log('Current printer:', printer);
  } catch (error) {
    console.error('Error getting current printer:', error);
  }
}

// In hình ảnh
async function printImage(filePath: string) {
  try {
    await MkPrinter.printImage({ filePath });
    console.log('Image printed successfully');
  } catch (error) {
    console.error('Error printing image:', error);
  }
}
```

## License

MIT