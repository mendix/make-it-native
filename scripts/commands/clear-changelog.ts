import fs from "fs";
import path from "path";
import {StoreName} from "./types";

const baseDir = path.join(__dirname, "../../");

export function clearChangelog(storeName: StoreName): void {
    console.log("Clear changelog");
    const changeLogPath = getChangelogPathByStoreName(storeName);
    fs.writeFileSync(changeLogPath, '');

    console.log("Clear changelog completed")
}

function getChangelogPathByStoreName(storeName: StoreName): string {
    switch (storeName) {
        case "google-play":
            return `${baseDir}/CHANGELOG.android.txt`;
        case "apple-store":
            return `${baseDir}/CHANGELOG.ios.txt`;
    }
    throw new Error(`Changelog path for storeName: ${storeName} not found`)
}