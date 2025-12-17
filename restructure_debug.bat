@echo off
echo Starting restructuring > restructure.log
md frontend
if exist frontend (
    echo Frontend directory created >> restructure.log
) else (
    echo Frontend directory creation FAILED >> restructure.log
)
move .next frontend\ >> restructure.log 2>&1
move app frontend\ >> restructure.log 2>&1
move components frontend\ >> restructure.log 2>&1
move components.json frontend\ >> restructure.log 2>&1
move hooks frontend\ >> restructure.log 2>&1
move lib frontend\ >> restructure.log 2>&1
move next-env.d.ts frontend\ >> restructure.log 2>&1
move next.config.mjs frontend\ >> restructure.log 2>&1
move package-lock.json frontend\ >> restructure.log 2>&1
move package.json frontend\ >> restructure.log 2>&1
move pnpm-lock.yaml frontend\ >> restructure.log 2>&1
move postcss.config.mjs frontend\ >> restructure.log 2>&1
move public frontend\ >> restructure.log 2>&1
move styles frontend\ >> restructure.log 2>&1
move tsconfig.json frontend\ >> restructure.log 2>&1
move node_modules frontend\ >> restructure.log 2>&1
move fixtech.sql backend\docker\ >> restructure.log 2>&1
echo Done >> restructure.log
exit
