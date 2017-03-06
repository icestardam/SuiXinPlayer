package com.adam.suixinplayer.entity;

/**
 * Created by Administrator on 2017/3/2.
 */
public class SongUrl {
    private String down_type;
    private String original;
    private String free;
    private String replay_gain;
    private String song_file_id;
    private String file_size;
    private String file_extension;
    private String file_duration;
    private String can_see;
    private String can_load;
    private String preload;
    private String file_bitrate;
    private String file_link;

    public SongUrl() {
    }

    public SongUrl(String down_type, String original, String free, String replay_gain, String song_file_id, String file_size, String file_extension, String file_duration, String can_see, String can_load, String preload, String file_bitrate, String file_link) {
        this.down_type = down_type;
        this.original = original;
        this.free = free;
        this.replay_gain = replay_gain;
        this.song_file_id = song_file_id;
        this.file_size = file_size;
        this.file_extension = file_extension;
        this.file_duration = file_duration;
        this.can_see = can_see;
        this.can_load = can_load;
        this.preload = preload;
        this.file_bitrate = file_bitrate;
        this.file_link = file_link;
    }

    @Override
    public String toString() {
        return "SongUrl{" +
                "down_type='" + down_type + '\'' +
                ", original='" + original + '\'' +
                ", free='" + free + '\'' +
                ", replay_gain='" + replay_gain + '\'' +
                ", song_file_id='" + song_file_id + '\'' +
                ", file_size='" + file_size + '\'' +
                ", file_extension='" + file_extension + '\'' +
                ", file_duration='" + file_duration + '\'' +
                ", can_see='" + can_see + '\'' +
                ", can_load='" + can_load + '\'' +
                ", preload='" + preload + '\'' +
                ", file_bitrate='" + file_bitrate + '\'' +
                ", file_link='" + file_link + '\'' +
                '}';
    }

    public String getDown_type() {
        return down_type;
    }

    public void setDown_type(String down_type) {
        this.down_type = down_type;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getReplay_gain() {
        return replay_gain;
    }

    public void setReplay_gain(String replay_gain) {
        this.replay_gain = replay_gain;
    }

    public String getSong_file_id() {
        return song_file_id;
    }

    public void setSong_file_id(String song_file_id) {
        this.song_file_id = song_file_id;
    }

    public String getFile_size() {
        return file_size;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public String getFile_extension() {
        return file_extension;
    }

    public void setFile_extension(String file_extension) {
        this.file_extension = file_extension;
    }

    public String getFile_duration() {
        return file_duration;
    }

    public void setFile_duration(String file_duration) {
        this.file_duration = file_duration;
    }

    public String getCan_see() {
        return can_see;
    }

    public void setCan_see(String can_see) {
        this.can_see = can_see;
    }

    public String getCan_load() {
        return can_load;
    }

    public void setCan_load(String can_load) {
        this.can_load = can_load;
    }

    public String getPreload() {
        return preload;
    }

    public void setPreload(String preload) {
        this.preload = preload;
    }

    public String getFile_bitrate() {
        return file_bitrate;
    }

    public void setFile_bitrate(String file_bitrate) {
        this.file_bitrate = file_bitrate;
    }

    public String getFile_link() {
        return file_link;
    }

    public void setFile_link(String file_link) {
        this.file_link = file_link;
    }
}
