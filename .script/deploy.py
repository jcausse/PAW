#!/usr/bin/env python3

# Ahhhh yes, I could have just used a pipeline, but I am not a repo admin

"""
Deploy the project to the server using Maven and SSH.
"""

import os
from subprocess import run, CalledProcessError
from configparser import RawConfigParser, NoSectionError, NoOptionError
from typing import Dict


SECRETS_FILENAME = './.script/deploy_secrets.properties'
SECRETS_SECTION = 'DEPLOYMENT'
SECRETS_OPTIONS = [
    'ssh_username', 
    'ssh_password', 
    'ssh_server', 
    'ssh_port', 
    'sftp_username', 
    'sftp_password', 
    'sftp_server'
]

WAR_FILENAME = './webapp/target/webapp.war'
TARGET_FILENAME = 'app.war'

BUILD_LOG_FILENAME = 'build.log'


def clean() -> None:
    """
    Clean the project using Maven.
    """
    print("Cleaning...", end=' ', flush=True)
    try:
        with open(os.devnull, 'w', encoding='utf-8') as devnull:
            run(['mvn', 'clean'], check=True, stdout=devnull, stderr=devnull)
        print("Done.")
    except CalledProcessError:
        print("Clean failed.")
        raise


def build() -> None:
    """
    Build the project using Maven.
    """
    print("Building...", end=' ', flush=True)
    try:
        with open(BUILD_LOG_FILENAME, 'w', encoding='utf-8') as f:
            run(['mvn', 'package', '-DskipTests'], check=True, stdout=f)
        print(f"Done. See {BUILD_LOG_FILENAME} for details.")
    except CalledProcessError:
        print(f"Build failed. See {BUILD_LOG_FILENAME} for details.")
        raise


def load_secrets(filename: str) -> Dict[str, str]:
    """
    Load the secrets from the file.
    @param filename: The filename of the secrets file.
    @return: A dictionary with the secrets.
    """
    print("Loading secrets...", end=' ', flush=True)
    if not os.path.exists(filename):
        print(f"Secrets file not found at {filename}.")
        raise FileNotFoundError

    config = RawConfigParser()
    config.read(filename)

    secrets = {}

    try:
        for option in SECRETS_OPTIONS:
            secrets[option] = config.get(SECRETS_SECTION, option)
    except NoSectionError:
        print(f"Section {SECRETS_SECTION} not found in {filename}.")
        raise
    except NoOptionError:
        print(f"Option {option} not found in {filename}.")
        raise

    print("Done.")
    return secrets


def upload_file(secrets: Dict[str, str]) -> None:
    """
    Upload the WAR file to the server via SCP using password authentication.
    @param secrets: Dictionary containing SSH connection details.
    """
    print("Uploading...", end=' ', flush=True)
    if not os.path.exists(WAR_FILENAME):
        print(f"Error: WAR file not found at {WAR_FILENAME}")
        raise FileNotFoundError

    username = secrets['ssh_username']
    password = secrets['ssh_password']
    server = secrets['ssh_server']
    port = secrets['ssh_port']
    remote_path = f"/home/{username}/{TARGET_FILENAME}"

    command = [
        'sshpass', '-p', password,
        'scp',
        '-P', port,
        '-o', 'StrictHostKeyChecking=no',
        WAR_FILENAME,
        f"{username}@{server}:{remote_path}"
    ]

    try:
        with open(os.devnull, 'w', encoding='utf-8') as devnull:
            run(command, check=True, stdout=devnull, stderr=devnull)
        print(f"Done. Uploaded to {username}@{server}:{remote_path}.")
    except CalledProcessError as e:
        print(f"Error uploading file: {e}")
        raise
    except FileNotFoundError:
        print("Error: sshpass or scp command not found. Please install sshpass and OpenSSH client.")
        raise


def deploy(secrets: Dict[str, str]) -> None:
    """
    Deploy the project to the server using SFTP.
    @param secrets: Dictionary containing SSH connection details.
    """
    print("Deploying...", end=' ', flush=True)

    sftp_username = secrets['sftp_username']
    sftp_password = secrets['sftp_password']
    sftp_server = secrets['sftp_server']

    ssh_username = secrets['ssh_username']
    ssh_password = secrets['ssh_password']
    ssh_server = secrets['ssh_server']
    ssh_port = secrets['ssh_port']

    sftp_command = f"sshpass -p {sftp_password} sftp {sftp_username}@{sftp_server} << 'EOF' > /dev/null 2>&1 \n \
        put /home/{ssh_username}/{TARGET_FILENAME} web/app.war > /dev/null 2>&1 \n \
        bye > /dev/null 2>&1 \nEOF"

    ssh_command = [
        'sshpass', '-p', ssh_password,
        'ssh',
        '-p', ssh_port,
        '-o', 'StrictHostKeyChecking=no',
        f"{ssh_username}@{ssh_server}",
        sftp_command
    ]

    try:
        run(ssh_command, check=True)
        print(f"Done. Deployed to {sftp_username}@{sftp_server}:web/app.war.")
    except CalledProcessError as e:
        print(f"Error deploying file: {e}")
        raise
    except FileNotFoundError:
        print("Error: sshpass or ssh command not found. Please install sshpass and OpenSSH client.")
        raise

def remove_war_file(secrets: Dict[str, str]) -> None:
    """
    Remove the WAR file from the server.
    @param secrets: Dictionary containing SSH connection details.
    """
    print("Removing WAR file...", end=' ', flush=True)

    ssh_username = secrets['ssh_username']
    ssh_password = secrets['ssh_password']
    ssh_server = secrets['ssh_server']
    ssh_port = secrets['ssh_port']

    ssh_command = [
        'sshpass', '-p', ssh_password,
        'ssh',
        '-p', ssh_port,
        '-o', 'StrictHostKeyChecking=no',
        f"{ssh_username}@{ssh_server}",
        f"rm /home/{ssh_username}/{TARGET_FILENAME}"
    ]

    try:
        run(ssh_command, check=True)
        print("Done.")
    except CalledProcessError as e:
        print(f"Error removing WAR file: {e}")
        raise
    except FileNotFoundError:
        print("Error: sshpass or ssh command not found. Please install sshpass and OpenSSH client.")
        raise


def main():
    """
    Deploy the project to the server using Maven and SSH.
    """
    try:
        clean()
        build()
        secrets = load_secrets(SECRETS_FILENAME)
        upload_file(secrets)
        deploy(secrets)
        remove_war_file(secrets)
    except Exception:
        print("=== DEPLOYMENT FAILED ===")
    finally:
        clean()

if __name__ == "__main__":
    main()
