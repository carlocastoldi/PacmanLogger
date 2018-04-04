# PacmanLogger
![Lanterna screenshot](resources/PacmanLogger.png)
An application with terminal GUI inspired by htop that makes Arch pacman's logs easier to read. Written in Scala

## Installation
### Dependencies
Mandatory:
  - Java (jre)

### Package Manager
#### Arch Linux
Available via AUR here: https://aur.archlinux.org/packages/pacmanlogger-git/

### Manual (sbt)
Compiling:
```bash
git clone https://github.com/carlocastoldi/PacmanLogger.git
cd PacmanLogger
sbt assembly
cp target/scala-2.12/PacmanLogger-assembly-<version>.jar ./pacmanlogger.jar
```
Running:
```bash
java -jar pacmanlogger.jar
```

## Developing tools
  - Scala (http://www.scala-lang.org/)
  - lanterna-3.0.0 (https://github.com/mabe02/lanterna)
  - scala-parser-combinators (https://github.com/scala/scala-parser-combinators)
