echo %1
java -Dspring.profiles.active=hotel,product -jar batch.jar %1 
echo "waiting for executed¡£¡£¡£"