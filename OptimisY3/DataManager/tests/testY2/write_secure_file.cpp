#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <time.h>
using namespace std;


int main()
{
  ofstream out("/secure/writeTest.txt");

  srand(time(0));

  for(int i = 0; i < 1024*1024; ++i)
   {
     char c = 'A' + (rand() % 25);
     out << c;
   }

  out.close();
 return 0;
}
