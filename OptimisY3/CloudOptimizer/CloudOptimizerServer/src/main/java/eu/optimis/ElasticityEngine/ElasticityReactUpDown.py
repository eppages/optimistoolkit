#!/usr/bin/env python


import sys
import math

class sim(object):

    def __init__(self,Server_Speed=100,D=5,Factor=40,R=0,delta_T=5,repair_c=0, Initial_Number_Servers=1):
        
        self.TraceFile=open("processedoutput.txt",'r')
        self.Server_Speed=Server_Speed
        self.Factor=Factor
        self.D=D
        self.Trace=self.TraceFile.readlines()
        self.R=R
        self.delta_T=delta_T
        self.AvgCapacity=Initial_Number_Servers
        self.repair_c=repair_c            #estimates
        self.CurrentCapacity=Initial_Number_Servers
        self.Time_lastEstimation=0
        self.trial=open('Full_delta_TNoMultiplicationReactUpDownConstGammaSS%sD%sF%sT%s.txt'%(Server_Speed,D,Factor,delta_T),'w')
        self.percentile=[]
        self.lastTime=0
        self.Second_Previous=0
        self.Minute_Previous=0
        self.Hour_Previous=0
        self.NumberOfReactiveServers=0
        self.delta_Hours=0
        self.delta_minutes=0
        self.delta_seconds=0
        self.sigma_Alive=1
        self.Current_Load=0
        self.avg_n=1
        self.u_estimate=0
        self.P_estimate=0
        self.Previous_Load=0
        self.s=0
        self.NumberOfProactiveDecisions=0
        self.numberOfestimations=0
        self.numberReactiveDecisions=0
        
        #==========================================================================================================
        # End of C_new variables    

    def run_sim(self):
        x=self.simloop()
        return x

    
    #Adding the Estimator and the controller functionality

#    def estimator(self,t,Delta_Load):
#        self.numberOfestimations+=1
#        self.avg_n=float(self.sigma_Alive)/t#self.delta_T #t   
#        self.u_estimate=float(self.AvgCapacity)/self.avg_n
#        self.P_estimate=float(Delta_Load)/math.ceil(self.delta_T) #t
#        self.delta_T=float(self.D)/self.AvgCapacity
#    
#    
#    def controller(self):
#        self.R=(self.u_estimate*self.P_estimate*self.avg_n)/(self.Server_Speed)**2
#        


     
    def repair(self,Delta_TimeFromLastRepair,Time):
#        self.repair_c+=(self.R*Delta_TimeFromLastRepair)
#        if self.repair_c>1 or self.repair_c<0:    
#            self.s=int(self.repair_c)
#            self.repair_c-=self.s    
#            if self.s<0:
#                if self.CurrentCapacity+self.s>0:
#                    self.CurrentCapacity+=self.s
#                    self.NumberOfProactiveDecisions+=abs(self.s)
#                else:
#                    self.CurrentCapacity=1
             
        TemporaryVariable=math.ceil(self.Current_Load/self.Server_Speed)-self.CurrentCapacity
        #print TemporaryVariable

        if self.Current_Load/self.Server_Speed>self.CurrentCapacity and (self.Current_Load-self.CurrentCapacity*self.Server_Speed)>self.D :
#            Delta_Load=self.Current_Load-self.Previous_Load
#            self.estimator(Time,Delta_Load)
#            self.controller()        
#            self.Time_lastEstimation=Time
#            self.Previous_Load=self.Current_Load
            self.CurrentCapacity+=TemporaryVariable
            self.NumberOfReactiveServers+=TemporaryVariable
            self.numberReactiveDecisions+=1
            
#            return 
#                
        if self.Current_Load/self.Server_Speed<self.CurrentCapacity and (self.Current_Load-self.CurrentCapacity*self.Server_Speed)<(-2*self.Server_Speed) :
#            Delta_Load=self.Current_Load-self.Previous_Load            
#            self.estimator(Time,Delta_Load)
#            self.controller()        
#            self.Time_lastEstimation=Time
#            self.Previous_Load=self.Current_Load
            self.CurrentCapacity+=TemporaryVariable+1
            self.NumberOfReactiveServers-=(TemporaryVariable+1)
            self.numberReactiveDecisions+=1
            
            return 
    

    def simloop(self):
        #print self.D
        GammaTime=0
        self.PreviousCapacity=0
        percentile95=0
        percentile5=0
        self.Previous_Load=0
        Delta_TimeFromLastRepair=0
        Time=1
        PC=0
        tg=0
        Initial_time=int(self.Trace[0].split()[0])
        self.Previous_Load=int(self.Trace[0].split()[1])*self.Factor
        Temporary_Time=Initial_time
        self.Second_Previous=Temporary_Time%100
        Temporary_Time=Temporary_Time/100
        self.Minute_Previous=Temporary_Time%100
        Temporary_Time=Temporary_Time/100
        self.Hour_Previous=Temporary_Time%100
        Delta_TimeFromLastRepair=Time
        CapacityList=[]
        LoadList=[]
        Delta_Load=0
        
            
        for t in self.Trace:
            t=t.split()
            Current_Time=int(t[0])
            self.Current_Load=int(t[1])*self.Factor
            Temporary_Time=Current_Time
            Current_Seconds=Temporary_Time%100
            Temporary_Time=Temporary_Time/100
            Current_Minutes=Temporary_Time%100
            Temporary_Time=Temporary_Time/100
            Current_Hours=Temporary_Time%100
            Delta_TimeFromLastRepair=Time
            flag=0
            
            TempCurrent_Seconds=Current_Seconds
            TempCurrent_Minutes=Current_Minutes
            TempCurrent_Hours=Current_Hours
            
            self.delta_seconds=Current_Seconds-self.Second_Previous
            self.Second_Previous=Current_Seconds
            while self.delta_seconds<0:
                if TempCurrent_Minutes>0:
                   self.delta_seconds+=60
                   TempCurrent_Minutes-=1
                elif TempCurrent_Minutes==0:
                    self.delta_seconds+=60
                    TempCurrent_Minutes=59
                    TempCurrent_Hours-=1
                    flag=1
                                                       
    
            self.delta_minutes=TempCurrent_Minutes-self.Minute_Previous
            self.Minute_Previous=Current_Minutes                
            while self.delta_minutes<0:
              if TempCurrent_Hours>0:
                       self.delta_minutes+=60
                       TempCurrent_Hours-=1
              elif TempCurrent_Hours==0:
                        self.delta_minutes+=60
                        TempCurrent_Hours=23
                        print 123456

        
            self.delta_Hours=TempCurrent_Hours-self.Hour_Previous
            self.Hour_Previous=Current_Hours
            while self.delta_Hours<0:
                  self.delta_Hours+=24

            Delta_TimeFromLastRepair=(self.delta_Hours*60+self.delta_minutes)*60+self.delta_seconds
            Time+=Delta_TimeFromLastRepair
            
            x=Time-self.Time_lastEstimation
            #print self.AvgCapacity
            CapacityList+=[(self.CurrentCapacity,Delta_TimeFromLastRepair)]
            LoadList+=[self.Current_Load]
            tempCapac=0
            if Time-GammaTime>=10:   # To be revised, Why 500 !
                if (self.CurrentCapacity-self.PreviousCapacity)!=0:
                    for i in CapacityList:
                        tempCapac+=i[0]*i[1]
                    if Time-GammaTime>0:
                        self.AvgCapacity=float(tempCapac)/(Time-GammaTime)
                    else:
                        NewTime=(Time-GammaTime)+60*60*24    
                        print NewTime
                        self.AvgCapacity=float(tempCapac)/NewTime    
                PC=self.CurrentCapacity-self.PreviousCapacity
                self.PreviousCapacity=self.CurrentCapacity
                tg=Time-GammaTime
                GammaTime=Time
                CapacityList=[]
                LoadList=[]
            #print self.AvgCapacity    
#            if x>=self.delta_T:     
#                Delta_Load=self.Current_Load-(self.CurrentCapacity*self.Server_Speed)
#                self.estimator(Time,Delta_Load)
#                self.controller()        
#                self.Time_lastEstimation=Time
#                self.Previous_Load=self.Current_Load
            #x='t='+str(t)
            self.repair(Delta_TimeFromLastRepair,Time)
            self.sigma_Alive+=self.CurrentCapacity
            print>>self.trial,Current_Time,"C",self.CurrentCapacity,"L",self.Current_Load,"delta",self.CurrentCapacity-float(self.Current_Load)/self.Server_Speed,"v",self.NumberOfReactiveServers ,"D",self.delta_T,"avC",self.AvgCapacity," P",self.P_estimate,"DL",Delta_Load,"pro ",self.NumberOfProactiveDecisions,"NE",self.numberOfestimations,"NRD",self.numberReactiveDecisions
#        self.percentile.sort()
#        percentile95=self.percentile[95*(len(self.percentile)+1)/100]
#        percentile5=self.percentile[5*(len(self.percentile)+1)/100]
        self.trial.close()
        self.TraceFile.close()
        #print>>self.log, self.D,' ',self.maxim,' ',self.minim,' ',percentile95,' ',percentile5
        #self.log.close()




    
