EEC 193A-B Team: Fulcrum

	RFID Circuit 
/////////////////////////////////////

Resources Needed 

1X Ardunio nano 33 IoT
1X RC522 RFID Reader

Steps to Reproduce

- Connect wires as such 
	Pin	Wiring to Arduino nano
	SDA	Digital 10
	SCK	Digital 13
	MOSI	Digital 11
	MISO	Digital 12
	IRQ	unconnected
	GND	GND
	RST	Digital 9
	3.3V	3.3V

- Download RFID Library, WiFiNINA, ArduinoBearSSL, ArduinoECCX08, ArduinoMqttClient, Arduino Cloud Provider Examples
- if you are not firmiliar with the nano board here is a tutorial section https://docs.arduino.cc/hardware/nano-33-iot
- run code (under AWS_IoT_WiFi folder name) to check for errors. The set up complies move on the the next step.
- UPDATE your wifi infomation in arduino_secrets.h file.
- Log on to your AWS Account
- Go to IoT core and make a thing and upload your Certificate Signing Request (CSR) to connect to the AWS Cloud. 
- Make a policy to allow all IoT actions and activate the generated certificate.

// if you are not well versed in AWS here is a step by step guide with pictures!!! //
https://create.arduino.cc/projecthub/Arduino_Genuino/securely-connecting-an-arduino-mkr-wifi-1010-to-aws-iot-core-a9f365?_gl=1*189x577*_ga*MTI0MDQ1MjQ1NS4xNjU0NTcwNjcy*_ga_NEXN8H46L5*MTY1NDYyNjc1Mi4yLjEuMTY1NDYyNjc1NC41OA..
//


- Uncomment all the print statments for debugging purposes.
- scan RFID card and update on line 171 for your own personal NFC chip
- to test go to the MQTT tester on AWS and subcribe to the topic 'arduino/outgoing'


Making the RFID APP
//////////////////////////////////////
 Resources Needed 

1X Google FireBase Account 
1X Andriod Studio ( Kotlin)
1X AWS
1X Iron-like will


 Steps to Reproduce

- Create a new project on Firebase
- Create a new app
- Create a new project on Android Studio
- Copy the google-services.json
- Edit the build.gradle files
- Edit the MainActivity to get a device token
- Create a class to override the behavior when a notification is received and the app is opened
- Edit the AndroidManifest
- Build and create an apk file
- Install the apk on your phone
- Send an notification using firebase
- Create a Platform Application on AWS SNS
- Create an endpoint using the device token
- Send a notification using a lambda (AWS SDK) 

// if you are not well versed in FireBase SDK or Android DEV here is full video link!!! //
https://www.youtube.com/watch?v=w6rQdurucCk&ab_channel=CarlosEduardoPerezMendoza
//

AWS LAMBDA RFID to APP Connection
//////////////////////////////////////

- After the RFID is setup we now need to track data and connect to the app.
- Make a Lambda fuction and copy/paste the code under SendNotification.js
- Now we need to make a Rule under the thing we registered as in the previous step. In this step make SELECT * FROM 'arduino/outgoing' as the qury statement and
  as for actions select the Lambda function you want to trigger
- Now test the circuit all together !!!

Fulcrum APP UI, User Sign Up/Auth, FULL stack app backend with AWS Amplify and Connection to the Tacking Circuit
/////////////////////////////////////

 Resources Needed

1X AWS Account
1X APP Template via https://www.dhiwise.com/ (open souurce)
2X Stronger then Iron-like Will

fulcrum

Built with AndroidX Support

Requires Android Studio Arctic Fox | 2020.3.1 or higher.

Current Java Version 1.5.30

SDK Versions

compileSdkVersion 31

buildToolsVersion "30.0.1"

minSdkVersion 23

targetSdkVersion 31


### Libraries

1. Retrofit- REST API Call
https://square.github.io/retrofit/
2. Glide - Image Loading and caching.
https://github.com/bumptech/glide
3. Material Design Components - Google's latest Material Components.
https://material.io/develop/android
4. koin - Dependency Injection
https://insert-koin.io/

### Figma design guideline for better accuracy

Read our guidelines to increase the accuracy of design conversion to code by optimizing Figma designs. 
https://docs.dhiwise.com/knowledgehub/reference-guide-and-tools/figma.

### App Navigation

Check your app\'s UI from the AppNavigation screens of your app.

### Package Structure


```
├── appcomponents       
│ ├── di                 - Dependency Injection Components 
│ │ └── MyApp.Js
│ ├── network            - REST API Call setup
│ │ ├── ResponseCode.Js
│ │ └── RetrofitProvider.Js
│ └── ui                 - Data Binding Utilities
│     └── CustomBindingAdapter.Js
├── constants            - Constant Files
│ ├── IntegerConstants.Js
│ └── StringConstants.Js
├── extensions           - Kotlin Extension Function Files
│ └── Strings.Js
├── modules              - Application Specific code
│ └── example            - A module of Application 
│  ├── ui                - UI handling classes
│  └── data              - Data Handling classes
│    ├── viewmodel       - ViewModels for the UI
│    └── model           - Model for the UI
└── network              - REST API setup
  ├── models             - Request/Response Models
  ├── repository         - Network repository
  ├── resources          - Common classes for API
  └── RetrofitService.Js
```
### Fonts
We were unable to find following Fonts, Please add manually to ```app/src/main/res/font``` and uncomment code in respective font family XML files.

```
poppinssemibold600
poppinsbold700
poppinsmedium500
poppinsregular400
``` 

Now Need to ADD in the APP Backend Via AWS Amplify

https://aws.amazon.com/getting-started/hands-on/build-android-app-amplify/
User Auth should be specified via email, to do this follow guide on link, set up pool/buckket to gather and store data 
set up cognito for user pool management
set API KEY be wary of expire date


here is a complete step by step tutroial to set up Amplify back for user auth, to add the fuctions and custom api's for tracking we can USE AWS Lambda and then use this to invoke 
the Lambda fuction
under fuctions add code from FulcrumFetchData-staging.js to a new Lambda fuction and then invoke it. 
like so https://ibb.co/cTW7nCc

https://stackoverflow.com/questions/56106825/invoke-lambda-function-from-amplify-generated-react-app-without-using-api-gatewa
now connect app to the tracking circuit via DynamoDB.

for tutorial purposes download .zip file Fulcrum-v1 and open on andriod studios and update Amplify library with your own and under
C:AndroidStudioProjects\Fulcrum_v1\amplify 
both for #current-cloud-backend folder and backend 
*note this process is automaticly done by AWS all you need to is make the API's.


 	Tracking
/////////////////////////////////////
Resources Needed

1x Raspberry Pi (and Monitor, Keyboard, Mouse, or some other means of control) //I believe that any computer which can run python would work, but a Raspberry Pi is more IoT
		This README assumes that Linux (or your preferred OS), python3, and pip3 are already installed on the Raspberry Pi
3x ESP32 Boards
1x Computer with Arduino IDE installed
3x USB to microUSB cords
1x Voltage Regulator
1x 9V Battery //We found that these ran out of juice fast. You may need several.
1x 9V-Battery-to-Jack Adaptor //comes with the voltage regulator in the ELEGOO Superstarter Kit
1x AWS DynamoDB Database

### Raspberry Pi Libraries

### Python Libraries
mosquitto
re
numpy
os
glob
joblib
sklearn //download here: <https://scikit-learn.org/stable/install.html>	You may have to first install dependencies. You should be prompted to do so when you attempt to install.
csv
boto3
datetime
awscli

### Arduino Libraries
ESP32 Board //not strictly a library. Tutorial for download and installation: https://randomnerdtutorials.com/installing-the-esp32-board-in-arduino-ide-windows-instructions/
SPI.h
DW1000.h
WiFi.h
PubSubClient.h


Steps to Reproduce

- Raspberry Pi MQTT set-up
		//The following is the guide I used for setting up MQTT communication between the ESP32 boards and the Raspberry Pi:
		https://diyi0t.com/microcontroller-to-raspberry-pi-wifi-mqtt-communication/
		//
	- Install all Python libraries			
	- Change the mosquitto.conf file as indicated in the above guide
	- Set up MQTT Username and Password
		- Be sure to copy these, as they will be necessary on the ESP32 boards
	- Connect Raspberry Pi to the internet
- ESP32 Boards
	- Install Arduino Libraries
	- In the anchorA_training code
		- On lines 29 and 30, include ssid and password for your WiFi
		- On line 33, write the IP address of the Raspberry Pi. Can be found by with the ifconfig command in linux.
		- On lines 36 and 37, write the mqtt username and password for the server you set up on the Raspberry Pi
	- Repeat previous step for anchorB_training code, anchorA_tracking code, and anchorB_tracking code
	- Flash anchorA_training, anchorB_training, and tag code onto the ESP32 boards. Keep track of which is which.
	- Place the two anchors on one edge of the space you wish to be tracking in
		- the further apart they are, the better. Around 4 to 6 meters is good. They can be seperated by walls.
	-Power the tag with the 9V battery and voltage regulator. The anchors can just be powered by computers, or by more voltage regulators if you have them.
- Raspberry Pi Python Code
	- In the create_training_files.py code
		- On line 6, name the space you wish to be tracking in. Like "House" or "Floor 1" or "Sector 7G". I call this the map name
		- On line 13, add the IP address of the Raspberry Pi, as above.
		- On lines 14 and 15, add the mqtt username and password, as above.
- Training
	- In the create_training_files.py code
		- On line 7, name the first room you want to generate a training file for. "Room 112" or "Lab" or "My Office", something along those lines
	- Make sure the tag is in the appropriate room
	- Run create_training_files.py //I like to reset the anchors at this time too, to make sure that they are transmitting at close to the same time, but this isn't really necessary
	- Range data will begin to be stored by the Raspberry Pi. Walk around the room with the tag to ensure a variety of range data is gathered.
		- The create_training_files.py will print the number of successesful data reads. When this number (NOT attempts) is high enough, stop running create_training_files
			- We typically went to around 50 successes. I suspect that in most cases, you only need 15 or so, but that is not tested.
			- If you are having trouble getting successes, make sure that both anchors are connected to the internet properly, and that the tag is in range of the anchors. Sometimes some parts of a room are not reachable.
	- When you stop running the program, make sure that a CSV file with two columns of floating point data named whatever you named the room has been created in a folder named whatever you called your space.
	- Repeat these steps for each room you wish to create a training file for.
	- In train_and_store_classifier.py
		- On line 8, add the map name
	- Run train_and_store_classifier.py //this trains a classifier on the range data in the CSV files and saves it in the same folder as the CSV files
- DynamoDB Setup
		//The following is the guide I used for setting up communication between Raspberry Pi and AWS DynamoDB:
		https://www.youtube.com/watch?v=gQLEOyBK6fg
		//
	- On AWS, create user with programmatic access and administrative priviliges
		- Download credentials
	- On Raspberry Pi terminal, run aws configure
		- Enter credentials (Key ID and Secret Key) and correct region
	- On AWS, create DynamoDB table named "tag_position" and with "datetime" as the Primary Key. If you change either of these, you will have to also change them in the tracker.py code
	- In the tracker.py code
		- On line 9, add the IP address of the Raspberry Pi, as above.
		- On lines 10 and 11, add the mqtt username and password, as above.
	- Flash anchorA_tracking code and anchorB_tracking to the two anchors and place them in the same positions
	- While the 3 ESP32 boards are running, run tracker.py
		- Tag positions should now be uploaded to the database as the Tag moves around, approximately once per 30 seconds. It varies a bit.
	- It is necessary to clear the database between instances of running the tracker.py code. Otherwise the app might read old data.


