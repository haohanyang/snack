# Download the latest server jar file from GitHub Actions Artifacts
# and unzip the file in the current directory

import requests
import logging
import os

REPO = "haohanyang/snack"
GH_TOKEN = os.getenv("GH_TOKEN")

if __name__ == "__main__":
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

    with open(f"{artifact_name}.zip", "wb") as f:
        f.write(response.content)

    logger.info("Download complete")

    # Unzip the artifact
    logger.info(f"Unzipping artifact {artifact_name}.zip")

    os.system(f"unzip {artifact_name}.zip")

    # Delete the zip file
    os.system(f"rm {artifact_name}.zip")

    logger.info("Unzip complete")
