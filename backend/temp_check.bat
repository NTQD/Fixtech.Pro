@echo off
docker exec -i backend-db-1 mysql -uroot -prootpassword techfix_catalog -e "SELECT id, name FROM services;" > check_output.txt 2>&1
echo Done >> check_output.txt
exit
