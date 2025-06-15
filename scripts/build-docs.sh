#!/bin/bash

# Build documentation locally
echo "🔧 Building CloudPayments SDK Documentation..."

# Generate API docs
echo "📚 Generating API documentation..."
npm run generate-api

# Build Docusaurus site
echo "🏗️ Building Docusaurus site..."
cd docs
npm ci
npm run build

echo "✅ Documentation built successfully!"
echo "📁 Output: docs/build/"
echo "🌐 To serve locally: cd docs && npm run serve" 