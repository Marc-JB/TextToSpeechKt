import { instantiate } from './compose-multiplatform.uninstantiated.mjs';

await wasmSetup;

instantiate({ skia: Module['asm'] });
