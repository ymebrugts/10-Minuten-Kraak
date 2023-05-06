
# 10 Minuten Kraak | 10 Minute Heist

A Multiplayer game using JavaFX and RMI.

## Getting Started

There are two projects available:
- The legacy Java 8 version 
- The modernized Java 17 version which supports Maven

Clone the repository and open the project in an IDE like IntelliJ.

## Run Project

#### Java 8 project
1. Under "File -> Project Structure" select:
   - Project
       - SDK: Java 1.8
       - Language Level: SDK Default
   - Module -> Sources
       - Mark "src" folder as sources root 
	   - Set Language level to 8
   - Libraries 
       - Add the two jars as dependencies by clicking on the plus and selecting "Java"

   In the project files navigate to FXApplication and right click and click "Run FXApplication() Main" to run the application
2. Run FXApplication

#### Java 17 project
1. Under "File -> Project Structure" select:
   - Project
       - SDK: Java 17
       - Language Level: SDK Default
   - Module -> Sources
       - Mark Java folder as sources root 
       - Mark Resources folder as resources root
	   - Set Language level to 17

    In the project files navigate to FXApplication and right click and click "Run FXApplication() Main" to run the application

2. Run "mvn clean install"
3. Run FXApplication


### Prerequisites
- Java 8 or Java 17
- Maven installed
- IntelliJ IDE is recommended

## Deployment

Simply create a Maven fat JAR on the Java 17 version

Creating a JAR for the Java 8 version is a little harder because you need to create a fat JAR manually.

## Built With
* [JAVA](https://www.java.com/nl/) - Java 8 and 17
* [RMI](https://docs.oracle.com/javase/tutorial/rmi/overview.html) - Java RMI Client-Server
* [JavaFX](https://gluonhq.com/products/javafx/) - The Graphics Engine
* [Maven](https://maven.apache.org/) - Dependency Management

## Authors

* **Yme Brugts** - *Contributor* - [YmeBrugts](https://github.com/ymebrugts/)
* **Wesley Richardson** - *Contributor* - [Asonn](https://github.com/asonn)

See also the list of [contributors](https://github.com/ymebrugts/10-Minuten-Kraak/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details
