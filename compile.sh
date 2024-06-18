#!/bin/bash
find . -name "*.class" -type f -delete
javac *.java
java Main