package com.xuchen.scheduler;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.scheduler.QueueScheduler;

public class NumberScheduler extends QueueScheduler {
    // Set number of page to visit.
    int visitNumber = Integer.MAX_VALUE;
    int visited = 0;

    public NumberScheduler(){

    }

    public NumberScheduler(int num){
        visitNumber = num*2-2;
    }

    @Override
    public void push(Request request, Task task){
        if (visited < visitNumber){
            super.push(request, task);
            visited += 1;
        }
    }
}
