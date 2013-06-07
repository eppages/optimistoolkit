__author__ =  'Anthony Sulistio'
__version__=  '1.0'

import os
import pika
import sys
import time

def sendMessage(path, hostName, queueName):
    connection = pika.BlockingConnection(pika.ConnectionParameters(host=hostName))
    channel = connection.channel()

    # make sure that RabbitMQ will never lose our queue by setting durable=True 
    #channel.queue_declare(queue='task_queue', durable=True)
    channel.queue_declare(queue=queueName)

    # traverse directory recursively and search for xml
    for r,d,f in os.walk(path):
        for files in f:
            if files.endswith(".xml"):
                message = os.path.join(r,files)
                #print "Sending location of " + message   # debugging

                # send the message to the queuing system
                channel.basic_publish(exchange='', routing_key=queueName, body=message)

    connection.close()


def main(): 
    path = "/opt/optimis/MonitoringInfrastructure/var"
    interval = 30   # in seconds
    hostName = 'localhost'
    queueName = 'mi_queue'   # queue name in the RabbitMQ

    print "Usage: python sender.py [directory_path time_interval hostname]"
    length = len(sys.argv)

    # argv[0] is this script file name
    #print sys.argv
    #print "len = " + str(length) + " -- sys.argv[0] = " + sys.argv[0]

    if length >= 3:
        path = sys.argv[1]          # directory path
        interval = int(sys.argv[2]) # time interval
        if length == 4: 
            hostName = sys.argv[3]  # hostname
    #else:    
        #print "Use the default values." 

    print "Using the following parameters for sending messages to RabbitMQ:"
    print "* directory path = " + path
    print "* time interval = " + str(interval) + " seconds between sending messages"
    print "* hostname = " + hostName
    print 'To exit press CTRL+C or use the kill command'
    print

    # infinite loop that keeps sending messages between a given time interval
    while True:
        sendMessage(path, hostName, queueName)
        #print "----- sleep -----"
        #print
        time.sleep(interval)  # in seconds


if __name__ == "__main__":
    try:
        main()
    except:
        print "sender.py -- Exits the program"
