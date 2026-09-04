import express from "express";
import cors from "cors";
import fs from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const __dirname = path.dirname(fileURLToPath(import.meta.url));
const logDir = path.join(__dirname, "logs");
const logFile = path.join(logDir, "server.log");
fs.mkdirSync(logDir, { recursive: true });

const app = express();
app.use(cors({ origin: "http://localhost:5173" }));

app.use((req, res, next) => {
  res.on("finish", () => {
    const base = `${new Date().toISOString()} ${req.method} ${req.originalUrl} -> ${res.statusCode}`;
    const line = res.locals.errorMessage ? `${base} ERROR: ${res.locals.errorMessage}\n` : `${base}\n`;
    fs.appendFile(logFile, line, () => {});
  });
  next();
});

app.get("/hello", (req, res) => {
  const text = req.query.text ?? "";
  res.json({ message: `Hello ${text}` });
});

app.get("/div", (req, res) => {
  const a = Number(req.query.a);
  const b = Number(req.query.b);
  if (b === 0) {
    throw new Error(`Division by zero: ${a} / ${b}`);
  }
  res.json({ result: a / b });
});

app.use((err, req, res, next) => {
  console.error(err);
  res.locals.errorMessage = err.message;
  res.status(500).json({ error: err.message });
});

const PORT = 3001;
app.listen(PORT, () => {
  console.log(`Backend listening on http://localhost:${PORT}`);
});
