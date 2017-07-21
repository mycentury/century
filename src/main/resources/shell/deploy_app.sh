#!/bin/bash
source /etc/profile
day=`date +%Y%m%d_%H%M%S`
BACKUP_HOME=/home/app/auto_dist/BACKUP
APPS_HOME=/home/app/apps
TOMCAT_HOME=/home/app/tomcats/tomcat8280_dd-switch-jrez
DIST_HOME=/home/app/auto_dist
WAR_NAME=dd-switch-jrez-prod_htask.war
DIST_NAME=dd-switch-jrez

if test -z $1; then
   echo "default war name"
else
   WAR_NAME=$1
fi

echo "Begin to unpack $WAR_NAME and  dist $DIST_NAME"
echo

cd $APPS_HOME
tar zcvf $BACKUP_HOME/$DIST_NAME.$day.tar.gz $DIST_NAME/


# mv $DIST_NAME/lvs.jsp $DIST_NAME/lvs2.jsp
echo sleep 10
sleep 10


cd $TOMCAT_HOME
./tomcatstop.sh



cd $APPS_HOME
rm $DIST_NAME -rf
mkdir $DIST_NAME
cp $DIST_HOME/$WAR_NAME $APPS_HOME/$DIST_NAME/$WAR_NAME -f
cd $DIST_NAME
jar xvf $WAR_NAME
rm $WAR_NAME -f

cd $TOMCAT_HOME
./tomcatstart.sh


echo
echo "Finish dist $DIST_NAME"