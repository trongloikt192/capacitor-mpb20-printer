import { WebPlugin } from '@capacitor/core';

import {
  PrintImageOptions,
  Mpb20PrinterPlugin,
  PrintTextOptions,
  StatusPrinterOptions,
  BluetoothDevices,
} from './definitions';

export class Mpb20PrinterWeb extends WebPlugin implements Mpb20PrinterPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    return options;
  }

  // @ts-ignore
  printText(options: PrintTextOptions): Promise<any> {}

  // @ts-ignore
  printImage(options: PrintImageOptions): Promise<any> {}

  // @ts-ignore
  listenPrinters(): Promise<{ devices: BluetoothDevices[] }> {}

  openBluetoothSettings(): any {}

  enableBluetooth(): any {}

  // @ts-ignore
  getStatusPrinter(options: StatusPrinterOptions): Promise<any> {}

  // @ts-ignore
  connectPrinter(options: { macAddress: string }): Promise<any> {}

  disconnectPrinter(): any {}

  // @ts-ignore
  getCurrentPrinter(): Promise<{ name: string; macAddress: string }>
}
