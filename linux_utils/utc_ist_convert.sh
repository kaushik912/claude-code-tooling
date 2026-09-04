#!/bin/bash
# UTC <-> IST time converter. IST = UTC + 5:30, no DST.
#
# Usage:
#   utc_ist_convert.sh ist HH:MM     -> prints UTC HH:MM (+ cron "min hour" fields)
#   utc_ist_convert.sh utc HH:MM     -> prints IST HH:MM
#   utc_ist_convert.sh               -> prints current UTC and IST
#
# Examples:
#   $ utc_ist_convert.sh ist 23:30
#   IST 23:30 -> UTC 18:00
#   cron fields (system TZ=UTC): 00 18
#
#   $ utc_ist_convert.sh ist 00:05      # wraps to previous UTC day
#   IST 00:05 -> UTC 18:35
#   cron fields (system TZ=UTC): 35 18
#
#   $ utc_ist_convert.sh utc 18:14
#   UTC 18:14 -> IST 23:44
#
#   $ utc_ist_convert.sh
#   Now UTC: 18:12
#   Now IST: 23:42

set -e

usage() {
    echo "Usage: $0 ist HH:MM | utc HH:MM | (no args = now)"
    echo
    echo "Examples:"
    echo "  $0 ist 23:30      # -> UTC 18:00, cron fields: 00 18"
    echo "  $0 utc 18:14      # -> IST 23:44"
    echo "  $0                # current UTC and IST"
    exit 1
}

to_utc_from_ist() {
    local h=$1 m=$2
    local total=$(( h * 60 + m - 330 ))       # IST is UTC+5:30
    total=$(( (total % 1440 + 1440) % 1440 )) # wrap into [0,1440)
    printf "%02d:%02d\n" $(( total / 60 )) $(( total % 60 ))
}

to_ist_from_utc() {
    local h=$1 m=$2
    local total=$(( h * 60 + m + 330 ))
    total=$(( (total % 1440 + 1440) % 1440 ))
    printf "%02d:%02d\n" $(( total / 60 )) $(( total % 60 ))
}

parse_hm() {
    local hm=$1
    if [[ ! $hm =~ ^([0-1]?[0-9]|2[0-3]):([0-5][0-9])$ ]]; then
        echo "Invalid time '$hm', expected HH:MM (24h)" >&2
        exit 1
    fi
    echo "${BASH_REMATCH[1]#0} ${BASH_REMATCH[2]#0}"
}

case "$1" in
    ist)
        [[ -n $2 ]] || usage
        read -r h m <<< "$(parse_hm "$2")"
        utc=$(to_utc_from_ist "$h" "$m")
        IFS=: read -r uh um <<< "$utc"
        echo "IST $2 -> UTC $utc"
        echo "cron fields (system TZ=UTC): $um $uh"
        ;;
    utc)
        [[ -n $2 ]] || usage
        read -r h m <<< "$(parse_hm "$2")"
        ist=$(to_ist_from_utc "$h" "$m")
        echo "UTC $2 -> IST $ist"
        ;;
    "")
        echo "Now UTC: $(date -u +%H:%M)"
        echo "Now IST: $(TZ=Asia/Kolkata date +%H:%M)"
        ;;
    *)
        usage
        ;;
esac
