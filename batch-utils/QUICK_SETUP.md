# Quick Setup - Unbatch Repository

## TL;DR - 5 Minute Setup

### For a New Repository

```bash
# 1. Create new repo
mkdir sample && cd sample
git init

# 2. Copy required files
cp /path/to/unbatch_files.py .
cp -r /path/to/batched_output .

# 3. Create workflow directory
mkdir -p .github/workflows

# 4. Create workflow file
cat > .github/workflows/unbatch-files.yml << 'EOF'
name: Unbatch Files and Commit Results

on:
  workflow_dispatch: {}
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
      - uses: actions/checkout@v4
      - uses: actions/setup-python@v5
        with:
          python-version: "3.11"
      - run: python unbatch_files.py batched_output/SOURCE_CODE_BATCH_*.md --output-dir extracted
      - run: |
          git config user.name "github-actions[bot]"
          git config user.email "github-actions[bot]@users.noreply.github.com"
          git add extracted/ || true
          git diff --cached --quiet || git commit -m "Auto-extract [skip ci]" && git push
EOF

# 5. Commit everything
git add .
git commit -m "Initial commit"

# 6. Push to GitHub
git remote add origin https://github.com/yourusername/sample.git
git branch -M main
git push -u origin main
```

### Enable GitHub Actions

1. Go to **Settings** → **Actions** → **General**
2. Select **"Read and write permissions"**
3. Click **Save**

### Run the Workflow

1. Go to **Actions** tab
2. Click **"Unbatch Files and Commit Results"**
3. Click **"Run workflow"**
4. Wait ~30 seconds
5. Check the **"extracted/"** directory in your repo!

## Files Structure

Your repo should have:

```
sample/
├── .github/
│   └── workflows/
│       └── unbatch-files.yml    # ← Workflow
├── batched_output/              # ← Your batch files
│   ├── SOURCE_CODE_BATCH_1.md
│   ├── SOURCE_CODE_BATCH_2.md
│   └── ...
└── unbatch_files.py             # ← Extraction script
```

After workflow runs:

```
sample/
├── .github/
├── batched_output/
├── extracted/                    # ← NEW! Auto-generated
│   ├── Dockerfile
│   ├── app/
│   └── ...
└── unbatch_files.py
```

## Common Commands

```bash
# Clone and see extracted files
git clone https://github.com/yourusername/sample.git
cd sample
ls -la extracted/

# Update batch files
cp new_batch.md batched_output/
git add batched_output/
git commit -m "Update batches"
git push
# → Workflow runs automatically

# Pull the auto-extracted files
git pull
```

## Troubleshooting

### Workflow not running?
→ Check Settings → Actions → Enable "Read and write permissions"

### No files extracted?
→ Check Actions tab logs for errors

### Want to run locally?
```bash
python unbatch_files.py batched_output/*.md --output-dir extracted
```

## That's It!

Your repository now automatically unbatches files whenever you update `batched_output/`.

For more details, see [GITHUB_ACTIONS_UNBATCH.md](GITHUB_ACTIONS_UNBATCH.md)
