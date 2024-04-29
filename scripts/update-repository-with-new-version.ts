import minimist from "minimist";
import {bumpVersion} from "./commands/bump-version";
import {clearChangelog} from "./commands/clear-changelog";
import {StoreName} from "./commands/types";
import {commitChanges} from "./commands/commit";


interface Arguments {
    'store-name': StoreName
}


export function updateRepositoryWithNewVersion(storeName: StoreName) {
    const newVersion = bumpVersion(storeName);
    clearChangelog(storeName);
    const message = `[RELEASE] Bump version to ${newVersion} for ${storeName}`;
    commitChanges(message);
}

const args = minimist<Arguments>(process.argv);
updateRepositoryWithNewVersion(args['store-name']);


