# GitHub Actions - Automated File Unbatching

This guide shows how to automatically unbatch files using GitHub Actions when you have a repository with only:
- `batched_output/` directory (containing batch markdown files)
- `unbatch_files.py` script

## Repository Structure

```
sample/
├── .github/
│   └── workflows/
│       └── unbatch-files.yml
├── batched_output/
│   ├── SOURCE_CODE_BATCH_1.md
│   ├── SOURCE_CODE_BATCH_2.md
│   ├── SOURCE_CODE_BATCH_3.md
│   └── SOURCE_CODE_BATCH_4.md
└── unbatch_files.py
```

After the workflow runs, it will create:

```
sample/
├── .github/
│   └── workflows/
│       └── unbatch-files.yml
├── batched_output/
│   └── ...
├── extracted/              # ← NEW: Auto-generated
│   ├── Dockerfile
│   ├── app/
│   │   ├── __init__.py
│   │   ├── config.py
│   │   ├── main.py
│   │   └── ...
│   └── ...
└── unbatch_files.py
```

## Setup Steps

### 1. Create the GitHub Actions Workflow

Create `.github/workflows/unbatch-files.yml`:

```yaml
name: Unbatch Files and Commit Results

on:
  workflow_dispatch: {}
  push:
    branches: [ "main" ]
    paths:
      - "batched_output/**"
      - "unbatch_files.py"
      - ".github/workflows/unbatch-files.yml"

permissions:
  contents: write

jobs:
  unbatch-and-commit:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repo
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version: "3.11"

      - name: Run unbatch script
        run: |
          echo "Running unbatch_files.py on batched_output/"
          python unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir extracted

      - name: Commit and push extracted files
        run: |
          git config user.name "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"

          # Add the extracted files directory
          git add extracted/ || true

          # Only commit if there are changes
          if git diff --cached --quiet; then
            echo "No changes to commit - files already extracted."
            exit 0
          fi

          git commit -m "Auto-extract files from batched_output [skip ci]"
          git push
```

### 2. Commit and Push

```bash
git add .github/workflows/unbatch-files.yml
git add batched_output/
git add unbatch_files.py
git commit -m "Add unbatch workflow"
git push
```

## How It Works

### Triggers

The workflow runs automatically when:

1. **Manual trigger**: Go to **Actions** tab → **Unbatch Files and Commit Results** → **Run workflow**
2. **Auto trigger**: When you push changes to:
   - Any files in `batched_output/`
   - The `unbatch_files.py` script
   - The workflow file itself

### What It Does

1. **Checkout**: Checks out your repository
2. **Setup Python**: Installs Python 3.11
3. **Run Script**: Executes `unbatch_files.py` to extract files to `extracted/` directory
4. **Commit**: Commits the extracted files back to the repository
5. **Push**: Pushes changes to the `main` branch

### Preventing Infinite Loops

The commit message includes `[skip ci]` which prevents the workflow from triggering itself again after committing.

## Usage Examples

### Example 1: First Time Setup

```bash
# 1. Clone your repo
git clone https://github.com/your-username/sample.git
cd sample

# 2. Add the workflow file
mkdir -p .github/workflows
# Copy the workflow YAML content to .github/workflows/unbatch-files.yml

# 3. Commit and push
git add .github/workflows/unbatch-files.yml
git commit -m "Add unbatch workflow"
git push

# 4. Go to GitHub Actions tab and run the workflow manually
# OR just wait for it to run automatically on the next push to batched_output/
```

### Example 2: Update Batch Files

```bash
# 1. Update batch files in batched_output/
# (copy new SOURCE_CODE_BATCH_*.md files)

# 2. Commit and push
git add batched_output/
git commit -m "Update batch files"
git push

# 3. Workflow runs automatically and extracts new files to extracted/
```

### Example 3: Verify Extraction

After the workflow runs:

```bash
# Pull the latest changes
git pull

# Check the extracted directory
ls -la extracted/

# View the files
cat extracted/Dockerfile
cat extracted/app/main.py
```

## Customization Options

### Change Output Directory

To extract to a different directory (e.g., `src/` instead of `extracted/`):

```yaml
- name: Run unbatch script
  run: |
    python unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir src
```

And update the git add command:

```yaml
- name: Commit and push extracted files
  run: |
    # ...
    git add src/ || true
    # ...
```

### Change Batch File Pattern

If your batch files have a different naming pattern:

```yaml
- name: Run unbatch script
  run: |
    python unbatch_files.py batched_output/MY_BATCH_*.md --output-dir extracted
```

### Add Dependencies

If `unbatch_files.py` requires additional packages:

```yaml
- name: Set up Python
  uses: actions/setup-python@v5
  with:
    python-version: "3.11"

- name: Install dependencies
  run: |
    python -m pip install --upgrade pip
    pip install -r requirements.txt

- name: Run unbatch script
  run: |
    python unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir extracted
```

## Troubleshooting

### Workflow Not Running

**Check permissions**:
- Go to **Settings** → **Actions** → **General**
- Under "Workflow permissions", select "Read and write permissions"
- Save

### Permission Denied on Push

If the `main` branch is protected:

**Option 1**: Exclude GitHub Actions bot from branch protection
- Go to **Settings** → **Branches**
- Edit the `main` branch protection rule
- Check "Allow specified actors to bypass required pull requests"
- Add `github-actions[bot]`

**Option 2**: Create a Pull Request instead (see advanced section below)

### No Files Extracted

Check the Actions log:
1. Go to **Actions** tab
2. Click on the failed workflow run
3. Expand the "Run unbatch script" step
4. Look for error messages

Common issues:
- Batch files don't match the pattern `SOURCE_CODE_BATCH_*.md`
- `unbatch_files.py` has a bug
- Markdown format is incorrect

## Advanced: Create Pull Request Instead of Direct Push

If you want to review changes before merging:

```yaml
name: Unbatch Files and Create PR

on:
  workflow_dispatch: {}
  push:
    branches: [ "main" ]
    paths:
      - "batched_output/**"

permissions:
  contents: write
  pull-requests: write

jobs:
  unbatch-and-pr:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repo
        uses: actions/checkout@v4

      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version: "3.11"

      - name: Run unbatch script
        run: |
          python unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir extracted

      - name: Create Pull Request
        uses: peter-evans/create-pull-request@v5
        with:
          commit-message: "Auto-extract files from batched_output"
          title: "Auto-extract files from batch"
          body: "Automated extraction of files from batched_output/ directory"
          branch: auto-unbatch
          delete-branch: true
```

## Complete Workflow Example

Here's a production-ready workflow with error handling:

```yaml
name: Unbatch Files and Commit Results

on:
  workflow_dispatch:
    inputs:
      output_dir:
        description: 'Output directory for extracted files'
        required: false
        default: 'extracted'
  push:
    branches: [ "main" ]
    paths:
      - "batched_output/**"
      - "unbatch_files.py"

permissions:
  contents: write

jobs:
  unbatch-and-commit:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout repo
        uses: actions/checkout@v4
        with:
          fetch-depth: 0

      - name: Set up Python
        uses: actions/setup-python@v5
        with:
          python-version: "3.11"

      - name: Verify batch files exist
        run: |
          if ! ls batched_output/SOURCE_CODE_BATCH_*.md 1> /dev/null 2>&1; then
            echo "Error: No batch files found in batched_output/"
            exit 1
          fi
          echo "Found batch files:"
          ls -lh batched_output/SOURCE_CODE_BATCH_*.md

      - name: Run unbatch script
        run: |
          OUTPUT_DIR="${{ github.event.inputs.output_dir || 'extracted' }}"
          echo "Running unbatch_files.py..."
          echo "Output directory: $OUTPUT_DIR"
          python unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir "$OUTPUT_DIR"

      - name: Show extracted files
        run: |
          OUTPUT_DIR="${{ github.event.inputs.output_dir || 'extracted' }}"
          echo "Extracted files:"
          find "$OUTPUT_DIR" -type f | sort

      - name: Commit and push extracted files
        run: |
          OUTPUT_DIR="${{ github.event.inputs.output_dir || 'extracted' }}"

          git config user.name "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"

          git add "$OUTPUT_DIR/" || true

          if git diff --cached --quiet; then
            echo "✅ No changes to commit - files already up to date."
            exit 0
          fi

          git commit -m "Auto-extract files from batched_output [skip ci]"
          git push

          echo "✅ Successfully extracted and committed files!"
```

## Summary

With this setup:
1. ✅ Push batch files to `batched_output/`
2. ✅ GitHub Actions automatically runs unbatch script
3. ✅ Extracted files are committed to `extracted/`
4. ✅ No local terminal needed
5. ✅ Full automation

Your collaborators can simply clone the repo and get the extracted files immediately!
