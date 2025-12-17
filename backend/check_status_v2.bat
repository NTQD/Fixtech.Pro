@echo off
echo Checking Docker Status > status_log.txt
docker ps >> status_log.txt 2>&1
echo ---------------------------------------- >> status_log.txt
echo Checking DB Charset >> status_log.txt
docker exec -i backend-db-1 mysql -uroot -prootpassword -e "SHOW VARIABLES LIKE 'character_set%';" >> status_log.txt 2>&1
echo ---------------------------------------- >> status_log.txt
echo Checking Sample Data >> status_log.txt
docker exec -i backend-db-1 mysql -uroot -prootpassword techfix_catalog -e "SELECT name, description FROM parts LIMIT 3;" >> status_log.txt 2>&1
type status_log.txt
exit
