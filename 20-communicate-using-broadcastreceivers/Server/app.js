const express = require('express');
const fileupload = require('express-fileupload');
const app = express();
const fs = require('fs');

app.use(fileupload());

app.use('/files', express.static(__dirname + '/files'));

app.post('/files', function (req, res) {
    res.setHeader('Content-Type', 'application/json');

    if (!req.files)
        return res.status(400).send({message: "multipart/form-data with 'file' field required to upload."});

    let sampleFile = req.files.file;

    let targetPath = __dirname + '/files/' + sampleFile.name;

    sampleFile.mv(targetPath, function (err) {
        if (err)
            return res.status(500).send({message: err});

        res.status(200).send({url: "/files/" + sampleFile.name, message: 'Success!'});
    });
});

app.get("/images", function (req, res) {
    const files = fs.readdirSync(__dirname + '/files');

    const response = files.filter(file => file != '.DS_Store').map(imagePath => {
        return {imagePath}
    });

    res.json({images: response})
});

app.listen(3000, function () {
    console.log('Example app listening on port 3000!');
});