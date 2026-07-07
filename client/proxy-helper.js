/**
 * modifying package.json proxy property
 */
 const fs = require('fs');
 // read file and make object
 let content = JSON.parse(fs.readFileSync('package.json', 'utf8'));
 // edit or add property
 content.proxy = 'http://localhost:5050/';
 //write file
 fs.writeFileSync('package.json', JSON.stringify(content, null, 2));