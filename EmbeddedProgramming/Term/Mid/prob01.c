volatile int state[3] = {0,0,0};
const int LED1 = 9;
const int LED2 = 10;
const int LED3 = 11;
const int BUTTON  = 2;

void setup() {
    pinMode(LED1, OUTPUT);
    pinMode(LED2, OUTPUT);
    pinMode(LED3, OUTPUT);
    attachInterrupt(digitalPinToInterrupt(2), onPressed, RISING);
}

void loop() {
    digitalWrite(LED1, state[0]);
    digitalWrite(LED2, state[1]);
    digitalWrite(LED3, state[2]);
}

void onPressed() {
    static unsigned long m1 = 0;
    unsigned long m2 = millis();
    
    if (m2 - m1 >= 100) {
        for (int i = 0; i < 3; i++) {
            if (state[i] == 0) {
                state[i]=1;
                break;
            }
            else state[i]=0;
        }

        m1 = m2;
    }
}