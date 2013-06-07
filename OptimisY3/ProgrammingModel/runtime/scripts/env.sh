#!/bin/sh

## Check needed environment variables
IT_HOME=${IT_HOME-NULL};
if [ "$IT_HOME" = "NULL" ]
then
echo
echo "Environment variable IT_HOME not set"
echo "Please use:"
echo " export IT_HOME=<Integrated Toolkit installation directory>"
exit 127
fi

export IT_LIB=$IT_HOME/integratedtoolkit/lib
if [ ! -d $IT_LIB ]
then
echo
echo "Cannot find IT libraries"
echo "Please check if $IT_LIB exists"
exit 127
fi

APP_LIB=$IT_HOME/gridunawareapps/lib
APP_CLASS=$IT_HOME/gridunawareapps/build

JAVA_HOME=${JAVA_HOME-NULL};
if [ "$JAVA_HOME" = "NULL" ]
then
echo
echo "Environment variable JAVA_HOME not set"
echo "Please use:"
echo " export JAVA_HOME=<JDK installation directory>"
exit 127
fi

GAT_LOCATION=${GAT_LOCATION-NULL};
if [ "$GAT_LOCATION" = "NULL" ]
then
echo
echo "Environment variable GAT_LOCATION not set"
echo "Please use:"
echo " export GAT_LOCATION=<GAT installation directory>"
exit 127
fi



## Set up the classpath

CLASSPATH=${CLASSPATH-NULL};
if [ "$CLASSPATH" = "NULL" ]
then
CLASSPATH=.
else
CLASSPATH=$CLASSPATH:.
fi

# Application classes

appClassPath=${appClassPath-NULL};
if [ "$appClassPath" != "NULL" ]
then
	CLASSPATH=$CLASSPATH:$appClassPath
fi

CLASSPATH=$CLASSPATH:$HOME/ServiceSs/JAXWS

# Integrated Toolkit
if [ -f $IT_LIB/IT.jar ]
then
    CLASSPATH=$CLASSPATH:$IT_LIB/IT.jar					# IT classes
fi
if [ -d $APP_CLASS ]
then
    CLASSPATH=$CLASSPATH:$APP_CLASS 			       		# Sample applications classes
fi
if [ -f $APP_LIB/guapp.jar ]
then
    CLASSPATH=$CLASSPATH:$APP_LIB/guapp.jar 			      	# Sample applications jar
fi





add_to_classpath () {
        DIRLIBS=${1}/*.jar
        for i in ${DIRLIBS}
        do
                CLASSPATH=$CLASSPATH:"$i"
        done
}






# Xalan and Xerces

if [ -f $IT_LIB/xalan/xalan.jar ]
then
    CLASSPATH=$CLASSPATH:$IT_LIB/xalan/xalan.jar
fi
if [ -f $IT_LIB/xalan/xml-apis.jar ]
then
    CLASSPATH=$CLASSPATH:$IT_LIB/xalan/xml-apis.jar
fi

# GAT

if [ -f $GAT_LOCATION/lib/GAT-API.jar ]
then
    CLASSPATH=$CLASSPATH:$GAT_LOCATION/lib/GAT-API.jar
fi
if [ -f $GAT_LOCATION/lib/GAT-engine.jar ]
then
    CLASSPATH=$CLASSPATH:$GAT_LOCATION/lib/GAT-engine.jar
fi

# Log4j

if [ -f $IT_LIB/log4j/log4j-1.2.15.jar ]
then
    CLASSPATH=$CLASSPATH:$IT_LIB/log4j/log4j-1.2.15.jar
fi

# Javassist
if [ -f $IT_LIB/javassist/javassist.jar ]
then
   CLASSPATH=$CLASSPATH:$IT_LIB/javassist/javassist.jar
fi


# Web Service
add_to_classpath $IT_LIB/cxf
# if [ -f $IT_LIB/cxf/commons-logging-1.1.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/commons-logging-1.1.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/cxf-2.4.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/cxf-2.4.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/geronimo-activation_1.1_spec-1.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/geronimo-activation_1.1_spec-1.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/geronimo-annotation_1.0_spec-1.1.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/geronimo-annotation_1.0_spec-1.1.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/geronimo-javamail_1.4_spec-1.7.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/geronimo-javamail_1.4_spec-1.7.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/jaxb-api-2.2.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/jaxb-api-2.2.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/jaxb-impl-2.2.1.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/jaxb-impl-2.2.1.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/jsr311-api-1.1.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/jsr311-api-1.1.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/neethi-3.0.0.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/neethi-3.0.0.jar
# fi
# 
# if [ -f $IT_LIB/cxf/spring-aop-3.0.5.RELEASE.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/spring-aop-3.0.5.RELEASE.jar
# fi
# 
# if [ -f $IT_LIB/cxf/spring-asm-3.0.5.RELEASE.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/spring-asm-3.0.5.RELEASE.jar
# fi
# 
# if [ -f $IT_LIB/cxf/spring-beans-3.0.5.RELEASE.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/spring-beans-3.0.5.RELEASE.jar
# fi
# 
# if [ -f $IT_LIB/cxf/spring-context-3.0.5.RELEASE.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/spring-context-3.0.5.RELEASE.jar
# fi
# 
# if [ -f $IT_LIB/cxf/spring-core-3.0.5.RELEASE.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/spring-core-3.0.5.RELEASE.jar
# fi
# 
# if [ -f $IT_LIB/cxf/spring-expression-3.0.5.RELEASE.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/spring-expression-3.0.5.RELEASE.jar
# fi
# 
# if [ -f $IT_LIB/cxf/stax2-api-3.1.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/stax2-api-3.1.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/woodstox-core-asl-4.1.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/woodstox-core-asl-4.1.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/wsdl4j-1.6.2.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/wsdl4j-1.6.2.jar
# fi
# 
# if [ -f $IT_LIB/cxf/wss4j-1.6.1.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/wss4j-1.6.1.jar
# fi
# 
# if [ -f $IT_LIB/cxf/xmlschema-core-2.0.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/cxf/xmlschema-core-2.0.jar
# fi

# SshTrilead
add_to_classpath $IT_LIB/sshTrilead
# if [ -f $IT_LIB/sshTrilead/trilead-ssh2-build213-RK.jar ]
# then
#     CLASSPATH=$CLASSPATH:$IT_LIB/sshTrilead/trilead-ssh2-build213-RK.jar
# fi

# Apache
add_to_classpath $IT_LIB/apache

# Optimis Components
add_to_classpath $IT_LIB/optimis



export CLASSPATH


## Set up the JVM properties

#LOCAL
JAVACMD=$JAVA_HOME/bin/java"\
	-Dlog4j.configuration=$IT_HOME/log/it-log4j \
	-Dit.project.file=$projFile \
	-Dit.resources.file=$resFile  \
	-Dit.lib=$IT_HOME/integratedtoolkit/lib  \
        -Dit.lang=java \
        -Dit.appName=$fullAppPath \
	-Dit.to.file=false \
	-Dit.graph=true \
	-Dit.monitor=1000\
	-Dit.context=/home/flordan/optimis_context\
	-Dit.tracing=false \
	-Dit.component=mainmaster \
	-Dit.component.scheduler=mainmaster \
	-Dit.gat.broker.adaptor=sshtrilead,azure \
	-Dit.gat.file.adaptor=sshtrilead,azure"

#REMOTE
#JAVACMD=$JAVA_HOME/bin/java"\cat it.
#	-Dlog4j.configuration=$IT_HOME/log/it-log4j \
#	-Dit.project.file=$projFile \
#	-Dit.resources.file=$resFile  \
#	-Dit.lib=$IT_HOME/integratedtoolkit/lib
#       -Dit.lang=java \
#       -Dit.appName=$fullAppPath \
#	-Dit.to.file=false \
#	-Dit.graph=true \
#	-Dit.monitor=1000\
#	-Dit.context=/home/flordan/optimis_context\
#	-Dit.tracing=false \
#	-Dit.gat.broker.adaptor=sshtrilead,azure \
#	-Dit.gat.file.adaptor=sshtrilead,azure"

#-Dlog4j.configuration=file:$GAT_LOCATION/log4j.properties
#-Dit.appName=$fullAppPath \

export JAVACMD

