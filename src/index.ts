import { registerPlugin } from '@capacitor/core';

import type { MkPrinterPlugin } from './definitions';

const MkPrinter = registerPlugin<MkPrinterPlugin>('MkPrinter', {
  web: () => import('./web').then(m => new m.MkPrinterWeb()),
});

export * from './definitions';
export { MkPrinter };
