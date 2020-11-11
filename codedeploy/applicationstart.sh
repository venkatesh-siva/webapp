#!/bin/bash
# Start the Spring boot app running
nohup java -jar ~/csye/target/csye-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapp.log 2> /home/ubuntu/webapp.log < /home/ubuntu/webapp.log &

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/cloudwatch-config.json -s

crontab -l > mycron
echo "@reboot java -jar /home/ubuntu/csye/target/csye-0.0.1-SNAPSHOT.jar > /home/ubuntu/webapp.log 2> /home/ubuntu/webapp.log < /home/ubuntu/webapp.log &" >> mycron
crontab mycron
rm mycron
