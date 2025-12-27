# Richkware-Manager-Client (RMC)

[![Build Status](https://github.com/richkmeli/Richkware-Manager-Client/workflows/Build%20and%20Test/badge.svg)](https://github.com/richkmeli/Richkware-Manager-Client/actions)
[![](https://jitpack.io/v/richkmeli/Richkware-Manager-Client.svg)](https://jitpack.io/#richkmeli/Richkware-Manager-Client)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/941c900cff06436ab420754cd5bfa26b)](https://app.codacy.com/app/richkmeli/Richkware-Manager-Client?utm_source=github.com&utm_medium=referral&utm_content=richkmeli/Richkware-Manager-Client&utm_campaign=Badge_Grade_Dashboard)

**Richkware-Manager-Client (RMC)** is a desktop client for **Richkware-Manager-Server**. It retrieves the list of infected hosts from the server and provides a GUI to interact with them and send commands.

## Related Projects

- **[Richkware](https://github.com/richkmeli/Richkware)**: The C++ framework for building the malware agents.
- **[Richkware-Manager-Server](https://github.com/richkmeli/Richkware-Manager-Server)** (RMS): The backend service that manages the infected hosts.

![Diagram](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/Diagram/RichkwareDiagram1.2.png)

## GUI

### Secure Connection
![Secure Connection](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/GUI/RMC/RMC_secureconnection.PNG)

### Login
![Login](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/GUI/RMC/RMC_login.PNG)

### Reverse Commands
![Reverse Commands](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/GUI/RMC/RMC_reversecommands.PNG)

## Getting Started

### Requirements

- **Java 17** or higher
- **Maven 3.6+**

### Download

You can download the pre-compiled JAR from the [RMC Releases](https://github.com/richkmeli/Richkware-Manager-Client/releases) page.

### Compilation

Run the following command to compile the project and generate the JAR file:

```bash
mvn clean package
```

The executable JAR will be generated in the `target/` folder.

### Running

Execute the client using Java:

```bash
java -jar target/Richkware-Manager-Client-1.1.2-jar-with-dependencies.jar
```

## IDE Support

This project is developed using **IntelliJ IDEA**.

[![JetBrains Logo](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/Jetbrains/jetbrains.svg)](https://www.jetbrains.com/opensource/)

*Open Source License provided by JetBrains.*
