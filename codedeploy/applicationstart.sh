#!/bin/bash
# Start the Spring boot app running
sudo nohup java -jar ~/csye/target/csye-0.0.1-SNAPSHOT.jar >> app.log 2>&1 &