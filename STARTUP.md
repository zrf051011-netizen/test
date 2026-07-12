# Dormitory System Startup

This project is a Java Web application built with Servlet, JSP, Maven, Tomcat 9, and MySQL 8.

## Local Environment Found

- Java: `E:\java`, Java 18
- Maven: bundled with IDEA at `E:\idea\IntelliJ IDEA 2024.3.1.1\plugins\maven\lib\maven3\bin\mvn.cmd`
- MySQL client: `D:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe`
- Tomcat: configured as a project-local tool under `tools\apache-tomcat-9.0.120`

## Build

In PowerShell:

```powershell
.\scripts\build-war.ps1
```

Output:

```text
target\dormitory-system.war
```

If `mvn` is not available on `PATH`, the build script looks for IDEA's bundled Maven first. If that is also missing, it downloads Apache Maven into `tools\apache-maven-*`, verifies the SHA512 checksum, and uses that local copy.

You can also install the project-local Maven explicitly:

```powershell
.\scripts\setup-maven.ps1
```

In IDEA, use the run configuration named `Build WAR`.

## Start Locally

Install project-local Tomcat 9:

```powershell
.\scripts\setup-tomcat9.ps1
```

Start the application:

```powershell
.\scripts\start-local.ps1
```

The startup script also sets Tomcat URI/JVM encoding to UTF-8.

Open:

```text
http://localhost:8080/dormitory-system/
```

Stop Tomcat:

```powershell
.\scripts\stop-local.ps1
```

## Database

The application expects:

```text
database: dormitory_system
user: root
password: 123456
```

The config file is:

```text
src/main/resources/c3p0-config.xml
```

The SQL script recreates tables, so only run it when existing local data can be overwritten:

```powershell
.\scripts\import-db.ps1 -Force
```

If your MySQL password is different:

```powershell
.\scripts\import-db.ps1 -Password "your_password" -Force
```

Then update `src/main/resources/c3p0-config.xml` with the same password.

## Run With IDEA

1. Open this directory in IDEA.
2. Wait for Maven import to finish.
3. Use Tomcat 9, not Tomcat 10. The local Tomcat home is `tools\apache-tomcat-9.0.120`.
4. Add a local Tomcat run configuration.
5. Deploy `dormitory-system:war exploded` or `target\dormitory-system.war`.
6. Set application context to `/dormitory-system`.
7. Open `http://localhost:8080/dormitory-system/`.

## Login Accounts

All initial passwords are `admin123`.

```text
admin
zhanglou
wanglou
lisi
wangwu
```
