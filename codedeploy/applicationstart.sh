#!/bin/bash
# Start the Spring boot app running
sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/cloudwatch-config.json -s
sudo java -jar /home/ubuntu/csye/target/csye-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapp.log 2> /home/ubuntu/webapp.log < /home/ubuntu/webapp.log &