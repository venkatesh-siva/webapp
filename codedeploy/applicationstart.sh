#!/bin/bash
sudo mv cloudwatch-config.json /opt/

# Start the Spring boot app running
nohup java -jar ~/csye/target/csye-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapp.log 2> /home/ubuntu/webapp.log < /home/ubuntu/webapp.log &

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/opt/cloudwatch-config.json -s
