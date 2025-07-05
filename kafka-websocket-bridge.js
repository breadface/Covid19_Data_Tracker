const WebSocket = require('ws');
const { Kafka } = require('kafkajs');
const express = require('express');
const http = require('http');

// Kafka configuration
const kafka = new Kafka({
  clientId: 'covid19-websocket-bridge',
  brokers: [process.env.KAFKA_BROKERS || 'localhost:9092']
});

const consumer = kafka.consumer({ groupId: 'websocket-bridge-group' });

// WebSocket server
const app = express();
const server = http.createServer(app);
const wss = new WebSocket.Server({ server });

// Store connected clients
const clients = new Set();

// WebSocket connection handling
wss.on('connection', (ws) => {
  console.log('New WebSocket client connected');
  clients.add(ws);

  ws.on('close', () => {
    console.log('WebSocket client disconnected');
    clients.delete(ws);
  });

  // Send initial connection confirmation
  ws.send(JSON.stringify({
    type: 'connection',
    data: { status: 'connected', timestamp: new Date().toISOString() }
  }));
});

// Kafka consumer setup
async function startKafkaConsumer() {
  await consumer.connect();
  await consumer.subscribe({ 
    topics: ['covid19-data', 'cancer-patient-data', 'mortality-analysis', 'real-time-stats'],
    fromBeginning: false 
  });

  await consumer.run({
    eachMessage: async ({ topic, partition, message }) => {
      try {
        const data = JSON.parse(message.value.toString());
        const wsMessage = {
          type: topic,
          data: data,
          timestamp: new Date().toISOString()
        };

        // Broadcast to all connected WebSocket clients
        clients.forEach(client => {
          if (client.readyState === WebSocket.OPEN) {
            client.send(JSON.stringify(wsMessage));
          }
        });

        console.log(`Broadcasted ${topic} to ${clients.size} clients`);
      } catch (error) {
        console.error('Error processing Kafka message:', error);
      }
    },
  });
}

// Health check endpoint
app.get('/health', (req, res) => {
  res.json({ 
    status: 'healthy', 
    connectedClients: clients.size,
    timestamp: new Date().toISOString()
  });
});

// Start the server
const PORT = process.env.PORT || 8083;
server.listen(PORT, () => {
  console.log(`Kafka WebSocket Bridge running on port ${PORT}`);
  startKafkaConsumer().catch(console.error);
});

// Graceful shutdown
process.on('SIGTERM', async () => {
  console.log('Shutting down gracefully...');
  await consumer.disconnect();
  server.close();
  process.exit(0);
}); 