[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Hibernate](https://img.shields.io/badge/Hibernate-59666C?style=for-the-badge&logo=Hibernate&logoColor=white)](https://hibernate.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![JDA](https://img.shields.io/badge/JDA-5865F2?style=for-the-badge&logo=discord&logoColor=white)](https://github.com/discord-jda/JDA)
[![Jenkins](https://img.shields.io/badge/Jenkins-D24939?style=for-the-badge&logo=jenkins&logoColor=white)](https://www.jenkins.io/)
# Kuroneko Botto
Kuroneko is a discord bot that plays music, rolls dice, tracks League of Legends ranks and helps with online ttrpg.  
To check her commands type '/' and discord will suggest them.    
You can add Kuroneko to your discord server [here](https://discord.com/api/oauth2/authorize?client_id=738883479902617670&permissions=8&scope=bot)  
or visit kuroneko.ano.ninja.
  
## Features
## Music Playback
- Play songs from YouTube (links or search)
- Support for playlists with shuffling, skipping, loop, queue display
- Joins your voice channel and handles multiple servers at once
## TTRPG
- Advanced dice rolling: /roll (public) and /stealth-roll (private, optional GM reveal)
  - Supports complex expressions like 4d6, 2e10 (exploding dice), 2d10+3 (single modifier), 5d4+*2 (per-roll modifier)
  - Custom avatars per channel/user for rolls
- Create characters with stats and portraits, then perform skill checks
- Interactive Initiative Tracker: Add multiple characters, roll initiatives, sort order, finish with a button
- Searchable D&D 5e Spellbook: /dnd-spell with autocomplete and detailed embeds (fetched from official API)
## League of Legends Integration
- Register summoners with /register-lol <riotID> for channel-specific tracking (multiple summoners per channel)
- Tracks rank changes (Solo/Duo & Flex), LP, wins/losses, tier promotions/demotions
- Detects champion mastery milestones
- Updates every 5 minutes via Riot API 
- Daily sync of champion/icons data
## Extras
- Comprehensive async logging of bot calls
- Will post Rumia if provoked
- Runs 24/7 on a $1.49 vps
- Always up to date with main branch thanks to Jenkins

### Tech stack
- Spring Boot 3
- Hibernate with PostgreSQL
- APIs: Riot Games (R4J library), D&D 5e, Lavaplayer (with youtube-source)
- Thymeleaf for the invite page
- Hosted with Jenkins

 ## Contributions
Contributions are welcome!  
If you have ideas, bug reports, or want to add features, open an issue or PR.  
Questions? Reach out to me directly on discord @hubkaw.

### Creating Commands
The simples way to create a new command is:
- Prepare local configuration according to ConfigLoader class (Only discord token and database login/password are required) 
- Create a @Component that implements SlashInteraction interface in the Interaction package. SlashInteractionManager will find and add the command automatically.
- Implement the methods (autocomplete is optional, just turn it off in CommandData)
- Test if it works!
