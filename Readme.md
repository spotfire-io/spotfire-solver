# Spotfire Solver

This is the Optaplanner service that applies the algorithm for optimizing a playlist.


## Requirements
1. ASDF
2. AWS Account
3. A Spotify Account


## Setup
* `asdf install` to install the required tools specified in the `.tool-versions` file.

## Deployment
* `./gradlew deploy`

## Running Locally
1. Boot up the `spotfire-website` and the `spotfire-api` locally.
2. Retrieve the `access token` from the running instance of the website, by opening it in your browser, clicking 
on the arrow on the top right corner and selecting `Copy Access Token`.
3. Add environment variables to the running config for the `SolverHanlderSpec`. Required env are:
    - AWS_PROFILE
    - ACCESS_TOKEN (the one copied on step 2)
4. Import a playlist using the `spotfire-website`
5. Retrieve the file location for the imported playlist from the logs in the `spotfire-api`
6. Add it to the `EXTRACT_PATH` in the `SolverHandlerSpec`.
7. Run the `SolverHandlerSpec`
