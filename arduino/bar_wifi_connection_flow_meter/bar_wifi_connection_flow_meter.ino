#include <WiFiEspAT.h>
#include <TFT_eSPI.h>
TFT_eSPI tft = TFT_eSPI(135, 240);

#define ESP32C3_RX_PIN 9
#define ESP32C3_TX_PIN 8
#define PIN_PWR_ON 22


#define DISPLAY_BACKLIGHT 4
#define BACKLIGHT_MAX 255
#define BAR_WIDTH 160
#define BAR_HEIGHT 30
#define BUTTON_PIN 6
#define RESET_BUTTON_PIN 7
#define BACKLIGHT_MAX 255

bool shouldFlash = false;
unsigned long lastButtonPress = 0;
const long debounceTime = 50;

WiFiServer server(80); 

byte sensorInterrupt = 21;  // 0 = digital pin 2
byte sensorPin = 21;

volatile byte pulseCount;
float calibrationFactor = 11;
unsigned long totalMilliLitres;
unsigned long oldTime;

#define statusLed 25

void setup() {

  Serial.begin(115200);

  pinMode(PIN_PWR_ON, OUTPUT);
  digitalWrite(PIN_PWR_ON, HIGH);

  Serial2.setTX(ESP32C3_TX_PIN);
  Serial2.setRX(ESP32C3_RX_PIN);
  Serial2.begin(115200);
  WiFi.init(Serial2);

  if (WiFi.status() == WL_NO_MODULE) {
    Serial.println("Communication with WiFi module failed!");
    while (true);
  }

  Serial.println("Waiting for connection to WiFi");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.print('.');
  }
  Serial.println();

  server.begin();

  IPAddress ip = WiFi.localIP();
  Serial.println("Connected to WiFi network.");
  Serial.print("To access the server, enter \"http://");
  Serial.print(ip);
  Serial.println("/\" in web browser.");

  // TFT setup
#if defined(DISPLAY_BACKLIGHT) && (DISPLAY_BACKLIGHT >= 0)
  pinMode(DISPLAY_BACKLIGHT, OUTPUT);
  digitalWrite(DISPLAY_BACKLIGHT, LOW);
#endif

  tft.init();
  tft.setRotation(1);
  tft.fillScreen(TFT_DARKGREY);
  tft.setTextFont(2);

#if defined(DISPLAY_BACKLIGHT) && (DISPLAY_BACKLIGHT >= 0)
  analogWrite(DISPLAY_BACKLIGHT, BACKLIGHT_MAX);
#endif

  pinMode(BUTTON_PIN, INPUT_PULLUP);
  pinMode(RESET_BUTTON_PIN, INPUT_PULLUP);


  //flow meter setup
  // Initialize a serial connection for reporting values to the host
  pinMode(statusLed, OUTPUT);
  digitalWrite(statusLed, HIGH);  // We have an active-low LED attached
  pinMode(sensorPin, INPUT);
  digitalWrite(sensorPin, HIGH);
  pulseCount = 0;
  oldTime = 0;
  totalMilliLitres = 0;
  attachInterrupt(sensorInterrupt, pulseCounter, FALLING);
}

/**
 * Main program loop
 */
void loop() {
 // Handle WiFi clients
  handleWiFiClients();

  // Handle flow meter readings
  handleFlowMeter();

  // Update the TFT display
  updateDisplay();
}

void handleWiFiClients() {
  WiFiClient client = server.available();
  if (client) {
    char buf[50];
    // Check if it's a GET request to set the flashing state
    // if (line.startsWith("GET /flash=")) {
    //   if (line.indexOf("on") != -1) {
    //     shouldFlash = true;
    //   } else if (line.indexOf("off") != -1) {
    //     shouldFlash = false;
    //   }
    // }
    while (client.connected()) {
      if (client.available()) {
        String line = client.readStringUntil('\n');
        Serial.println("line:" + line);
        line.trim();
        if (line == 0) {
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/json");
          // client.println("Refresh: 5");
          client.println();
          // client.println("<!DOCTYPE HTML>");
          // client.println("<html>");
          // client.println("<h1>Device State</h1>");
          // // client.println("<p>Flashing: " + String(shouldFlash ? "On" : "Off") + "</p>");
          // // client.println("<p>Mililiters used: " + String(totalMilliLitres) + "</p>");   
          // client.println("</html>");
          client.println("{");
          client.println("\"name\": \"test\",");
          client.println("\"location\": \"bathroom\",");
          // sprintf(buf,"\"water_usage\": [{ \n \"milliliters\": %lu",totalMilliLitres);
          sprintf(buf,"\"water_usage\": %lu",totalMilliLitres);
          client.println(buf);
          // client.println("}]");
          client.println("}");
          client.flush();
          break;
        }
      }
    }
    client.stop();
  }
}

void handleFlowMeter() {
  if ((millis() - oldTime) > 1000) {
    detachInterrupt(sensorInterrupt);
    float flowRate = ((1000.0 / (millis() - oldTime)) * pulseCount) / calibrationFactor;
    oldTime = millis();
    float flowMilliLitres = (flowRate / 60) * 1000;
    totalMilliLitres += flowMilliLitres;
    // CHANGE
    totalMilliLitres += 1;
    pulseCount = 0;
    attachInterrupt(sensorInterrupt, pulseCounter, FALLING);
  }
}

void updateDisplay() {
  if (isResetButtonPressed()) {
    shouldFlash = false; // Turn off flashing if reset button is pressed
    tft.fillScreen(TFT_DARKGREY);  // Reset the screen to its initial state
    int x = (tft.width() - BAR_WIDTH) / 2;
    int y = 35;
    tft.setCursor(x, y);
    tft.print("Water usage");
    tft.setCursor(x + 130, 85);
    tft.print("10 L");
    tft.setCursor(x, 85);
    tft.print("1 L");
    drawBar(0);  // Set bar to initial state
    int fillAmount = 0;
  } else {
    handleButton();
    int x = (tft.width() - BAR_WIDTH) / 2;
    int y = 35;
    tft.setCursor(x, y);
    tft.print("Water usage");
    tft.setCursor(x + 130, 85);
    tft.print("10 L");
    tft.setCursor(x, 85);
    tft.print("1 L");
    int fillAmount = map(totalMilliLitres, 0, 10000, 0, 100); // Assuming max of 10L for 100%
    drawBar(fillAmount);
  }
}

void handleButton() {
  if (millis() - lastButtonPress > debounceTime) {
    if (digitalRead(BUTTON_PIN) == LOW) {
      shouldFlash = !shouldFlash;
      lastButtonPress = millis();
      delay(debounceTime);
    }
  }
}
bool isResetButtonPressed() {
  if (digitalRead(RESET_BUTTON_PIN) == LOW) {
    delay(debounceTime);
    if (digitalRead(RESET_BUTTON_PIN) == LOW) {
      return true;
    }
  }
  return false;
}


void drawBar(int fillAmount) {
  int filledWidth = map(fillAmount, 0, 100, 0, BAR_WIDTH);
  int startY = (tft.height() - BAR_HEIGHT) / 2;

  tft.fillRect((tft.width() - BAR_WIDTH) / 2, startY, filledWidth, BAR_HEIGHT, TFT_BLUE);
  tft.fillRect((tft.width() - BAR_WIDTH) / 2 + filledWidth, startY, BAR_WIDTH - filledWidth, BAR_HEIGHT, TFT_LIGHTGREY);
}

/*
Insterrupt Service Routine
 */
void pulseCounter() {
  // Increment the pulse counter
  pulseCount++;
}
