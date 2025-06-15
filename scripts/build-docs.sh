#!/bin/bash

# Build documentation locally
echo "🔧 Building CloudPayments SDK Documentation..."

# Install dependencies
echo "📦 Installing dependencies..."
yarn install

# Generate API docs (temporarily disabled due to MDX compilation issues)
# echo "📚 Generating API documentation..."
# yarn generate-api

# Build Docusaurus site
echo "🏗️ Building Docusaurus site..."
yarn docs:build

echo "✅ Documentation built successfully!"
echo "📁 Output: docs/build/"
echo "🌐 To serve locally: yarn docs:dev" 