# Asset Migration

Replaces usages of old assets with the corresponding new ones.

## Usage

1. Download the JAR from the [latest build](https://github.com/MrBean355/asset-migration/actions?query=branch%3Amaster+is%3Acompleted).
2. Run the program: `java -jar downloaded-file.jar [args]`
3. Available arguments (order doesn't matter):
    1. `--mapping [file]`      : CSV file to read mappings from. Required.
    2. `--include [directory]` : Include a directory in the migration. Can provide multiple times. Must provide at least one.
    3. `--delete`              : Delete old assets instead of replacing usages. 
    4. `--dry-run`             : Perform a dry run; don't actually modify any files.
4. Example command 1: `java -jar assets-migrator-1.0.0.jar --mapping assets.csv --include first-repo --dry-run `
5. Example command 2: `java -jar assets-migrator-1.0.0.jar --mapping assets.csv --include first-repo --include second-repo --delete`
