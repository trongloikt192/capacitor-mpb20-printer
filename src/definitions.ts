export interface MkPrinterPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;

  /**
   * You can send data in ZPL Zebra Programing Language
   * @param options
   * @returns returns a promise
   */
  printText(options: PrintTextOptions): Promise<any>

  /**
   * Get ZPL equivalent code from the base64 Image string
   * @param options
   * @returns returns a promise
   */
  printImage(options: PrintImageOptions): Promise<any>

  /**
   * Discover bonded devices
   * @returns returns a promise
   */
  listenPrinters(): Promise<{ devices: BluetoothDevices[] }>

  /**
   * Show the Bluetooth settings on the device
   * @returns returns a promise
   */
  openBluetoothSettings(): Promise<any>

  /**
   * Enable Bluetooth on the device
   * @returns returns a promise
   */
  enableBluetooth(): Promise<any>

  /**
   * You can get a status response from a connected Zebra printer using
   * @param options
   * @returns returns a promise
   */
  getStatusPrinter(options: StatusPrinterOptions): Promise<{ status: string }>

  /**
   * Connect to a printer
   * @param options
   * @returns returns a promise
   */
  connectPrinter(options: { macAddress: string }): Promise<any>;

  /**
   * Disconnect from a printer
   * @returns returns a promise
   */
  disconnectPrinter(): Promise<any>;

  /**
   * Get the current connected printer
   * @returns returns a promise
   */
  getCurrentPrinter(): Promise<{ name: string, macAddress: string }>;
}

interface RowOption {
  text: string,
  fontSize?: number,
  fontAlign?: string,
  fontUnderline?: boolean
}

export interface PrintTextOptions {
  rows: RowOption[]
}

export interface PrintImageOptions {
  base64Data: string,
}

export interface BluetoothDevices {
  /**
   * Name of the remote device
   */
  name: string,
  /**
   * Identifier of the remote device
   */
  macAddress: string,
  /**
   *
   */
  id: string,
  /**
   *
   */
  class?: string
}

export interface StatusPrinterOptions {
  /**
   * Identifier of the remote device
   */
  macAddress: string
}

export declare enum StatusPrinter {
  IS_READY_TO_PRINT = "Printer is ready for use",
  IS_PAUSED = "Printer is currently paused",
  IS_PAPER_OUT = "Printer is out of paper",
  IS_HEAD_OPEN = "Printer head is open",
  UNKNOWN_ERROR = "Cannot print, unknown error"
}