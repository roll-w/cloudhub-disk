# Cloudhub Disk

A cloud-disk-like web application based on SpringBoot and
[Cloudhub File System](https://github.com/Roll-W/cloudhub).

## Features

- User management (register, login, logout, etc.) and user group management
- File (include folder as well, the same below) management: 
includes upload, download, delete, rename, move, copy, etc.
- File sharing
- File preview: includes image, video, audio, documents (PDF only) and text files 
- File search
- Favorite files
- Trash
- File history
- File permission management
- Simple data statistics

## Requirements

- Java 17
- [Cloudhub File System](https://github.com/Roll-W/cloudhub) deployed
- MySQL 8.0

## Getting Started

1. Build and Package

    Run `mvn package` to build and package the project.
    
    For the frontend, run `npm install` and `npm run build` to build the frontend.

2. Deploy
    
    Uncompress the built package in your server.

3. Configure

    Edit `cloudhub.conf` in the `conf` folder, read the comments in the file for more information.

4. Run

    Run the shell script `start-disk-client-server.sh` in the `sbin` folder to start the server.

## License 

```text
Copyright (C) 2023 RollW

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
