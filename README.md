## Overview
This is a simple program that reads a CSV file containing bank transactions and generates an expenses spreadsheet.

It is intended to automate the budgeting and expense tracking that I used to do manually.

An example of that sheet is visible at https://docs.google.com/spreadsheets/d/1n2Dk5aA8060z4YQA-V04QVugAOScXeOv0A867uLnLUk/edit?usp=sharing

## Running

During development, use the bootRun task:

```
./gradlew bootRun --args='-i src/test/resources/import_manifest.json'
```

After building, use the executable jar:

```
java -jar build/libs/expense-0.0.1-SNAPSHOT.jar -i src/test/resources/import_manifest.json
```

## TODO
* Consider using BigDecimal instead of Double for amounts
* Support reports with dates from multiple periods
    * A quick and dirty option is to filter transactions by date
    * A better option is to make the output contain information for multiple periods
* Check TODOs in comments