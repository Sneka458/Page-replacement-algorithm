const express = require("express");
const { exec } = require("child_process");
const path = require("path");
const fs = require("fs");
const app = express();

app.use(express.json());
// Serve the frontend UI from the public directory
app.use(express.static("public"));

app.post("/api/run", (req, res) => {
    const { algorithm, frames, referenceString } = req.body;

    let algo_type = 1;
    if (algorithm === 'lru') algo_type = 2;
    if (algorithm === 'optimal') algo_type = 3;

    const numFrames = frames;
    const numPages = referenceString.length;

    // Prepare arguments for Algo.java
    let args = [algo_type, numFrames, numPages, ...referenceString].join(" ");
    const command = `java Algo ${args}`;

    // Execute the compiled Java program
    exec(command, { cwd: __dirname }, (err, stdout, stderr) => {
        if (err) {
            console.error(stderr || err);
            return res.status(500).json({ error: "Error running Java program. Make sure 'javac Algo.java' was run." });
        }
        try {
            const parsed = JSON.parse(stdout);
            res.json(parsed);
        } catch (e) {
            res.status(500).json({ error: "Invalid output from Java program", output: stdout });
        }
    });
});

app.get("/", (req, res) => {
   res.sendFile(path.join(__dirname, "public", "index.html"));
});

app.listen(3000, () => {
    console.log("Server running at http://localhost:3000");
});