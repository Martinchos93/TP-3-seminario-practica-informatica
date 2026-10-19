import { chromium } from 'playwright';
import { readFileSync } from 'node:fs';
import { dirname, resolve } from 'node:path';
import { fileURLToPath } from 'node:url';

const DOCS = resolve(dirname(fileURLToPath(import.meta.url)));
const capturas = JSON.parse(readFileSync(process.argv[2], 'utf8'));

const escapar = (t) => t.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
const colorear = (linea) => {
  const l = escapar(linea);
  if (l.startsWith('[OK]')) return `<span class="ok">${l}</span>`;
  if (l.startsWith('[ERROR]')) return `<span class="error">${l}</span>`;
  if (l.startsWith('---') || l.startsWith('====')) return `<span class="titulo">${l}</span>`;
  return l;
};

const navegador = await chromium.launch();
const pagina = await navegador.newPage({ deviceScaleFactor: 2 });
for (const [nombre, texto] of Object.entries(capturas)) {
  const cuerpo = texto.split('\n').map(colorear).join('\n');
  await pagina.setContent(`<!DOCTYPE html><html><head><meta charset="UTF-8"><style>
    body { margin: 0; background: #ffffff; display: inline-block; }
    .ventana { margin: 0; border-radius: 8px; overflow: hidden; background: #1e1e1e;
               width: max-content; min-width: 640px; }
    .barra { background: #3a3a3a; padding: 7px 10px; display: flex; gap: 7px; align-items: center;
             font: 12px -apple-system, Helvetica, sans-serif; color: #cfcfcf; }
    .barra i { width: 11px; height: 11px; border-radius: 50%; display: inline-block; }
    .barra span { margin-left: 12px; }
    pre { margin: 0; padding: 12px 16px; color: #e6e6e6;
          font: 13px/1.38 Menlo, Consolas, monospace; }
    .ok { color: #7ee787; } .error { color: #ff8b80; } .titulo { color: #79c0ff; }
  </style></head><body><div class="ventana"><div class="barra">
    <i style="background:#ff5f57"></i><i style="background:#febc2e"></i><i style="background:#28c840"></i>
    <span>tribu-consola — java -jar tribu-consola.jar</span></div><pre>${cuerpo}</pre></div></body></html>`);
  await pagina.locator('.ventana').screenshot({ path: resolve(DOCS, 'capturas', `${nombre}.png`) });
  console.log(nombre);
}
await navegador.close();
