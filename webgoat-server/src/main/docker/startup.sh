#!/bin/sh

java -Xmx256m -Xss256k -Djava.security.egd=file:/dev/./urandom -jar /home/webgoat/webgoat.jar &
#echo "Waiting for WebGoat to start..."
#sleep 20
#java -Xmx128m -Xss256k -Djava.security.egd=file:/dev/./urandom -jar /home/webgoat/webwolf.jar
