package me.zombie_striker;

import me.zombie_striker.qualityarmory.data.PriorityDataMap;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Test {

    public static void main(String... args){
        HashMap<Integer, String> hashmap = new HashMap<>();
        long timesRan = 0;
        hashmap.put(0,"Zero");
        hashmap.put(1,"1");
        hashmap.put(2,"2");
        hashmap.put(3,"3");
        hashmap.put(4,"4");
        hashmap.put(5,"5");
        hashmap.put(6,"6");
        hashmap.put(7,"7");
        hashmap.put(8,"8");
        hashmap.put(9,"9");
        hashmap.put(10,"10");
        hashmap.put(11,"11");
        hashmap.put(12,"12");
        hashmap.put(13,"13");
        hashmap.put(14,"14");
        hashmap.put(15,"15");
        hashmap.put(16,"16");
        hashmap.put(17,"17");
        hashmap.put(18,"18");
        hashmap.put(19,"19");
        long starttime = System.nanoTime();
        while(System.nanoTime()-starttime<10000000){
            hashmap.get((int)Math.sqrt(Math.random()*19*19));
            timesRan++;
        }
        System.out.println("HashMap -> "+timesRan);


        List<Long> times = new LinkedList<>();
        for(int i = 0; i < 2000;i++) {
            timesRan=0;
            PriorityDataMap dataMap = new PriorityDataMap(4);

            for(int j = 0; j < 2000; j++)
            dataMap.put((int)Math.random()*4,j,"="+j);

            long starttime2 = System.nanoTime();
            while (System.nanoTime() - starttime2 < 10000000) {
                dataMap.get((int)Math.sqrt(Math.random() *dataMap.size()*dataMap.size()));
                timesRan++;
            }
            times.add(timesRan);
        }

        long best = 0;
        long worst = Integer.MAX_VALUE;
        long average2 = 0;
        for(Long e : times){
            if(e > best)
                best=e;
            if(e<worst)
                worst=e;
            average2+=e;
        }
        double average = average2/times.size();

        System.out.println("PriorityDataMap -> Best="+best+"  Worst="+worst+"  Ave="+average);

    }
}
