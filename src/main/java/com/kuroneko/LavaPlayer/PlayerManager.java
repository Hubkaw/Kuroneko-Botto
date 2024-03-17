package com.kuroneko.LavaPlayer;

import com.kuroneko.Config.KuronekoEmbed;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class PlayerManager {
    private static PlayerManager INSTANCE;
    private final Map<Long, GuildMusicManager> musicManagers;
    private final AudioPlayerManager audioPlayerManager;

    public PlayerManager() {
        musicManagers = new HashMap<>();
        audioPlayerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerLocalSource(audioPlayerManager);
        AudioSourceManagers.registerRemoteSources(audioPlayerManager);
    }

    public GuildMusicManager getMusicManager(Guild guild) {
        return this.musicManagers.computeIfAbsent(guild.getIdLong(), (guildId) -> {
            final GuildMusicManager guildMusicManager = new GuildMusicManager(audioPlayerManager);
            guild.getAudioManager().setSendingHandler(guildMusicManager.sendHandler);
            return guildMusicManager;
        });
    }

    public void loadAndPlay(Guild guild, InteractionHook event, String trackUrl) {
        final GuildMusicManager musicManager = getMusicManager(guild);
        audioPlayerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack audioTrack) {
                musicManager.scheduler.queue(audioTrack);

                MessageEmbed embed = new KuronekoEmbed().setTitle("Song added")
                        .setDescription(audioTrack.getInfo().title + " has been added.")
                        .setThumbnail("http://img.youtube.com/vi/"+audioTrack.getInfo().identifier+"/0.jpg")
                        .build();
                event.sendMessageEmbeds(embed).complete().delete().queueAfter(12, TimeUnit.SECONDS);
            }

            @Override
            public void playlistLoaded(AudioPlaylist audioPlaylist) {
                final List<AudioTrack> tracks = audioPlaylist.getTracks();
                if (trackUrl.startsWith("ytsearch")) {
                    AudioTrack audioTrack = tracks.get(0);
                    musicManager.scheduler.queue(audioTrack);
                    MessageEmbed embed = new KuronekoEmbed().setTitle("Song added")
                            .setThumbnail("http://img.youtube.com/vi/"+audioTrack.getInfo().identifier+"/0.jpg")
                            .setDescription(audioTrack.getInfo().title)
                            .build();
                    event.sendMessageEmbeds(embed).complete().delete().queueAfter(12, TimeUnit.SECONDS);
                } else {
                    tracks.forEach(
                            musicManager.scheduler::queue
                    );
                    MessageEmbed embed = new KuronekoEmbed()
                            .setTitle("Playlist added")
                            .setDescription("Songs queued: "+(musicManager.scheduler.getQueue().size()+1))
                            .setThumbnail("http://img.youtube.com/vi/"+tracks.get(0).getInfo().identifier+"/0.jpg")
                            .build();
                    event.sendMessageEmbeds(embed).complete().delete().queueAfter(12, TimeUnit.SECONDS);
                }
            }

            @Override
            public void noMatches() {
                MessageEmbed embed = new KuronekoEmbed().setTitle("I couldn't find it Senpai")
                        .setDescription("No results for: " + trackUrl)
                        .build();
                event.sendMessageEmbeds(embed).complete().delete().queueAfter(12, TimeUnit.SECONDS);
            }

            @Override
            public void loadFailed(FriendlyException e) {
                e.printStackTrace();
                MessageEmbed embed = new KuronekoEmbed().setTitle("I cant play this Senpai")
                        .setDescription("Its disabled in my country, private or age restricted >.<")
                        .build();
                event.sendMessageEmbeds(embed).complete().delete().queueAfter(12, TimeUnit.SECONDS);
            }

        });
    }

    public static PlayerManager getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new PlayerManager();
        return INSTANCE;
    }
}