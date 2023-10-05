#include <TFT_eSPI.h>
TFT_eSPI tft = TFT_eSPI(135, 240);

#define DISPLAY_BACKLIGHT  4 
#define BACKLIGHT_MAX    255
#define BAR_WIDTH 160
#define BAR_HEIGHT 30

// Setup
void setup(void) {
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
}

// Function to draw the bar based on fillAmount
void drawBar(int fillAmount) {
  int filledWidth = map(fillAmount, 0, 100, 0, BAR_WIDTH);
  
  // Calculate the starting y-coordinate to place the bar in the middle
  int startY = (tft.height() - BAR_HEIGHT) / 2;
  
  tft.fillRect((tft.width() - BAR_WIDTH) / 2, startY, filledWidth, BAR_HEIGHT, TFT_BLUE); // filled part
  tft.fillRect((tft.width() - BAR_WIDTH) / 2 + filledWidth, startY, BAR_WIDTH - filledWidth, BAR_HEIGHT, TFT_LIGHTGREY); // empty part
}

// Main loop
void loop() {
  int x = (tft.width() - BAR_WIDTH) / 2;
  int y = 35;
  tft.setCursor(x, y);
  tft.print("Water usage");
  tft.setCursor(x + 130, 85);
  tft.print("10 L");
  tft.setCursor(x, 85);
    tft.print("1 L");
  for (int i = 0; i <= 100; i++) {
    drawBar(i);
    delay(50); // delay for demonstration purposes
  }

  delay(2000); // Wait 2 seconds before repeating
}
