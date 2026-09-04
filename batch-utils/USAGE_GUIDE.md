# Batch File Utilities

Two utilities for batching source code files into markdown documents and extracting them back.

## batch_files.py

Recursively scans a directory and creates markdown files containing batches of source code files.

### Usage

```bash
python3 batch_files.py <directory> [options]
```

### Options

- `--batch-size N`: Number of files per batch (default: 5)
- `--output-prefix PREFIX`: Prefix for output files (default: SOURCE_CODE_BATCH)
- `--output-dir DIR`: Directory to save output files (default: current directory)
- `--extensions EXT1 EXT2 ...`: File extensions or names to include (default: .py Dockerfile)

### Examples

```bash
# Batch all Python files and Dockerfiles, 5 files per batch
python3 batch_files.py . --batch-size 5

# Batch to a specific output directory
python3 batch_files.py . --batch-size 5 --output-dir batched_output

# Include YAML files (docker-compose.yml) and output to a folder
python3 batch_files.py . --batch-size 5 --extensions .py .yml .yaml Dockerfile --output-dir batched_output

# Batch with custom prefix and output directory
python3 batch_files.py /path/to/project --batch-size 10 --output-prefix MY_CODE --output-dir my_batches

# Include additional file types
python3 batch_files.py . --batch-size 5 --extensions .py .js .ts Dockerfile docker-compose.yml
```

### What it does

- Recursively finds all specified file types in the directory
- Excludes generated/build files (__pycache__, .git, node_modules, etc.)
- Creates markdown files with format:
  ```
  ## File: relative/path/to/file.py

  ```python
  [file contents]
  ```
  ```
- Each batch file contains the specified number of source files

### Example for this project

```bash
# Batch to current directory
python3 batch_files.py . --batch-size 5 --output-prefix CODE_BATCH

# Batch to output folder (recommended)
python3 batch_files.py . --batch-size 5 --extensions .py .yml .yaml Dockerfile --output-dir batched_output
```

This created 4 files in `batched_output/`:
- SOURCE_CODE_BATCH_1.md (5 files)
- SOURCE_CODE_BATCH_2.md (5 files)
- SOURCE_CODE_BATCH_3.md (5 files)
- SOURCE_CODE_BATCH_4.md (2 files)

Total: 17 Python, YAML, and Dockerfile files

## unbatch_files.py

Extracts source code files from batch markdown files back to their original structure.

### Usage

```bash
python3 unbatch_files.py <batch_file1> [batch_file2 ...] [options]
```

### Options

- `--output-dir DIR`: Output directory for extracted files (default: extracted)
- `--dry-run`: Show what would be extracted without creating files

### Examples

```bash
# Extract all batch files to 'extracted' directory
python3 unbatch_files.py CODE_BATCH_*.md

# Extract from a folder to a custom directory
python3 unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir unbatched_output

# Extract to a custom directory
python3 unbatch_files.py CODE_BATCH_*.md --output-dir my_project

# Dry run to see what would be extracted
python3 unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir unbatched_output --dry-run
```

### What it does

- Parses batch markdown files
- Extracts file paths and contents
- Recreates the directory structure
- Writes files to their original relative paths

## Features

### Excluded Directories
- `__pycache__`, `.pytest_cache`
- `.git`, `.venv`, `venv`, `env`
- `node_modules`
- `.idea`, `.vscode`
- `dist`, `build`

### Excluded File Extensions
- `.pyc`, `.pyo`, `.pyd`
- `.so`, `.dll`, `.dylib`

### Supported Languages (syntax highlighting)
Python, JavaScript, TypeScript, Java, C/C++, Go, Rust, Ruby, PHP, Bash, YAML, JSON, XML, HTML, CSS, SQL

## Workflow

1. **Batch files**: Create markdown batches for sharing or backup
   ```bash
   python3 batch_files.py . --batch-size 5 --extensions .py .yml .yaml Dockerfile --output-dir batched_output
   ```

2. **Share**: Send the generated batch files from `batched_output/`

3. **Extract**: Recreate the files from batch markdown
   ```bash
   # Preview what will be extracted
   python3 unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir unbatched_output --dry-run

   # Extract for real
   python3 unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir unbatched_output
   ```

## Complete Example

```bash
# 1. Batch files to batched_output directory
python3 batch_files.py . --batch-size 5 --extensions .py .yml .yaml Dockerfile --output-dir batched_output

# 2. Unbatch files to unbatched_output directory
python3 unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir unbatched_output

# 3. Verify they match (optional)
diff -r app/ unbatched_output/app/
```

## Use Cases

- Sharing code in contexts where file attachments are limited
- Creating readable code documentation
- Backing up code in markdown format
- Code review with all files in one document
- Feeding code to AI systems with context preservation
