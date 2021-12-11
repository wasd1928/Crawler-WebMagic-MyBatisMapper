package com.xuchen.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

import java.util.Date;

public class TimeScheduler extends QueueScheduler {
    long millisec = 5000;
    long start = new Date().getTime();
    int numberOfVisit = 0;

    public int getNumberOfVisit() {
        return numberOfVisit/2;
    }


    public TimeScheduler(){

    }
    public TimeScheduler(int sec) {
        this.millisec = sec* 1000L;
    }

    @Override
    public void push(Request request, Task task){
        super.push(request,task);
        numberOfVisit += 1;
    }

    @Override
    public Request poll(Task task) {
        long now = new Date().getTime();
        if (start+millisec > now) {
            return super.poll(task);
        } else {
            return null;
        }
    }
}
