# Batch File Utilities

This directory contains utilities for batching and unbatching source code files into markdown documents.

## 📁 Directory Contents

### Core Scripts
- **batch_files.py** - Batch source files into markdown documents
- **unbatch_files.py** - Extract files from batch markdown back to original structure

### Documentation
- **USAGE_GUIDE.md** - Complete guide for batch/unbatch utilities
- **GITHUB_ACTIONS_UNBATCH.md** - GitHub Actions automation guide
- **QUICK_SETUP.md** - Quick start guide for GitHub Actions

### Setup & Demo
- **setup_unbatch_repo.sh** - Automated repository setup script
- **.github/workflows/** - GitHub Actions workflow files

### Example Data
- **batched_output/** - Example batch markdown files

## 🚀 Quick Start

### Create Batches

```bash
# From celonis-challenge directory
python batch_utils/batch_files.py . \
  --batch-size 5 \
  --extensions .py .yml .yaml Dockerfile \
  --output-dir batch_utils/batched_output
```

### Extract Files

```bash
python batch_utils/unbatch_files.py \
  batch_utils/batched_output/SOURCE_CODE_BATCH_*.md \
  --output-dir extracted
```

## 📚 Documentation

Start here:
1. **USAGE_GUIDE.md** - Learn about batch/unbatch utilities (detailed reference)
2. **QUICK_SETUP.md** - Set up GitHub Actions in 5 minutes
3. **GITHUB_ACTIONS_UNBATCH.md** - Detailed automation guide

## 💡 Use Cases

- Share code where file attachments are limited
- Create readable code documentation
- Automate code extraction via GitHub Actions
- Backup code in markdown format
- Feed code to AI systems with preserved context

## 🔧 Requirements

- Python 3.7+
- No external dependencies

## 📦 For Use in Another Repository

To use these utilities in a separate repository:

1. Copy `unbatch_files.py` to your repo
2. Copy `.github/workflows/unbatch-files.yml` to your repo
3. Follow instructions in `QUICK_SETUP.md`

Your repo structure should be:
```
your-repo/
├── .github/
│   └── workflows/
│       └── unbatch-files.yml
├── batched_output/
│   └── SOURCE_CODE_BATCH_*.md
└── unbatch_files.py
```

Push to GitHub and the files will be automatically extracted!
