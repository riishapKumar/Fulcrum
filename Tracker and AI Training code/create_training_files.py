import paho.mqtt.client as mqtt
import re
import numpy as np
import os

map_name = ''                       #Name of the space you are tracking in.
room_name = ''                      #Name of the current room. THIS WILL BE THE NAME OF THE CSV FILE AND WHAT THE CLASSIFIER ULTIMATELY SPITS OUT, SO WRITE CAREFULLY
directoryName = 'trainingDataFiles/' + map_name
os.makedirs(directoryName,exist_ok=True)

file_name = directoryName + '/' + room_name + '.csv' 

MQTT_ADDRESS = ''                   # IP address of the Raspberry Pi. Needs to change whenever you connect to different WiFi
MQTT_USER = ''                      # mqtt username. You set this up in the console of the raspberry pi
MQTT_PASSWORD = ''                  # mqtt password. You set this up in the console of the raspberry pi
MQTT_TOPIC = 'range_data/+/+'

previousSource = ''
previousRange = -1.1
previousTime = 0

currentRange = -1.1

useMaxDifference = False
maxDifference = 5000;

attemptCount = 0;
successCount = 0;
maxWrite = 100;



def on_connect(client, userdata, flags, rc):
    """ The callback for when the client receives a CONNACK response from the server."""
    print('Connected with result code ' + str(rc))
    client.subscribe(MQTT_TOPIC)


def on_message(client, userdata, msg):
    """The callback for when a PUBLISH message is received from the server."""
    global previousSource
    global previousRange
    global previousTime
    
    global currentRange
    
    global attemptCount
    global successCount
    
    global timeAverage
    
    #print(msg.topic + ' ' + str(msg.payload))
    
    #DATA PROCESSING
    clientExpansion = msg.topic.split("/")
    source = clientExpansion[1]
    dataType = clientExpansion[2]
    
    #FIGURING OUT IF ITS A GOOD READ
    if dataType == "range":
        rangeData = float(re.findall("\d+\.\d+",str(msg.payload))[0])
        
        previousRange = currentRange
        currentRange = rangeData
        
    elif clientExpansion[2] == "time":        
        currentTime = int(re.findall("\d+",str(msg.payload))[0])
        
        orderCondition = False
        timeCondition = False
        timeAverage = 0
        
        if previousSource == 'rangeA':
            attemptCount += 1
            if source == 'rangeB':
                orderCondition = True
                timeDiff = currentTime - previousTime
                timeAverage = timeAverage * ((attemptCount - 1)/attemptCount) + timeDiff / attemptCount
                #print(timeAverage/1000)
            else:
                orderCondition = False
        
        if useMaxDifference and timeDiff >= maxDifference:
            timeCondition = False
        else:
            timeCondition = True
            
        if orderCondition and timeCondition:
            rangeCouple = np.array([[previousRange, currentRange]])
            #print(rangeCouple)
            with open(file_name,'ab') as csvfile:
                np.savetxt(csvfile, rangeCouple, fmt = '%02.2f', delimiter=',')
                csvfile.close()
            successCount += 1
            print('Num. Attempts: ' + str(attemptCount))
            print('Num. Successes: ' + str(successCount))
            print('Ratio: ' + str(successCount/attemptCount))
                
        previousSource = source

def main():
    mqtt_client = mqtt.Client()
    mqtt_client.username_pw_set(MQTT_USER, MQTT_PASSWORD)
    mqtt_client.on_connect = on_connect
    mqtt_client.on_message = on_message

    mqtt_client.connect(MQTT_ADDRESS, 1883)
    mqtt_client.loop_forever()


if __name__ == '__main__':
    print('MQTT to InfluxDB bridge')
    main()