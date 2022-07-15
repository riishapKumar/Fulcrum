import paho.mqtt.client as mqtt
import re
import numpy as np
import os
import boto3
import joblib
import datetime

MQTT_ADDRESS = ''                   #We'll have to change this when we use different WiFi
MQTT_USER = ''                      #Set up on the Raspberry Pi
MQTT_PASSWORD = ''                  #Set up on the Raspberry Pi
MQTT_TOPIC = 'range_data/+/+'

previousSource = None
previousRange = None
previousDateTime = None

reverse_polarity = False

map_name = ''                       #Needs to chage whenever the map changes
path = 'trainingDataFiles/' + map_name

classifier_load_path = path + '/' + map_name + ' classifier.joblib'
classifier = joblib.load(classifier_load_path)

classmap_load_path = path + '/' + map_name + ' classmap.txt'
classmap = {}
with open(classmap_load_path, 'r') as f:
    for line in f:
        (key, val) = line.split(',')
        classmap[int(key)] = val
    f.close()

class MyDb(object):

    def __init__(self, Table_Name='tag_position'):
        self.Table_Name=Table_Name

        self.db = boto3.resource('dynamodb')
        self.table = self.db.Table(Table_Name)

        self.client = boto3.client('dynamodb')

    @property
    def get(self):
        response = self.table.get_item(
            Key={
                'datetime':"1"
            }
        )

        return response

    def put(self, datetime='' , location='', tag = ''):
        self.table.put_item(
            Item={
                'datetime':datetime,
                'location':location,
                'tag':tag
            }
        )

    def delete(self,datetime=''):
        self.table.delete_item(
            Key={
                'datetime': datetime
            }
        )

    def describe_table(self):
        response = self.client.describe_table(
            TableName='test_Table'
        )
        return response

obj = MyDb()

def on_connect(client, userdata, flags, rc):
    """ The callback for when the client receives a CONNACK response from the server."""
    print('Connected with result code ' + str(rc))
    client.subscribe(MQTT_TOPIC)


def on_message(client, userdata, msg):
    """The callback for when a PUBLISH message is received from the server."""
    global previousSource
    global previousRange
    global previousDateTime
    
    print(msg.topic + ' ' + str(msg.payload))
    
    #DATA PROCESSING
    clientExpansion = msg.topic.split("/")
    source = clientExpansion[1]
    #dataType = clientExpansion[2]
    
    rangeData = float(re.findall("\d+\.\d+",str(msg.payload))[0])
    
    
    if previousSource == 'rangeA' and source == 'rangeB':
        rangeCouple = np.array([[previousRange, rangeData]]) #Currrently this is backwards from how the classifier was trained. Just trying something. It should be previousRange and then rangeData 
        print(rangeCouple)
        if reverse_polarity:
            rangeCouple = np.fliplr(rangeCouple)
        num_result = classifier.predict(rangeCouple)
        room = classmap[num_result[0]]
        dateTime = str(datetime.datetime.now())
        try:
            obj.delete(datetime = str(previousDateTime))
        except:
            print('An error occurred')
        obj.put(datetime=dateTime,location = room, tag = 'Computer Graphics Textbook')
        previousDateTime = dateTime
        
    previousRange = rangeData
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