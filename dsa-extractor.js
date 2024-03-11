const path = require('path');
const fs = require('fs');

function findTextBetweenComments(inputString) {
    const regex = /\/\*(.*?)\*\//gs;
    const matches = [];
    let match;

    while ((match = regex.exec(inputString)) !== null) {
        if (!match[1].includes('pseudo') && !match[1].includes('@param')) {
            matches.push(match[1].trim());
        } else {
            // console.log(match[1].trim())
        }
    }

    return matches;
}

const directoryPath = './P4';

fs.readdir(directoryPath, (err, files) => {
    if (err) {
        console.error('Error reading directory:', err);
        return;
    }
    let filesProcessed = 0
    const problems = []
    const promise = new Promise((res, rej) => {
        files.forEach(file => {
            if (path.extname(file) === '.js') {
                const filePath = path.join(directoryPath, file);
                fs.readFile(filePath, 'utf8', (err, data) => {
                    if (err) {
                        console.error('Error reading file:', err);
                        return;
                    }
                    filesProcessed++
                    const textBetweenComments = findTextBetweenComments(data);
                    problems.push(...textBetweenComments)
                    if (filesProcessed === 7) {
                        res(problems)
                    }
                    // console.log(`File: ${file}`);
                    // console.log('Text between comments:', textBetweenComments);
                });
            }
        });
    })
    promise.then(data => {
        const outputFilePath = path.join(__dirname, 'dsa-problems.js');
        const jsonData = JSON.stringify(data)
        fs.appendFile(outputFilePath, jsonData, err => {
            if (err) {
                console.error('Error appending to file:', err);
            } else {
                console.log('Data has been appended to', outputFilePath);
            }
        });
    })
});

