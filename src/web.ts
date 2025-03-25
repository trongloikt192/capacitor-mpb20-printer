import { WebPlugin } from '@capacitor/core';

import {
  PrintImageOptions,
  MkPrinterPlugin,
  PrintTextOptions,
  StatusPrinterOptions,
  BluetoothDevices,
} from './definitions';

export class MkPrinterWeb extends WebPlugin implements MkPrinterPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
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
