ADELE-Common
============

Defines set of consistent versions of ADELE frameworks and provides tools to generate a ready to use distribution "a la carte" of adele frameworks.

Maven Repository
=============


    <repositories>
        <repository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>adele-central-snapshot</id>
          <name>adele-repos</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </repository>
        <repository>
          <snapshots />
          <id>snapshots</id>
          <name>adele-central-release</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
          <snapshots>
            <enabled>false</enabled>
          </snapshots>
          <id>central</id>
          <name>adele-repos</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </pluginRepository>
        <pluginRepository>
          <snapshots />
          <id>snapshots</id>
          <name>adele-central-release</name>
          <url>http://maven.dynamis-technologies.com/artifactory/adele-repos</url>
        </pluginRepository>
    </pluginRepositories>

