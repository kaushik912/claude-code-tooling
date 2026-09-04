#!/usr/bin/env python3
"""
Batch File Extractor
Extracts source code files from batch markdown files created by batch_files.py
"""

import os
import re
import argparse
from pathlib import Path
from typing import List, Tuple


def parse_batch_markdown(markdown_file: str) -> List[Tuple[str, str]]:
    """
    Parse a batch markdown file and extract file paths and contents.
    Returns a list of tuples: (relative_path, content)
    """
    with open(markdown_file, 'r', encoding='utf-8') as f:
        content = f.read()

    files = []
    # Split into one chunk per file on the "## File: " marker, so a chunk
    # never crosses into another file's content.
    chunks = re.split(r'\n## File: ', '\n' + content)

    for chunk in chunks[1:]:
        header_end = chunk.find('\n\n')
        if header_end == -1:
            continue
        file_path = chunk[:header_end].strip()
        rest = chunk[header_end + 2:]

        # The FILE_ID sentinel pair (with a backreference on the id) is the
        # real block boundary, so nested ``` inside the file's own content
        # can't be mistaken for the closing fence.
        block_match = re.search(
            r'<!-- FILE_ID:(\w+) -->\n`{3,}\w*\n(.*?)\n`{3,}\n<!-- END_FILE_ID:\1 -->',
            rest, re.DOTALL
        )
        if not block_match:
            continue
        file_content = block_match.group(2) + '\n'
        files.append((file_path, file_content))

    return files


def extract_files(batch_files: List[str], output_dir: str, dry_run: bool = False):
    """Extract files from batch markdown files to output directory."""
    total_files = 0

    for batch_file in batch_files:
        print(f"\nProcessing {batch_file}...")
        files = parse_batch_markdown(batch_file)

        for relative_path, content in files:
            output_path = Path(output_dir) / relative_path
            total_files += 1

            if dry_run:
                print(f"  Would create: {output_path}")
            else:
                # Create parent directories if they don't exist
                output_path.parent.mkdir(parents=True, exist_ok=True)

                # Write the file
                with open(output_path, 'w', encoding='utf-8') as f:
                    f.write(content)

                print(f"  Created: {output_path}")

    return total_files


def main():
    parser = argparse.ArgumentParser(
        description='Extract source code files from batch markdown files'
    )
    parser.add_argument(
        'batch_files',
        nargs='+',
        help='Batch markdown file(s) to process'
    )
    parser.add_argument(
        '--output-dir',
        default='extracted',
        help='Output directory for extracted files (default: extracted)'
    )
    parser.add_argument(
        '--dry-run',
        action='store_true',
        help='Show what would be extracted without creating files'
    )

    args = parser.parse_args()

    # Verify all batch files exist
    for batch_file in args.batch_files:
        if not Path(batch_file).exists():
            print(f"Error: File '{batch_file}' does not exist")
            return 1

    print(f"Output directory: {args.output_dir}")
    if args.dry_run:
        print("DRY RUN MODE - No files will be created\n")

    total_files = extract_files(args.batch_files, args.output_dir, args.dry_run)

    if args.dry_run:
        print(f"\nDry run complete. Would extract {total_files} file(s)")
    else:
        print(f"\nDone! Extracted {total_files} file(s) to '{args.output_dir}'")

    return 0


if __name__ == '__main__':
    exit(main())
