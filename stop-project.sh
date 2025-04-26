#!/bin/bash

echo "🔴 Stopping Docker containers (Kafka, Redis, MySQL)..."
docker-compose down

echo "🛑 Stopping Router Service and Kafka Consumer..."

# Kill background Java processes started by 'nohup ant run' and 'nohup ant run-consumer'
# Only target the router-service Ant processes

ps aux | grep '[a]nt -f router-service/build.xml run' | awk '{print $2}' | xargs -r kill -9
ps aux | grep '[a]nt -f router-service/build.xml run-consumer' | awk '{print $2}' | xargs -r kill -9

# Kill any process running on port 8080
PID=$(sudo lsof -t -i:8080)
if [ ! -z "$PID" ]; then
    echo "🛑 Killing process on port 8080 (PID: $PID)"
    kill -9 $PID
fi


echo "✅ All services stopped cleanly!"
