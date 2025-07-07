const express = require('express');
const cors = require('cors');

const app = express();
const port = 8001;

app.use(cors());
app.use(express.json());

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ status: 'OK', server: 'Node.js', timestamp: new Date().toISOString() });
});

// Random number generator endpoint
app.get('/random', (req, res) => {
  const min = parseInt(req.query.min) || 1;
  const max = parseInt(req.query.max) || 100;
  
  if (min >= max) {
    return res.status(400).json({ error: 'Min value must be less than max value' });
  }
  
  const randomNumber = Math.floor(Math.random() * (max - min + 1)) + min;
  
  res.json({
    number: randomNumber,
    min: min,
    max: max,
    timestamp: new Date().toISOString(),
    server: 'Node.js'
  });
});

// Root endpoint
app.get('/', (req, res) => {
  res.json({ 
    message: 'Node.js Server is running!', 
    endpoints: ['/health', '/random?min=1&max=100'],
    server: 'Node.js'
  });
});

app.listen(port, '0.0.0.0', () => {
  console.log(`Node.js server running on port ${port}`);
});
