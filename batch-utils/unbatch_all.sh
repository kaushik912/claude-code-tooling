#!/usr/bin/env bash
set -euo pipefail

ROOT="${1:-$(pwd)}"
DRY_RUN=0

if [[ "${1:-}" == "--dry-run" ]]; then
  DRY_RUN=1
  ROOT="${2:-$(pwd)}"
fi

run_unbatch() {
  local batch_dir="$1"
  local output_dir="$2"

  local cmd=(python3 batch-utils/unbatch_files.py "$batch_dir"/SOURCE_CODE_BATCH_*.md --output-dir "$output_dir")

  if (( DRY_RUN )); then
    printf '%q ' "${cmd[@]}"
    printf '\n'
  else
    "${cmd[@]}"
  fi
}

cd "$ROOT"

while IFS= read -r batch_dir; do
  base_name="$(basename "$batch_dir")"

  if [[ "$base_name" == *_batched_output ]]; then
    output_dir="${batch_dir%_batched_output}_unbatched"
  elif [[ "$base_name" == batched_output ]]; then
    output_dir="${batch_dir%/batched_output}/extracted"
  elif [[ "$base_name" == batched_output_* ]]; then
    output_dir="${batch_dir/batched_output/extracted}"
  elif [[ "$batch_dir" == *batched_output* ]]; then
    output_dir="${batch_dir/batched_output/extracted}"
  else
    continue
  fi

  run_unbatch "$batch_dir" "$output_dir"
done < <(find "$ROOT" -type d -name '*batched_output*' | sort)
