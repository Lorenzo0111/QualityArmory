package me.zombie_striker.qualityarmory.data;

public class AnimationKeyFrame {

    private String sound;
    private int tick;
    private AnimationGunAction action;

    public AnimationKeyFrame(String sound, AnimationGunAction action, int tick){
        this.sound = sound;
        this.tick = tick;
        this.action = action;
    }

    public AnimationGunAction getAction() {
        return action;
    }

    public int getTick() {
        return tick;
    }

    public String getSound() {
        return sound;
    }
}
