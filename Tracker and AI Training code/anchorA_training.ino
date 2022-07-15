
/**

   @todo
    - move strings to flash (less RAM consumption)
    - fix deprecated convertation form string to char* startAsAnchor
    - give example description
*/
#include <SPI.h>
#include "DW1000Ranging.h"
#include <WiFi.h>
#include <PubSubClient.h>

#define ANCHOR_ADD "0A:00:5B:D5:A9:9A:E2:9C" //Make sure this is different from all other ESP32 board addresses

#define SPI_SCK 18
#define SPI_MISO 19
#define SPI_MOSI 23
#define DW_CS 4

// connection pins
const uint8_t PIN_RST = 27; // reset pin
const uint8_t PIN_IRQ = 34; // irq pin
const uint8_t PIN_SS = 4;   // spi select pin


// Wifi
// NEEDS TO CHANGE WHEN WIFI CHANGES
const char* ssid = "";
const char* wifi_password = "";

// MQTT
const char* mqtt_server = ""; // NEEDS TO CHANGE WHEN WIFI CHANGES. This is the ip address of the Raspberry Pi which hosts the MQTT server
const char* data_topic_1 = "range_data/rangeA/range";
const char* data_topic_2 = "range_data/rangeA/time";
const char* mqtt_username = ""; // you set this up on the Raspberry Pi. Make sure it matches.
const char* mqtt_password = ""; // you set this up on the Raspberry Pi. Make sure it matches.
const char* clientID = "raspPiClient";

WiFiClient wifiClient;

PubSubClient client(mqtt_server, 1883, wifiClient);

int status;

void connect_MQTT() {
  Serial.print("Connecting to ");
  Serial.println(ssid);

  // Connect to the WiFi
  WiFi.begin(ssid, wifi_password);

  // Wait until the connection has been confirmed before continuing
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  // Debugging - Output the IP Address of the ESP8266
  Serial.println("WiFi connected");
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());

  // Connect to MQTT Broker
  // client.connect returns a boolean value to let us know if the connection was successful.
  // If the connection is failing, make sure you are using the correct MQTT Username and Password (Setup Earlier in the Instructable)
  status = client.connect(clientID, mqtt_username, mqtt_password);
  if (status) {
    Serial.println("Connected to MQTT Broker!");
    Serial.print("Error: ");
    Serial.println(status);
  }
  else {
    Serial.println("Connection to MQTT Broker failed...");
    Serial.println("Error: ");
    Serial.print(status);
  }
}

uint16_t address;
float range;
unsigned long numMill;

void setup()
{
  Serial.begin(115200);
  delay(1000);
  //init the configuration
  SPI.begin(SPI_SCK, SPI_MISO, SPI_MOSI);
  DW1000Ranging.initCommunication(PIN_RST, PIN_SS, PIN_IRQ); //Reset, CS, IRQ pin
  //define the sketch as anchor. It will be great to dynamically change the type of module
  DW1000Ranging.attachNewRange(newRange);
  DW1000Ranging.attachBlinkDevice(newBlink);
  DW1000Ranging.attachInactiveDevice(inactiveDevice);
  //Enable the filter to smooth the distance
  //DW1000Ranging.useRangeFilter(true);

  //we start the module as an anchor
  // DW1000Ranging.startAsAnchor("82:17:5B:D5:A9:9A:E2:9C", DW1000.MODE_LONGDATA_RANGE_ACCURACY);

  DW1000Ranging.startAsAnchor(ANCHOR_ADD, DW1000.MODE_LONGDATA_RANGE_LOWPOWER, false);
  // DW1000Ranging.startAsAnchor(ANCHOR_ADD, DW1000.MODE_SHORTDATA_FAST_LOWPOWER);
  // DW1000Ranging.startAsAnchor(ANCHOR_ADD, DW1000.MODE_LONGDATA_FAST_LOWPOWER);
  // DW1000Ranging.startAsAnchor(ANCHOR_ADD, DW1000.MODE_SHORTDATA_FAST_ACCURACY);
  // DW1000Ranging.startAsAnchor(ANCHOR_ADD, DW1000.MODE_LONGDATA_FAST_ACCURACY);
  // DW1000Ranging.startAsAnchor(ANCHOR_ADD, DW1000.MODE_LONGDATA_RANGE_ACCURACY);

  connect_MQTT();
}

void loop()
{
//  Serial.setTimeout(2000);

  DW1000Ranging.loop();
}

void newRange()
{
  address = DW1000Ranging.getDistantDevice()->getShortAddress();
  range = DW1000Ranging.getDistantDevice()->getRange();
  numMill = millis();

  char rangeChar;

  Serial.print(numMill);
  Serial.print("\t");
  Serial.print(address, HEX);
  Serial.print("\t");
  Serial.println(range);

  rangeChar = char(range);
  Serial.println(rangeChar);

  

  if (client.publish(data_topic_1, String(range).c_str())) {
    Serial.println("Range sent!");
  }
  // Again, client.publish will return a boolean value depending on whether it succeded or not.
  // If the message failed to send, we will try again, as the connection may have broken.
  else {
    Serial.println("Range failed to send. Reconnecting to MQTT Broker and trying again");
    client.connect(clientID, mqtt_username, mqtt_password);
    delay(10); // This delay ensures that client.publish doesn't clash with the client.connect call
    client.publish(data_topic_1, String(range).c_str());
  }

  if (client.publish(data_topic_2, String(numMill).c_str())) {
    Serial.println("Time sent!");
  }
  // Again, client.publish will return a boolean value depending on whether it succeded or not.
  // If the message failed to send, we will try again, as the connection may have broken.
  else {
    Serial.println("Time failed to send. Reconnecting to MQTT Broker and trying again");
    client.connect(clientID, mqtt_username, mqtt_password);
    delay(10); // This delay ensures that client.publish doesn't clash with the client.connect call
    client.publish(data_topic_2, String(numMill).c_str());
  }

  delay(1000*2);

}

void newBlink(DW1000Device *device)
{
//  Serial.print("blink; 1 device added ! -> ");
//  Serial.print(" short:");
//  Serial.println(device->getShortAddress(), HEX);
}

void inactiveDevice(DW1000Device *device)
{
//  Serial.print("delete inactive device: ");
//  Serial.println(device->getShortAddress(), HEX);
}
