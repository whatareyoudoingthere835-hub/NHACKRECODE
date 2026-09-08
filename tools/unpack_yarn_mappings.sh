#!/bin/sh
# Extracts bundled yarn 1.21.11 mappings into clients/yarn-mappings/ (gitignored working copy).
cd "$(dirname "$0")/.." && mkdir -p clients && tar xzf tools/yarn-1.21.11-mappings.tar.gz -C clients && mv clients/mappings clients/yarn-mappings.tmp 2>/dev/null; rm -rf clients/yarn-mappings; mv clients/yarn-mappings.tmp clients/yarn-mappings; echo done
