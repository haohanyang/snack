# Download the latest client build artifact of GitHub Actions
# and unzip the file to /var/www/snack-client

import requests
import logging
import os
from pathlib import Path

REPO = os.environ["CLIENT_REPO"]
GH_TOKEN = os.environ["CLIENT_GH_TOKEN"]
DOWNLOAD_DIR = "/var/www"

if __name__ == "__main__":
    # List artifacts for the repository
    # /repos/{owner}/{repo}/actions/artifacts
    logger = logging.getLogger(__name__)
    url = f"https://api.github.com/repos/{REPO}/actions/artifacts"

    artifacts_info = requests.get(
        url,
        headers={
            "Accept": "application/vnd.github+json",
            "X-GitHub-Api-Version": "2022-11-28",
            "Authorization": f"Bearer {GH_TOKEN}",
        },
    ).json()

    # Check if the request was successful
    if "total_count" not in artifacts_info:
        logger.error(f"Failed to list artifacts for {REPO}")
        logger.error(artifacts_info["message"])
        exit(1)

    # Make sure the directory exists
    if not (Path(DOWNLOAD_DIR).exists() and Path(DOWNLOAD_DIR).is_dir()):
        logger.error(f"Download directory {DOWNLOAD_DIR} does not exist")
        exit(1)

    total_count = artifacts_info["total_count"]
    logger.info(f"Total count: {total_count}")

    artifacts = artifacts_info["artifacts"]

    if len(artifacts) == 0:
        logger.info("No artifacts found")
        exit(0)

    latest_artifact = artifacts[0]

    artifact_id = latest_artifact["id"]
    artifact_name = latest_artifact["name"]
    workflow_head_sha = latest_artifact["workflow_run"]["head_sha"]

    if latest_artifact["expired"]:
        logger.error(f"Artifact {artifact_id} is expired")
        exit(0)

        # Download the latest artifact
    archive_download_url = latest_artifact["archive_download_url"]
    logger.info(f"Downloading artifact {artifact_id} from {archive_download_url}")

    response = requests.get(
        archive_download_url,
        headers={
            "Accept": "application/vnd.github+json",
            "X-GitHub-Api-Version": "2022-11-28",
            "Authorization": f"Bearer {GH_TOKEN}",
        },
    )

    with open(f"{DOWNLOAD_DIR}/{artifact_name}.zip", "wb") as f:
        f.write(response.content)

    logger.info("Download complete")

    # Unzip the artifact
    logger.info(f"Unzipping artifact {artifact_name}.zip")

    # Create the directory if not exists
    if not (
        Path(f"{DOWNLOAD_DIR}/snack-website").exists()
        and Path(f"{DOWNLOAD_DIR}/snack-website").is_dir()
    ):
        os.system(f"mkdir {DOWNLOAD_DIR}/snack-website")
    else:
        # Remove the directory if exists
        os.system(f"rm -rf {DOWNLOAD_DIR}/snack-website")
    os.system(
        f"unzip {DOWNLOAD_DIR}/{artifact_name}.zip -d {DOWNLOAD_DIR}/snack-website"
    )

    logger.info("Unzip complete")
