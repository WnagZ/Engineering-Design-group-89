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

void setup() {
  Serial.begin(115200);
  while (!Serial);

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
}

void loop() {
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
  } else {
    handleButton();
  }

  if (shouldFlash) {
    tft.fillScreen(TFT_RED);
    delay(1000);
    tft.fillScreen(TFT_BLACK);
    delay(1000);
  } else {
    int x = (tft.width() - BAR_WIDTH) / 2;
    int y = 35;
    tft.setCursor(x, y);
    tft.print("Water usage");
    tft.setCursor(x + 130, 85);
    tft.print("10 L");
    tft.setCursor(x, 85);
    tft.print("1 L");
    for (int i = 0; i <= 100; i++) {
      handleButton();
      if (shouldFlash) {
        break;
      }
      drawBar(i);
      delay(50);
    }
    delay(2000);
  }
}


void handleWiFiClients() {
  WiFiClient client = server.available();
  if (client) {
    while (client.connected()) {
      if (client.available()) {
        String line = client.readStringUntil('\n');
        line.trim();
        if (line.length() == 0) {
          client.println("HTTP/1.1 200 OK");
          client.println("Content-Type: text/html");
          client.println("Connection: close");
          client.println("Refresh: 5");
          client.println();
          client.println("<!DOCTYPE HTML>");
          client.println("<html>");
          for (int analogChannel = 0; analogChannel < 4; analogChannel++) {
            int sensorReading = analogRead(analogChannel);
            client.print("analog input ");
            client.print(analogChannel);
            client.print(" is ");
            client.print(sensorReading);
            client.println("<br />");
          }
          client.println("</html>");
          client.flush();
          break;
        }
      }
    }
    client.stop();
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

void drawBarInterface() {
  int x = (tft.width() - BAR_WIDTH) / 2;
  int y = 35;
  tft.setCursor(x, y);
  tft.print("Water usage");
  tft.setCursor(x + 130, 85);
  tft.print("10 L");
  tft.setCursor(x, 85);
  tft.print("1 L");

  for (int i = 0; i <= 100; i++) {
    handleButton();
    if (shouldFlash) {
      break;
    }

    drawBar(i);
    delay(50);
  }

  delay(2000);
}

void drawBar(int fillAmount) {
  int filledWidth = map(fillAmount, 0, 100, 0, BAR_WIDTH);
  int startY = (tft.height() - BAR_HEIGHT) / 2;

  tft.fillRect((tft.width() - BAR_WIDTH) / 2, startY, filledWidth, BAR_HEIGHT, TFT_BLUE);
  tft.fillRect((tft.width() - BAR_WIDTH) / 2 + filledWidth, startY, BAR_WIDTH - filledWidth, BAR_HEIGHT, TFT_LIGHTGREY);
}
