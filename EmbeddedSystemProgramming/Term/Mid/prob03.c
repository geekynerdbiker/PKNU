#include <Servo.h>

Servo servo;
const int servoPin = 3;

void setup() {
    Serial.begin(9600);
    servo.attach(servoPin);
}

void loop() {
    while(Serial.available() == 0) {}
    angle = Serial.parseInt();
    servo.write(angle);
}