# Minecraft Dependency Packager

**This is a simple method to package with maven with all dependency declared in pom and rewrite in plugin.yml without write it every time**

## What does it do

- Put in plugin.yml a list of compile or runtime dependency

## How to implement

To implement with Maven, add the plugin in your build on your pom

```
<plugin>
	<groupId>io.github.lorisdemicheli</groupId>
	<artifactId>minecraft-package</artifactId>
	<version>1.0.1</version>
	<executions>
		<execution>
			<goals>
				<goal>build-jar</goal>
			</goals>
		</execution>
	</executions>
</plugin>
```

## How to use

- with commandline run 'mvn package'
- with eclipse run as maven with goals 'package'
			