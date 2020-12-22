#include<iostream>
#include<unistd.h>
#include"GPIO.h"

using namespace exploringBB;
using namespace std;

GPIO *inGPIO1, *inGPIO2;
int count = 0;

int activate(){
   cout << "Button Pressed" << endl;
   count++;
   return 0;
}

int main(){
   if(getuid()!=0){
      cout << "You must run this program as root. Exiting." << endl;
      return -1;
   }
   inGPIO1 = new GPIO(115);
   inGPIO2 = new GPIO(68);

   while(count < 10) {
      inGPIO1->setDirection(INPUT);
      inGPIO1->setEdgeType(RISING);
      inGPIO2->setDirection(INPUT);
      inGPIO2->setEdgeType(RISING);
      inGPIO1->waitForEdge(&activate);
      inGPIO2->waitForEdge(&activate);
   }

   cout << "Terminate" << endl;
   return 0;
}