#include <TimerOne.h>

const int LED = 13;

void setup() {
  pinMode(LED,OUTPUT);
  Timer1.initialize(500000);
  Timer1.attachInterrupt(flicker);
}

void loop() {
}

void flicker() {
  digitalWrite(LED, !digitalRead(LED));
}