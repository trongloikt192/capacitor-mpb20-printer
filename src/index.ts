import { registerPlugin } from '@capacitor/core';

import type { Mpb20PrinterPlugin } from './definitions';

const Mpb20Printer = registerPlugin<Mpb20PrinterPlugin>('Mpb20Printer', {
  web: () => import('./web').then(m => new m.Mpb20PrinterWeb()),
});

export * from './definitions';
export { Mpb20Printer };
