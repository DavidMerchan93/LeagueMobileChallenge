#!/bin/bash

# Pre-commit setup script for League Mobile Challenge
echo "Setting up pre-commit hooks..."

# Check if pre-commit is installed
if ! command -v pre-commit &> /dev/null; then
    echo "Installing pre-commit..."
    pip3 install pre-commit
fi

# Install the pre-commit hook
echo "Installing pre-commit hooks..."
pre-commit install

# Run hooks on all files to verify setup
echo "Testing pre-commit hooks..."
pre-commit run --all-files

echo "Pre-commit setup complete!"
echo ""
echo "Available commands:"
echo "  pre-commit run --all-files    # Run hooks on all files"
echo "  pre-commit run --files <file> # Run hooks on specific files"
echo "  ./gradlew qualityCheck        # Run ktlint and detekt manually"