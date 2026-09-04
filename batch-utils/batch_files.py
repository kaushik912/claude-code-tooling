#!/usr/bin/env python3
"""
Batch File Concatenator
Creates markdown files containing batches of source code files with their paths and contents.
"""

import os
import re
import uuid
import argparse
from pathlib import Path
from typing import List, Set


def get_fence(content: str) -> str:
    """Return a backtick fence longer than any backtick run in content, so nested ``` blocks don't break it."""
    max_run = max((len(m.group()) for m in re.finditer(r'`+', content)), default=0)
    return '`' * max(3, max_run + 1)


def get_code_files(directory: str, extensions: Set[str]) -> List[Path]:
    """Recursively get all code files with specified extensions."""
    code_files = []
    exclude_dirs = {
        '__pycache__', '.git', '.pytest_cache', 'node_modules',
        '.venv', 'venv', 'env', '.idea', '.vscode', 'dist', 'build','target'
    }
    exclude_files = {'.pyc', '.pyo', '.pyd', '.so', '.dll', '.dylib'}

    for root, dirs, files in os.walk(directory):
        # Remove excluded directories from traversal
        dirs[:] = [d for d in dirs if d not in exclude_dirs]

        for file in files:
            file_path = Path(root) / file

            # Skip if file extension is in exclude list
            if file_path.suffix in exclude_files:
                continue

            # Check if file matches our criteria
            if file_path.suffix in extensions or file_path.name in extensions:
                code_files.append(file_path)

    return sorted(code_files)


def create_batch_markdown(files: List[Path], output_file: str, base_dir: str):
    """Create a markdown file with concatenated file contents."""
    with open(output_file, 'w', encoding='utf-8') as out_f:
        out_f.write(f"# Source Code Batch\n\n")
        out_f.write(f"This file contains {len(files)} source files.\n\n")
        out_f.write("---\n\n")

        for file_path in files:
            relative_path = file_path.relative_to(base_dir)
            out_f.write(f"## File: {relative_path}\n\n")

            try:
                with open(file_path, 'r', encoding='utf-8') as in_f:
                    content = in_f.read()
            except Exception as e:
                content = f"# Error reading file: {e}\n"

            if not content.endswith('\n'):
                content += '\n'

            # Unique ID sentinels are the real block boundary for unbatch_files.py;
            # the fence itself is cosmetic (syntax highlighting), so nested ``` in
            # content can't cause mis-extraction the way fence-only matching could.
            file_id = uuid.uuid4().hex[:8]
            fence = get_fence(content)
            out_f.write(f"<!-- FILE_ID:{file_id} -->\n")
            out_f.write(f"{fence}{get_language_from_extension(file_path.suffix, file_path.name)}\n")
            out_f.write(content)
            out_f.write(f"{fence}\n")
            out_f.write(f"<!-- END_FILE_ID:{file_id} -->\n\n")
            out_f.write("---\n\n")


def get_language_from_extension(ext: str, filename: str = '') -> str:
    """Map file extension to markdown syntax highlighting language."""
    # Check for specific filenames first
    if filename in ['.gitignore', '.env.example', '.env']:
        return 'bash'
    if filename == 'Dockerfile':
        return 'dockerfile'
    if filename == 'docker-compose.yml':
        return 'yaml'

    # Then check extensions
    mapping = {
        '.py': 'python',
        '.js': 'javascript',
        '.ts': 'typescript',
        '.java': 'java',
        '.cpp': 'cpp',
        '.c': 'c',
        '.go': 'go',
        '.rs': 'rust',
        '.rb': 'ruby',
        '.php': 'php',
        '.sh': 'bash',
        '.yaml': 'yaml',
        '.yml': 'yaml',
        '.json': 'json',
        '.xml': 'xml',
        '.html': 'html',
        '.css': 'css',
        '.sql': 'sql',
        '.md': 'markdown',
        '.txt': 'text',
    }
    return mapping.get(ext.lower(), '')


def main():
    parser = argparse.ArgumentParser(
        description='Batch source code files into markdown documents'
    )
    parser.add_argument(
        'directory',
        help='Directory to process'
    )
    parser.add_argument(
        '--batch-size',
        type=int,
        default=5,
        help='Number of files per batch (default: 5)'
    )
    parser.add_argument(
        '--output-prefix',
        default='SOURCE_CODE_BATCH',
        help='Prefix for output files (default: SOURCE_CODE_BATCH)'
    )
    parser.add_argument(
        '--extensions',
        nargs='+',
        default=[
            '.py', '.yml', '.yaml', '.sh', '.txt', '.md',
            'Dockerfile', 'docker-compose.yml', '.env.example', '.gitignore'
        ],
        help='File extensions or names to include (default: all code and config files)'
    )
    parser.add_argument(
        '--output-dir',
        default='.',
        help='Directory to save output files (default: current directory)'
    )

    args = parser.parse_args()

    # Convert directory to absolute path
    directory = Path(args.directory).resolve()

    if not directory.exists():
        print(f"Error: Directory '{directory}' does not exist")
        return 1

    # Convert extensions to set
    extensions = set(args.extensions)

    # Create output directory if it doesn't exist
    output_dir = Path(args.output_dir)
    output_dir.mkdir(parents=True, exist_ok=True)

    # Get all code files
    print(f"Scanning directory: {directory}")
    code_files = get_code_files(directory, extensions)

    if not code_files:
        print("No code files found!")
        return 1

    print(f"Found {len(code_files)} code files")
    print(f"Creating batches of {args.batch_size} files each...")
    print(f"Output directory: {output_dir}")

    # Create batches
    batch_count = 0
    for i in range(0, len(code_files), args.batch_size):
        batch_files = code_files[i:i + args.batch_size]
        batch_count += 1

        output_file = output_dir / f"{args.output_prefix}_{batch_count}.md"
        print(f"Creating {output_file} ({len(batch_files)} files)...")

        create_batch_markdown(batch_files, str(output_file), directory)

    print(f"\nDone! Created {batch_count} batch file(s)")
    return 0


if __name__ == '__main__':
    exit(main())
