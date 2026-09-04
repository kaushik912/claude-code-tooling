#!/usr/bin/env bash
# Recursively remove .log files from a directory.
#
# Usage:
#   clear_log_files.sh [--dry-run] DIRECTORY
#
# Examples:
#   clear_log_files.sh /var/tmp/my-app
#   clear_log_files.sh --dry-run ~/logs

set -euo pipefail

usage() {
    echo "Usage: $0 [--dry-run] DIRECTORY"
    echo
    echo "Recursively remove regular files ending in .log from DIRECTORY."
    echo "Use --dry-run to list files without removing them."
}

dry_run=false
if [[ ${1:-} == "--dry-run" ]]; then
    dry_run=true
    shift
fi

if [[ ${1:-} == "-h" || ${1:-} == "--help" ]]; then
    usage
    exit 0
fi

if [[ $# -ne 1 ]]; then
    usage >&2
    exit 1
fi

directory=$1
if [[ ! -d $directory ]]; then
    echo "Not a directory: $directory" >&2
    exit 1
fi

count=0
while IFS= read -r -d '' file; do
    if $dry_run; then
        printf 'Would remove: %s\n' "$file"
    else
        rm -- "$file"
        printf 'Removed: %s\n' "$file"
    fi
    count=$((count + 1))
done < <(find "$directory" -type f -name '*.log' -print0)

if $dry_run; then
    printf 'Found %d .log file(s).\n' "$count"
else
    printf 'Removed %d .log file(s).\n' "$count"
fi
