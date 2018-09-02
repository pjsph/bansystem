# BanSystem
A ban system spigot plugin

Current version: 1.4

This plugin is [available on Spigotmc](https://www.spigotmc.org/resources/bansystem.60483/).

## Features

- Ban a player permanently
- Ban a player with a specified time
- Unban a player

- Database connection or hard disk saving system

## Commands

`/ban <player> perm <reason>` : Permanent ban
`/ban <player> <duration>:<time unit> <reason>` : Temporary ban
`/unban <player>` : Unban
`/bansystem` : Basic plugin command

## Configuration

*config.yml*
"use_db" : Use database if 'true' or hard disk saving system if 'false'
"url" : Url of the database to connect to
"port" : The port of the connection
"db_name" : The database name you use
"username" : The username of the database
"password" : The password of the database

*bans.yml & infos.yml*
This is where banned players and some crucial informations for the plugin are stored. **So don't touch these files!**

## Other features
If you want to move your storage from hard disk to database (or the opposite), follow these steps:

1. Run your server with your default stockage system (ex: hard disk)
1. While your server is running, change config.yml to the other stockage system (ex: database)
1. Execute this command: bansystem reload (with a "/" if you are in the game)
1. Your informations have now been moved to the other stockage system! (ex: database)
