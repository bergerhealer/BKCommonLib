#!/bin/bash
# This script is used to import jar files into this /lib local maven repository
# Please set the spigot build tools path accordingly
# Usage: add_server.sh [version]
#        add_server.sh 1.12.1

# Make sure this is correct!
spigotbuild_dir=C:/Users/QT/Desktop/SpigotBuild

# Rest is (should be) magic
spigot_version=$1
#spigot_version=1.12.1
lib_version=${spigot_version}-R0.1-SNAPSHOT
mvn_dir=${spigotbuild_dir}/apache-maven-3.2.5/bin
spigot_lib_path=${spigotbuild_dir}/spigot-${spigot_version}.jar
repo_dir=$PWD
cd $mvn_dir
mvn org.apache.maven.plugins:maven-install-plugin:2.5.2:install-file  \
    -Dfile=${spigot_lib_path} \
    -DgroupId=org.spigotmc -DartifactId=spigot \
    -Dversion=${lib_version} -Dpackaging=jar \
    -DlocalRepositoryPath=${repo_dir}