const hash = require('crypto');
const fs = require('fs');
const glob = require('glob');

function generateChecksum(str) {
  return hash
  .createHash('md5')
  .update(str, 'utf8')
  .digest('hex');
}

const result = {};

glob.sync(`src/assets/i18n/**/*.json`).forEach(path => {
  const [, lang] = path.split('src/assets/i18n/');
  const content = fs.readFileSync(path, { encoding: 'utf-8' });
  result[lang.replace('.json', '')] = generateChecksum(content);
});

fs.writeFileSync('./src/i18n-cache-busting.json', JSON.stringify(result));
