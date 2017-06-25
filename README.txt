This application was made in the NBIoT hackathon which was held on 23.06 in Berlin.
We had a little longer than 1 day to finish our project. 
I did this project with one hardware, back-end developer, 1 designer and 1 idea generator.

Our team aimed to make a prototype to help for people to save their food.
We wanted that people are notified when their fridge-temperature or humidity is not proper to keep their food safely.

My job was to make a mobile application which communicates to IoT device by using MQTT (Message Queue Telemetry Transport).
By importing "Paho" library, I could make it.

Now, people can see the fridge-temperature and humidity and they will be informed when the data is out of the range.
Of course, they need to install the gateway to the fridge which is developed from our awesome back-end developer.
When they add new sensor to the gateway, they can register it in the application and they can name it. (E.g. upper, middle.)



