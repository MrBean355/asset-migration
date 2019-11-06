# Asset Migration

Replaces usages of old assets with the corresponding new ones.

## Usage

1. Download the JAR from the [latest build](https://github.com/MrBean355/asset-migration/actions?query=branch%3Amaster+is%3Acompleted).
2. Run the program: `java -jar downloaded-file.jar [args]`
3. Available arguments:
    1. `--include [directory]`: Include a directory in the migration. Can provide multiple times. Must provide at least one.
    2. `--dry-run`: Perform a dry run; don't actually modify any files.
    3. `--output [file]`: Log to a file. Optional; logs to console if omitted.
4. Example command 1: `java -jar assets-migrator-1.0.0.jar --dry-run --include first-repo`
5. Example command 2: `java -jar assets-migrator-1.0.0.jar --include first-repo --include second-repo --output log.txt`
