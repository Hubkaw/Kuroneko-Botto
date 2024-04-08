package com.kuroneko.LavaPlayer;


import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class TrackScheduler extends AudioEventAdapter {
    private final AudioPlayer player;
    private final BlockingQueue<AudioTrack> queue;
    private boolean isLooped;


    public TrackScheduler(AudioPlayer player) {
        this.player = player;
        this.queue = new LinkedBlockingQueue<>();
        this.isLooped = false;
    }

    public void queue(AudioTrack track) {
        if (!player.startTrack(track, true)) {
            queue.offer(track);
        }
    }

    public void nextTrack() {
        player.startTrack(queue.poll(), false);
    }

    public void shuffle(){
        List<AudioTrack> temporaryQueue = new ArrayList<>(queue.stream().toList());
        Collections.shuffle(temporaryQueue);
        this.queue.clear();
        temporaryQueue.forEach(audioTrack -> {
            try {
                this.queue.put(audioTrack);
            } catch (InterruptedException ignored) {
            }
        });
    }

    public int skip(int i) {
        int skipped = i > queue.size() ? queue.size() : i;
        queue.drainTo(new ArrayList<>(), i - 1);
        nextTrack();
        return skipped;
    }

    public void setLoop(boolean looped) {
        isLooped = looped;
    }

    public int skipAll() {
        int size = queue.size();
        queue.clear();
        nextTrack();
        return size;
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if (endReason.mayStartNext) {
            if (isLooped) {
                player.startTrack(track.makeClone(), false);
            } else {
                nextTrack();
            }
        }
    }

    public boolean isLooped() {
        return isLooped;
    }

    public BlockingQueue<AudioTrack> getQueue() {
        return queue;
    }
}