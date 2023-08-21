#!/bin/bash

lo=1
hi=1500
while [ "$((hi - lo))" -gt 1 ]; do
  mid=$((lo + (( hi - lo ) / 2)))
  echo "Memory $mid MB"

  if sbt "repeatRunWithMaxMem $mid";
  then hi=$mid;
  else lo=$mid; fi
done
echo "Failed with $lo MB, Succeeded with $hi MB"

if [ -n "$GITHUB_STEP_SUMMARY" ]; then
  echo "### Required $hi MB" >> "$GITHUB_STEP_SUMMARY"
fi
