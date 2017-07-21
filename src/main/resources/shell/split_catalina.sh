####所谓的切割其实是重命名####
#!/bin/bash
yesterday=`date -d'1 day ago' +%Y-%m-%d`
_dir_tomcat=/home/app/tomcats/
for list in `ls $_dir_tomcat`
do
    if [ -f ${_dir_tomcat}${list}/logs/catalina.out ];
        then
            cp ${_dir_tomcat}${list}/logs/catalina.out ${_dir_tomcat}${list}/logs/catalina.out_${yesterday}
            echo "" > ${_dir_tomcat}${list}/logs/catalina.out
            find ${_dir_tomcat}${list}/logs/ -type f -mtime +30 -delete
    fi
done




####所谓的切割其实是重命名####
#!/bin/bash 

cd `dirname $0` 
d=`date +%Y%m%d` 
d7=`date -d'7 day ago' +%Y%m%d` 

cd ../logs/ 

cp catalina.out catalina.out.${d} 
echo "" > catalina.out 
rm -rf catalina.out.${d7} 
