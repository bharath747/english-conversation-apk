const fs = require('fs');
const path = require('path');
const out = 'www';
fs.rmSync(out, { recursive: true, force: true });
fs.mkdirSync(out, { recursive: true });
for (const file of ['index.html','styles.css','app.js','manifest.json','service-worker.js']) {
  fs.copyFileSync(file, path.join(out, file));
}
console.log('PWA built into www/');
