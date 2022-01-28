package tta.destinigo.talktoastro.model;

public class TimerValueUpdate {
    long timeLeft = 0L;
    long startTime = 0L;

    public void setTime(long time) {
        this.timeLeft = time;
    }

    public Long getTime() {
        return timeLeft;
    }

    public void setStartTime(long time) {
        this.startTime = startTime;
    }

    public Long getStartTime() {
        return startTime;
    }
}
