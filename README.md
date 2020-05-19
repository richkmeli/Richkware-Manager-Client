# Richkware-Manager-Client

[![Build Status](https://travis-ci.org/richkmeli/Richkware-Manager-Client.svg?branch=master)](https://travis-ci.org/richkmeli/Richkware-Manager-Client)
[![](https://jitpack.io/v/richkmeli/Richkware-Manager-Client.svg)](https://jitpack.io/#richkmeli/Richkware-Manager-Client)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/941c900cff06436ab420754cd5bfa26b)](https://app.codacy.com/app/richkmeli/Richkware-Manager-Client?utm_source=github.com&utm_medium=referral&utm_content=richkmeli/Richkware-Manager-Client&utm_campaign=Badge_Grade_Dashboard)

Client of **Richkware-Manager-Server**, that it obtains the list of all hosts from the server and it's able to send any kind of commands to them.

## Related Projects

[Richkware](https://github.com/richkmeli/Richkware): Framework for building Windows malware.

[Richkware-Manager-Server](https://github.com/richkmeli/Richkware-Manager-Server): Service for the management of hosts in which is present an instance of malware developed using **Richkware** framework.

![](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/Diagram/RichkwareDiagram1.2.png)

## GUI

![Secure Connection](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/GUI/RMC/RMC_secureconnection.PNG)

![Login](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/GUI/RMC/RMC_login.PNG)

![Reverse Commands](https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/GUI/RMC/RMC_reversecommands.PNG)

## Get Started

To obtain the jar file, you can download it from the [RMC Releases](https://github.com/richkmeli/Richkware-Manager-Client/releases), instead if you want to build the jar file by yourself, you need to download:

-   java
-   maven

### Compile

To compile the project and generate the jar file, you have to run:

    mvn package
    
After the generation of the jar file, located in the folder "target", you can open the GUI, executing the following command:

    java -jar target/RichkwareManagerClient-XXX-jar-with-dependencies.jar

## IDE

This project is developed with Intellij IDEA.
[Open Sources Licences](https://www.jetbrains.com/opensource/) provided by JetBrains.

<img src="https://raw.githubusercontent.com/richkmeli/richkmeli.github.io/master/Richkware/Jetbrains/jetbrains.svg" width="100" alt="Jetbrains Logo"/>
