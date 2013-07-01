#!/usr/bin/env bash
# Cleans the old (created before today) DATEX files generated by WsDatex




######## CONFIGURATION ########

TMP_DIR=/opt/webservice/datex/tmp




######## MAIN PROGRAM ########

TODAY=$(date +%Y-%m-%d)
TODAY=2011-09-16

echo "Cleaning files created before $TODAY"

for FILE in $TMP_DIR/*datex*
do
    FILE_DATE=$(basename "$FILE" | cut -d'_' -f1)
    [[ "$FILE_DATE" < "$TODAY" ]] && rm -f "$FILE"
done