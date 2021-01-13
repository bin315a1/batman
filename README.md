# Batman

## Table of Contents
1. [Overview](#overview)
2. [Setup](#setup)
4. [Useful Commands](#useful-commands)

## Overview

Brief overview of the project setup

#### Database
Project currently uses AWS DynamoDB. A localized DynamoDB instance with persisting data volume can be launched on port 8042 by:
1. cd into ${projectRoot}/dockerfiles
2. run `docker-compose -f docker-compose-local-dynamodb.yml up`

#### API Documentation
The project is set up to use Swagger2 for API documentation. To view all API description, run the server, visit `/swagger-ui.html` for documentation ui

## Setup

coming soon

## Useful Commands
#### ssh into a docker container
1. `docker ps`, and copy the container id
2. `docker exec -it <containerID> /bin/bash`

#### Sample DynamoDB Queries
1. Scan an entire table
`aws dynamodb scan --endpoint-url http://localhost:8042/ --table-name dbusers`
2. Create a table with json input ``

#### 