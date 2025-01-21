#!/usr/bin/env bash

mkdir -p .github/workflows

echo "Copying Server workflows..."
cp -R server/.github/workflows/* .github/workflows/

echo "workflows have been set up successfully."