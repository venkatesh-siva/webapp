#!/bin/bash

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \ -a fetch-config \ -m ec2 \ -c file:~/cloudwatch-config.json \ -s

# Start the Spring boot app running
java -jar ~/csye/target/csye-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &
