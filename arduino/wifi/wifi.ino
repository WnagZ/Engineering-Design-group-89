#include <WiFi.h>
#include <WiFiClient.h>
#include <WiFiAP.h>

const char* ssid = "";
const char* password = "";

void setup() {
  // initialize serial communication at 9600 bits per second:
  Serial.begin(9600);
  connect();
}
void loop() {
  char incomingByte;
   // If there is a data stored in the serial receive buffer, read it and print it to the serial port as human-readable ASCII text.
  if(Serial.available()){  
    incomingByte = Serial.read();
    Serial.print(incomingByte);  
    connect();
  }
}

void connect() {
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
}