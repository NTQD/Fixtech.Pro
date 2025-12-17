@echo off
echo --- Docker PS --- > gateway_diag.txt
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}" >> gateway_diag.txt
echo. >> gateway_diag.txt
echo --- Netstat Port 3000 --- >> gateway_diag.txt
netstat -ano | findstr :3000 >> gateway_diag.txt
echo. >> gateway_diag.txt
echo --- API Gateway Logs --- >> gateway_diag.txt
docker logs --tail 20 backend-api-gateway-1 >> gateway_diag.txt 2>&1
exit
