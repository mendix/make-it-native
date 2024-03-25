import fs from "fs";
import semver from "semver";
import path from "path";
import {StoreName} from "./types";

const baseDir = path.join(__dirname, "../../");

export function bumpVersion(storeName: StoreName): string {
    const packageJsonPath = `${baseDir}/package.json`;
    const packageJson = require(packageJsonPath);

    const oldVersion = packageJson["store-versions"][storeName];
    const newVersion = semver.inc(oldVersion, "patch");

    console.log(`Update ${storeName} version from ${oldVersion} to ${newVersion}`)

    packageJson["store-versions"][storeName] = newVersion
    fs.writeFileSync(packageJsonPath, JSON.stringify(packageJson, null, 2));
    console.log("Version update completed")
    return `${newVersion}`;
}