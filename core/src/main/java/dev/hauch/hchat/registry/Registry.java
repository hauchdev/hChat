package dev.hauch.hchat.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

// class Registry
public abstract class Registry<T> {

    private final Map<String, T> entries = new LinkedHashMap<>();
    private final String displayName;

    // make Registry
    protected Registry(String displayName) {
        this.displayName = Objects.requireNonNull(displayName, "displayName");
    }

    // add 
    public void register(String name, T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        if (entries.containsKey(name)) {
            throw new IllegalStateException(
                    displayName + " '" + name + "' is already registered.");
        }
        entries.put(name, value);
    }

    // override data
    public void override(String name, T value) {
        Objects.requireNonNull(name, "name");
        Objects.requireNonNull(value, "value");
        entries.put(name, value);
    }

    // get data
    public T get(String name) {
        return entries.get(name);
    }

    // contains data
    public boolean contains(String name) {
        return entries.containsKey(name);
    }

    // unregister data
    public void unregister(String name) {
        entries.remove(name);
    }

    // all data
    public Collection<T> all() {
        return Collections.unmodifiableCollection(entries.values());
    }

    // names data
    public Set<String> names() {
        return Collections.unmodifiableSet(entries.keySet());
    }

    // size data
    public int size() {
        return entries.size();
    }

    // clear data
    public void clear() {
        entries.clear();
    }

    @Override
    // to string
    public String toString() {
        return displayName + " registry (" + entries.size() + " entries)";
    }
}
