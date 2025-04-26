#!/bin/bash

echo "🔵 Starting Docker containers (Kafka, Redis, MySQL)..."
docker-compose up -d

echo "🟠 Compiling Java project..."
ant -f router-service/build.xml clean compile

echo "🟢 Starting Router Service (HTTP server)..."
nohup ant -f router-service/build.xml run > logs/router-service.out 2>&1 &

sleep 5

echo "🟣 Starting Kafka Consumer..."
nohup ant -f router-service/build.xml run-consumer > logs/consumer-service.out 2>&1 &

echo "✅ All services started! You can now send HTTP requests to http://localhost:8080/log"
