package me.zombie_striker.qualityarmory.data;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PriorityDataMap<K,V> implements Map {

    private HashMap<K,V>[] priorityMap;
    private int size = 0;

    public PriorityDataMap(int priorityLevels){
        priorityMap = new HashMap[priorityLevels];
        for(int i = 0; i < priorityLevels;i++)
            priorityMap[i]=new HashMap<>();
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size==0;
    }

    @Override
    public boolean containsKey(Object key) {
        for(int i = 0; i < priorityMap.length;i++){
            if(priorityMap[i].containsKey(key))
                return true;
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        for(int i = 0; i < priorityMap.length;i++){
            if(priorityMap[i].containsValue(value))
                return true;
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        for(int i = 0; i < priorityMap.length;i++){
            if(priorityMap[i].containsKey(key))
                priorityMap[i].get(key);
        }
        return null;
    }

    @Nullable
    @Override
    @Deprecated
    public Object put(Object key, Object value) {
        return null;
    }
    public Object put(int priority, K key, V value){
        for(int p = 0; p <= priority;p++){
            if(this.priorityMap[p].containsKey(key)){
                return this.priorityMap[p].put(key,value);
            }
        }
        size++;
        return this.priorityMap[priority].put(key,value);
    }

    @Override
    public Object remove(Object key) {
        Object first = null;
        for(int priority = 0; priority<this.priorityMap.length;priority++){
            if(priorityMap[priority].containsKey(key)){
                size--;
                if(first==null)
                    first = priorityMap[priority].get(key);
                priorityMap[priority].remove(key);
            }
        }
        return first;
    }

    @Override
    public void putAll(@NotNull Map m) {

    }

    @Override
    public void clear() {
        for(int i = 0; i < priorityMap.length;i++){
            this.priorityMap[i].clear();
        }
    }

    @NotNull
    @Override
    public Set keySet() {
        Set set = new HashSet();
        for(int i = 0; i < priorityMap.length;i++){
            set.addAll(priorityMap[i].keySet());
        }
        return set;
    }

    @NotNull
    @Override
    public Collection values() {
        Set set = new HashSet();
        for(int i = 0; i < priorityMap.length;i++){
            set.addAll(priorityMap[i].values());
        }
        return set;
    }

    @NotNull
    @Override
    public Set<Entry> entrySet() {
        Set set = new HashSet();
        for(int i = 0; i < priorityMap.length;i++){
            set.addAll(priorityMap[i].entrySet());
        }
        return set;
    }
}
