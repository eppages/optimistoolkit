#include <iostream>
#include <fstream>
#include <stdlib.h>
#include <time.h>
using namespace std;


int main()
{
  ifstream inp("/secure/writeTest.txt");
   char c;
   long i = 0;

  while(inp >> c)
  {
     ++i;
  }

  cout << "Read " << i << " bytes." << endl;

  inp.close();
 return 0;
}
