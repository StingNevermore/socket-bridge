[![Socket Bridge CI](https://github.com/StingNevermore/socket-bridge/actions/workflows/ci.yml/badge.svg)](https://github.com/StingNevermore/socket-bridge/actions/workflows/ci.yml)
# Socket Bridge

Socket Bridge is a tool that provides networking socket bridging capabilities with a client-server architecture.

## Overview

Socket Bridge consists of two main components:
- A server application that handles socket bridging operations
- A CLI tool for controlling the server

The project is built using:
- Kotlin/Java programming languages
- Spring Boot framework
- Netty for network communication
- Picocli for command-line interface
- GraalVM for native image compilation

## Features

- Start/stop/query status of the socket bridge server
- Socket forwarding and bridging
- Command-line interface for easy management

## Installation

### Prerequisites
- JDK 11 or later
- GraalVM (for native image compilation)

### Building from Source

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/socket-bridge.git
   cd socket-bridge
   ```

2. Build the project using Gradle:
   ```
   ./gradlew build
   ```

3. Build the native CLI tool:
   ```
   ./gradlew nativeCompile
   ```

## Usage

### CLI Commands

The CLI tool supports the following commands:

```
socket-bridge-cli <command> [<args>]
```

Available commands:
- `start` - Start the socket bridge server
- `stop` - Stop the running server
- `status` - Check server status

For more information on a specific command:
```
socket-bridge-cli help <command>
```

### Configuration

The server can be configured through JVM options in the `config/jvm.options` file located in your Socket Bridge installation directory.

## Development

### Project Structure

- `server/` - Socket Bridge server implementation
- `cli-tools/` - Command-line interface implementation
- `libs/` - Shared libraries
  - `cli-server-communication/` - Communication protocol between CLI and server

### Building

```
./gradlew build
```

## License

This project is licensed under the terms found in the LICENSE file.

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.
